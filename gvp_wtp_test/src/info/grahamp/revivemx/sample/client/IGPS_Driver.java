package info.grahamp.revivemx.sample.client;

/**
 * @author grahampoor
 * Interface for GPS  driver for reviveMX exercise.
 * If this was more than a sample three hour test we would have a abstract factory 
 * generating the real and the test GPS driver
 *
 */
public interface IGPS_Driver extends Runnable {
	/**
	 * @param listener
	 * The implementer of this interface will invoke the called back 
	 * passed the new value when the location changes.
	 */
	void setListener(IGPS_Listener listener);
	void stop();
}
