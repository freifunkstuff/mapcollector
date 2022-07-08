package de.ffle.mapcollector.repository;

import de.ffle.mapcollector.model.NodeAddress;
import de.ffle.mapcollector.model.NodeInfo;
import de.ffle.mapcollector.model.NodeStats;

public interface IStatisticsRepository {
	public void sendNodeStatistics(NodeAddress node, NodeInfo info, NodeStats stats);
}
