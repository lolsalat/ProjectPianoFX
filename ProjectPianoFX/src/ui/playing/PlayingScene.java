package ui.playing;

import file.PieceObserver;
import observer.PieceChangedObserver;
import piano.Piano;
import ui.RenderHelper;

public class PlayingScene extends RenderHelper{

	public static final int BACKGROUND = 0;
	public static final int BARS = 1;
	public static final int KEYBOARD = 2;
	public static final int LYRICS = 3;
	public static final int UI = 4;
	public static final int UTIL = 5;
	
	private static final int LAYERS = 6;
	
	private Piano piano;
	private Accompany accompany;
	private PieceController piece;
	private UIKeyboard keyboard;
	private Visualizer visualizer;
	private Waiter waiter;
	private LyricsShower lyrics;
	
	public PlayingScene() {
		super(LAYERS);
		piano = new Piano(0, 88);
		accompany = new Accompany();
		piece = new PieceController();
		keyboard = new UIKeyboard(this);
		waiter = new Waiter();
		lyrics = new LyricsShower(this);
		
		piece.addObserver((PieceChangedObserver)accompany);
		piece.addObserver((PieceObserver)accompany);
		visualizer = new Visualizer();
		visualizer.setScene(this);
		piece.addObserver(visualizer);
		piano.addObserver(waiter);
		piece.addObserver(waiter);
		piece.addObserver((PieceChangedObserver)lyrics);
		piece.addObserver((PieceObserver)lyrics);
		
		add(KEYBOARD, keyboard);
		add(UTIL, accompany);
		add(UTIL, piece);
		add(UTIL, visualizer);
		add(UTIL, waiter);
		add(UTIL, lyrics);
	}

	public Waiter getWaiter() {
		return waiter;
	}
	
	public PieceController getPiece() {
		return piece;
	}
	
	public Accompany getAccompany() {
		return accompany;
	}

	public Piano getPiano() {
		return piano;
	}
	
	public UIKeyboard getKeyboard() {
		return keyboard;
	}

	public Visualizer getVisualizer() {
		return visualizer;
	}
	
	public LyricsShower getLyrics() {
		return lyrics;
	}
	
}
