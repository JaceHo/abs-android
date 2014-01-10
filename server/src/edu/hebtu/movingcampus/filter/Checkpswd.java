package edu.hebtu.movingcampus.filter;

import java.io.IOException;
import java.util.Iterator;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import edu.hebtu.movingcampus.dao.BaseDao;
import edu.hebtu.movingcampus.dao.CheckpswdDaoimpl;

/**
 * Servlet Filter implementation class Checkpswd
 */
@WebFilter("/*")
public class Checkpswd implements Filter {

	/**
	 * Default constructor.
	 */
	public Checkpswd() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		// place your code here

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse rep = (HttpServletResponse) response;
		HttpSession session = req.getSession();
		req.setCharacterEncoding("utf-8");
		rep.setCharacterEncoding("utf-8");
	
		if (req.getRequestURI().contains("mep")) {//处理非过滤网页
			chain.doFilter(request, response);
		} else {// 进行sql注入判断
			Iterator<String[]> values = req.getParameterMap().values().iterator();// 获取所有的表单参数
			if (BaseDao.sql_jc(values)) {
				response.getWriter().printf(new Gson().toJson(new String("禁止sql注入")));
			} else {//处理正常请求
				if (req.getParameter("name")!=null) {
					if (CheckpswdDaoimpl.Check(req.getParameter("name"),req.getParameter("pswd"))) {
						session.setAttribute("cid", req.getParameter("name"));
						chain.doFilter(request, response);
					} else {
						response.getWriter().printf(new Gson().toJson(new String("用户名或者密码不对")));
					}
				} else {
					Object obj = session.getAttribute("cid");
					if (obj != null) {
						chain.doFilter(request, response);
					} else {
						response.getWriter().printf(new Gson().toJson(new String("禁止访问")));
					}
				}
			}
		}
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}
}
