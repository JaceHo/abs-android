package edu.hebtu.movingcampus.entity;

import java.util.Date;

import edu.hebtu.movingcampus.enums.NewsType;

public class NewsShort {
	protected Date date;
	protected String title;
	protected String content;
	protected NewsType type;
	protected int id;

	// 该字段暂不使用
	private String thumbnail_url;

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getDate() {
		return date;
	}

	public void setTime(Date time) {
		this.date = time;
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

	public NewsType getType() {
		return type;
	}

	public void setType(NewsType type) {
		this.type = type;
	}

	public int getIcon() {
		return type.getIconResource();
	}

	public int getID() {
		return id;
	}

	public void setID(int iD) {
		this.id = iD;
	}

	public String getThumbnail_url() {
		return thumbnail_url;
	}

	public void setThumbnail_url(String thumbnail_url) {
		this.thumbnail_url = thumbnail_url;
	}
}
