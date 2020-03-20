package ui.playing;

import file.Lyrics.Sentence;
import file.Part;
import file.Piece;
import file.PieceObserver;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import observer.PieceChangedObserver;
import ui.UIObject;

public class LyricsShower implements PieceObserver, PieceChangedObserver, UIObject{

	private PlayingScene scene;
	private Sentence curSentence;
	private boolean active;
	private int offset;
	
	public SimpleBooleanProperty enabled;
	
	public LyricsShower(PlayingScene scene) {
		this.scene = scene;
		this.enabled = new SimpleBooleanProperty();
	}

	
	public void setEnabled(boolean b) {
		enabled.set(b);
		if(b && active) {
			offset = 0;
			scene.getPiece().getPiece().getLyrics().setTime(scene.getPiece().getPiece().getPosition());
			curSentence = scene.getPiece().getPiece().getLyrics().getSentence();
		}
	}

	@Override
	public void update() {
		if(!active || !enabled.get())
			return;
		if(offset >= curSentence.syllables.length)
			offset = 0;
		
		int pos = scene.getPiece().getPiece().getPosition();
		
		while(curSentence.end < pos) {
			if(!scene.getPiece().getPiece().getLyrics().nextSentence()) {
				break;
			}
			offset = 0;
			curSentence = scene.getPiece().getPiece().getLyrics().getSentence();
		}
		
		while(curSentence.syllables[offset].end < pos) {
			offset ++;
			if(offset >= curSentence.syllables.length) {
				break;
			}
		}
	}


	@Override
	public void render(GraphicsContext ct, double width, double height) {
		if(!active || !enabled.get())
			return;
		// TODO better positioning
		
		Font before = ct.getFont();
		
		double fs = Math.min(width / 50, height / 30);
		
		ct.setFont(Font.font(fs));
		
		fs *= 2.0 / 3.0;
		
		double offset = width / 2 - fs * curSentence.characters / 2;
		
		for(int i = 0; i < curSentence.syllables.length; i++) {
			ct.setFill(Color.BLUE);
			if(i == this.offset) {
				ct.setFill(Color.RED);
			}
			ct.fillText(curSentence.syllables[i].text, offset,  2*fs);
			offset += curSentence.syllables[i].text.length() * fs;
		}
		
		ct.setFont(before);
		
	}


	@Override
	public void stopped(boolean forced, Piece piece) {
		active = false;
	}


	@Override
	public void volumeChanged(float before, float after, Piece piece) {
		
	}


	@Override
	public void reset(Piece piece) {
		if(enabled.get() && active) {
			piece.getLyrics().setTime(0);
			curSentence = piece.getLyrics().getSentence();
		}
		active = false;
	}


	@Override
	public void started(Piece piece) {
		active = piece.hasLyrics();
		if(enabled.get() && active) {
			piece.getLyrics().setTime(0);
			curSentence = piece.getLyrics().getSentence();
		}
	}


	@Override
	public void paused(Piece piece) {
		
	}


	@Override
	public void resumed(Piece piece) {
		
	}


	@Override
	public void speedChanged(float before, float after, Piece piece) {
		
	}


	@Override
	public void jumped(int from, int to, Piece piece) {
		active = piece.hasLyrics();
		if(enabled.get() && active) {
			piece.getLyrics().setTime(to);
			curSentence = piece.getLyrics().getSentence();
		}
	}


	@Override
	public void partAdded(Part part, Piece piece) {
		
	}


	@Override
	public void partRemoved(Part part, Piece piece) {
		
	}


	@Override
	public void pieceChanged(Piece from, Piece to) {
		active = to.hasLyrics();
	}
	
	
}
