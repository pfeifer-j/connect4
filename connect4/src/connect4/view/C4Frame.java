package connect4.view;

import javax.swing.JFrame;

public class C4Frame extends JFrame {

  C4Frame() {
    setTitle("Connect Four");
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    add(new C4Panel(this));

    setVisible(true);
    pack();
  }

  public static JFrame getFrame() {
    return new C4Frame();
  }
}
