package file;

public class Keystroke {

	public int start;
	public int end;
	public byte key;
	public byte volume;
	public String part;
	public int offset;
	
	public Keystroke(int start, int end, byte key, byte volume, String part, int offset) {
		this.start = start;
		this.end = end;
		this.key = key;
		this.volume = volume;
		this.part = part;
		this.offset = offset;
	}
	
	@Override
	public String toString() {
		return String.format("Keystroke[start=%d, end=%d, key=%d, volume=%d, part=%s, offset=%d]", start, end, key, volume, part, offset);
	}
	
	public Keystroke copy() {
		return new Keystroke(start, end, key, volume, part, offset);
	}
}
