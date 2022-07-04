package de.ffle.mapcollector.rest.hopglass;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_NULL)
public class HopglassNode {
	public ZonedDateTime firstseen;
	public ZonedDateTime lastseen;
	public HopglassNodeInfo nodeinfo=new HopglassNodeInfo();
	public HopglassNodeFlags flags=new HopglassNodeFlags();
	public HopglassNodeStatistics statistics=new HopglassNodeStatistics();
}

