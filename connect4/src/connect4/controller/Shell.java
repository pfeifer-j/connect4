package connect4.controller;

import connect4.model.Board;
import connect4.model.Connect4;
import connect4.model.IllegalMoveException;
import connect4.model.Player;
import connect4.view.View;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * This utility-class represents the controller in the MVC-model. The {@code
 * Shell} controls the program by interacting with the {@code Connect4}.
 */
public final class Shell {

  private static Board connect4 = new Connect4();
  private static int level = Board.CONNECT;
  private static Player currentPlayer = Player.HUMAN;
  private static boolean quit;

  /**
   * Private constructor to hide the utility-class {@code Shell}. Since its
   * asserted that the {@code Shell} isn't instantiated, the constructor throws
   * an AssertionError on use.
   */
  private Shell() {
    throw new AssertionError("Suppress the use of this utility-class");
  }

  /**
   * Driver of the program. Reads and handles user-input.
   *
   * @param args Not in use.
   */
  public static void main(String[] args) {
    BufferedReader reader = new BufferedReader(
        new InputStreamReader(System.in));
    execute(reader);
  }

  /**
   * Executes the turns of {@code Player.HUMAN} and {@code Player.MACHINE}
   * alternately.
   *
   * @param reader Used for user-input.
   */
  private static void execute(BufferedReader reader) {
    currentPlayer = connect4.getFirstPlayer();

    do {
      if (currentPlayer.equals(Player.HUMAN) || connect4.isGameOver()) {
        executePlayersTurn(reader);
      } else if (currentPlayer.equals(Player.MACHINE)
          && !connect4.isGameOver()) {
        executeComputersTurn();
      } else {
        View.illegalState();
      }

      if (connect4.isGameOver() && !quit) {
        printResult();
      }
    } while (!quit);
  }

  /**
   * Executes the turn of the {@code Player.HUMAN}.
   *
   * @param reader Used for user-input.
   */
  private static void executePlayersTurn(BufferedReader reader) {
    boolean playerMadeHisMove = false;
    do {
      View.prompt();
      String[] input = getCommand(reader);

      if (input != null && input.length > 0 && input.length < 3) {
        playerMadeHisMove = handleCommand(input);
      } else {
        View.noCommandFound();
      }
    } while (!playerMadeHisMove);
  }

  /**
   * Executes the turn of the {@code Player.MACHINE}.
   */
  private static void executeComputersTurn() {
    try {
      connect4 = connect4.machineMove();
    } catch (InterruptedException e) {
      View.msgErrorWithPrompt("An InterruptedException can't be thrown yet.");
    } catch (IllegalMoveException e) {
      View.msgError("The could.");
    }
    currentPlayer = Player.HUMAN;
  }

  /**
   * Analysis the input and executes the related method.
   *
   * @param input The given command which was put in by the user.
   * @return True if the player finished his turn.
   */
  private static boolean handleCommand(String[] input) {
    switch (input[0].charAt(0)) {
      case 'n', 'N' -> {
        return handleNew(input);
      }
      case 'l', 'L' -> {
        return handleLevel(input);
      }
      case 's', 'S' -> {
        return handleSwitch(input);
      }
      case 'm', 'M' -> {
        return handleMove(input);
      }
      case 'w', 'W' -> {
        return handleWitness(input);
      }
      case 'p', 'P' -> {
        return handlePrint(input);
      }
      case 'h', 'H' -> {
        return handleHelp(input);
      }
      case 'q', 'Q' -> {
        return handleQuit(input);
      }
      default -> View.noCommandFound();
    }
    return false;
  }

  /**
   * Starts a new game. The {@code level} and the {@code firstPlayer} the new
   * game remain the same.
   *
   * @param input The user-input.
   * @return True if the player finished his turn.
   */
  private static boolean handleNew(String[] input) {
    if (input.length == 1) {
      Player firstPlayer = connect4.getFirstPlayer();
      connect4 = new Connect4(firstPlayer);
      currentPlayer = connect4.getFirstPlayer();
      connect4.setLevel(level);
      return true;
    } else {
      View.noCommandFound();
      return false;
    }
  }

  /**
   * Sets the level to a new level given by the user. The level has to be in
   * {1,2,3,4,5}. If the given level is lower than 0 or higher than 5 it is set
   * to 0 or 5 respectively.
   *
   * @param input The user-input.
   * @return True if the player finished his turn.
   */
  private static boolean handleLevel(String[] input) {
    if (input.length < 2) {
      View.msgError("Please select exactly one level between 1 and 5.");
    } else {
      level = Integer.parseInt(input[1]);
      connect4.setLevel(level);
    }
    return false;
  }

  /**
   * Executes the move for the {@code Player.HUMAN}.
   *
   * @param input The user-input.
   * @return True if the player finished his turn.
   */
  private static boolean handleMove(String[] input) {
    if (input.length == 2) {
      try {
        connect4 = connect4.move(Integer.parseInt(input[1]));
      } catch (IllegalMoveException e) {
        View.msgError("This row is full.");
        return false;
      }
      currentPlayer = Player.MACHINE;
      return true;
    } else {
      View.msgError(
          "Please select exactly one column. Use 'help' for more information.");
      return false;
    }
  }

  /**
   * Switches the {@code firstPlayer} and starts a new game.
   *
   * @param input The user-input.
   * @return True if the player finished his turn.
   */
  private static boolean handleSwitch(String[] input) {
    if (input.length == 1) {
      Player newFirstPlayer = connect4.getFirstPlayer();

      if (newFirstPlayer.equals(Player.HUMAN)) {
        newFirstPlayer = Player.MACHINE;
      } else if (newFirstPlayer.equals(Player.MACHINE)) {
        newFirstPlayer = Player.HUMAN;
      }
      connect4 = new Connect4(newFirstPlayer);
      connect4.setLevel(level);
      currentPlayer = newFirstPlayer;
      return true;
    } else {
      View.noCommandFound();
      return false;
    }
  }

  /**
   * Prints the group of 4 which won the game if there is a group. Prints an
   * error if the game hasn't finished yet.
   *
   * @param input The user-input.
   * @return True if the player finished his turn.
   */
  private static boolean handleWitness(String[] input) {
    if (input.length == 1) {
      String witnesses = connect4.getWitness().toString();

      if (witnesses == null) {
        View.msgError("There are no witnesses yet.");
      } else {
        witnesses = witnesses.replace("[", "").replace("]", "");
        View.printWitness(witnesses);
      }
    } else {
      View.noCommandFound();
    }
    return false;
  }

  /**
   * Prints the {@code Board}.
   *
   * @param input The user-input.
   * @return True if the player finished his turn.
   */
  private static boolean handlePrint(String[] input) {
    if (input.length == 1) {
      View.printBoard(connect4.toString());
    } else {
      View.noCommandFound();
    }
    return false;
  }

  /**
   * Prints the help-menu.
   *
   * @param input The user-input.
   * @return True if the player finished his turn.
   */
  private static boolean handleHelp(String[] input) {
    if (input.length == 1) {
      View.printHelp();
    } else {
      View.noCommandFound();
    }
    return false;
  }

  /**
   * Quits the game.
   *
   * @param input The user-input.
   * @return True if the player finished his turn.
   */
  private static boolean handleQuit(String[] input) {
    if (input.length == 1) {
      quit = true;
      return true;
    } else {
      View.noCommandFound();
      return false;
    }
  }

  /**
   * Extracts the command out of the user-input.
   *
   * @param reader Used to get the user-input.
   * @return The extracted command.
   */
  private static String[] getCommand(BufferedReader reader) {
    boolean validCommand = false;
    String[] input = null;

    while (!validCommand) {
      try {
        input = reader.readLine().split("\\s+");
      } catch (IOException e) {
        View.msgErrorWithPrompt("An io-exception occurred. Try again or "
            + "restart the game.");
        continue;
      }
      validCommand = isValidInput(input);
    }
    return input;
  }

  /**
   * Checks if the input is of a legal pattern.
   *
   * @param input The user-input.
   * @return True if the player finished his turn.
   */
  private static boolean isValidInput(String[] input) {
    if (input[0].isEmpty()) {
      View.msgErrorWithPrompt("Empty input.");
      return false;
    }

    if (input.length > 2) {
      View.msgErrorWithPrompt("Input to long.");
      return false;
    }

    if (!input[0].matches("[A-Za-z]+")) {
      View.msgErrorWithPrompt("Input has to match a command.");
      return false;
    }

    if (input.length == 2
        && !(input[1].matches("[1-" + Board.COLS + "]"))) {
      View.msgErrorWithPrompt("Input has to match a command.");
      return false;
    }
    return true;
  }

  /**
   * Prints the result of a game.
   */
  private static void printResult() {
    Player winner = connect4.getWinner();

    if (winner.equals(Player.HUMAN)) {
      View.msgWon();
    } else if (winner.equals(Player.MACHINE)) {
      View.msgLost();
    } else {
      View.msgTie();
    }
  }
}