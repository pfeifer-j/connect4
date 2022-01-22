package abalone.model;
import java.util.LinkedList;

/**
 * Implements the Abalone (lite) Board interface, and by doing so
 * the Abalone game. For more information on that read the Board javadoc.
 *
 * @version         1.0 20 Jun 2021
 * @author          Me
 *
 * // Anmerkung an den Korrektor:
 * //
 * // Bitte kommentieren Sie viel, ich bin für jeden Tipp
 * // dankbar und werde diese in der nächsten Abgabe
 * // best möglichst berücksichtigen.
 * //
 * // MfG. Me
 *
 */
public final class Game implements Board, Cloneable {
    /*
    The class Game implement the machineMove method recursively,
    this allows the Game to run up to difficulty level 4 with in
    reasonable time.

    Sine some methods access the data structure {@code board} directly
    (zero indexed), while others do not (one indexed), the Keyword Zero
    is added at the end of those methods arguments to prevent confusion.
     */

    /**
     * Amount of rows on the Abalone
     * board fully filled with balls.
     */
    private static final int FULL_FILLED_ROWS = 2;

    /**
     * The amount of spaces at the end and the
     * beginning of the partially filled row.
     */
    private static final int LAST_ROWS_SPACES = 2;

    /**
     * A state of the method / finite-state machine (FSM) sumitoBalls().
     */
    private static final int READ_OWN_BALLS = 0;

    /**
     * A state of the method / finite-state machine (FSM) sumitoBalls().
     */
    private static final int READ_ENEMY_BALLS = 1;

    /**
     * Index of the row value int the MOVES array: MOVES[move][ROW] = row.
     */
    private static final int ROW = 0;

    /**
     * Index of the row value int the MOVES array: MOVES[move][DIAG] = DIAG.
     */
    private static final int DIAG = 1;

    /**
     * A compute parameter of the board evaluation.
     * It controls the aggression level of the machine.
     */
    private static final double MACHINE_AGGRESSION_LEVEL = 1.5;

    /**
     * A compute parameter of the board evaluation.
     * It controls the value of a win in the evaluation.
     */
    private static final double WIN_EVALUATION_WORTH = 5000000;

    /**
     * Stores all valid moves one Ball can have.
     */
    private static final int[][] MOVES;

    static { // static initializer block.
        MOVES = new int[6][];
        MOVES[0] = new int[]{0, 1};
        MOVES[1] = new int[]{1, 1};
        MOVES[2] = new int[]{1, 0};
        MOVES[3] = new int[]{0, -1};
        MOVES[4] = new int[]{-1, -1};
        MOVES[5] = new int[]{-1, 0};
    }

    /**
     * Machine move lookahead, can also be interpreted as
     * difficulty level. Default value is as written below.
     */
    private static int difficultyLevel = 2;

    /**
     * The boardSize of the Abalone Game.
     * Must be odd and at least MIN_SIZE, vgl Board interface.
     */
    private final int boardSize;

    /**
     * Half value of boardSize (rounded down). Stored since it often needed.
     */
    private final int halfSize;

    /**
     * Color assigned to the Human Player at the beginning of a game.
     */
    private final Color humanColor;

    /**
     * Color assigned to the Machine Player at the beginning of a game.
     */
    private final Color machineColor;

    /**
     * The amount of balls each Player has at the beginning of a game.
     * Varies depending on the boardSize.
     */
    private final int startBalls;

    /**
     * Balls lost during the game by the Human Player.
     */
    private int lostHumanBalls;

    /**
     * Balls lost during the game by the Machine Player.
     */
    private int lostMachineBalls;

    /**
     * Player who is allowed to move a Ball next.
     */
    private Player nextPlayer;

    /**
     * List of all the Balls the Human Player has. Lost balls are removed.
     */
    private LinkedList<Ball> humanBalls;

    /**
     * List of all the Balls the Machine Player has. Lost balls are removed.
     */
    private LinkedList<Ball> machineBalls;

    /**
     * One Slot modelling the entire valid board exterior.
     * As an invariant: Is always null at the beginning of a move.
     */
    private Slot boardExterior;

    /**
     * Data structure that models the game board.
     */
    private Slot[][] board;

    /**
     * Creates a new Game and stets the its attributes according to
     * the call parameters.
     *
     * @param humanStarts Is true if the Human Player ist the Player
     *                    to draw first resp. has the Black Balls.
     * @param boardSize Is the size of the Game board. Must be odd
     *                  larger or equal to seven.
     */
    public Game(boolean humanStarts, int boardSize) {
        if (boardSize < Board.MIN_SIZE || (boardSize - 1) % 2 == 1) {
            throw new IllegalArgumentException("Invalid bordSize!");
        }
        humanBalls = new LinkedList<>();
        machineBalls = new LinkedList<>();
        boardExterior = new Slot(null, 0, 0);
        board = new Slot[boardSize][boardSize];
        this.boardSize = boardSize;
        halfSize = boardSize / 2; // round off on purpose.
        lostHumanBalls = 0;
        lostMachineBalls = 0;
        if (humanStarts) {
            humanColor = Color.Black;
            machineColor = Color.White;
            nextPlayer = Player.Human;
        } else {
            humanColor = Color.White;
            machineColor = Color.Black;
            nextPlayer = Player.Machine;
        }

        // Creates Human start Balls and their Slots.
        createHumanStart();

        // Creates Machine start Balls and their Slots.
        createMachineStart();

        // Creates Slots without start Balls.
        createRestSlots();

        // Set amount of Balls at the Start.
        startBalls = humanBalls.size();
    }

    /**
     * Deep copies this.
     *
     * @return A deep copy of this Game.
     */
    @Override
    public Game clone() {

        // At the beginning of a new round boardExterior is null.
        assert boardExterior.getBall() == null;

        // Clone this (shallow).
        Game copy;
        try {
            copy = (Game) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }

        // Clone Balls Lists (deep).
        copy.humanBalls = new LinkedList<>();
        copy.machineBalls = new LinkedList<>();
        for (Ball ball : humanBalls) {
            copy.humanBalls.add(ball.clone());
        }
        for (Ball ball : machineBalls) {
            copy.machineBalls.add(ball.clone());
        }

        // Clone boardExterior (deep).
        copy.boardExterior = boardExterior.clone();

        // Clone board (deep but old Balls references).
        copy.board = board.clone();
        for (int i = 0; i < boardSize; ++i) {
            copy.board[i] = board[i].clone();
            for (int j = firstValidDiagIndex(i);
                    j <= lastValidDiagIndex(i); ++j) {
                copy.board[i][j] = board[i][j].clone();
            }
        }

        // Adjust board references to point to cloned Balls.
        for (Ball ball : copy.humanBalls) {
            Slot slot = ball.getSlot();
            int row = slot.getRow();
            int diag = slot.getDiag();
            copy.getBoardSlot(row, diag).setBall(ball);
        }
        for (Ball ball : copy.machineBalls) {
            Slot slot = ball.getSlot();
            int row = slot.getRow();
            int diag = slot.getDiag();
            copy.getBoardSlot(row, diag).setBall(ball);
        }
        return copy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Player getOpeningPlayer() {
        if (humanColor == Color.Black) {
            return Player.Human;
        } else {
            return Player.Machine;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Color getHumanColor() {
        return  humanColor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Player getNextPlayer() {
        if (isGameOver()) {
            throw new IllegalStateException("There is no next Player."
                    + " Game is over!");
        }
        return nextPlayer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValidPosition(int row, int diag) {

        // Coordinate conversion.
        --row;
        --diag;
        return isValidPosition(row, diag, boardSize);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValidTarget(int row, int diag) {

        /*
        The value extendedSize includes the valid Board exterior,
        its the same as a bord of the next bigger BordSize (+2).
        Since row and diag refer to the old coordinates an offset
        is needed, that offset reverses the coordinate conversion.
         */
        int extendedSize = boardSize + 2;
        return isValidPosition(row, diag, extendedSize);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Board move(int rowFrom, int diagFrom, int rowTo, int diagTo) {
        if (isGameOver()) {
            throw new IllegalStateException("The Game is already over!");
        } else if (getNextPlayer() != Player.Human) {
            throw new IllegalStateException("Its not your Turn,"
                    + " wait for machine!");
        } else if (!isValidPosition(rowFrom, diagFrom)
                    || !isValidTarget(rowTo, diagTo)) {
            throw new IllegalArgumentException("Coordinates are invalid"
                    + " / outside the Board!");
        }
        // Read move from board coordinates.
        Integer move = validMove(rowFrom, diagFrom, rowTo, diagTo);
        if (move == null) {
            return null;
        }
        return move(rowFrom, diagFrom, move);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Board machineMove() {
        if (isGameOver()) {
            throw new IllegalStateException("Game is Over!");
        } else if (nextPlayer != Player.Machine) {
            throw new IllegalStateException("Cant perform machineMove,"
                    + " its not its Turn!");
        }

        return machineMove(0).getGame();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLevel(int level) {
        if (level < 1) {
            throw new IllegalArgumentException(level + " is no valid level!");
        }
        difficultyLevel = level;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isGameOver() {
        return lostHumanBalls == Board.ELIM || lostMachineBalls == Board.ELIM;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Player getWinner() {
        if (lostMachineBalls == Board.ELIM) {
            return Player.Human;
        } else if (lostHumanBalls == Board.ELIM) {
            return Player.Machine;
        } else {
            throw new IllegalStateException("There is no Winner,"
                    + " the game is still running.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumberOfBalls(Color color) {
        switch (color) {
            case Black -> {
                if (humanColor == Color.Black) {
                    return startBalls - lostHumanBalls;
                } else {
                    return startBalls - lostMachineBalls;
                }
            }
            case White -> {
                if (humanColor == Color.White) {
                    return startBalls - lostHumanBalls;
                } else {
                    return startBalls - lostMachineBalls;
                }
            }
            default -> throw new IllegalArgumentException("Color"
                    + " must not be null!");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Color getSlot(int row, int diag) {
        Ball ball = getBoardSlot(row, diag).getBall();
        if (ball == null) {
            return null;
        } else {
            return ball.getColor();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSize() {
        return boardSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        int leadingSpaces = halfSize;
        StringBuilder sb = new StringBuilder();
        for (int i = boardSize - 1; i >= 0; i--) {

            // Append leading spaces.
            sb.append(" ".repeat(leadingSpaces));
            if (i > halfSize) {
                --leadingSpaces;
            } else {
                ++leadingSpaces;
            }

            // Avoid multiple computations in for loop head.
            int fvdi = firstValidDiagIndex(i);
            int lvdi = lastValidDiagIndex(i);
            for (int j = fvdi; j <= lvdi; j++) {
                if (board[i][j] == null) {
                    throw new IllegalArgumentException("Invalid GameBoard!"
                            + " Cant print to String.");
                } else if (board[i][j].getBall() == null) {
                    sb.append(".");
                } else if (board[i][j].getBall().getColor() == Color.Black) {
                    sb.append("X");
                } else if (board[i][j].getBall().getColor() == Color.White) {
                    sb.append("O");
                } else {
                    throw new IllegalArgumentException("Invalid GameBoard!"
                            + " Cant print to String.");
                }
                if (j != lvdi) {
                    sb.append(" ");
                }
            }
            if (i != 0) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * Clones this and moves all sumito Balls of a Slot in the direction move.
     *
     * @param rowFrom Row coordinate of the Slot.
     * @param diagFrom Diag coordinate of the Slot.
     * @param move Direction to move toward.
     * @return The cloned Game (Board) after move.
     */
    private Game move(int rowFrom, int diagFrom, int move) {
        /*
        The more concrete Object Game (instead of Board) is used as a return
        value, because it allows easy access to this classes non interface
        methods when this method is called in machineMove(int i).
         */

        // Checks for correct usage of method.
        assert !isGameOver();
        assert isValidPosition(rowFrom, diagFrom) && move >= 0
                && move < MOVES.length;

        // Deep copy this.
        Game game = clone();

        // Get first and last Ball that is to be moved.
        Slot start = game.getBoardSlot(rowFrom, diagFrom);
        Slot end = game.sumitoBalls(start, move);

        // sumitoBalls returns null if the move is invalid.
        if (end == null) {
            return null;
        }

        // Move Balls from last to first.
        game.moveBall(end, move);
        while (start != end) {
            end = game.getPreviousSlot(end, move);
            game.moveBall(end, move);
        }

        // Set flags.
        Ball lostBall = game.boardExterior.getBall();
        if (lostBall != null) {
            if (lostBall.getColor() == humanColor) {
                ++game.lostHumanBalls;
                game.humanBalls.remove(lostBall);
            } else {
                ++game.lostMachineBalls;
                game.machineBalls.remove(lostBall);
            }

            // Clean up lost balls field.
            game.boardExterior.setBall(null);
        }

        // Next Player Logic.
        if (game.isGameOver()) {
            game.nextPlayer = null;
        } else {

            // Switching twice is necessary because
            // hasValidMoves accesses the nextPlayer attribute.
            game.switchNextPlayer();
            if (!game.hasValidMoves()) {
                game.switchNextPlayer();
            }
        }
        return game;
    }

    /**
     * Recursive evaluation of the best possible move
     * following the documented heuristic.
     *
     * @param i Depth of the recursion. Must always be 0 when method is called.
     * @return A BoardEvaluation containing the best Game board and its rating.
     */
    private BoardEvaluation machineMove(int i) {
        assert i >= 0;
        BoardEvaluation eval = null;

        // Recursive stop GameOver.
        if (isGameOver()) {
            assert getWinner() != null;

            // Compute board value.
            eval = switch (getWinner()) {
                case Human -> evaluate(i, BoardEvaluation.NO_WIN);
                case Machine -> evaluate(BoardEvaluation.NO_WIN, i);
                default -> throw new Error();
            };

        // Recursive stop max lookahead.
        } else if (i == difficultyLevel) {

            // Compute Board value.
            eval = evaluate(BoardEvaluation.NO_WIN, BoardEvaluation.NO_WIN);

        // Recursion case.
        } else {

            // As long as the game is running there is always a next player.
            assert nextPlayer != null;
            LinkedList<Ball> balls = getPlayersBalls(nextPlayer);

            // List of all valid game (Boards) and their ratings.
            LinkedList<BoardEvaluation> evaluations = new LinkedList<>();
            for (Ball ball : balls) {
                assert ball != null;
                for (int move = 0; move < MOVES.length; move++) {

                    // Since move is always valid,
                    // only a sumito position is necessary for a valid move.
                    if (sumitoBalls(ball.getSlot(), move) != null) {
                        Game game = move(ball.getSlot().getRow(),
                                         ball.getSlot().getDiag(), move);

                        // Only valid moves are made, game must be not null.
                        assert game != null;
                        BoardEvaluation nextEval = game.machineMove(i + 1);

                        // First call, only delegation needed
                        // since this is the old board.
                        if (i == 0) {
                            evaluations.add(nextEval);
                        } else {

                            // Compute Board value.
                            BoardEvaluation oneEval = evaluate(
                                BoardEvaluation.NO_WIN, BoardEvaluation.NO_WIN);
                            oneEval.setBoardRating(oneEval.getBoardRating()
                                + nextEval.getBoardRating());
                            evaluations.add(oneEval);
                        }
                    }
                }
            }

            // Searching for the best or worst bord for the
            // machine depending on whose move it was.
            double minMaxRating;
            switch (nextPlayer) {

                // Tries to harm the machine as much as he can (worst value).
                case Human -> {
                    minMaxRating = Double.MAX_VALUE;
                    for (BoardEvaluation oneEval : evaluations) {
                        double oneRating = oneEval.getBoardRating();
                        if (oneRating < minMaxRating) {
                            minMaxRating = oneRating;
                            eval = oneEval;
                        }
                    }
                }

                // Tries to make the best move possible (best value).
                case Machine -> {
                    minMaxRating = -Double.MAX_VALUE;
                    for (BoardEvaluation oneEval : evaluations) {
                        double oneRating = oneEval.getBoardRating();
                        if (oneRating > minMaxRating) {
                            minMaxRating = oneRating;
                            eval = oneEval;
                        }
                    }
                }
                default -> throw new Error();
            }
        }
        assert eval != null;
        return eval;
    }

    private BoardEvaluation evaluate(double humanWinIn, double machineWinIn) {
        double n = computeN();
        double m = computeM();
        double v = computeV(humanWinIn, machineWinIn);
        return new BoardEvaluation(this, boardSize * n + m + v);
    }

    private double computeN() {
        return (startBalls - lostMachineBalls)
                - MACHINE_AGGRESSION_LEVEL * (startBalls - lostHumanBalls);
    }

    private double computeV(double humanWinIN, double machineWinIn) {
        double vh = 0;
        if (humanWinIN != BoardEvaluation.NO_WIN) {
            vh = WIN_EVALUATION_WORTH / humanWinIN;
        }
        double vm = 0;
        if (machineWinIn != BoardEvaluation.NO_WIN) {
            vm = WIN_EVALUATION_WORTH / machineWinIn;
        }
        return  vm - MACHINE_AGGRESSION_LEVEL * vh;
    }

    private double computeM() {
        double humanBallsValues = ballsValue(Player.Human);
        double machineBallsValues = ballsValue(Player.Machine);
        return machineBallsValues - MACHINE_AGGRESSION_LEVEL * humanBallsValues;
    }

    private double ballsValue(Player player) {
        int sum = 0;
        int[] ballsValues = new int[halfSize + 1];
        LinkedList<Ball> balls = getPlayersBalls(player);
        for (Ball ball : balls) {
            int row = ball.getSlot().getRow() - 1;
            int diag1 = ball.getSlot().getDiag() - 1;
            int diag2 = row - diag1 + halfSize;
            int d1 = Math.min(row, boardSize - row - 1);
            int d2 = Math.min(diag1, boardSize - diag1 - 1);
            int d3 = Math.min(diag2, boardSize - diag2 - 1);
            int d = Math.min(Math.min(d1, d2), d3);
            ballsValues[d] = ballsValues[d] + 1;
        }
        for (int i = 0; i <= halfSize; i++) {
            sum = sum + ballsValues[i] * i;
        }
        return sum;
    }

    private LinkedList<Ball> getPlayersBalls(Player player) {
        assert player != null;
        return switch (player) {
            case Human -> humanBalls;
            case Machine -> machineBalls;
            default -> throw new Error();
        };
    }

    private Slot sumitoBalls(Slot slot, int move) {
        Slot worker = slot;
        int sumito = 1;
        int state = READ_OWN_BALLS;
        while (getNextSlot(worker, move).getBall() != null) {
            worker = getNextSlot(worker, move);

            // If a own Ball is behind enemy Balls the Balls cant be moved.
            if (slot.getBall().getColor() == worker.getBall().getColor()
                    && state == READ_ENEMY_BALLS) {
                return null;

            // Count both sides Balls.
            } else if (slot.getBall().getColor()
                    == worker.getBall().getColor()) {
                ++sumito;
            } else {
                state = READ_ENEMY_BALLS;
                --sumito;
            }
        }

        // Checks whether there are enough Balls to move enemy Balls.
        if (sumito > 0) {
            return worker;
        } else {
            return null;
        }
    }

    private Slot getPreviousSlot(Slot slot, int move) {
        return getBoardSlot(slot.getRow() - MOVES[move][ROW],
                slot.getDiag() - MOVES[move][DIAG]);
    }

    private Slot getNextSlot(Slot slot, int move) {
        return getBoardSlot(slot.getRow() + MOVES[move][ROW],
                slot.getDiag() + MOVES[move][DIAG]);
    }


    private Integer validMove(int rowFrom, int diagFrom,
                              int rowTo, int diagTo) {
        if (isPlayersBall(rowFrom, diagFrom) && isValidTarget(rowTo, diagTo)) {
            for (int i = 0; i < MOVES.length; i++) {
                if (rowTo - rowFrom == MOVES[i][ROW]
                        && diagTo - diagFrom == MOVES[i][DIAG]) {
                    return i;
                }
            }
        }
        return null;
    }

    private boolean hasValidMoves() {
        assert nextPlayer != null;
        LinkedList<Ball> balls = switch (nextPlayer) {
            case Human ->  humanBalls;
            case Machine ->  machineBalls;
        };
        for (Ball ball : balls) {

            // Lost Balls are removed from List.
            assert ball != null;

            // boardExterior should always be cleared after a move.
            assert boardExterior.getBall() == null;

            // Constructor creates only validPosition Slots (and boardExterior).
            assert isValidPosition(ball.getSlot().getRow(),
                    ball.getSlot().getRow());

            // Every Ball has the Color of the next Player
            assert ball.getColor() == getPlayerColor(nextPlayer);

            // checks for possible moves.
            for (int move = 0; move < MOVES.length; move++) {
                if (sumitoBalls(ball.getSlot(), move) != null) {
                    return true;
                }
            }
        }
        return false;
    }

    private void switchNextPlayer() {
        assert nextPlayer != null;
        nextPlayer = switch (nextPlayer) {
            case Human ->  Player.Machine;
            case Machine ->  Player.Human;
            default -> throw new Error();
        };
    }

    private boolean isPlayersBall(int row, int diag) {
        if (isValidPosition(row, diag)) {
            Ball ball = getBoardSlot(row, diag).getBall();
            if (ball == null) {
                return false;
            } else {
                return ball.getColor() == getPlayerColor(nextPlayer);
            }
        } else {
            return false;
        }
    }

    private Color getPlayerColor(Player player) {
        assert player != null;
        return switch (player) {
            case Human -> humanColor;
            case Machine -> machineColor;
        };
    }

    private void moveBall(Slot slot, int move) {
        assert slot != boardExterior;
        Slot target = getBoardSlot(slot.getRow() + MOVES[move][ROW],
                              slot.getDiag() + MOVES[move][DIAG]);
        if (target.getBall() == null) {
            Ball ball = slot.getBall();
            target.setBall(ball);
            slot.setBall(null);
            ball.setSlot(target);
        } else {
            throw new IllegalArgumentException("A Ball is always moved to an"
                    + " empty Slot. The Slot must first be cleared!");
        }
    }

    private boolean isValidPosition(int rowZero, int diagZero, int boardSize) {
        assert  boardSize >= Board.MIN_SIZE && boardSize % 2 == 1;
        return rowZero >= 0 && rowZero < boardSize
                && diagZero >= firstValidDiagIndex(rowZero, boardSize)
                && diagZero <= lastValidDiagIndex(rowZero, boardSize);
    }

    private int lastValidDiagIndex(int rowZero) {
        return  lastValidDiagIndex(rowZero, boardSize);
    }

    private int firstValidDiagIndex(int rowZero) {
        return firstValidDiagIndex(rowZero, boardSize);
    }

    private static int lastValidDiagIndex(int rowZero, int boardSize) {
        assert  boardSize >= Board.MIN_SIZE && boardSize % 2 == 1;
        return  Math.min(rowZero + (boardSize / 2), boardSize - 1);
    }

    private static int firstValidDiagIndex(int rowZero, int boardSize) {
        assert  boardSize >= Board.MIN_SIZE && boardSize % 2 == 1;
        return Math.max(0, rowZero - (boardSize / 2));
    }

    private Slot getBoardSlot(int row, int diag) {
        if (isValidPosition(row, diag)) {
            return board[row - 1][diag - 1];
        } else if (isValidTarget(row, diag)) {
            return boardExterior;
        } else {
            throw new IllegalArgumentException("The position is invalid!"
                    + " Its neither on the Bord nor the Exterior!");
        }
    }

    /**
     * Do not call this methode!
     * Its an auxiliary method for the constructor.
     * (Since it is otherwise to long)
     */
    private void createHumanStart() {
        for (int i = 0; i <= FULL_FILLED_ROWS; i++) {
            int fvdi = firstValidDiagIndex(i);
            int lvdi = lastValidDiagIndex(i);
            for (int j = fvdi; j <= lvdi; j++) {
                if (i < FULL_FILLED_ROWS || (j >= fvdi + LAST_ROWS_SPACES
                        && j <= lvdi - LAST_ROWS_SPACES)) {
                    Ball ball = new Ball(humanColor);
                    humanBalls.add(ball);
                    Slot slot = new Slot(ball, i + 1, j + 1);
                    ball.setSlot(slot);
                    board[i][j] = slot;
                }
            }
        }
    }

    /**
     * Do not call this methode!
     * Its an auxiliary method for the constructor.
     * (Since it is otherwise to long)
     */
    private void createMachineStart() {
        for (int i = boardSize - 1;
             i >= boardSize - 1 - FULL_FILLED_ROWS; i--) {
            int fvdi = firstValidDiagIndex(i);
            int lvdi = lastValidDiagIndex(i);
            for (int j = lvdi; j >= fvdi; j--) {
                if (i >= boardSize - FULL_FILLED_ROWS
                        || (j >= fvdi + LAST_ROWS_SPACES
                        && j <= lvdi - LAST_ROWS_SPACES)) {
                    Ball ball = new Ball(machineColor);
                    machineBalls.add(ball);
                    Slot slot = new Slot(ball, i + 1, j + 1);
                    ball.setSlot(slot);
                    board[i][j] = slot;
                }
            }
        }
    }

    /**
     * Do not call this methode!
     * Its an auxiliary method for the constructor.
     * (Since the constructor is otherwise to long)
     */
    private void createRestSlots() {
        for (int i = 0; i < boardSize; i++) {
            int fvdi = firstValidDiagIndex(i);
            int lvdi = lastValidDiagIndex(i);
            for (int j = fvdi; j <= lvdi; j++) {
                if (board[i][j] == null) {
                    board[i][j] = new Slot(null, i + 1, j + 1);
                }
            }
        }
    }
}
