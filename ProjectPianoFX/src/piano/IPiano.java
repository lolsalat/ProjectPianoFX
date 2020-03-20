package piano;

import io.IIOutputDevice;
import io.IInputDevice;

public interface IPiano extends IInputDevice, IIOutputDevice {
	
	/**
	 * Gets the volume for a key
	 * @param key to get volume of
	 * @return volume
	 */
	int getVolume(int key);
	
	/**
	 * Gets the lowest key of this piano
	 * @return the lowest key
	 */
	int getLowestKey();
	
	/**
	 * Gets the amount of keys on this piano
	 * @return amount of keys
	 */
	int getKeysCount();
	
	/**
	 * Gets the highest key of this piano
	 * @return highest key
	 */
	default int getHighestKey() {
		return getLowestKey() + getKeysCount() - 1;
	}
	
	/**
	 * Checks whether a key is currently pressed <br>
	 * pressed means volume for the given key is bigger than zero
	 * @param key
	 * @return true iff key is pressed
	 */
	default boolean keyDown(int key) {
		return getVolume(key) > 0;
	}
	
}
