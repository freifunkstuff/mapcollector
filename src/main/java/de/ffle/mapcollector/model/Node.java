package de.ffle.mapcollector.model;

import java.time.ZonedDateTime;

/**
 * Stored data of a node
 */
public class Node extends NodeAddress {
	
	protected ZonedDateTime lastFetched; // date/time of last fetch attempt
	protected ZonedDateTime lastUpdated; // date/time of last successfull fetch
	protected ZonedDateTime firstSeen;
	protected ZonedDateTime lastSeen;
	protected NodeInfo info;
	protected NodeStats stats;
	
	public Node(String id, String primaryIpAddress) {
		super(id, primaryIpAddress);
	}

	public ZonedDateTime getLastFetched() {
		return lastFetched;
	}

	public void setLastFetched(ZonedDateTime lastFetched) {
		this.lastFetched = lastFetched;
	}

	public ZonedDateTime getFirstSeen() {
		return firstSeen;
	}

	public void setFirstSeen(ZonedDateTime firstSeen) {
		this.firstSeen = firstSeen;
	}

	public ZonedDateTime getLastSeen() {
		return lastSeen;
	}

	public void setLastSeen(ZonedDateTime lastSeen) {
		this.lastSeen = lastSeen;
	}

	public ZonedDateTime getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(ZonedDateTime lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public NodeInfo getInfo() {
		return info;
	}

	public void setInfo(NodeInfo info) {
		this.info = info;
	}

	public NodeStats getStats() {
		return stats;
	}

	public void setStats(NodeStats stats) {
		this.stats = stats;
	}
	

}
