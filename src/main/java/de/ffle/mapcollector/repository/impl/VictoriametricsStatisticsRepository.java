package de.ffle.mapcollector.repository.impl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import de.ffle.mapcollector.CommunityFilter;
import de.ffle.mapcollector.model.NodeAddress;
import de.ffle.mapcollector.model.NodeInfo;
import de.ffle.mapcollector.model.NodeStats;
import de.ffle.mapcollector.repository.IStatisticsRepository;

/**
 * Pushes statistics to a VictoriaMetrics server, using the prometheus import endpoint
 * 
 * https://docs.victoriametrics.com/#how-to-import-data-in-prometheus-exposition-format
 * https://prometheus.io/docs/instrumenting/exposition_formats/
 * 
 * @author mwyraz
 */
@Service
public class VictoriametricsStatisticsRepository implements IStatisticsRepository {
	
	@Value("${statistics.victoriametrics.importUrl}")
	protected String prometheusWriteUrl;
	
	@Autowired
	protected CommunityFilter communityFilter;
	
	protected CloseableHttpAsyncClient httpClient;

	@PostConstruct
	protected void initHttpClient() {
		httpClient=HttpAsyncClients.createDefault();
		httpClient.start();
	}
	@PreDestroy
	protected void shutdownHttpClient() throws IOException {
		if (httpClient!=null) {
			httpClient.close();
		}
	}
	
	protected final FutureCallback<HttpResponse> metricsResponseCallback=new FutureCallback<HttpResponse>() {
		
		@Override
		public void failed(Exception ex) {
		}
		
		@Override
		public void completed(HttpResponse result) {
		}
		
		@Override
		public void cancelled() {
		}
	};
	
	@Override
	public void sendNodeStatistics(NodeAddress node, NodeInfo info, NodeStats stats) {
		if (!communityFilter.isShownCommunity(info)) {
			return;
		}
		String metrics=generateMetrics(node, info, stats);
		if (metrics==null) {
			return;
		}
		HttpPost post=new HttpPost(prometheusWriteUrl);
		post.setEntity(new ByteArrayEntity(metrics.getBytes(StandardCharsets.UTF_8),ContentType.TEXT_PLAIN));
		httpClient.execute(post, metricsResponseCallback);
	}
	
	
	protected static class MetricsBuilder {
		protected StringBuilder sb=new StringBuilder();
		protected StringBuilder lb=new StringBuilder();
		boolean metricHasLabel=false;
		boolean eof=false;
		
		public MetricsBuilder metric(String name) {
			lb.setLength(0);
			metricHasLabel=false;
			lb.append(name);
			return this;
		}

		protected void appendQuoted(StringBuilder b, String s) {
			for (char c: s.toCharArray()) {
				switch (c) {
					case '\\':
						b.append("\\\\");
						break;
					case '"':
						b.append("\\\"");
						break;
					case '\n':
						b.append("\\n");
						break;
					default:
						b.append(c);
						break;
				}
			}
		}

		public MetricsBuilder labels(Map<String,String> labels) {
			for (Entry<String, String> label: labels.entrySet()) {
				label(label.getKey(), label.getValue());
			}
			return this;
		}
		
		public MetricsBuilder label(String name, String value) {
			if (name!=null && value!=null) {
				if (!metricHasLabel) {
					lb.append("{");
					metricHasLabel=true;
				} else {
					lb.append(",");
				}
				lb.append(name);
				lb.append("=\"");
				appendQuoted(lb,value);
				lb.append("\"");
			}
			return this;
		}
		
		public MetricsBuilder value(Number value) {
			return value(value,null);
		}
		
		public MetricsBuilder value(Number value, ZonedDateTime optionalTimestamp) {
			if (value!=null) {
				if (metricHasLabel) {
					lb.append("}");
				}
				lb.append(" ");
				lb.append(value);
				if (optionalTimestamp!=null) {
					lb.append(" ");
					lb.append(optionalTimestamp.toInstant().toEpochMilli());
				}
				sb.append(lb).append("\n");
			}
			return this;
		}
		
		@Override
		public String toString() {
			if (!eof) {
				sb.append("# EOF\n");
				eof=true;
			}
			return sb.toString();
		}
	}
	
	protected static Integer add(Integer... values) {
		Integer result=null;
		for (Integer i: values) {
			if (i==null) {
				continue;
			}
			if (result==null) {
				result=i;
			} else {
				result+=i;
			}
		}
		return result;
	}
	
	public static String generateMetrics(NodeAddress node, NodeInfo info, NodeStats stats) {
		
		if (node==null || info==null || stats==null) {
			return null;
		}
		
		Map<String, String> nodeLabels=new LinkedHashMap<>();
		nodeLabels.put("nodeid", node.getId());
		nodeLabels.put("hostname", info.getName());
		nodeLabels.put("model", info.getModel());
		nodeLabels.put("domain", info.getCommunity());
		nodeLabels.put("owner", info.getContactEmail());
		nodeLabels.put("autoupdater", info.getAutoUpdate()==null?null: info.getAutoUpdate().booleanValue()?"enabled":"disabled");
		nodeLabels.put("firmware_base", info.getFirmwareBase());
		nodeLabels.put("firmware_release", info.getFirmwareRelease());
		
		
		MetricsBuilder mb=new MetricsBuilder();

		mb.metric("node_info")
			.labels(nodeLabels)
			.value(1);
		
		mb.metric("node_time.up")
			.labels(nodeLabels)
			.value(stats.getUptimeSeconds()==null?null:stats.getUptimeSeconds().longValue());
		
		mb.metric("node_traffic.rx.bytes")
			.labels(nodeLabels)
			.value(stats.getTrafficWifiRx());
		
		mb.metric("node_traffic.tx.bytes")
			.labels(nodeLabels)
			.value(stats.getTrafficWifiTx());

		mb.metric("node_clients.wifi24")
			.labels(nodeLabels)
			.value(stats.getClients2g());

		mb.metric("node_clients.wifi5")
			.labels(nodeLabels)
			.value(stats.getClients5g());

		mb.metric("node_clients.total")
			.labels(nodeLabels)
			.value(add(stats.getClients2g(),stats.getClients5g()));

		mb.metric("node_load")
			.labels(nodeLabels)
			.value(stats.getLoadAvg5());

		mb.metric("node_memory.total")
			.labels(nodeLabels)
			.value(stats.getMemTotal());

		mb.metric("node_memory.available")
			.labels(nodeLabels)
			.value(stats.getMemFree());
		
		return mb.toString();
	}

}
