package de.ffle.mapcollector.rest.meshviewer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class MeshviewerLocation {
	
	public Double latitude;
	public Double longitude;
	public Integer altitude;
	
	public MeshviewerLocation(Double latitude, Double longitude, Integer altitude) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
	}

	public static MeshviewerLocation of(Double latitude, Double longitude, Integer altitude) {
		if (latitude==null || longitude==null) {
			return null;
		}
		return new MeshviewerLocation(latitude, longitude, altitude);
	}

}
