package de.ffle.mapcollector.source.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import de.ffle.mapcollector.model.NodeAddress;
import de.ffle.mapcollector.source.INodeListSource;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GatewayNodeListSourceTests.TestConfig.class,
	properties = "nodelist.gateway.ip=127.0.0.1")
@EnableAutoConfiguration
public class GatewayNodeListSourceTests {
	
	static class TestConfig {
		@Bean
		public INodeListSource nodeListSource() {
			return new GatewayNodeListSource() {
				// override to get rid of network dependency for this test
				protected byte[] downloadNodes() throws IOException {
					try (InputStream in=new FileInputStream("testdata/nodes.html")) {
						return IOUtils.toByteArray(in);
					}
				}
				@Override
				protected byte[] downloadStatus() throws IOException {
					try (InputStream in=new FileInputStream("testdata/status.html")) {
						return IOUtils.toByteArray(in);
					}
				}
			};
		}
	}
	
	@Autowired
	protected INodeListSource source;
	
	@Test
	public void testFetchNodes() throws Exception {
		
		List<NodeAddress> nodes=source.fetchNodes();
		assertThat(nodes)
			.hasSize(716)
			.extracting(NodeAddress::getId,NodeAddress::getPrimaryIpAddress)
			.contains(
					tuple("1059","10.200.4.40"),
					tuple("1530","10.200.6.1"),
					tuple("51020","10.200.200.21") // list must contain the gateway itself!
				);
		
	}
	

}
