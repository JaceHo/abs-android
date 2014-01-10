package edu.hebtu.movingcampus.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Iterator;

public class BaseDao {

	public static Connection getCon() {
		Prop prop = new Prop();
		Connection connection = null;
		try {
			Class.forName(prop.driver);
			connection = DriverManager.getConnection(prop.url, prop.user,prop.password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return connection;
	}

	public static void closeConn(ResultSet rs, PreparedStatement pstm,
			Connection con) {
		try {
			if (rs != null)
				rs.close();
			if (pstm != null)
				pstm.close();
			if (con != null) {
				con.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 防止sql注入1
	public static String TransactSQLInjection(String sql) {
		return sql.replaceAll(".*([';]+|(--)+).*", " ");
	}

	// 防止sql注入2
	private static String in_str = "'|and|exec|insert|select|delete|update|count|*|%|chr|mid|master|truncate|char|declare|; |or|-|+|,";

	public static boolean sql_inj(String str) {
		String[] inj_stra = in_str.split("\\|");
		for (int i = 0; i < inj_stra.length; i++) {
			if (str.indexOf(" " + inj_stra[i] + " ") >= 0) {
				return true;
			}
		}
		return false;
	}
	public static boolean sql_jc(Iterator<String[]> values){
		while (values.hasNext()) {
			String[] value = (String[]) values.next();
			for (int i = 0; i < value.length; i++) {
				if (BaseDao.sql_inj(value[i])) {
					return true;
				}
			}
		}
		return false;
	}
}
