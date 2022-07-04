package de.ffle.mapcollector.model;

/**
 * Base address of a node
 */
public class NodeAddress {
	
	protected final String id;
	protected final String primaryIpAddress;
	
	public NodeAddress(String id, String primaryIpAddress) {
		this.id = id;
		this.primaryIpAddress = primaryIpAddress;
	}
	
	@Override
	public String toString() {
		return id+"/"+primaryIpAddress;
	}

	public String getId() {
		return id;
	}
	
	public String getPrimaryIpAddress() {
		return primaryIpAddress;
	}
}
