package de.ffle.mapcollector.rest.hopglass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.ffle.mapcollector.model.NodeStats;

@JsonInclude(value = Include.NON_NULL)
public class HopglassNodeStatistics {
	
	public void fill(NodeStats stats) {
		uptimeSeconds=stats.getUptimeSeconds();
		loadavg=stats.getLoadAvg5();
		if (stats.getMemTotal()!=null && stats.getMemFree()!=null) {
			memoryUsage=(1d-(stats.getMemFree()*100/stats.getMemTotal())/100d);
		}
		if (stats.getAirtime2gActive()!=null && stats.getAirtime2gBusy()!=null && stats.getAirtime2gRx()!=null && stats.getAirtime2gTx()!=null) {
			Map<String,Object> at=new HashMap<>();
			at.put("frequency", 2472);
			at.put("busy",(stats.getAirtime2gBusy()*100/stats.getAirtime2gActive())/100d);
			at.put("rx",(stats.getAirtime2gRx()*100/stats.getAirtime2gActive())/100d);
			at.put("tx",(stats.getAirtime2gTx()*100/stats.getAirtime2gActive())/100d);
			airtime.add(at);
		}
		if (stats.getAirtime5gActive()!=null && stats.getAirtime5gBusy()!=null && stats.getAirtime5gRx()!=null && stats.getAirtime5gTx()!=null) {
			Map<String,Object> at=new HashMap<>();
			at.put("frequency", 5220);
			at.put("busy",(stats.getAirtime5gBusy()*100/stats.getAirtime5gActive())/100d);
			at.put("rx",(stats.getAirtime5gRx()*100/stats.getAirtime5gActive())/100d);
			at.put("tx",(stats.getAirtime5gTx()*100/stats.getAirtime5gActive())/100d);
			airtime.add(at);
		}
		
		Integer clientsTotal=null;
		if (stats.getClients2g()!=null) {
			clients.put("wifi24", stats.getClients2g());
			clientsTotal=stats.getClients2g();
		}
		if (stats.getClients5g()!=null) {
			clients.put("wifi5", stats.getClients5g());
			if (clientsTotal==null) {
				clientsTotal=stats.getClients5g();
			} else {
				clientsTotal+=stats.getClients5g();
			}
		}
		if (clientsTotal!=null) {
			clients.put("total", clientsTotal);
		}
		
		
		if (stats.getSelectedGateway()!=null) {
			String gateway=stats.getSelectedGateway();
			if (stats.getPreferredGateway()!=null && !stats.getSelectedGateway().equals(gateway)) {
				gateway+=" ("+stats.getPreferredGateway()+")";
			}
			this.gateway=gateway;
		}
		

		
	}
	
	public Double loadavg;
	@JsonProperty("memory_usage")
	public Double memoryUsage;
	@JsonProperty("uptime")
	public Double uptimeSeconds;
	
	public List<Map<String,Object>> airtime=new ArrayList<>();
	
	public Map<String,Object> clients=new HashMap<>();
	
	public String gateway;
}

