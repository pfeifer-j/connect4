package connectFour.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;

/**
 * Models the window of the connect4 game. This class uses the singleton pattern
 * to ensure that only one game can run at a time.
 */
public final class C4Frame extends JFrame {

  /**
   * The only instance of the {@code C4Frame}.
   */
  private static final C4Frame C4_FRAME = new C4Frame();

  /**
   * The constructor is private to limit the usage.
   */
  private C4Frame() {

    // Set the frame options.
    setTitle("Connect4");
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout());
    setVisible(true);

    // Fill the frame with the gamePanel.
    C4Panel gamePanel = new C4Panel();
    add(gamePanel, BorderLayout.CENTER);

    // Resize window to fit every component and save the resulting size.
    pack();
    setMinimumSize(new Dimension(getSize().width, getSize().height));
  }

  /**
   * Returns the only instance.
   *
   * @return the {@code C4Frame}-instance.
   */
  public static JFrame getFrame() {
    return C4_FRAME;
  }
}
