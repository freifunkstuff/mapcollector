package de.ffle.mapcollector.process;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;

import com.fasterxml.jackson.databind.JsonNode;

import de.ffle.mapcollector.model.Node;

//@Service
@Profile("fetcher")
public class NIONodeFetcher extends AbstractNodeFetcher {
	
	protected Map<String,Future<?>> currentlyFetching=new ConcurrentHashMap<>();
	protected CloseableHttpAsyncClient httpClient;
	
	@Value("${nodefetcher.finishFetchOnShutdown:false}")
	protected boolean finishFetchOnShutdown;
	
	@PostConstruct
	protected void initHttpClient() throws Exception {
		
		HttpAsyncClientBuilder builder = HttpAsyncClients.custom();
		
		builder.setDefaultRequestConfig(RequestConfig.custom()
				  .setConnectTimeout(30000)
				  .setConnectionRequestTimeout(30000)
				  .setSocketTimeout(30000)
				  .build());
		
		PoolingNHttpClientConnectionManager connManager = new PoolingNHttpClientConnectionManager(new DefaultConnectingIOReactor());
		connManager.setMaxTotal(1200);
		connManager.setDefaultMaxPerRoute(1200);
		
		builder.setConnectionManager(connManager);

		if (StringUtils.isNotBlank(proxyUrl)) {
			
			
			builder.setProxy(HttpHost.create(proxyUrl));
			
			if (!StringUtils.isAnyBlank(proxyUsername,proxyPassword)) {
				String proxyAuth="Basic "+Base64.getEncoder().encodeToString((proxyUsername+":"+proxyPassword).getBytes(StandardCharsets.UTF_8));
				
				builder.setDefaultHeaders(Collections.singleton(new BasicHeader(HttpHeaders.PROXY_AUTHORIZATION, proxyAuth)));
			}
		}
		
		httpClient=builder.build();
		httpClient.start();
	}
	
	@PreDestroy
	protected void shutdownHttpClient() throws IOException {
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
	}
	
	protected void fetch(Node node) {
		if (currentlyFetching.containsKey(node.getId())) {
			logger.debug("Should fetch node {} but last fetch is not yet finished",node.getId());
			return;
		}
		
		while (currentlyFetching.size()>1000) {
			try {
				synchronized (currentlyFetching) {
					currentlyFetching.wait(1000);
				} 
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
		}
		
		logger.trace("Fetching node {} ({})",node.getId(), node.getPrimaryIpAddress());
		
		
		currentlyFetching.put(node.getId(),
			httpClient.execute(new HttpGet("http://"+node.getPrimaryIpAddress()+"/sysinfo-json.cgi"), new FutureCallback<HttpResponse>() {
				
				@Override
				public void failed(Exception ex) {
					currentlyFetching.remove(node.getId());
					synchronized (currentlyFetching) {
						currentlyFetching.notifyAll();
					}
					logger.debug("Fetching node {} ({}) failed: {}",node.getId(),node.getPrimaryIpAddress(),ex.toString());
					updateNodeError(node,ex);
				}
				
				@Override
				public void completed(HttpResponse result) {
					try {
						if (result.getStatusLine().getStatusCode()!=200) {
							logger.debug("Fetching node {} ({}) failed: {}",node.getId(),node.getPrimaryIpAddress(),result.getStatusLine());
							failed(null);
							return;
						}
						currentlyFetching.remove(node.getId());
						synchronized (currentlyFetching) {
							currentlyFetching.notifyAll();
						}
						JsonNode json;
						try {
							json=JSON_READER.readTree(result.getEntity().getContent());
						} catch (Exception ex) {
							failed(ex);
							return;
						}
						try {
							updateNode(node,json);
							logger.debug("Fetching node {} succeeded",node.getId());
						} catch (Exception ex) {
							logger.debug("Node {} fetched update failed",node.getId(),ex);
						}
					} finally {
						try {
							EntityUtils.consume(result.getEntity());
						} catch (Exception ex) {
						}
					}
				}
				
				@Override
				public void cancelled() {
					currentlyFetching.remove(node.getId());
					synchronized (currentlyFetching) {
						currentlyFetching.notifyAll();
					}
					updateNodeError(node,null);
				}
			}));
	}
}
