package edu.hebtu.movingcampus.dao;

import java.io.InputStream;
import java.util.Properties;

public class Prop {
	public String url;
	public String user;
	public String password;
	public String driver;
	public Properties props;

	public Prop() {
		try {
			props = new Properties();
			InputStream in = this.getClass().getResourceAsStream("/db.properties");
			props.load(in);
		} catch (Exception e) {
			e.printStackTrace();
		}
		url = props.getProperty("jdbc.url").toString();
		user = props.getProperty("jdbc.username").toString();
		password = props.getProperty("jdbc.password").toString();
		driver = props.getProperty("jdbc.driver").toString();
	}
}