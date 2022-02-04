package connectFour.model;

/**
 * Custom exception used to indicate the use of an illegal move during the game
 * of {@code Connect4}. Occurs e.g. if a move is executed on a full column. The
 * {@code IllegalMoveException} extends {@code RuntimeException} to ensure that
 * the exception can be thrown during the normal operation of the Java Virtual
 * Machine.
 */
public class IllegalMoveException extends RuntimeException {

  /**
   * Constructs a new exception using the super-constructor of {@code
   * RuntimeException} using a message.
   *
   * @param msg The message used to describe the occurred problem.
   */
  public IllegalMoveException(String msg) {
    super(msg);
  }
}