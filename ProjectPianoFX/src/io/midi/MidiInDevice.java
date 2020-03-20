package io.midi;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

public interface MidiInDevice extends Receiver {

	void keyPressed(int channel, int key, int volume);
	
	void keyReleased(int channel,  int key, int volume);
	
	@Override
	default void send(MidiMessage message, long ts) {
		byte[] data = message.getMessage();
		
		if((data[0] &0xFF) == 0x90) {
			ShortMessage msg = (ShortMessage)message;
			keyPressed(msg.getChannel(), msg.getData1()-21, msg.getData2());
		} else if((data[0] & 0xFF) == 0x80) {
			ShortMessage msg = (ShortMessage)message;
			keyReleased(msg.getChannel(), msg.getData1()-21, msg.getData2());
		}
		
	}
}
