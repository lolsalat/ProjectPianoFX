package io.midi;

import java.util.ArrayList;
import java.util.Collection;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;

public class MidiManager {

	public static byte toMidiKey(int key) {
		return (byte)(key+21);
	}
	
	public static int fromMidiKey(byte key) {
		return key - 21;
	}
	
	public static void connectMidiIn(MidiDevice device, MidiInDevice in) {
		try {
			if(!device.isOpen())
				device.open();
			device.getTransmitter().setReceiver(in);
		} catch(MidiUnavailableException e) {
			throw new RuntimeException("Midi unavailable:", e);
		}
	}
	
	public static MidiOutDevice createMidiOut(MidiDevice device, int channel) {
		try {
			if(!device.isOpen())
				device.open();
			return new MidiOutDevice(device.getReceiver(), channel);
		} catch(MidiUnavailableException e) {
			throw new RuntimeException("Midi unavailable:", e);
		}
	}
	
	public static Collection<MidiDevice> getMidiOutDevices() {
		Info[] info = MidiSystem.getMidiDeviceInfo();
		
		ArrayList<MidiDevice> ret = new ArrayList<>();
		
		for(int i = 0; i < info.length; i++) {
			try {
				MidiDevice device = MidiSystem.getMidiDevice(info[i]);
				if(device.getMaxReceivers() != 0) {
					ret.add(device);
				}
			} catch (MidiUnavailableException e) {
				
			}
		}
		return ret;
	}
	
	public static Collection<MidiDevice> getMidiInDevices() {
		Info[] info = MidiSystem.getMidiDeviceInfo();
		
		ArrayList<MidiDevice> ret = new ArrayList<>();
		
		for(int i = 0; i < info.length; i++) {
			try {
				MidiDevice device = MidiSystem.getMidiDevice(info[i]);
				if(device.getMaxTransmitters() != 0) {
					ret.add(device);
				}
			} catch (MidiUnavailableException e) {
				
			}
		}
		return ret;
	}
	
}
