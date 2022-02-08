package connectFour.view;

import connectFour.model.Board;
import connectFour.model.ConnectFour;
import connectFour.model.Coordinates2D;
import connectFour.model.IllegalMoveException;
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
   * The background-color of a slot of the ConnectFour-game which was placed by
   * the {@code Player.HUMAN}.
   */
  private static final Color HUMAN_COLOR = Color.YELLOW;

  /**
   * The background-color of a slot of the ConnectFour-game which was placed by
   * the {@code Player.MACHINE}.
   */
  private static final Color MACHINE_COLOR = Color.RED;

  /**
   * The background-color of an empty slot.
   */
  private static final Color EMPTY_COLOR = Color.WHITE;

  /**
   * The background-color of a slot.
   */
  private static final Color HIGHLIGHTED_COLOR = Color.BLACK;

  /**
   * The height of a SouthNumberPanel. The height is by default just enough to fit the
   * standard font of a JLabel.
   */
  private static final int COLUMN_HEIGHT = 15;

  /**
   * The width of a WestNumberPanel. The width is by default just enough to fit
   * the standard font of a JLabel.
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
   * The default color of a border within the grid of the playing-field.
   */
  private static final Color BORDER_COLOR = Color.BLUE;

  /**
   * The background-color of a slot.
   */
  private static final Color SLOT_BACK_GROUND_COLOR = Color.BLUE;

  /**
   * The size-difference in percent between the diameter of the circle in a slot
   * and the slotSize.
   */
  private static final double SLOT_DIAMETER_REDUCTION = 0.95;

  /**
   * The amount in percent which determines the size of the circle which marks a
   * winning slot.
   */
  private static final double MARKER_DIAMETER_REDUCTION = 0.5;

  /**
   * The amount of which the size has to be divided to fit the circle of a slot.
   * By default, the size is halved and therefore {@code SIZE_DIVIDER} is set to
   * two.
   */
  private static final int SLOT_SIZE_DIVIDER = 2;

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
   * Board} since the controlling elements and view-components have to be able
   * to react to change within the board.
   */
  private final ObservedBoard observedBoard = new ObservedBoard(
      new ConnectFour());

  /**
   * Used to present information about the current gameState.
   */
  private final JLabel statusLabel;

  /**
   * Used for enabling the possibility of the UnDo-Button by saving old
   * gameBoard-states on this stack.
   */
  private Stack<Board> gameStack = new Stack<>();

  /**
   * Necessary to keep track which players turn it is. By default, currentPlayer
   * is set to {@code Player.HUMAN}, since the human is the
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
    this.statusLabel = new JLabel("It's your turn!");
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
   * next move, the level-change will be active after the move has be
   * calculated.
   *
   * @param newLevel is the level which will be used after the current
   *                 machineMove.
   */
  private void handleLevel(int newLevel) {
    level = newLevel;

    // Update the level in the observedBoard.
    Board newConnectFour = observedBoard.getBoard();
    newConnectFour.setLevel(newLevel);
    observedBoard.setBoard(newConnectFour.clone());

    // Inform the user about the successful level change.
    statusLabel.setText("The new level is " + level + ".");
  }

  /**
   * Stars a new game with the firstPlayer given as a parameter.
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
    // simplify the handleMove()-method immensely by ensuring a turn-order.
    if (firstPlayer == Player.MACHINE) {
      moveThread = new MoveThread();
      moveThread.start();
    }

    // Since the game has just started (and the machine moved already) the
    // label is updated.
    statusLabel.setText("It's your turn!");
  }

  /**
   * Starts a new game with a switched firstPlayer.
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
   * Executes the move of a player and after the humanMove was executed,
   * calculate the machineMove.
   *
   * @param column which was selected by the player.
   */
  private void handleMove(int column) {

    // Only allow a move to be made, if the game is still running.
    if (observedBoard.getBoard().isGameOver()) {
      statusLabel.setText("The game is over. " + getWinnerText());

      // Warn the player, if the machine is still calculating.
    } else if (moveThread != null && moveThread.isAlive()) {
      statusLabel.setText("The Calculation is still running! ");

      // Otherwise, start executing the players turn.
    } else {
      boolean humanMoveExecuted = humanMove(column);

      // Only if the human successfully moved, execute the machineMove.
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

      // Execute the move, if possible.
      Board newBoard;
      try {
        newBoard = observedBoard.getBoard().move(column);
      } catch (IllegalMoveException e) {
        statusLabel.setText("A illegal move was executed.");
        return false;
      }

      // If the selected column was full newBoard is now null.
      // Check if the selected column was full.
      if (newBoard == null) {
        statusLabel.setText("The selected column is full! ");
        return false;
      } else {
        observedBoard.setBoard(newBoard);
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

    // Only necessary if the game is over.
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
   * better user-experience.
   * Similar to WestNumberPanel but different enough for it to be a separate
   * inner-class.
   */
  private static final class SouthNumberPanel extends JPanel {

    /**
     * Constructs a panel which will be placed on the bottom edge of the board.
     *
     * @param col is the column in which this panel is placed and which is
     *            stored inside the JLabel.
     */
    private SouthNumberPanel(Integer col) {
      setLayout(new BorderLayout());

      // Setting only one visible border to make the column on the board look
      // like a ruler.
      if (col < Board.COLS) {
        setBorder(BorderFactory.createMatteBorder(BORDER_INVISIBLE,
            BORDER_INVISIBLE, BORDER_INVISIBLE, BORDER_VISIBLE, BORDER_COLOR));
      }

      // Setting panelSize and adding the label.
      setPreferredSize(new Dimension(ROW_WIDTH, COLUMN_HEIGHT));
      JLabel number = new JLabel(col.toString());
      number.setHorizontalAlignment(SwingConstants.CENTER);
      number.setVerticalAlignment(SwingConstants.CENTER);
      add(number, BorderLayout.NORTH);
    }
  }

  /**
   * Models a panel which represents the number of a certain row. This panel
   * contains only JLabel in which the number of the row is stored for a better
   * user-experience.
   * Similar to SoutNumberPanel but different enough for it to be a separate
   * inner-class.
   */
  private static final class WestNumberPanel extends JPanel {

    /**
     * Constructs a panel which will be placed on the left edge of the board.
     *
     * @param row which this panel is placed in and the number which is stored
     *            inside the JLabel.
     */
    private WestNumberPanel(Integer row) {
      setLayout(new BorderLayout());

      // Setting only one visible border to make the column on the board look
      // like a ruler.
      if (row < Board.ROWS) {
        setBorder(BorderFactory.createMatteBorder(BORDER_VISIBLE,
            BORDER_INVISIBLE, BORDER_INVISIBLE, BORDER_INVISIBLE,
            BORDER_COLOR));
      }

      // Setting panelSize and adding the label.
      setPreferredSize(new Dimension(ROW_WIDTH, COLUMN_HEIGHT));
            JLabel number = new JLabel(row + " ");
      number.setHorizontalAlignment(SwingConstants.CENTER);
      number.setVerticalAlignment(SwingConstants.CENTER);
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
   * Models the gameBoard and all its components. Those are the slots which
   * represent a stone and the scale for the row and column numbers for better
   * readability for the user.
   */
  private final class CenterPanel extends JPanel {

    /**
     * Construct a new {@code CenterPanel}.
     */
    private CenterPanel() {

      // Set the general layout.
      setLayout(new GridBagLayout());
      GridBagConstraints constraints = new GridBagConstraints();
      constraints.weightx = 1;
      constraints.weighty = 1;

      // Fill the grid.
      for (int i = 0; i <= Board.ROWS; i++) {
        for (int j = 0; j <= Board.COLS; j++) {
          constraints.gridx = j;
          constraints.gridy = i;

          // The emptyPanel is only placed on the south-west slot, which
          // doesn't contain any information.
          if (i == Board.ROWS && j == 0) {
            constraints.weightx = 0;
            constraints.weighty = 0;
            constraints.anchor = GridBagConstraints.CENTER;
            constraints.fill = GridBagConstraints.BOTH;
            add(new JPanel(), constraints); // Adding an empty panel.

            //WestNumberPanel are put on the south-side containing number-
            // indications. The number of the row has to be converted since
            // the WestNumberPanel is placed at the bottom and not the top.
          } else if (j == 0) {
            constraints.weightx = 0;
            constraints.weighty = 0;
            constraints.anchor = GridBagConstraints.EAST;
            constraints.fill = GridBagConstraints.VERTICAL;
            add(new WestNumberPanel(Board.ROWS - i), constraints);

            //SouthNumberPanel are put on the west-side containing
            // number-indications.
          } else if (i == Board.ROWS) {
            constraints.weightx = 0;
            constraints.weighty = 0;
            constraints.anchor = GridBagConstraints.NORTH;
            constraints.fill = GridBagConstraints.HORIZONTAL;
            add(new SouthNumberPanel(j), constraints);

            //All other panels are SlotPanels. The column has to be
            // converted, since there is the column with number-indications
            // on the left side.
          } else {
            constraints.weightx = 1;
            constraints.weighty = 1;
            constraints.fill = GridBagConstraints.BOTH;
            constraints.anchor = GridBagConstraints.CENTER;
            add(new SlotPanel(i,j - 1, Player.EMPTY), constraints);
          }
        }
      }
    }
  }

  /**
   * Represents the slots of a connectFour-game. The player and the machine can
   * move their stones into slotPanels. A slot observes the board and responds
   * on change.
   */
  private final class SlotPanel extends JPanel implements C4Observer {

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
      setAlignmentX(CENTER_ALIGNMENT);
      setAlignmentY(CENTER_ALIGNMENT);
      addMouseListener(
          new MouseListener() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void mouseClicked(MouseEvent e) {

              // Execute the move with the column which was clicked on.
              handleMove(column);
              update();
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void mousePressed(MouseEvent e) {

              // Not in use.
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void mouseReleased(MouseEvent e) {

              // Not in use.
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void mouseEntered(MouseEvent e) {

              // Not in use.
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void mouseExited(MouseEvent e) {

              // Not in use.
            }
          });
    }

    @Override
    public void paintComponent(Graphics graphics) {
      super.paintComponent(graphics);
      Graphics2D g = (Graphics2D) graphics;

      //Clear the background of parent-panel.
      super.paintComponent(g);

      // Selecting the size of the circle.
      int slotDiameter = (int) (getHeight() * SLOT_DIAMETER_REDUCTION);
      int x =
          getWidth() / SLOT_SIZE_DIVIDER - slotDiameter / SLOT_SIZE_DIVIDER;
      int y =
          getHeight() / SLOT_SIZE_DIVIDER - slotDiameter / SLOT_SIZE_DIVIDER;

      // Selecting the color of the circle.
      if (player.equals(Player.HUMAN)) {
        g.setColor(HUMAN_COLOR);
      } else if (player.equals(Player.MACHINE)) {
        g.setColor(MACHINE_COLOR);
      } else {
        highlighted = false;
        g.setColor(EMPTY_COLOR);
      }

      g.fillOval(x, y, slotDiameter, slotDiameter);

      // Only if a slot is highlighted, mark it. Otherwise, ignore it.
      // A slot is marked by drawing a smaller, black circle within the slot.
      if (highlighted) {
        int markerDiameter = (int) (getHeight() * MARKER_DIAMETER_REDUCTION);
        x = getHeight() / SLOT_SIZE_DIVIDER
            - markerDiameter / SLOT_SIZE_DIVIDER;
        y = getHeight() / SLOT_SIZE_DIVIDER
            - markerDiameter / SLOT_SIZE_DIVIDER;
        g.setColor(HIGHLIGHTED_COLOR);
        g.fillOval(x, y, markerDiameter, markerDiameter);
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
   * Models the panel in which all buttons are placed.
   */
  private final class ButtonPanel extends JPanel {

    /**
     * Construct a new {@code ButtonPanel}.
     */
    private ButtonPanel() {
      setLayout(new FlowLayout());

      // Add the levelButton.
      JComboBox<Integer> levelButton = new JComboBox<>();
      for (int i = 1; i <= ConnectFour.MAX_LEVEL; i++) {
        levelButton.addItem(i);
      }
      levelButton.setSelectedItem(Board.CONNECT);
      levelButton.addActionListener(event -> {

        // getSelectedItem() requires a check to avoid a NullPointerException.
        assert levelButton.getSelectedItem() != null;
        handleLevel((Integer) levelButton.getSelectedItem());
      });
      add(levelButton);

      // Add the newGameButton.
      JButton newGameButton = new JButton("New");
      newGameButton.addActionListener(event -> handleNew(null));
      add(newGameButton);

      // Add the switchButton.
      JButton switchButton = new JButton("Switch");
      switchButton.addActionListener(event -> handleSwitch());
      add(switchButton);

      // Add the undoButton.
      JButton undoButton = new JButton("Undo");
      undoButton.addActionListener(event -> handleUndo());
      add(undoButton);

      // Add the quitButton.
      JButton quitButton = new JButton("Quit");
      quitButton.addActionListener(event -> handleQuit());
      add(quitButton);
    }
  }
}
