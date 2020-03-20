package ui.edit;

import java.io.File;
import java.util.List;

import file.IO;
import file.Keystroke;
import file.Piece;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import observer.PieceChangedObserver;
import ui.UIObject;
import ui.playing.Bar;
import ui.playing.PlayingScene;

public class EditStage {

	public Stage stage;
	public PlayingScene scene;
	public GridPane pane;
	public Button save;
	public Button pause;
	public Button create;
	

	public KeystrokeEdit keystrokeEdit;
	
	public EditStage(Stage primary, PlayingScene scene) {
		stage = new Stage();
		stage.setAlwaysOnTop(true);
		pane = new GridPane();
		save = new Button("Save piece");
		pause = new Button("Pause");
		create = new Button("new");
		stage.setScene(new Scene(pane));
		pane.add(save, 0, 0);
		pane.add(pause, 1, 0);
		pane.add(create, 2, 0);
		keystrokeEdit = new KeystrokeEdit(scene);
		pane.add(keystrokeEdit, 0, 1, 4, 1);
		keystrokeEdit.setVisible(false);
		
		this.scene = scene;
		EventHandler<MouseEvent> eventHandler = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				double x = e.getX() / primary.getWidth();
				double y = e.getY() / primary.getHeight();
				
				List<UIObject> hit = scene.getObjects(PlayingScene.BARS, x, y);
				
				if(hit.isEmpty())
					return;
				
				keystrokeEdit.setStroke(((Bar)hit.get(0)));
				keystrokeEdit.setVisible(true);
								
			}
		};
		primary.addEventFilter(MouseEvent.MOUSE_CLICKED, eventHandler);
		save.setOnMouseClicked((x) -> {
			IO.savePiece(scene.getPiece().getPiece(), new File("music/" + scene.getPiece().getPiece().getName()));
		});
		create.setOnMouseClicked(x -> {
			Piece piece = scene.getPiece().getPiece();
			
			Bar b = keystrokeEdit.getSelected();
			Keystroke selected = b == null ? null : b.getKeystroke();
			
			
			Keystroke s = selected == null ?
					new Keystroke(piece.getPosition() + 100, piece.getPosition() + 500, (byte)0, (byte)40, piece.getPart(0).getName(), 0)
					: selected.end < piece.getPosition() ?
					new Keystroke(piece.getPosition() + 100, piece.getPosition() + 100 + selected.end-selected.start, selected.key, selected.volume, selected.part, 0)
					: selected.copy();
			piece.addKeystroke(s);
			Bar bar = scene.getVisualizer().addBar(s, piece.getPartIndex(s.part));
			
			keystrokeEdit.setStroke(bar);
			keystrokeEdit.setVisible(true);
		});
		pause.setOnMouseClicked(x -> {
			if(scene.getPiece().getPiece().isPaused()) {
				scene.getPiece().getPiece().resume();
				pause.setText("Pause");
			} else {
				scene.getPiece().getPiece().pause();
				pause.setText("Resume");
			}
		});
		scene.getPiece().addObserver(new PieceChangedObserver() {

			@Override
			public void pieceChanged(Piece from, Piece to) {
				keystrokeEdit.setVisible(false);
				if(from != null)
					from.getParts().forEach(x -> keystrokeEdit.removePart(x.getName()));
				to.getParts().forEach(x -> keystrokeEdit.addPart(x.getName()));
			}
			
		});
	}

	
}
