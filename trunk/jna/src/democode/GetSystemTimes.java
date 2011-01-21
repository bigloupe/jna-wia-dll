package democode;

import com.sun.jna.Native;

import libs.Kernel32;
import libs.Kernel32.FILETIME;

public class GetSystemTimes {

	public static void main(String[] args) throws InterruptedException {
		Kernel32 kernel32 = (Kernel32) 
				Native.loadLibrary("kernel32", Kernel32.class);
		FILETIME idle = new FILETIME();
		FILETIME kernel = new FILETIME();
		FILETIME user = new FILETIME();
		boolean ok = kernel32.GetSystemTimes(idle, kernel, user);
		if (!ok) {
			System.out.printf("Error: %d%n", kernel32.GetLastError());
			System.exit(1);
		}
		(new Thread() {
			public void run() {
				while(true)
					;//try {Thread.sleep(1);} catch (Exception e) {}
			}
		}).start();
		long idleTime = (((long)idle.dwHighDateTime) << 32) + idle.dwLowDateTime;
		long kernelTime = (((long)kernel.dwHighDateTime) << 32) + kernel.dwLowDateTime;
		long userTime = (((long)user.dwHighDateTime) << 32) + user.dwLowDateTime;
		for (int i = 0; i < 10; ++i) {
			Thread.sleep(1000);
			ok = kernel32.GetSystemTimes(idle, kernel, user);
			if (!ok) {
				System.out.printf("Error: %d%n", kernel32.GetLastError());
				System.exit(1);
			}
			long idleTime2 = (((long)idle.dwHighDateTime) << 32) + (idle.dwLowDateTime & 0xffffffffL);
			long kernelTime2 = (((long)kernel.dwHighDateTime) << 32) + (kernel.dwLowDateTime & 0xffffffffL);
			long userTime2 = (((long)user.dwHighDateTime) << 32) + (user.dwLowDateTime & 0xffffffffL);
			System.out.printf("Idle: %f, Kernel: %f, User: %f%n", (idleTime2 - idleTime)/1e7, 
					(kernelTime2 - kernelTime)/1e7, (userTime2 - userTime)/1e7);
			idleTime = idleTime2; kernelTime = kernelTime2; userTime = userTime2;
		}
	}
}
