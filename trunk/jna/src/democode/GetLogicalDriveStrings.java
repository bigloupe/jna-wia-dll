package democode;

import libs.Kernel32;

import com.sun.jna.Native;

public class GetLogicalDriveStrings {

	public static void main(String[] args) {
		Kernel32 kernel32 = (Kernel32) 
			Native.loadLibrary("kernel32", Kernel32.class);
		byte buf[] = new byte[128];
		int stat = kernel32.GetLogicalDriveStringsA(128, buf);
		System.out.printf("GetLogicalDriveStringsA: %d%n", stat);
		for (int i = 0; i < stat; ++i) {
			byte b = buf[i];
			if (b == 0)
				System.out.print(" ");
			else {
				System.out.print((char)b);
			}
		}
		System.out.println();
	}
}
