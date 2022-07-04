package de.ffle.mapcollector.model;

/**
 * Update to one side of a link between node
 */
public class NodeLinkUpdate {
	protected String otherNodeId;
	protected LinkType type;
	protected int rq;
	protected int tq;
	
	public String getOtherNodeId() {
		return otherNodeId;
	}
	public void setOtherNodeId(String otherNodeId) {
		this.otherNodeId = otherNodeId;
	}
	public LinkType getType() {
		return type;
	}
	public void setType(LinkType type) {
		this.type = type;
	}
	public int getRq() {
		return rq;
	}
	public void setRq(int rq) {
		this.rq = rq;
	}
	public int getTq() {
		return tq;
	}
	public void setTq(int tq) {
		this.tq = tq;
	}
	
	
}
