package connect4.view;

import connect4.model.Board;
import connect4.view.observerPattern.C4Observable;

/**
 * Converts the board in {@code ConnectFour} to an observable object. This class
 * is used in the GUI to respond to change and therefore extends the custom
 * observable-pattern {@code C4Observable} used in this project.
 */
public class ObservedBoard extends C4Observable {

    /**
     * The board which contains all slots of the game and their owners.
     */
    private Board board;

    /**
     * Constructs a new {@code ObservableBoard}
     *
     * @param board which will be observed.
     */
    public ObservedBoard(Board board) {
        this.board = board;
    }

    /**
     * Gets the board.
     *
     * @return the observed board.
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Sets a new board and informs all observers about the change.
     *
     * @param board which will be observed.
     */
    public void setBoard(Board board) {
        setChanged();
        this.board = board;
        notifyObservers();
    }
}
