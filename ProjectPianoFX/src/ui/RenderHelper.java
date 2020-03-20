package ui;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javafx.scene.canvas.GraphicsContext;

public class RenderHelper implements UIObject {

	private List<Set<UIObject>> layers;

	private Set<Entry<Integer, UIObject>> removeLater, addLater;

	public RenderHelper(int layers) {
		this.layers = Collections.synchronizedList(new ArrayList<>(layers));
		for (int i = 0; i < layers; i++)
			this.layers.add(new HashSet<>());
		removeLater = Collections.synchronizedSet(new HashSet<>());
		addLater = Collections.synchronizedSet(new HashSet<>());
	}

	public List<UIObject> getObjects(int layer, double x, double y) {
		LinkedList<UIObject> objs = new LinkedList<>();

		for (UIObject obj : layers.get(layer)) {
			if (obj instanceof Bounded && ((Bounded) obj).contains(x, y))
				objs.add(obj);

		}

		return objs;
	}

	public void cleanLayerLater(int layer) {
		layers.get(layer).forEach(x -> removeLater.add(new SimpleEntry<>(layer, x)));
	}

	public boolean addLater(int layer, UIObject object) {

		return addLater.add(new SimpleEntry<>(layer, object));

	}

	public boolean removeLater(int layer, UIObject object) {
		return removeLater.add(new SimpleEntry<>(layer, object));
	}

	public boolean remove(int layer, UIObject object) {
		return layers.get(layer).remove(object);
	}

	public boolean add(int layer, UIObject object) {
		return layers.get(layer).add(object);
	}

	@Override
	public void update() {
		layers.forEach(x -> x.forEach(UIObject::update));
		removeLater.forEach(x -> {
			layers.get(x.getKey()).remove(x.getValue());
		});
		removeLater.clear();
	}

	@Override
	public void render(GraphicsContext ct, double width, double height) {
		ct.clearRect(0, 0, width, height);
		layers.forEach(x -> x.forEach(y -> y.render(ct, width, height)));
	}
}
