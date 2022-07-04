package de.ffle.mapcollector.rest.hopglass;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_NULL)
public class HopglassNodeFlags {
	public boolean gateway=false;
	public boolean backbone=false;
	public boolean online=false;
}

