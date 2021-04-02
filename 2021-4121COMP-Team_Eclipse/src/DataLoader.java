import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class DataLoader {
	private static final String PATH_MONFRI = "MondayFriday.csv";
	private static final String PATH_SAT = "Saturday.csv";
	private static final String PATH_SUN = "Sunday.csv";
	private static final String PATH_CODES = "RailwayStationCodes.csv";
	private static final String PATH_MONSATREV = "MondaySaturdayReversed.csv";
	private static final String PATH_SUNREV = "SundayReversed.csv";
	private static HashMap<String, String> codeMap;
	private static HashMap<String, Station> stationMap;

	public static void main(String[] args) throws FileNotFoundException {
		codeMap = setCodeMap(PATH_CODES);
		stationMap = new HashMap<>();
		Timetable monFriTable = loadTable(PATH_MONFRI, "Monday - Friday");
		Timetable satTable = loadTable(PATH_SAT, "Saturday");
		Timetable sunTable = loadTable(PATH_SUN, "Sunday");
		Timetable monSatTableReversed = loadTable(PATH_MONSATREV, "Monday - Saturday Reversed");
		Timetable sunTableReversed = loadTable(PATH_SUNREV, "Sunday Reversed");
		

		UserInterface.runProgram(monFriTable, satTable, sunTable, monSatTableReversed, sunTableReversed);
	}

	/**
	 * Maps station names to station codes
	 * 
	 * @param filePath Path to CSV file - 1st column contains station names, 2nd
	 *                 column contains matching station codes
	 * @return HashMap mapping station names to station codes
	 * @throws FileNotFoundException
	 */
	private static HashMap<String, String> setCodeMap(String filePath) throws FileNotFoundException {
		Scanner fileScan = new Scanner(new FileReader(filePath));
		HashMap<String, String> codeMap = new HashMap<>();
		while (fileScan.hasNext()) {
			String[] parts = parseCSV(fileScan);
			codeMap.put(parts[0], parts[1]);
		}
		return codeMap;
	}

	/**
	 * Iterates over all rows of a timetable file, splitting each row into usable
	 * data, then creates and returns a Timetable object
	 * 
	 * 
	 * @param filePath Path to CSV file - contains stations and the time of train
	 *                 stops at those stations
	 * @param schedule String description of timetable's schedule - e.g. "Monday -
	 *                 Friday" or "Sunday"
	 * @return Timetable
	 * @throws FileNotFoundException
	 */
	private static Timetable loadTable(String filePath, String schedule) throws FileNotFoundException {
		Scanner fileScan = new Scanner(new FileReader(filePath));
		ArrayList<Station> stationList = new ArrayList<>();
		Timetable table = new Timetable(stationList, schedule, codeMap, stationMap);

		while (fileScan.hasNext()) {
			ArrayList<String> times = new ArrayList<>();
			String[] parts = parseCSV(fileScan);
			Station station;
			String stationName = parts[0];
			String stationCode = codeMap.get(stationName);

			if (!stationMap.containsKey(stationCode)) { // If station object hasn't already been created, create it and
														// add to
														// map to prevent future duplication
				station = new Station(stationName, stationCode);
				stationMap.put(stationCode, station);
			} else { // Else, get the already-existing station from the map
				station = stationMap.get(stationCode);
			}

			stationList.add(station);

			if (parts[1].equals("P")) {
				station.hasParking = true;
			}
			if (parts[2].equals("B")) {
				station.hasBikeStorage = true;
			}
			if (parts[3].equals("D")) {
				station.hasDisabledAccess = true;
			}
				
			for (int i = 4; i < parts.length; i++) {
				times.add(parts[i]);
			}

			switch (schedule) {
			case "Monday - Friday":
				station.setMonFriTimes(times);
				break;
			case "Saturday":
				station.setSatTimes(times);
				break;
			case "Sunday":
				station.setSunTimes(times);
				break;
			case "Monday - Saturday Reversed":
				station.setMonSatTimesReversed(times);
				break;
			case "Sunday Reversed":
				station.setSunTimesReversed(times);
				break;
			}
		}
		return table;
	}
/**
 * Reads in row of CSV file from scanner, returns row as array, split along commas
 * @param fileScan Scanner to read from
 * @return String array - CSV row split along commas
 */
	private static String[] parseCSV(Scanner fileScan) {
		String row = fileScan.nextLine();
		String[] parts = row.split(",");
		return parts;
	}

}