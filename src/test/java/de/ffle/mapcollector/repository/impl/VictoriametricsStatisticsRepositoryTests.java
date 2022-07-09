package de.ffle.mapcollector.repository.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import de.ffle.mapcollector.model.NodeAddress;
import de.ffle.mapcollector.model.NodeInfo;
import de.ffle.mapcollector.model.NodeStats;


public class VictoriametricsStatisticsRepositoryTests {
	
	@Test
	public void testCreateNodeMetrics() {
		NodeAddress node=new NodeAddress("1000", "10.11.12.13");
		NodeInfo info=new NodeInfo();
		NodeStats stats=new NodeStats();
		
		info.setCommunity("Leipzig");
		info.setAutoUpdate(Boolean.TRUE);
		info.setFirmwareBase("OpenWrt");
		info.setFirmwareRelease("6.0.1");
		info.setName("MyTestNode");
		info.setModel("Mocky Mock");
		info.setContactEmail("here@example.com");
		
		stats.setUptimeSeconds(3600.0);
		stats.setTrafficWifiRx(1234567l);
		stats.setTrafficWifiTx(7654321l);
		stats.setClients2g(3);
		stats.setClients5g(5);
		stats.setLoadAvg5(2.4d);
		stats.setMemTotal(8*1024*1024l);
		stats.setMemFree(3000000l);
		
		String metrics=VictoriametricsStatisticsRepository.generateMetrics(node, info, stats);
		assertThat(metrics).isNotNull();
		
		assertThat(metrics
				// replace the default labels for better test readability
				.replace("{nodeid=\"1000\",hostname=\"MyTestNode\",model=\"Mocky Mock\",domain=\"Leipzig\",owner=\"here@example.com\",autoupdater=\"enabled\",firmware_base=\"OpenWrt\",firmware_release=\"6.0.1\"}", "{_labels_}")
				.split("[\\r\\n]+"))
			.containsExactly(
					"node_info{_labels_} 1",
					"node_time.up{_labels_} 3600",
					"node_traffic.rx.bytes{_labels_} 1234567",
					"node_traffic.tx.bytes{_labels_} 7654321",
					"node_clients.wifi24{_labels_} 3",
					"node_clients.wifi5{_labels_} 5",
					"node_clients.total{_labels_} 8",
					"node_load{_labels_} 2.4",
					"node_memory.total{_labels_} 8388608",
					"node_memory.available{_labels_} 3000000",
					"# EOF"
					);
		
	}
	

}

