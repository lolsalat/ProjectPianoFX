package test;


import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import ui.UIObject;

public class FPSCounter implements UIObject{

	private double x, y;
	private Color color;
	
	private long time = -1;
	private int frames;
	private int fps;
	
	public FPSCounter(double x, double y, Color color) {
		this.x = x;
		this.y = y;
		this.color = color;
	}

	@Override
	public void update() {
		long cur = System.currentTimeMillis();
		
		if(cur - time > 1000) {
			time = cur;
			fps = frames;
			frames = 0;
		}
		
	}

	@Override
	public void render(GraphicsContext ct, double width, double height) {
		ct.setStroke(color);
		ct.strokeText("FPS: " + fps, x, y);
		frames ++;
	}
	
}
