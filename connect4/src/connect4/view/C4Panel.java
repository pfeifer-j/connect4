package connect4.view;

import connect4.model.Board;
import connect4.model.Connect4;
import connect4.model.DataObserver;
import connect4.model.IllegalMoveException;
import connect4.model.Player;
import connect4.view.observerPattern.Observer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Stack;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class C4Panel extends JPanel {

  private static final int INTERFACE_SIZE = 50;
  private static final Dimension INTERFACE_PANEL_SIZE =
      new Dimension(INTERFACE_SIZE, INTERFACE_SIZE);
  private static final Dimension centerPanelSize = new Dimension(500, 500);
  private static CenterPanel centerPanel;
  private static C4Controller controller;
  private static DataObserver dataObserver = new DataObserver();
  private JFrame c4Frame;

  public C4Panel(JFrame c4Frame) {
    this.c4Frame = c4Frame;

    setPreferredSize(c4Frame.getSize());
    setLayout(new GridBagLayout());
    GridBagConstraints constraints = new GridBagConstraints();

    constraints.fill = GridBagConstraints.HORIZONTAL;
    constraints.gridx = 0;
    constraints.gridy = 0;
    centerPanel = new CenterPanel();
    add(centerPanel, constraints);

    constraints.ipady = 0;
    constraints.gridy = 1;
    add(new ButtonSection(), constraints);
  }

  private void guiLevel(Integer level) {
    // killMachineMove();
    C4Controller.handleLevel(level);
  }

  private void guiNewGame() {
    // killMachineMove();
    C4Controller.handleNew();
    centerPanel = new CenterPanel();
  }

  private void guiSwitch() {
    C4Controller.handleSwitch();
    centerPanel = new CenterPanel();
  }

  private void guiUndo() {
    C4Controller.handleUndo();
  }

  private void guiQuit() {
    for (java.awt.Frame frame : java.awt.Frame.getFrames()) {
      frame.dispose();
    }
  }

  private static class C4Controller {

    private static final Stack<Board> gameStack = new Stack<>();
    private static Board connect4 = new Connect4();
    private static Player firstPlayer = Player.HUMAN;
    private static Player currentPlayer = Player.HUMAN;
    private static int level = Board.CONNECT;

    private C4Controller() {

    }

    private static void handleNew() {
      Player firstPlayer = connect4.getFirstPlayer();
      connect4 = new Connect4(firstPlayer);
      currentPlayer = firstPlayer;
      connect4.setLevel(level);
      if (firstPlayer == Player.MACHINE) {
        machineMove();
      }
      dataObserver.setBoard(connect4.clone());
    }

    private static void handleLevel(int newLevel) {
      level = newLevel;
      connect4.setLevel(level);
      dataObserver.setBoard(connect4.clone());
    }

    private static void handleMove(int column) {
      if (connect4.isGameOver()) {
        //ToDo
        System.out.println(connect4.getWinner());
      } else {
        humanMove(column);
        machineMove();
        dataObserver.setBoard(connect4.clone());
      }
    }

    private static void humanMove(int column) {
      if (currentPlayer == Player.HUMAN && !connect4.isGameOver()) {
        try {
          gameStack.push(connect4.clone());
          connect4 = connect4.move(column);
          dataObserver.setBoard(connect4.clone());
          currentPlayer = Player.MACHINE;
        } catch (IllegalMoveException e) {
          System.out.println("Illegal move");
        }
      }
    }

    private static void machineMove() {
      if (currentPlayer == Player.MACHINE && !connect4.isGameOver()) {
        try {
          connect4 = connect4.machineMove();
          dataObserver.setBoard(connect4);
          currentPlayer = Player.HUMAN;
          System.out.println(dataObserver.getBoard().toString() + "\n" + currentPlayer);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }

    private static void handleSwitch() {
      Player newFirstPlayer = connect4.getFirstPlayer();

      if (newFirstPlayer.equals(Player.HUMAN)) {
        newFirstPlayer = Player.MACHINE;
      } else if (newFirstPlayer.equals(Player.MACHINE)) {
        newFirstPlayer = Player.HUMAN;
      }
      connect4 = new Connect4(newFirstPlayer);
      connect4.setLevel(level);
      currentPlayer = newFirstPlayer;
      if (currentPlayer == Player.MACHINE) {
        machineMove();
      }
      dataObserver.setBoard(connect4.clone());
    }

    private static void handleUndo() {
      if (!gameStack.isEmpty()) {
        connect4 = gameStack.pop();
        dataObserver.setBoard(connect4.clone());
      }
      System.out.println(connect4.toString() + "\n" + currentPlayer);
    }
  }

  private static class C4Button extends JButton {

    C4Button(String name, ActionListener actionListener) {

      setFocusable(false);
      setText(name);
      addActionListener(actionListener);
    }
  }

  private static class C4ComboBox extends JComboBox<Integer> {

    C4ComboBox() {
      for (int i = 1; i <= Connect4.MAX_LEVEL; i++) {
        this.addItem(i);
      }
      setFocusable(false);
      setSelectedItem(Board.CONNECT);
    }
  }

  private final class CenterPanel extends JPanel {

    private GridBagLayout gridBagLayout;
    private GridBagConstraints constraints;

    private CenterPanel() {
      gridBagLayout = new GridBagLayout();
      setLayout(gridBagLayout);
      constraints = new GridBagConstraints();
      constraints.fill = GridBagConstraints.HORIZONTAL;
      setBackground(Color.WHITE);

      for (int i = 0; i <= Board.ROWS; i++) {
        for (int j = 0; j <= Board.COLS; j++) {
          constraints.gridx = j;
          constraints.gridy = i;
          if (i == Board.ROWS && j == 0) {
            JPanel emptyPanel = new JPanel();
            emptyPanel.setBackground(Color.WHITE);
            add(emptyPanel, constraints);
          } else if (j == 0) {
            add(new RowPanel(Board.ROWS - i), constraints);
          } else if (i == Board.ROWS) {
            add(new ColPanel(j), constraints);
          } else{
            add(new SlotPanel(i, j, Player.EMPTY), constraints);
          }
        }
      }
    }
  }

  private final class SlotPanel extends JPanel implements Observer {

    private final int row;
    private final int column;
    private Player player;

    private SlotPanel(int row, int col, Player player) {
      this.row = row;
      this.column = col;
      this.player = player;

      dataObserver.addObserver(this);

      setPreferredSize(INTERFACE_PANEL_SIZE);
      setBackground(Color.BLUE);
      addMouseListener(
          new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
              C4Controller.handleMove(column);
              update();
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

    @Override
    public void paintComponent(Graphics graphics) {
      super.paintComponent(graphics);

      setBackground(Color.BLUE);
      Dimension dimension = getSize();
      Graphics2D g = (Graphics2D) graphics;

      if (player.equals(Player.HUMAN)) {
        g.setColor(Color.YELLOW);
      } else if (player.equals(Player.MACHINE)) {
        g.setColor(Color.RED);
      } else {
        g.setColor(Color.WHITE);
      }

      setAlignmentX(JLabel.CENTER);
      setAlignmentY(JLabel.CENTER);
      g.fillOval(5, 5, dimension.width - 10, dimension.height - 10);
    }

    @Override
    public void update() {
      player = dataObserver.getBoard().getSlot(row,column-1);
      repaint();
    }
  }

  private final class RowPanel extends JPanel {

    private RowPanel(int row) {

      if (row < Board.ROWS) {
        setBorder(BorderFactory.createMatteBorder(3, 0, 0, 0, Color.BLUE));
      }
      // setPreferredSize(new Dimension(20, INTERFACE_SIZE));
      setBackground(Color.WHITE);

      JLabel number = new JLabel();
      number.setText(row + "");

      add(number);
    }
  }

  private final class ColPanel extends JPanel {

    private ColPanel(int col) {
      if (col < Board.COLS) {
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 3, Color.BLUE));
      }
      // setPreferredSize(new Dimension(INTERFACE_SIZE, 20));
      setBackground(Color.WHITE);

      JLabel number = new JLabel();
      number.setText(col + "");

      add(number);
    }
  }

  private final class ButtonSection extends JPanel {

    private ButtonSection() {

      setLayout(new FlowLayout());
      JComboBox<Integer> levelButton = new C4ComboBox();
      levelButton.addActionListener(
          event -> guiLevel((Integer) levelButton.getSelectedItem()));
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
