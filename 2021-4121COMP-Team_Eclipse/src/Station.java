import java.util.ArrayList;

public class Station {
	private String stationName;
	private String stationCode;
	private ArrayList<String> monFriTimes;
	private ArrayList<String> satTimes;
	private ArrayList<String> sunTimes;
	private String formattedName; //Name with spaces added, so it can be printed in a table
	
	
	//Has name of station, 3-letter code, list of train stop times
	public Station(String stationName, String stationCode) {
		this.stationName = stationName;
		this.stationCode = stationCode;
	}
	
	public String getName() {
		return stationName;
	}
	
	public String getCode() {
		return stationCode;
	}
	
	public void setFormattedName(String formattedName) {
		this.formattedName = formattedName;
	}
	
	public String getFormattedName() {
		return formattedName;
	}
	
	
	public ArrayList<String> getMonFriTimes() {
		return monFriTimes;
	}

	public void setMonFriTimes(ArrayList<String> monFriTimes) {
		this.monFriTimes = monFriTimes;
	}

	public ArrayList<String> getSatTimes() {
		return satTimes;
	}

	public void setSatTimes(ArrayList<String> satTimes) {
		this.satTimes = satTimes;
	}

	public ArrayList<String> getSunTimes() {
		return sunTimes;
	}

	public void setSunTimes(ArrayList<String> sunTimes) {
		this.sunTimes = sunTimes;
	}
}
