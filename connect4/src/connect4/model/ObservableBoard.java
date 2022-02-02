package connect4.model;

import connect4.view.observerPattern.CustomObservable;

/**
 * Converts the board in {@code Connect4} to an observable object. This class is
 * used in the GUI to respond to change.
 */
public class ObservableBoard extends CustomObservable {

  private Board board;

  /**
   * Constructs a new {@code ObservableBoard}
   *
   * @param board which will be observed.
   */
  public ObservableBoard(Board board) {
    this.board = board;
  }

  /**
   * Gets the board.
   *
   * @return the observed board.
   */
  public Board getBoard() {
    return board;
  }

  /**
   * Sets a new board and informs all observers about the change.
   *
   * @param board which will be observed.
   */
  public void setBoard(Board board) {
    setChanged();
    this.board = board;
    notifyObservers();
  }
}
