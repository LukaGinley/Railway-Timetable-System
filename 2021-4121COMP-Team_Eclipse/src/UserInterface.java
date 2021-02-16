import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class UserInterface {
	static Scanner inputScan = new Scanner(System.in);
	static int currentPage = 0;
	static int finalPage;

	public static void runProgram(Timetable monFriTable, Timetable satTable) throws FileNotFoundException {
		while (true) {
			String mainMenuSelection = mainMenu();
			if (!mainMenuSelection.equals("4")) { // Run program until user enters '4' at main menu (Quit)
				Timetable selectedTimetable = null;
				switch (mainMenuSelection) { // Else, set timetable based on user selection
				case "1":
					selectedTimetable = monFriTable;
					break;
				case "2":
					selectedTimetable = satTable;
					break;
				default:
					System.out.println("Error: Timetable selection not correctly set, defaulting to Monday-Friday");
					selectedTimetable = monFriTable;
				}

				while (true) {
					// Keep drawing table while user is viewing/turning pages (drawTable returns
					// true)
					// Break out of loop once user returns to main menu (drawTable returns false)
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

	private static String mainMenu() {
		System.out.println("--- Java Console Based Railway Timetable ---");
		System.out.println("1 - View Monday - Friday Timetable");
		System.out.println("2 - View Saturday Timetable");
		// System.out.println("3 - View Sunday Timetable");
		System.out.println("4 - Quit");
		System.out.println("Please input a number to proceed: ");

		String mainMenuSelection = validate(new String[] { "1", "2", "4" });
		return mainMenuSelection;
	}

	private static boolean pageMenu() { // Returns false if returning to main menu, true otherwise
		System.out.println("Page " + (currentPage + 1) + " of " + (finalPage + 1)); // Page indicator
		String[] validSelections = { "3", "3", "3" };
		if (currentPage > 0) { // Can't go back if on first page
			System.out.println("1 - Previous page");
			validSelections[0] = "1";
		}
		if (currentPage < finalPage) { // Can't go forward if on final page
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

	private static boolean drawTable(Timetable selectedTimetable) {
		ArrayList<Station> stationList = selectedTimetable.getStationList();
		int[] pageLimits = selectedTimetable.delimitPages();
		finalPage = (pageLimits.length - 1);
		int printTo = pageLimits[currentPage]; // What column to print up to (inclusive)
		int printFrom;
		if (currentPage == 0) {
			// Print from the start of timetable if on the first page
			printFrom = 0;
		} else {
			// Else, print from the end of the last page, plus one
			printFrom = pageLimits[currentPage - 1] + 1;
		}

		// Concatenate the station name (formatted) along with the times for the current
		// page
		// print out with tabs between the columns
		System.out.println("-------------------------------------\n"+selectedTimetable.getSchedule()+"\n-------------------------------------");
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

	private static String validate(String[] validOptions) {// Takes in array of valid options, prompts user until their
															// input is valid
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
