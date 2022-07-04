package de.ffle.mapcollector.rest.hopglass;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_NULL)
public class HopglassNodes {
	public final int version = 2;
	public final ZonedDateTime timestamp=ZonedDateTime.now();
	public final List<HopglassNode> nodes=new ArrayList<>(); 
}
