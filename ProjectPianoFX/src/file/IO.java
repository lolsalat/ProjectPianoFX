package file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

import file.Lyrics.Sentence;
import file.Lyrics.Syllable;
import util.Utils;

public class IO {

	public static final byte[] PART_HEADER = {'P', 'A', 'R', 'T'};
	public static final byte[] PART_POSTFIX = {'M', 'L', 'P'};
	
	public static void savePiece(Piece piece, File file) {
		if(!file.exists())
			file.mkdirs();
		if(!file.isDirectory())
			throw new IllegalArgumentException("Piece needs a directory to be saved in, not a file");
		
		// save top level information
		try {
			BufferedWriter w = Files.newBufferedWriter(Paths.get(file.getAbsolutePath(), "score.info"), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
			
			w.write("# General information\n");
			
			w.write("name=" + piece.getName() + "\n");
			
			if(piece.hasVideo())
				w.write("video=" + new File(piece.getVideo()).getName() + "\n");
			
			if(piece.getVideoOffset() != 0)
				w.write("videooffset=" + piece.getVideoOffset() + "\n");
			
			if(piece.hasBackground())
				w.write("background=" + piece.getBackground() + "\n");
			
			w.write("\n");
			
			w.write("# Parts\n");
			piece.getParts().forEach(p -> {
				try {
					w.write("part=" + p.getName() + ":" + p.getName() + ".SPART\n");
					OutputStream out = new FileOutputStream(file.getAbsolutePath() + "/" + p.getName() + ".SPART");
					writePart(p, out);
					out.close();
				} catch (IOException e) {
					throw new RuntimeException("IO Exception while writing piece", e);
				}
				
			});
			
			if(piece.hasLyrics()) {
				w.write("\n");
				w.write("# Lyrics\n");
				w.write("lyrics=lyrics.txt");
				BufferedWriter wr = Files.newBufferedWriter(Paths.get(file.getAbsolutePath(), "lyrics.txt"), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
				writeLyrics(piece.getLyrics(),  wr);
				wr.close();
			}
			
			w.close();
		} catch(IOException e) {
			throw new RuntimeException("IO exception while saving piece", e);
		}
		
	}
	
	public static void writeLyrics(Lyrics lyrics, BufferedWriter out) throws IOException{
		for(Sentence s : lyrics.getData()) {
			out.write(s.start + " ");
			
			for(Syllable sl : s.syllables) {
				out.write(String.format("[%d:%d]%s", sl.start-s.start, sl.end-sl.start, sl.text));
			}
			
			out.write("\n");
		}
	}
	
	public static void writePart(Part part, OutputStream out) throws IOException {
		out.write(PART_HEADER);
		
		out.write(Utils.intToBytes(part.getKeystrokesCount()));
		
		for(int i = 0; i < part.getKeystrokesCount(); i++) {
			Keystroke stroke = part.getKeystroke(i);
			
			out.write(Utils.intToBytes(stroke.start));
			out.write(Utils.intToBytes(stroke.end));
			out.write(stroke.key);
			out.write(stroke.volume);
		}
		
		out.write(PART_POSTFIX);
	}
	
	public static Piece readPiece(File file) {
		String directory = file.getParentFile().getAbsolutePath();
		Piece piece = new Piece();
		
		try {
			Files.readAllLines(file.toPath(), StandardCharsets.UTF_8).forEach(
					line -> {
						try {
							parseLine(directory, line, piece);
						} catch(Exception e) {
							System.err.println(e.getMessage() + ", ignoring");
						}
					}
			);
		} catch (IOException e) {
			throw new RuntimeException(String.format("IO-Exception while reading file '%s'", file.getAbsolutePath()),e);
		}
		
		return piece;
	}
	
	public static void parseLine(String directory, String line, Piece piece) {
		if(line.isBlank() || line.startsWith("#"))
			return;
		
		int index = line.indexOf('=');
		
		if(index == -1) {
			throw new RuntimeException(String.format("Line '%s' does not contain '='", line));
		}
		
		String key = line.substring(0,index);
		String value = index == line.length() - 1 ? "" : line.substring(index+1);
		
		switch(key.trim().toLowerCase()) {
			case "name": 
				piece.setName(value);
				break;
			
			case "speed":
				piece.setSpeed(Float.parseFloat(value.trim()));
				break;
				
			case "part":{
				int index1 = value.indexOf(':');
				if(index1 == -1)
					throw new RuntimeException(String.format("Line '%s' does not contain an ':' in part value (Syntax: part=<name>:<path>)", line));
				
				String name = value.substring(0,index1);
				
				if(index1 == line.length())
					throw new RuntimeException(String.format("Line '%s' has an empty path for part (Syntax: part=<name>:<path>)", line));
				
				String path = value.substring(index1+1);
				
				try {
					InputStream in = Files.newInputStream(Paths.get(directory, path), StandardOpenOption.READ);
					piece.addPart(parsePart(name, in));
					in.close();
				} catch (IOException e) {
					throw new RuntimeException(String.format("Line '%s' has encountered an IO-Exception while reading part from '%s':", line, Paths.get(directory, path).toString()), e);
				}
				break;
			}
			
			case "video":{
				piece.setVideo(directory + "/" + value);
				break;
			}
			
			case "background":{
				piece.setBackground(directory + "/" + value);
				break;
			}
			
			case "videooffset":{
				try {
					piece.setVideoOffset(Integer.parseInt(value));
				} catch(NumberFormatException e) {
					throw new RuntimeException(String.format("Line '%s' contains an illegal video-offset value: '%s' is not an Integer", line, value), e);
				}
				break;
			}
			
			case "videospeed":{
				try {
					piece.setVideoSpeed(Float.parseFloat(value));
				} catch(NumberFormatException e) {
					throw new RuntimeException(String.format("Line '%s' contains an illegal video-speed value: '%s' is not an Float", line, value), e);
				}
				break;
			}
			
			case "lyrics":{
				
				
				try {
					InputStream in = Files.newInputStream(Paths.get(directory, value), StandardOpenOption.READ);
					piece.setLyrics(parseLyrics(in));
					in.close();
				} catch (Exception e) {
					throw new RuntimeException(String.format("Line '%s' has encountered an IO-Exception while reading lyrics from '%s':", line, Paths.get(directory, value).toString()), e);
				}
				break;
			}
			
			default : {
				throw new RuntimeException(String.format("Line '%s' has unknown key '%s'", line, key));
			}
				
		}
		
	}
	
	public static Lyrics parseLyrics(InputStream in) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		
		ArrayList<Sentence> sentences = new ArrayList<>();
		String line;
		
		while((line = reader.readLine()) != null) {
			int start = Integer.parseInt(line.substring(0, line.indexOf(' ')));
			
			String[] syls = line.split(Pattern.quote("["));
			
			Syllable[] syllables = new Syllable[syls.length-1];
			
			for(int i = 1; i < syls.length; i++) {
				String cur = syls[i];
				
				int index = cur.indexOf(',');
				
				int offset = Integer.parseInt(cur.substring(0, index));
				
				int index1 = cur.indexOf(']');
				
				int duration = Integer.parseInt(cur.substring(index +1, index1));
				syllables[i-1] = new Syllable(start + offset, start+ offset + duration, cur.substring(index1+1));	
			}
			sentences.add(new Sentence(syllables));
		}
		
		return new Lyrics(sentences.toArray(new Sentence[] {}));
	}
	
	public static Part parsePart(String name, InputStream stream) throws IOException {
		byte[] int_buffer = new byte[4];
		if(stream.read(int_buffer) < 4) {
			stream.close();
			throw new IllegalStateException("EOF reached while reading header");
		}
		
		if(!Arrays.equals(int_buffer, PART_HEADER)) {
			System.err.printf("Error while reading part %s: No fileheader present, is this really a part-file?\n", name);
		} else {
			if(stream.read(int_buffer) < 4) {
				stream.close();
				throw new IllegalStateException("EOF reached while reading keystrokes count");
			}
		}
		
		int count = Utils.bytesToInt(int_buffer);
		
		
		if(count < 0) {
			stream.close();
			throw new IllegalStateException(String.format("Keystroke count %d is invalid ... It's negative!", count));
		}
		
		ArrayList<Keystroke> keystrokes = new ArrayList<Keystroke>(count);
		
		
		byte[] keystroke_buffer = new byte[10];
		for(int i = 0; i < count; i++) {
			
			if(stream.read(keystroke_buffer) < 10) {
				System.err.printf("Error while reading part %s: Unexpected end of file; Ignoring\n", name);
				break;
			}
			
			keystrokes.add(parseKeystroke(keystroke_buffer, i, name));
		
		}
		
		int read = stream.read(int_buffer);
		
		if(read == 0) {
			System.err.printf("Error while reading part %s: Missing postfix; Ignoring\n", name);
		} else if(read < 3) {
			System.err.printf("Error while reading part %s: Broken postfix; Ignoring\n", name);
		} else {
			if(int_buffer[0] != PART_POSTFIX[0] || int_buffer[1] != PART_POSTFIX[1] || int_buffer[2] != PART_POSTFIX[2]) {
				System.err.printf("Error while reading part %s: Wrong postfix; Ignoring\n", name);
			}
			if(read > 3) {
				System.err.printf("Error while reading part %s: Data after postfix; Ignoring\n", name);
			}
		}
		keystrokes.sort((a,b)-> Integer.compare(a.start, b.start));
		for(int i = 0; i < keystrokes.size(); i++) {
			keystrokes.get(i).offset = i;
		}
		return new Part(name, keystrokes);
	}
	
	private static Keystroke parseKeystroke(byte[] buffer, int offset, String part){
		assert(buffer.length == 10);
		
		int start = Utils.bytesToInt(buffer[0], buffer[1], buffer[2], buffer[3]);
		int end = Utils.bytesToInt(buffer[4], buffer[5], buffer[6], buffer[7]);
		byte key = buffer[8], volume = buffer[9];
		
		if(start < 0) {
			System.err.printf("Error while reading keystroke: starttime %d is negative; skipping\n", start);
			return null;
		}
		if(end < 0) {
			System.err.printf("Error while reading keystroke: endtime %d is negative; skipping\n", start);
			return null;
		}
		if(end < start) {
			System.err.printf("Error while reading keystroke: endtime %d is smaller than starttime %d; skippin\n", end, start);
			return null;
		}
		if(key < 0) {
			System.err.printf("Error while reading keystroke: key %d is negative; skipping\n", key);
			return null;
		}
		if(key > 87) {
			System.err.printf("Error while reading keystroke: key %d is out of range; skipping\n", key);
			return null;
		}
		if(volume < 0) {
			System.err.printf("Error while reading keystroke: volume %d is negative; using default volume\n", volume);
			volume = 80;
		}
		
		Keystroke stroke = new Keystroke(start, end, key, volume, part, offset);
		
		return stroke;
	}
	
}
