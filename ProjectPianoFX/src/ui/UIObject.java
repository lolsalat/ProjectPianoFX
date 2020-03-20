package ui;

import javafx.scene.canvas.GraphicsContext;

public interface UIObject {

	void update();
	
	void render(GraphicsContext ct, double width, double height);
	
}
