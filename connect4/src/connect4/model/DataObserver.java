package connect4.model;

import connect4.view.observerPattern.Observable;

public class DataObserver extends Observable {

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
