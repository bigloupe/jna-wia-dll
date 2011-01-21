package democode;
import libs.Kernel32;

import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;

public class GetDiskFreeSpaceEx {

	public static void main(String[] args) {
		Kernel32 kernel32 = (Kernel32) 
				Native.loadLibrary("kernel32", Kernel32.class);
		IntByReference r1 = new IntByReference(), r2 = new IntByReference(), 
		r3 = new IntByReference(), r4 = new IntByReference();
		if (kernel32.GetDiskFreeSpaceA(null, r1, r2, r3, r4)) {
			System.out.printf("r1: %d %n", r1.getValue());
			System.out.printf("r2: %d %n", r2.getValue());
			System.out.printf("r3: %d %n", r3.getValue());
			System.out.printf("r4: %d %n", r4.getValue());
		} else
			System.out.printf("GetDiskFreeSpaceEx() returned false%n");
		System.out.printf("All done%n");
	}

}
