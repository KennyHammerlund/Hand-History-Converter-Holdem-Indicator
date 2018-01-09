package handHistoryButtons;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

import javafx.scene.control.Button;

/**
 * Copy to Clipboard Button
 * 
 * @author Kenny Hammerlund
 *
 */
public class copyClipboardBtn extends Button {

	public copyClipboardBtn(String string) {
		this.setText(string);
	}

	/**
	 * Takes a string in and copies it to the clipboard
	 * 
	 * @param string
	 */
	public void copyClip(String string) {
		StringSelection stringSelection = new StringSelection(string);
		java.awt.datatransfer.Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, null);
	}
}
