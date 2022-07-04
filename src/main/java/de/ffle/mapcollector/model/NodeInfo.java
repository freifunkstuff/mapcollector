package de.ffle.mapcollector.model;

import java.time.ZonedDateTime;

/**
 * Static info of a node that is still valid if a node goes offline
 */
public class NodeInfo {
	
	protected String community;
	protected String name;
	
	protected NodeType nodeType;
	protected String model;
	protected String firmwareBase;
	protected String firmwareRelease;
	protected Boolean autoUpdate;
	
	protected Double locationLatitude;
	protected Double locationLongitude;
	protected Integer locationAltitude;
	
	protected String location;
	protected String contactEmail;
	protected String note;
	
	protected Integer cpuCount;

	public void setCommunity(String community) {
		this.community = community;
	}
	
	public String getCommunity() {
		return community;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public NodeType getNodeType() {
		return nodeType;
	}

	public void setNodeType(NodeType nodeType) {
		this.nodeType = nodeType;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getFirmwareBase() {
		return firmwareBase;
	}

	public void setFirmwareBase(String firmwareBase) {
		this.firmwareBase = firmwareBase;
	}

	public String getFirmwareRelease() {
		return firmwareRelease;
	}

	public void setFirmwareRelease(String firmwareRelease) {
		this.firmwareRelease = firmwareRelease;
	}

	public Boolean getAutoUpdate() {
		return autoUpdate;
	}

	public void setAutoUpdate(Boolean autoUpdate) {
		this.autoUpdate = autoUpdate;
	}

	public Double getLocationLatitude() {
		return locationLatitude;
	}

	public void setLocationLatitude(Double locationLatitude) {
		this.locationLatitude = locationLatitude;
	}

	public Double getLocationLongitude() {
		return locationLongitude;
	}

	public void setLocationLongitude(Double locationLongitude) {
		this.locationLongitude = locationLongitude;
	}

	public Integer getLocationAltitude() {
		return locationAltitude;
	}

	public void setLocationAltitude(Integer locationAltitude) {
		this.locationAltitude = locationAltitude;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getContactEmail() {
		return contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	
	public Integer getCpuCount() {
		return cpuCount;
	}
	
	public void setCpuCount(Integer cpuCount) {
		this.cpuCount = cpuCount;
	}

}
