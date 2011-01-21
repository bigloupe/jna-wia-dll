package democode;

import com.sun.jna.Native;

import libs.Kernel32;
import libs.Kernel32.FILETIME;

public class GetProcessTimes {
	
	private static long time(FILETIME ft) {
		long t = (((long)ft.dwHighDateTime) << 32) + ft.dwLowDateTime;
		return t;
	}

	public static void main(String[] args) throws InterruptedException {
		Kernel32 kernel32 = (Kernel32) 
				Native.loadLibrary("kernel32", Kernel32.class);
		int procHdl = kernel32.GetCurrentProcess();
		FILETIME creationTime = new FILETIME();
		FILETIME exitTime = new FILETIME();
		FILETIME kernel = new FILETIME();
		FILETIME user = new FILETIME();
		(new Thread() {
			public void run() {
				while(true) {
					//System.err.println("--------------------------------------------");
					;//try {Thread.sleep(5);} catch (Exception e) {}
				}
			}
		}).start();
		boolean ok = kernel32.GetProcessTimes(procHdl, creationTime, exitTime, kernel, user);
		//long idleTime = (((long)idle.dwHighDateTime) << 32) + idle.dwLowDateTime;
		double kernelTime = time(kernel);
		double userTime = time(user);
		for (int i = 0; i < 10; ++i) {
			Thread.sleep(1000);
			ok = kernel32.GetProcessTimes(procHdl, creationTime, exitTime, kernel, user);
			if (!ok)
				break;
			double kernelTime2 = time(kernel);
			double userTime2 = time(user);
			System.out.printf("Kernel: %f, User: %f%n", (kernelTime2 - kernelTime) / 1e7, (userTime2 - userTime) / 1e7);
			kernelTime = kernelTime2; userTime = userTime2;
		}
		System.exit(0);
	}
}
