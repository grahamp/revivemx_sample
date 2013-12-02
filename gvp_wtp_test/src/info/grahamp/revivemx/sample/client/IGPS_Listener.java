package info.grahamp.revivemx.sample.client;

/**
 * @author grahampoor
 * interface for fake and possibly real symbolic GPS Callback.
 */
public interface IGPS_Listener {
	void onLocationChanged(String symbolicLocation);

}
