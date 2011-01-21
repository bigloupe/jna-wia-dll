package net.javajeff.jtwain;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;

import libs.Kernel32;
import libs.User32;
import libs.Win32Twain;
import libs.User32.MSG;
import libs.Win32Twain.BITMAPINFOHEADER;
import libs.Win32Twain.TW_EVENT;
import libs.Win32Twain.TW_IDENTITY;
import libs.Win32Twain.TW_IMAGEINFO;
import libs.Win32Twain.TW_PENDINGXFERS;
import libs.Win32Twain.TW_STATUS;
import libs.Win32Twain.TW_USERINTERFACE;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;

public class JTwain {

	public static boolean init() {
		kernel32 = (Kernel32) Native.loadLibrary("kernel32", Kernel32.class);
		user32 = (User32) Native.loadLibrary("user32", User32.class);
		twain = (Win32Twain) Native.loadLibrary("Twain_32", Win32Twain.class);
		return true;
	}

	public static Image acquire() throws JTwainException {
		int hwnd = user32.CreateWindowExA(0, "STATIC", "",
				WS_POPUPWINDOW, CW_USEDEFAULT, CW_USEDEFAULT, CW_USEDEFAULT,
				CW_USEDEFAULT, HWND_DESKTOP, 0, 0/*Win32TwainLibrary.HMODULE*/, null);
		//System.out.printf("CreateWindow: %d%n", hwnd);
		if (hwnd == 0) {
			throw new JTwainException("Unable to create private window");
		}
		boolean ok = user32.SetWindowPos(hwnd, HWND_TOPMOST, 0,
				0, 0, 0, (short) SWP_NOSIZE);
		if (!ok) {
			user32.DestroyWindow(hwnd);
			throw new JTwainException("Unable to position private window");
		}
		int stat = OpenDSM(g_AppID, hwnd);
		if (stat != TWRC_SUCCESS) {
			user32.DestroyWindow(hwnd);
			throw new JTwainException("Unable to open DSM");
		}
		//System.out.printf("app.Id: %d%n", app.Id);
		TW_IDENTITY srcID = new TW_IDENTITY();
		stat = GetDefaultSource(g_AppID, srcID);
		if (stat != TWRC_SUCCESS) {
			CloseDSM(g_AppID, hwnd);
			user32.DestroyWindow(hwnd);
			stat = GetConditionCode(g_AppID, srcID);
			throw new JTwainException("Unable to get default: " + stat);
		}
		//System.out.printf("Selected M, F, N: %s, %s, %s%n", 
				//srcID.getManufacturer(), srcID.getProductFamily(), srcID.getProductName());
		stat = OpenDefaultSource(g_AppID, srcID);
		if (stat != TWRC_SUCCESS) {
			CloseDSM(g_AppID, hwnd);
			user32.DestroyWindow(hwnd);
			stat = GetConditionCode(g_AppID, srcID);
			throw new JTwainException("Unable to open default: " + stat);
		}
		
		TW_USERINTERFACE ui = new TW_USERINTERFACE();
		ui.ShowUI = true;
		ui.ModalUI = false;
		ui.hParent = hwnd;
		
		stat = EnableDefaultSource(g_AppID, srcID, ui);
		if (stat != TWRC_SUCCESS) {
			CloseDefaultSource(g_AppID, srcID);
			CloseDSM(g_AppID, hwnd);
			user32.DestroyWindow(hwnd);
			stat = GetConditionCode(g_AppID, srcID);
			throw new JTwainException("Unable to enable default DS: " + stat);
		}

		MSG msg = new MSG();
		TW_EVENT event = new TW_EVENT();
		TW_PENDINGXFERS pxfers = new TW_PENDINGXFERS();
		while (user32.GetMessageA(msg, 0, 0, 0)) {
			event.pEvent = msg.getPointer();
			event.TWMessage = 0;
			stat = ProcessEvent(g_AppID, srcID, event);
			if (stat == TWRC_NOTDSEVENT) {
				user32.TranslateMessage(msg);
				user32.DispatchMessageA(msg);
				continue;
			}
			if (event.TWMessage == MSG_CLOSEDSREQ) {
				break;
			}
			if (event.TWMessage == MSG_XFERREADY) {
				TW_IMAGEINFO ii = new TW_IMAGEINFO();
				stat = GetImageInfo(g_AppID, srcID, ii);
				if (stat == TWRC_FAILURE) {
					ResetPendingTransfers(g_AppID, srcID, pxfers);
					throw new JTwainException("Unable to obtain image information (acquire)");
				}
				if (ii.Compression != TWCP_NONE || ii.BitsPerPixel != 8 &&
						ii.BitsPerPixel != 24) {
		                  // Cancel all transfers.
					ResetPendingTransfers(g_AppID, srcID, pxfers);
					throw new JTwainException("Image compressed or not 8-bit/24-bit (acquire)");
				}
				int hdl[] = new int[1];
				stat = PerformImageTransfer(g_AppID, srcID, hdl);
				if (stat != TWRC_XFERDONE) {
					ResetPendingTransfers(g_AppID, srcID, pxfers);
					throw new JTwainException("User aborted transfer or failure (acquire)");
				}
				Pointer p = kernel32.GlobalLock(hdl[0]);
				if (p != null) {
					System.out.printf("handle: %s%n", p.toString());
					BITMAPINFOHEADER bmih = new BITMAPINFOHEADER(p);
					//dump(bmih);
					if (ii.BitsPerPixel == 8)
						image = xferDIB8toImage(bmih);
					else
						image = xferDIB24toImage(bmih);
					if (image == null)
						throw new JTwainException("Could not transfer DIB to Image (acquire)");
				}
				kernel32.GlobalUnlock(hdl[0]);
				kernel32.GlobalFree(hdl[0]);
				ResetPendingTransfers(g_AppID, srcID, pxfers);
				stat = TWRC_SUCCESS;
				break;
			}
		}
		DisableDefaultSource(g_AppID, srcID, ui);
		CloseDefaultSource(g_AppID, srcID);
		CloseDSM(g_AppID, hwnd);
		user32.DestroyWindow(hwnd);
		return image;
	}

	public static void selectSourceAsDefault() throws JTwainException {
		int hwnd = user32.CreateWindowExA(0, "STATIC", "",
				WS_POPUPWINDOW, CW_USEDEFAULT, CW_USEDEFAULT, CW_USEDEFAULT,
				CW_USEDEFAULT, HWND_DESKTOP, 0, 0/*Win32TwainLibrary.HMODULE*/, null);
		//System.out.printf("CreateWindow: %d%n", hwnd);
		if (hwnd == 0) {
			throw new JTwainException("Unable to create private window (select)");
		}
		boolean ok = user32.SetWindowPos(hwnd, HWND_TOPMOST, 0,
				0, 0, 0, (short) SWP_NOSIZE);
		if (!ok) {
			user32.DestroyWindow(hwnd);
			throw new JTwainException("Unable to position private window (select)");
		}
		setupAppId(g_AppID);
		int stat = OpenDSM(g_AppID, hwnd);
//		int stat = OpenDSM(g_AppID, -1);
		if (stat != TWRC_SUCCESS) {
			user32.DestroyWindow(hwnd);
			throw new JTwainException("Unable to open DSM (select)");
		}
		//System.out.printf("app.Id: %d%n", app.Id);
		// BEGIN: Memory Alignment Problem Demo
		TW_IDENTITY srcID = new TW_IDENTITY(Structure.ALIGN_DEFAULT);
		stat = SelectSource(g_AppID, srcID);
		if (stat != TWRC_SUCCESS) {
			CloseDSM(g_AppID, hwnd);
			user32.DestroyWindow(hwnd);
			if (stat == TWRC_CANCEL)
				return;
			stat = GetConditionCode(g_AppID, srcID);
			throw new JTwainException("Unable to display user interface: " + stat);
		}
		dump(srcID);
		System.out.printf("ProtocolMajor: %02x%n", srcID.ProtocolMajor);
		System.out.printf("ProtocolMinor: %02x%n", srcID.ProtocolMinor);
		System.out.printf("SupportedGroups: %04x%n", srcID.SupportedGroups);
		System.out.printf("Manufacturer: %s%n", new String(srcID.Manufacturer, 0, 34));
		// END: Memory Alignment Problem Demo
		//System.out.printf("Selected M, F, N: %s, %s, %s%n", 
		//		src.getManufacturer(), src.getProductFamily(), src.getProductName());
		stat = CloseDSM(g_AppID, hwnd);
		if (stat != 0) {
			user32.DestroyWindow(hwnd);
			throw new JTwainException("Unable to close DSM");
		}
		//System.out.printf("app.Id: %d%n", app.Id);
		stat = user32.DestroyWindow(hwnd);
		if (stat == 0) {
			throw new JTwainException("Unable to destroy private window");
		}
	}

	private static Image xferDIB8toImage(BITMAPINFOHEADER bmih) {
		int width = bmih.biWidth;
		int height = bmih.biHeight; // height < 0 if bitmap is top-down
		if (height < 0)
			height = -height;
//System.out.printf("w: %d, h: %d%n", width, height);
		int pixels[] = new int[width * height];
		int numColors;
		if (bmih.biClrUsed > 0)
			numColors = bmih.biClrUsed;
		else
			numColors = (1 << bmih.biBitCount);
		int padBytes = (4 - width % 4) % 4; // Each pixel occupies 1 byte
											// (palette index)
		// and the number of row bytes is a multiple of
		// 4.
//System.out.printf("NumColors: %d, PadBytes: %d%n", numColors, padBytes);
		int rowBytes = width + padBytes;
		byte bitmap[] = bmih.getPointer().getByteArray(bmih.size() + numColors * 4, height * rowBytes);
		int palette[] = bmih.getPointer().getIntArray(bmih.size(), numColors);
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				// Extract color information for pixel and build an equivalent
				// Java pixel for storage in the Java-based integer array.
				byte bitVal = bitmap[rowBytes * row + col];
//System.out.printf("%02x%s", bitVal, ((col % 16) == 0 && col != 0) ? "\n"  : " ");
				int pixel = 0xff000000 | palette[bitVal & 0xff];
				// Store the pixel in the array at the appropriate index.
				pixels[width * (height - row - 1) + col] = pixel;
			}
		}
		MemoryImageSource mis = new MemoryImageSource(width, height, pixels, 0,	width);
		return Toolkit.getDefaultToolkit().createImage(mis);
	}

	private static Image xferDIB24toImage(BITMAPINFOHEADER bmih) {
		int width = bmih.biWidth;
		int height = bmih.biHeight; // height < 0 if bitmap is top-down
		if (height < 0)
			height = -height;
		// System.out.printf("w: %d, h: %d%n", width, height);
		int pixels[] = new int[width * height];
		/*
		 * int numColors; if (bmih.biClrUsed > 0) numColors = bmih.biClrUsed;
		 * else numColors = (1 << bmih.biBitCount);
		 */int padBytes = (3 * width) % 4; // Each pixel occupies 1 byte
		// (palette index)
		// and the number of row bytes is a multiple of
		// 4.
		// System.out.printf("NumColors: %d, PadBytes: %d%n", numColors,
		// padBytes);
		int rowBytes = 3 * width + padBytes;
		byte bitmap[] = bmih.getPointer().getByteArray(bmih.size(),
				height * rowBytes);
		// int palette[] = bmih.getPointer().getIntArray(bmih.size(),
		// numColors);
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				// Obtain pixel index;
				int index = rowBytes * row + col * 3;
				// System.out.printf("%02x%s", bitVal, ((col % 16) == 0 && col
				// != 0) ? "\n" : " ");
				int pixel = 0xff000000 | (bitmap[index + 2] & 0xff) << 16
						| (bitmap[index + 1] & 0xff) << 8 | (bitmap[index] & 0xff);
				// Store the pixel in the array at the appropriate index.
				pixels[width * (height - row - 1) + col] = pixel;
			}
		}
		MemoryImageSource mis = new MemoryImageSource(width, height, pixels, 0,
				width);
		return Toolkit.getDefaultToolkit().createImage(mis);
	}

	private static void setupAppId(TW_IDENTITY appID) {
		appID.Id = 0;
		appID.ProtocolMajor = 1;
		appID.ProtocolMinor = 9;
		appID.SupportedGroups = (DG_CONTROL | DG_IMAGE);
		appID.setManufacturer("Sanjay Dasgupta");
		appID.setProductFamily("JTwain JNA Demo");
		appID.setProductName("TWAIN-JNA");
		appID.Version.MajorNum = 1;
		appID.Version.MinorNum = 0;
		appID.Version.Language = 2; //TWLG_ENG
		appID.Version.Country = 91; //TWCY_INDIA           
		appID.Version.setInfo("JNA-JTwain 1.0");
		//dump(appID);
	}

	private static int OpenDSM(TW_IDENTITY application, int winHdl) {
		int stat = twain.DSM_Entry(application, null, 
				DG_CONTROL,
				DAT_PARENT,
				MSG_OPENDSM,
				/* winHdl */ // does not work
				/* new int[] {winHdl} */ // works
				new IntByReference(winHdl));
		return stat;
	}

	private static int GetDefaultSource(TW_IDENTITY application, TW_IDENTITY src) {
		int stat = twain.DSM_Entry(application, null, 
				DG_CONTROL,
				DAT_IDENTITY,        
				MSG_GETDEFAULT,
				src);
		return stat;
	}

	private static int OpenDefaultSource(TW_IDENTITY application, TW_IDENTITY src) {
		int stat = twain.DSM_Entry(application, null, 
				DG_CONTROL,
				DAT_IDENTITY,        
				MSG_OPENDS,
				src);
		return stat;
	}

	private static int CloseDefaultSource(TW_IDENTITY application, TW_IDENTITY src) {
		int stat = twain.DSM_Entry(application, null, 
				DG_CONTROL,
				DAT_IDENTITY,        
				MSG_CLOSEDS,
				src);
		return stat;
	}

	private static int SelectSource(TW_IDENTITY application, TW_IDENTITY src) {
		int stat = twain.DSM_Entry(application, null, 
				DG_CONTROL,
				DAT_IDENTITY,        
				MSG_USERSELECT,
				src);
		return stat;
	}

	private static int EnableDefaultSource(TW_IDENTITY application, 
			TW_IDENTITY src, TW_USERINTERFACE ui) {
		int stat = twain.DSM_Entry(application, src, 
				DG_CONTROL,
				DAT_USERINTERFACE,        
				MSG_ENABLEDS,
				ui);
		return stat;
	}

	private static int DisableDefaultSource(TW_IDENTITY application, 
			TW_IDENTITY src, TW_USERINTERFACE ui) {
		int stat = twain.DSM_Entry(application, src, 
				DG_CONTROL,
				DAT_USERINTERFACE,        
				MSG_DISABLEDS,
				ui);
		return stat;
	}

	private static int GetImageInfo(TW_IDENTITY application, 
			TW_IDENTITY src, TW_IMAGEINFO ii) {
		int stat = twain.DSM_Entry(application, src, 
				DG_IMAGE,
				DAT_IMAGEINFO,        
				MSG_GET,
				ii);
		return stat;
	}

	private static int ResetPendingTransfers(TW_IDENTITY application, 
			TW_IDENTITY src, TW_PENDINGXFERS xfers) {
		int stat = twain.DSM_Entry(application, src, 
				DG_CONTROL,
				DAT_PENDINGXFERS,        
				MSG_RESET,
				xfers);
		return stat;
	}

	private static int ProcessEvent(TW_IDENTITY application, 
			TW_IDENTITY src, TW_EVENT event) {
		int stat = twain.DSM_Entry(application, src, 
				DG_CONTROL,
				DAT_EVENT,        
				MSG_PROCESSEVENT,
				event);
		return stat;
	}

	private static int PerformImageTransfer(TW_IDENTITY application, 
			TW_IDENTITY src, int hdl[]) {
		int stat = twain.DSM_Entry(application, src, 
				DG_IMAGE,
				DAT_IMAGENATIVEXFER,
				MSG_GET,
				hdl);
		return stat;
	}

	private static int CloseDSM(TW_IDENTITY application, int winHdl) {
		int stat = twain.DSM_Entry(application, null, 
				DG_CONTROL,
				DAT_PARENT,
				MSG_CLOSEDSM,
				winHdl);
		return stat;
	}

	private static int GetConditionCode(TW_IDENTITY application, TW_IDENTITY src) {
		TW_STATUS status = new TW_STATUS();
		int stat = twain.DSM_Entry(application, null, 
				DG_CONTROL,
				DAT_STATUS,        
				MSG_GET,
				status);
		return (stat == 0) ? status.ConditionCode : ((stat << 16) + status.ConditionCode);
	}

	private static void dump(Structure s) {
		s.write();
		int size = s.size();
		System.out.printf("Structure: %s  Size: %d%n", s.getClass().getSimpleName(), size);
		Pointer p = s.getPointer();
		byte bb[] = p.getByteArray(0, size);
		outer: for (int i = 0; ; i += 16) {
			System.out.printf("%03d: ", i);
			for (int j = 0; j < 16; ++j) {
				int k = i + j;
				if (k >= size)
					break;
				System.out.printf(" %02x", bb[k]);
			}
			System.out.println();
			System.out.printf("%03d: ", i);
			for (int j = 0; j < 16; ++j) {
				int k = i + j;
				if (k >= size)
					break outer;
				byte b = bb[k];
				System.out.printf("  %c", (b >= 32 && b < 127) ? b : '.');
			}
			System.out.println();
		}
		System.out.println();
	}

	public static Win32Twain twain = null;
	private static Kernel32 kernel32 = null; 
	private static User32 user32 = null;
	private static Image image = null;
	private static final short MSG_GET = 1, MSG_GETDEFAULT = 3, MSG_RESET = 7, MSG_CLOSEDSM = 0x0302, 
		MSG_OPENDSM = 0x0301, MSG_OPENDS = 0x0401, MSG_CLOSEDS = 0x0402, MSG_USERSELECT = 0x0403,
		MSG_ENABLEDS = 0x0502, MSG_DISABLEDS = 0x0501, MSG_PROCESSEVENT = 0x0601;
	private static final short DAT_EVENT = 2, DAT_IDENTITY = 3, DAT_PARENT = 4, DAT_PENDINGXFERS = 5, DAT_STATUS = 8, 
		DAT_USERINTERFACE = 9, DAT_IMAGEINFO = 0x0101, DAT_IMAGENATIVEXFER = 0x0104;
	private static final int MSG_CLOSEDSREQ = 0x0102, MSG_XFERREADY = 0x0101;
	private static final int TWCP_NONE = 0;
	@SuppressWarnings("unused")
	private static final short DG_CONTROL = 1, DG_IMAGE = 2, DG_AUDIO = 4;
	@SuppressWarnings("unused")
	private static final int TWRC_SUCCESS = 0, TWRC_FAILURE = 1, twrc_checkstatus = 2,
		TWRC_CANCEL = 3, TWRC_NOTDSEVENT = 5, TWRC_XFERDONE = 6;
	private static final int HWND_DESKTOP = 0x10014;
	private static final int WS_POPUPWINDOW = 0x80000000 | 0x00800000 | 0x00080000;
	private static final int CW_USEDEFAULT = 0x80000000;
	private static final int HWND_TOPMOST = -1;
	private static final int SWP_NOSIZE = 1;
	private static TW_IDENTITY g_AppID = new TW_IDENTITY();
}
