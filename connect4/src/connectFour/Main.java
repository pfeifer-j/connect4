package connectFour;

import connectFour.view.C4Frame;
import javax.swing.SwingUtilities;

/**
 * Entrypoint of the program, which starts the game.
 */
public class Main {

  /**
   * Prevents the initialisation of the utility-class {@code Main}. Since its
   * asserted that the {@code Shell} isn't instantiated, the constructor throws
   * an AssertionError on usage.
   */
  private Main() {
    throw new AssertionError("Suppress the use of this utility-class");
  }

  /**
   * Driver of the program.
   *
   * @param args not in use.
   */
  public static void main(String[] args) {

    // Used for thread-safety.
    SwingUtilities.invokeLater(C4Frame::getFrame);
  }

}
