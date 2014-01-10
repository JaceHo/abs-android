package edu.hebtu.movingcampus.login.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import edu.hebtu.movingcampus.dao.BaseDao;
import edu.hebtu.movingcampus.login.entity.User;

public class UserDao {

	public User findbyid(String id) {
		String cid = BaseDao.TransactSQLInjection(id);
		User user = new User();
		Connection connection = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			connection = BaseDao.getCon();

			pstm = connection.prepareStatement("select * from student where cardid =\"" +cid+"\"");
			rs = pstm.executeQuery();
			while (rs.next()) {
				user.setClassNum(rs.getInt("classid"));
				user.setSex(rs.getBoolean("sex"));
				user.setPhoneNum(rs.getString("phonenum"));
				user.setPid(rs.getString("pid"));
				user.setUserName(rs.getString("name"));
				if(rs.getBoolean("role")){
					user.setRoleName("学生");
				}else{
					user.setRoleName("老师");
				}			
			}
			pstm =connection.prepareStatement("select * from student,institute  where student.cardid =\""+cid+"\"and student.institute=institute.idinstitute");
			rs = pstm.executeQuery();
			while (rs.next()) {
				user.setCollege(rs.getString(12));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			BaseDao.closeConn(rs, pstm, connection);
		}
		return user;
	}
}
