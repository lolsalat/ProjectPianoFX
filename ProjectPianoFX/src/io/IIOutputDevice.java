package io;

public interface IIOutputDevice {

	/**
	 * Sends a key-press
	 * @param key to press
	 * @param volume of key-press
	 */
	void pressKey(int key, int volume);
	
	/**
	 * Sends a key-release <br>
	 * @param key to release
	 * @param velocity how fast the key is released
	 */
	void releaseKey(int key, int velocity);

	/**
	 * Sends a key-release <br>
	 * @param key to release
	 */
	default void releaseKey(int key) {
		releaseKey(key, -1);
	}
}
