package de.ffle.mapcollector.rest.meshviewer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class MeshviewerLink {
	
	@JsonProperty("source")
	public String sourceId;
	
	@JsonProperty("source_address")
	public String sourceMac;
	
	@JsonProperty("source_tq")
	public Double sourceTq;
	
	@JsonProperty("target")
	public String targetId;
	
	@JsonProperty("target_address")
	public String targetMac;

	@JsonProperty("target_tq")
	public Double targetTq;
	
	public String type;
	
}
