package ui.playing;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import piano.Piano;
import ui.RelativeBounds;
import ui.UIObject;

public class UIKeyboard implements UIObject{

	private int key0 = 0;
	private int keyE = 87;
	private float keyHeight = 0.15F;
	private PlayingScene scene;
	
	public UIKeyboard(PlayingScene scene) {
		this.scene = scene;
	}
	
	public int whiteKeys(int key) {
		int add = 0;
		
		switch(key % 12) {
		case(0):
		case(1):
			add = 0;
			break;
		
		case(2):
			add = 1;
			break;
		case(3):
		case(4):
			add = 2;
			break;
			
		case(5):
		case(6):
			add = 3;
			break;
		
		case(7):
			add = 4;
			break;
		case(8):
		case(9):
			add = 5;
			break;
		
		case(10):
		case(11):
			add = 6;
			break;
		}
		
		return (key / 12) * 7 + add;
	}
	
	public boolean isBlack(int key) {
		int mod = key % 12;
		
		return mod == 1 || mod == 4 || mod == 6 || mod == 9 || mod == 11;
	}
	
	public RelativeBounds getKeyBounds(int key) {
		int start = whiteKeys(key0);
		int width = whiteKeys(keyE) + 1 - start;
		int offset = whiteKeys(key) - start;
		
		RelativeBounds bounds = new RelativeBounds(offset / (float)width, 1-keyHeight, 1 / (float)width, keyHeight);
		
		if(isBlack(key)) {
			bounds.height *= 0.7F;
			bounds.x += bounds.width / 2F + bounds.width / 4F;
			bounds.width /= 2F;
		}
		
		return bounds;
	}
	

	@Override
	public void update() {
		
	}

	@Override
	public void render(GraphicsContext ct, double width, double height) {

		ct.setFill(Color.WHITE);
		ct.fillRect(0, (1.0-keyHeight)* width, height, height * keyHeight);
		
		Piano piano = ((PlayingScene)scene).getPiano();

		Waiter waiter = scene.getWaiter();
		
		Accompany accompany = scene.getAccompany();
		
		ct.setStroke(Color.BLACK);
		for(int key = key0; key <= keyE; key++) {
			
			
			// TODO this is overhead a f
			if(isBlack(key))
				continue;
			RelativeBounds bounds = getKeyBounds(key);

				double x = width*(bounds.x),
					y = height*(bounds.y),
					w = width*(bounds.width), 
					h = height*(bounds.height);
			
			ct.strokeRect(x, y, w, h);
			
			
			
			if(piano.keyDown(key)) {
				ct.setFill(Color.RED);
			} else if(waiter.isWaiting(key)) {
				ct.setFill(Color.LIMEGREEN);
			} else if(accompany.keyDown(key)) {
				ct.setFill(Color.DODGERBLUE);
			} else {
				ct.setFill(new Color(1,1,1,0.8));
			}

			ct.fillRect(x+1, y+1, w-2, h-2);
			
		}
		
		ct.setFill(Color.BLACK);
	for(int key = key0; key <= keyE; key++) {

			// TODO this is overhead a f
			if (!isBlack(key))
				continue;
			RelativeBounds bounds = getKeyBounds(key);

			if(piano.keyDown(key))
				ct.setFill(Color.ORANGERED);
			else if(waiter.isWaiting(key))
			    ct.setFill(Color.DARKGREEN);
			 else if(accompany.keyDown(key))
				 ct.setFill(Color.BLUE);
			
			ct.fillRect(width*(bounds.x), height*(bounds.y), width*(bounds.width),
					height*(bounds.height));
		
			ct.setFill(Color.BLACK);
		}
	}
	
}
