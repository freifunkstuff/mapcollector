package de.ffle.mapcollector.process;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class StartupRunner implements CommandLineRunner {

	@Autowired
	protected NewNodeCollector newNodeCollector;
	
	@Autowired
	protected NodeFetcher nodeFetcher;
	
	@Autowired
	protected ConfigurableApplicationContext ctx;
	
	@Override
	public void run(String... args) throws Exception {
//		newNodeCollector.fetchNewNodes();
//		nodeFetcher.fetchNodeData();
//		ctx.close();
//		Thread.sleep(1000);
	}
	
	
	

}
