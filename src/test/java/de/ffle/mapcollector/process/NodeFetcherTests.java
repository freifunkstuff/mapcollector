package de.ffle.mapcollector.process;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.ffle.mapcollector.CommunityFilter;
import de.ffle.mapcollector.model.Node;
import de.ffle.mapcollector.model.NodeInfo;
import de.ffle.mapcollector.model.NodeStats;
import de.ffle.mapcollector.model.NodeType;
import de.ffle.mapcollector.repository.INodeRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = NodeFetcherTests.TestConfig.class)
public class NodeFetcherTests {
	
	public static class TestConfig {
		
		@Bean
		public NodeFetcher nodeFetcher() {
			return new NodeFetcher();
		}
		@Bean
		public CommunityFilter communityFilter() {
			return new CommunityFilter();
		}
		@Bean
		public INodeRepository nodeRepository() {
			return Mockito.mock(INodeRepository.class);
		}
		
	}
	
	@Autowired
	protected NodeFetcher nodeFetcher; 
	
	protected JsonNode loadSysinfo(String path) throws Exception {
		return new ObjectMapper().readTree(new File(path));
	}
	
	@Test
	public void testFetchNode1709() throws Exception {
		
		JsonNode sysinfo=loadSysinfo("testdata/1709.json");
		
		NodeInfo update=nodeFetcher.parseNodeInfo(sysinfo);
		
		assertThat(update)
			.isNotNull()
			.hasFieldOrPropertyWithValue("firmwareBase", "OpenWrt 21.02.3 r16554-1d4dea6d4f")
			.hasFieldOrPropertyWithValue("firmwareRelease", "8.0.6")
			.hasFieldOrPropertyWithValue("model", "Xiaomi Mi Router 4A Gigabit Edition")
			.hasFieldOrPropertyWithValue("nodeType", NodeType.node)
			.hasFieldOrPropertyWithValue("autoUpdate", true)
			.hasFieldOrPropertyWithValue("community", "Leipzig")
			.hasFieldOrPropertyWithValue("locationLatitude", 51.364141)
			.hasFieldOrPropertyWithValue("locationLongitude", 12.358973)
			.hasFieldOrPropertyWithValue("locationAltitude", 8)
			.hasFieldOrPropertyWithValue("name", "evergreen2020")
			.hasFieldOrPropertyWithValue("location", "1. OG")
			.hasFieldOrPropertyWithValue("contactEmail", "christoph@freifunk-leipzig.de")
			.hasFieldOrPropertyWithValue("note", "test notes")
		;
		
		
	}

	@Test
	public void testFetchNode1708() throws Exception {
		
		JsonNode sysinfo=loadSysinfo("testdata/1708.json");
		
		NodeInfo info=nodeFetcher.parseNodeInfo(sysinfo);
		
		assertThat(info)
			.isNotNull()
			.hasFieldOrPropertyWithValue("firmwareBase", "OpenWrt 21.02.2 r16495-bf0c965af0")
			.hasFieldOrPropertyWithValue("firmwareRelease", "8.0.6")
			.hasFieldOrPropertyWithValue("model", "TP-Link CPE210 v1")
			.hasFieldOrPropertyWithValue("nodeType", NodeType.node)
			.hasFieldOrPropertyWithValue("autoUpdate", true)
			.hasFieldOrPropertyWithValue("community", "Leipzig")
			.hasFieldOrPropertyWithValue("locationLatitude", 51.33557)
			.hasFieldOrPropertyWithValue("locationLongitude", 12.23935)
			.hasFieldOrPropertyWithValue("locationAltitude", 0)
			.hasFieldOrPropertyWithValue("name", "mw-cpe210-1")
			.hasFieldOrPropertyWithValue("location", "Falkenweg 5, 04420 Frankenheim")
			.hasFieldOrPropertyWithValue("contactEmail", "freifunk@michael.wyraz.de")
			.hasFieldOrPropertyWithValue("note", "")
			;

		NodeStats stats=nodeFetcher.parseNodeStats(sysinfo);
		
		assertThat(stats)
			.isNotNull()
			.hasFieldOrPropertyWithValue("memTotal", 62324736L)
			.hasFieldOrPropertyWithValue("memFree", 30515200L)
			.hasFieldOrPropertyWithValue("trafficWifiRx", 154783391L)
			.hasFieldOrPropertyWithValue("trafficWifiTx", 1159435150L)
			;
		
		
	}
	
}
