package info.grahamp.revivemx.sample.client;

import info.grahamp.revivemx.sample.client.gps.IGPS_Driver;
import info.grahamp.revivemx.sample.client.gps.IGPS_Listener;
import info.grahamp.revivemx.sample.client.gps.mock.TestGPSDriver;
import info.grahamp.revivemx.sample.shared.ILocations;
import info.grahamp.revivemx.sample.shared.LocationResponse;
import info.grahamp.revivemx.sample.shared.PlateResponse;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.Map;

import com.google.gson.Gson;

public class PlateClient implements Runnable , IGPS_Listener {
	private final String id;
	private String plate_number = null;
	private String current_location = null;
	public String getCurrent_location() {
		return current_location;
	}

	

	private String current_message=null;
	private int transaction_id = 0; // An id for a sent transaction, so we can
									 // match responses to a specific client server call
	// Could track client trans actions but seems too much for now.
	//HashMap<String, String> pendingTransactions = new HashMap<String, String>();
	private static final String SERVER = "http://localhost:8080/gvp_dynamic_web_project";
	private static final String PLATE_SERVLET = SERVER + "/PlateServer";
	private static final String LOCATION_MESSAGE_SERVLET = SERVER+ "/LocationMessageServer";
	private static final String ID_VALUE_KEY="1id_value1"; 
	private static final String TID_VALUE_KEY="2tid_value2"; 
	private static final String LOC_VALUE_KEY="3loc_value3"; 
	private static final String PLATE_REQUEST_TEMPLATE= "id="+ID_VALUE_KEY+"&tid="+TID_VALUE_KEY;
	private static final String MESSAGE_REQUEST_TEMPLATE = PLATE_REQUEST_TEMPLATE+"&loc="+LOC_VALUE_KEY;
	String transaction_id_to_send = null;

	/* This class blocks in the constructor because this class is invalid until it has a id. 
	 * Consumers of this class need be away of the blocking behaviors.*/
	public PlateClient(String plateID) {
		id = plateID;
		setPlateNumberFromServer(plateID);
		IGPS_Driver gpsDriver = new TestGPSDriver(this);
		Thread gpsThread = new Thread(gpsDriver);
		gpsThread.start();
	}

	synchronized private void setPlateNumberFromServer(String plateID) {
		String message_request = addIdAndAndTransactionIdToRequest(PLATE_REQUEST_TEMPLATE);
		String server_response = getServerResponse(PLATE_SERVLET, message_request);
		Gson gson = new Gson();
		PlateResponse resp = gson.fromJson(server_response, PlateResponse.class);
		if (id.equals(resp.id)){
			plate_number = resp.plateNumber;
			System.out.println("Client Boot => PlateID="+plateID+" Plate Number="+plate_number);
		} else {
			System.err.println("Error! Server PlateID="+plateID+"!=PlateID="+id+" Plate Number="+plate_number);
		}
	}

	@SuppressWarnings("unused")
	private PlateClient() // hide default constructor, invalid, a PlateClient instance must have an id.
	{
		id = "-1"; // Should never be called... but must assign id
	}

	synchronized private String addIdAndAndTransactionIdToRequest(String messageRequestTemplate) {
		String message_request = messageRequestTemplate.replace(ID_VALUE_KEY,id);
		transaction_id++;
		transaction_id_to_send = String.valueOf(transaction_id);
		message_request = message_request.replace(TID_VALUE_KEY,transaction_id_to_send);	
		return message_request;
	}

	synchronized private String getServerResponse(String url_string,
			String message_request) {
		String response = null;

		try {
			URL url = new URL(url_string);
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);

			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
					conn.getOutputStream()));
		
			out.write(message_request);//+"\r\n");
			out.flush();
			out.close();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));

			//while ( (response = in.readLine()) != null ) {
			if ((response = in.readLine()) != null) {
				System.out.println(response);			
			}
			in.close();
		} catch (MalformedURLException ex) {
			// a real program would need to handle this exception
		} catch (IOException ex) {
			// a real program would need to handle this exception
		}
		return response;
	}
    int tourCount = 0;
	synchronized private void setMessageFromServerUsingCurrentLocation() {
		System.out.println();
		if (current_location.equals(ILocations.HomeLot)){
			// Index 0 is HomeLot it is special is our starting place and never driven to.
			// Mark it to make the log more readable an to check this condition.
			tourCount=0;
			System.out.println();
			System.out.println("Begin: At Start Location send it to  server " +  ILocations.HomeLot);
		} else{
			tourCount++;
			System.out.println("Loc event "+tourCount+") GPS returned ["+current_location+"] send it to server get message ");
			
		}
		String message_request = addIdAndAndTransactionIdToRequest(MESSAGE_REQUEST_TEMPLATE);
		message_request = message_request.replace(LOC_VALUE_KEY,current_location);
		String server_response = getServerResponse(LOCATION_MESSAGE_SERVLET, message_request);
		Gson gson = new Gson();
		LocationResponse resp = gson.fromJson(server_response, LocationResponse.class);
		if (id.equals(resp.id)){
			// Should we display the message if the current location has not changed? if (location.equals(resp.loc))
			current_message = resp.message;
			// Plate 1 at HomeLot: Ready to roll!
			// Plate 1 is moving: AGC-388
			
			System.out.println("Client: Plate "+this.id+" is at "+current_location+": ["+current_message+"] at "+new Date(System.currentTimeMillis()));
			System.out.println("Client: Plate "+this.id+" is moving: "+this.plate_number);
			
		} else {
			System.err.println("Error! Server PlateID="+resp.id+"!=PlateID="+id+" Plate Number="+plate_number);
		}
	}
	
	/**
	 * @param newPostions
	 * Setting a new location calls the server and is a long running operation
	 * provide an option to make the call on a new thread.
	 */
	public  void startSetCurrentPositionThreaded(String new_location) {
		this.current_location = new_location;
		Thread pc_thread = new Thread(this);
		pc_thread.start();
	}

	@Override
	public void run() {
		setMessageFromServerUsingCurrentLocation();
	}
	
	public void setCurrent_location(String new_location) {
			this.current_location = new_location;
			setMessageFromServerUsingCurrentLocation();	
	}
	
	@Override
	public void onLocationChanged(String symbolicLocation) {
		//System.out.println("Location changed ="+symbolicLocation);
		setCurrent_location(symbolicLocation);
		
	}
	
	public static void main(String[] args) {
		if (args != null) {
			if (args.length > 1) {
				for (int i = 0; i < args.length; i++) {
					String plateID = args[i];
					try {
						// Run simulations on separate JVMs as specified by requirements and to model the real world.
						Map<String, String> g = System.getenv(); 
						System.out.println("Starting JVM process for plateID="+plateID);
						Runtime.getRuntime().exec(new String[] {"java", "-jar", g.get("HOME")+"./export/plate_client.jar",plateID});
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} else if (args.length > 0) {
				// just one argument;
				PlateClient pc = new PlateClient(args[0]);
			} else {
				System.out.println("!JUST Testing plate");
				PlateClient pc = new PlateClient("9");
			}
		}

	}

}
