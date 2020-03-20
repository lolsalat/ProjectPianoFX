package ui.playing;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import file.Keystroke;
import file.Piece;
import file.PieceTraverser;
import javafx.scene.canvas.GraphicsContext;
import observer.KeyObserver;

public class Waiter extends PieceTraverser implements KeyObserver {

	private Set<Integer> waitingFor;

	private int offset = 50;

	private int pauseAt = -1;

	private boolean[] keys = new boolean[88];

	public boolean isWaiting(int key) {
		return keys[key];
	}

	private void resetKeys() {
		for (int i = 0; i < keys.length; i++)
			keys[i] = false;
	}

	public Waiter() {
		super();
		this.accurity = -25;
		waitingFor = Collections.synchronizedSet(new HashSet<>());
	}

	public void clean() {
		waitingFor.clear();
		resetKeys();
	}

	@Override
	public void keyPressed(int key, int volume) {
		keys[key] = false;
		if (waitingFor.remove((Object) key) && waitingFor.isEmpty()) {
			pauseAt = -1;
			if (piece.isPaused())
				piece.resume();
		}
	}

	@Override
	public void keyReleased(int key, int volume) {

	}

	@Override
	public void keyStrokeStart(Keystroke stroke, int part) {
		waitingFor.add((int) stroke.key);
		keys[stroke.key] = true;

		if (pauseAt == -1) {
			pauseAt = stroke.start + offset;
		}
	}

	@Override
	public void keyStrokeEnd(Keystroke stroke, int part) {
		if (piece.getPosition() < stroke.end) {
			keyPressed(stroke.key, stroke.volume);
		}
	}

	@Override
	public void update() {
		super.update();
		if (!piece.isPaused() && pauseAt != -1 && piece.getPosition() > pauseAt) {
			if (!waitingFor.isEmpty()) {
				piece.pause();
			}
			pauseAt = -1;
		}
	}

	@Override
	public void volumeChanged(float before, float after, Piece piece) {

	}

	@Override
	public void render(GraphicsContext ct, double width, double height) {

	}

}
