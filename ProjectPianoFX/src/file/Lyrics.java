package file;

public class Lyrics {
	
	private Sentence[] data;
	private int offset = 0;
	
	public Lyrics(Sentence[] data) {
		this.data = data;
	}
	
	public void setTime(int time) {
		// TODO we could optimize this a lot (binary search etc.)
		offset = 0;
		while(offset < data.length && data[offset].end < time)
			offset ++;
	}
	
	public Sentence[] getData() {
		return data;
	}
	
	public boolean nextSentence() {
		offset ++;
		return offset < data.length;
	}
	
	public Sentence getSentence() {
		if(offset >= data.length)
			return null;
		return data[offset];
	}
	
	public static class Sentence {
		
		public int start, end;
		public int characters;
		public Syllable[] syllables;
		
		public Sentence(Syllable[] syllables) {
			this.syllables = syllables;
			for(Syllable l : syllables)
				characters += l.text.length();
			start = syllables[0].start;
			end = syllables[syllables.length-1].end;
		}
		
	}
	
	public static class Syllable {
		
		public int start;
		public int end;
		public String text;
		
		
		public Syllable(int start, int end, String text) {
			this.start = start;
			this.end = end;
			this.text = text;
		}
		
	}
	
}
