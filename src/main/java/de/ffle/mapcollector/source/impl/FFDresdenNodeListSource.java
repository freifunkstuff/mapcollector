package de.ffle.mapcollector.source.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.ffle.mapcollector.model.INodeAddress;
import de.ffle.mapcollector.model.impl.NodeAddress;
import de.ffle.mapcollector.source.INodeListSource;

/**
 * Fetches node list from https://meshviewer.freifunk-dresden.de/ and filters by community
 * @author mwyraz
 *
 */
public class FFDresdenNodeListSource implements INodeListSource {
	
	protected CloseableHttpClient httpClient=HttpClients.createDefault();
	
	protected final String JSON_URL="https://meshviewer.freifunk-dresden.de/data/nodes.json";
	
	protected byte[] downloadJsonList() throws IOException {
		try (CloseableHttpResponse resp=httpClient.execute(new HttpGet(JSON_URL))) {
			return IOUtils.toByteArray(resp.getEntity().getContent());
		}
	}
	
	@Override
	public List<INodeAddress> fetchNodes() throws IOException {
		byte[] json=downloadJsonList();
		JsonNode data=new ObjectMapper().reader().readTree(json);
		JsonNode nodes=data.get("nodes");
		if (nodes==null) {
			throw new IOException("No nodes found in JSON");
		}
		List<INodeAddress> result=new ArrayList<>();
		for (JsonNode node: nodes) {
			JsonNode nodeInfo=node.get("nodeinfo");
			String siteInfo=nodeInfo.get("system").get("site_code").textValue();
			if (!"Leipzig".equals(siteInfo)) {
				continue;
			}
			String nodeId=nodeInfo.get("node_id").textValue();
			String primaryIpAddress=nodeInfo.get("network").get("addresses").get(0).textValue();
			result.add(new NodeAddress(nodeId, primaryIpAddress));
		}
		return result;
	}

}
