package edu.hebtu.movingcampus.entity;

public class ClassRoom {
			private String xiaoQu ;//校区
			private String building;//教学楼
			private String jc ;//节次
			private String zc ;//周次
			private String xq ;//星期
			private String roomname;//教室名称
			public String getRoomname() {
				return roomname;
			}
			public void setRoomname(String roomname) {
				this.roomname = roomname;
			}
			private String roomid;
			public String getRoomid() {
				return roomid;
			}
			public void setRoomid(String roomid) {
				this.roomid = roomid;
			}
			public String getXiaoQu() {
				return xiaoQu;
			}
			public void setXiaoQu(String xiaoQu) {
				this.xiaoQu = xiaoQu;
			}
			public String getBuilding() {
				return building;
			}
			public void setBuilding(String building) {
				this.building = building;
			}
			public String getJc() {
				return jc;
			}
			public void setJc(String jc) {
				this.jc = jc;
			}
			public String getZc() {
				return zc;
			}
			public void setZc(String zc) {
				this.zc = zc;
			}
			public String getXq() {
				return xq;
			}
			public void setXq(String xq) {
				this.xq = xq;
			}
			public String getRoomLocation() {
				return xiaoQu + " " + building + " " + roomid;
			}

}
