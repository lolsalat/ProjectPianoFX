package setting;

import javafx.beans.property.Property;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class CheckboxSetting extends UISetting<Boolean>{

	private CheckBox box;
	private Label name;
	private Property<Boolean> bind;
	
	private CheckboxSetting(String name, CheckBox box) {
		super(name, box.selectedProperty());
		this.box = box;
	}
	
	public CheckboxSetting(Property<Boolean> bind, String name) {
		this(name, new CheckBox());
		this.bind = bind;
		box.setSelected(bind.getValue());
		this.name = new Label();
		this.name.setText(name);
	}

	@Override
	public void changed(Boolean newValue) {
		bind.setValue(newValue);
	}
	
	@Override
	public void add(GridPane pane, int y) {
		pane.add(name, 0, y);
		pane.add(box, 1, y);
	}

	@Override
	public void remove(GridPane pane) {
		pane.getChildren().remove(name);
		pane.getChildren().remove(box);
	}

}
