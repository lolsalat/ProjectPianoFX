package ui.edit;

import file.Keystroke;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.GridPane;
import ui.playing.Bar;
import ui.playing.PlayingScene;

public class KeystrokeEdit extends GridPane{

	private Keystroke stroke;
	private Keystroke original;
	
	private Spinner<Integer> start, end, volume, key;
	private Button reset, delete;
	private Bar bar;
	private ComboBox<String> part;
	
	public KeystrokeEdit(PlayingScene scene) {
		super();
		start = createLabeledSpinner("start", 0, 0, (a,b,c) -> {
			if(!this.isVisible())
				return;
			if(stroke.end <= stroke.start)
				return;
			stroke.start = start.getValue();
			scene.getPiece().getPiece().editKeystroke(stroke.part, stroke.offset, stroke);
		});
		end = createLabeledSpinner("end", 0, 1, (a,b,c) -> {
			if(!this.isVisible())
				return;
			if(stroke.end <= stroke.start)
				return;
			stroke.end = end.getValue();
		});
		volume = createLabeledSpinner("volume", 0, 2, (a,b,c) -> {
			if(!this.isVisible())
				return;
			if(c > 127 || c <= 0)
				return;
			stroke.volume = (byte)(int)c;
		});
		key = createLabeledSpinner("key", 0, 3, (a,b,c) -> {
			if(!this.isVisible())
				return;
			if(c > 88 || c <= 0)
				return;
			stroke.key = (byte)(int)c;
		});
		part = createLabeled("part", new ComboBox<String>(), 0, 4);
		part.valueProperty().addListener((a,b,c) -> {
			if(!this.isVisible())
				return;
			String oldPart = stroke.part;
			stroke.part = c;
			scene.getPiece().getPiece().editKeystroke(oldPart, stroke.offset, stroke);
			scene.remove(PlayingScene.BARS, bar);
			bar = scene.getVisualizer().addBar(stroke, scene.getPiece().getPiece().getPartIndex(stroke.part));
		});
		reset = new Button("reset");
		reset.setOnMouseClicked(x -> {
			if(!this.isVisible())
				return;
			scene.getPiece().getPiece().editKeystroke(stroke.part, stroke.offset, original);
			scene.remove(PlayingScene.BARS, bar);
			bar = scene.getVisualizer().addBar(original, scene.getPiece().getPiece().getPartIndex(stroke.part));
			setStroke(bar);
		});
		add(reset, 0,5);
		delete = new Button("delete");
		delete.setOnMouseClicked(x -> {
			if(!this.isVisible())
				return;
			KeystrokeEdit.this.setVisible(false);
			scene.getPiece().getPiece().removeKeystroke(stroke.part, stroke.offset);
			scene.remove(PlayingScene.BARS, bar);
		});
		add(delete, 1,5);
	}
	
	public void addPart(String name) {
		part.getItems().add(name);
	}
	
	public void removePart(String name) {
		part.getItems().remove(name);
	}
	
	public Bar getSelected() {
		return bar;
	}
	
	public <T extends Node> T createLabeled(String label, T elem, int x, int y) {
		Label l = new Label(label);
		add(l, x, y);
		add(elem, x+1, y);
		return elem;
	}
	
	public Spinner<Integer> createLabeledSpinner(String label, int x, int y, ChangeListener<Integer> listener) {
		Label l = new Label(label);
		Spinner<Integer> spinner = new Spinner<Integer>(0, Integer.MAX_VALUE, 0);
		spinner.valueProperty().addListener(listener);
		add(l, x, y);
		add(spinner, x+1, y);
		return spinner;
	}
	
	public void setStroke(Bar bar) {
		this.setVisible(false);
		this.stroke = bar.getKeystroke();
		this.original = stroke.copy();
		this.bar = bar;
		start.getValueFactory().setValue(stroke.start);
		end.getValueFactory().setValue(stroke.end);
		volume.getValueFactory().setValue((int) stroke.volume);
		key.getValueFactory().setValue((int) stroke.key);
		part.setValue(stroke.part);
	}
	
}


