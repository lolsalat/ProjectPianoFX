package file;

public interface PieceObserver {

	void stopped(boolean forced, Piece piece);
	
	void volumeChanged(float before, float after, Piece piece);
	
	void reset(Piece piece);
	
	void started(Piece piece);
	
	void paused(Piece piece);
	
	void resumed(Piece piece);
	
	void speedChanged(float before, float after, Piece piece);
	
	void jumped(int from, int to, Piece piece);
	
	void partAdded(Part part, Piece piece);
	
	void partRemoved(Part part, Piece piece);
	

}
