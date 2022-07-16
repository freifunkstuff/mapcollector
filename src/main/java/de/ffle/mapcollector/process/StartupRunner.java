package de.ffle.mapcollector.process;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.lang3.mutable.MutableInt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

import de.ffle.mapcollector.model.Node;
import de.ffle.mapcollector.repository.INodeRepository;

@Service
public class StartupRunner implements CommandLineRunner {

	@Autowired
	protected NewNodeCollector newNodeCollector;
	
	@Autowired
	protected NodeFetcher nodeFetcher;
	
	@Autowired
	protected ConfigurableApplicationContext ctx;

	@Autowired
	protected INodeRepository nodeRepository;
	
	@Override
	public void run(String... args) throws Exception {
		newNodeCollector.fetchNewNodes();
		nodeFetcher.fetchNodeData();
//		Map<String, Map<String,MutableInt>> byType=new TreeMap<>();
//		for (Node node: nodeRepository.getNodes()) {
//			if (node.getInfo()==null) continue;
//			if (node.getInfo().getModel()==null) continue;
//			if (node.getInfo().getCommunity()==null) continue;
//			byType.computeIfAbsent(node.getInfo().getModel(), (k)->new TreeMap<>())
//				.computeIfAbsent(node.getInfo().getCommunity(), (k)->new MutableInt())
//				.add(1);
//		}
//		
//		for (Entry<String, Map<String,MutableInt>> e: byType.entrySet()) {
//			System.err.println(e.getKey()+" "+(e.getKey().toLowerCase()
//					.replaceAll("[^a-z0-9\\-]+","-")
//					.replaceAll("^-+", "")
//					.replaceAll("-+$", "")
//					+".svg"));
//			System.err.println("   "+e.getValue());
//		}
//		
//		ctx.close();
//		Thread.sleep(1000);
		
	}
	
	
	

}
