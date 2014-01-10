package edu.hebtu.movingcampus.entity;

public class ExamineInfor {
	private String bh;// 考试编号
	// private ArrayList<String> jsm;//教师名
	private String kcm;// 课程名
	private String ksrq;// 考试日期
	private String kssj;// 考试时间
	private String roomid;// 教室号

	@Override
	public String toString() {
		return "ExamineInfor [bh=" + bh + ", kcm=" + kcm + ", ksrq=" + ksrq
				+ ", kssj=" + kssj + ", roomid=" + roomid + "]";
	}

	public String getBh() {
		return this.bh;
	}

	public String getKcm() {
		return this.kcm;
	}

	public String getKsrq() {
		return this.ksrq;
	}

	public String getKssj() {
		return this.kssj;
	}

	public String getRoomid() {
		return this.roomid;
	}

	public void setBh(String paramString) {
		this.bh = paramString;
	}

	public void setKcm(String paramString) {
		this.kcm = paramString;
	}

	public void setKsrq(String paramString) {
		this.ksrq = paramString;
	}

	public void setKssj(String paramString) {
		this.kssj = paramString;
	}

	public void setRoomid(String paramString) {
		this.roomid = paramString;
	}

}
