package info.grahamp.revivemx.sample.shared;
public class LocationResponse implements ILocations{
  	public LocationResponse(final String id, final String location, final String message, final String transactionId){
    		this.loc = location;
    		this.id = id;
    		this.message = message;
    		this.tid = transactionId;
    	}
    	public String loc;
    	public String id;
    	public String message;
    	public String tid;
    }    