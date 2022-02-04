package connectFour.view.observerPattern;

import java.util.Vector;

/**
 * Implements the custom version of the observable-pattern.
 */
public class C4Observable {

  private final Vector<C4Observer> c4Observers;
  private boolean modified = false;

  /**
   * Constructs a new {@code Observable}.
   */
  public C4Observable() {
    c4Observers = new Vector<>();
  }

  /**
   * Adds a new observer, who will be notified it this objects state changes.
   *
   * @param c4Observer who is added and will be notified in the future.
   */
  public synchronized void addObserver(C4Observer c4Observer) {
    if (c4Observer == null) {
      throw new NullPointerException();
    }
    if (!c4Observers.contains(c4Observer)) {
      c4Observers.addElement(c4Observer);
    }
  }

  /**
   * Notifies all added observers about the changed state.
   */
  public void notifyObservers() {

    Object[] observers;

    synchronized (this) {
      if (!modified) {
        return;
      }
      observers = c4Observers.toArray();
      modified = false;
    }

    for (Object observer : observers) {
      ((C4Observer) observer).update();
    }
  }

  /**
   * After the state of an observable object is altered, the {@code modified} is
   * marked as changed.
   */
  protected synchronized void setChanged() {
    modified = true;
  }
}
