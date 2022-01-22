package abalone.view;

import javax.swing.SwingUtilities;

/**
 * Utility class Main, starts the program by creating a AbaloneFrame.
 *
 * @version         1.0 8 Jul 2021
 * @author          Me
 */
public final class Main {

    /**
     * Private constructor to prevent the
     * creation of instances of utility class Main.
     */
    private Main() {
        throw new UnsupportedOperationException("Illegal call of constructor, "
                + "no instance of utility type class allowed");
    }

    /**
     * Main method, initializes the AbaloneFrame.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(AbaloneFrame::createAbaloneFrame);
    }
}
