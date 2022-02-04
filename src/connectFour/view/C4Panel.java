package connectFour.view;

import connectFour.model.Board;
import connectFour.model.ConnectFour;
import connectFour.model.Coordinates2D;
import connectFour.model.IllegalMoveException;
import connectFour.model.ObservedBoard;
import connectFour.model.Player;
import connectFour.view.observerPattern.C4Observer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collection;
import java.util.Stack;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Models the panel in which the game takes place.
 */
public class C4Panel extends JPanel {

  /**
   * The background-color of a slot.
   */
  private static final Color HUMAN_COLOR = Color.YELLOW;
  /**
   * The background-color of a slot.
   */
  private static final Color MACHINE_COLOR = Color.RED;
  /**
   * The background-color of a slot.
   */
  private static final Color EMPTY_COLOR = Color.WHITE;
  /**
   * The background-color of a slot.
   */
  private static final Color HIGHLIGHTED_COLOR = Color.BLACK;
  /**
   * The height of a ColPanel. The height is by default just enough to fit the
   * standard font of a JLabel.
   */
  private static final int COLUMN_HEIGHT = 15;
  /**
   * The width of a RowPanel. The width is by default just enough to fit the
   * standard font of a JLabel.
   */
  private static final int ROW_WIDTH = 15;

  /**
   * The size of the right/east border.
   */
  private static final int BORDER_VISIBLE = 3;

  /**
   * The size of all borders except the right/east one.
   */
  private static final int BORDER_INVISIBLE = 0;

  /**
   * The default color of a border.
   */
  private static final Color BORDER_COLOR = Color.BLUE;
  /**
   * The background-color of a slot.
   */
  private static final Color SLOT_BACK_GROUND_COLOR = Color.BLUE;
  /**
   * The amount of size-difference between the diameter of the circle and the
   * slotSize.
   */
  private static final int DIAMETER_REDUCTION = 10;

  /**
   * The amount of which the size is divided to better fit the circle of a slot.
   * By default, the size is halved and therefore {@code SIZE_DIVIDER} is set to
   * two.
   */
  private static final int SIZE_DIVIDER = 2;
  /**
   * Preferred size of the gameSlots. Used for height and width.
   */
  private static final int SLOT_SIZE = 50;
  /**
   * Preferred dimension of the gameSlots.
   */
  private static final Dimension SLOT_PANEL_SIZE = new Dimension(SLOT_SIZE,
      SLOT_SIZE);
  /**
   * Converter for transferring the information about the state of the {@code
   * Board} since the controller and view have to be able to react to change
   * within the board.
   */
  private final ObservedBoard observedBoard = new ObservedBoard(
      new ConnectFour());
  /**
   * Used to present information about the current gameState.
   */
  private final JLabel statusLabel = new JLabel("It's your turn!");
  /**
   * Used for enabling the possibility of the UnDo-Button by saving old
   * gameBoard-states on this stack.
   */
  private Stack<Board> gameStack = new Stack<>();

  /**
   * Necessary to keep track which players turn it is. By default, currentPlayer
   * is set to {@code connectFour.model.Player.HUMAN}, since the human is the
   * default-firstPlayer.
   */
  private Player currentPlayer = Player.HUMAN;

  /**
   * Used for easier management of the difficulty setting of the game. The
   * default-value is defined in Board.CONNECT.
   */
  private int level = Board.CONNECT;

  /**
   * Enables the usage of multiple threads while calculation the next
   * machineMove which gives the player the possibility to interact with the GUI
   * while a move is being calculated.
   */
  private MoveThread moveThread;

  /**
   * Constructs a new {@code C4Panel}.
   */
  public C4Panel() {
    setLayout(new BorderLayout());

    // Adding the topPanel which contains the JLabel to inform the user about
    // the current state of the game.
    JPanel topPanel = new JPanel();
    topPanel.add(statusLabel);
    add(topPanel, BorderLayout.NORTH);

    // Adding the centerPanel which contains the board.
    CenterPanel centerPanel = new CenterPanel();
    add(centerPanel, BorderLayout.CENTER);

    // Adding the buttonPanel which contains the buttons.
    add(new ButtonPanel(), BorderLayout.SOUTH);
  }


  /**
   * Sets the new selected level. If the machine is currently calculating the
   * next move, the level
   *
   * @param newLevel is the level which will be used after the current
   *                 machineMove.
   */
  private void handleLevel(int newLevel) {
    level = newLevel;

    // Update the level in the boardObserver.
    Board newConnectFour = observedBoard.getBoard();
    newConnectFour.setLevel(newLevel);
    observedBoard.setBoard(newConnectFour.clone());

    // Inform the user about the successful level change.
    statusLabel.setText("The new level is " + level + ".");
  }

  /**
   * Stars a new game with the firstPlayer given as a parameter. This method is
   * executed after the "New"-button was clicked.
   *
   * @param firstPlayer of the new game. If firstPlayer is {@code null} use the
   *                    firstPlayer of the last game instead. Used to simplify
   *                    the handleSwitch()-method.
   */
  private void handleNew(Player firstPlayer) {

    // Necessary for a responsive user-experience.
    interruptMachineMove();

    // If a new game has been started the gameStack is cleared.
    // Thus, the player can't go back to an old came by using UnDo's
    gameStack = new Stack<>();

    // Start a new game. If there is no firstPlayer given as a parameter,
    // use the firstPlayer of the old game.
    if (firstPlayer == null) {
      firstPlayer = observedBoard.getBoard().getFirstPlayer();
    }
    observedBoard.setBoard(new ConnectFour(firstPlayer));
    currentPlayer = firstPlayer;
    observedBoard.getBoard().setLevel(level);

    // If the machine has the first move, this move is executed now to
    // simplify the handleMove()-method immensely.
    if (firstPlayer == Player.MACHINE) {
      moveThread = new MoveThread();
      moveThread.start();
    }

    // Since the game has just started (and the machine moved already) the
    // label is updated.
    statusLabel.setText("It's your turn!");
  }

  /**
   * Starts a new game while the firstPlayer is switched.
   */
  private void handleSwitch() {
    Player newFirstPlayer = observedBoard.getBoard().getFirstPlayer()
        .opposite();
    handleNew(newFirstPlayer);
  }

  /**
   * Resets the last move the player and the machine made. This is done by
   * saving every old game before the player made his move on the {@code
   * gameStack}.
   */
  private void handleUndo() {

    if (gameStack.isEmpty()) {
      statusLabel.setText("There is nothing to undo! It's your "
          + "turn again!");
    } else {

      // Necessary for a responsive user-experience.
      interruptMachineMove();

      observedBoard.setBoard(gameStack.pop());
      statusLabel.setText("Your move was undone. It's your turn!");
    }
  }

  /**
   * Quits the game.
   */
  private void handleQuit() {

    // Necessary for a responsive user-experience.
    interruptMachineMove();

    statusLabel.setText("Quitting...");
    for (java.awt.Frame frame : java.awt.Frame.getFrames()) {
      frame.dispose();
    }
  }

  /**
   * Executes the move of a player and after the humanMove is executed, it
   * executes the machineMove.
   *
   * @param column which was selected by the player.
   */
  private void handleMove(int column) {

    // Only allow a move to be made, if the game is still running.
    if (observedBoard.getBoard().isGameOver()) {
      statusLabel.setText("The game is over." + getWinnerText());

      // Warn the player, if the machine is still calculating.
    } else if (moveThread != null && moveThread.isAlive()) {
      statusLabel.setText("The Calculation is still running! ");

      // Otherwise, start executing the players turn.
    } else {
      boolean humanMoveExecuted = humanMove(column);

      // Only if the human successfully move, execute the machineMove.
      if (humanMoveExecuted) {
        moveThread = new MoveThread();
        moveThread.start();
      } else {
        String currentText = statusLabel.getText();
        statusLabel.setText(currentText + "Try again!");
      }
    }
  }


  /**
   * Executes the move of the player.
   *
   * @param column which was selected by the player.
   * @return true if the move was executed successfully.
   */
  private boolean humanMove(int column) {

    // Only execute a move, if it's the humans turn and if the game is still
    // running.
    if (currentPlayer != Player.HUMAN && observedBoard.getBoard()
        .isGameOver()) {
      return false;
    } else {

      // Update the gameStack before the move was made by pushing a cloned
      // version of the current game onto the stack.
      gameStack.push(observedBoard.getBoard().clone());

      Board connectFour;
      try {
        connectFour = observedBoard.getBoard().move(column);
      } catch (IllegalMoveException e) {
        statusLabel.setText("A illegal move was executed.");
        return false;
      }

      // If the selected column was full connectFour is now null.
      // Check if the selected column was full.
      if (connectFour == null) {
        statusLabel.setText("The selected column is full! ");
        return false;
      } else {
        observedBoard.setBoard(connectFour);
        currentPlayer = Player.MACHINE;
        return true;
      }
    }
  }

  /**
   * Returns a message containing information about the winner.
   *
   * @return a message about the winning player or if there was a tie.
   */
  private String getWinnerText() {
    if (observedBoard.getBoard().isGameOver()) {
      Player winner = observedBoard.getBoard().getWinner();

      if (winner == null) {
        return "The game was a tie.";
      } else {
        return "The " + winner.name().toLowerCase() + " has won.";
      }
    } else {
      return "The game is still running!";
    }
  }

  /**
   * Interrupts the calculation of the machineMove, since the calculation can
   * take a while on higher levels and maybe the user doesn't want to wait.
   */
  private void interruptMachineMove() {
    if (moveThread != null && moveThread.isAlive()) {
      moveThread.interrupt();
    }
  }

  /**
   * Models a panel which represents the number of a certain column. This panel
   * contains only JLabel in which the number of the column is stored for a
   * better user-experience. Since this class is only created to remove
   * redundancy and doesn't implement new functionality it is static.
   */
  private static final class ColPanel extends JPanel {

    /**
     * Constructs a panel which will be placed on the bottom edge of the board.
     *
     * @param col is the column in which this panel is placed and which is
     *            stored inside the JLabel.
     */
    private ColPanel(Integer col) {
      setLayout(new BorderLayout());

      // Setting only one visible border to make the column on the board look
      // like a ruler.
      if (col < Board.COLS) {
        setBorder(BorderFactory.createMatteBorder(BORDER_INVISIBLE,
            BORDER_INVISIBLE, BORDER_INVISIBLE, BORDER_VISIBLE, BORDER_COLOR));
      }

      JLabel number = new JLabel(col.toString());
      number.setHorizontalAlignment(SwingConstants.CENTER);

      // Setting panelSize
      setPreferredSize(new Dimension(SLOT_SIZE, COLUMN_HEIGHT));

      add(number, BorderLayout.NORTH);
    }
  }

  /**
   * Models a panel which represents the number of a certain row. This panel
   * contains only JLabel in which the number of the row is stored for a better
   * user-experience. Since this class is only created to remove redundancy and
   * doesn't implement new functionality it is static.
   */
  private static final class RowPanel extends JPanel {

    /**
     * Constructs a panel which will be placed on the left edge of the board.
     *
     * @param row which this panel is placed in and the number which is stored
     *            inside the JLabel.
     */
    private RowPanel(Integer row) {
      setLayout(new BorderLayout());

      // Setting only one visible border to make the column on the board look
      // like a ruler.
      if (row < Board.ROWS) {
        setBorder(BorderFactory.createMatteBorder(BORDER_VISIBLE,
            BORDER_INVISIBLE, BORDER_INVISIBLE, BORDER_INVISIBLE,
            BORDER_COLOR));
      }

      // Adding one space character for slightly adjusting the positioning.
      JLabel number = new JLabel(row + " ");
      number.setHorizontalAlignment(SwingConstants.LEFT);

      // Setting panelSize
      setPreferredSize(new Dimension(ROW_WIDTH, SLOT_SIZE));

      add(number, BorderLayout.EAST);
    }
  }

  /**
   * Models the class of Thread that executes a machine move. Used to execute
   * the machineMove in a different thread for a better user-experience.
   */
  private class MoveThread extends Thread {

    /**
     * Executes the move of the machine in a different thread.
     */
    @Override
    public void run() {

      // Update the clonedBoard and check if the game is over.
      Board clonedBoard = observedBoard.getBoard().clone();
      if (clonedBoard.isGameOver()) {
        statusLabel.setText(getWinnerText());
      } else {

        // If the game is still running, execute the move.
        try {
          statusLabel.setText("The Calculation is running! ");
          clonedBoard = clonedBoard.machineMove();

          // Since it's possible that the user updated the level during
          // calculation, reset the level and update the board.
          // This step can be unnecessary if the level wasn't changed during
          // execution but using a flag which is set if the level was changed
          // and an if-statement, to set the updated level is less efficient
          // than just resetting the level everytime.
          clonedBoard.setLevel(level);

          observedBoard.setBoard(clonedBoard.clone());
          currentPlayer = Player.HUMAN;

          // Inform the user about the gameState.
          if (clonedBoard.isGameOver()) {
            statusLabel.setText(getWinnerText());
          } else {
            statusLabel.setText("It's your turn!");
          }

          // And if the machineMove was interrupted, inform the user as well.
        } catch (InterruptedException e) {
          statusLabel.setText("Calculation was interrupted. It's "
              + "your turn now.");
        }
      }
    }
  }

  /**
   * Represents the slots of a connectFour-game. The player and the machine can
   * move their stones into slotPanels. A slot observes the board and responds
   * on change.
   */
  private final class SlotPanel extends JPanel implements
      C4Observer {

    /**
     * Row of the slotPanel in the gridBagLayout.
     */
    private final int row;
    /**
     * Column of the slotPanel in the gridBagLayout.
     */
    private final int column;
    /**
     * The player who put a stone into this panel.
     */
    private Player player;
    /**
     * True, if this slot is part of a witness. This means it is one of the
     * winning slots, which are highlighted after a game is won.
     */
    private boolean highlighted;

    /**
     * Construct a new {@code SlotPanel}.
     *
     * @param slotRow    of the slot in the grid.
     * @param slotCol    of the slot in the grid.
     * @param slotPlayer who put a stone into the slot.
     */
    private SlotPanel(int slotRow, int slotCol, Player slotPlayer) {
      this.row = slotRow;
      this.column = slotCol;
      this.player = slotPlayer;

      // The slot observes the board and therefore has to be added as an
      // observer to observableBoard.
      observedBoard.addObserver(this);

      // Setting the panelProperties.
      setPreferredSize(SLOT_PANEL_SIZE);
      setBackground(SLOT_BACK_GROUND_COLOR);
      addMouseListener(
          new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

              // Execute the move with the column which was clicked on.
              handleMove(column);
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
      Graphics2D g = (Graphics2D) graphics;

      // Selecting the size of the circle.
      Dimension dimension = getSize();
      int diameter = dimension.height - DIAMETER_REDUCTION;
      int x = getSize().width / SIZE_DIVIDER - diameter / SIZE_DIVIDER;
      int y = getSize().height / SIZE_DIVIDER - diameter / SIZE_DIVIDER;

      // Selecting the color of the circle.
      if (player.equals(Player.HUMAN)) {
        g.setColor(HUMAN_COLOR);
      } else if (player.equals(Player.MACHINE)) {
        g.setColor(MACHINE_COLOR);
      } else {
        highlighted = false;
        g.setColor(EMPTY_COLOR);
      }

      g.fillOval(x, y, diameter, diameter);

      // Only if a slot is highlighted, mark it. Otherwise, ignore it.
      if (highlighted) {
        diameter = (dimension.height - 10) / 2;
        x = getSize().width / 2 - diameter / 2;
        y = getSize().height / 2 - diameter / 2;
        g.setColor(HIGHLIGHTED_COLOR);
        g.fillOval(x, y, diameter, diameter);
      }
    }

    /**
     * Update all slots which are part of the winning set. This means the slots
     * coordinates are contained in the witness.
     */
    private void updateMarkedSlot() {

      // The method getWitness() expects getWinner() to be called first.
      assert (observedBoard.getBoard().getWinner() == null);

      highlighted = false;

      // Calculate the coordinates of a slot.
      // The rows and columns have to be converted, since the used
      // gridBagLayout has an additional column and the rows are counted from
      // bottom to top.
      int convertedRow = Board.ROWS - row;
      int convertedCol = column + 1;
      Coordinates2D current = new Coordinates2D(convertedRow, convertedCol);
      Collection<Coordinates2D> witnesses =
          observedBoard.getBoard().getWitness();

      // Check if the slot is part of the witness.
      witnesses.forEach(witness -> {
        if (witness.compareTo(current) == 0) {
          highlighted = true;
        }
      });
    }

    /**
     * Called after a slot was clicked and updates the GUI accordingly.
     */
    @Override
    public void update() {
      player = observedBoard.getBoard().getSlot(row, column);

      // All slots have to be unmarked while the game is still running.
      highlighted = false;

      // Only if the game is won by a player, mark all winning slots.
      // Otherwise, do nothing.
      if (observedBoard.getBoard().isGameOver()
          && observedBoard.getBoard().getWinner() != null) {
        updateMarkedSlot();

        // Inform user about the winner.
        statusLabel.setText(getWinnerText());
      }

      repaint();
    }
  }

  /**
   * Models the gameBoard and all its components like the slots which represent
   * a stone and the scale for row and column numbers.
   */
  private final class CenterPanel extends JPanel {

    /**
     * Construct a new {@code CenterPanel}.
     */
    private CenterPanel() {

      // Set the general layout.
      GridBagLayout gridBagLayout = new GridBagLayout();
      setLayout(gridBagLayout);
      GridBagConstraints constraints = new GridBagConstraints();

      // Fill the grid.
      for (int i = 0; i <= Board.ROWS; i++) {
        for (int j = 0; j <= Board.COLS; j++) {
          constraints.gridx = j;
          constraints.gridy = i;

          // The emptyPanel is only placed on the south-west slot, which
          // doesn't contain any information.
          if (i == Board.ROWS && j == 0) {
            JPanel emptyPanel = new JPanel();
            add(emptyPanel, constraints);

            //RowPanels are put on the south-side containing number-indications.
            // The number of the row has to be converted since the rowPanel
            // is placed at the bottom and not the top.
          } else if (j == 0) {
            add(new RowPanel(Board.ROWS - i), constraints);

            //ColPanels are put on the west-side containing number-indications.
          } else if (i == Board.ROWS) {
            add(new ColPanel(j), constraints);

            //All other panels are SlotPanels. The column has to be
            // converted, since there is the column with number-indications
            // on the left side.
          } else {
            add(new SlotPanel(i, j - 1, Player.EMPTY), constraints);
          }
        }
      }
    }
  }

  /**
   * Models the panel in which all buttons are placed.
   */
  private final class ButtonPanel extends JPanel {

    /**
     * Construct a new {@code ButtonPanel}.
     */
    private ButtonPanel() {
      setLayout(new FlowLayout());

      // Add levelButton.
      JComboBox<Integer> levelButton = new JComboBox<>();
      for (int i = 1; i <= ConnectFour.MAX_LEVEL; i++) {
        levelButton.addItem(i);
      }
      levelButton.setSelectedItem(Board.CONNECT);
      levelButton.addActionListener(event -> {

        // getSelectedItem() requires a check to avoid a NullPointerException.
        // Without the assertion IntelliJ shows a warning.
        assert levelButton.getSelectedItem() != null;
        handleLevel((Integer) levelButton.getSelectedItem());
      });
      add(levelButton);

      // Add newGameButton.
      JButton newGameButton = new JButton("New");
      newGameButton.addActionListener(event -> handleNew(null));
      add(newGameButton);

      // Add switchButton.
      JButton switchButton = new JButton("Switch");
      switchButton.addActionListener(event -> handleSwitch());
      add(switchButton);

      // Add undoButton.
      JButton undoButton = new JButton("Undo");
      undoButton.addActionListener(event -> handleUndo());
      add(undoButton);

      // Add quitButton.
      JButton quitButton = new JButton("Quit");
      quitButton.addActionListener(event -> handleQuit());
      add(quitButton);
    }
  }
}
