package abalone.view.observerPattern;

/**
 * A class can implement the {@code Observer} interface when it
 * wants to be informed of changes in observable objects.
 *
 * @version         1.0 8 Jul 2021
 * @author          Me
 */
public interface Observer {

    /**
     * This method is called whenever the observed object is changed. An
     * application calls an {@code Observable} object's
     * {@code notifyObservers} method to have all the object's
     * observers notified of the change.
     */
    void update();
}
