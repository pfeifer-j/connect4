package connectFour.model;

/**
 * Enum for representing the {@code connectFour.model.Player} and a players
 * {@code symbol} in the {@code Board}.
 */
public enum Player {

  /**
   * Used for representing an empty field in the {@code Board}.
   */
  EMPTY("."),

  /**
   * Used for representing the field of the human player in the {@code Board}.
   */
  HUMAN("X"),

  /**
   * Used for representing the field of the bot in the {@code Board}.
   */
  MACHINE("O");

  private final String symbol;

  Player(String player) {
    this.symbol = player;
  }

  /**
   * Getter for the symbol of a {@code connectFour.model.Player}.
   *
   * @return The symbol of a {@code connectFour.model.Player}.
   */
  public String getSymbol() {
    return symbol;
  }


  /**
   * Returns the opposite player. If {@code this} is {@code
   * connectFour.model.Player.Empty} return {@code Player.Empty}.
   *
   * @return the opposite player.
   */
  public Player opposite() {
    if (this.equals(Player.MACHINE)) {
      return Player.HUMAN;
    } else if (this.equals(Player.HUMAN)) {
      return Player.MACHINE;
    } else {
      return Player.EMPTY;
    }
  }
}