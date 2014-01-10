package edu.hebtu.movingcampus.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CheckpswdDaoimpl {
	public static Boolean Check(String id,String pswd) {
		String cid = BaseDao.TransactSQLInjection(id);
		String pwd = BaseDao.TransactSQLInjection(pswd);
		Boolean result=null;
		Connection connection = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			connection = BaseDao.getCon();
			pstm = connection.prepareStatement("select * from student where cardid =\""+cid+"\" and lastpid =\""+pwd+"\"");
			rs = pstm.executeQuery();			
			if (rs.next()) {
				result = true;
			} else {
				result = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			BaseDao.closeConn(rs, pstm, connection);
		}
		return result;
	}
}
