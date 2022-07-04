package de.ffle.mapcollector.source.impl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

import de.ffle.mapcollector.model.NodeAddress;
import de.ffle.mapcollector.source.INodeListSource;

/**
 * Fetches node list from a gateway or node connected to the freifunk network
 */
public class GatewayNodeListSource implements INodeListSource {
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	protected CloseableHttpClient httpClient=HttpClients.createDefault();
	
	@Value("http://${nodelist.gateway.ip}/nodes.cgi")
	protected String gatewayNodesUrl;

	@Value("http://${nodelist.gateway.ip}/status.cgi")
	protected String gatewayStatusUrl;
	
	protected NodeAddress ownAddress;
	
	protected byte[] downloadNodes() throws IOException {
		try (CloseableHttpResponse resp=httpClient.execute(new HttpGet(gatewayNodesUrl))) {
			return IOUtils.toByteArray(resp.getEntity().getContent());
		}
	}
	protected byte[] downloadStatus() throws IOException {
		try (CloseableHttpResponse resp=httpClient.execute(new HttpGet(gatewayStatusUrl))) {
			return IOUtils.toByteArray(resp.getEntity().getContent());
		}
	}

	
	@Override
	@Scheduled(cron = "0 */5 * * * *")
	public List<NodeAddress> fetchNodes() throws IOException {
		byte[] html=downloadNodes();
		
		Document doc = Jsoup.parse(new String(html,StandardCharsets.UTF_8));
		
		Elements el=doc.select("fieldset:has(legend:containsOwn(Freifunk Knoten)) tr:has(td)");
		
		if (el.size()<3) {
			throw new IOException("Nodes not found in HTML");
		}
		List<NodeAddress> result=new ArrayList<>();
		for (Element tr: el) {
			if (tr.childNodeSize()!=5) {
				continue;
			}
			String nodeId=tr.child(0).text();
			String primaryIpAddress=tr.child(1).text();
			result.add(new NodeAddress(nodeId, primaryIpAddress));
		}
		
		addOwnAddress(result);
		
		return result;
	}
	
	protected NodeAddress fetchOwnAddress() throws IOException {
		byte[] html=downloadStatus();
		
		Document doc = Jsoup.parse(new String(html,StandardCharsets.UTF_8));
		
		Element el=doc.selectFirst("fieldset:has(legend:containsOwn(Allgemeines)) tr:has(th:containsOwn(Knoten-IP-Adresse:)) td");
		if (el==null) {
			return null;
		}
		String ipAndNodeId=el.text();
		if (ipAndNodeId==null) {
			return null;
		}
		Matcher m=Pattern.compile("^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})\\s+\\((\\d+)\\)$").matcher(ipAndNodeId);
		if (!m.matches()) {
			return null;
		}
		return new NodeAddress(m.group(2), m.group(1));
	}
	
	protected void addOwnAddress(List<NodeAddress> result) throws IOException {
		if (ownAddress==null) {
			ownAddress=fetchOwnAddress();
		}
		if (ownAddress==null) {
			logger.warn("Unable to retrieve gateway IP and adress from {}",gatewayStatusUrl);
		} else {
			result.add(ownAddress);
		}
		
	}

}
