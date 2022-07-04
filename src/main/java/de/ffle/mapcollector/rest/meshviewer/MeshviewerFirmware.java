package de.ffle.mapcollector.rest.meshviewer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class MeshviewerFirmware {
	
	public String base;
	public String release;
	
	public MeshviewerFirmware(String base, String release) {
		this.base = base;
		this.release = release;
	}

	public static MeshviewerFirmware of(String base, String release) {
		if (base==null && release==null) {
			return null;
		}
		return new MeshviewerFirmware(base, release);
	}

}
