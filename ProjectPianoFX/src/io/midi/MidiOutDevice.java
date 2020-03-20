package io.midi;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

import io.IIOutputDevice;

public class MidiOutDevice implements IIOutputDevice{

	private Receiver receiver;
	private int channel;
	private boolean[] down = new boolean[88];
	
	public boolean isDown(int key) {
		return down[key];
	}
	
	public MidiOutDevice(Receiver receiver, int channel) {
		this.receiver = receiver;
		this.channel = channel;
	}
	
	public void reset() {
		for(int i = 0; i < 88; i++)
			if(down[i])
				releaseKey(i,100);
	}
	
	@Override
	public void pressKey(int key, int volume) {
		try {
			down[key] = true;
			receiver.send(new ShortMessage(ShortMessage.NOTE_ON, channel, key+21, volume), -1);
		} catch (InvalidMidiDataException e) {
			throw new IllegalStateException("keystroke is invalid!", e);
		}
	}

	@Override
	public void releaseKey(int key, int velocity) {
		try {
			down[key] = false;
			receiver.send(new ShortMessage(ShortMessage.NOTE_OFF, channel, key+21, velocity), -1);
		} catch (InvalidMidiDataException e) {
			throw new IllegalStateException("key release is invalid!", e);
		}
	}
	
	@Override
	public void releaseKey(int key) {
		releaseKey(key, 64);
	}

}
