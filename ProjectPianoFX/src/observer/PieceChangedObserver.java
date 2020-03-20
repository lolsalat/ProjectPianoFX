package observer;

import file.Piece;

public interface PieceChangedObserver {

	void pieceChanged(Piece from, Piece to);
	
}
