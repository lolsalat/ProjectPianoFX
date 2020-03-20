package file;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Map.Entry;

import ui.UIObject;

// TODO use synchronized list instead of synchronizing ourselfs
public abstract class PieceTraverser implements PieceObserver, UIObject{

	
	private ArrayList<String> parts;
	private ArrayList<Integer> indices;
	private ArrayList<Integer> offsets;
	private ArrayList<Entry<Integer,Keystroke>> playing;
	
	protected boolean active;
	protected boolean enabled;
	
	protected boolean takeOffsets;
	
	protected int next_stop;
	protected int next_start;
	
	protected int accurity = 0;
	
	protected Piece piece;
	
	public PieceTraverser() {
		parts = new ArrayList<>();
		indices = new ArrayList<>();
		offsets = new ArrayList<>();
		playing = new ArrayList<>();
	}
	
	public void clearParts() {
		parts.clear();
		indices.clear();
		offsets.clear();
		playing.forEach(x -> this.keyStrokeEnd(x.getValue(), x.getKey()));
		playing.clear();
	}
	
	public void setEnabled(boolean b) {
		enabled = b;
	}
	
	public void forceUpdate() {
		takeOffsets = true;
	}
	
	public void enable() {
		setEnabled(true);
	}
	
	public void disable() {
		setEnabled(false);
	}
	
	protected void updateUpcoming(int time) {
		if(!active)
			return;
		int best = Integer.MAX_VALUE;
		
		synchronized(indices) {
			
			for(int o = 0; o < indices.size(); o++) {
				
				int p = indices.get(o);
	
				Part part = piece.getPart(p);
				

				int offset = takeOffsets ? part.getOffset() : offsets.get(o);
				
				Keystroke stroke;
				
				while(part.hasKeystrokes(offset) && (stroke = part.getKeystroke(offset)).start - accurity <= time) {
					synchronized(playing) {
						if(stroke.end < next_stop)
							next_stop = stroke.end;
						playing.add(new SimpleEntry<>(p, stroke));
						keyStrokeStart(stroke, p);
					}
					offset ++;
				}
				
				if(part.hasKeystrokes(offset)) {
					stroke = part.getKeystroke(offset);
					if(stroke.start < best) {
						best = stroke.start;
					}
				}
				
				offsets.set(o, offset);
			}
			
		}

		
		takeOffsets = false;
		next_start = best;
	}
	
	protected void updatePlaying(int time) {
		if(!active)
			return;
		
		
		int best = Integer.MAX_VALUE;
		
		synchronized(playing) {
			for(int i = 0; i < playing.size(); i++) {
				Entry<Integer, Keystroke> entry = playing.get(i);
				Keystroke stroke = entry.getValue();
				if(stroke.end - accurity <= time) {
					playing.remove(i);
					keyStrokeEnd(stroke, entry.getKey());
					i--;
				} else {
					if(stroke.end < best) {
						best = stroke.end;
					}
				}
				
			}
		}
		next_stop = best;
	}
	
	public void removePart(String part) {
		synchronized(parts) {
			if(parts.remove(part) && piece != null) {
				synchronized (indices) {
					int partIndex = piece.getPartIndex(part);
					int index = indices.indexOf((Object)partIndex);
					indices.remove(index);
					offsets.remove(index);
					playing.stream().filter(x -> (x.getValue().part.equals(part))).forEach(x -> keyStrokeEnd(x.getValue(), index));
				}
			}
		}
	}
	
	public void addPart(String part) {
		synchronized(parts) {
			if(parts.contains(part))
				return;
			if(parts.add(part) && piece != null) {
				synchronized (indices) {
					int index = piece.getPartIndex(part);
					indices.add(index);
					offsets.add(piece.getPart(index).getOffset());
				}
			}
		}

		next_start = 0;
	}
	
	public void setParts(String... parts) {
		synchronized(indices) {
			indices.clear();
		}
		synchronized(offsets) {
			offsets.clear();
		}
		
		synchronized(this.parts) {
			this.parts.clear();
			
			for(int i = 0; i < parts.length; i++) {
				String part = parts[i];
				if(this.parts.add(part) && piece != null) {
					int index = piece.getPartIndex(part);
					if(index != -1) {
						synchronized(indices) {
							indices.add(index);
						}
						synchronized(offsets) {
							offsets.add(piece.getPart(index).getOffset());
						}
					}
				}
			}
			
		}

		next_start = 0;
	}
	
	@Override
	public void stopped(boolean forced, Piece piece) {
		synchronized(playing) {
			playing.forEach(x -> this.keyStrokeEnd(x.getValue(), x.getKey()));
			playing.clear();
		}
	}

	@Override
	public void reset(Piece piece) {
		active = false;
		next_start = 0;
		next_stop = 0;
		this.piece = piece;
		synchronized(offsets) {
			offsets.clear();
		}
		synchronized(indices) {
			indices.clear();
			
			for(String part : parts) {
				synchronized(offsets) {
					offsets.add(0);
				}
				int index = piece.getPartIndex(part);
				if(index != -1)
					indices.add(index);
			}
		}
	}

	@Override
	public void started(Piece piece) {
		this.piece = piece;
		active = true;
	}

	@Override
	public void paused(Piece piece) {
		
	}

	@Override
	public void resumed(Piece piece) {
		
	}

	@Override
	public void speedChanged(float before, float after, Piece piece) {
		
	}

	@Override
	public void jumped(int from, int to, Piece piece) {
		next_start = 0;
		next_stop = 0;
		
		/*
		 * TODO:
		 *  -> remove playing that have ended (or not yet started)
		 *  ->
		 */
		
		if(to != 0)
			takeOffsets = true;
	}

	@Override
	public void partAdded(Part part, Piece piece) {
		
	}

	@Override
	public void partRemoved(Part part, Piece piece) {
		
	}

	@Override
	public void update() {
		if(!enabled || piece.isEnded())
			return;
		int time = piece.getPosition()-accurity;
		if(next_stop <= time)
			updatePlaying(time);
		if(next_start <= time)
			updateUpcoming(time);
		if(next_stop <= time)
			updatePlaying(time);
	}

	public abstract void keyStrokeStart(Keystroke stroke, int part);
	
	public abstract void keyStrokeEnd(Keystroke stroke, int part);
	
}
