package connect4.view;

import connect4.model.Board;
import connect4.model.Coordinates2D;
import java.util.Collection;

public class View {

  public static void promt() {
    System.out.println("4inarow> ");
  }

  public static void msgWon() {
    System.out.println("Congratulations! You won.");
  }

  public static void msgLost() {
    System.out.println("Sorry! Machine wins.");
  }

  public static void msgTie() {
    System.out.println("Nobody wins. Tie.");
  }

  public static void printWitness(Collection<Coordinates2D> witness){
    System.out.println(witness);
  }

  public static void printBoard(Board board){
    System.out.println(board);
  }

  public static void printHelp(){
    System.out.println("Help.");
  }

  public static void error() {
    System.out.print("Error! ");
  }

  public static void errorInput() {
    error();
    System.out.print("Wrong input.");
  }
}
