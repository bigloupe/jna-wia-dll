package democode;

import java.awt.Toolkit;

import javax.swing.JOptionPane;

import com.sun.jna.Native;

import libs.User32;

public class MessageBeep {
	private static void messageBeep(int uType) {
	    User32 user32 = (User32) Native.loadLibrary("User32", User32.class);
		if(!user32.MessageBeep(uType))
			JOptionPane.showMessageDialog(null, "Error from MessageBeep()");
	}

	public static void main(String[] args) throws Exception {
		System.out.println("The Windows Asterisk sound");
		messageBeep(0x00000040);
		Thread.sleep(1000);
		System.out.println("The Windows Exclamation sound");
		messageBeep(0x00000030);
		Thread.sleep(1000);
		System.out.println("The Windows Critical Stop sound");
		messageBeep(0x00000010);
		Thread.sleep(1000);
		System.out.println("The Windows Question sound");
		messageBeep(0x00000020);
		Thread.sleep(1000);
		System.out.println("A simple beep");
		messageBeep(-1);
		Thread.sleep(1000);
		System.out.println("Toolkit.getDefaultToolkit().beep()");
		Toolkit.getDefaultToolkit().beep();
	}
}
