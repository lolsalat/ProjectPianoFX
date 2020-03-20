package setting;

import java.util.LinkedList;
import java.util.List;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.GridPane;

public class SettingsTab extends Tab{
	
	public List<UISetting<?>> settings;
	private int y;
	private GridPane grid;
	private ScrollPane pane;
	
	private SettingsTab(String name, ScrollPane pane) {
		super(name, pane);
		this.setClosable(false);
		this.pane = pane;
		settings = new LinkedList<>();
		grid = new GridPane();
		pane.setContent(grid);
	}
	
	public SettingsTab(String name) {
		this(name, new ScrollPane());
	}
	
	public void add(TabPane pane) {
		pane.getTabs().add(this);
		this.pane.prefWidthProperty().bind(pane.widthProperty());
		this.pane.prefHeightProperty().bind(pane.heightProperty());
	}
	
	public void rebuild() {
		y = 0;
		for(UISetting<?> s : settings) {
			s.remove(grid);
			s.add(grid, y);
			y += s.getHeight();
		}
	}
	
	public boolean removeSetting(UISetting<?> setting) {
		y -= setting.getHeight();
		setting.remove(grid);
		return settings.remove(setting);
	}
	
	public void addSetting(UISetting<?> setting) {
		setting.add(grid, y);
		y += setting.getHeight();
		settings.add(setting);
	}
}
