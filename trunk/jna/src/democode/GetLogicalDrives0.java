package democode;

import libs.Kernel32;

import com.sun.jna.Native;

public class GetLogicalDrives0 {

	public static void main(String[] args) {
		Kernel32 kernel32 = (Kernel32) Native.loadLibrary(
				"kernel32", Kernel32.class);

		int drives = kernel32.GetLogicalDrives();
		System.out.printf("GetLogicalDrives() => %08x (hex)%n", drives);
		for (int i = 0; i < 32; ++i) {
			int bit = 0x00000001 << i;
			if ((drives & bit) == 0)
				continue;
			System.out.printf("%c:\\%n", (char) ((int) 'A' + i));
		}
	}
}
