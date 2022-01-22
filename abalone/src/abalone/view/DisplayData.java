package abalone.view;

import abalone.model.Board;
import abalone.view.observerPattern.Observable;

/**
 * This class makes display relevant changes to board and theme
 * observable. For more information on that read the super class javaDoc.
 *
 * Display irrelevant changes such as the change of
 * the difficulty level in board stay unnoticed.
 *
 * @version         1.0 8 Jul 2021
 * @author          Me
 */
public class DisplayData extends Observable {

    /**
     * The Board object that is observed.
     */
    private Board board;

    /**
     * The Theme object that is observed.
     */
    private final Theme theme = new Theme();

    /**
     * Gets the board that is observed.
     *
     * @return The observed board.
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Sets the parameter board as this board
     * and notifies the observers of the change.
     *
     * @param board Board to be set as this board.
     */
    public void setBoard(Board board) {
        setChanged();
        this.board = board;
        notifyObservers();
    }

    /**
     * Gets the observed Theme object theme.
     *
     * @return The observed Theme object theme.
     */
    public Theme getTheme() {
        return theme;
    }

    /**
     * Sets selectedTheme as the selectedTheme attribute of theme
     * and notifies the observers of the change.
     *
     * Use this method if you want the observers to take notice of the
     * change not the similarly call method of the Theme class.
     *
     * @param selectedTheme Index of the theme that is to be selected.
     *                      Use the class constants of Theme for this call.
     */
    public void setSelectedTheme(int selectedTheme) {
        setChanged();
        theme.setSelectedTheme(selectedTheme);
        notifyObservers();
    }
}
