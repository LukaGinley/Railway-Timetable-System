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
	public static void runProgram(Timetable monFriTable, Timetable satTable, Timetable sunTable)
			throws FileNotFoundException {

		Timetable selectedTimetable = null;
		while (true) {
			String mainMenuSelection = mainMenu();
			if (!mainMenuSelection.equals("3")) {

				switch (mainMenuSelection) {
				case "1":
					selectedTimetable = selectTimetable(monFriTable, satTable, sunTable);

					break;
				case "2":
					selectedTimetable = filterTimetable(selectTimetable(monFriTable, satTable, sunTable));
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

	public static Timetable selectTimetable(Timetable monFriTable, Timetable satTable, Timetable sunTable) {
		System.out.println("-- Select Timetable --");
		System.out.println("1 - Monday - Friday");
		System.out.println("2 - Saturday");
		System.out.println("3 - Sunday");

		Timetable selectedTimetable;
		String menuSelection = validate(new String[] { "1", "2", "3", "4" });
		switch (menuSelection) {
		case "1":
			selectedTimetable = monFriTable;
			break;
		case "2":
			selectedTimetable = satTable;
			break;
		case "3":
			selectedTimetable = sunTable;
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
		System.out.println("4 - Return to menu");
		String userSelection = validate(new String[] { "1", "2", "3", "4" });
		return userSelection;
	}

	public static Timetable filterTimetable(Timetable unfilteredTable) {
		String userSelection = filterMenu();
		if (userSelection.equals("4")) {
			return unfilteredTable;
		} else if (userSelection.equals("3")) {
			return filterDouble(unfilteredTable);
		} else {
			return filterSingle(unfilteredTable);
		}
	}

	public static Timetable filterDouble(Timetable unfilteredTable) {
		System.out.println("Enter origin station code:");
		String originCode = inputScan.nextLine();
		System.out.println("Enter destination station code:");
		String destinationCode = inputScan.nextLine();
		ArrayList<Station> originalList = unfilteredTable.getStationList();
		ArrayList<Station> filteredList = new ArrayList<>();
		for (Station station : originalList) {
			if ((station.getCode().equals(destinationCode)) || (station.getCode().equals(originCode))) {
				filteredList.add(station);
			}
		}

		ArrayList<Integer> toRemove = new ArrayList<>();

		for (Station station : filteredList) {
			ArrayList<String> stationTimes = unfilteredTable.getStationTimes(station);
			for (int i = 0; i < stationTimes.size(); i++) {
				if (stationTimes.get(i).equals("-")) {
					toRemove.add(i);
				}
			}
		}

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
		filteredTable.isOriginDestinationFiltered = true;
		return filteredTable;
	}

	public static Timetable filterSingle(Timetable unfilteredTable) {
		System.out.println("Enter station code:");
		String selectedCode = inputScan.nextLine();
		ArrayList<Station> originalList = unfilteredTable.getStationList();
		ArrayList<Station> filteredList = new ArrayList<>();

		Station selectedStation = null;
		for (Station station : originalList) {
			if (station.getCode().equals(selectedCode)) {
				selectedStation = station;
			}
		}
		if (selectedStation == null) {
			System.err.println("Could not find station code, returning unfiltered table");
			return unfilteredTable;
		} else {
			System.err.println(
					"DEBUG: Station selected is " + selectedStation.getName() + " (" + selectedStation.getCode() + ")");
		}
		ArrayList<String> selectedTimes = unfilteredTable.getStationTimes(selectedStation);
		System.err.println("Length of stationTimes for " + selectedCode + " is " + selectedTimes.size());
		ArrayList<Integer> toRemove = new ArrayList<>();
		for (int i = 0; i < selectedTimes.size(); i++) {
			if (selectedTimes.get(i).equals("-")) {
				System.err.println("DEBUG: Index " + i + " contains " + selectedTimes.get(i));
				System.err.println("TO REMOVE");
				toRemove.add(i);
			} else {
				System.err.println("DEBUG: Index " + i + " contains " + selectedTimes.get(i));
				System.err.println("NOT REMOVED");
			}
		}
		filteredList = originalList;
		for (Station station : filteredList) {
			System.err.println("Currently on station " + station.getName());
			ArrayList<String> stationTimes = unfilteredTable.getStationTimes(station);

			for (int i = stationTimes.size() - 1; i >= 0; i--) {
				if (toRemove.contains(i)) {
					System.err.println("Removing column " + i + ", containing " + stationTimes.get(i));
					stationTimes.remove(i);
				} else {
					System.err.println("Printing column " + i + ", containing " + stationTimes.get(i));
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
			String printedRow = station.getFormattedName() + "\t";
			ArrayList<String> stationTimes = selectedTimetable.getStationTimes(station);
			for (int i = printFrom; i <= printTo; i++) {
				printedRow += "\t" + stationTimes.get(i) + "\t";
			}
			System.out.println(printedRow);
		}
		if (selectedTimetable.isOriginDestinationFiltered) {
			String durationRow = "Duration: ";
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

	private static boolean isFilterValid(Timetable selectedTimetable) {
		System.out.println("Enter origin station name/code");
		String origin = inputScan.nextLine();
		System.out.println("Enter destination station name/code");
		String destination = inputScan.nextLine();

		ArrayList<Station> stationList = selectedTimetable.getStationList();
		boolean foundOrigin = false;
		boolean foundDestination = false;
		for (Station station : stationList) {
			if (origin.equals(station.getCode()) || origin.equals(station.getName())) {
				foundOrigin = true;
				System.out.println("DEBUG: ORIGIN STATION FOUND");
			}
			if (destination.equals(station.getCode()) || destination.equals(station.getName())) {
				foundDestination = true;
				System.out.println("DEBUG: DESTINATION STATION FOUND");
			}
		}
		if (foundOrigin == false) {
			System.out.println("Could not find origin station");
			return false;
		}
		if (foundDestination == false) {
			System.out.println("Could not find destination station");
			return false;
		}
		System.out.println("DEBUG: STATIONS CORRECTLY FOUND");
		return true;
	}

}
