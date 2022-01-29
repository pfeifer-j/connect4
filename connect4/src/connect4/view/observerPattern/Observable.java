package connect4.view.observerPattern;

import java.util.Vector;

/**
 * Implements the {@code Observable}-part of the common observer-pattern.
 */
public class Observable {

  private final Vector<Observer> observers;
  private boolean changed = false;

  /**
   * Constructs a new {@code Observable}.
   */
  public Observable() {
    observers = new Vector<>();
  }

  /**
   * Adds a new observer, who will be notified it this objects state changes.
   *
   * @param observer who is added and will be notified in the future.
   */
  public synchronized void addObserver(Observer observer) {
    if (observer == null) {
      throw new NullPointerException();
    }
    if (!observers.contains(observer)) {
      observers.addElement(observer);
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
      arrLocal = observers.toArray();
      changed = false;
    }

    for (int i = arrLocal.length - 1; i >= 0; i--) {
      ((Observer) arrLocal[i]).update();
    }
  }

  /**
   * After the state of an observable object is altered, it is marked changed.
   */
  protected synchronized void setChanged() {
    changed = true;
  }
}
