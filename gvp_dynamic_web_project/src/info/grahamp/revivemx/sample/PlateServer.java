package info.grahamp.revivemx.sample;

import info.grahamp.revivemx.sample.shared.PlateResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

/**
 * Servlet implementation class PlateServer
 */
public class PlateServer extends HttpServlet  {
	private static int count_of_plates_served =0;
	public static int getCount_of_plates_served() {
		return count_of_plates_served;
	}

	private static final long serialVersionUID = 1L;
	private static final String CMD_KEY = "id";
	private String[] PLATE_ID_TO_NUMBER_TABLE={"AAA-000","BBB-111","CCC-222","DDD-333","EEE-444","FFF-555","GGG-666","HHH-777","III-888","JJJ-999","KKK-010"};   
	Gson gson = new Gson();	

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("doGet calls post");
		doPost(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		count_of_plates_served++;
		String id = request.getParameter(CMD_KEY);
		int plateIndex = Integer.parseInt(id);
		PlateResponse pr = new PlateResponse(id,PLATE_ID_TO_NUMBER_TABLE[plateIndex-1]);
		String jsonResponse =gson.toJson(pr);
		response.setContentType("text/plain");
	    PrintWriter out = response.getWriter();
	    out.println(jsonResponse);	    
	    out.flush();
		System.out.println("PlateServer.doPost: plates served="+count_of_plates_served+" this response="+jsonResponse);
	}

}
