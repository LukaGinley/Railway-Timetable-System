import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class UserInterface {
	static Scanner inputScan = new Scanner(System.in);
	static int currentPage = 0;
	static int finalPage;

	/**
	 * Keep drawing table while user is viewing/turning pages (drawTable returns
	 * true) Break out of loop once user returns to main menu (drawTable returns
	 * false)
	 * 
	 * @param monFriTable
	 * @param satTable
	 * @param sunTable
	 * @throws FileNotFoundException
	 */
	public static void runProgram(Timetable monFriTable, Timetable satTable, Timetable sunTable,
			Timetable monSatTableReversed, Timetable sunTableReversed) throws FileNotFoundException {

		Timetable selectedTimetable = null;
		while (true) {
			String mainMenuSelection = mainMenu();
			if (!mainMenuSelection.equals("3")) {

				switch (mainMenuSelection) {
				case "1":
					selectedTimetable = selectTimetable(monFriTable, satTable, sunTable, monSatTableReversed,
							sunTableReversed);

					break;
				case "2":
					selectedTimetable = filterTimetable(
							selectTimetable(monFriTable, satTable, sunTable, monSatTableReversed, sunTableReversed));
					break;
				}

				while (true) {
					if (!drawTable(selectedTimetable)) {
						break;
					}
				}

			} else {
				System.out.println("Program has been terminated");
				break;
			}
		}

	}

	// TODO: Rework to allow for two directions
	public static Timetable selectTimetable(Timetable monFriTable, Timetable satTable, Timetable sunTable,
			Timetable monSatTableReversed, Timetable sunTableReversed) {
		System.out.println("-- Select Timetable --");
		System.out.println("1 - Monday - Friday");
		System.out.println("2 - Saturday");
		System.out.println("3 - Sunday");

		Timetable selectedTimetable = null;
		String menuSelection = validate(new String[] { "1", "2", "3", "4" });
		String userInput;
		switch (menuSelection) {
		case "1":
			System.out.println("1 - From Liverpool"); // Make it not display this when showing the timetable
			System.out.println("2 - From Blackpool");
			userInput = inputScan.nextLine();
			if (userInput.matches("1")) {
				selectedTimetable = monFriTable;
			} else {
				selectedTimetable = monSatTableReversed;
			}
			break;
		case "2":
			System.out.println("1 - From Liverpool"); // Make it not display this when showing the timetable
			System.out.println("2 - From Blackpool");
			userInput = inputScan.nextLine();
			if (userInput.matches("1")) {
				selectedTimetable = satTable;
			} else {
				selectedTimetable = monSatTableReversed;
			}
			break;
		case "3":
			System.out.println("1 - From Liverpool");
			System.out.println("2 - From Blackpool");
			userInput = inputScan.nextLine();
			if (userInput.matches("1")) {
				selectedTimetable = sunTable;
			} else {
				selectedTimetable = sunTableReversed;
			}

			break;

		default:
			System.out.println("Error: Timetable selection not correctly set, defaulting to Monday-Friday");
			selectedTimetable = monFriTable;
		}
		return selectedTimetable;
	}

	public static String filterMenu() {
		System.out.println("-- Filter Timetable --");
		System.out.println("1 - Origin only");
		System.out.println("2 - Destination only");
		System.out.println("3 - Origin & Destination");
		System.out.println("4 - Filter by Station Facilities");
		System.out.println("5 - Print without filtering");
		String userSelection = validate(new String[] { "1", "2", "3", "4", "5" });
		return userSelection;
	}

	public static Timetable filterTimetable(Timetable originalTable) {
		Timetable unfilteredTable = new Timetable(originalTable);
		String userSelection = filterMenu();
		if (userSelection.equals("5")) {
			return unfilteredTable;
		} else if (userSelection.equals("3")) {
			return filterDouble(unfilteredTable);
		} else if (userSelection.equals("4")) {
			return facilityFilter(facilityInput(), unfilteredTable);
		} else {
			return filterSingle(unfilteredTable);
		}
	}

	public static Timetable filterDouble(Timetable unfilteredTable) {
		ArrayList<Station> originalList = unfilteredTable.getStationList();
		ArrayList<Station> filteredList = new ArrayList<>();

		System.out.println("Enter origin station name/code:");
		Station origin = getValidStation(originalList);
		System.out.println("Enter destination station name/code:");
		Station destination = getValidStation(originalList);

		filteredList.add(origin);
		filteredList.add(destination);

		// TODO: Work out direction of travel based on origin/destination
		// give each station an index when loading in - work based on that?

		removeBlankColumns(unfilteredTable, filteredList);

		Timetable filteredTable = new Timetable(filteredList, unfilteredTable.getSchedule(),
				unfilteredTable.getCodeMap(), unfilteredTable.getStationMap());
		filteredTable.isOriginDestinationFiltered = true;
		return filteredTable;
	}

	/**
	 * Removes timetable columns where a train does not stop at a filtered station
	 * 
	 * @param timetable
	 * @param stationList
	 * 
	 */
	public static void removeBlankColumns(Timetable timetable, ArrayList<Station> stationList) {
		ArrayList<Integer> toRemove = new ArrayList<>();
		for (Station station : stationList) {
			ArrayList<String> stationTimes = timetable.getStationTimes(station);
			for (int i = 0; i < stationTimes.size(); i++) {
				if (stationTimes.get(i).equals("-")) {
					toRemove.add(i);
				}
			}
		}

		for (Station station : stationList) {
			ArrayList<String> stationTimes = timetable.getStationTimes(station);

			for (int i = stationTimes.size() - 1; i >= 0; i--) {
				if (toRemove.contains(i)) {
					stationTimes.remove(i);
				}
			}
		}
	}

	public static Timetable filterSingle(Timetable unfilteredTable) {
		ArrayList<Station> originalList = unfilteredTable.getStationList();
		ArrayList<Station> filteredList = new ArrayList<>();

		System.out.println("Enter station code:");
		Station selectedStation = getValidStation(originalList);

		ArrayList<String> selectedTimes = unfilteredTable.getStationTimes(selectedStation);
		ArrayList<Integer> toRemove = new ArrayList<>();
		for (int i = 0; i < selectedTimes.size(); i++) {
			if (selectedTimes.get(i).equals("-")) {
				toRemove.add(i);
			}
		}
		filteredList = originalList;
		for (Station station : filteredList) {
			ArrayList<String> stationTimes = unfilteredTable.getStationTimes(station);

			for (int i = stationTimes.size() - 1; i >= 0; i--) {
				if (toRemove.contains(i)) {
					stationTimes.remove(i);
				}
			}
		}

		Timetable filteredTable = new Timetable(filteredList, unfilteredTable.getSchedule(),
				unfilteredTable.getCodeMap(), unfilteredTable.getStationMap());
		return filteredTable;
	}

	/**
	 * Prints main menu to screen, gets validated selection from user
	 * 
	 * @return String - user's selection e.g. "2"
	 */
	private static String mainMenu() {
		System.out.println("--- Java Console Based Railway Timetable ---");
		System.out.println("1 - View Timetable");
		System.out.println("2 - Filter Timetable");
		System.out.println("3 - Quit");
		System.out.println("Please input a number to proceed: ");

		String mainMenuSelection = validate(new String[] { "1", "2", "3" });
		return mainMenuSelection;
	}

	/**
	 * Sub-menu while viewing timetable, allowing for changing between pages.
	 * Prevents going forward/back if it is a not a valid choice for current page.
	 * e.g. can not go back on first page, cannot go forward on last page
	 * 
	 * @return boolean - False if user wishes to return to main menu, True if user
	 *         is instead turning the page
	 */
	private static boolean pageMenu() {
		System.out.println("Page " + (currentPage + 1) + " of " + (finalPage + 1));
		String[] validSelections = { "3", "3", "3" };
		if (currentPage > 0) {
			System.out.println("1 - Previous page");
			validSelections[0] = "1";
		}
		if (currentPage < finalPage) {
			System.out.println("2 - Next page");
			validSelections[1] = "2";
		}
		System.out.println("3 - Return to menu");
		String pageSelection = validate(validSelections);
		switch (pageSelection) {
		case "1": {
			currentPage -= 1;
			break;
		}
		case "2": {
			currentPage += 1;
			break;
		}
		case "3": {
			return false;
		}
		}
		return true;
	}

	/**
	 * Takes a Timetable object, and prints it to the console. Concatenates the
	 * station name with the list of times for that station, with tabs in between.
	 * 
	 * @param selectedTimetable - Timetable the user wishes to print
	 * @return boolean - False if user wishes to return to main menu, True if user
	 *         is instead turning the page
	 */
	private static boolean drawTable(Timetable selectedTimetable) {
		selectedTimetable.formatStationNames();
		ArrayList<Station> stationList = selectedTimetable.getStationList();
		int[] pageLimits = selectedTimetable.delimitPages();
		finalPage = (pageLimits.length - 1);
		int printTo = pageLimits[currentPage];
		int printFrom;
		if (currentPage == 0) {
			printFrom = 0;
		} else {
			printFrom = pageLimits[currentPage - 1] + 1;
		}

		System.out.println("-------------------------------------\n" + selectedTimetable.getSchedule()
				+ "\n-------------------------------------");
		for (Station station : stationList) {
			String printedRow = station.getFormattedName() + " ";
			if (station.hasParking) {
				printedRow += "P ";
			} else {
				printedRow += "  ";
			}
			if (station.hasBikeStorage) {
				printedRow += "B ";
			} else {
				printedRow += "  ";
			}
			if (station.hasDisabledAccess) {
				printedRow += "D\t";
			} else {
				printedRow += " \t";
			}
			ArrayList<String> stationTimes = selectedTimetable.getStationTimes(station);
			for (int i = printFrom; i <= printTo; i++) {
				printedRow += "\t" + stationTimes.get(i) + "\t";
			}
			System.out.println(printedRow);
		}
		if (selectedTimetable.isOriginDestinationFiltered) {
			String durationRow = "Duration: \t";
			for (int i = 0; i < selectedTimetable.toAppend; i++) {
				durationRow = " " + durationRow;
			}

			ArrayList<String> durationList = selectedTimetable.setDurations();
			for (int i = printFrom; i <= printTo; i++) {
				durationRow += "\t" + durationList.get(i);
			}
			System.out.println(durationRow);
		}
		return pageMenu();
	}

	/**
	 * Gets validated input from user - takes in array of valid options, continually
	 * prompts user until their input is a valid option
	 * 
	 * @param validOptions String array, containing possible user inputs
	 * @return String, user's validated input
	 */
	private static String validate(String[] validOptions) {
		while (true) {
			String userInput = inputScan.nextLine();
			for (int i = 0; i < validOptions.length; i++) {
				if (validOptions[i].equals(userInput)) {
					return userInput;
				}
			}
			System.out.println("Please enter a valid option");
		}
	}

	/**
	 * Searches through a list for a station, based on either name or 3-letter code
	 * 
	 * 
	 * @param stationList - list of stations to search
	 * @param stationID   - either station name or station code
	 * @return Station found, null if not found
	 */
	private static Station getStation(ArrayList<Station> stationList, String stationID) {
		for (Station station : stationList) {
			if (stationID.equals(station.getCode()) || stationID.equals(station.getName())) {
				return station;
			}
		}
		return null;
	}

	/**
	 * Prompts user until they identify valid station name/code
	 * 
	 * @param stationList
	 * @return returns validated Station
	 */
	private static Station getValidStation(ArrayList<Station> stationList) {
		String input;
		Station station = null;
		while (true) {
			input = inputScan.nextLine();
			station = getStation(stationList, input);
			if (station != null) {
				break;
			} else {
				System.out.println("Not found, please enter a valid station name or 3-letter code");
			}
		}
		return station;
	}

	private static ArrayList<Boolean> facilityInput() {
		boolean hasParking = false;
		boolean hasBikeStorage = false;
		boolean hasDisabledAccess = false;
		
		ArrayList<Boolean> facilityList = new ArrayList<>();

		System.out.println("Would you like car parking?");
		System.out.println("1 - Yes");
		System.out.println("2 - No");

		switch ((validate(new String[] { "1", "2" }))) {
		case "1":
			hasParking = true;
			break;
		case "2":
			hasParking = false;
			break;
		default:
			assert (false);
		}

		System.out.println("Would you like bike racks, including storage? [Y/N] ");
		System.out.println("1 - Yes");
		System.out.println("2 - No");

		switch ((validate(new String[] { "1", "2" }))) {
		case "1":
			hasBikeStorage = true;
			break;

		case "2":
			hasBikeStorage = false;
			break;
		default:
			assert (false);
		}

		System.out.println("Would you be in need of disability assistance? [Y/N] ");
		System.out.println("1 - Yes");
		System.out.println("2 - No");

		switch ((validate(new String[] { "1", "2" }))) {
		case "1":
			hasDisabledAccess = true;
			break;

		case "2":
			hasDisabledAccess = false;
			break;
		default:
			assert (false);
		}
		

		facilityList.add(hasParking);
		facilityList.add(hasBikeStorage);
		facilityList.add(hasDisabledAccess);
		
		return facilityList;

	}
	
	private static Timetable facilityFilter(ArrayList<Boolean> facilityList, Timetable unfilteredTable) {

			ArrayList<Station> originalList = unfilteredTable.getStationList();
			ArrayList<Station> filteredList = new ArrayList<>();
			
			boolean hasParking = facilityList.get(0);
			boolean hasBikeStorage = facilityList.get(1);
			boolean hasDisabledAccess = facilityList.get(2);
			
			for (Station station:originalList) {
				if (hasParking == true && station.hasParking == false) {
					continue;
				}
				if (hasBikeStorage == true && station.hasBikeStorage == false) {
					continue;
				}
				if (hasDisabledAccess == true && station.hasDisabledAccess == false) {
					continue;
				}
				filteredList.add(station);
			}
			
			
			
			Timetable filteredTable = new Timetable(filteredList, unfilteredTable.getSchedule(),
					unfilteredTable.getCodeMap(), unfilteredTable.getStationMap());
			return filteredTable;
			
	}
	
}












