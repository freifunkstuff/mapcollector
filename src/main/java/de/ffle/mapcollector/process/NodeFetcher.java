package de.ffle.mapcollector.process;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Function;

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
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.ffle.mapcollector.CommunityFilter;
import de.ffle.mapcollector.model.LinkType;
import de.ffle.mapcollector.model.Node;
import de.ffle.mapcollector.model.NodeAddress;
import de.ffle.mapcollector.model.NodeInfo;
import de.ffle.mapcollector.model.NodeLinkUpdate;
import de.ffle.mapcollector.model.NodeStats;
import de.ffle.mapcollector.model.NodeType;
import de.ffle.mapcollector.repository.INodeRepository;
import de.ffle.mapcollector.util.DataHelper;

@Service
public class NodeFetcher {
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	protected INodeRepository nodeRepository;
	
	@Autowired
	protected CommunityFilter communityFilter;
	
	@Scheduled(cron = "0 */3 * * * *")
	public void fetchNodeData() throws Exception {
		for (Node node: nodeRepository.getNodes()) {
			if (shouldFetch(node)) {
				fetch(node);
			}
		}
	}
	
	protected boolean shouldFetch(Node node) {
		if (node.getLastFetched()==null) {
			return true;
		}
		long lastFetchedMinutesAgo = Duration.between(node.getLastFetched(), ZonedDateTime.now()).toMinutes();
		long lastSeenMinutesAgo = node.getLastSeen()==null ? 999 : Duration.between(node.getLastSeen(), ZonedDateTime.now()).toMinutes();
		
		if ((node.getInfo()==null || node.getInfo().getCommunity()==null)) {
			return lastFetchedMinutesAgo>=1;
		}
		
		if (communityFilter.isShownCommunity(node)) {
			return lastFetchedMinutesAgo>=5 || lastSeenMinutesAgo>1;
		}
		
		return lastFetchedMinutesAgo>=15 || lastSeenMinutesAgo>5; // no other rules
	}
	
	protected Map<String,Future<?>> currentlyFetching=new ConcurrentHashMap<>();
	protected CloseableHttpAsyncClient httpClient;

	@Value("${nodefetcher.proxyUrl:}")
	protected String proxyUrl;
	
	@Value("${nodefetcher.proxyUsername:}")
	protected String proxyUsername;
	
	@Value("${nodefetcher.proxyPassword:}")
	protected String proxyPassword;
	
	@PostConstruct
	protected void initHttpClient() {
		
		HttpAsyncClientBuilder builder = HttpAsyncClients.custom();
		
		builder.setDefaultRequestConfig(RequestConfig.custom()
				  .setConnectTimeout(30000)
				  .setConnectionRequestTimeout(30000)
				  .setSocketTimeout(30000).build());

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
		if (httpClient!=null) {
			httpClient.close();
		}
	}
	
	
	protected void fetch(Node node) {
		if (currentlyFetching.containsKey(node.getId())) {
			logger.debug("Should fetch node {} but last fetch is not yet finished",node.getId());
			return;
		}
		logger.trace("Fetching node {} ({})",node.getId(), node.getPrimaryIpAddress());
		
		
		currentlyFetching.put(node.getId(),
			httpClient.execute(new HttpGet("http://"+node.getPrimaryIpAddress()+"/sysinfo-json.cgi"), new FutureCallback<HttpResponse>() {
				
				@Override
				public void failed(Exception ex) {
					currentlyFetching.remove(node.getId());
					updateNodeError(node,ex);
				}
				
				@Override
				public void completed(HttpResponse result) {
					if (result.getStatusLine().getStatusCode()!=200) {
						logger.debug("Fetching node {} ({}) failed: {}",node.getId(),node.getPrimaryIpAddress(),result.getStatusLine());
						failed(null);
						return;
					}
					currentlyFetching.remove(node.getId());
					JsonNode json;
					try {
						json=new ObjectMapper().reader().readTree(result.getEntity().getContent());
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
				}
				
				@Override
				public void cancelled() {
					currentlyFetching.remove(node.getId());
					updateNodeError(node,null);
				}
			}));
	}
	
	protected NodeInfo parseNodeInfo(JsonNode sysinfo) {
		JsonNode data=sysinfo.get("data");
		if (data==null) {
			return null;
		}
		NodeInfo node=new NodeInfo();
		JsonNode firmware=data.get("firmware");
		if (firmware!=null) {
			node.setFirmwareBase(nodeString(firmware,"DISTRIB_DESCRIPTION"));
			node.setFirmwareRelease(nodeString(firmware,"version"));
		}
		JsonNode system=data.get("system");
		if (system!=null) {
			node.setModel(nodeString(system,"model2"));
			node.setNodeType(NodeType.valueOf(nodeString(system,"node_type")));
			node.setAutoUpdate(nodeValue(system,JsonNode::asBoolean,"autoupdate"));
		}
		JsonNode common=data.get("common");
		if (common!=null) {
			node.setCommunity(nodeString(common,"community"));
		}
		JsonNode gps=data.get("gps");
		if (gps!=null) {
			node.setLocationLatitude(nodeValue(gps, JsonNode::asDouble, "latitude"));
			node.setLocationLongitude(nodeValue(gps, JsonNode::asDouble, "longitude"));
			node.setLocationAltitude(nodeValue(gps, JsonNode::asInt, "altitude"));
		}
		JsonNode contact=data.get("contact");
		if (contact!=null) {
			node.setName(urlDecode(nodeString(contact,"name")));
			node.setLocation(urlDecode(nodeString(contact,"location")));
			node.setContactEmail(urlDecode(nodeString(contact,"email")));
			node.setNote(urlDecode(nodeString(contact,"note")));
		}
		return node;
	}

	protected NodeStats parseNodeStats(JsonNode sysinfo) {
		JsonNode data=sysinfo.get("data");
		if (data==null) {
			return null;
		}
		NodeStats stats=new NodeStats();
		
		JsonNode system=data.get("system");
		if (system!=null) {
			String uptime=nodeString(system, "uptime");
			if (uptime!=null) {
				uptime=uptime.split("\\s+",2)[0];
				stats.setUptimeSeconds(Double.parseDouble(uptime));
				stats.setUptime(ZonedDateTime.now().minus((long)(stats.getUptimeSeconds()*1000),ChronoUnit.MILLIS));
			}
		}
		JsonNode statistic=data.get("statistic");
		if (statistic!=null) {
			stats.setClients2g(nodeValue(statistic, (n)->n.intValue(), "client2g","1min"));
			stats.setClients5g(nodeValue(statistic, (n)->n.intValue(), "client5g","1min"));
			
			stats.setTrafficWifiRx(nodeValue(statistic, (n)->n.longValue(), "interfaces","wifi2_rx"));
			stats.setTrafficWifiTx(nodeValue(statistic, (n)->n.longValue(), "interfaces","wifi2_tx"));
			
			stats.setMemTotal(DataHelper.parseNBytes(nodeString(statistic, "meminfo_MemTotal")));
			stats.setMemFree(DataHelper.parseNBytes(nodeString(statistic, "meminfo_MemFree")));
			
			String load=nodeString(statistic, "cpu_load");
			if (load!=null) {
				stats.setLoadAvg5(Double.parseDouble(load.split("\\s+")[1]));
			}
		}
		
		JsonNode bmxd=data.get("bmxd");
		if (bmxd!=null) {
			stats.setSelectedGateway(nodeString(bmxd, "gateways","selected"));
			stats.setPreferredGateway(nodeString(bmxd, "gateways","preferred"));
		}
		
		JsonNode airtime=data.get("airtime");
		if (airtime!=null) {
			String airtime2g=nodeString(airtime, "radio2g");
			if (StringUtils.isNotBlank(airtime2g)) {
				String[] parts=airtime2g.split(",",4);
				stats.setAirtime2gActive(Long.parseLong(parts[0]));
				stats.setAirtime2gBusy(Long.parseLong(parts[1]));
				stats.setAirtime2gRx(Long.parseLong(parts[2]));
				stats.setAirtime2gTx(Long.parseLong(parts[3]));
			}
			String airtime5g=nodeString(airtime, "radio5g");
			if (StringUtils.isNotBlank(airtime5g)) {
				String[] parts=airtime5g.split(",",4);
				stats.setAirtime5gActive(Long.parseLong(parts[0]));
				stats.setAirtime5gBusy(Long.parseLong(parts[1]));
				stats.setAirtime5gRx(Long.parseLong(parts[2]));
				stats.setAirtime5gTx(Long.parseLong(parts[3]));
			}
		}
		
		JsonNode nswitch=data.get("network_switch");
		if (nswitch!=null) {
			nswitch=oneOf(nswitch,"switch","switch0");
		}
		if (nswitch!=null) {
			int portNum=1;
			for (JsonNode port: nswitch) {
				String name=nodeString(port, "port"); // "1" or "3 (wan)"
				String carrier=nodeString(port, "carrier"); // 0/1 or up/down
				String speed=nodeString(port, "speed"); // 1000 or 1000baseT
				
				boolean up;
				switch(carrier) {
					case "0":
					case "down":
						up=false;
						break;
					case "1":
					case "up":
						up=true;
						break;
					default:
						throw new IllegalArgumentException("Unexpected carrier: "+carrier);
				}
				
				int status;
				
				if (up) {
					switch(speed.toLowerCase()) {
						case "10":
						case "10baset":
							status=10;
							break;
						case "100":
						case "100baset":
							status=100;
							break;
						case "1000":
						case "1000baset":
							status=1000;
							break;
						default:
							throw new IllegalArgumentException("Unexpected speed: "+speed);
					}
				} else {
					status=0;
				}
				
				switch (portNum) {
					case 1:
						stats.setPort1Name(name);
						stats.setPort1Status(status);
						break;
					case 2:
						stats.setPort2Name(name);
						stats.setPort2Status(status);
						break;
					case 3:
						stats.setPort3Name(name);
						stats.setPort3Status(status);
						break;
					case 4:
						stats.setPort4Name(name);
						stats.setPort4Status(status);
						break;
					case 5:
						stats.setPort5Name(name);
						stats.setPort5Status(status);
						break;
				}
				
				if (++portNum>5) {
					break;
				}
			}
		}
		
		return stats;
	}
	
	protected List<NodeLinkUpdate> parseNodeLinks(JsonNode sysinfo) {
		JsonNode links=traverse(sysinfo, "data", "bmxd", "links");
		if (links==null) {
			return null;
		}
		List<NodeLinkUpdate> result=new ArrayList<>();
		for (JsonNode link: links) {
			String type=nodeString(link, "type");
			if (type==null||type.isEmpty()) {
				String intf=nodeString(link, "interface");
				if (intf!=null && intf.startsWith("tbb_wg")) {
					type="backbone";
				} else {
					continue;
				}
			}
			NodeLinkUpdate nlu=new NodeLinkUpdate();
			nlu.setType(LinkType.valueOf(type));
			nlu.setOtherNodeId(nodeString(link, "node"));
			nlu.setRq(nodeValue(link, (n)->n.asInt(),"rq"));
			nlu.setTq(nodeValue(link, (n)->n.asInt(),"tq"));
			result.add(nlu);
		}
		return result;
	}
	

	protected JsonNode oneOf(JsonNode node, String... children) {
		if (node!=null) {
			for (String child: children) {
				if (node.has(child)) {
					return node.get(child);
				}
			}
		}
		return null;
		
	}
	
	protected JsonNode traverse(JsonNode node, String... path) {
		for (String p: path) {
			if (node==null) {
				return null;
			}
			node=node.get(p);
		}
		return node;
	}
	protected <T> T nodeValue(JsonNode node, Function<JsonNode,T> extractor, String...path) {
		node=traverse(node,path);
		if (node==null || node.isNull()) {
			return null;
		}
		return extractor.apply(node);
	}
	protected String nodeString(JsonNode node, String...path) {
		return nodeValue(node, JsonNode::textValue, path);
	}
	protected String urlDecode(String s) {
		if (s==null) {
			return null;
		}
		return URLDecoder.decode(s, StandardCharsets.UTF_8);
	}
	
	protected void updateNode(NodeAddress node, JsonNode sysinfo) {
		NodeInfo info=parseNodeInfo(sysinfo);
		NodeStats stats=null;
		if (info!=null) {
			stats=parseNodeStats(sysinfo);
		}
		Collection<NodeLinkUpdate> links=parseNodeLinks(sysinfo);
		nodeRepository.updateNode(node, info, stats, links);
	}
	protected void updateNodeError(Node node, Throwable cause) {
		nodeRepository.updateNode(node, null, null, null);
		if (cause!=null) {
			cause.printStackTrace();
		}
	}

}
