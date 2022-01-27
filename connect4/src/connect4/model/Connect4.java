package connect4.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * The class {@code Connect4} contains the logic for a regular game of
 * Connect4.
 */
public class Connect4 implements Board {

  /**
   * The maximal level which can be selected for playing.
   */
  public static final int MAX_LEVEL = 5;

  /**
   * The minimal level which can be selected for playing.
   */
  public static final int MIN_LEVEL = 1;

  private Player[][] board;
  private int level;
  private Player firstPlayer;
  private Player lastStonePlaced;
  private int[][] groupCounterArray;
  private final Collection<Coordinates2D> witnessPlayer;
  private final Collection<Coordinates2D> witnessComputer;

  /**
   * Constructs a new {@code Connect4}-instance using the default settings.
   */
  public Connect4() {
    this.firstPlayer = Player.HUMAN;
    this.level = Board.CONNECT;
    this.lastStonePlaced = Player.EMPTY;
    this.board = new Player[Board.ROWS][Board.COLS];

    for (Player[] row : board) {
      Arrays.fill(row, Player.EMPTY);
    }

    groupCounterArray = new int[2][3];
    witnessPlayer = new LinkedList<>();
    witnessComputer = new LinkedList<>();
  }

  /**
   * Constructs a new {@code Connect4}-instance with a given {@code
   * firstPlayer}.
   *
   * @param firstPlayer The new {@code firstPlayer} to start the game.
   */
  public Connect4(Player firstPlayer) {
    this.firstPlayer = firstPlayer;
    this.level = Board.CONNECT;
    this.lastStonePlaced = Player.EMPTY;
    this.board = new Player[Board.ROWS][Board.COLS];

    for (Player[] row : board) {
      Arrays.fill(row, Player.EMPTY);
    }

    groupCounterArray = new int[2][3];
    witnessPlayer = new LinkedList<>();
    witnessComputer = new LinkedList<>();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {

    StringBuilder boardToString = new StringBuilder();

    for (int i = 0; i < Board.ROWS; i++) {
      for (int j = 0; j < Board.COLS; j++) {
        boardToString.append(board[i][j].getSymbol());

        if (j < Board.COLS - 1) {
          boardToString.append(" ");
        }
      }

      if (i < Board.COLS - 2) {
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
    if (boardIsFull()) {
      return true;
    }

    calculateAllGroups();
    return groupCounterArray[0][2] >= 1 || groupCounterArray[1][2] >= 1;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Player getFirstPlayer() {
    return firstPlayer;
  }

  private void setFirstPlayer(Player firstPlayer) {
    this.firstPlayer = firstPlayer;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Board move(int col) throws IllegalMoveException {

    if (col < 1 || col > 7) {
      throw new IllegalArgumentException();
    }
    col = col - 1;

    if (lastStonePlaced.equals(Player.HUMAN) || isGameOver()) {
      throw new IllegalMoveException("");
    }

    Connect4 clonedBoard = (Connect4) this.clone();
    boolean stoneCouldBeSet = clonedBoard.dropStone(col, Player.HUMAN);

    if (!stoneCouldBeSet) {
      throw new IllegalMoveException("");
    }

    lastStonePlaced = Player.HUMAN;
    return clonedBoard;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Board machineMove() throws IllegalMoveException {

    int index = getBestIndex();

    if (lastStonePlaced.equals(Player.MACHINE) || isColFull(index)) {
      throw new IllegalMoveException("");
    } else {
      Connect4 clonedBoard = (Connect4) this.clone();
      clonedBoard.dropStone(index, Player.MACHINE);
      lastStonePlaced = Player.MACHINE;
      return clonedBoard;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setLevel(int newLevel) {

    if (newLevel >= 5) {
      this.level = MAX_LEVEL;
    } else if (newLevel <= 1) {
      this.level = MIN_LEVEL;
    } else {
      this.level = newLevel;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Player getWinner() {
    Player winner = null;
    getWitness();

    if (isGameOver()) {
      if (witnessPlayer.size() >= Board.CONNECT) {
        winner = Player.HUMAN;
      } else if (witnessComputer.size() >= Board.CONNECT) {
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
    if (isGameOver()) {

      if (witnessPlayer.size() >= 4) {
        return sortCollection(witnessPlayer);
      }

      if (witnessComputer.size() >= 4) {
        return sortCollection(witnessComputer);
      }
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Player getSlot(int row, int col) {
    String value = board[row][col].name();

    if (value.equals(Player.EMPTY.name())) {
      return null;
    }
    return Player.valueOf(value);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Board clone() {
    Connect4 clonedConnect = new Connect4();

    Player[][] clonedBoard = new Player[Board.ROWS][Board.COLS];

    for (int i = 0; i < Board.ROWS; i++) {
      for (int j = 0; j < Board.COLS; j++) {
        clonedBoard[i][j] = Player.valueOf(board[i][j].toString());
      }
    }

    clonedConnect.setBoard(clonedBoard);
    clonedConnect.setFirstPlayer(firstPlayer);
    clonedConnect.setLastStonePlaced(lastStonePlaced);
    clonedConnect.setLevel(level);

    return clonedConnect;
  }

  /**
   * Sorts the collection by converting it to a linked list. Used to sort the
   * witnesses in lexicographical order.
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
   * Sets the board.
   *
   * @param board To be set.
   */
  private void setBoard(Player[][] board) {
    this.board = board;
  }

  /**
   * Sets the last Stone which was placed.
   *
   * @param lastStonePlaced Which was placed.
   */
  private void setLastStonePlaced(Player lastStonePlaced) {
    this.lastStonePlaced = lastStonePlaced;
  }

  /**
   * Drops a stone into a given column. This methods
   *
   * @param col  The column in which a stone is placed.
   * @param slot The slot-type which should be placed.
   * @return True if the stone could be set
   */
  private boolean dropStone(int col, Player slot) {
    assert !isColFull(col);

    for (int i = 0; i < Board.ROWS; i++) {
      if (board[Board.ROWS - 1 - i][col].equals(Player.EMPTY)) {
        board[Board.ROWS - 1 - i][col] = slot;
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
   * Calculates all occurring groups in the {@code board}.
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

      if (witnessPlayer.size() < 4 && witnessComputer.size() < 4) {
        witnessPlayer.clear();
        witnessComputer.clear();
      }
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

      if (witnessPlayer.size() < 4 && witnessComputer.size() < 4) {
        witnessPlayer.clear();
        witnessComputer.clear();
      }
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
    for (rowCounter = 0; rowCounter < Board.ROWS + Board.COLS - 1;
        rowCounter++) {
      savedRowCounter = rowCounter;

      if (rowCounter >= Board.ROWS) {
        rowCounter = Board.ROWS - 1;
      }
      colCounter = 0;

      if (savedRowCounter >= Board.ROWS) {
        colCounter = savedRowCounter - rowCounter;
      }
      savedColCounter = rowCounter;

      //Inner Loop
      while (savedColCounter >= 0 && colCounter < Board.COLS) {
        next = board[savedColCounter][colCounter];

        if (current != null) {
          boolean lastIteration =
              (savedColCounter == 0) || (colCounter == Board.COLS - 1);
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

      if (witnessPlayer.size() < 4 && witnessComputer.size() < 4) {
        witnessPlayer.clear();
        witnessComputer.clear();
      }
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
    for (diagCounter = 0; diagCounter < (Board.ROWS + Board.COLS - 1);
        diagCounter++) {
      colCounter = Board.COLS - diagCounter - 1;

      if (diagCounter >= Board.COLS) {
        colCounter = 0;
      }

      //Inner Loop
      for (int rowCounter = 0;
          rowCounter < Board.ROWS && colCounter < Board.COLS; rowCounter++) {

        if (diagCounter >= Board.COLS) {
          rowCounter = diagCounter - Board.COLS + 1 + colCounter;
        }
        next = board[rowCounter][colCounter];

        if (current != null) {
          boolean lastIteration =
              (diagCounter == (Board.ROWS + Board.COLS - 1 - 3) - 1) || (
                  rowCounter == Board.ROWS - 1) || (colCounter
                  == Board.COLS - 1);
          groupCounter = updateGroups(current, next, groupCounter,
              lastIteration);
        } else if (!next.equals(Player.EMPTY)) {
          groupCounter = 1;
        }
        current = next;
        updateWitnesses(rowCounter, colCounter);
        colCounter++;
      }
      current = null;
    }
  }


  /**
   * Updated {@code groupCounterArray} which contains the number of groups for
   * {@code Player.HUMAN} and {@code Player.MACHINE}. groupCounterArray[0]
   * contains the groups of the human. groupCounterArray[1] contains the groups
   * of the machine.
   *
   * @param current       Contains the current player.
   * @param next          Contains the next player.
   * @param groupCounter  Contains the current
   * @param lastIteration True if the end of a line is reached.
   * @return The new value for the {@code groupCounter}.
   */
  private int updateGroups(Player current, Player next,
      int groupCounter, boolean lastIteration) {

    //Empty to Empty
    if (current.equals(Player.EMPTY) && next.equals(Player.EMPTY)) {
      return 0;

      // Empty to P/C
    } else if (current.equals(Player.EMPTY)) {
      return 1;

      // Same Player to same Player
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

      //P/C to different Player
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
   * Used to keep track of the witnesses of the game.
   *
   * @param row Row of the currently observed element of the {@code board}.
   * @param col Column of the currently observed element of the {@code board}.
   */
  private void updateWitnesses(int row, int col) {

    if (!(witnessPlayer.size() >= 4 || witnessComputer.size() >= 4)) {

      //Since the indices used in an array differ from the coordinates of a
      // standard coordinate system, the values have to be adjusted.
      if (board[row][col].equals(Player.MACHINE)) {
        witnessPlayer.clear();
        witnessComputer.add(
            new Coordinates2D((-1 * (row - (Board.ROWS - 1)) + 1), col + 1));
      } else if (board[row][col].equals(Player.HUMAN)) {
        witnessPlayer.add(
            new Coordinates2D((-1 * (row - (Board.ROWS - 1)) + 1), col + 1));
        witnessComputer.clear();
      } else {
        witnessPlayer.clear();
        witnessComputer.clear();
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

    //Calculating the quality of the occurring groupSizes for both players.
    calculateAllGroups();
    int[] playerGroups = groupCounterArray[0];
    int[] machineGroups = groupCounterArray[1];
    int groupSizes = 50 + machineGroups[0] + 4 * machineGroups[1]
        + 5000 * machineGroups[2] - playerGroups[0] - 4 * playerGroups[1]
        - 500000 * playerGroups[2];

    //Calculating the quality of the positioning of the slot's where  filled.
    int[] playerCols = getSlotsPerCol()[0];
    int[] machineCols = getSlotsPerCol()[1];
    int placements = machineCols[1] + 2 * machineCols[2] + 3 * machineCols[3]
        + 2 * machineCols[4] + machineCols[5] - playerCols[1]
        - 2 * playerCols[2] - 3 * playerCols[3] - 2 * playerCols[4]
        - playerCols[5];

    //Adding the instant win bonus if the machine could win immediately.
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
   * slotsPerCol[0] = slots in the specified column of the {@code Player.HUMAN}.
   * slotsPerCol[1] = slots in the specified column of the {@code Player
   * .MACHINE}.
   *
   * @return The number of stones of both players in a given column.
   */
  private int[][] getSlotsPerCol() {
    int[][] slotsPerCol = new int[2][7];

    for (int i = 0; i < board[0].length; i++) {
      for (int j = 0; j < board.length; j++) {
        if (board[j][i].equals(Player.HUMAN)) {
          slotsPerCol[0][i]++;
        } else if (board[j][i].equals(Player.MACHINE)) {
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
  private int getBestIndex() {
    Connect4 tree = (Connect4) clone();
    return tree.getHighestEval(level)[1];
  }

  /**
   * Calculates the column in which a stone can be thrown to achieve the best
   * possible {@code board} for the {@code Player.MACHINE}.
   *
   * @param height Represents the level or the depth of the recursion used.
   * @return The calculated score of a {@code board} and the column in which the
   * stone has to be thrown to achieve this calculated value.
   */
  private int[] getHighestEval(int height) {
    int[] result = new int[2];
    result[0] = Integer.MIN_VALUE;

    for (int i = 0; i < Board.COLS; i++) {
      if (isColFull(i)) {
        continue;
      }
      Connect4 child = (Connect4) clone();
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
    return result;
  }

  /**
   * Calculates the column in which a stone can be thrown to achieve the worst
   * possible {@code board} for the {@code Player.HUMAN}.
   *
   * @param height Represents the level or the depth of the recursion used.
   * @return The calculated score of a {@code board} and the column in which the
   * stone has to be thrown to achieve this calculated value.
   */
  private int[] getLowestEval(int height) {
    int[] result = new int[2];
    result[0] = Integer.MAX_VALUE;

    for (int i = 0; i < Board.COLS; i++) {
      if (isColFull(i)) {
        continue;
      }

      Connect4 child = (Connect4) clone();
      child.dropStone(i, Player.HUMAN);
      int minScore = child.evaluate(height);

      if (height > 1) {
        minScore = minScore + child.getHighestEval(height - 1)[0];
      }

      if (minScore < result[0]) {
        result[0] = minScore;
      }
    }
    return result;
  }

  /**
   * Checks if a given column is already full.
   *
   * @param col Column to be checked.
   * @return True if the column is already full.
   */
  private boolean isColFull(int col) {
    return !board[0][col].equals(Player.EMPTY);
  }
}