package connect4.controller;

import connect4.model.Connect4;
import connect4.model.Player;
import connect4.view.View;
import java.io.BufferedReader;
import java.io.IOException;

public class Controller {

  Connect4 connect4;
  boolean quit;

  public Controller() {
    connect4 = new Connect4();
    quit = false;

  }

  public void execute(BufferedReader reader) {
    View.promt();
    while (!quit) {
      if (connect4.getFirstPlayer().equals("Player")) {
        executePlayersTurn(reader);
      } else if (connect4.getFirstPlayer().equals("Computer")) {
        executeComputersTurn();
      }
    }
  }

  public void executePlayersTurn(BufferedReader reader) {
    String[] input;
    try {
      input = reader.readLine().split("\\s+");
    } catch (IOException e) {
      View.error();
      quit = true;
      return;
    }
    if (isValidInput(input)) {
      handleCommand(input);
    }
  }

  private boolean isValidInput(String[] input) {
    return true;
  }

  public void executeComputersTurn() {
  }

  public boolean handleCommand(String[] input) {
    switch (input[0]) {
      case "new", "n", "N" -> {
        connect4 = new Connect4();
      }
      case "level", "l", "L" -> {
        connect4.setLevel(Integer.parseInt(input[1]));
      }
      case "switch", "s", "S" -> {
        connect4 = new Connect4();
        connect4.setFirstPlayer(new Player());
      }
      case "move", "m", "M" -> {
        connect4.move(Integer.parseInt(input[1]));
      }
      case "witness", "w", "W" -> {
        View.printWitness(connect4.getWitness());
      }
      case "print", "p", "P" -> {
        View.printBoard(connect4.getBoard());
      }
      case "help", "h", "H" -> {
        View.printHelp();
      }
      case "quit", "q", "Q" -> {
        quit = true;
      }
      default -> {
        View.error();
        return false;
      }
    }
    return true;
  }
}