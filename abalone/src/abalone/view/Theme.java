package abalone.view;

import java.awt.Color;

/**
 * Theme contains the themes savable for the AbaloneGamePanel. And stores
 * the currently selected theme. If you want to add a new theme,
 * just add it following the pattern seen below.
 * No changes to other classes necessary, the window adjusts itself.
 *
 * @version         1.0 8 Jul 2021
 * @author          Me
 */
public final class Theme {

    /**
     * Index to access the fist color of the ball af the starting player.
     */
    public static final int STARTING_PLAYER_BALL_FIRST = 0;

    /**
     * Index to access the second color of the ball af the starting player.
     */
    public static final int STARTING_PLAYER_BALL_SECOND = 1;

    /**
     * Index to access the fist color of the ball af the non starting player.
     */
    public static final int NOT_STARTING_PLAYER_BALL_FIRST = 2;

    /**
     * Index to access the second color of the ball af the non starting player.
     */
    public static final int NOT_STARTING_PLAYER_BALL_SECOND = 3;

    /**
     * Index to access the color of an internal slot resp. panel.
     */
    public static final int INTERIOR = 4;

    /**
     * Index to access the color of an valid internal slots ball, if the slot
     * holds no ball.
     */
    public static final int INTERIOR_NO_BALL = 5;

    /**
     * Index to access the color of an valid external slot resp. panel. To these
     * slots can be moved to but not moved form. Slots that are directly
     * enclosed by two valid external slots are colored the same way to form a
     * cohesive board exterior.
     */
    public static final int ONLY_TARGET = 6;

    /**
     * Index to access the color of an valid external slots ball. To these slots
     * can be moved to but not moved form. There is no distinction between slots
     * holding balls and slots that arent holding balls, since external slots
     * cant hold balls.
     */
    public static final int ONLY_TARGET_NO_BALL = 7;

    /**
     * Index to access the color of the board background.
     */
    public static final int BOARD_BACKGROUND = 8;

    /**
     * Index to access the color a slot is highlighted with when clicked on.
     */
    public static final int HIGHLIGHTED = 9;

    /**
     * Index to access the modern theme.
     */
    public static final int MODERN_THEME = 0;

    /**
     * Index to access the classic theme.
     */
    public static final int CLASSIC_THEME = 1;

    /**
     * Array that stores the theme names. If want to add a new theme, add the
     * Theme name here and create appropriate public static final int constant.
     * Please mind that the order is relevant and must be matched appropriately.
     */
    private static final String[] THEME_NAMES = {"Modern", "Classic"};

    /**
     * Array that stores the themes data.
     */
    private static final Color[][] THEMES;

    /**
     * Theme that is currently selected.
     */
    private int selectedTheme = CLASSIC_THEME;

    static {

        // Add a new row if you want to add a theme.
        THEMES = new Color[2][10];

        // Modern theme
        THEMES[MODERN_THEME][STARTING_PLAYER_BALL_FIRST] =
                Color.getHSBColor(1, 1, 0.05f);
        THEMES[MODERN_THEME][STARTING_PLAYER_BALL_SECOND] =
                Color.getHSBColor(1, 1, 0.7f);
        THEMES[MODERN_THEME][NOT_STARTING_PLAYER_BALL_FIRST] =
                Color.getHSBColor(0.528f, 0.05f, 1);
        THEMES[MODERN_THEME][NOT_STARTING_PLAYER_BALL_SECOND] =
                Color.getHSBColor(0.514f, 1, 1);
        THEMES[MODERN_THEME][INTERIOR] = Color.WHITE;
        THEMES[MODERN_THEME][INTERIOR_NO_BALL] =
                Color.getHSBColor(0, 0, 0.75f);
        THEMES[MODERN_THEME][ONLY_TARGET] =
                Color.getHSBColor(1, 1, 0.25f);
        THEMES[MODERN_THEME][ONLY_TARGET_NO_BALL] =
                Color.getHSBColor(1, 1, 0.1f);
        THEMES[MODERN_THEME][BOARD_BACKGROUND] = Color.BLACK;
        THEMES[MODERN_THEME][HIGHLIGHTED] = Color.GREEN;

        // Classic theme
        THEMES[CLASSIC_THEME][STARTING_PLAYER_BALL_FIRST] =
                Color.getHSBColor(1, 1, 0.05f);
        THEMES[CLASSIC_THEME][STARTING_PLAYER_BALL_SECOND] =
                Color.getHSBColor(1, 1, 0.7f);
        THEMES[CLASSIC_THEME][NOT_STARTING_PLAYER_BALL_FIRST] =
                Color.getHSBColor(0.528f, 0.05f, 1);
        THEMES[CLASSIC_THEME][NOT_STARTING_PLAYER_BALL_SECOND] =
                Color.getHSBColor(0.514f, 1, 1);
        THEMES[CLASSIC_THEME][INTERIOR] =
                Color.getHSBColor(0.077f, 0.25f, 0.90f);
        THEMES[CLASSIC_THEME][INTERIOR_NO_BALL] =
                Color.getHSBColor(0, 0, 0.75f);
        THEMES[CLASSIC_THEME][ONLY_TARGET] =
                Color.getHSBColor(0.072f, 1, 0.55f);
        THEMES[CLASSIC_THEME][ONLY_TARGET_NO_BALL] =
                Color.getHSBColor(0.072f, 0.7f, 0.65f);
        THEMES[CLASSIC_THEME][BOARD_BACKGROUND] =
                Color.getHSBColor(0.072f, 1, 0.33f);
        THEMES[CLASSIC_THEME][HIGHLIGHTED] = Color.GREEN;

        // Add new theme here.
    }

    /**
     * Gets the currently selected theme.
     *
     * @return the currently selected theme.
     */
    public int getSelectedTheme() {
        return selectedTheme;
    }

    /**
     * Sets the currently selected theme. The class constants are meant to be
     * used here.
     *
     * @param selectedTheme Class constant that stores the index of a color.
     */
    public void setSelectedTheme(int selectedTheme) {
        if (selectedTheme < 0 || selectedTheme >= THEMES.length) {
        throw new IllegalArgumentException("Invalid theme index,"
                + " use class constants to access the themes.");
    }
        this.selectedTheme = selectedTheme;
    }

    /**
     * Gets a Color by its index. The class constants are meant to be
     * used here.
     *
     * @param colorIndex Class constant that stores the index of a color.
     * @return The color index by the class constant.
     */
    public Color getColor(int colorIndex) {
        if (colorIndex < 0 || colorIndex >= THEMES[0].length) {
            throw new IllegalArgumentException("Invalid color index,"
                    + " use class constants to access the colors.");
        }
        return THEMES[selectedTheme][colorIndex];
    }

    /**
     * Gets the number of themes available.
     *
     * @return the number of themes available.
     */
    public static int getSize() {
        return THEMES.length;
    }

    /**
     * Gets the name of the theme revert to by the index.
     *
     * @param themeIndex Index that revers to the theme.
     * @return Name of the theme revert to by themeIndex.
     */
    public static String getName(int themeIndex) {
        return THEME_NAMES[themeIndex];
    }
}
