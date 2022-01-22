package abalone.view;

import abalone.model.Board;
import abalone.model.Game;
import abalone.model.Player;
import abalone.view.observerPattern.Observer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.Serial;
import java.util.Stack;

/**
 * Models the main panel of the abalone game GUI.
 *
 * @version         1.0 8 Jul 2021
 * @author          Me
 */
public class AbaloneGamePanel extends JPanel implements Observer {

    /**
     * Serial ID of this.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The menu colors are fixed and independent of the Theme.
     * This color is the background color for all Objects containing menus,
     * for example the menu bar and the JPanel containing the GameButtons.
     */
    public static final Color MENU_COLOR = Color.LIGHT_GRAY;

    /**
     * Number that is used in almost every part of the GUI to initializes
     * component sizes and there for can be used to scale the entire user
     * interface.
     */
    private static final int INTERFACE_SIZE = 60;

    /**
     * Time in milliseconds the machine sleeps.
     *
     * At simple difficulty levels the machine moves very fast, there for
     * leafing the player no time to look up from his own Balls and see witch
     * move the machine made. To address this the machine thread can be sent to
     * sleep before drawing. Should this effect turn out to be more annoying
     * than useful, it can be turned of by setting it to zero.
     */
    private static final int MACHINE_SLEEP = 400;

    /**
     * At higher levels the machine moves slow enough to see
     * its move and the function is turned of.
     */
    private static final int MAX_LEVEL_TO_SLEEP = 3;

    /**
     * The background color of all buttons.
     */
    private static final Color BUTTON_COLOR = Color.DARK_GRAY;

    /**
     * The text color of all buttons.
     */
    private static final Color BUTTON_TEXT_COLOR = Color.WHITE;

    /**
     * This Dimension is the preferredSize of every JPanel subclass except for
     * this and the CenterPanel.
     */
    private static final Dimension INTERFACE_PANEL_SIZE = new Dimension(
            INTERFACE_SIZE, INTERFACE_SIZE);

    /**
     * The font used in all text inside this Panel.
     */
    private static final Font FONT = new Font("Comic Sans MS",
            Font.BOLD, INTERFACE_SIZE / 2);

    /**
     * The frame this is attached to.
     */
    private final JFrame frame;

    /**
     * The Observable Object storing all changeable information displayed
     * trough the GUI.
     */
    private final DisplayData displayData;

    /**
     * A reference to the Thread computing the machine move. May be null.
     */
    private Thread guiMachineMove;

    /**
     * A reference to the center panel of the abalone game panel.
     */
    private JPanel centerPanel;

    /**
     * The amount of Balls each player starts with. Depends on the board size.
     */
    private int startBalls;

    /**
     * Stores whether the human is allowed to start, when the next game is
     * starts.
     */
    private boolean humanStarts = true;

    /**
     * The new board size of the upcoming game.
     * Since it can differ form the board size currently played on, int must
     * not be used for anything but to set the Size of the upcoming game.
     */
    private int boardSize = 9;

    /**
     * The difficulty level of the upcoming machine move. Used to update
     * the board difficulty level just before executing the machine move.
     */
    private int difficultyLevel = 2;

    /**
     * Stores the Slot that is currently highlighted.
     * As an invariant its the slot from which will be moved form.
     */
    private SlotPanel isHighlighted = null;

    /**
     * Stores the next player of the previous board.
     */
    private Player oldNextPlayer;

    /**
     * Stack of old bord form just before a human move was executed.
     */
    private final Stack<Board> oldBoards = new Stack<>();

    /**
     * Stack of old next players form just before a human move was executed.
     */
    private final Stack<Player> olderNextPlayers = new Stack<>();

    /**
     * Creates a new abalone game panel and all its child panels.
     *
     * @param frame Frame to be set as this parent Frame.
     */
    public AbaloneGamePanel(JFrame frame) {
        if (frame == null) {
            throw new IllegalArgumentException("Parent frame"
                    + " must not be null.");
        }

        // Initialize this.
        this.frame = frame;
        displayData = new DisplayData();
        displayData.addObserver(this);
        setLayout(new BorderLayout());

        // Panels need a board to adjust their colors.
        guiNewGame();
        add(new TopPanel(), BorderLayout.NORTH);
        add(new BottomPanel(), BorderLayout.SOUTH);
    }

    /**
     * Gets the display data for this panel.
     *
     * @return The display data for this panel.
     */
    public DisplayData getDisplayData() {
        return displayData;
    }

    /**
     * Gets the size of the game board of the next game.
     *
     * @return Size of the game board of the next game.
     */
    public int getBoardSize() {
        return boardSize;
    }

    /**
     * Sets the size of the game board of the next game.
     *
     * @param boardSize Size of the game board of the next game.
     */
    public void setBoardSize(int boardSize) {

        // Every bord is minimum MIN_SIZE and odd.
        assert boardSize >= Board.MIN_SIZE && boardSize % 2 == 1;
        this.boardSize = boardSize;
    }

    /**
     * Gets the difficulty level of the next machine move.
     *
     * @return The difficulty level of the next machine move.
     */
    public int getDifficultyLevel() {
        return difficultyLevel;
    }

    /**
     * Sets the difficulty level of the next machine move.
     *
     * @param difficultyLevel The difficulty level of the next machine move.
     */
    public void setDifficultyLevel(int difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    /**
     * Kills the machine move thread, this is necessary since the abalone model.
     * Was not created with an multithreaded GUI in mind.
     */
    @Deprecated
    public void killMachineMove() {
        if (guiMachineMove != null && guiMachineMove.isAlive()) {
            guiMachineMove.stop();
        }
    }

    /**
     * Sets the level of the next machine move, and starts the machine move
     * in a new Thread, if the machine is allowed to move next.
     */
    @Override
    public void update() {
        Board board = displayData.getBoard();
        if (!board.isGameOver() && board.getNextPlayer() == Player.Machine) {
            board.setLevel(difficultyLevel);
            guiMachineMove = new GuiMachineMove();
            guiMachineMove.start();
        }
    }

    /**
     * Executes a human move from the highlighted slot to
     * the parameter coordinates if possible.
     *
     * @param rowTo Row coordinate to move to.
     * @param diagTo Diag coordinate to move to.
     */
    private void guiHumanMove(int rowTo, int diagTo) {
        assert displayData.getBoard().isValidTarget(rowTo, diagTo);
        Board board = displayData.getBoard();
        Board newBoard = board.move(isHighlighted.row, isHighlighted.diag,
                                    rowTo, diagTo);
        if (newBoard == null) {
            Toolkit.getDefaultToolkit().beep();
        } else {
            completeMove(newBoard, true);
        }
        SlotPanel wasHighlighted = isHighlighted;
        isHighlighted = null;
        wasHighlighted.update();
    }

    /**
     * Undo the last human move.
     */
    private void guiUndo() {
        isHighlighted = null;
        oldNextPlayer = olderNextPlayers.pop();
        displayData.setBoard(oldBoards.pop());
    }

    /**
     * Create a new game.
     */
    @Deprecated
    private void guiNewGame() {
        killMachineMove();
        oldBoards.removeAllElements();
        olderNextPlayers.removeAllElements();

        if (centerPanel != null) {
            AbaloneGamePanel.this.remove(centerPanel);
        }

        centerPanel = new CenterPanel();
        AbaloneGamePanel.this.add(centerPanel, BorderLayout.CENTER);

        // Needed if boardSize is switched.
        frame.pack();
    }

    /**
     * Switch starting player and create a new game.
     */
    @Deprecated
    private void guiSwitch() {
        humanStarts = !humanStarts;
        guiNewGame();
    }

    /**
     * Close the application.
     */
    private void guiQuit() {
        for (java.awt.Frame frame : java.awt.Frame.getFrames()) {
            frame.dispose();
        }
    }

    /**
     * After the execution of a move some additional work is
     * to be done before the new Board can be set as the game Board.
     *
     * The human move has to update the undo Stacks oldBoards and
     * oldNextPlayers.
     *
     * The Machine move calls this method via invokeLater and there
     * for synchronizes the writing access to displayData.
     *
     * @param newBoard The board to be set as the game board.
     * @param humanMove Indicates whether the method was method was
     *                  called by the human move or the machine move.
     */
    private void completeMove(Board newBoard, boolean humanMove) {
        assert newBoard != null;
        Board oldBoard = displayData.getBoard();
        if (humanMove) {
            oldBoards.add(oldBoard);
            olderNextPlayers.add(oldNextPlayer);
        }
        oldNextPlayer = oldBoard.getNextPlayer();
        displayData.setBoard(newBoard);
    }

    /**
     * Action performed when a slot is clicked.
     *
     * @param clickedSlot Slot that was clicked.
     */
    private void slotClicked(SlotPanel clickedSlot) {
        /*
        Method needed since internal class is otherwise to long.
         */

        assert clickedSlot != null;

        // Get values to work with.
        int row = clickedSlot.row;
        int diag = clickedSlot.diag;
        Board board = displayData.getBoard();

        // No Slot selection available (invalid state).
        if (board.isGameOver() || board.getNextPlayer() == Player.Machine) {
            Toolkit.getDefaultToolkit().beep();

        // Slot selection.
        } else {

            // Select first slot.
            if (isHighlighted == null) {

                // Slot must be valid and hold
                // a human ball to be selectable.
                if (board.isValidPosition(row, diag)
                        && board.getSlot(row, diag) == board.getHumanColor()) {
                    isHighlighted = clickedSlot;

                    // Only this slot must be updated.
                    update();

                // Valid target must be selected after position.
                } else {
                    Toolkit.getDefaultToolkit().beep();
                }

            // Unselect this slot.
            } else if (isHighlighted == clickedSlot) {
                isHighlighted = null;

                // Only this slot must be updated.
                update();

            // Human Move
            } else {
                guiHumanMove(row, diag);
            }
        }
    }

    /**
     * Models the class of Thread that executes a machine move.
     */
    private class GuiMachineMove extends Thread {

        @Override
        public void run() {
            if (MACHINE_SLEEP != 0 && difficultyLevel <= MAX_LEVEL_TO_SLEEP) {
                try {
                    sleep(MACHINE_SLEEP);
                } catch (InterruptedException e) {
                /*
                No handling necessary since the wait is only used
                to get a better user experience. And if an interrupt
                occurs the program is probably about to be closed anyway.
                 */
                }
            }
            Board newBoard = displayData.getBoard().machineMove();
            SwingUtilities.invokeLater(() -> completeMove(newBoard, false));
        }
    }

    /**
     * Models the top panel of the game panels BoarderLayout.
     * Handles most of the game state information output.
     */
    private final class TopPanel extends JPanel implements Observer {

        /**
         * Serial ID of this.
         */
        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * Output if the machine moves next.
         */
        private static final String MACHINE_TURN = "Machine turn! "
                + "(please wait)";

        /**
         * Output if the player / human is allowed to move next.
         */
        private static final String HUMAN_TURN = "Your turn!";

        /**
         * Output if the machine has won.
         */
        private static final String MACHINE_WIN = "Sorry! Machine wins.";

        /**
         * Output if the player / human has won.
         */
        private static final String HUMAN_WIN = "Congratulations! You won.";

        /**
         * Output if the machine must skip.
         */
        private static final String MACHINE_SKIP = "I must skip,"
                + " please move again.";

        /**
         * Output if the human must skip.
         */
        private static final String HUMAN_SKIP = "You must skip,"
                + " I will move again.";

        /**
         * Label that prints the output.
         */
        private final JLabel label;

        /**
         * Creates a top panel.
         */
        private TopPanel() {
            setPreferredSize(INTERFACE_PANEL_SIZE);
            label = new JLabel();
            label.setFont(FONT);
            add(label);
            displayData.addObserver(this);
            update();
        }

        /**
         * Updates the top panel according to the game state and theme.
         */
        @Override
        public void update() {
            assert displayData != null;
            Board board = displayData.getBoard();
            assert board != null;
            Theme theme = displayData.getTheme();

            // Declare colors.
            Color firstHumanColor;
            Color secondHumanColor;
            Color firstMachineColor;
            Color secondMachineColor;

            // Initialize colors.
            switch (board.getHumanColor()) {
                case White -> {
                    firstHumanColor = theme.getColor(
                            Theme.NOT_STARTING_PLAYER_BALL_FIRST);
                    secondHumanColor = theme.getColor(
                            Theme.NOT_STARTING_PLAYER_BALL_SECOND);
                    firstMachineColor = theme.getColor(
                            Theme.STARTING_PLAYER_BALL_FIRST);
                    secondMachineColor = theme.getColor(
                            Theme.STARTING_PLAYER_BALL_SECOND);
                }
                case Black -> {
                    firstHumanColor = theme.getColor(
                            Theme.STARTING_PLAYER_BALL_FIRST);
                    secondHumanColor = theme.getColor(
                            Theme.STARTING_PLAYER_BALL_SECOND);
                    firstMachineColor = theme.getColor(
                            Theme.NOT_STARTING_PLAYER_BALL_FIRST);
                    secondMachineColor = theme.getColor(
                            Theme.NOT_STARTING_PLAYER_BALL_SECOND);
                }
                default -> throw new Error();
            }

            if (board.isGameOver()) {
                switch (board.getWinner()) {
                    case Machine -> {
                        setBackground(firstMachineColor);
                        label.setForeground(secondMachineColor);
                        label.setText(MACHINE_WIN);
                    }
                    case Human -> {
                        setBackground(firstHumanColor);
                        label.setForeground(secondHumanColor);
                        label.setText(HUMAN_WIN);
                    }
                    default -> throw new Error();
                }
            } else {
                Player next = board.getNextPlayer();
                switch (next) {
                    case Machine -> {
                        setBackground(firstMachineColor);
                        label.setForeground(secondMachineColor);
                        if (next == oldNextPlayer) {
                            label.setText(HUMAN_SKIP);
                        } else {
                            label.setText(MACHINE_TURN);
                        }
                    }
                    case Human -> {
                        setBackground(firstHumanColor);
                        label.setForeground(secondHumanColor);
                        if (next == oldNextPlayer) {
                            label.setText(MACHINE_SKIP);
                        } else {
                            label.setText(HUMAN_TURN);
                        }
                    }
                    default -> throw new Error();
                }
            }
        }
    }

    /**
     * Models the game bord of the abalone game.
     * Arranges initializes the slot panels in the needed form.
     */
    private final class CenterPanel extends JPanel implements Observer {

        /**
         * Serial ID of this.
         */
        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * Creates a new center panel.
         */
        private CenterPanel() {

            // Initialize this.
            oldNextPlayer = null;
            displayData.setBoard(new Game(humanStarts, boardSize));
            startBalls = displayData.getBoard().getNumberOfBalls(
                    abalone.model.Color.Black);
            displayData.addObserver(this);
            setBackground(displayData.getTheme().getColor(
                    Theme.BOARD_BACKGROUND));

            // +2 for board exterior. displayData board size is
            // needed since this.boardSize may have already changed.
            int extendedBoardSize = displayData.getBoard().getSize() + 2;

            // *2 for invalid panels. -1 for symmetry.
            int panels = extendedBoardSize * 2 - 1;
            setLayout(new GridLayout(extendedBoardSize, panels));

            // Stores the first valid SlotPanel grid column index
            // (different for each row).
            int startDiagCounterAt = extendedBoardSize / 2 + 1;

            // Stores the diag value for the fist valid SlotPanel indicated
            // by startDiagCounterAt. startDiagCounterAt and startingDiagValue
            // have the same initial value but diverge at some point.
            int startingDiagValue = startDiagCounterAt;

            /*
            Creates and adds all SlotPanels starting top row fist column.
            This is necessary because the position of a SlotPanel in the grid
            is determinate by the order they are added. The outer for loop are
            the rows, the inner for loop are the columns, diagIndex is the
            abalone diag index.

            Since there is no way get the abalone way of addressing the
            SlotPanels without declaring it first some assumptions about the
            abalone board have to be made, if you want to use this GUI as for a
            different Board then the fallowing code must be rewritten.
             */
            for (int i = extendedBoardSize - 1; i >= 0; i--) {

                /*
                The amount of valid slot in a abalone board increases from top
                down until the middle of the board. From the middle till the
                bottom of the bord startingDiagValue stays 0 since the diag
                index of the first valid slot on a abalone board is always 0
                there.
                */
                if (startingDiagValue == 0) {
                    ++startDiagCounterAt;
                } else {
                    --startDiagCounterAt;
                    --startingDiagValue;
                }

                // The startingDiagValue is need in every row and there for is
                // not allowed to change within the inner for loop.
                int diagIndex = startingDiagValue;
                for (int j = 0; j < panels; j++) {

                    // Panels outside the abalone board.
                    if (j < startDiagCounterAt
                            || j > panels - startDiagCounterAt - 1) {
                        add(new SlotPanel(i, SlotPanel.INVALID_PANEL, true));

                    /*
                    Every second Panel is valid if its inside the board, forming
                    a grid pattern. The same scheme is used in the Laplace
                    expansion.

                    The valid Panels are dependent of the boardSize, and
                    alternate with each increase of the boardSize (+2).
                     */
                    } else if ((i + j) % 2 == (extendedBoardSize / 2) % 2) {

                        // Since we now fully declared our grid we can now
                        // check whether our assumptions are correct. Does
                        // not show all valid targets are created.
                        assert displayData.getBoard().isValidTarget(i,
                                diagIndex);

                        add(new SlotPanel(i, diagIndex, false));

                        // Only if a valid Panel is hit dirgIndex is increased,
                        // only these panels are addressed by the abalone game,
                        // since only these can hold balls.
                        ++diagIndex;

                    // Invalid Panels within the abalone board.
                    } else {
                        add(new SlotPanel(i, SlotPanel.INVALID_PANEL, false));
                    }
                }
            }
        }

        /**
         * Updates the background color of the center panel. This is necessary
         * since there is a small space between this panel and its children
         * panels if the window is dragged into an not optimal form.
         */
        @Override
        public void update() {
            setBackground(displayData.getTheme().getColor(
                    Theme.BOARD_BACKGROUND));
        }
    }

    /**
     * Models a slot of the abalone game board.
     * Handles the user interaction with the game.
     */
    private final class SlotPanel extends JPanel implements Observer {

        /**
         * Serial ID of this.
         */
        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * Value of the diag index that indicates that the panel
         * has no diag index resp. is invalid.
         */
        private static final int INVALID_PANEL = -1;

        /**
         * In addition to being invalid the panel can also be outside the
         * board. This distinction is only needed for painting the panels.
         *
         */
        private final boolean outsideBoard;

        /**
         * The row index of the panel resp. slot.
         * Even an invalid panel as a valid row index.
         */
        private final int row;

        /**
         * The diag index of the panel resp. slot. Also shows whether or not the
         * panel is valid by containing or not containing a valid diag index.
         */
        private final int diag;

        /**
         * Creates a new SlotPanel.
         *
         * @param row Row index of the panel.
         * @param diag Diag index of the panel, can be invalid.
         *             Don't confuse Diag index with columns.
         * @param outsideBoard Whether or not panel is outside the board.
         */
        private SlotPanel(int row, int diag, boolean outsideBoard) {

            // Smaller numbers are invalid
            assert row >= 0 && diag >= INVALID_PANEL;

            this.row = row;
            this.diag = diag;
            this.outsideBoard = outsideBoard;

            setPreferredSize(INTERFACE_PANEL_SIZE);
            displayData.addObserver(this);
            if (!outsideBoard && diag != INVALID_PANEL) {
                addMouseListener(new MouseListener() {

                    // Handles the board interaction.
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        slotClicked(SlotPanel.this);
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
        }

        /**
         * Paints the panel according to the game state.
         *
         * @param graphics Graphics used to paint.
         */
        @Override
        public void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics;
            Theme theme = displayData.getTheme();

            // The background of every Panel.
            setBackground(theme.getColor(Theme.BOARD_BACKGROUND));

            // Forms a rhombus.
            Dimension d = getSize();
            int[] x = {0, d.width / 2, d.width, d.width / 2};
            int[] y = {d.height / 2, 0, d.height / 2, d.height};

            // Panels outside the Board only need the background.
            if (!outsideBoard) {
                Color color;
                GradientPaint paint = null;

                // Select color.
                if ((!displayData.getBoard().isValidPosition(row, diag)
                        && displayData.getBoard().isValidTarget(row, diag))
                        || row == 0
                        || row == displayData.getBoard().getSize() + 1) {
                    color = theme.getColor(Theme.ONLY_TARGET);
                } else {
                    color = theme.getColor(Theme.INTERIOR);
                }
                g.setColor(color);

                // Paint rhombus.
                g.fillPolygon(x, y, x.length);

                // Select ball colors.
                if (displayData.getBoard().isValidTarget(row, diag)) {
                    if (displayData.getBoard().isValidPosition(row, diag)) {
                        abalone.model.Color ballColor = displayData.
                                getBoard().getSlot(row, diag);

                        // No Ball (null)
                        if (ballColor == null) {
                            color = theme.getColor(Theme.INTERIOR_NO_BALL);

                        // Not stating players ball (White).
                        } else if (ballColor == abalone.model.Color.White) {
                            paint = new GradientPaint(
                                    d.width / 6f, d.height / 2f,
                                    theme.getColor(
                                        Theme.NOT_STARTING_PLAYER_BALL_FIRST),
                                    d.width, d.height / 2f,
                                    theme.getColor(
                                        Theme.NOT_STARTING_PLAYER_BALL_SECOND));

                        // Stating players ball (Black)
                        } else if (ballColor == abalone.model.Color.Black) {
                            paint = new GradientPaint(
                                    d.width / 6f, d.height / 2f,
                                    theme.getColor(
                                        Theme.STARTING_PLAYER_BALL_FIRST),
                                    d.width * 1.2f, d.width / 2f,
                                    theme.getColor(
                                        Theme.STARTING_PLAYER_BALL_SECOND));
                        }

                    // Target only valid slot never contains a ball.
                    } else {
                        color = theme.getColor(Theme.ONLY_TARGET_NO_BALL);
                    }
                }

                // Select paint if possible.
                if (paint != null) {
                    g.setColor(null);
                    g.setPaint(paint);
                } else {
                    g.setColor(color);
                }

                /*
                Integer division always rounds down, and makes the ball
                appear noticeable off center at small window Sizes.
                 */
                double xCompute = ((double) d.width) / 6.0;
                double yCompute = ((double) d.height) / 6.0;
                int xPos = (int) Math.round(xCompute);
                int yPos = (int) Math.round(yCompute);
                int width = (int) Math.round(xCompute * 4.0);
                int height = (int) Math.round(yCompute * 4.0);

                // Paint ball.
                g.fillOval(xPos, yPos, width, height);

                // Paint highlighted.
                if (isHighlighted == this) {
                    g.setColor(theme.getColor(Theme.HIGHLIGHTED));
                    int strokeSize = INTERFACE_SIZE / 15;
                    g.setStroke(new BasicStroke(strokeSize));
                    g.drawOval(xPos, yPos, width, height);
                }
            }
        }

        /**
         * Repaints the SlotPanel.
         */
        @Override
        public void update() {
            repaint();
        }
    }

    /**
     * Models the bottom panel of the abalone game panel. Is just a
     * container that divides the bottom panel into three separate panels.
     */
    private final class BottomPanel extends JPanel {
        /*
        Could be done inside a method, but since I used a class
        for every other Component I didn't want to break the style.
         */

        /**
         * Serial ID of this.
         */
        @Serial
        private static final long serialVersionUID = 1L;

        private BottomPanel() {
            setLayout(new BorderLayout());
            setPreferredSize(INTERFACE_PANEL_SIZE);
            add(new HumanBallPanel(), BorderLayout.EAST);
            add(new MachineBallPanel(), BorderLayout.WEST);
            add(new BottomCenterPanel(), BorderLayout.CENTER);
        }
    }

    /**
     * Model the bottom center panel. Contains four
     * buttons used to interact with the game.
     */
    private final class BottomCenterPanel extends JPanel {

        /**
         * Serial ID of this.
         */
        @Serial
        private static final long serialVersionUID = 1L;

        private BottomCenterPanel() {
            setBackground(MENU_COLOR);
            setLayout(new FlowLayout());

            // New game Button.
            add(new GameButton("New", KeyEvent.VK_N, e -> guiNewGame()));

            // Switch Button.
            add(new GameButton("Switch", KeyEvent.VK_S, e -> guiSwitch()));

            // Undo Button.
            add(new UndoButton(e -> guiUndo()));

            // Quit Button.
            add(new GameButton("Quit", KeyEvent.VK_Q, e -> guiQuit()));
        }
    }

    /**
     * Models the shared functionalities of the human ball panel
     * and the machine ball panel.
     */
    private abstract class BallPanel extends JPanel implements Observer {

        /**
         * Serial ID of this.
         */
        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * Displays the amount of Balls left.
         */
        private final JLabel ballsLabel;

        /**
         * Creates a ball panel and sets default values.
         * Abstract class, used in super() calls only.
         */
        protected BallPanel() {
            setPreferredSize(INTERFACE_PANEL_SIZE);
            ballsLabel = new JLabel();
            ballsLabel.setFont(FONT);
            add(ballsLabel);
            displayData.addObserver(this);
            update();
        }

        /**
         * Updates the balls label.
         *
         * Updates the amount of ball a player has left. Adjusts the text color
         * depending on how many balls the has left he can lose before he loses
         * the game.
         *
         * @param color abalone.model.Color of the player whose panel this is.
         */
        protected void updateBallsLabel(abalone.model.Color color) {
        assert color != null;
            /*
            Computing balls left to lose instead of lost balls ensures that
            the code wont break even if {@code Board.ELIM} is changed.
             */
            int balls = displayData.getBoard().getNumberOfBalls(color);
            int ballsLeftToLose = Board.ELIM - (startBalls - balls);
            Color ballsForeground = switch (ballsLeftToLose) {
                case 0 -> Color.RED;
                case 1 -> Color.ORANGE;
                case 2 -> Color.YELLOW;
                default -> Color.GREEN;
            };
            ballsLabel.setForeground(ballsForeground);
            ballsLabel.setText(Integer.toString(balls));
        }
    }

    /**
     * Models the human ball panel.
     */
    private class HumanBallPanel extends BallPanel {

        /**
         * Serial ID of this.
         */
        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * Updates the HumanBallPanel.
         *
         * Updates the panel background depending on the color of the balls of
         * the human. Updates the balls label depending on the amount of balls
         * the human has left.
         */
        @Override
        public void update() {
            Theme theme = displayData.getTheme();
            switch (displayData.getBoard().getHumanColor()) {
                case White -> {
                    updateBallsLabel(abalone.model.Color.White);
                    setBackground(theme.getColor(
                            Theme.NOT_STARTING_PLAYER_BALL_FIRST));
                }
                case Black -> {
                    updateBallsLabel(abalone.model.Color.Black);
                    setBackground(theme.getColor(
                            Theme.STARTING_PLAYER_BALL_FIRST));
                }
                default -> throw new Error();
            }
        }
    }

    private class MachineBallPanel extends BallPanel {

        /**
         * Serial ID of this.
         */
        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * Updates the MachineBallPanel.
         *
         * Updates the panel background depending on the color of the balls of
         * the machine. Updates the balls label depending on the amount of balls
         * the machine has left.
         */
        @Override
        public void update() {
            Theme theme = displayData.getTheme();
            switch (displayData.getBoard().getHumanColor()) {
                case White -> {
                    updateBallsLabel(abalone.model.Color.Black);
                    setBackground(theme.getColor(
                            Theme.STARTING_PLAYER_BALL_FIRST));
                }
                case Black -> {
                    updateBallsLabel(abalone.model.Color.White);
                    setBackground(theme.getColor(
                            Theme.NOT_STARTING_PLAYER_BALL_FIRST));
                }
                default -> throw new Error();
            }
        }
    }

    /**
     * Models a button of this game.
     * Sets some default values.
     */
    private static class GameButton extends JButton {

        /**
         * Serial ID of this.
         */
        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * Creates a new GameButton.
         * Allows for efficient adjustment of relevant attributes.
         *
         * @param name Text of the button.
         * @param keyEvent Mnemonic of the button.
         * @param actionListener ActionListener of the button.
         */
        GameButton(String name, int keyEvent, ActionListener actionListener) {
            assert name != null && actionListener != null;
            setFocusable(false);
            setPreferredSize(new Dimension((INTERFACE_SIZE * 5) / 2,
                     (INTERFACE_SIZE * 4) / 5));
            setBackground(BUTTON_COLOR);
            setForeground(BUTTON_TEXT_COLOR);
            setFont(FONT);
            setText(name);
            setMnemonic(keyEvent);
            addActionListener(actionListener);
        }
    }

    /**
     * Models the update button.
     * Needs its own class since it isn't always enabled.
     */
    private class UndoButton extends GameButton implements Observer {

        /**
         * Serial ID of this.
         */
        @Serial
        private static final long serialVersionUID = 1L;

        UndoButton(ActionListener actionListener) {
            super("Undo", KeyEvent.VK_U, actionListener);
            displayData.addObserver(this);
            setEnabled(false);
        }

        /**
         * Updates whether or not the button is enabled.
         */
        @Override
        public void update() {
            setEnabled(!oldBoards.isEmpty());
        }
    }
}
