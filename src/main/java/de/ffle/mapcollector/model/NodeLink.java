package de.ffle.mapcollector.model;

import java.time.Duration;
import java.time.ZonedDateTime;

/**
 * A link between nodes. Left node is always the lower one (in alphabetical order, case insensitive)
 * Since RX/TX are available on both ends, we store both with timestamp. If both timestamps are recent,
 * we use "rq" from both ends to describe the link quality. If one of the timestamps is outdated, we
 * use rq+tq from the newer one. 
 */
public class NodeLink {
	protected String leftNodeId;
	protected String rightNodeId;
	protected LinkType type;
	
	protected ZonedDateTime leftTs;
	protected Integer leftRq;
	protected Integer leftTq;
	protected ZonedDateTime rightTs;
	protected Integer rightRq;
	protected Integer rightTq;
	
	private boolean isValid(int maximumValidityMinutes, ZonedDateTime dt) {
		return dt!=null && Duration.between(dt,ZonedDateTime.now()).toMinutes()<maximumValidityMinutes;
	}

	public boolean isValid(int maximumValidityMinutes) {
		return isValid(maximumValidityMinutes, leftTs)||isValid(maximumValidityMinutes, rightTs);
	}
	
	public String getId() {
		return new StringBuilder()
				.append(leftNodeId)
				.append(".")
				.append(rightNodeId)
				.append(".")
				.append(type)
				.toString();
	}
	
	public Integer getTq(int maximumValidityMinutes) {
		if (isValid(maximumValidityMinutes,leftTs)) {
			return leftTq;
		}
		if (isValid(maximumValidityMinutes,rightTs)) {
			return rightRq;
		}
		return null;
	}
	public Integer getRq(int maximumValidityMinutes) {
		if (isValid(maximumValidityMinutes,rightTs)) {
			return rightTq;
		}
		if (isValid(maximumValidityMinutes,leftTs)) {
			return leftRq;
		}
		return null;
	}
	
	public String getLeftNodeId() {
		return leftNodeId;
	}
	public void setLeftNodeId(String leftNodeId) {
		this.leftNodeId = leftNodeId;
	}
	public String getRightNodeId() {
		return rightNodeId;
	}
	public void setRightNodeId(String rightNodeId) {
		this.rightNodeId = rightNodeId;
	}
	public LinkType getType() {
		return type;
	}
	public void setType(LinkType type) {
		this.type = type;
	}
	public ZonedDateTime getLeftTs() {
		return leftTs;
	}
	public void setLeftTs(ZonedDateTime leftTs) {
		this.leftTs = leftTs;
	}
	public Integer getLeftRq() {
		return leftRq;
	}
	public void setLeftRq(Integer leftRq) {
		this.leftRq = leftRq;
	}
	public Integer getLeftTq() {
		return leftTq;
	}
	public void setLeftTq(Integer leftTq) {
		this.leftTq = leftTq;
	}
	public ZonedDateTime getRightTs() {
		return rightTs;
	}
	public void setRightTs(ZonedDateTime rightTs) {
		this.rightTs = rightTs;
	}
	public Integer getRightRq() {
		return rightRq;
	}
	public void setRightRq(Integer rightRq) {
		this.rightRq = rightRq;
	}
	public Integer getRightTq() {
		return rightTq;
	}
	public void setRightTq(Integer rightTq) {
		this.rightTq = rightTq;
	}
}
