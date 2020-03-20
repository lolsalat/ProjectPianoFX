package file;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

public class Part implements IPart{

	private String name;
	private ArrayList<Keystroke> keystrokes;
	private int offset;
	private int end;
	
	public Part(String name, Collection<Keystroke> keystrokes) {
		this.name = name;
		this.keystrokes = new ArrayList<>();
		keystrokes.forEach(x -> {
			
			if(x.end > end)
				end = x.end;
			this.keystrokes.add(x);
			
		});
	}
	
	public void addKeystroke(Keystroke stroke) {
		int index = firstIndex(s -> s.start >= stroke.start);
		stroke.offset = index;
		keystrokes.add(index == -1 ? keystrokes.size() : index, stroke);

		
		index++;
		while(index < keystrokes.size()) {
			keystrokes.get(index++).offset++;
		}
	}
	
	public void removeKeystroke(int index) {
		keystrokes.remove(index);
		while(index < keystrokes.size()) {
			keystrokes.get(index).offset = index++;
		}
	}
	
	private Keystroke last() {
		if(keystrokes.isEmpty())
			return null;
		return keystrokes.get(keystrokes.size()-1);
	}
	
	private int lastIndex(Function<Keystroke, Boolean> func) {

		// TODO we can optimize this alot if needed
		
		for (int i = keystrokes.size()-1; i >= 0; i--) {
			if(func.apply(keystrokes.get(i)))
				return i;
		}
		
		return -1;
	}
	
	private int firstIndex(Function<Keystroke, Boolean> func) {
		
		// TODO we can optimize this alot if needed
		
		for (int i = 0; i < keystrokes.size(); i++) {
			if(func.apply(keystrokes.get(i)))
				return i;
		}
		
		return -1;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getLength() {
		Keystroke last = last();
		
		if(last == null)
			return 0;
		
		return last.end;
	}

	@Override
	public int getKeystrokesCount() {
		return keystrokes.size();
	}

	@Override
	public int after(int time) {
		return firstIndex(x -> x.start >= time);
	}

	@Override
	public int before(int time) {
		return lastIndex(x -> x.start <= time);
	}

	@Override
	public Keystroke getKeystroke(int index) {
		return keystrokes.get(index);
	}

	@Override
	public int getOffset() {
		return offset;
	}

	@Override
	public void setOffset(int offset) {
		this.offset = offset;
	}

	@Override
	public boolean hasKeystrokes(int offset) {
		return offset >= 0 && offset < keystrokes.size();
	}

	@Override
	public boolean isOver(int time) {
		return end <= time;
	}


}
