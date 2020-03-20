package util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class Utils {

	public static void sleep(long millis) {
		if(millis <= 0)
			return;
		try {
			Thread.sleep(millis);
		} catch(Exception e) {
			throw new IllegalStateException(e);
		}
	}

	public static int bytesToInt(byte... bytes) {
		return  ((bytes[0] & 0xFF) << 24) + ((bytes[1] & 0xFF) << 16) + ((bytes[2] & 0xFF) << 8) + (bytes[3] & 0xFF);
	}
	
	public static byte[] intToBytes(int i) {
		return new byte[] {
				
				(byte)(i>>24),
				(byte)(i>>16),
				(byte)(i>>8),
				(byte)i
				
		};
	}
	
	public static String readFromFile(String path) {
		try {
			FileInputStream is = new FileInputStream(path);
			InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
			BufferedReader buffReader = new BufferedReader(isr);
			
			String read = "", line;
			
			while((line = buffReader.readLine()) != null)
				read += "\n" + line;

			buffReader.close();
			return read.substring(1);
		} catch(IOException e) {
			System.err.println("Error reading file '" + path + "':");
			e.printStackTrace();
			return "";
		}
	}
	
	public static boolean writeToFile(String path, String text) {
		try {
			
			FileOutputStream os = new FileOutputStream(path);
			OutputStreamWriter osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);
			osw.write(text, 0, text.length());
			osw.flush();
			osw.close();
			return true;
		} catch(IOException e) {
			return false;
		}
	}
}
