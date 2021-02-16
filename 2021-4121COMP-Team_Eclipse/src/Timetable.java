import java.util.ArrayList;
import java.util.HashMap;

public class Timetable {
	private static final int PAGE_WIDTH = 8;
	private static HashMap<String, String> codeMap; // Will use for station filtering
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

	public void formatStationNames() {
		int longestName = getLongestName();
		for (Station station : stationList) {
			// Append spaces to the start of the station name, so it aligns evenly in a
			// table
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

	public int[] delimitPages() {
		int tableWidth = getTableWidth();
		// Returns an int array representing pages of the table
		// Array contents = what index is printed *up to and including* on that page
		// e.g. pages[0] == 7, pages[1] == 15 (for a page width of 8)
		int pageRemainder = tableWidth % PAGE_WIDTH; // Columns remaining after dividing into full-size pages
		if (pageRemainder == 0) {
			// If no remainder, table can be fully divided into even pages
			int pageCount = tableWidth / PAGE_WIDTH;
			pageLimits = new int[pageCount];
			for (int i = 0; i < pageLimits.length; i++) {
				pageLimits[i] = ((i + 1) * PAGE_WIDTH) - 1;
			}

		} else {
			// If remainder, table can be divided into even pages *with a smaller final
			// page*
			int pageCount = (tableWidth / PAGE_WIDTH) + 1;
			pageLimits = new int[pageCount];
			for (int i = 0; i < pageLimits.length - 1; i++) {
				pageLimits[i] = ((i + 1) * PAGE_WIDTH) - 1;
			}
			pageLimits[pageLimits.length - 1] = (pageLimits[pageLimits.length - 2]) + pageRemainder;
		}

		return pageLimits;

	}

	private int getTableWidth() {
		// Returns *total* number of time columns
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
