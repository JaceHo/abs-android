package edu.hebtu.movingcampus.card.entity;

public class Card {
private Boolean Status;
private Double count;
private Double LastPay;

public Boolean getStatus() {
	return Status;
}
public void setStatus(Boolean status) {
	Status = status;
}
public Double getCount() {
	return count;
}
public void setCount(Double count) {
	this.count = count;
}
public Double getLastPay() {
	return LastPay;
}
public void setLastPay(Double lastPay) {
	LastPay = lastPay;
}

}
