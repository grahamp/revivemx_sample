package info.grahamp.revivemx.sample.shared;

public interface ILocations {
	public static final String HomeLot ="HomeLot"; // Is special is our starting place and never driven to.
	public static final String DowntownParking ="DowntownParking";
	public static final String UptownParking ="UptownParking";
	public static final String OutOfBounds ="OutOfBounds";
	// Index 0 is HomeLot it is special is our starting place and never driven to.
	public static final String[] LOCATIONS ={HomeLot,DowntownParking, UptownParking,OutOfBounds};
 
}
