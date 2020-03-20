package observer;


public interface KeyObserver {

	/**
	 * Invoked every time a key is pressed
	 * @param key
	 * @param volume
	 */
	void keyPressed(int key, int volume);
	
	
	/**
	 * Invoked every time a key is released
	 * @param key
	 * @param volume
	 * @param sender whatever this KeyObserver observes
	 */
	void keyReleased(int key, int volume);
	
}
