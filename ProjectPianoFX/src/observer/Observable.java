package observer;

public interface Observable<Observer> {

	/**
	 * Adds a observer
	 * @param observer
	 */
	void addObserver(Observer observer);
	
	/**
	 * Removes a observer
	 * @param observer
	 */
	void removeObserver(Observer observer);
	
	/**
	 * removes all observers
	 * @param observer
	 */
	void clearObservers();
	
}
