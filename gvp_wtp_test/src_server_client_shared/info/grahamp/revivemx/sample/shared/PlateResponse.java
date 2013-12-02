package info.grahamp.revivemx.sample.shared;


public class PlateResponse {
	public PlateResponse(final String id, final String plateNumber){
		this.id = id;
		this.plateNumber = plateNumber;
	}
	public String id;
	public String plateNumber;

/* 
  * @see HttpServlet#HttpServlet()
 */
private PlateResponse() {
    super();
 
}
}