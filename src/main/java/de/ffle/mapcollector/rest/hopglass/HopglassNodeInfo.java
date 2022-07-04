package de.ffle.mapcollector.rest.hopglass;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.ffle.mapcollector.model.NodeInfo;

@JsonInclude(value = Include.NON_NULL)
public class HopglassNodeInfo {
	
	public void fill(NodeInfo info) {
		if (info.getModel()!=null) {
			hardware=Collections.singletonMap("model", info.getModel());
		}

		if (info.getAutoUpdate()!=null) {
			Map<String,Object> autoupdate=new HashMap<>();
			autoupdate.put("enabled", info.getAutoUpdate());
			autoupdate.put("branch", "stable");
			software.put("autoupdater", autoupdate);
		}
		if (info.getFirmwareBase()!=null || info.getFirmwareRelease()!=null) {
			Map<String,Object> autoupdate=new HashMap<>();
			autoupdate.put("base", info.getFirmwareBase());
			autoupdate.put("release", info.getFirmwareRelease());
			software.put("firmware", autoupdate);
		}

		if (info.getContactEmail()!=null) {
			owner=Collections.singletonMap("contact", info.getContactEmail());
		}

		if (info.getLocationLatitude()!=null && info.getLocationLongitude()!=null) {
			location=new HashMap<>();
			location.put("latitude", info.getLocationLatitude());
			location.put("longitude", info.getLocationLongitude());
			location.put("altitude", info.getLocationAltitude());
		}
		
		// TODO: links (pages), site, domain
	}
	
	@JsonProperty("node_id")
	public String nodeId;
	
	@JsonProperty("hostname")
	public String nodeName;
	
	@JsonIgnore
	public String ipAddress;
	
	public Map<String,Object> hardware;
	
	public Map<String,Object> software=new HashMap<>();
	
	public Map<String,Object> owner;
	
	public Map<String,Object> location;
	
	@JsonProperty("network")
	private Map<String,Object> getNetwork() {
		if (ipAddress==null) {
			return null;
		}
		return Collections.singletonMap("addresses", Collections.singletonList(ipAddress));
	}
}

