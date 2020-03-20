package ui.edit;

import file.Lyrics.Sentence;
import file.Lyrics.Syllable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;

public class LyricsEdit extends GridPane{

	public Sentence curSentence;
	public ListView<Syllable> view;
	public ObservableList<Syllable> items;
	
	public LyricsEdit() {
		items = FXCollections.observableArrayList();
		view = new ListView<>(items);
	}
	
	public void setSentence(Sentence s) {
		
		curSentence = s;
		
		items.clear();
		
		for(Syllable syl : s.syllables)
			items.add(syl);
		
	}
	
}
