package democode;

import com.sun.jna.Native;

import libs.Kernel32;

public class EjectMedia {

	public static void main(String[] args) {
		Kernel32 kernel32 = (Kernel32) 
				Native.loadLibrary("kernel32", Kernel32.class);
		String device = "\\\\.\\D:";
		System.out.printf("Ejecting: %s%n", device);
		int hdl2 = kernel32.CreateFileA(
				"\\\\.\\D:", 
				0x10000000, // GENERIC_ALL
				3, // sharing - read, write 
				null, 
				3, // OPEN_EXISTING
				0x80, 0);
		System.out.printf("CreateFile: %d%n", hdl2);
		System.out.printf("GetLastError: %04x %n", kernel32.GetLastError());
		int bytesReturned[] = new int[1];
		System.out.println("DeviceIoControl: " + kernel32.DeviceIoControl(hdl2, 
				// IOCTL_STORAGE_EJECT_MEDIA: 002d4808
				0x002d4808, 
				null, 0, 
				null, 0, 
				//new IntByReference(bytesReturned[0]),
				bytesReturned, 
				null));
		System.out.printf("GetLastError: %04x %n", kernel32.GetLastError());
	}
}
