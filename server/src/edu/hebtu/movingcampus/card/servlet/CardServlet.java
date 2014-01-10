package edu.hebtu.movingcampus.card.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import edu.hebtu.movingcampus.card.dao.CardDao;

/**
 * Servlet implementation class Cardservlet
 */
@WebServlet("/card")
public class CardServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CardServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub	
		
		PrintWriter out = response.getWriter();
		HttpSession session = request.getSession();
		if (request.getParameter("action").equals("lock")) {
			int result = new CardDao().Lossreport((String) session.getAttribute("cid"),"lock");
			{
				if (result == 1) {
					out.print(new Gson().toJson(new Boolean(true)));
				}else{
					out.print(new Gson().toJson(new Boolean(false)));
				}
			}
		}
		if (request.getParameter("action").equals("unlock")) {
			int result = new CardDao().Lossreport((String) session.getAttribute("cid"),"unlock");
			{
				if (result == 1) {
					out.print(new Gson().toJson(new Boolean(true)));
				}else{
					out.print(new Gson().toJson(new Boolean(false)));
				}
			}
		}
		if (request.getParameter("action").equals("lookup")) {
			out.print(new Gson().toJson(new CardDao().FindByID((String) session.getAttribute("cid"))));
		}
		out.close();
	}

}
