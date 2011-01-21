package democode;

import com.sun.jna.Native;

import libs.Kernel32;

public class DeviceSerialNumber {

	public static void main(String[] args) {
		Kernel32 kernel32 = (Kernel32) 
				Native.loadLibrary("kernel32", Kernel32.class);
		String device = "\\\\.\\C:";
		int hdl2 = kernel32.get  CreateFileA(
				device, 
				0x10000000, // GENERIC_ALL
				3, // sharing - read, write 
				null, 
				3, // OPEN_EXISTING
				0x80, 0);
		System.out.printf("CreateFile('%s'): %d%n", device, hdl2);
		if (hdl2 == -1) {
			System.out.printf("GetLastError: %04x %n", kernel32.GetLastError());
			return;
		}
		byte outBuf[] = new byte[128];
		int bytesReturned[] = new int[1];
		System.out.println("DeviceIoControl: " + kernel32.DeviceIoControl(hdl2, 
				//IOCTL_STORAGE_GET_MEDIA_SERIAL_NUMBER: 002d0c10
				0x002d0c10, 
				null, 0, 
				outBuf, 128, 
				//new IntByReference(bytesReturned[0]),
				bytesReturned, 
				null));
		System.out.printf("GetLastError: %04x %n", kernel32.GetLastError());
		System.out.println("DeviceIoControl: " + kernel32.DeviceIoControl(hdl2, 
				//IOCTL_STORAGE_GET_DEVICE_NUMBER: 002d1080
				0x002d1080, 
				null, 0, 
				outBuf, 128, 
				//new IntByReference(bytesReturned[0]),
				bytesReturned, 
				null));
		System.out.printf("GetLastError: %04x %n", kernel32.GetLastError());
	}
}
