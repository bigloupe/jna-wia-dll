package democode;

import libs.Kernel32;
import com.sun.jna.Native;

public class GetVolumeInformation {
	
  private static String b2s(byte b[]) {
    int len = 0;
    while (b[len] != 0)
      ++len;
    return new String(b, 0, len);
  }

  public static void main(String[] args) {
    Kernel32 kernel32 = (Kernel32) Native.loadLibrary(
        "kernel32", Kernel32.class);
    int drives = kernel32.GetLogicalDrives();
    for (int i = 0; i < 32; ++i) {
      if ((drives & (1 << i)) == 0)
        continue;
      String path = String.format("%c:\\", (char) ((int) 'A' + i));
      byte volName[] = new byte[256], fsName[] = new byte[256];
      int volSerNbr[] = new int[1], maxCompLen[] = new int[1], fileSysFlags[] = new int[1];
      boolean ok = kernel32.GetVolumeInformationA(path, volName, 
          256, volSerNbr, maxCompLen, fileSysFlags, fsName, 256);
      if (ok)
        System.out.printf("%s %08X '%s' %s %08X%n", path, volSerNbr[0], 
            b2s(volName), b2s(fsName), fileSysFlags[0]);
      else
        System.out.printf("%s <OFFLINE>%n", path);
    }
  }
}