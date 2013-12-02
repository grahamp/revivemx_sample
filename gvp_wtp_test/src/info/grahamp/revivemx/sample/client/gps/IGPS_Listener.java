package info.grahamp.revivemx.sample.client.gps;

/**
 * @author grahampoor
 * interface for fake and possibly real symbolic GPS Callback.
 */
public interface IGPS_Listener {
	/**
	 * @param symbolicLocation
	 * The IGPS_Listener will call this for a a registered listener passing the String that 
	 * identifies a location.
	 */
	void onLocationChanged(String symbolicLocation);

}
