package democode;

import com.sun.jna.Native;

import libs.Kernel32;
import libs.User32;

public class TickCount {

	public static void main(String[] args) {
	    User32 user32 = (User32) Native.loadLibrary("User32", User32.class);
		Kernel32 kernel32 = (Kernel32) 
				Native.loadLibrary("kernel32", Kernel32.class);
		System.out.println("TickCount: " + kernel32.GetTickCount());
		System.out.println("CurrentProcessId: " + kernel32.GetCurrentProcessId());
		System.out.println("CurrentProcess(handle): " + kernel32.GetCurrentProcess());
		System.out.println("DesktopWindow(handle): " + user32.GetDesktopWindow());
		int w = user32.CreateWindowExA(
				0, "STATIC", "", 0x80000000, 0, 0, 0, 0, 0, 0, 0, null);
		System.out.println("CreateWindow(handle): " + w);
		System.out.println("DestroyWindow(handle): " + user32.DestroyWindow(w));
		System.out.println("LoadLibrary(handle): " + kernel32.LoadLibraryA("Kernel32"));
	}
}
