package de.ffle.mapcollector.process;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.NoConnectionReuseStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import de.ffle.mapcollector.model.Node;

@Service
@Profile("fetcher")
public class ThreadedNodeFetcher extends AbstractNodeFetcher {
	
	protected Map<String,Future<?>> currentlyFetching=new ConcurrentHashMap<>();
	protected CloseableHttpClient httpClient;
	
	@Value("${nodefetcher.finishFetchOnShutdown:false}")
	protected boolean finishFetchOnShutdown;

	@Value("${nodefetcher.threadCount:300}")
	protected int threadCount=300;
	
	protected ExecutorService executor;
	
	@PostConstruct
	protected void initHttpClientAndThreadPool() throws Exception {
		
		HttpClientBuilder builder = HttpClients.custom();
		
		builder.setDefaultRequestConfig(RequestConfig.custom()
				  .setConnectTimeout(30000)
				  .setConnectionRequestTimeout(30000)
				  .setSocketTimeout(30000)
				  .build());
		
		builder.setConnectionReuseStrategy(NoConnectionReuseStrategy.INSTANCE);
		
		PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
		connManager.setDefaultMaxPerRoute(5000);
		connManager.setMaxTotal(5000);
		builder.setConnectionManager(connManager);

		
		
		if (StringUtils.isNotBlank(proxyUrl)) {
			
			builder.setProxy(HttpHost.create(proxyUrl));
			
			if (!StringUtils.isAnyBlank(proxyUsername,proxyPassword)) {
				String proxyAuth="Basic "+Base64.getEncoder().encodeToString((proxyUsername+":"+proxyPassword).getBytes(StandardCharsets.UTF_8));
				
				builder.setDefaultHeaders(Collections.singleton(new BasicHeader(HttpHeaders.PROXY_AUTHORIZATION, proxyAuth)));
			}
		}
		
		httpClient=builder.build();
		
		executor=Executors.newFixedThreadPool(threadCount);
	}
	
	@PreDestroy
	protected void shutdownHttpClientAndThreadPool() throws IOException {
		if (finishFetchOnShutdown) {
			for (Future<?> f: currentlyFetching.values()) {
				try {
					f.get();
				} catch (Throwable th) {
					while (th instanceof ExecutionException) {
						th=th.getCause();
					}
					if (th instanceof SocketTimeoutException) {
						return;
					}
					th.printStackTrace();
				}
				
			}
		}
		if (httpClient!=null) {
			httpClient.close();
		}
		if (executor!=null)  {
			try {
				executor.awaitTermination(30, TimeUnit.SECONDS);
			} catch (InterruptedException ex) {
				// ignored
			}
			executor.shutdownNow();
		}
		
	}
	
	protected void fetch(Node node) {
		if (currentlyFetching.containsKey(node.getId())) {
			logger.debug("Should fetch node {} but last fetch is not yet finished",node.getId());
			return;
		}
		
		logger.trace("Fetching node {} ({})",node.getId(), node.getPrimaryIpAddress());
		
		currentlyFetching.put(node.getId(),executor.submit(()->{
			
			try (CloseableHttpResponse resp=httpClient.execute(new HttpGet("http://"+node.getPrimaryIpAddress()+"/sysinfo-json.cgi"))) {
				try {
					if (resp.getStatusLine().getStatusCode()!=200) {
						logger.debug("Fetching node {} ({}) failed: {}",node.getId(),node.getPrimaryIpAddress(),resp.getStatusLine());
						updateNodeError(node,null);
						return;
					}
					currentlyFetching.remove(node.getId());
					synchronized (currentlyFetching) {
						currentlyFetching.notifyAll();
					}
					JsonNode json=parseJson(resp.getEntity().getContent());
					try {
						updateNode(node,json);
						logger.debug("Fetching node {} succeeded",node.getId());
					} catch (Exception ex) {
						logger.debug("Node {} fetched update failed",node.getId(),ex);
					}
				} finally {
					EntityUtils.consumeQuietly(resp.getEntity());
				}
			} catch (Exception ex) {
				if (logger.isDebugEnabled()) {
					logger.debug("Fetching node {} ({}) failed: {}",node.getId(),node.getPrimaryIpAddress(),ex);
				} else {
					logger.info("Fetching node {} ({}) failed: {}",node.getId(),node.getPrimaryIpAddress(),ex.toString());
				}
				updateNodeError(node,ex);
			} finally {
				currentlyFetching.remove(node.getId());
				synchronized (currentlyFetching) {
					currentlyFetching.notifyAll();
				}
			}
		}));
	}
}
