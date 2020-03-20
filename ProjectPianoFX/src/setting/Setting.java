package setting;

import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public abstract class Setting<E> {

	protected Property<E> property;
	protected String name;
	
	public Setting(String name, Property<E> property) {
		this.property = property;
		this.name = name;
		property.addListener(new ChangeListener<E>() {

			@Override
			public void changed(ObservableValue<? extends E> observable, E oldValue, E newValue) {
				Setting.this.changed(newValue);
			}
			
		});
	}
	
	public String getName() {
		return name;
	}
	
	public Property<E> getProperty(){
		return property;
	}
	
	protected void changed(E newValue) {
		
	}
	
}
