package connect4.model;

import java.util.Collection;

public class Connect4 implements Board {

  Board board;

  public Connect4() {
    this.board = null;
  }

  @Override
  public Player getFirstPlayer() {
    return null;
  }

  public void setFirstPlayer(Player player) {
  }

  @Override
  public Board move(int col) {
    return null;
  }

  @Override
  public Board machineMove() {
    return null;
  }

  @Override
  public void setLevel(int level) {
  }

  @Override
  public boolean isGameOver() {
    return false;
  }

  @Override
  public Player getWinner() {
    return null;
  }

  @Override
  public Collection<Coordinates2D> getWitness() {
    return null;
  }

  @Override
  public Player getSlot(int row, int col) {
    return null;
  }

  @Override
  public Board clone() {
    return null;
  }

  public Board getBoard() {
    return board;
  }
}
