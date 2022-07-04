package de.ffle.mapcollector.rest.meshviewer;

import java.time.ZonedDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class MeshviewerNode {

	@JsonProperty("node_id")
	public String nodeId;
	
	@JsonProperty("hostname")
	public String nodeName;

	public ZonedDateTime firstseen;
	public ZonedDateTime lastseen;
	
	public List<String> addresses;
	
	public String mac;

	public String contact;
	
	public String model;
	
	public MeshviewerLocation location;
	
	public MeshviewerFirmware firmware;
	
	public MeshviewerAutoUpdater autoupdater;
	
	public String domain;
	
	@JsonProperty("is_online")
	public boolean online;

	@JsonProperty("is_gateway")
	public boolean isGateway;
	
	// Statstics (online only)
	
	public String gateway;
	
	@JsonProperty("clients")
	public Integer clientsTotal;
	
	@JsonProperty("clients_wifi24")
	public Integer clientsWifi24;
	
	@JsonProperty("clients_wifi5")
	public Integer clientsWifi5;

	public ZonedDateTime uptime;
	
	@JsonProperty("loadavg")
	public Double loadAvg;
	
	@JsonProperty("memory_usage")
	public Double memoryUsage;
	
}

