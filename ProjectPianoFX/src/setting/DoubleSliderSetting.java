package setting;

import javafx.beans.property.Property;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;

public class DoubleSliderSetting extends UISetting<Number> {

	private Slider slider;
	private Label label;
	private Property<Number> bind;
	private Button reset;
	private Label name;
	private int max_length;
	
	private DoubleSliderSetting(String name, Slider slider) {
		super(name, slider.valueProperty());
		this.slider = slider;
	}
	
	public DoubleSliderSetting(Property<Number> bind, double min, double max, double defaultValue, String name) {
		this(name, new Slider());
		this.bind = bind;
		this.name = new Label();
		this.name.setText(name);
		max_length = (int)Math.log10(Math.max(Math.abs(min), Math.abs(max))) + 4;
		reset = new Button();
		label = new Label();
		label.setText(bind.getValue().toString());
		slider.setMin(min);
		slider.setMax(max);
		slider.setValue((Double)bind.getValue());
		reset.setOnMouseClicked(x -> slider.valueProperty().setValue(defaultValue));
		reset.setText("Reset");
	}
	
	@Override
	public void changed(Number newValue) {
		String text = newValue.toString();
		label.setText(text.substring(0,Math.min(max_length, text.length())));
		bind.setValue(newValue);
	}

	@Override
	public void add(GridPane pane, int y) {
		pane.add(name, 0, y);
		pane.add(slider, 1, y);
		pane.add(label, 2, y);
		pane.add(reset, 3, y);
	}

	@Override
	public void remove(GridPane pane) {
		pane.getChildren().remove(name);
		pane.getChildren().remove(slider);
		pane.getChildren().remove(label);
		pane.getChildren().remove(reset);
	}

	
}
