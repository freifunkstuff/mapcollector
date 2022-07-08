package de.ffle.mapcollector;

import org.springframework.stereotype.Service;

import de.ffle.mapcollector.model.NodeInfo;
import de.ffle.mapcollector.model.NodeType;

@Service
public class CommunityFilter {
	
	public boolean isShownCommunity(NodeInfo info) {
		if (info==null) {
			return false;
		}
		if ("Leipzig".equals(info.getCommunity())) {
			return true;
		}
		if (info.getNodeType()==NodeType.server) {
			return true; // result contains all gateways so that the gateway links can be shown
		}
		return false;
	}

}
