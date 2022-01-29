package connect4.view.observerPattern;

/**
 * Implements the {@code Observer}-part of the common observer-pattern.
 */
public interface Observer {

  /**
   * Called whenever the state of an observed object is changed.
   */
  void update();
}
