package abalone.model;

/**
 * Models a Slot of the board game Abalone.
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
public class Slot implements Cloneable {

    /**
     * Row of the Slot, one indexed.
     */
    private final int row;

    /**
     * Diagonal (first) of the Slot, one indexed.
     */
    private final int diag;

    /**
     * Ball that lies in the Slot.
     * Not in every Slot lies a Ball.
     */
    private Ball ball;

    /**
     * Creates a new Slot and stets the its attributes according to
     * the call parameters.
     *
     * @param ball The Ball that lies in this Slot.
     *             May be null.
     * @param row Row coordinate of the game slot represented by this.
     * @param diag Diagonal coordinate of the game slot represented by this.
     */
    public Slot(Ball ball, int row, int diag) {
        this.ball = ball;
        this.row = row;
        this.diag = diag;
    }

    /**
     * Shallow copies a Slot. A deep copy of Ball and Slot
     * would create an infinity loop, so both are cloned
     * shallow and must be relinked in a class that uses them.
     *
     * @return A shallow copy of this.
     */
    @Override
    public Slot clone() {
        Slot copy;
        try {
            copy = (Slot) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
        return copy;
    }

    /**
     * Gets row index of this.
     *
     * @return Row index of this.
     */
    public int getRow() {
        return row;
    }

    /**
     * Gets diagonal index of this.
     *
     * @return Diagonal index of this.
     */
    public int getDiag() {
        return diag;
    }

    /**
     * Gets the Ball of this.
     *
     * @return The Ball of this. My be null if the Slot is empty.
     */
    public Ball getBall() {
        return ball;
    }

    /**
     * Sets the Ball of this.
     *
     * @param ball The ball to set. My be null if the Slots ball is moved.
     */
    public void setBall(Ball ball) {
        this.ball = ball;
    }
}
