package democode;

import java.util.Arrays;
import libs.Win32Twain.TW_IDENTITY;
import com.sun.jna.Structure;

public class StructAlignment {

	private static void dump(Structure s) {
		s.write();
		System.out.printf("Size: %d%n", s.size());
		byte bb[] = s.getPointer().getByteArray(0, s.size());
		for (int i = 0; i < s.size(); ++i) {
			System.out.printf("%02x%c", bb[i], ((i % 16) == 15) ? '\n' : ' ');
		}
		System.out.println();
	}

	private static void tag(TW_IDENTITY id) {
		id.ProtocolMajor = (short) 0x1111;
		id.ProtocolMinor = (short) 0x2222;
		id.SupportedGroups = 0x33333333;
		Arrays.fill(id.Manufacturer, (byte)0x44);
		Arrays.fill(id.ProductFamily, (byte)0x55);
		Arrays.fill(id.ProductName, (byte)0x66);
	}

	public static void main(String[] args) {
		TW_IDENTITY s1 = new TW_IDENTITY(Structure.ALIGN_NONE);
		tag(s1);
		dump(s1);
		TW_IDENTITY s2 = new TW_IDENTITY(Structure.ALIGN_DEFAULT);
		tag(s2);
		dump(s2);
	}
}