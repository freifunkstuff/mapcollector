package de.ffle.mapcollector.process;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.ffle.mapcollector.model.NodeAddress;
import de.ffle.mapcollector.repository.INodeRepository;
import de.ffle.mapcollector.source.INodeListSource;

@Service
public class NewNodeCollector {
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	
	@Autowired
	protected INodeListSource nodeListSource;

	@Autowired
	protected INodeRepository nodeRepository;
	
	protected void fetchNewNodes() throws IOException {
		
		logger.info("Fetching new node list");
		List<NodeAddress> nodes=nodeListSource.fetchNodes();
		int newNodeCount=nodeRepository.addNewNodes(nodes);
		logger.info("Added {} new nodes",newNodeCount);
	}
	

}
