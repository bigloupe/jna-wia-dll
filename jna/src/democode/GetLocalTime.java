package democode;

import libs.Kernel32;
import libs.Kernel32.SYSTEMTIME;
import com.sun.jna.Native;

public class GetLocalTime {

	public static void main(String[] args) {
		try {
		Kernel32 kernel32 = (Kernel32)
				Native.loadLibrary("kernel32", Kernel32.class);
		SYSTEMTIME st = new SYSTEMTIME();
		kernel32.GetLocalTime(st);
		System.out.printf("Year: %d%n", st.wYear);
		System.out.printf("Month: %d%n", st.wMonth);
		System.out.printf("Day: %d%n", st.wDay);
		System.out.printf("Hour: %d%n", st.wHour);
		System.out.printf("Minute: %d%n", st.wMinute);
		System.out.printf("Second: %d%n", st.wSecond);
		} catch (Throwable e) {
			System.out.printf("%s:%s%n", e.getClass().getName(), e.getMessage());
		}
	}
}
