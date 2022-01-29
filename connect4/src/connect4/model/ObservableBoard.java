package connect4.model;

import connect4.view.observerPattern.Observable;

public class ObservableBoard extends Observable {

  public ObservableBoard(Board board) {
    this.board = board;
  }

  private Board board;

  public Board getBoard() {
    return board;
  }

  public void setBoard(Board board) {
    setChanged();
    this.board = board;
    notifyObservers();
  }
}
