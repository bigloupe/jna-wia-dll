package libs;

import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;

public interface Kernel32 extends Library {
	//http://msdn.microsoft.com/en-us/library/ms679277(VS.85).aspx
	boolean Beep(int freq, int duration);
	
	int GetCurrentDirectoryA(int bufLen, byte buffer[]);
	boolean SetCurrentDirectoryA(String dir);
	
	//http://msdn.microsoft.com/en-us/library/ms679360(VS.85).aspx
	int GetLastError();
	int GetCurrentProcess();
	int GetCurrentProcessId();
	int GetTickCount();
	int LoadLibraryA(String lib);
	Pointer GlobalLock(int hdl);
	boolean GlobalUnlock(int hdl);
	int GlobalFree(int hdl);
	int GetLogicalDrives();
	int GetLogicalDriveStringsA(int bufLen, byte buf[]);
	//http://msdn.microsoft.com/en-us/library/aa364993(VS.85).aspx
	boolean GetVolumeInformationA(String lpRootPathName, byte lpVolumeNameBuffer[], 
			int nVolumeNameSize, int lpVolumeSerialNumber[], int lpMaximumComponentLength[], 
			int lpFileSystemFlags[], byte lpFileSystemNameBuffer[], int nFileSystemNameSize);
	int GetDriveTypeA(String drive);
	
/*	public static class SYSTEMTIME extends Structure {
		public int wYear;
		public int wMonth;
		public int wDayOfWeek;
		public int wDay;
		public int wHour;
		public int wMinute;
		public int wSecond;
		public int wMilliseconds;
	}
*/
	public static class SYSTEMTIME extends Structure {
		public short wYear;
		public short wMonth;
		public short wDayOfWeek;
		public short wDay;
		public short wHour;
		public short wMinute;
		public short wSecond;
		public short wMilliseconds;
	}

	void GetSystemTime(SYSTEMTIME st);
	void GetLocalTime(SYSTEMTIME st);
        void GetComputerName();

	public static class FILETIME extends Structure {
		public int dwLowDateTime;
		public int dwHighDateTime;
	}
	
	boolean GetProcessTimes(int processHdl, FILETIME creation, FILETIME exit, FILETIME kernel, FILETIME user);
	boolean GetSystemTimes(FILETIME idle, FILETIME kernel, FILETIME user);
	
	public static class SECURITY_ATTRIBUTES extends Structure {
		int nLength;
		Pointer lpSecurityDescriptor;
		boolean bInheritHandle;
	}

	int CreateFileA(String file, int access, int mode, SECURITY_ATTRIBUTES secAttrs,
			int disposition, int flagsAndAttribs, int hdlTemplate);

	public static class OVERLAPPED extends Structure {
		int Internal;
		int InternalHigh;
		// union begins ...
		// embedded struct members ...
		int Offset;
		int OffsetHigh;
		// union member PVOID Pointer;
		int hEvent;
	}
	
	//http://msdn.microsoft.com/en-us/library/aa363411(VS.85).aspx
	//http://msdn.microsoft.com/en-us/library/aa363226(VS.85).aspx	
	boolean DeviceIoControl(int hdl, int opCode, byte inBuf[], int inBufSize, 
			byte outBuf[], int outBufSize, int bytesReturned[], OVERLAPPED ol);

	static class SYSTEM_POWER_STATUS extends Structure {
		public byte ACLineStatus;
		public byte BatteryFlag;
		public byte BatteryLifePercent;
		public byte Reserved1;
		public int BatteryLifeTime;
		public int BatteryFullLifeTime;
	}
	boolean GetSystemPowerStatus(SYSTEM_POWER_STATUS sps);

	boolean GetDiskFreeSpaceA(String s, IntByReference r1, IntByReference r2, IntByReference r3, IntByReference r4);
	boolean GetDiskFreeSpaceEx(String s, IntByReference r1, IntByReference r2, IntByReference r3);
}
