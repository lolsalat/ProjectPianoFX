package setting;


import javafx.beans.property.Property;
import javafx.scene.layout.GridPane;

public abstract class UISetting<E> extends Setting<E>{

	public UISetting(String name, Property<E> e) {
		super(name, e);
	}
	
	public int getHeight() {
		return 1;
	}
	
	public abstract void add(GridPane pane, int y);
	
	public abstract void remove(GridPane pane);
	
}
