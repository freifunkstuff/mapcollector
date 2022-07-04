package de.ffle.mapcollector;

import org.springframework.stereotype.Service;

import de.ffle.mapcollector.model.Node;
import de.ffle.mapcollector.model.NodeType;

@Service
public class CommunityFilter {
	
	public boolean isShownCommunity(Node node) {
		if (node.getInfo()==null) {
			return false;
		}
		if ("Leipzig".equals(node.getInfo().getCommunity())) {
			return true;
		}
		if (node.getInfo().getNodeType()==NodeType.server) {
			return true; // result contains all gateways so that the gateway links can be shown
		}
		return false;
	}

}
