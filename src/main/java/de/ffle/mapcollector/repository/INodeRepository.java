package de.ffle.mapcollector.repository;

import java.util.Collection;
import java.util.List;

import de.ffle.mapcollector.model.Node;
import de.ffle.mapcollector.model.NodeAddress;
import de.ffle.mapcollector.model.NodeInfo;
import de.ffle.mapcollector.model.NodeLink;
import de.ffle.mapcollector.model.NodeLinkUpdate;
import de.ffle.mapcollector.model.NodeStats;

public interface INodeRepository {
	
	public int addNewNodes(List<NodeAddress> nodes);
	
	public List<Node> getNodes();
	
	public List<NodeLink> getNodeLinks();
	
	public void updateNode(NodeAddress node, NodeInfo info, NodeStats stats, Collection<NodeLinkUpdate> links);

}
