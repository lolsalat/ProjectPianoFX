package setting;

import java.util.Collection;

import javafx.beans.property.Property;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class ComboboxSetting<E> extends UISetting<E>{

	private ComboBox<E> box;
	private Label name;
	private Property<E> bind;
	
	private ComboboxSetting(String name, ComboBox<E> box) {
		super(name, box.valueProperty());
		this.box = box;
	}
	
	public ComboboxSetting(Collection<E> items, Property<E> bind, String name) {
		this(name, new ComboBox<E>());
		this.bind = bind;
		box.getItems().addAll(items);
		box.setValue(bind.getValue());
		this.name = new Label();
		this.name.setText(name);
	}

	@Override
	public void changed(E newValue) {
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
