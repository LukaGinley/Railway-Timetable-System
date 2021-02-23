import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class UserInterface {
	static Scanner inputScan = new Scanner(System.in);
	static int currentPage = 0;
	static int finalPage;

	/**
	 * Keep drawing table while user is viewing/turning pages (drawTable
	 * returns true) Break out of loop once user returns to main menu (drawTable
	 * returns false)
	 * 
	 * @param monFriTable
	 * @param satTable
	 * @param sunTable
	 * @throws FileNotFoundException
	 */
	public static void runProgram(Timetable monFriTable, Timetable satTable, Timetable sunTable)
			throws FileNotFoundException {
		while (true) {
			String mainMenuSelection = mainMenu();
			if (!mainMenuSelection.equals("5")) {
				Timetable selectedTimetable = null;
				switch (mainMenuSelection) {
				case "1":
					selectedTimetable = monFriTable;
					break;
				case "2":
					selectedTimetable = satTable;
					break;
				case "3":
					selectedTimetable = sunTable;
					break;
				// case "4":
				// if (isFilterValid(monFriTable)) {
				// filterStations();
				// break;
				// }
				default:
					System.out.println("Error: Timetable selection not correctly set, defaulting to Monday-Friday");
					selectedTimetable = monFriTable;
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

	/*
	 * //TODO: Rework into fully functional filtering feature private static boolean
	 * isFilterValid(Timetable selectedTimetable) {
	 * System.out.println("Enter origin station name/code"); String origin =
	 * inputScan.nextLine();
	 * System.out.println("Enter destination station name/code"); String destination
	 * = inputScan.nextLine();
	 * 
	 * ArrayList<Station> stationList = selectedTimetable.getStationList(); boolean
	 * foundOrigin = false; boolean foundDestination = false; for (Station station :
	 * stationList) { if (origin.equals(station.getCode()) ||
	 * origin.equals(station.getName())) { foundOrigin = true;
	 * System.out.println("DEBUG: ORIGIN STATION FOUND"); } if
	 * (destination.equals(station.getCode()) ||
	 * destination.equals(station.getName())) { foundDestination = true;
	 * System.out.println("DEBUG: DESTINATION STATION FOUND"); } } if (foundOrigin
	 * == false) { System.out.println("Could not find origin station"); return
	 * false; } if (foundDestination == false) {
	 * System.out.println("Could not find destination station"); return false; }
	 * System.out.println("DEBUG: STATIONS CORRECTLY FOUND"); return true; }
	 */

	/**
	 * Prints main menu to screen, gets validated selection from user
	 * 
	 * @return String - user's selection e.g. "2"
	 */
	private static String mainMenu() {
		System.out.println("--- Java Console Based Railway Timetable ---");
		System.out.println("1 - View Monday - Friday Timetable");
		System.out.println("2 - View Saturday Timetable");
		System.out.println("3 - View Sunday Timetable");
		System.out.println("4 - Filter Stations");
		System.out.println("5 - Quit");
		System.out.println("Please input a number to proceed: ");

		String mainMenuSelection = validate(new String[] { "1", "2", "3", "4", "5" });
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
}
