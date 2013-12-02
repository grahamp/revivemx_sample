package info.grahamp.revivemx.sample.client.gps.mock;

import info.grahamp.revivemx.sample.client.gps.IGPS_Driver;
import info.grahamp.revivemx.sample.client.gps.IGPS_Listener;
import info.grahamp.revivemx.sample.shared.ILocations;

import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author grahampoor Test / Fake GPS callback driver for reviveMX exercise. If
 *         this was more than a sample three hour test we would have a abstract
 *         factory creating this test one and the real one Instead of GPS
 *         coordinates, the plate will send symbolic locations and the server
 *         will always respond with a fixed message for a given location. The
 *         locations are corresponding messages are as follows: HomeLot ->
 *         “Ready to roll!” DowntownParking -> “Check me out UptownParking ->
 *         “Rent me” OutOfBounds -> “Help! I’m lost” Each plate should begin at
 *         the HomeLot, randomly travels to three other locations (repeats are
 *         fine) and then returns to the HomeLot. It should take 10 seconds to
 *         travel from one location to another.
 * 
 */
public class TestGPSDriver implements IGPS_Driver, ILocations {
	IGPS_Listener listenerToCallOnChange;
	String current_location = ILocations.HomeLot;
	static final private int locationCount = 4;
	static final private int seconds = 10;
	private TestGPSDriver() {
	}

	public TestGPSDriver(IGPS_Listener listernerIn) {
		setListener(listernerIn);
	}

	/**
	 * @param listener
	 *            Will invoke the calledback and passed the new value when the
	 *            location changes.
	 */
	public void setListener(IGPS_Listener listener) {

		listenerToCallOnChange = listener;
	}

	// runnable thread
	class RunnableThread implements Runnable {
		@Override
		public void run() {
			listenerToCallOnChange.onLocationChanged(ILocations.HomeLot);
		}
	}

	// public class ScheduledThreadPoolExecutorTest {
	public static void main(String... args) throws InterruptedException,
			ExecutionException {
		class TestListener implements IGPS_Listener {
			int count=0;
			@Override
			public void onLocationChanged(String symbolicLocation) {
				System.out.println((count++)+")"+symbolicLocation+" "+new Date(System.currentTimeMillis()));
				
			}
			
		}
		IGPS_Listener tl = new TestListener();
		TestGPSDriver td=new TestGPSDriver(tl);
		int corePoolSize = 2;
		// creates ScheduledThreadPoolExecutor object with number of thread 2
		ScheduledThreadPoolExecutor stpe = new ScheduledThreadPoolExecutor(
				corePoolSize);
		// starts runnable thread
		while(td.getListener() != null) {
		stpe.execute(td.new RunnableThread());
		for (int i = 0; i < locationCount; i++) {
			// starts callable thread that will start after 5 seconds
			ScheduledFuture<Integer> sf = stpe.schedule(
					td.new CallableThread(), seconds,
					TimeUnit.SECONDS);
			// gets value returned by callable thread
			int res = sf.get();
			// returns active thread
			int activeCnt = stpe.getActiveCount();
		}// stops all the threads in ScheduledThreadPoolExecuto
		}
		stpe.shutdownNow();
		System.out.println(stpe.isShutdown());

	}

	// callable thread that will return value
	class CallableThread implements Callable<Integer> {
		@Override
		public Integer call() throws Exception {
			return provideRandomLocationThatIsNotHome();
		}

		private Integer provideRandomLocationThatIsNotHome() {
			// Index 0 is HomeLot it is special is our starting place and never driven to.
			// The ceiling of 1 less than the total location
			// should give us random values that don't include 0 which is Home
			
			int newLocation = (int) Math.ceil(Math.random() *(LOCATIONS.length-1));
			 
			 listenerToCallOnChange.onLocationChanged(LOCATIONS[newLocation]);
			 return newLocation;
		}
	}

	@Override
	public void run() {
		int corePoolSize = 2;
		// creates ScheduledThreadPoolExecutor object with number of thread 2
		ScheduledThreadPoolExecutor stpe = new ScheduledThreadPoolExecutor(
				corePoolSize);
		// starts runnable thread
		// Send data while there are listeners.
		while(getListener() != null) { 
		stpe.execute(this.new RunnableThread());
		// starts callable thread that will start after 10 seconds

	
		for (int i = 0; i < locationCount; i++) {
			ScheduledFuture<Integer> sf = stpe.schedule(
					this.new CallableThread(), seconds,
					TimeUnit.SECONDS);
			// gets value returned by callable thread
			int res = -1;
			try {
				res = sf.get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		//	System.out.println("value returned by Callable Thread." + res);
			
			// returns active thread
			int activeCnt = stpe.getActiveCount();
			//System.out.println("activeCnt:" + activeCnt);
		}
	
		} // end while
		// stops all the threads in ScheduledThreadPoolExecutor
		stpe.shutdownNow();
		System.out.println(stpe.isShutdown());
	}

	private IGPS_Listener getListener() {
		return this.listenerToCallOnChange;
	}

	@Override
	public void stop() {
		this.listenerToCallOnChange = null;
		
	}
}
