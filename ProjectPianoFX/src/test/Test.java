package test;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;

import file.IO;
import file.Keystroke;
import file.Part;
import file.Piece;
import file.PieceObserver;
import io.midi.MidiManager;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import observer.KeyObserver;
import observer.PieceChangedObserver;
import setting.CheckboxSetting;
import setting.ComboboxSetting;
import setting.DoubleSliderSetting;
import setting.SettingsTab;
import ui.edit.EditStage;
import ui.playing.PlayingScene;
import ui.playing.VideoPlayer;

public class Test extends Application{
	
	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		
		primaryStage.setWidth(screen.width / 2);
		primaryStage.setHeight(screen.height / 2);
		
		primaryStage.setX(screen.width / 4);
		primaryStage.setY(screen.height / 4);
		
		PlayingScene helper = new PlayingScene();
		
		
		Canvas canvas = new Canvas();

		helper.add(PlayingScene.UI, new FPSCounter(10, 20, Color.RED));
	
		
		VideoPlayer player = new VideoPlayer();
		
		Group root = new Group();
		root.getChildren().add(player.getView());
		root.getChildren().add(canvas);
		
		Scene scene = new Scene(root);

		
		player.bind(scene.widthProperty(), scene.heightProperty());

		canvas.widthProperty().bind(scene.widthProperty());
		canvas.heightProperty().bind(scene.heightProperty());
		
		primaryStage.setScene(scene);
		primaryStage.setOnCloseRequest(x -> System.exit(0));

//		pieceDebug(helper);
//		playInput(helper);
//		randomKeys(helper);
		

		helper.getPiece().addObserver(player);
		
		String p= "PokemonTheme";

		Piece piece = IO.readPiece(new File("music/" + p + "/score.info"));
		
		
		helper.getVisualizer().enable();
		helper.getAccompany().enable();
		helper.getWaiter().enable();
		helper.getLyrics().setEnabled(true);

		
		AnimationTimer timer = new AnimationTimer() {
			
			@Override
			public void handle(long now) {
				helper.update();
				helper.render(canvas.getGraphicsContext2D(), canvas.getWidth(), canvas.getHeight());
			}
			
		};

		primaryStage.setTitle("My Little Piano Ver 0.3 - Cookie build");
		primaryStage.show();	
		
		
		Stage settings = SettingsStage(helper);
		EditStage edit = new EditStage(primaryStage, helper);
		
		scene.setOnKeyPressed(x -> {
			
			switch(x.getCode()) {
			case F11:
				primaryStage.setFullScreen(true);
				break;
			case S:
				settings.show();
				settings.requestFocus();
				break;
			case E:
				edit.stage.show();
				break;
			default:
				break;
			}
				
		});
		

		helper.getPiece().setPiece(piece);
		timer.start();
		piece.play();
	}
	
	public static Stage SettingsStage(PlayingScene scene) {
		Stage settings = new Stage();
		
		settings.setWidth(600);
		settings.setHeight(400);
		
		HBox root = new HBox();
		
		SettingsTab piece = new SettingsTab("Piece");
		
		SimpleDoubleProperty spd = new SimpleDoubleProperty();
		spd.set(1);
		spd.addListener(x -> {
			scene.getPiece().getPiece().setSpeed((float)(double)spd.getValue());
		});
		DoubleSliderSetting speed = new DoubleSliderSetting(spd, 0.0, 4.0, 1.0, "Speed");
		piece.addSetting(speed);
		
		SimpleDoubleProperty vol = new SimpleDoubleProperty();
		vol.set(1);
		vol.addListener(x -> {
			scene.getPiece().getPiece().setVolume((float)(double)vol.getValue());
		});
		DoubleSliderSetting volume = new DoubleSliderSetting(vol, 0.0, 4.0, 1.0, "Volume");
		piece.addSetting(volume);
		
		SettingsTab midiTab = new SettingsTab("Midi");
		
		Collection<MidiDevice> midi_in = MidiManager.getMidiInDevices();
		SimpleObjectProperty<MidiDevice> min = new SimpleObjectProperty<MidiDevice>();
		min.addListener(new ChangeListener<MidiDevice>(){

			@Override
			public void changed(ObservableValue<? extends MidiDevice> observable, MidiDevice oldValue,
					MidiDevice newValue) {
				try {
					if(oldValue != null)
						oldValue.getTransmitter().close();
				} catch (MidiUnavailableException e) {
					e.printStackTrace();
				}
				MidiManager.connectMidiIn(newValue, scene.getPiano());
			}
		});
		min.setValue(midi_in.iterator().next());
		ComboboxSetting<MidiDevice> midiInSetting = new ComboboxSetting<MidiDevice>(midi_in, min, "Midi In");
		midiTab.addSetting(midiInSetting);
		
		Collection<MidiDevice> midi_out = MidiManager.getMidiOutDevices();
		SimpleObjectProperty<MidiDevice> mout = new SimpleObjectProperty<MidiDevice>();
		mout.addListener(new ChangeListener<MidiDevice>(){

			@Override
			public void changed(ObservableValue<? extends MidiDevice> observable, MidiDevice oldValue,
					MidiDevice newValue) {
				try {
					if(oldValue != null)
						oldValue.getReceiver().close();
				} catch (MidiUnavailableException e) {
					e.printStackTrace();
				}
				scene.getAccompany().setOut(MidiManager.createMidiOut(newValue, 1));
			}
		});
		mout.set(midi_out.iterator().next());
		ComboboxSetting<MidiDevice> midiOutSetting = new ComboboxSetting<MidiDevice>(midi_out, mout, "Midi Out");
		midiTab.addSetting(midiOutSetting);
		
		Collection<String> pieces = new ArrayList<>();
		
		Arrays.stream(new File("music").list()).forEach(pieces::add);
		SimpleObjectProperty<String> active = new SimpleObjectProperty<String>();
		active.addListener(new ChangeListener<String>(){

			@Override
			
			public void changed(ObservableValue<? extends String> observable, String oldValue,
					String newValue) {
				scene.getPiece().getPiece().stop();
				Piece piece = IO.readPiece(new File("music/" + newValue + "/score.info"));
				scene.getPiece().setPiece(piece);
				scene.getVisualizer().reset(piece);
				piece.play();
			}
		});
		ComboboxSetting<String> pieceSetting = new ComboboxSetting<String>(pieces, active, "Piece");
		piece.addSetting(pieceSetting);
		
		SettingsTab parts = new SettingsTab("Parts");
		
		scene.getPiece().addObserver(new PieceChangedObserver() {

			LinkedList<CheckboxSetting> visualize = new LinkedList<>();
			LinkedList<CheckboxSetting> wait = new LinkedList<>(); // TODO
			LinkedList<CheckboxSetting> accompany = new LinkedList<>();
			
			@Override
			public void pieceChanged(Piece from, Piece to) {
			
				visualize.forEach(parts::removeSetting);
				final LinkedList<CheckboxSetting> old = visualize;
				visualize = new LinkedList<>();
				
				to.getParts().forEach(x -> {
					Optional<CheckboxSetting> setting = old.stream().filter(a -> a.getName().equals("Visualize " + x.getName())).findAny();
					if(setting.isEmpty()) {
						SimpleBooleanProperty prop = new SimpleBooleanProperty(true);
						prop.addListener(new ChangeListener<Boolean>() {

							@Override
							public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
									Boolean newValue) {
								if(newValue) {
									scene.getVisualizer().addPart(x.getName());
								} else {
									scene.getVisualizer().removePart(x.getName());
								}
							}
							
						});
						CheckboxSetting s = new CheckboxSetting(prop, "Visualize " + x.getName());

						scene.getVisualizer().addPart(x.getName());
						visualize.add(s);
					} else {
						visualize.add(setting.get());
					}
				});
				
				visualize.forEach(parts::addSetting);
				old.clear();
				
				accompany.forEach(parts::removeSetting);
				final LinkedList<CheckboxSetting> old1 = accompany;
				accompany = new LinkedList<>();
				
				to.getParts().forEach(x -> {
					Optional<CheckboxSetting> setting = old1.stream().filter(a -> a.getName().equals("Accompany " + x.getName())).findAny();
					if(setting.isEmpty()) {
						SimpleBooleanProperty prop = new SimpleBooleanProperty(true);
						prop.addListener(new ChangeListener<Boolean>() {

							@Override
							public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
									Boolean newValue) {
								if(newValue) {
									scene.getAccompany().addPart(x.getName());
								} else {
									scene.getAccompany().removePart(x.getName());
								}
							}
							
						});
						scene.getAccompany().addPart(x.getName());
						CheckboxSetting s = new CheckboxSetting(prop, "Accompany " + x.getName());
						accompany.add(s);
					} else {
						accompany.add(setting.get());
					}
				});
				
				accompany.forEach(parts::addSetting);
				old1.clear();
			
				wait.forEach(parts::removeSetting);
				final LinkedList<CheckboxSetting> old2 = wait;
				wait = new LinkedList<>();
				
				to.getParts().forEach(x -> {
					Optional<CheckboxSetting> setting = old2.stream().filter(a -> a.getName().equals("Train " + x.getName())).findAny();
					if(setting.isEmpty()) {
						SimpleBooleanProperty prop = new SimpleBooleanProperty();
						prop.addListener(new ChangeListener<Boolean>() {

							@Override
							public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
									Boolean newValue) {
								if(newValue) {
									scene.getWaiter().addPart(x.getName());
								} else {
									scene.getWaiter().removePart(x.getName());
								}
							}
							
						});
						CheckboxSetting s = new CheckboxSetting(prop, "Train " + x.getName());
						wait.add(s);
					} else {
						wait.add(setting.get());
					}
				});
				
				wait.forEach(parts::addSetting);
				old2.clear();
				
				
				parts.rebuild();
			}
			
		});
		
		TabPane pane = new TabPane();
		
		pane.prefWidthProperty().bind(settings.widthProperty());
		pane.prefHeightProperty().bind(settings.heightProperty());
		
		root.getChildren().add(pane);
		
		piece.add(pane);
		midiTab.add(pane);
		parts.add(pane);
		
		Scene s = new Scene(root);
		
		settings.setScene(s);
		
		return settings;
	}
	
	public static void randomKeys(PlayingScene scene) {
		Timer t = new Timer();
		t.schedule(new TimerTask() {

			List<Integer> down = new LinkedList<Integer>();
			
			@Override
			public void run() {
				int key = new Random().nextInt(88);
				down.add(key);
				scene.getPiano().pressKey(key, 50);
			
				if(down.isEmpty())
					return;
				
				if(new Random().nextBoolean() || down.size() >= 5) {
					scene.getPiano().releaseKey(down.remove(new Random().nextInt(down.size())));
				}
			}
			
		}, 1000, 200);
	}
	
	public static void playInput(PlayingScene scene) {
		scene.getPiano().addObserver(new KeyObserver() {

			@Override
			public void keyPressed(int key, int volume) {
				scene.getAccompany().keyStrokeStart(new Keystroke(-1, -1, (byte)key, (byte)volume, "Playing", -1), -1);
			}

			@Override
			public void keyReleased(int key, int volume) {
				scene.getAccompany().keyStrokeEnd(new Keystroke(-1, -1, (byte)key, (byte)0, "Playing", -1), -1);
			}
			
		});
	}
	
	
	
	public static void pieceDebug(PlayingScene scene) {
		PieceObserver debug = new PieceObserver() {

			@Override
			public void stopped(boolean forced, Piece piece) {
				System.out.println("STOPPED");
				piece.reset();
				piece.play();
			}

			@Override
			public void reset(Piece piece) {
				System.out.println("RESET");
			}

			@Override
			public void started(Piece piece) {
				System.out.println("STARTED");
			}

			@Override
			public void paused(Piece piece) {
				System.out.println("PAUSED");
			}

			@Override
			public void resumed(Piece piece) {
				System.out.println("RESUMED");
			}

			@Override
			public void speedChanged(float before, float after, Piece piece) {
				System.out.println("SPEED CHANGED");
			}

			@Override
			public void jumped(int from, int to, Piece piece) {
				System.out.println("JUMPED");
			}

			@Override
			public void partAdded(Part part, Piece piece) {
				System.out.println("PART ADDED");
			}

			@Override
			public void partRemoved(Part part, Piece piece) {
				System.out.println("PART REMOVED");
			}

			@Override
			public void volumeChanged(float before, float after, Piece piece) {
				// TODO Auto-generated method stub
				
			}
			
		};
		scene.getPiece().addObserver(debug);
	}


}
