package democode;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

public class DemoJOptionPane {

	public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}
		JOptionPane.showConfirmDialog(null, "JOptionPane.showConfirmDialog");
		try {Thread.sleep(1000);} catch (Exception e) {};
		JOptionPane.showInputDialog("JOptionPane.showInputDialog");
		try {Thread.sleep(1000);} catch (Exception e) {};
		JOptionPane.showMessageDialog(null, "JOptionPane.showMessageDialog", "JOptionPane.showMessageDialog", JOptionPane.ERROR_MESSAGE);
	}
}
