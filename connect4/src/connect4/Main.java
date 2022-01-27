package connect4;

import connect4.view.C4Frame;
import javax.swing.SwingUtilities;

public class Main {
  /**
   * Driver of the program. Reads and handles user-input.
   *
   * @param args Not in use.
   */
  public static void main(String[] args) {
    SwingUtilities.invokeLater((C4Frame::getFrame));
  }
}
