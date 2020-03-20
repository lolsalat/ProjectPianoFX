package piano;

import java.util.ArrayList;
import java.util.function.Consumer;

import io.midi.MidiInDevice;
import observer.KeyObserver;

public class Piano implements IPiano, MidiInDevice{

	/**
	 * Observers are managed in an ArrayList
	 */
	private final ArrayList<KeyObserver> observers;
	
	/**
	 * Volume of each key
	 */
	private final int[] keys;
	
	/**
	 * first key of this piano
	 */
	private final int key0;
	
	/**
	 * returns the name of a given key
	 */
	public static String getName(int key) {
		String k;
		switch(key % 12) {
		case(0):
			k= "A";
			break;
		case(1):
			k= "A#";
		break;
		case(2):
			k= "B";
		break;
		case(3):
			k= "C";
		break;
		case(4):
			k= "C#";
		break;
		case(5):
			k = "D";
		break;
		case(6):
			k = "D#";
		break;
		case(7):
			k = "E";
		break;
		case(8):
			k = "F";
			break;
		case(9):
			k = "F#";
		break;
		case(10):
			k = "G";
		break;
		case(11):
			k = "G#";
		break;
			default:
				return "UNKNOWN";
		}
		
		// TODO we might use the correct notation
		return k + (key / 12);
		
		
	}
	
	/**
	 * <b>Constructor</b>
	 * @param key0 lowest key on this piano
	 * @param keysCount amount of keys this piano has
	 */
	public Piano(int key0, int keysCount) {
		this.key0 = key0;
		keys = new int[keysCount];
		observers = new ArrayList<KeyObserver>();
	}
	
	/**
	 * Informs all registered observers
	 * @param action to execute for each registered observer
	 */
	private void notifyObservers(Consumer<KeyObserver> action) {
		observers.forEach(action);
	}
	
	@Override
	public void addObserver(KeyObserver observer) {
		observers.add(observer);
	}

	@Override
	public void removeObserver(KeyObserver observer) {
		observers.remove(observer);
		
	}

	@Override
	public int getVolume(int key) {
		if(key < key0 || key >= keys.length + key0)
			return 0;
		return keys[key];
	}

	@Override
	public void pressKey(int key, int volume) {
		if(key < key0 || key >= keys.length + key0)
			return;
		keys[key] = volume;
		notifyObservers(o -> o.keyPressed(key, volume));
	}

	@Override
	public void releaseKey(int key, int velocity) {
		if(key < key0 || key >= keys.length + key0)
			return;
		keys[key] = 0;
		notifyObservers(o -> o.keyReleased(key, velocity));
	}

	@Override
	public int getLowestKey() {
		return key0;
	}

	@Override
	public int getKeysCount() {
		return keys.length;
	}

	@Override
	public void clearObservers() {
		observers.clear();
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(int channel, int key, int volume) {
		this.pressKey(key, volume);
	}

	@Override
	public void keyReleased(int channel, int key, int volume) {
		this.releaseKey(key, volume);
	}

	public void releaseKeys() {
		for(int i = 0; i < keys.length; i++)
			releaseKey(i, 0);
	}

}
