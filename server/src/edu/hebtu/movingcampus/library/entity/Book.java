package edu.hebtu.movingcampus.library.entity;

public class Book {

    private String name;
    private int fine;
    private int reBorrow;
    private int remainTime;

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

}
