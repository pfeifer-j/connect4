package connectFour.view.observerPattern;

import java.util.Vector;

/**
 * Implements the observable-pattern.
 */
public class C4Observable {

  private final Vector<C4Observer> c4Observers;
  private boolean changed = false;

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

    Object[] arrLocal;

    synchronized (this) {
      if (!changed) {
        return;
      }
      arrLocal = c4Observers.toArray();
      changed = false;
    }

    for (int i = arrLocal.length - 1; i >= 0; i--) {
      ((C4Observer) arrLocal[i]).update();
    }
  }

  /**
   * After the state of an observable object is altered, it is marked changed.
   */
  protected synchronized void setChanged() {
    changed = true;
  }
}
