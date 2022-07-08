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

}
