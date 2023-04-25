package main;

import main.gameObject.Team;
import main.gamesystem.Exception.DifficultyOption;
import main.gamesystem.Exception.IllegalInputException;
import main.gamesystem.Market;

import java.util.Arrays;
import java.util.Scanner;

/**
 * class for Command line User Interface.
 * @author H Yang, J Kim
 */
public class CmdLineUi implements UserInterface {

	private final Scanner scan;

	private GameEnvironment gameEnvironment;

	enum Option {

		INFO("info - Show current game status briefly"),
		MARKET( "market - Market to purchase, sell athletes or items"),
		STADIUM("stadium - Play Game!"),
		TEAM("team - Manage Team Here"),
		EXIT("exit - Terminate this game");
		final String DESCRIPTION;

		Option(String description) {
			this.DESCRIPTION = description;
		}

		public String toString() {

			return this.DESCRIPTION;
		}
	}

	enum MarketOption {
		USAGE("""
				usage
				\tbuy|sell -a [1-6]|-i [1-8]"""),
		BUY("""
				buy - show Market stock to athletes or items
				\toption
				\t\t-a\tshow purchasable athlete. purchasing process will be proceed if input number is exist and valid
				\t\t-i\tshow purchasable items. purchasing process will be proceed if input number is exist and valid"""),
		SELL("""
				sell - show Team Roster and inventory to sell athletes or items
				\toption
				\t\t-a\tshow sellable. athlete selling process will be proceed if input number is exist and valid
				\t\t-i\tshow sellable. items selling process will be proceed if input number is exist and valid""");

		final String DESCRIPTION;

		MarketOption(String string) {

			this.DESCRIPTION = string;
		}

		public String toString() {

			return this.DESCRIPTION;
		}
	}

	/**
	 * Class constructor to construct scanner
	 */
	public CmdLineUi() {
		this.scan = new Scanner(System.in);
	}

	/**
	 * initial game environment setup
	 * @param gameEnvironment game environment which is core of this program
	 */
	public void setup(GameEnvironment gameEnvironment) {
		this.gameEnvironment = gameEnvironment;

		//Welcoming~~~
		System.out.println("|||||||||||||||||||||||||||||||||||||");
		System.out.println("||        SPORTS TOURNAMENT        ||");
		System.out.println("|||||||||||||||||||||||||||||||||||||");
		System.out.println();

		//setup sequence
		String name = this.setTeamName();
		int week = this.setWeeksForSeason();
		DifficultyOption difficulty = this.setDifficulty();
		//send setup info to gameEnvironment
		gameEnvironment.set(name, week, difficulty);
		//start main program
		this.main();
	}

	/**
	 * set up player's team name. only accept the letter lengths between 3 and 15(inclusive)
	 * doesn't allow to have special characters and.
	 */
	public String setTeamName() {

		System.out.println("Please choose your team name");
		//iterate until input is valid
		while (true) {
			String input = scan.nextLine();
			try {
				//check input is valid catch exception if input is invalid
				gameEnvironment.check(input, NAME_REGEX, NAME_CHAR_REQUIREMENT);
				//print feedback to player
				System.out.println("Awesome! Your team name is " + input);
				return input;

			} catch (IllegalInputException e) {
				//print err message
				System.out.println(e.getMessage());
				if (input.length() > 15 || input.length() < 3) System.out.println(NAME_LENGTH_REQUIREMENT);
			}
		}
	}

	/**
	 * set up number of weeks for season of the game between 5 and 15 (inclusive).
	 */
	public int setWeeksForSeason() {

		System.out.println("Please choose the number of weeks for the season of the game");
		System.out.println("Minimum is 5 weeks and Maximum is 15 weeks");
		while (true) {
			String input = scan.nextLine();
			try {
				//Check input requirement
				gameEnvironment.check(input, SEASON_REGEX, VALID_NUMBER);
				//give feedback to user
				System.out.printf("The game season set %s weeks long%n", input);

				//parse input to integer and send input to gameEnvironment
				return Integer.parseInt(input);

			} catch (IllegalInputException iie) {

				//catch any wrong input requirement
				System.out.println(iie.getMessage());
			}

		}
	}

	/**
	 * set up the difficulty of the game between easy and difficult
	 */
	public DifficultyOption setDifficulty() {
		//get array of difficulty options before system start
		DifficultyOption[] options = DifficultyOption.values();
		//main system starts here
		while (true) {
			System.out.println("Choose the number of difficulty of the game level.");
			this.listing(options);
			System.out.println("Your Option(Choose Number 1 or 2): ");
			String input = scan.nextLine();
			try {
				//check input requirement if not this throws exception
				gameEnvironment.check(input, "(1|2)", INVALID_NUMBER);

				//set difficulty option if input requirement is met
				switch (input) {
					case "1" -> { return options[0]; }
					case "2" -> { return options[1]; }
				}

				// give feedback to user
				System.out.printf("Difficulty set: %s%n", options[Integer.parseInt(input) - 1].DIFFICULTY);

			} catch (IllegalInputException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	/**
	 * Main Menu CommandLine Interfaces
	 */
	public void main() {
		//get main option value
		Option[] option = Option.values();
		getInfo();
		while(true) {
			try {
				System.out.println("Please input command here");
				System.out.println("input \"help\" for more information");
				String input = scan.nextLine();

				// check input option Exception throws if input requirement is not met
				switch (input) {
					case "help" -> listing(option);
					case "exit" -> confirmExit();
					//TODO - create methods and call them here
					case "market" -> marketSystem();
					case "info" -> getInfo();
					//case "team"
					//case "stadium"
					default -> throw new IllegalInputException();
				}
			}catch (IllegalInputException e) {

				System.out.println(e.getMessage());
			}

		}

	}


	void getInfo() {
		System.out.printf("Your Team Name: %s%n", gameEnvironment.getTeam().getName());
		System.out.println("Current week: ");
		System.out.printf("Difficulty: %s%n", gameEnvironment.getDifficulty());
	}

	/**
	 * method to recruit athletes
	 */
	private void marketSystem() {
		MarketOption[] option = MarketOption.values();
		Market market = gameEnvironment.getMarket();
		Team player = gameEnvironment.getTeam();

		//Welcoming~
		System.out.println("Welcome to Trading Market!");
		while(true) {
			System.out.println("input command here\ninput \"help\" for more information\ninput \"exit\" to exit market");
			System.out.println("WARNING: There is no double-checking so decide carefully");
			String input = scan.nextLine();
			try {

				// Market has 6 athletes stocks 8 items stocks, Team can have 7 athletes and 8 items as maximum
				// if this preference is changed regex need to be fixed
				// if input requirement is not met exception will be thrown
				gameEnvironment.check(input, "exit|help|buy ((-a|-i)|(-a [1-6]|-i [1-8]))|sell ((-a|-i)|(-a [1-7]|-i [1-8]))",
						null);
				//terminate while loop to get out the market
				if(input.equals("exit")) break;

					//else check command input
				else if (input.equals("help")) {
					System.out.println(option[0]);
					listing(Arrays.copyOfRange(option, 1, option.length));
				} else if (input.equals("buy -a")) listing(market.getAthleteProduct());
					//else if (input.equals("buy -i")) listing(market.getItemProduct());
				else if(input.equals("sell -a")) listing(player.getRoster());
				else if(input.equals("sell -i")) listing(player.getInventory());
				else if (input.matches("buy -a [1-6]")) {
					String[] str = input.split(" ");
					market.purchase(market.getAthleteProduct(), Integer.parseInt(str[2])-1);
				}
//				else if (input.matches("buy -i [1-8]")){
//					String[] str = input.split(" ");
//					market.purchase(market.getItemProduct(), Integer.parseInt(str[2]) -1);
//				}
//				else if (input.matches("sell -a [1-7]")) {
//
//				}
//				else if(input.matches("sell -i [1-8]]")){
//
//				}
			} catch (IllegalInputException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	/**
	 * print options with cardinal number
	 * @param array array to print option
	 */
	public void listing(Object[] array) {

		for (int i = 1; i <= array.length; i++) {
			System.out.printf("%d. %s%n", i, (array[i - 1] == null) ? "EMPTY\n" : array[i - 1]);
		}
	}

	/**
	 * method to double-check commend exit to terminate this program entirely
	 * @throws IllegalInputException if input is not "Y" or "n" (case is sensitive) error occurs
	 */
	private void confirmExit() throws IllegalInputException {
		// double-checking before exit entire program
		// "Y" input is case-sensitive
		// if input is neither "Y" nor "n" then method throws Exception
		System.out.println("Exit this game?(Y|n)");
		String input = scan.nextLine();
		switch(input) {
			case "Y" -> {
				System.out.println("GOOD-GYE!!!");
				System.exit(0);
			}
			case "n" -> System.out.println();
			default -> throw new IllegalInputException();

		}

	}
}