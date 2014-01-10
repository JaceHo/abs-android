package edu.hebtu.movingcampus.entity;

/**
 * @author hippo
 * @version 1.0
 * @created 14-Nov-2013 9:13:29 AM
 */
public class CardEntity {
	private Boolean status;
	private Double count;
	private Double lastPay;

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public Double getCount() {
		return count;
	}

	public void setCount(Double count) {
		this.count = count;
	}

	public Double getLastPay() {
		return lastPay;
	}

	public void setLastPay(Double lastPay) {
		this.lastPay = lastPay;
	}
}
