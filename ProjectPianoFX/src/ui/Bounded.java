package ui;

import javafx.scene.shape.Rectangle;

public interface Bounded {

	public Rectangle getBounds();
	
	public boolean contains(double x, double y);
	
}
