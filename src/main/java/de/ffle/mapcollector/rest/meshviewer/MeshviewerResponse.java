package de.ffle.mapcollector.rest.meshviewer;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class MeshviewerResponse {
	
	public ZonedDateTime timestamp=ZonedDateTime.now();
	public List<MeshviewerNode> nodes=new ArrayList<>();
	public List<MeshviewerLink> links=new ArrayList<>();
}
