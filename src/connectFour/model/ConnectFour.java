package connectFour.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Contains the logic for a regular game of ConnectFour.
 */
public class ConnectFour implements Board {

  /**
   * The maximal level which can be selected for playing.
   */
  public static final int MAX_LEVEL = 15;

  /**
   * The amount of stones a player has to row up to win.
   */
  public static final int WINNING_ROW_LENGTH = 4;

  /**
   * The board which contains all slots of the game and their owners.
   */
  private Player[][] board;

  /**
   * Contains the current level. The level is an indicator for the difficulty.
   */
  private int level;

  /**
   * The {@code Player} who starts the game.
   */
  private Player firstPlayer;

  /**
   * The {@code Player} who executed the last turn.
   */
  private Player lastPlayer;

  /**
   * Stores the groups of each player. Used in the calculation of the next
   * machineMove and in the calculation of the winner.
   */
  private int[][] groupCounterArray;

  /**
   * Stores the witness of the {@code Player.HUMAN}. A witness is a group of
   * four slots which indicate that the game was won.
   */
  private Collection<Coordinates2D> witnessHuman;

  /**
   * Stores the witness of the {@code Player.MACHINE}. A witness is a group of
   * four slots which indicate that the game was won.
   */
  private Collection<Coordinates2D> witnessMACHINE;

  /**
   * Constructs a new {@code ConnectFour}-instance using the default settings.
   */
  public ConnectFour() {
    this.firstPlayer = Player.HUMAN;
    constructConnectFour();
  }

  /**
   * Constructs a new {@code ConnectFour}-instance with a given {@code
   * firstPlayer}.
   *
   * @param firstPlayer The new {@code firstPlayer} to start the game.
   */
  public ConnectFour(Player firstPlayer) {
    this.firstPlayer = firstPlayer;
    constructConnectFour();
  }

  /**
   * Sorts the collection by converting it to a linked list. Used to sort the
   * witnesses in lexicographical order and copies it to a linked list.
   *
   * @param collection Will be sorted.
   * @return The sorted collection.
   */
  private static Collection<Coordinates2D> sortCollection(
      Collection<Coordinates2D> collection) {
    List<Coordinates2D> list = new LinkedList<>(collection);
    Collections.sort(list);
    return list;
  }

  /**
   * Constructs a game of connectFour. This method is only called by the
   * constructors and used to reduce redundancy.
   */
  private void constructConnectFour() {
    this.level = CONNECT;
    this.lastPlayer = Player.EMPTY;
    this.board = new Player[ROWS][COLS];

    for (Player[] row : board) {
      Arrays.fill(row, Player.EMPTY);
    }

    groupCounterArray = new int[2][3];
    witnessHuman = new LinkedList<>();
    witnessMACHINE = new LinkedList<>();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    StringBuilder boardToString = new StringBuilder();

    for (int i = 0; i < ROWS; i++) {
      for (int j = 0; j < COLS; j++) {
        boardToString.append(board[i][j].toString());

        // Empty space for better positioning between the symbols.
        if (j < COLS - 1) {
          boardToString.append(" ");
        }
      }

      // Add a new line after each row.
      if (i < COLS - 2) {
        boardToString.append("\n");
      }
    }
    return boardToString.toString();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isGameOver() {
    calculateAllGroups();
    return groupCounterArray[0][2] >= 1 || groupCounterArray[1][2] >= 1
        || boardIsFull();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Player getFirstPlayer() {
    return firstPlayer;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Board move(int col) throws IllegalMoveException {

    // The given column has to be within the legal range.
    if (col < 0 || col > COLS - 1) {
      throw new IllegalArgumentException("The given column is not between 1 "
          + "and 7.");
    }

    // The human must not be allowed to place more than one stone at a time.
    if (lastPlayer.equals(Player.HUMAN) || isGameOver()) {
      throw new IllegalMoveException("This move is not allowed. Either the "
          + "game is over or its the turn of the machine.");
    }

    ConnectFour clonedBoard = this.clone();

    // If the column was full, return null.
    if (clonedBoard.isColFull(col)) {
      return null;
    } else {
      boolean stoneCouldBeSet = clonedBoard.dropStone(col, Player.HUMAN);

      // Warn the player if the move was illegal.
      if (!stoneCouldBeSet) {
        throw new IllegalMoveException("The stone couldn't be put in this "
            + "column. Illegal move.");
      }

      lastPlayer = Player.HUMAN;
      return clonedBoard;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Board machineMove() throws IllegalMoveException, InterruptedException {

    // Execution is only allowed if the last move was played by the human.
    if (lastPlayer.equals(Player.MACHINE)) {
      throw new IllegalMoveException("");
    } else {

      // Calculate the best index for the turn using the minimax-algorithm.
      int index = getBestIndex();
      assert !isColFull(index);
      ConnectFour clonedBoard = this.clone();
      clonedBoard.dropStone(index, Player.MACHINE);
      lastPlayer = Player.MACHINE;
      return clonedBoard;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setLevel(int newLevel) {

    // The highest level that will be set is saved in MAX_LEVEL.
    // In the GUI the player can only select levels up to MAX_LEVEL.
    assert newLevel >= 1 && newLevel <= MAX_LEVEL;
    this.level = newLevel;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Player getWinner() {
    Player winner = null;

    //
    getWitness();

    if (isGameOver()) {
      if (witnessHuman.size() >= CONNECT) {
        winner = Player.HUMAN;
      } else if (witnessMACHINE.size() >= CONNECT) {
        winner = Player.MACHINE;
      }
    }
    return winner;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Collection<Coordinates2D> getWitness() {

    // There can only be a witness, if the game is over.
    if (isGameOver()) {

      // The winner is decided by checking if there is a witness.
      if (witnessHuman.size() >= WINNING_ROW_LENGTH) {
        return sortCollection(witnessHuman);
      } else if (witnessMACHINE.size() >= WINNING_ROW_LENGTH) {
        return sortCollection(witnessMACHINE);
      }
    } else {
      throw new IllegalStateException("There is no witness yet.");
    }

    // Return an empty collection if the game is over but there is no winner.
    return new LinkedList<>();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Player getSlot(int row, int col) {
    assert board[row][col] != null;
    return board[row][col];
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ConnectFour clone() {

    // Create a new instance of {@code ConnectFour}.
    ConnectFour clonedConnect = new ConnectFour();

    // Fill this instance by cloning the current gameState.
    Player[][] clonedBoard = new Player[ROWS][COLS];
    for (int i = 0; i < ROWS; i++) {
      clonedBoard[i] = board[i].clone();
    }
    clonedConnect.board = clonedBoard;
    clonedConnect.level = level;
    clonedConnect.firstPlayer = firstPlayer;
    clonedConnect.lastPlayer = lastPlayer;

    return clonedConnect;
  }

  /**
   * Drops a stone into a given column, if the column is not full yet.
   *
   * @param col  The column in which a stone is placed.
   * @param slot The slot-type which should be placed.
   * @return True if the stone could be set
   */
  private boolean dropStone(int col, Player slot) {
    assert !isColFull(col);

    for (int i = 0; i < ROWS; i++) {
      if (board[ROWS - 1 - i][col].equals(Player.EMPTY)) {
        board[ROWS - 1 - i][col] = slot;
        return true;
      }
    }
    return false;
  }

  /**
   * Checks if the {@code board} is full.
   *
   * @return True if the {@code board} is full.
   */
  private boolean boardIsFull() {
    for (int i = 0; i < board[0].length; i++) {
      if (board[0][i].equals(Player.EMPTY)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Calculates all occurring groups in the {@code board}. Possible groups have
   * 2, 3 or 4 slots in a row.
   */
  private void calculateAllGroups() {
    groupCounterArray = new int[2][3];
    generateGroupsOfRows();
    generateGroupsOfCols();
    generateGroupsOfDiagUp();
    generateGroupsOfDiagDown();
  }

  /**
   * Calculates all horizontal groups in the {@code board}.
   */
  private void generateGroupsOfRows() {
    Player current = null;
    Player next;
    int groupCounter = 0;

    for (int i = 0; i < board.length; i++) {
      for (int j = 0; j < board[0].length; j++) {
        next = board[i][j];

        if (current != null) {
          boolean lastIteration = j == board[0].length - 1;
          groupCounter = updateGroups(current, next, groupCounter,
              lastIteration);
        } else if (!next.equals(Player.EMPTY)) {
          groupCounter = 1;
        }

        current = next;
        updateWitnesses(i, j);
      }
      groupCounter = 0;

      clearWitnesses();
    }
  }

  /**
   * Calculates all vertical groups in the {@code board}.
   */
  private void generateGroupsOfCols() {
    Player current = null;
    Player next;
    int groupCounter = 0;

    for (int j = 0; j < board[0].length; j++) {
      for (int i = 0; i < board.length; i++) {
        next = board[i][j];

        if (current != null) {
          boolean lastIteration = i == board.length - 1;
          groupCounter = updateGroups(current, next, groupCounter,
              lastIteration);
        } else if (!next.equals(Player.EMPTY)) {
          groupCounter = 1;
        }
        current = next;
        updateWitnesses(i, j);
      }
      groupCounter = 0;
      clearWitnesses();
    }
  }

  /**
   * Calculates all diagonally ascending groups in the {@code board}.
   */
  private void generateGroupsOfDiagUp() {
    int rowCounter;
    int savedRowCounter;
    int colCounter;
    int savedColCounter;

    Player current = null;
    Player next;
    int groupCounter = 0;

    //Outer Loop
    for (rowCounter = 0; rowCounter < ROWS + COLS - 1;
        rowCounter++) {
      savedRowCounter = rowCounter;

      if (rowCounter >= ROWS) {
        rowCounter = ROWS - 1;
      }
      colCounter = 0;

      if (savedRowCounter >= ROWS) {
        colCounter = savedRowCounter - rowCounter;
      }
      savedColCounter = rowCounter;

      //Inner Loop
      while (savedColCounter >= 0 && colCounter < COLS) {
        next = board[savedColCounter][colCounter];

        if (current != null) {
          boolean lastIteration =
              (savedColCounter == 0) || (colCounter == COLS - 1);
          groupCounter = updateGroups(current, next, groupCounter,
              lastIteration);
        } else if (!next.equals(Player.EMPTY)) {
          groupCounter = 1;
        }
        current = next;
        updateWitnesses(savedColCounter, colCounter);
        colCounter++;
        savedColCounter--;
      }
      current = null;
      groupCounter = 0;

      clearWitnesses();
      rowCounter = savedRowCounter;
    }
  }

  /**
   * Calculates all diagonally descending groups in the {@code board}.
   */
  private void generateGroupsOfDiagDown() {
    Player current = null;
    Player next;
    int groupCounter = 0;
    int diagCounter;
    int colCounter;

    //Outer Loop
    for (diagCounter = 0; diagCounter < (ROWS + COLS - 1);
        diagCounter++) {
      colCounter = COLS - diagCounter - 1;

      if (diagCounter >= COLS) {
        colCounter = 0;
      }

      //Inner Loop
      for (int rowCounter = 0;
          rowCounter < ROWS && colCounter < COLS; rowCounter++) {

        if (diagCounter >= COLS) {
          rowCounter = diagCounter - COLS + 1 + colCounter;
        }
        next = board[rowCounter][colCounter];

        if (current != null) {
          boolean lastIteration =
              (diagCounter == (ROWS + COLS - 1 - 3) - 1) || (
                  rowCounter == ROWS - 1) || (colCounter
                  == COLS - 1);
          groupCounter = updateGroups(current, next, groupCounter,
              lastIteration);
        } else if (!next.equals(Player.EMPTY)) {
          groupCounter = 1;
        }
        current = next;
        updateWitnesses(rowCounter, colCounter);
        colCounter++;
      }
      clearWitnesses();
      current = null;
    }
  }

  /**
   * Resets the witnesses after the groups of a direction where updated without
   * finding a group with at least 4 members.
   */
  private void clearWitnesses() {
    if (witnessHuman.size() < 4 && witnessMACHINE.size() < 4) {
      witnessHuman.clear();
      witnessMACHINE.clear();
    }
  }

  /**
   * Updates {@code groupCounterArray} which contains the number of groups for
   * {@code Player.HUMAN} and {@code Player.MACHINE}. groupCounterArray[0]
   * contains the groups of the human. groupCounterArray[1] contains the groups
   * of the machine. In groupCounterArray[1][0] are groups with two members
   * stored. In groupCounterArray[1][1] are groups with three members stored. In
   * groupCounterArray[1][2] are groups with four members stored.
   *
   * @param current       Contains the current player.
   * @param next          Contains the next player.
   * @param groupCounter  Contains the current
   * @param lastIteration True if the end of a line is reached.
   * @return The new value for the {@code groupCounter}.
   */
  private int updateGroups(Player current, Player next,
      int groupCounter, boolean lastIteration) {

    // Last slot is Empty and next slot Empty.
    if (current.equals(Player.EMPTY) && next.equals(Player.EMPTY)) {
      return 0;

      // Last slot is owned by the human and next slot is owned by the machine.
    } else if (current.equals(Player.EMPTY)) {
      return 1;

      // Last slot is owned by the human and next slot is owned by the human.
    } else if (current.equals(next)) {
      ++groupCounter;
      if (lastIteration && groupCounter >= 2 || groupCounter >= 4) {

        if (current.equals(Player.HUMAN)) {
          groupCounterArray[0][groupCounter - 2]++;
        } else if (current.equals(Player.MACHINE)) {
          groupCounterArray[1][groupCounter - 2]++;
        }
        groupCounter = 0;
      }
      return groupCounter;

      // The last and current slot differ.
    } else {
      if (groupCounter >= 2 && groupCounter <= 4) {

        if (current.equals(Player.HUMAN)) {
          groupCounterArray[0][groupCounter - 2]++;
        } else if (current.equals(Player.MACHINE)) {
          groupCounterArray[1][groupCounter - 2]++;
        }
      }
      return 1;
    }
  }

  /**
   * Used to keep track of the witnesses of the game. Since the indices used in
   * an array differ from the coordinates of a standard coordinate system, the
   * values have to be adjusted.
   *
   * @param row Row of the currently observed element of the {@code board}.
   * @param col Column of the currently observed element of the {@code board}.
   */
  private void updateWitnesses(int row, int col) {
    if (!(witnessHuman.size() >= 4 || witnessMACHINE.size() >= 4)) {

      // Add the coordinates the observed slot belongs to the machine.
      if (board[row][col].equals(Player.MACHINE)) {
        witnessHuman.clear();
        witnessMACHINE.add(
            new Coordinates2D((-1 * (row - (ROWS - 1)) + 1), col + 1));

        // Add the coordinates the observed slot belongs to the human.
      } else if (board[row][col].equals(Player.HUMAN)) {
        witnessHuman.add(
            new Coordinates2D((-1 * (row - (ROWS - 1)) + 1), col + 1));
        witnessMACHINE.clear();

        // Otherwise, clear both witnessLists.
      } else {
        witnessHuman.clear();
        witnessMACHINE.clear();
      }
    }
  }

  /**
   * Calculates the quality of the {@code board} of an element of the
   * game-tree.
   *
   * @param height The height of an element of the game-tree.
   * @return The calculated value using the heuristic specified in the exercise.
   */
  private int evaluate(int height) {

    // Calculating the quality of the occurring groupSizes for both players.
    calculateAllGroups();
    int[] playerGroups = groupCounterArray[0];
    int[] machineGroups = groupCounterArray[1];
    int groupSizes = 50 + machineGroups[0] + 4 * machineGroups[1]
        + 5000 * machineGroups[2] - playerGroups[0] - 4 * playerGroups[1]
        - 500000 * playerGroups[2];

    // Calculating the quality of the positioning of the slot's where  filled.
    int[] playerCols = getSlotsPerCol()[0];
    int[] machineCols = getSlotsPerCol()[1];
    int placements = machineCols[1] + 2 * machineCols[2] + 3 * machineCols[3]
        + 2 * machineCols[4] + machineCols[5] - playerCols[1]
        - 2 * playerCols[2] - 3 * playerCols[3] - 2 * playerCols[4]
        - playerCols[5];

    // Adding the instant win bonus if the machine could win immediately.
    int instaWin;
    if (height == level - 1 && (machineGroups[2] >= 1)) {
      instaWin = 5000000;
    } else {
      instaWin = 0;
    }

    return groupSizes + placements + instaWin;
  }

  /**
   * Calculates the amount of stones of each player in a specified column.
   * slotsPerCol[0] = slots in the specified column of the {@code Player
   * .HUMAN}. slotsPerCol[1] = slots in the specified column of the {@code
   * Player.MACHINE}.
   *
   * @return The number of stones of both players in a given column.
   */
  private int[][] getSlotsPerCol() {
    int[][] slotsPerCol = new int[2][7];

    for (int i = 0; i < board[0].length; i++) {
      for (Player[] players : board) {
        if (players[i].equals(Player.HUMAN)) {
          slotsPerCol[0][i]++;
        } else if (players[i].equals(Player.MACHINE)) {
          slotsPerCol[1][i]++;
        }
      }
    }
    return slotsPerCol;
  }


  /**
   * Calculates the best column for the {@code Player.MACHINE} using a game-tree
   * and the mini-max-algorithm.
   *
   * @return the ideal column.
   */
  private int getBestIndex() throws InterruptedException {
    int currentLevel = level;
    ConnectFour tree = clone();
    return tree.getHighestEval(currentLevel)[1];
  }

  /**
   * Calculates the column in which a stone can be thrown to achieve the best
   * possible {@code board} for the {@code Player.MACHINE}. This method shares
   * redundant parts with getLowestEval are kept separate for better readability
   * and understanding.
   *
   * @param height Represents the level or the depth of the recursion used.
   * @return The calculated score of a {@code board} and the column in which the
   * stone has to be thrown to achieve this calculated value.
   */
  private int[] getHighestEval(int height) throws InterruptedException {
    if (Thread.currentThread().isInterrupted()) {
      throw new InterruptedException("The thread was interrupted.");
    }

    int[] result = new int[2];
    result[0] = Integer.MIN_VALUE;

    for (int i = 0; i < COLS; i++) {
      if (!isColFull(i)) {
        ConnectFour child = clone();
        child.dropStone(i, Player.MACHINE);
        int maxScore = child.evaluate(height);

        if (height > 1) {
          maxScore = maxScore + child.getLowestEval(height - 1)[0];
        }

        if (maxScore > result[0]) {
          result[0] = maxScore;
          result[1] = i;
        }
      }
    }
    return result;
  }

  /**
   * Calculates the column in which a stone can be thrown to achieve the worst
   * possible {@code board} for the {@code Player.MACHINE}. This method shares
   * redundant parts with getHighestEval are kept separate for better
   * readability and understanding.
   *
   * @param height Represents the level or the depth of the recursion used.
   * @return The calculated score of a {@code board} and the column in which the
   * stone has to be thrown to achieve this calculated value.
   */
  private int[] getLowestEval(int height) throws InterruptedException {
    if (Thread.currentThread().isInterrupted()) {
      throw new InterruptedException("The thread was interrupted.");
    }

    int[] result = new int[2];
    result[0] = Integer.MAX_VALUE;

    for (int i = 0; i < COLS; i++) {
      if (!isColFull(i)) {

        ConnectFour child = clone();
        child.dropStone(i, Player.HUMAN);
        int minScore = child.evaluate(height);

        if (height > 1) {
          minScore = minScore + child.getHighestEval(height - 1)[0];
        }

        if (minScore < result[0]) {
          result[0] = minScore;
        }
      }
    }
    return result;
  }

  /**
   * Checks, if a given column is already full.
   *
   * @param col Column to be checked.
   * @return True if the column is already full.
   */
  private boolean isColFull(int col) {
    return !board[0][col].equals(Player.EMPTY);
  }
}