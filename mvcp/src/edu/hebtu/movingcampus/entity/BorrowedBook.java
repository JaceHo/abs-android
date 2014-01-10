package edu.hebtu.movingcampus.entity;

public class BorrowedBook {
	private String name;
	private int fine;
	private int reBorrow;
	private int remainTime;

	public int getReBorrow() {
		return reBorrow;
	}

	public void setReBorrow(int reBorrow) {
		this.reBorrow = reBorrow;
	}

	public int getRemainTime() {
		return remainTime;
	}

	public void setRemainTime(int remainTime) {
		this.remainTime = remainTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getFine() {
		return fine;
	}

	public void setFine(int fine) {
		this.fine = fine;
	}

}
