import java.util.ArrayList;
import java.util.HashMap;

public class Timetable {
	private static final int PAGE_WIDTH = 8;
	private static HashMap<String, String> codeMap;
	private static HashMap<String, Station> stationMap;
	private String schedule;
	private ArrayList<Station> stationList;
	private int[] pageLimits;

	public Timetable(ArrayList<Station> stationList, String schedule, HashMap<String, String> codeMap,
			HashMap<String, Station> stationMap) {
		this.stationList = stationList;
		this.schedule = schedule;
		this.codeMap = codeMap;
		this.stationMap = stationMap;
	}

	/**
	 * Get list of train stop times at a given station, based on the requesting
	 * Timetable's schedule. e.g. Monday-Friday Timetable will get Monday-Friday
	 * times from stations, Sunday Timetable will get Sunday times from stations
	 * 
	 * @param station - the station to get times for
	 * @return ArrayList - containing list of train stop times at the given station
	 */
	public ArrayList<String> getStationTimes(Station station) {
		ArrayList<String> stationTimes = null;
		switch (schedule) {
		case "Monday - Friday":
			stationTimes = station.getMonFriTimes();
			break;
		case "Saturday":
			stationTimes = station.getSatTimes();
			break;
		case "Sunday":
			stationTimes = station.getSunTimes();
			break;
		default:
			System.out.println("Error: Timetable schedule not correctly set, defaulting to Monday-Friday times");
			stationTimes = station.getMonFriTimes();
		}
		return stationTimes;
	}

	/**
	 * Formats the station names, so they align evenly in a table. Calculates the
	 * longest station name, and appends sufficient spaces to the start of station
	 * names to make them equal length
	 */
	public void formatStationNames() {
		int longestName = getLongestName();
		for (Station station : stationList) {
			String formattedName = station.getName();
			int toAppend = longestName - formattedName.length();
			for (int i = 0; i < toAppend; i++) {
				formattedName = " " + formattedName;
			}
			station.setFormattedName(formattedName);
		}
	}

	private int getLongestName() {
		int longestName = 0;
		for (Station station : stationList) {
			int nameLength = station.getName().length();
			if (nameLength > longestName) {
				longestName = nameLength;
			}
		}
		return longestName;
	}

	/**
	 * Splits table up into separate pages the size of PAGE_WIDTH constant. If not
	 * possible to divide equally, works out the remainder left to go on the final,
	 * smaller page
	 * 
	 * @return int array - represents pages of the table Array contents = what index
	 *         is printed *up to and including* on that page e.g. pages[0] == 7,
	 *         pages[1] == 15 (for a page width of 8)
	 */
	public int[] delimitPages() {
		int tableWidth = getTableWidth();
		int pageRemainder = tableWidth % PAGE_WIDTH;
		if (pageRemainder == 0) {

			int pageCount = tableWidth / PAGE_WIDTH;
			pageLimits = new int[pageCount];
			for (int i = 0; i < pageLimits.length; i++) {
				pageLimits[i] = ((i + 1) * PAGE_WIDTH) - 1;
			}

		} else {
			int pageCount = (tableWidth / PAGE_WIDTH) + 1;
			pageLimits = new int[pageCount];
			for (int i = 0; i < pageLimits.length - 1; i++) {
				pageLimits[i] = ((i + 1) * PAGE_WIDTH) - 1;
			}
			pageLimits[pageLimits.length - 1] = (pageLimits[pageLimits.length - 2]) + pageRemainder;
		}

		return pageLimits;

	}

	/**
	 * 
	 * @return int - total number of columns in timetable (total number of services
	 *         / stops at a given station)
	 */
	private int getTableWidth() {
		int tableWidth = getStationTimes(stationList.get(0)).size();
		return tableWidth;
	}

	public String getSchedule() {
		return schedule;
	}

	public void setSchedule(String schedule) {
		this.schedule = schedule;
	}

	public ArrayList<Station> getStationList() {
		return stationList;
	}

}
