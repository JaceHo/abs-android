package edu.hebtu.movingcampus.library.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import edu.hebtu.movingcampus.dao.BaseDao;
import edu.hebtu.movingcampus.library.entity.Book;

public class BookDao {
	
	
	
	public ArrayList<Book>  GetBook(String days) {
		ArrayList<Book> list = new ArrayList<Book>();
		Connection connection = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		int day = Integer.parseInt(days);
		try {
			connection = BaseDao.getCon();
			String sql="SELECT * FROM news Where catagory="+day;
			pstm = connection.prepareStatement(sql);
			rs = pstm.executeQuery();
			Book book = null;
			while (rs.next()) {
				book = new Book();
				book.setFine(rs.getInt(3));
				
				list.add(book);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			BaseDao.closeConn(rs, pstm, connection);
		}
		return list;
	}
}