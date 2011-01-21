package democode;

import libs.Kernel32;
import libs.Kernel32.SYSTEMTIME;
import com.sun.jna.Native;

public class GetSystemTime {

  public static void main(String[] args) {
    Kernel32 kernel32 = (Kernel32)
        Native.loadLibrary("kernel32", Kernel32.class);
    SYSTEMTIME st = new SYSTEMTIME();
    kernel32.GetSystemTime(st);
    System.out.printf("Year: %d%n", st.wYear);
    System.out.printf("Month: %d%n", st.wMonth);
    System.out.printf("Day: %d%n", st.wDay);
    System.out.printf("Hour: %d%n", st.wHour);
    System.out.printf("Minute: %d%n", st.wMinute);
    System.out.printf("Second: %d%n", st.wSecond);
  }
}