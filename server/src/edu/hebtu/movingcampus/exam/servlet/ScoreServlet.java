package edu.hebtu.movingcampus.exam.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import edu.hebtu.movingcampus.exam.entity.Score;

/**
 * Servlet implementation class ScoreServlet
 */
@WebServlet("/exam")
public class ScoreServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ScoreServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		// /score?xn=2013&xq=2
		PrintWriter out = response.getWriter();
		ArrayList<Score> a = new ArrayList<>();
		String xq = request.getParameter("xq");
		if (xq.equals("1")) {
			Score score1 = new Score();
			score1.setAttribute("专业必修");
			score1.setCredit("4");
			score1.setName("高数");
			score1.setScore("100");
			a.add(score1);
			Score score2 = new Score();
			score2.setAttribute("专业必修");
			score2.setCredit("4");
			score2.setName("线代");
			score2.setScore("90");
			a.add(score2);
			Score score3 = new Score();
			score3.setAttribute("专业必修");
			score3.setCredit("4");
			score3.setName("离散");
			score3.setScore("80");
			a.add(score3);
			Score score4 = new Score();
			
			score4.setAttribute("专业必修");
			score4.setCredit("4");
			score4.setName("概率");
			score4.setScore("80");
			a.add(score4);

		} else {
			Score score1 = new Score();
			score1.setAttribute("专业必修");
			score1.setCredit("4");
			score1.setName("C++");
			score1.setScore("100");
			a.add(score1);
			Score score2 = new Score();
			score2.setAttribute("专业必修");
			score2.setCredit("4");
			score2.setName("C");
			score2.setScore("90");
			a.add(score2);
			Score score3 = new Score();
			score3.setAttribute("专业必修");
			score3.setCredit("4");
			score3.setName("java");
			score3.setScore("80");
			a.add(score3);
			Score score4 = new Score();
			
			score4.setAttribute("专业必修");
			score4.setCredit("4");
			score4.setName("object-c");
			score4.setScore("80");
			a.add(score4);
		}
		String res = new Gson().toJson(a);

		out.printf(res);
	}
}
