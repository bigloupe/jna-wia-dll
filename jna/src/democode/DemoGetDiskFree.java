package democode;
import libs.Kernel32;

import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;

public class DemoGetDiskFree {

	public static void main(String[] args) {
		Kernel32 kernel32 = (Kernel32) 
				Native.loadLibrary("kernel32", Kernel32.class);
		IntByReference r1 = new IntByReference(), r2 = new IntByReference(), 
		r3 = new IntByReference(), r4 = new IntByReference();
		if (kernel32.GetDiskFreeSpaceA("C:\\", r1, r2, r3, r4)) {
			long multiplier = r1.getValue() * r2.getValue();
			long free = r3.getValue() * multiplier;
			long total = r4.getValue() * multiplier;
			System.out.printf("'C:\\': free: %d, total: %d %n", free, total);
		} else
			System.out.printf("GetDiskFreeSpaceEx() returned false%n");
		if (kernel32.GetDiskFreeSpaceA("D:\\", r1, r2, r3, r4)) {
			long multiplier = r1.getValue() * r2.getValue();
			long free = r3.getValue() * multiplier;
			long total = r4.getValue() * multiplier;
			System.out.printf("'D:\\': free: %d, total: %d %n", free, total);
		} else
			System.out.printf("GetDiskFreeSpaceEx() returned false%n");
		System.out.printf("All done%n");
	}

}
