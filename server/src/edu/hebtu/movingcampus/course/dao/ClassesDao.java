package edu.hebtu.movingcampus.course.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import edu.hebtu.movingcampus.course.entity.Classes;
import edu.hebtu.movingcampus.dao.BaseDao;


public class ClassesDao {
	
	
	public ArrayList<Classes> FindById(String id, String exe) {
		String cid = BaseDao.TransactSQLInjection(id);
		Connection connection = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		ArrayList<Classes> result = new ArrayList<>();
		try {
			connection = BaseDao.getCon();

			pstm = connection.prepareStatement("select * from student,allinonecard  where student.cardid =\""+ cid + "\"and student.idstudent=.allinonecard.id");
			rs = pstm.executeQuery();
			while (rs.next()) {
			
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			BaseDao.closeConn(rs, pstm, connection);
		}
		return result;
	}
}
