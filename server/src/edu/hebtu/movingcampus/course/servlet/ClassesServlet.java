package edu.hebtu.movingcampus.course.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import edu.hebtu.movingcampus.course.entity.Classes;

/**
 * Servlet implementation class ClassesServlet
 */
@WebServlet("/course")
public class ClassesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ClassesServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter out = response.getWriter();
		ArrayList<ArrayList<Classes>> b = new ArrayList<ArrayList<Classes>>();
		
		
		ArrayList<Classes> a1 = new ArrayList<>();
		Classes classes1 = new Classes();
		classes1.setStatus(true);
		classes1.setNum("2");
		classes1.setDesc("C++萤火单博");
		classes1.setJsm("1");
		classes1.setKch("1");
		classes1.setKcm("C++");
		classes1.setRoomid("1");
		classes1.setUnit("202");		
		a1.add(classes1);
		for(int i= 0;i<4;i++){
			Classes classesfor1 = new Classes();
			classesfor1.setStatus(false);
			a1.add(classesfor1);
		}
		b.add(a1);
		b.add(a1);
		b.add(a1);
		b.add(a1);
		b.add(a1);
		b.add(a1);
		b.add(a1);
		
	
		String res = new Gson().toJson(b);

		out.printf(res);
	}

}
