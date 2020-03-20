package file;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class Piece {

	private String name;
	private List<Part> parts;
	private float speed = 1F;
	private int position;
	private boolean paused;
	private boolean ended = true;
	private List<PieceObserver> observers;
	private String video;
	private int videoOffset;
	private float videoSpeed = 1F;
	private String background;
	private float volume = 1F;
	private Lyrics lyrics;
	
	private long lastUpdate;
	
	public Piece() {
		this("UNKNOWN PIECE");
	}
	
	public Piece(String name) {
		this.name = name;
		parts = Collections.synchronizedList(new ArrayList<>());
		observers = Collections.synchronizedList(new ArrayList<>());
	}
	
	public float getVolume() {
		return volume;
	}
	
	public void setVolume(float volume) {
		float before = this.volume;
		this.volume = volume;
		notifyObservers(x -> x.volumeChanged(before, volume, this));
	}
	
	public boolean isEnded() {
		return ended;
	}
	
	public void setVideo(String video) {
		this.video = video;
	}
	
	public String getVideo() {
		return video;
	}
	
	public boolean hasVideo() {
		return video != null;
	}
	
	public void setBackground(String background) {
		this.background = background;
	}
	
	public String getBackground() {
		return background;
	}
	
	public boolean hasBackground() {
		return background != null;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean hasLyrics() {
		return lyrics != null;
	}
	
	public void setLyrics(Lyrics lyrics) {
		this.lyrics = lyrics;
	}
	
	public Lyrics getLyrics() {
		return lyrics;
	}
	
	public void editKeystroke(String part, int index, Keystroke stroke) {
		removeKeystroke(part, index);
		addKeystroke(stroke);
	}

	public void addKeystroke(Keystroke stroke) {
		getPart(stroke.part).addKeystroke(stroke);
	}
	
	public void removeKeystroke(String part, int index) {
		getPart(part).removeKeystroke(index);
	}
	
	public void removeObservers(Collection<PieceObserver> observers) {
		this.observers.removeAll(observers);
	}
	
	public void addObservers(Collection<PieceObserver> observers) {
		this.observers.addAll(observers);
	}
	
	public void update() {
		
		long time = System.currentTimeMillis();
	
		
		if(!paused && !ended) {
			int delta = (int) ((time - lastUpdate)*speed);
			setPosition(position + delta, false);
			
			
			for(Part p : parts) {
				if(!p.isOver(position)) {
					lastUpdate = time;
					return;
				}
			
			}
			
			ended = true;
			notifyObservers(x -> x.stopped(false, this));
		}
		
		lastUpdate = time;
		
	}
	
	public void stop() {
		ended = true;
		setPosition(0);
		notifyObservers(x -> x.stopped(true, this));
	}
	
	public void play() {
		setPosition(0);
		notifyObservers(x -> x.started(this));
		ended = false;
		paused = false;
	}
	
	public void reset() {
		ended = true;
		setPosition(0);
		paused = false;
		notifyObservers(x -> x.reset(this));
	}
	
	public void resume(){
		setPaused(false);
		notifyObservers(x -> x.resumed(this));
	}
	
	public void pause() {
		setPaused(true);
		notifyObservers(x -> x.paused(this));
	}
	
	public boolean addObserver(PieceObserver observer) {
		return observers.add(observer);
	}
	
	public boolean removeObserver(PieceObserver observer) {
		return observers.remove(observer);
	}
	
	public int getPartIndex(String name) {
		for(int i = 0; i< parts.size(); i++)
			if(parts.get(i).getName().equals(name))
				return i;
		return -1;
	}
	
	public List<Part> getParts(){
		return parts;
	}
	
	public boolean isPaused() {
		return paused;
	}
	
	public Part getPart(int index) {
		return parts.get(index);
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		float before = this.speed;
		this.speed = speed;
		notifyObservers(x -> x.speedChanged(before, speed, this));
	}

	public int getPosition() {
		return position;
	}

	private void notifyObservers(Consumer<PieceObserver> c) {
		observers.forEach(c);	
	}
	
	private void setPosition(int position, boolean notifyObservers) {
		int before = this.position;
		
		if(position < before) {
			// go back in all parts
			for(Part p : parts) {
				int offset = p.getOffset();
				
				while(!p.hasKeystrokes(offset))
					offset --;
				
				while(offset > 0 && p.getKeystroke(offset).start >= position)
					offset --;
				
				// fixes off by one
				offset ++;
				
				p.setOffset(offset);
			}
			
		} else {
			// go forward in all parts
			for(Part p : parts) {
				
				int offset = p.getOffset();
				int count = p.getKeystrokesCount();
				while(offset < count && p.getKeystroke(offset).start < position)
					offset ++;
				
				p.setOffset(offset);
			}
		}
		
		
		this.lastUpdate = System.currentTimeMillis();
		this.position = position;
		if(notifyObservers) {
			notifyObservers(x -> x.jumped(before, position, this));
		}
	}
	
	public void setPosition(int position) {
		setPosition(position, true);
	}
	
	public boolean addPart(Part part) {
		if(hasPart(part.getName()))
			return false;
		notifyObservers(x -> x.partAdded(part, this));
		return parts.add(part);
	}
	
	public boolean removePart(String name) {
		if(!hasPart(name))
			return false;
		Part part = getPart(name);
		notifyObservers(x -> x.partRemoved(part, this));
		return parts.remove(part);
	}
	
	public boolean hasPart(String name) {
		for(Part p : parts)
			if(p.getName().equals(name))
				return true;
		return false;
	}
	
	public Part getPart(String name) {
		for(Part p : parts)
			if(p.getName().equals(name))
				return p;
		throw new IllegalArgumentException(String.format("Piece '%s' does not have a part with name '%s'", this.name, name));
	}
	
	public String getName() {
		return name;
	}

	public int getVideoOffset() {
		return videoOffset;
	}
	
	public void setVideoOffset(int offset) {
		this.videoOffset = offset;
	}
	
	public void setVideoSpeed(float speed) {
		videoSpeed = speed;
	}
	
	public float getVideoSpeed() {
		return videoSpeed;
	}
	
}
