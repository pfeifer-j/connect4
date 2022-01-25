package connect4.view;

/**
 * Utility-class used for printing messages to inform the user about the state
 * of the game. Represents the view-component of the MVC-model.
 */
public final class View {

  private View() {
    throw new AssertionError("Suppress the use of this utility-class");
  }

  /**
   * Prints the promt.
   */
  public static void prompt() {
    System.out.print("4inarow> ");
  }

  /**
   * Prints the message the human {@code Player} receives on a win.
   */
  public static void msgWon() {
    System.out.println("Congratulations! You won.");
  }

  /**
   * Prints the message the human {@code Player} receives after the machine
   * won.
   */
  public static void msgLost() {
    System.out.println("Sorry! Machine wins.");
  }

  /**
   * Prints the message the human {@code Player} receives in case of a tie.
   */
  public static void msgTie() {
    System.out.println("Nobody wins. Tie.");
  }

  /**
   * Prints the group of tiles which decided the games outcome.
   *
   * @param witness Contains the coordinates of the tiles used to win a game.
   */
  public static void printWitness(String witness) {
    System.out.println(witness);
  }

  /**
   * Prints the board of a game of {@code Connect4}.
   *
   * @param board Contains the board.
   */
  public static void printBoard(String board) {
    System.out.println(board);
  }

  /**
   * Prints the help-menu. This menu contains a list of every usable command
   * with a small explanation.
   */
  public static void printHelp() {
    System.out.println("""
        new: \t\t\t start a new game.
        level i: \t set level to i ∈ {1,2,3,4,5}.
        switch: \t start new game with switched opener.
        move c: \t put a stone in row c ∈ {1,2,...,n}. n = "Number of rows".
        witness: \t print the group of stones which won the game.
        print: \t\t print the current board.
        help: \t\t print the help menu.
        quit: \t\t quit the game.""");
  }

  /**
   * Prints the start of an error-message.
   */
  public static void error() {
    System.out.print("Error! ");
  }

  /**
   * Prints an error-message with descriptive text.
   *
   * @param msg Contains a description of the occurred error.
   */
  public static void msgError(String msg) {
    error();
    System.out.println(msg);
  }

  /**
   * Prints an error-message with descriptive text followed by the prompt.
   *
   * @param msg Contains a description of the occurred error.
   */
  public static void msgErrorWithPrompt(String msg) {
    error();
    System.out.println(msg);
    prompt();
  }

  /**
   * Prints an error-message after the program went into an illegal state.
   */
  public static void illegalState() {
    error();
    System.out.println("The game is in an illegal State. It's neither the "
        + "player's nor the computer's turn.");
  }

  /**
   * Print an error-message after the user entered an invalid command.
   */
  public static void noCommandFound() {
    error();
    System.out.println("Command not found. Type 'help' to show all commands.");
  }
}