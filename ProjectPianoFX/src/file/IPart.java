package file;

public interface IPart {

	String getName();
	
	int getLength();
	
	int getKeystrokesCount();
	
	int after(int time);
	
	int before(int time);
	
	Keystroke getKeystroke(int index);
	
	int getOffset();
	
	void setOffset(int offset);
	
	boolean hasKeystrokes(int offset);
	
	boolean isOver(int time);
}
