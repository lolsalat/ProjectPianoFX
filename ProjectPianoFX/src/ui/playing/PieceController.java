package ui.playing;

import java.util.ArrayList;

import file.Piece;
import file.PieceObserver;
import javafx.scene.canvas.GraphicsContext;
import observer.PieceChangedObserver;
import ui.UIObject;

public class PieceController implements UIObject {

	private Piece piece;
	
	private ArrayList<PieceObserver> observers;
	private ArrayList<PieceChangedObserver> changedObservers;
	
	public PieceController() {
		this.observers = new ArrayList<>();
		changedObservers = new ArrayList<>();
	}
	
	public boolean addObserver(PieceChangedObserver observer) {
		return changedObservers.add(observer);
	}
	
	public boolean removeObserver(PieceChangedObserver observer) {
		return changedObservers.remove(observer);
	}
	
	public boolean addObserver(PieceObserver observer) {
		return observers.add(observer);
	}
	
	public boolean removeObserver(PieceObserver observer) {
		return observers.remove(observer);
	}

	public void setPiece(Piece piece) {
		if(this.piece != null) {
			this.piece.stop();
			this.piece.removeObservers(observers);
		}
		
		Piece before = this.piece;
		if(before != null) {
			piece.setSpeed(before.getSpeed());
			piece.setVolume(before.getVolume());
		}
		this.piece = piece;
		piece.addObservers(observers);
		piece.reset();
		changedObservers.forEach(x-> x.pieceChanged(before, piece));
	}


	public Piece getPiece() {
		return piece;
	}

	@Override
	public void update() {
		if(piece == null)
			return;
		piece.update();
	}

	@Override
	public void render(GraphicsContext ct, double width, double height) {
		
	}

}
