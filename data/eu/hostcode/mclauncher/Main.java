package eu.hostcode.mclauncher;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.Scanner;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import de.dawik.mclauncher.util.Vars;
import de.tisan.flatui.components.fcommons.FlatLayoutManager;
import eu.hostcode.mclauncher.gui.GUILaunchInfo;
import eu.hostcode.mclauncher.gui.GUIMain;
import eu.hostcode.mclauncher.gui.GUISetAccount;

public class Main {
	public static String BASE_URL = "http://clauncher.hostcode.eu/download.php?file=";
	public static int VERSION = 2;

	public static void main(String[] args) {
		FlatLayoutManager.showCreditFrame = false;
		String filePath = System.getenv("APPDATA") + "\\CLauncher\\Packs\\";
		File f = new File(filePath);
		if (!f.exists()) {
			f.mkdirs();
		}
		boolean start = true;
		Scanner s;
		try {
			s = new Scanner(new URL(Main.BASE_URL + "latestversion.ver").openStream());
			int onlineVersion = 0;
			if (s.hasNextLine()) {
				onlineVersion = Integer.valueOf(s.nextLine());
			}
			System.out.println(onlineVersion);
			if (onlineVersion > Main.VERSION) {
				start = false;
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				JOptionPane.showMessageDialog(null, "Please download the newest version from clauncher.hostcode.eu", "Update!", JOptionPane.ERROR_MESSAGE);
				Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
				if ((desktop != null) && desktop.isSupported(Desktop.Action.BROWSE)) {
					try {
						desktop.browse(new URI("http://clauncher.hostcode.eu/download.php?file=CLauncher.jar"));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		if (start) {
			GUILaunchInfo.get().setVisible(true);
			if (Vars.get().pass.equals("NO")) {
				new GUISetAccount().setVisible(true);
			} else {
				new GUIMain().setVisible(true);
			}
		}
		// Starter.downloadAndStart("dummy");
	}
}
