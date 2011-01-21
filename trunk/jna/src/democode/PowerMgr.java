package democode;

import com.sun.jna.Native;

import libs.Kernel32;

public class PowerMgr {
	public static void main(String[] args) {
		Kernel32 kernel32 = (Kernel32) 
				Native.loadLibrary("kernel32", Kernel32.class);
		Kernel32.SYSTEM_POWER_STATUS sps = new Kernel32.SYSTEM_POWER_STATUS();
		if (kernel32.GetSystemPowerStatus(sps)) {
			System.out.printf("ACLineStatus: %d (0=offline, 1=online, 255=unknown)%n", sps.ACLineStatus);
			System.out.printf("BatteryFlag: %d (1=high, 2=low, 4=critical, 8=charging, 128=no battery, 255=unknown)%n", sps.BatteryFlag);
			System.out.printf("BatteryLifePercent: %d %n", sps.BatteryLifePercent);
			System.out.printf("BatteryLifeTime: %d %n", sps.BatteryLifeTime);
			System.out.printf("BatteryFullLifeTime: %d %n", sps.BatteryFullLifeTime);
		} else
			System.out.printf("GetSystemPowerStatus() returned false%n");
		System.out.printf("All done%n");
	}
}
