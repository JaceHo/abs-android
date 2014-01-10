package edu.hebtu.movingcampus.update.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.hebtu.movingcampus.update.control.XmlControl;

/**
 * Servlet implementation class mep
 */
@WebServlet("/update")
public class UpdateServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UpdateServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		try {
			PrintWriter out = response.getWriter();
			
			String version = XmlControl.getElements("version", getServletContext().getRealPath("/mep/update.xml"));
			String url = XmlControl.getElements("url", getServletContext().getRealPath("/mep/update.xml"));
			String apk = XmlControl.getElements("apk", getServletContext().getRealPath("/mep/update.xml"));
			String information = XmlControl.getElements("information", getServletContext().getRealPath("/mep/update.xml"));
			
			out.println("当前版本号:"+version);
			out.println("软件下载地址:"+url);
			out.println("软件名称:"+apk);
			out.println("更新信息:"+information);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
