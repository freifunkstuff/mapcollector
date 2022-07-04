package de.ffle.mapcollector.rest.meshviewer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class MeshviewerAutoUpdater {
	
	public Boolean enabled;
	public String branch;
	
	public MeshviewerAutoUpdater(Boolean enabled, String branch) {
		this.enabled = enabled;
		this.branch = branch;
	}

	public static MeshviewerAutoUpdater of(Boolean enabled) {
		if (enabled==null) {
			return null;
		}
		if (enabled.booleanValue()) {
			return new MeshviewerAutoUpdater(true, "stable");
		}
		return new MeshviewerAutoUpdater(false, null);
	}

}
