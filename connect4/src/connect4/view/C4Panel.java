package connect4.view;

import connect4.model.Board;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class C4Panel extends JPanel {


  private static final int INTERFACE_SIZE = 100;

  private static final Dimension INTERFACE_PANEL_SIZE = new Dimension(
      INTERFACE_SIZE, INTERFACE_SIZE);

  JFrame c4Frame;

  public C4Panel(JFrame c4Frame) {
    int size = 0;
    setBorder(BorderFactory.createEmptyBorder(size, size, size, size));

    setLayout(new GridBagLayout());
    GridBagConstraints constraints = new GridBagConstraints();

    constraints.fill = GridBagConstraints.HORIZONTAL;
    constraints.gridx = 0;
    constraints.gridy = 0;
    constraints.ipady = 150;
    add(new CenterPanel(), constraints);

    constraints.ipady = 0;
    constraints.gridy = 1;
    add(new ButtonSection(), constraints);
    //add(new BottomSection(), BorderLayout.SOUTH);
  }

  private void guiNewGame() {
  }

  private void guiSwitch() {
  }

  private void guiUndo() {
  }

  private void guiQuit() {
  }

  private static class C4Button extends JButton {

    C4Button(String name, ActionListener actionListener) {

      setFocusable(false);
      setText(name);
      addActionListener(actionListener);
    }
  }

  private static final class BoardSection extends JPanel {

    private BoardSection() {
    }
  }

  private final class CenterPanel extends JPanel {


    private CenterPanel() {

      //setSize(300,300);

      setLayout(new GridLayout(Board.ROWS + 1, Board.COLS + 1));
      setBackground(Color.BLUE);

      for (int i = 0; i <= Board.ROWS; i++) {
        for (int j = 0; j <= Board.COLS; j++) {
          if (i == Board.ROWS && j == 0) {
            add(new JPanel());
          } else if (j == 0) {
            add(new RowPanel(Board.ROWS - i + ""));
          } else if (i == Board.ROWS) {
            add(new ColPanel(j + ""));
          } else {
            add(new SlotPanel());
          }
        }
      }
    }
  }

  private final class SlotPanel extends JPanel {

    private SlotPanel() {
      setPreferredSize(INTERFACE_PANEL_SIZE);
      addMouseListener(new MouseListener() {
        @Override
        public void mouseClicked(MouseEvent e) {
          //slotClicked(SlotPanel.this);
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
      });

    }

    /**
     * Paints the panel according to the game state.
     *
     * @param graphics Graphics used to paint.
     */
    @Override
    public void paintComponent(Graphics graphics) {
      super.paintComponent(graphics);

      setBackground(Color.BLUE);

      Dimension dimension = getSize();

      Graphics2D g = (Graphics2D) graphics;
      g.setColor(Color.WHITE);
      g.fillOval(0, 0, dimension.width - 5, dimension.height -5);
    }
  }

  private final class RowPanel extends JPanel {

    private RowPanel(String row) {
      setSize(5, 10);
      JLabel number = new JLabel();
      number.setText(row);
      add(number);
    }
  }

  private final class ColPanel extends JPanel {

    private ColPanel(String col) {
      setSize(10, 5);
      JLabel number = new JLabel();
      number.setText(col + "");
      add(number);
    }
  }


  private final class BottomSection extends JPanel {

    private BottomSection() {
      setLayout(new BorderLayout());
      add(new ButtonSection());
    }
  }

  private final class ButtonSection extends JPanel {

    private ButtonSection() {

      setLayout(new FlowLayout());

      JComboBox<Integer> levelButton = new JComboBox<>(
          new Integer[]{1, 2, 3, 4, 5});
      add(levelButton);

      JButton newGameButton = new C4Button("New", event -> guiNewGame());
      add(newGameButton);

      JButton switchButton = new C4Button("Switch", event -> guiSwitch());
      add(switchButton);

      JButton undoButton = new C4Button("Undo", event -> guiUndo());
      add(undoButton);

      JButton quitButton = new C4Button("Quit", event -> guiQuit());
      add(quitButton);
    }
  }
}
