package ui.playing;

import java.io.File;

import file.Part;
import file.Piece;
import file.PieceObserver;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

public class VideoPlayer extends MediaView implements PieceObserver{
	
	private float videoSpeed;
	
	private MediaPlayer player;
	private Media video;
	private MediaView view;
	private double accurity = 10;
	private String lastVideo;
	
	public VideoPlayer() {
		view = new MediaView();
		view.setPreserveRatio(false);
	}
	
	public void bind(ReadOnlyDoubleProperty width, ReadOnlyDoubleProperty height) {
		view.fitWidthProperty().bind(width);
		view.fitHeightProperty().bind(height);
	}
	
	public MediaView getView() {
		return view;
	}
	
	@Override
	public void stopped(boolean forced, Piece piece) {
		
	}

	@Override
	public void reset(Piece piece) {
		if(piece.hasVideo())
			try {
				// TODO protocol
				String vid = new File(piece.getVideo()).toURI().toString();
				if(video == null)
					video = new Media(vid);
				else {
					if(!vid.equals(lastVideo))
						synchronized(video) {
							video = new Media(vid);
						}
				}
				if(!vid.equals(lastVideo)) {
					if(player != null) {
						player.stop();
						player.dispose();
					}
					player = new MediaPlayer(video);
					player.setMute(true);
					view.setMediaPlayer(player);
				}
				
				lastVideo = vid;
				videoSpeed = piece.getVideoSpeed();
				player.setRate(videoSpeed * piece.getSpeed());
				player.setStartTime(Duration.millis(piece.getVideoOffset()));
			} catch(Exception e) {
				e.printStackTrace();
				System.err.printf("Could not initialize video '%s'\n", piece.getVideo());
			}
		else if(piece.hasBackground())
			try {
				player.stop();
				// TODO
				video = null;
			} catch(Exception e) {
				System.err.printf("Could not initialize Background '%s'\n", piece.getBackground());
			}
		else {
			player.stop();
			video = null;
		}
		if(video == null)
			return;
		player.seek(Duration.millis(piece.getPosition()*videoSpeed));
		player.pause();
	}

	@Override
	public void started(Piece piece) {
		if(video != null)
		player.play();
	}

	@Override
	public void paused(Piece piece) {
		if(video == null)
			return;
		player.pause();
	}

	@Override
	public void resumed(Piece piece) {
		if(video == null)
			return;
		if(Math.abs(player.getCurrentTime().toMillis() - piece.getVideoOffset() - piece.getPosition()*videoSpeed) > accurity)
			player.seek(Duration.millis(piece.getPosition()*videoSpeed + piece.getVideoOffset() + accurity));
		player.play();
	}

	@Override
	public void speedChanged(float before, float after, Piece piece) {
		if(video == null)
			return;
		player.setRate(after*videoSpeed);
		player.seek(Duration.millis(piece.getPosition()*videoSpeed + piece.getVideoOffset() + accurity));
	}

	@Override
	public void jumped(int from, int to, Piece piece) {
		if(video == null)
			return;
		player.seek(Duration.millis(piece.getPosition()*videoSpeed + piece.getVideoOffset() + accurity));
	}

	@Override
	public void partAdded(Part part, Piece piece) {
		
	}

	@Override
	public void partRemoved(Part part, Piece piece) {
		
	}

	@Override
	public void volumeChanged(float before, float after, Piece piece) {
		// TODO Auto-generated method stub
		
	}

}
