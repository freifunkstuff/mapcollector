package de.ffle.mapcollector.model;

import java.time.ZonedDateTime;

/**
 * Statistical info of a node that has no meaning after a node went offline
 */
public class NodeStats {
	
	protected Double uptimeSeconds;
	
	protected ZonedDateTime uptime;
	
	protected Integer clients2g;
	protected Integer clients5g;
	
	protected Long trafficWifiRx;
	protected Long trafficWifiTx;

	protected Long memTotal;
	protected Long memFree;
	
	protected Double loadAvg5;
	
	protected String preferredGateway;
	protected String selectedGateway;
	
	protected Long airtime2gActive;
	protected Long airtime2gBusy;
	protected Long airtime2gRx;
	protected Long airtime2gTx;
	
	protected Long airtime5gActive;
	protected Long airtime5gBusy;
	protected Long airtime5gRx;
	protected Long airtime5gTx;
	
	protected String port1Name;
	protected String port2Name;
	protected String port3Name;
	protected String port4Name;
	protected String port5Name;
	
	protected Integer port1Status;
	protected Integer port2Status;
	protected Integer port3Status;
	protected Integer port4Status;
	protected Integer port5Status;

	public Double getUptimeSeconds() {
		return uptimeSeconds;
	}
	public void setUptimeSeconds(Double uptimeSeconds) {
		this.uptimeSeconds = uptimeSeconds;
	}
	public ZonedDateTime getUptime() {
		return uptime;
	}
	public void setUptime(ZonedDateTime uptime) {
		this.uptime = uptime;
	}
	public Integer getClients2g() {
		return clients2g;
	}
	public void setClients2g(Integer clients2g) {
		this.clients2g = clients2g;
	}
	public Integer getClients5g() {
		return clients5g;
	}
	public void setClients5g(Integer clients5g) {
		this.clients5g = clients5g;
	}
	public Long getTrafficWifiRx() {
		return trafficWifiRx;
	}
	public void setTrafficWifiRx(Long trafficWifiRx) {
		this.trafficWifiRx = trafficWifiRx;
	}
	public Long getTrafficWifiTx() {
		return trafficWifiTx;
	}
	public void setTrafficWifiTx(Long trafficWifiTx) {
		this.trafficWifiTx = trafficWifiTx;
	}
	public Long getMemTotal() {
		return memTotal;
	}
	public void setMemTotal(Long memTotal) {
		this.memTotal = memTotal;
	}
	public Long getMemFree() {
		return memFree;
	}
	public void setMemFree(Long memFree) {
		this.memFree = memFree;
	}
	public Double getLoadAvg5() {
		return loadAvg5;
	}
	public void setLoadAvg5(Double loadAvg5) {
		this.loadAvg5 = loadAvg5;
	}
	public String getPreferredGateway() {
		return preferredGateway;
	}
	public void setPreferredGateway(String preferredGateway) {
		this.preferredGateway = preferredGateway;
	}
	public String getSelectedGateway() {
		return selectedGateway;
	}
	public void setSelectedGateway(String selectedGateway) {
		this.selectedGateway = selectedGateway;
	}
	public Long getAirtime2gActive() {
		return airtime2gActive;
	}
	public void setAirtime2gActive(Long airtime2gActive) {
		this.airtime2gActive = airtime2gActive;
	}
	public Long getAirtime2gBusy() {
		return airtime2gBusy;
	}
	public void setAirtime2gBusy(Long airtime2gBusy) {
		this.airtime2gBusy = airtime2gBusy;
	}
	public Long getAirtime2gRx() {
		return airtime2gRx;
	}
	public void setAirtime2gRx(Long airtime2gRx) {
		this.airtime2gRx = airtime2gRx;
	}
	public Long getAirtime2gTx() {
		return airtime2gTx;
	}
	public void setAirtime2gTx(Long airtime2gTx) {
		this.airtime2gTx = airtime2gTx;
	}
	public Long getAirtime5gActive() {
		return airtime5gActive;
	}
	public void setAirtime5gActive(Long airtime5gActive) {
		this.airtime5gActive = airtime5gActive;
	}
	public Long getAirtime5gBusy() {
		return airtime5gBusy;
	}
	public void setAirtime5gBusy(Long airtime5gBusy) {
		this.airtime5gBusy = airtime5gBusy;
	}
	public Long getAirtime5gRx() {
		return airtime5gRx;
	}
	public void setAirtime5gRx(Long airtime5gRx) {
		this.airtime5gRx = airtime5gRx;
	}
	public Long getAirtime5gTx() {
		return airtime5gTx;
	}
	public void setAirtime5gTx(Long airtime5gTx) {
		this.airtime5gTx = airtime5gTx;
	}
	public String getPort1Name() {
		return port1Name;
	}
	public void setPort1Name(String port1Name) {
		this.port1Name = port1Name;
	}
	public String getPort2Name() {
		return port2Name;
	}
	public void setPort2Name(String port2Name) {
		this.port2Name = port2Name;
	}
	public String getPort3Name() {
		return port3Name;
	}
	public void setPort3Name(String port3Name) {
		this.port3Name = port3Name;
	}
	public String getPort4Name() {
		return port4Name;
	}
	public void setPort4Name(String port4Name) {
		this.port4Name = port4Name;
	}
	public String getPort5Name() {
		return port5Name;
	}
	public void setPort5Name(String port5Name) {
		this.port5Name = port5Name;
	}
	public Integer getPort1Status() {
		return port1Status;
	}
	public void setPort1Status(Integer port1Status) {
		this.port1Status = port1Status;
	}
	public Integer getPort2Status() {
		return port2Status;
	}
	public void setPort2Status(Integer port2Status) {
		this.port2Status = port2Status;
	}
	public Integer getPort3Status() {
		return port3Status;
	}
	public void setPort3Status(Integer port3Status) {
		this.port3Status = port3Status;
	}
	public Integer getPort4Status() {
		return port4Status;
	}
	public void setPort4Status(Integer port4Status) {
		this.port4Status = port4Status;
	}
	public Integer getPort5Status() {
		return port5Status;
	}
	public void setPort5Status(Integer port5Status) {
		this.port5Status = port5Status;
	}
	
}
