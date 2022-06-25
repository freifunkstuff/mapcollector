package de.ffle.mapcollector.model.impl;

import de.ffle.mapcollector.model.INodeAddress;

public class NodeAddress implements INodeAddress {
	
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

	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public String getPrimaryIpAddress() {
		return primaryIpAddress;
	}

}
