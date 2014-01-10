package edu.hebtu.movingcampus.room.servlet;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.hebtu.movingcampus.room.entity.ClassRoom;

/**
 * Servlet implementation class ClassRoom
 */
@WebServlet("/room")
public class ClassRoomServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ClassRoomServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		ArrayList<ClassRoom> a = new ArrayList<>();
		ClassRoom cr1 = new ClassRoom();
		cr1.setBuilding("A");
		cr1.setJc("1-2");
		cr1.setZc("3");
		cr1.setXiaoQu("新校区");
		cr1.setXq("1");
		cr1.setRoomid("1");
		a.add(cr1);
		a.add(cr1);
		a.add(cr1);
		a.add(cr1);
		a.add(cr1);
		a.add(cr1);
	}

}
