package edu.hebtu.movingcampus.card.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import edu.hebtu.movingcampus.card.entity.Card;
import edu.hebtu.movingcampus.dao.BaseDao;

public class CardDao {

	@SuppressWarnings("resource")
	public int Lossreport(String id, String exe) {
		String cid = BaseDao.TransactSQLInjection(id);
		Connection connection = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		Integer result = null;// 判断执行结果
		Boolean status = null;// 记录当前卡的状态
		Integer db_id = null;// 记录当前卡的ID,不是学号!
		try {
			connection = BaseDao.getCon();

			pstm = connection.prepareStatement("select * from student,allinonecard  where student.cardid =\""+ cid + "\"and student.idstudent=.allinonecard.id");
			rs = pstm.executeQuery();
			while (rs.next()) {
				status = rs.getBoolean(13);
				db_id = rs.getInt(11);}
			
			if (status) {
				if (exe.equals("unlock")) {
					result = 0;
				} else {
					pstm = connection.prepareStatement("UPDATE `allinonecard` SET `status`='0' WHERE `id`=\""+ db_id + "\"");
					result = pstm.executeUpdate();}
			} else {
				if (exe.equals("unlock")) {
					pstm = connection.prepareStatement("UPDATE `allinonecard` SET `status`='1' WHERE `id`=\""+ db_id + "\"");
					result = pstm.executeUpdate();
				} else {
					result = 0;}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			BaseDao.closeConn(rs, pstm, connection);
		}
		return result;
	}

	public Card FindByID(String id) {
		String cid = BaseDao.TransactSQLInjection(id);
		Connection connection = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		Card card = new Card();
		try {
			connection = BaseDao.getCon();
			pstm = connection
					.prepareStatement("select * from student,allinonecard  where student.cardid =\""
							+ cid + "\"and student.idstudent=.allinonecard.id");
			rs = pstm.executeQuery();
			while (rs.next()) {
				card.setCount(rs.getDouble(12));
				card.setStatus(rs.getBoolean(13));
				card.setLastPay(rs.getDouble(14));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			BaseDao.closeConn(rs, pstm, connection);
		}
		return card;
	}
}
