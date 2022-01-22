package abalone.model;

/**
 * Models a ball of the board game Abalone.
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
public class Ball implements Cloneable {

    /**
     * Color of the Ball.
     */
    private final Color color;

    /**
     * Slot the Ball lies in.
     */
    private Slot slot;

    /**
     * Creates a new Ball. Since a Ball references a Slot,
     * and a Slot references a Ball. But both Objects cant be
     * created at the same time the Slot value of a Ball has
     * to be set after the creation of the Slot.
     *
     * @param color The Color of the Ball.
     */
    public Ball(Color color) {
        this.color = color;
    }

    /**
     * Shallow copies a Ball. A deep copy of Ball and Slot
     * would create an infinity loop, so both are cloned
     * shallow and must be relinked in a class that uses them.
     *
     * @return A shallow copy of this.
     */
    @Override
    public Ball clone() {
        Ball copy;
        try {
            copy = (Ball) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
        return copy;
    }

    /**
     * Gets the Color of the Ball.
     *
     * @return Color of the Ball.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Gets the Slot of the Ball.
     *
     * @return Slot of the Ball.
     */
    public Slot getSlot() {
        return slot;
    }

    /**
     * Sets the Slot of the Ball.
     *
     * @param slot The Slot that is to be set as the Slot of the Ball.
     */
    public void setSlot(Slot slot) {
        this.slot = slot;
    }
}
