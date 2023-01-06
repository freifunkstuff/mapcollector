package de.ffle.mapcollector.repository;

import java.util.List;
import java.util.Map;

import de.ffle.mapcollector.model.Node;
import de.ffle.mapcollector.model.NodeAddress;
import de.ffle.mapcollector.model.NodeInfo;
import de.ffle.mapcollector.model.NodeLink;
import de.ffle.mapcollector.model.NodeStats;

public interface IStatisticsRepository {
	public void sendNodeStatistics(NodeAddress node, NodeInfo info, NodeStats stats);
	
	public void sendLinkStatistics(Map<String,Node> nodesById, List<NodeLink> nodeLinks);
}
