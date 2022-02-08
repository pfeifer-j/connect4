package connect4;

import connect4.view.C4Frame;
import javax.swing.SwingUtilities;

/**
 * Entrypoint of the program, which starts the game.
 */
public final class Main {

    /**
     * Prevents the initialisation of the utility-class {@code Main}.
     *
     * @throws AssertionError on usage since the {@code Shell} must not be
     *         instantiated.
     */
    private Main() {
        throw new AssertionError("Suppress the use of this utility-class");
    }

    /**
     * Driver of the program.
     *
     * @param args not in use.
     */
    public static void main(String[] args) {

        // Used for thread-safety.
        SwingUtilities.invokeLater(C4Frame::getFrame);
    }
}
