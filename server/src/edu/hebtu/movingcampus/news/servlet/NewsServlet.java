package edu.hebtu.movingcampus.news.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.hebtu.movingcampus.news.dao.NewsDao;
import edu.hebtu.movingcampus.news.entity.News;

/**
 * Servlet implementation class NewsServlet
 */
@WebServlet("/news")
public class NewsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public NewsServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter out = response.getWriter();
		String type = request.getParameter("type");
		String start = request.getParameter("from");
		String size = request.getParameter("size");
		Gson gson=new GsonBuilder().setDateFormat("MM/dd HH:mm").create();
		ArrayList<News> news = new NewsDao().GetNews(type, start, size);
		String res = gson.toJson(news);
		out.printf(res);
		out.close();
	}

}
