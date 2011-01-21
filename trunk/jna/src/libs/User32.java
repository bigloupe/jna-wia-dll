package libs;

import com.sun.jna.Library;
import com.sun.jna.Structure;

public interface User32 extends Library {
	//http://msdn.microsoft.com/en-us/library/ms680356(VS.85).aspx
	boolean MessageBeep(int uType);

	boolean LockWorkStation();
	int GetDesktopWindow();
	int CreateWindowExA(int styleEx, String className, String windowName, int style, 
			int x, int y, int width, int height, int hndParent, int hndMenu, 
			int hndInst, Object parm);
	boolean SetWindowPos(int hWnd, int hWndInsAfter, int x, int y, int cx, int cy, short uFlgs);
	int DestroyWindow(int hdl);
	
	//http://msdn.microsoft.com/en-us/library/ms644958(VS.85).aspx
	/*typedef struct {
	    HWND hwnd;
	    UINT message;
	    WPARAM wParam;
	    LPARAM lParam;
	    DWORD time;
	    POINT pt;
	} MSG, *PMSG;*/	

	public static class POINT extends Structure {
		public int x;
		public int y;
	}

		public static class MSG extends Structure {
			public int hwnd;
			public int message;
			public short wParm;
			public int lParm;
			int time;
			POINT pt;
		}

	//http://msdn.microsoft.com/en-us/library/ms644936(VS.85).aspx
	boolean GetMessageA(MSG lpMsg, int hWnd, int wMsgFilterMin, int wMsgFilterMax);
	//http://msdn.microsoft.com/en-us/library/ms644955(VS.85).aspx
	boolean TranslateMessage(MSG lpMsg);
	//http://msdn.microsoft.com/en-us/library/ms644934(VS.85).aspx
	int DispatchMessageA(MSG lpMsg);
	
}
