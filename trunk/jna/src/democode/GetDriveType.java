package democode;

import libs.Kernel32;

import com.sun.jna.Native;

public class GetDriveType {

	public static void main(String[] args) {
		Kernel32 kernel32 = (Kernel32)
				Native.loadLibrary("kernel32", Kernel32.class);

		int drives = kernel32.GetLogicalDrives();
		System.out.printf("GetLogicalDrives() => %04x (hex)%n", drives);
		String types[] = {
				"DRIVE_UNKNOWN", "DRIVE_NO_ROOT_DIR", "DRIVE_REMOVABLE", 
				"DRIVE_FIXED", "DRIVE_REMOTE", "DRIVE_CDROM", "DRIVE_RAMDISK"
		};
		for (int i = 0; i < 32; ++i) {
		      int bit = 0x00000001 << i;
		      if ((drives & bit) == 0)
				continue;
			String vol = (char)((int)'A' + i) + ":\\";
			int type = kernel32.GetDriveTypeA(vol);
			System.out.printf("GetDriveType('%s') => %s (%d)%n", vol, types[type], type);
		}
	}
}
