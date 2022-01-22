package abalone.model;

/**
 * BoardEvaluation is a wrapper type class, and as such holds
 * no class secret. The class is designed to wrap Game and its
 * evaluations value.
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
public final class BoardEvaluation {

    /**
     * Value that implies that no player has won jet.
     */
    public static final int NO_WIN = -1;

    /**
     * References a Game (Board) snapshot.
     */
    private final Game game;

    /**
     * Stores the evaluation of the Game snapshot.
     */
    private double boardRating;

    /**
     * Creates a new BordEvaluation and sets its attributes
     * according to its call parameters.
     *
     * @param game The Game that is to be stored resp. linked.
     * @param boardRating The beforehand evaluated value of the Game.
     */
    public BoardEvaluation(Game game, double boardRating) {
        this.game = game;
        this.boardRating = boardRating;
    }

    /**
     * Gets the game reference.
     *
     * @return The reference to linked Game.
     */
    public Game getGame() {
        return game;
    }

    /**
     * Gets the rating of game.
     *
     * @return Rating of game.
     */
    public double getBoardRating() {
        return boardRating;
    }

    /**
     * Sets the rating of game.
     *
     * @param boardRating New rating of game.
     */
    public void setBoardRating(double boardRating) {
        this.boardRating = boardRating;
    }
}
