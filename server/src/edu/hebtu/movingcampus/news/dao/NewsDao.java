package edu.hebtu.movingcampus.news.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import edu.hebtu.movingcampus.dao.BaseDao;
import edu.hebtu.movingcampus.news.entity.News;
import edu.hebtu.movingcampus.news.entity.News.ONewsType;

public class NewsDao {
	
	
	
	public ArrayList<News>  GetNews(String type,String start,String num) {
		ArrayList<News> list = new ArrayList<News>();
		Connection connection = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		int sstart = Integer.parseInt(start)-1;
		try {
			connection = BaseDao.getCon();
			String sql="SELECT * FROM news Where catagory="+type+" order by time desc limit "+sstart+","+num;
			pstm = connection.prepareStatement(sql);
			rs = pstm.executeQuery();
			News news = null;
			while (rs.next()) {
				news = new News();
				news.setContent(rs.getNString(3));
				news.setDate(rs.getDate(4));
				news.setTitle(rs.getString(2));
				news.setType(ONewsType.values()[rs.getInt(5)]);
				news.setId(rs.getInt(1));
				list.add(news);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			BaseDao.closeConn(rs, pstm, connection);
		}
		return list;
	}
}