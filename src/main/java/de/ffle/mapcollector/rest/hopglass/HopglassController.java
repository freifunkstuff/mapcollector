package de.ffle.mapcollector.rest.hopglass;

import java.time.Duration;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

import de.ffle.mapcollector.CommunityFilter;
import de.ffle.mapcollector.model.Node;
import de.ffle.mapcollector.model.NodeLink;
import de.ffle.mapcollector.repository.INodeRepository;

//@RestController
//@RequestMapping("/hopglass")
// Hopglass controller is not finished and not very well tested. It's here for reference but will be removed in a later commit
public class HopglassController {
	
	@Autowired
	protected INodeRepository nodeRepository;
	
	@Autowired
	protected CommunityFilter communityFilter;
	
	protected HopglassNodes nodes;
	protected HopglassGraph graph;
	
	protected final int OFFLINE_MINUTES=10;
	
	protected void fetch() {
		
		HopglassNodes nodes=new HopglassNodes();
		HopglassGraph graph=new HopglassGraph();
		
		for (Node node: nodeRepository.getNodes()) {
			if (node.getInfo()==null) {
				continue;
			}
			if (!communityFilter.isShownCommunity(node)) {
				continue;
			}
			HopglassNode hn=new HopglassNode();
			hn.firstseen=node.getFirstSeen();
			hn.lastseen=node.getLastSeen();
			
			hn.nodeinfo.nodeId=node.getId();
			hn.nodeinfo.nodeName=buildNodeName(node);
			hn.nodeinfo.ipAddress=node.getPrimaryIpAddress();
			
			hn.nodeinfo.fill(node.getInfo());
			
			if (node.getLastSeen()!=null && Duration.between(node.getLastSeen(), nodes.timestamp).toMinutes()<OFFLINE_MINUTES) {
				hn.flags.online=true;
				if (node.getStats()!=null) {
					hn.statistics.fill(node.getStats());
				}
			}
			
			nodes.nodes.add(hn);
		}
		
		for (NodeLink link: nodeRepository.getNodeLinks()) {
			graph.add(link, OFFLINE_MINUTES);
		}
		
		this.nodes=nodes;
		this.graph=graph;
		
	}
	
	protected String buildNodeName(Node node) {
		if (StringUtils.isBlank(node.getInfo().getName())) {
			return node.getId();
		}
		return node.getInfo().getName()+" ("+node.getId()+")";
	}
	
	@RequestMapping("/nodes.json")
	@CrossOrigin(origins = "*")
	public HopglassNodes getNodes() {
		fetch();
		return nodes;
	}
	
	@RequestMapping("/graph.json")
	@CrossOrigin(origins = "*")
	public HopglassGraph getGraph() {
		fetch();
		return graph;
	}

}
