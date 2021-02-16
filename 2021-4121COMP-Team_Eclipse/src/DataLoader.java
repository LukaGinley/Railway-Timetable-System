import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

// GIT TEST
// CHANGE MADE HERE!!
// ADDED MORE COMMENTS OMG!
// THIS IS EPIC!
// ECLIPSE GANG

public class DataLoader {
	private static final String PATH_MONFRI = "MondayFriday.csv"; // Path to Monday - Friday timetable
	private static final String PATH_SAT = "Saturday.csv"; // Path to Saturday timetable
	// private static final String PATH_SUN = "Sunday.csv"; #TODO
	private static final String PATH_CODES = "RailwayStationCodes.csv";
	private static HashMap<String, String> codeMap; // Maps station names to station codes
	private static HashMap<String, Station> stationMap; // Maps station codes to station objects

	public static void main(String[] args) throws FileNotFoundException {
		codeMap = setCodeMap(PATH_CODES);
		stationMap = new HashMap<>();
		Timetable monFriTable = loadTable(PATH_MONFRI, "Monday - Friday"); // Loads stations and times from file
		Timetable satTable = loadTable(PATH_SAT, "Saturday");
		// Timetable sunTable = loadTable(PATH_SUN, "Sunday");

		UserInterface.runProgram(monFriTable, satTable); // Passes control to UserInterface class

		// UserInterface.runProgram(monFriTable, satTable, sunTable);
	}

	private static HashMap<String, String> setCodeMap(String filePath) throws FileNotFoundException { // First column is name, second codes
		Scanner fileScan = new Scanner(new FileReader(filePath));
		HashMap<String, String> codeMap = new HashMap<>();
		while (fileScan.hasNext()) {
			String[] parts = parseCSV(fileScan);
			codeMap.put(parts[0], parts[1]); // Maps station name to station code
		}
		return codeMap;
	}

	private static Timetable loadTable(String filePath, String schedule) throws FileNotFoundException {
		Scanner fileScan = new Scanner(new FileReader(filePath)); // Creates Scanner to read file contents
		ArrayList<Station> stationList = new ArrayList<>(); // List of stations on timetable
		Timetable table = new Timetable(stationList, schedule, codeMap, stationMap); // New timetable

		while (fileScan.hasNext()) {
			ArrayList<String> times = new ArrayList<>(); // List of times on current row/station
			String[] parts = parseCSV(fileScan); // Split CSV row along commas
			
			Station station;
			String stationName = parts[0]; // First part is the station name (see CSV file)
			String stationCode = codeMap.get(stationName); // Get 3-letter station code based on name
			
			if (!stationMap.containsKey(stationCode)) { //If station hasn't already been created, create it and add to map to prevent future duplication
				station = new Station(stationName, stationCode); 
				stationMap.put(stationCode, station);
			} else { //Else, get the already-existing station from the map
				station = stationMap.get(stationCode);
			}
			stationList.add(station); //Add to timetable's list
			
			for (int i = 1; i < parts.length; i++) { // For the rest of the parts in the row, add each to times list
				times.add(parts[i]);
			}
						
			switch(filePath) { // Add time list to relevant field depending on day
			case PATH_MONFRI:
				station.setMonFriTimes(times);
				break;
			case PATH_SAT:
				station.setSatTimes(times);
				break;
			//case PATH_SUN:
				//station.setSunTimes(times);
				//break;
			}
			
			table.formatStationNames();  //Correctly format station names for tabular display based on max column width
		}
		return table;
	}


	private static String[] parseCSV(Scanner fileScan) { // Reads CSV row, returns row as array
		String row = fileScan.nextLine();
		String[] parts = row.split(",");
		return parts;
	}

}