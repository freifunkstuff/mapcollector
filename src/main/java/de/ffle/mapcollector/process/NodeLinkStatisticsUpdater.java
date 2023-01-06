package de.ffle.mapcollector.process;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import de.ffle.mapcollector.model.Node;
import de.ffle.mapcollector.repository.INodeRepository;
import de.ffle.mapcollector.repository.IStatisticsRepository;

@Service
@Profile("fetcher")
public class NodeLinkStatisticsUpdater {
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	protected INodeRepository nodeRepository;
	
	@Autowired(required = false)
	protected IStatisticsRepository statisticsRepository;

	@Scheduled(cron = "0 * * * * *") // every 5 minutes
	public void updateNodeLinkStatistics() {
		if (statisticsRepository==null) {
			return;
		}
		
		Map<String,Node> nodesById=new HashMap<>();
		for (Node n: nodeRepository.getNodes()) {
			nodesById.put(n.getId(), n);
		}
		
		statisticsRepository.sendLinkStatistics(nodesById, nodeRepository.getNodeLinks());
		logger.info("Nodes link statistics updated");
	}
	
}
