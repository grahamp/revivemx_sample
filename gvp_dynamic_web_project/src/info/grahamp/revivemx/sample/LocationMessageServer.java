package info.grahamp.revivemx.sample;


import info.grahamp.revivemx.sample.shared.LocationResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

/**
 * Servlet implementation class LocationMessageServer
 */
public class LocationMessageServer extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String ID_KEY = "id";
	private static final String LOCATION_KEY = "loc";
	private static final String TRANSACTION_ID_KEY = "tid";

	
	private HashMap<String,String> location_to_message_map= new HashMap<String,String>();
	   Gson gson = new Gson();
	static private int count_of_messages_served;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LocationMessageServer() {
        super();
        location_to_message_map.put(LocationResponse.HomeLot, "Ready to roll!");
        location_to_message_map.put(LocationResponse.DowntownParking , "Check me out");
        location_to_message_map.put(LocationResponse.UptownParking , "Rent me");
        location_to_message_map.put(LocationResponse.OutOfBounds , "Help!  I am lost");
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("doGet Location Server.. call doPost");
		doPost(request,response);
	    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		count_of_messages_served++;
		System.out.println("LocationMessageServer.doPost: messages served="+this.count_of_messages_served);
		String id = request.getParameter(ID_KEY);
		String location = request.getParameter(LOCATION_KEY);
		String transaction = request.getParameter(TRANSACTION_ID_KEY);
		String message = location_to_message_map.get(location);
		LocationResponse lr = new LocationResponse(id,location,message,transaction);
		String jsonResponse =gson.toJson(lr);
		response.setContentType("text/plain");
	    PrintWriter out = response.getWriter();
	    out.println(jsonResponse);	    
	    out.flush();
	}

	static public int getCount_of_messages_served() {
		return count_of_messages_served;
	}

}
