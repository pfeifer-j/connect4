package connect4.view;

import connect4.model.Board;
import connect4.model.Connect4;
import connect4.model.Coordinates2D;
import connect4.model.Player;
import connect4.view.observerPattern.C4Observable;
import java.util.Collection;

/**
 * Wrapper used to observe the {@code Connect4}-game and by notifying its
 * observers on state-change.
 */
public class BoardWrapper extends C4Observable {

    /**
     * The board which is wrapped by this class.
     */
    private Board board;

    /**
     * Constructs a new {@code Connect4}-instance using the default settings.
     */
    public BoardWrapper() {
        this.board = new Connect4();
        setChanged();
        notifyObservers();
    }

    /**
     * Creates a new board using a given firstPlayer and ensures that the
     * boardWrapper in the C4Panel can be {@code final}.
     */
    public void newBoard(Player firstPlayer) {
        this.board = new Connect4(firstPlayer);
        setChanged();
        notifyObservers();
    }

    /**
     * Sets a new board and informs all observers about the change. Necessary
     * for implementing the gameStack used for the UnDo-Button.
     *
     * @param board which will be observed.
     */
    public void setBoard(Board board) {
        this.board = board;
        setChanged();
        notifyObservers();
    }

    /**
     * Executes the getFirstPlayer()-method on the wrapped board. More
     * information can be found in the {@code Board}-interface.
     */
    public Player getFirstPlayer() {
        return board.getFirstPlayer();
    }

    /**
     * Executes the move()-method on the wrapped board. More information can be
     * found in the {@code Board}-interface.
     */
    public Board move(int col) {
        setChanged();
        notifyObservers();
        return board.move(col);
    }

    /**
     * Executes the setLevel()-method on the wrapped board. More information can
     * be found in the {@code Board}-interface.
     */
    public void setLevel(int level) {
        board.setLevel(level);
        setChanged();
        notifyObservers();
    }

    /**
     * Executes the isGameOver()-method on the wrapped board. More information
     * can be found in the {@code Board}-interface.
     *
     * @return true, if the game is over.
     */
    public boolean isGameOver() {
        return board.isGameOver();
    }

    /**
     * Executes the getWinner()-method on the wrapped board. More information
     * can be found in the {@code Board}-interface.
     *
     * @return the winner, if there is one.
     */
    public Player getWinner() {
        return board.getWinner();
    }

    /**
     * Executes the getWitness()-method on the wrapped board. More information
     * can be found in the {@code Board}-interface.
     *
     * @return a witness, if there is one.
     */
    public Collection<Coordinates2D> getWitness() {
        return board.getWitness();
    }

    /**
     * Executes the getSlot()-method on the wrapped board. More information can
     * be found in the {@code Board}-interface.
     *
     * @return the requested slot.
     */
    public Player getSlot(int row, int col) {
        return board.getSlot(row, col);
    }


    /**
     * Executes the clone()-method on the wrapped board. More information can be
     * found in the {@code Board}-interface.
     *
     * @return the cloned board.
     */
    public Board clone() {
        return board.clone();
    }
}
