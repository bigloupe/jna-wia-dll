package democode;

import libs.Kernel32;
import com.sun.jna.Native;

public class BeepMorse2 {
	private static Kernel32 kernel32 = (Kernel32) Native.loadLibrary("kernel32", Kernel32.class);

	private static void toMorseCode(String letter) throws Exception {
		for (byte b : letter.getBytes()) {
			kernel32.Beep(1200, ((b == '.') ? 100 : 300));
			Thread.sleep(100);
		}
	}

	public static void main(String[] args) throws Exception {
		String helloWorld[][] = {
			{"....", ".", ".-..", ".-..", "---"}, // HELLO
			{".--", "---", ".-.", ".-..", "-.."}  // WORLD
		};
		for (String word[] : helloWorld) {
			for (String letter : word) {
				toMorseCode(letter);
				Thread.sleep(300);
			}
			Thread.sleep(700);
		}
	}
}
