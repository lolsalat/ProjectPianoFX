package ui.playing;

import file.Keystroke;
import file.Piece;
import file.PieceTraverser;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Visualizer extends PieceTraverser{

	private int time = 2000;
	private PlayingScene scene;
	
	public static final Color ERROR_COLOR = new Color(0, 1, 1, 0.7);
	public static final Color[] COLORS = new Color[] {
			new Color(0 , 0, 1, 0.7),
			new Color(1, 0, 0, 0.7),
			new Color(0, 1, 0, 0.7),
			new Color(1, 0, 1, 0.7)
	};
	
	public void setScene(PlayingScene scene) {
		this.scene = scene;
	}
	
	@Override
	public void reset(Piece piece) {
		scene.cleanLayerLater(PlayingScene.BARS);
		super.reset(piece);
	}
	
	public Bar addBar(Keystroke stroke, int part) {
		Color color;
		
		if(part == -1 || part >= COLORS.length)
			color = ERROR_COLOR;
		else
			color = COLORS[part];
		Bar bar = new Bar(scene, stroke, piece, scene.getKeyboard(), color);
		scene.add(PlayingScene.BARS, bar);
		return bar;
	}
	
	@Override
	public void keyStrokeStart(Keystroke stroke, int part) {
		addBar(stroke, part);
	}

	@Override
	public void keyStrokeEnd(Keystroke stroke, int part) {
		// TODO Auto-generated method stub
	}

	@Override
	public void update() {
		if(!enabled)
			return;
		int time = piece.getPosition() + this.time*2;
		if(next_stop <= time)
			updatePlaying(time);
		if(next_start <= time)
			updateUpcoming(time);
		if(next_stop <= time)
			updatePlaying(time);
	}

	@Override
	public void volumeChanged(float before, float after, Piece piece) {
		
	}

	@Override
	public void render(GraphicsContext ct, double width, double height) {
		
	}
}
