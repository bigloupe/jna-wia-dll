package democode;

import java.io.File;

import com.sun.jna.Native;

import libs.Kernel32;

public class SetCurrDirectory {

	public static void main(String[] args) {
		Kernel32 kernel32 = (Kernel32) 
				Native.loadLibrary("kernel32", Kernel32.class);
		String userDir = System.getProperty("user.dir");
		System.out.printf("user.dir (before): %s%n", userDir);
		byte strBuf[] = new byte[128];
		int len = kernel32.GetCurrentDirectoryA(128, strBuf);
		System.out.printf("GetCurrentDirectory (before): %s%n", new String(strBuf, 0, len));
		File dirFile = new File(userDir);
		dirFile = dirFile.getParentFile();
		System.out.printf("ParentFile: %s%n", dirFile.getAbsoluteFile());
		System.out.printf("SetCD result: %s%n", kernel32.SetCurrentDirectoryA(dirFile.getAbsolutePath()));
		len = kernel32.GetCurrentDirectoryA(128, strBuf);
		System.out.printf("GetCurrentDirectory (after): %s%n", new String(strBuf, 0, len));
		System.out.printf("user.dir (after): %s%n", System.getProperty("user.dir"));
	}
}
