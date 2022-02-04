package connectFour.model;

/**
 * The class {@code Coordinates2D} is used to represent the position of a tile
 * in the {@code Board} of the game with its coordinates. Implements the
 * interface Comparable to ensure a simple way of sorting multiple coordinates
 * lexicographically.
 */
public class Coordinates2D implements Comparable<Coordinates2D> {

  private final int coordinateX;
  private final int coordinateY;

  /**
   * Constructs a new {@code Coordinates2D} element with the given values.
   *
   * @param coordinateX    The X-coordinate of the {@code Coordinates2D}.
   * @param getCoordinateY The Y-coordinate of the {@code Coordinates2D}.
   */
  public Coordinates2D(int coordinateX, int getCoordinateY) {
    this.coordinateX = coordinateX;
    this.coordinateY = getCoordinateY;
  }

  /**
   * Represents a {@code Coordinates2D} as a string.
   *
   * @return the {@code Coordinates2D} as a string.
   */
  public String toString() {
    return "(" + coordinateX + ", " + coordinateY + ")";
  }

  /**
   * Used to compare two {@code Coordinates2D}. Ensures a simple way of sorting
   * multiple {@code Coordinates2D}'s.
   *
   * @param other the {@code Coordinates2D} that {@code this} is compared to.
   * @return the value 0 if both {@code Coordinates2D} contain the same values,
   * the value -1 if the first {@code Coordinates2D} {@code this} is greater,
   * the value 1 if the second {@code Coordinates2D} {@code other} is greater.
   */
  @Override
  public int compareTo(Coordinates2D other) {
    int result = Integer.compare(this.coordinateX, other.coordinateX);
    if (result == 0) {
      result = Integer.compare(this.coordinateY, other.coordinateY);
    }
    return result;
  }
}