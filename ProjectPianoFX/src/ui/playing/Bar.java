package ui.playing;


import file.Keystroke;
import file.Piece;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import ui.Bounded;
import ui.RelativeBounds;
import ui.UIObject;

public class Bar implements UIObject, Bounded{

	public static float time = 2000F;
	
	private Keystroke keystroke;
	private Piece piece;
	private Color color;
	private UIKeyboard keyboard;
	private PlayingScene scene;
	
	public Bar(PlayingScene scene, Keystroke stroke, Piece piece, UIKeyboard keyboard, Color color) {
		this.piece = piece;
		keystroke = stroke;
		this.color = color;
		this.keyboard = keyboard;
		this.scene = scene;
	}
	
	public Keystroke getKeystroke() {
		return keystroke;
	}
	
	@Override
	public void update() {
		if(piece.isEnded())
			scene.removeLater(PlayingScene.BARS, this);
		
		if(piece.getPosition() >= keystroke.end)
			scene.removeLater(PlayingScene.BARS, this);
	}

	@Override
	public void render(GraphicsContext ct, double width, double height) {
		ct.setFill(color);
		RelativeBounds keyBounds = keyboard.getKeyBounds(keystroke.key);
		int pos = piece.getPosition();
		
		float h = keyBounds.y;
		
		float y1 = 1- (keystroke.end-pos) / time + 0.02F;
		float y0 = 1-(keystroke.start-pos) / time;
		
		y1 = Float.min(1, Float.max(0, y1)*h);
		y0 = Float.min(1, Float.max(0, y0)*h);
		
	
		
		ct.fillRoundRect(keyBounds.x * width, y1 * height, keyBounds.width * width, height*(y0-y1), width / 100, width / 100);
	}

	@Override
	public Rectangle getBounds() {
		RelativeBounds keyBounds = keyboard.getKeyBounds(keystroke.key);
		int pos = piece.getPosition();
		
		float h = keyBounds.y;
		
		float y1 = 1- (keystroke.end-pos) / time + 0.02F;
		float y0 = 1-(keystroke.start-pos) / time;
		
		y1 = Float.min(1, Float.max(0, y1)*h);
		y0 = Float.min(1, Float.max(0, y0)*h);
		
		return new Rectangle(keyBounds.x, y1, keyBounds.width, y0-y1);
	}

	@Override
	public boolean contains(double x, double y) {
		return getBounds().contains(x, y);
	}

	public Color getColor() {
		return color;
	}

	
}
