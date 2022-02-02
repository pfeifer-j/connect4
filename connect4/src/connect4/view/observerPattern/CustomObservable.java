package connect4.view.observerPattern;

import java.util.Vector;

/**
 * Implements the {@code Observable}-part of the common observer-pattern.
 */
public class CustomObservable {

  private final Vector<CustomObserver> customObservers;
  private boolean changed = false;

  /**
   * Constructs a new {@code Observable}.
   */
  public CustomObservable() {
    customObservers = new Vector<>();
  }

  /**
   * Adds a new observer, who will be notified it this objects state changes.
   *
   * @param customObserver who is added and will be notified in the future.
   */
  public synchronized void addObserver(CustomObserver customObserver) {
    if (customObserver == null) {
      throw new NullPointerException();
    }
    if (!customObservers.contains(customObserver)) {
      customObservers.addElement(customObserver);
    }
  }

  /**
   * Notifies all added observers about the changed state.
   */
  public void notifyObservers() {

    Object[] arrLocal;

    synchronized (this) {
      if (!changed) {
        return;
      }
      arrLocal = customObservers.toArray();
      changed = false;
    }

    for (int i = arrLocal.length - 1; i >= 0; i--) {
      ((CustomObserver) arrLocal[i]).update();
    }
  }

  /**
   * After the state of an observable object is altered, it is marked changed.
   */
  protected synchronized void setChanged() {
    changed = true;
  }
}
