module ProjectPianoFX {
	exports test;
	exports ui.playing;
	exports io.midi;
	exports io;
	exports ui;
	exports piano;
	exports file;
	exports observer;
	exports util;
	
	requires transitive javafx.controls;
	requires transitive javafx.base;
	requires transitive javafx.graphics;
	requires transitive javafx.media;
	requires transitive java.desktop;
	requires transitive com.google.gson;
}