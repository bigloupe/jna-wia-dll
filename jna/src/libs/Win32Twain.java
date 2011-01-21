package libs;
import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public interface Win32Twain extends Library {

/*typedef struct {
   TW_UINT16  ConditionCode; // Any TWCC_ constant     
   TW_UINT16  Reserved;      // Future expansion space 
} TW_STATUS, FAR * pTW_STATUS;*/

	public static class TW_STATUS extends Structure {
		public short ConditionCode;
		public short Reserved;
	}

/*typedef char    TW_STR32[34],     FAR *pTW_STR32;*/

/*typedef struct {
   TW_UINT16  MajorNum;  // Major revision number of the software. 
   TW_UINT16  MinorNum;  // Incremental revision number of the software. 
   TW_UINT16  Language;  // e.g. TWLG_SWISSFRENCH 
   TW_UINT16  Country;   // e.g. TWCY_SWITZERLAND 
   TW_STR32   Info;      // e.g. "1.0b3 Beta release" 
} TW_VERSION, FAR * pTW_VERSION;*/

	public static class TW_VERSION extends Structure implements Structure.ByValue {
		public TW_VERSION(int align) {
			super();
			setAlignType(align);
		}
		public TW_VERSION() {
			super();
			setAlignType(Structure.ALIGN_NONE);
		}
		public short MajorNum;
		public short MinorNum;
		public short Language; 
		public short Country;   
		public byte Info[] = new byte[34];
		public void setInfo(String m) {
			byte mb[] = m.getBytes();
			for (int i = 0; i < Math.min(32, mb.length); ++i) {
				Info[i] = mb[i];
			}
		}
		public String getInfo() {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < 32; ++i) {
				if (Info[i] == 0)
					break;
				sb.append((char)Info[i]);
			}
			return sb.toString();
		}
	}

/*typedef struct {
   TW_UINT32  Id;              // Unique number.  In Windows, application hWnd      
   TW_VERSION Version;         // Identifies the piece of code              
   TW_UINT16  ProtocolMajor;   // Application and DS must set to TWON_PROTOCOLMAJOR 
   TW_UINT16  ProtocolMinor;   // Application and DS must set to TWON_PROTOCOLMINOR 
   TW_UINT32  SupportedGroups; // Bit field OR combination of DG_ constants 
   TW_STR32   Manufacturer;    // Manufacturer name, e.g. "Hewlett-Packard" 
   TW_STR32   ProductFamily;   // Product family name, e.g. "ScanJet"       
   TW_STR32   ProductName;     // Product name, e.g. "ScanJet Plus"         
} TW_IDENTITY, FAR * pTW_IDENTITY;*/

	public static class TW_IDENTITY extends Structure {
		public TW_IDENTITY(int align) {
			super();
			setAlignType(align);
		}
		public TW_IDENTITY() {
			super();
			setAlignType(Structure.ALIGN_NONE);
		}
		public int Id;
		public TW_VERSION Version = new TW_VERSION();
		public short ProtocolMajor;
		public short ProtocolMinor;
		public int SupportedGroups;
		public byte Manufacturer[] = new byte[34];
		public byte ProductFamily[] = new byte[34];
		public byte ProductName[] = new byte[34];
		public void setManufacturer(String m) {
			byte mb[] = m.getBytes();
			for (int i = 0; i < Math.min(32, mb.length); ++i) {
				Manufacturer[i] = mb[i];
			}
		}
		public String getManufacturer() {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < 32; ++i) {
				if (Manufacturer[i] == 0)
					break;
				sb.append((char)Manufacturer[i]);
			}
			return sb.toString();
		}
		public void setProductFamily(String m) {
			byte mb[] = m.getBytes();
			for (int i = 0; i < Math.min(32, mb.length); ++i) {
				ProductFamily[i] = mb[i];
			}
		}
		public String getProductFamily() {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < 32; ++i) {
				if (ProductFamily[i] == 0)
					break;
				sb.append((char)ProductFamily[i]);
			}
			return sb.toString();
		}
		public void setProductName(String m) {
			byte mb[] = m.getBytes();
			for (int i = 0; i < Math.min(32, mb.length); ++i) {
				ProductName[i] = mb[i];
			}
		}
		public String getProductName() {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < 32; ++i) {
				if (ProductName[i] == 0)
					break;
				sb.append((char)ProductName[i]);
			}
			return sb.toString();
		}
	}

/*typedef struct {
    TW_INT16     Whole;        // maintains the sign 
    TW_UINT16    Frac;
} TW_FIX32,  FAR *pTW_FIX32;*/

	public static class TW_FIX32 extends Structure {
		public short Whole;
		public short Frac;
	}

/*typedef struct {
   TW_FIX32   XResolution;      // Resolution in the horizontal             
   TW_FIX32   YResolution;      // Resolution in the vertical               
   TW_INT32   ImageWidth;       // Columns in the image, -1 if unknown by DS
   TW_INT32   ImageLength;      // Rows in the image, -1 if unknown by DS   
   TW_INT16   SamplesPerPixel;  // Number of samples per pixel, 3 for RGB   
   TW_INT16   BitsPerSample[8]; // Number of bits for each sample           
   TW_INT16   BitsPerPixel;     // Number of bits for each padded pixel     
   TW_BOOL    Planar;           // True if Planar, False if chunky          
   TW_INT16   PixelType;        // How to interp data; photo interp (TWPT_) 
   TW_UINT16  Compression;      // How the data is compressed (TWCP_xxxx)   
} TW_IMAGEINFO, FAR * pTW_IMAGEINFO;*/	

	public static class TW_IMAGEINFO extends Structure {
		public TW_FIX32   XResolution;             
		public TW_FIX32   YResolution;                
		public int ImageWidth;
		public int ImageLength;  
		public short SamplesPerPixel;   
		public short BitsPerSample[] = new short[8];
		public short BitsPerPixel;
		public boolean Planar;
		public short PixelType;
		public short Compression;
	}
	
/*typedef struct tagRGBQUAD {
  BYTE rgbBlue;
  BYTE rgbGreen;
  BYTE rgbRed;
  BYTE rgbReserved;
}RGBQUAD;*/	

	public static class RGBQUAD extends Structure {
		public RGBQUAD() {
			setAlignType(Structure.ALIGN_NONE);
		}
		public byte rgbBlue;
		public byte rgbGreen;
		public byte rgbRed;
		public byte rgbReserved;
	}
	
/*typedef struct {
   TW_BOOL    ShowUI;  // TRUE if DS should bring up its UI           
   TW_BOOL    ModalUI; // For Mac only - true if the DS's UI is modal 
   TW_HANDLE  hParent; // For windows only - Application window handle        
} TW_USERINTERFACE, FAR * pTW_USERINTERFACE;*/

	public static class TW_USERINTERFACE extends Structure {
		public boolean ShowUI;
		public boolean ModalUI;
		public int hParent;
	}

/*typedef struct {
   TW_UINT16 Count;
   union {
      TW_UINT32 EOJ;
      TW_UINT32 Reserved;
   };
} TW_PENDINGXFERS, FAR *pTW_PENDINGXFERS;*/	

	public static class TW_PENDINGXFERS extends Structure {
		public int EOJ;
		public int Reserved;
	}

/*typedef struct {
   TW_MEMREF  pEvent;    // Windows pMSG or Mac pEvent.                 
   TW_UINT16  TWMessage; // TW msg from data source, e.g. MSG_XFERREADY 
} TW_EVENT, FAR * pTW_EVENT;*/

	public static class TW_EVENT extends Structure {
		public Pointer pEvent;
		public short TWMessage;
	}

/*	public static class POINT extends Structure {
		public int x;
		public int y;
	}

*//*typedef struct {
    HWND hwnd;
    UINT message;
    WPARAM wParam;
    LPARAM lParam;
    DWORD time;
    POINT pt;
} MSG, *PMSG;*/	

/*	public static class MSG extends Structure {
		public int hwnd;
		public int message;
		public short wParm;
		public int lParm;
		int time;
		POINT pt;
	}
*/
/*typedef struct tagBITMAPINFOHEADER { 
  DWORD biSize; 
  LONG biWidth; 
  LONG biHeight; 
  WORD biPlanes; 
  WORD biBitCount 
  DWORD biCompression; 
  DWORD biSizeImage; 
  LONG biXPelsPerMeter; 
  LONG biYPelsPerMeter; 
  DWORD biClrUsed; 
  DWORD biClrImportant; 
} BITMAPINFOHEADER;  */

	public static class BITMAPINFOHEADER extends Structure {
		public BITMAPINFOHEADER(Pointer p, int align) {
			super(p);
			setAlignType(align);
			read();
		}
		public BITMAPINFOHEADER(Pointer p) {
			super(p);
			setAlignType(Structure.ALIGN_NONE);
			read();
		}
		public int biSize; 
		public int biWidth; 
		public int biHeight; 
		public short biPlanes; 
		public short biBitCount; 
		public int biCompression; 
		public int biSizeImage; 
		public int biXPelsPerMeter; 
		public int biYPelsPerMeter; 
		public int biClrUsed; 
		public int biClrImportant; 
	}

	public short DSM_Entry(TW_IDENTITY origin, TW_IDENTITY destination, int dg, short dat, short msg, Object p);
}
