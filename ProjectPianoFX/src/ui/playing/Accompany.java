package ui.playing;

import file.Keystroke;
import file.Piece;
import file.PieceTraverser;
import io.midi.MidiOutDevice;
import javafx.scene.canvas.GraphicsContext;
import observer.PieceChangedObserver;

public class Accompany extends PieceTraverser implements PieceChangedObserver{

	private MidiOutDevice out;
	private boolean[] down;
	
	public Accompany() {
		super();
		down = new boolean[88];
	}
	
	public boolean keyDown(int i) {
		return down[i];
	}
	
	public void setOut(MidiOutDevice out) {
		this.out = out;
	}
	
	@Override
	public void reset(Piece piece) {
		super.reset(piece);
		if(out != null)
			out.reset();
		for(int i = 0; i < down.length; i++)
			down[i] = false;
	}
	
	public MidiOutDevice getOut() {
		return out;
	}
	
	@Override
	public void keyStrokeStart(Keystroke stroke, int part) {
		down[stroke.key] = true;
		if(out != null) {
			int volume = (int) (stroke.volume * piece.getVolume());
			out.pressKey(stroke.key, Integer.min(127, Integer.max(1, volume)));
		}
	}

	@Override
	public void keyStrokeEnd(Keystroke stroke, int part) {
		down[stroke.key] = false;
		if(out != null) {
			out.releaseKey(stroke.key, stroke.volume);
		}
	}

	@Override
	public void pieceChanged(Piece from, Piece to) {
		for(int i = 0; i < down.length; i++)
			down[i] = false;
	}
	
	@Override
	public void volumeChanged(float before, float after, Piece piece) {
		
	}

	@Override
	public void render(GraphicsContext ct, double width, double height) {
		
	}

}
