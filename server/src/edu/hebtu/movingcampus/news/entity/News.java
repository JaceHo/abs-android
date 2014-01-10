package edu.hebtu.movingcampus.news.entity;

import java.util.Date;

public class News {
	private String title;
	private String content;
	private ONewsType type;
	private Date date;
	private int id;
	public int getId() {
		return id;
	}
	public void setId(int iD) {
		id = iD;
	}
	public enum ONewsType {
		O_HOLIDAY, O_COLLEAGE, O_SCHOOLS, O_ACADEMIC, O_NOTICE_SPEECH, O_JOB, O_JWNOTICE, O_SECOND_HNAD, O_LOCAL;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	public ONewsType getType() {
		return type;
	}
	public void setType(ONewsType type) {
		this.type = type;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}

}
