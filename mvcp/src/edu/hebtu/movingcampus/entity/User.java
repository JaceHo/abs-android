package edu.hebtu.movingcampus.entity;

public class User {
	private Boolean sex;// 1 Male, 0 Female
	private String phoneNum;// phone number
	private String jid;// jsessionid
	protected String pid = "";// personid后6位
	private String cid;// cardid
	protected String userName = "";// liuzhaoliang
	private String college;

	// 初版不用
	protected String roleName;// teacher/student/

	private int classNum;

	public int getClassNum() {
		return classNum;
	}

	public void setClassNum(int classNum) {
		this.classNum = classNum;
	}

	public String getCollege() {
		return college;
	}

	public void setCollege(String college) {
		this.college = college;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Boolean getSex() {
		return sex;
	}

	public void setSex(Boolean sex) {
		this.sex = sex;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public String getJid() {
		return jid;
	}

	public void setJid(String jid) {
		this.jid = jid;
	}

	public String getPassword() {
		return pid.substring(pid.length() - 6, pid.length());
	}
}
