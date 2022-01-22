package abalone.view;

import abalone.model.Board;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JRadioButtonMenuItem;
import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serial;

/**
 * AbaloneFrame models the frame of the abalone game window.
 *
 * The constructor is private to limit the amount of Frames to one if
 * sad behavior is desired. Use the method crateAbaloneFrame instead.
 *
 * @version         1.0 8 Jul 2021
 * @author          Me
 */
public final class AbaloneFrame extends JFrame {

    /**
     * Serial ID of this.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Shows whether or not the creation of multiple frames is allowed.
     */
    private static final boolean ALLOWS_MULTIPLE_INSTANCES = false;

    /**
     * Sets the largest board a menu entry is created for.
     */
    private static final int MAX_BOARD_SIZE = 25;

    /**
     * Sets the minimum difficulty level for witch an menu entry is created.
     */
    private static final int MIN_DIFFICULTY_LEVEL = 1;

    /**
     * Sets the maximum difficulty level for witch an menu entry is created.
     */
    private static final int MAX_DIFFICULTY_LEVEL = 4;

    /**
     * Stores whether or not an instance has been created.
     */
    private static boolean instanceCreated = false;

    /**
     * Stores an link to the game panel for later access.
     */
    private final AbaloneGamePanel gamePanel;

    /**
     * Private constructor to prevent the creation of multiple instances.
     * Use createAbaloneFrame instead.
     */
    private AbaloneFrame() {
        super("Abalone (lite)");
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        createWindowClosedListener();

        // Initialize game panel and menu bar.
        gamePanel = new AbaloneGamePanel(this);
        add(gamePanel);
        setJMenuBar(new MenuBar());

        /*
        Needed here since CenterPanel that calls this method when the board
        size is changed is not the last panel to be generated.
         */
        pack();
        setVisible(true);
        instanceCreated = true;
    }

    /**
     * This method replaces the constructor and enables the limitation of
     * creatable instances.
     *
     * @return A new crated AbaloneFrame.
     */
    public static JFrame createAbaloneFrame() {
        if (ALLOWS_MULTIPLE_INSTANCES || !instanceCreated) {
            return new AbaloneFrame();
        } else {
            throw new IllegalStateException("Instance already created,"
                    + " no further instances are allowed.");
        }
    }

    /**
     * Models the menu bar of the abalone game frame.
     */
    private final class MenuBar extends JMenuBar {
        private MenuBar() {

            // Create menu bar.
            setBackground(AbaloneGamePanel.MENU_COLOR);

            // Create menu menu.
            JMenu menu = new JMenu("Menu");
            menu.setMnemonic(KeyEvent.VK_M);
            JMenu levelMenu = new JMenu("Level");
            levelMenu.setMnemonic(KeyEvent.VK_L);
            createLevelRadioButtons(levelMenu, gamePanel.getDifficultyLevel());
            menu.add(levelMenu);
            JMenu sizeMenu = new JMenu("Board size");
            sizeMenu.setMnemonic(KeyEvent.VK_B);
            createSizeRadioButtons(sizeMenu, gamePanel.getBoardSize());
            menu.add(sizeMenu);

            // Create theme menu.
            JMenu theme = new JMenu("Theme");
            theme.setMnemonic(KeyEvent.VK_T);
            createThemeRadioButtons(theme,
                    gamePanel.getDisplayData().getTheme().getSelectedTheme());

            // Add menus.
            add(menu);
            add(theme);
        }
    }

    private void createThemeRadioButtons(JMenu parentMenu, int selected) {
        ButtonGroup group = new ButtonGroup();
        for (int i = 0; i < Theme.getSize(); i++) {
            JRadioButtonMenuItem radioButtonMenuItem = new JRadioButtonMenuItem(
                    Theme.getName(i), i == selected);
            parentMenu.add(radioButtonMenuItem);
            group.add(radioButtonMenuItem);

            // Necessary to access i from inner class.
            final int finalI = i;
            radioButtonMenuItem.addActionListener(e -> {
                if (e.getSource() instanceof JRadioButtonMenuItem) {
                    gamePanel.getDisplayData().setSelectedTheme(finalI);
                }
            });
        }
    }

    private void createLevelRadioButtons(JMenu parentMenu, int selected) {
        ButtonGroup group = new ButtonGroup();
        for (int i = MIN_DIFFICULTY_LEVEL; i <= MAX_DIFFICULTY_LEVEL; i++) {
            JRadioButtonMenuItem radioButtonMenuItem = new JRadioButtonMenuItem(
                    Integer.toString(i), i == selected);
            parentMenu.add(radioButtonMenuItem);
            group.add(radioButtonMenuItem);

            // Necessary to access i from inner class.
            final int finalI = i;
            radioButtonMenuItem.addActionListener(e -> {
                if (e.getSource() instanceof JRadioButtonMenuItem) {
                    gamePanel.setDifficultyLevel(finalI);
                }
            });
        }
    }

    private void createSizeRadioButtons(JMenu parentMenu, int selected) {
        ButtonGroup group = new ButtonGroup();
        for (int i = Board.MIN_SIZE; i <= MAX_BOARD_SIZE; i = i + 2) {
            JRadioButtonMenuItem radioButtonMenuItem = new JRadioButtonMenuItem(
                    Integer.toString(i), i == selected);
            parentMenu.add(radioButtonMenuItem);
            group.add(radioButtonMenuItem);

            // Necessary to access i from inner class.
            final int finalI = i;
            radioButtonMenuItem.addActionListener(e -> {
                if (e.getSource() instanceof JRadioButtonMenuItem) {
                    gamePanel.setBoardSize(finalI);
                }
            });
        }
    }

    @Deprecated
    private void createWindowClosedListener() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {

                // Kills machine move if it is running.
                gamePanel.killMachineMove();

                // Resets flag.
                instanceCreated = false;
                super.windowClosed(e);
            }
        });
    }
}
