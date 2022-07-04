package de.ffle.mapcollector.rest.hopglass;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.ffle.mapcollector.model.NodeLink;

@JsonInclude(value = Include.NON_NULL)
public class HopglassGraph {
	public final int version = 1;
	
	public final Batadv batadv = new Batadv(); 

	@JsonInclude(value = Include.NON_NULL)
	public class Batadv {
		public final boolean directed=true;
		public final boolean multigraph=false;
		public final Object[] graph=new Object[0];
		
		public Collection<NodeIndex> getNodes() {
			return nodeMap.values();
		}
		
		public List<LinkInfo> getLinks() {
			return links;
		}
	}
	@JsonInclude(value = Include.NON_NULL)
	public static class NodeIndex {
		@JsonProperty("node_id")
		public String nodeId;
		
		public String getId() {
			return nodeId;
		}
		
		public int seq;
	}
	@JsonInclude(value = Include.NON_NULL)
	public static class LinkInfo {
		public int source;
		public int target;
		public double tq;
		public String type;
	}

	private Map<String, NodeIndex> nodeMap=new LinkedHashMap<>();
	private List<LinkInfo> links=new ArrayList<>();
	
	public void add(NodeLink link, int validityInMinutes) {
		if (!link.isValid(validityInMinutes)) {
			return;
		}
			
		if (!nodeMap.containsKey(link.getLeftNodeId())) {
			NodeIndex idx=new NodeIndex();
			idx.nodeId=link.getLeftNodeId();
			idx.seq=nodeMap.size();
			nodeMap.put(link.getLeftNodeId(), idx);
		}
		if (!nodeMap.containsKey(link.getRightNodeId())) {
			NodeIndex idx=new NodeIndex();
			idx.nodeId=link.getRightNodeId();
			idx.seq=nodeMap.size();
			nodeMap.put(link.getRightNodeId(), idx);
		}
		
		{
			LinkInfo li=new LinkInfo();
			li.source=nodeMap.get(link.getLeftNodeId()).seq;
			li.target=nodeMap.get(link.getRightNodeId()).seq;
			li.tq=1d/(link.getTq(validityInMinutes)/100d);
			switch(link.getType()) {
				case lan:
				default:
					li.type="other";
					break;
				case wifi_mesh:
					li.type="wireless";
			}
			links.add(li);
		}

		{
			LinkInfo li=new LinkInfo();
			li.source=nodeMap.get(link.getRightNodeId()).seq;
			li.target=nodeMap.get(link.getLeftNodeId()).seq;
			li.tq=1d/(link.getRq(validityInMinutes)/100d);
			switch(link.getType()) {
			case lan:
			default:
				li.type="other";
				break;
			case wifi_mesh:
				li.type="wireless";
		}
			links.add(li);
		}
		
		
	}
	
}
