package edu.hebtu.movingcampus.course.entity;

public class Classes {

	private Boolean status;	
	private String num;
	private String jsm;//教师名
	private String kch;//课程号
	private String kcm;//课程名
	private String roomid;//教室号
	private String unit;//单元楼
	private String desc;//课程简介
	
	
	public Boolean getStatus() {
		return status;
	}
	public void setStatus(Boolean status) {
		this.status = status;
	}
	public String getNum() {
		return num;
	}
	public void setNum(String num) {
		this.num = num;
	}
	public String getJsm() {
		return jsm;
	}
	public void setJsm(String jsm) {
		this.jsm = jsm;
	}
	public String getKch() {
		return kch;
	}
	public void setKch(String kch) {
		this.kch = kch;
	}
	public String getKcm() {
		return kcm;
	}
	public void setKcm(String kcm) {
		this.kcm = kcm;
	}
	public String getRoomid() {
		return roomid;
	}
	public void setRoomid(String roomid) {
		this.roomid = roomid;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
}
