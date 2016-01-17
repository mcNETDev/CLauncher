package eu.hostcode.mclauncher.gui;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import de.tisan.flatui.components.fbutton.FlatButton;
import de.tisan.flatui.components.fcommons.Anchor;
import de.tisan.flatui.components.fcommons.FlatColors;
import de.tisan.flatui.components.fcommons.FlatLayoutManager;
import de.tisan.flatui.components.ficon.FlatIconFont;
import de.tisan.flatui.components.flisteners.ActionListener;
import de.tisan.flatui.components.flisteners.MouseClickedHandler;
import de.tisan.flatui.components.flisteners.Priority;
import de.tisan.flatui.components.ftitlebar.DefaultFlatTitleBarListener;
import de.tisan.flatui.components.ftitlebar.FlatTitleBarWin10;

public class GUILaunchInfo extends JFrame {

	private static GUILaunchInfo in;
	private String pack = "";
	private JPanel contentPane;
	public JLabel status;
	public Process process;
	// public ArrayList<LogEntry> log;
	private JTextPane log;
	private JScrollPane scrollPane;
	private Style styleIncoming;
	private Style styleOutgoing;
	private Style styleComment;

	public static GUILaunchInfo get() {
		return GUILaunchInfo.in != null ? GUILaunchInfo.in : (GUILaunchInfo.in = new GUILaunchInfo());
	}

	private GUILaunchInfo() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		setUndecorated(true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setBounds(50, 50, 718, 398);
		contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setBorder(null);
		contentPane.setLayout(null);

		FlatLayoutManager man = FlatLayoutManager.get(this);
		FlatTitleBarWin10 bar = new FlatTitleBarWin10(man, "Console");
		bar.setMaximizable(false);
		bar.setAnchor(Anchor.RIGHT, Anchor.LEFT);
		// bar.addComponentListener(new ComponentListener() {
		//
		// @Override
		// public void componentShown(ComponentEvent e) {
		// }
		//
		// @Override
		// public void componentResized(ComponentEvent e) {
		// // funktioniert nicht
		// int rootWidth = txtpnFg.getWidth();
		// int rootHeight = getHeight();
		// int percentWidth = (rootWidth * 100) /
		// man.getContentPane().getBounds().width;
		// int percentHeight = (rootHeight * 100) /
		// man.getContentPane().getBounds().height;
		// txtpnFg.setSize(e.getComponent().getWidth() * (percentWidth / 100),
		// e.getComponent().getHeight() * (percentHeight / 100));
		// }
		//
		// @Override
		// public void componentMoved(ComponentEvent e) {
		// }
		//
		// @Override
		// public void componentHidden(ComponentEvent e) {
		// }
		// });
		bar.setBounds(0, 0, getWidth(), 30);
		bar.addFlatTitleBarListener(new DefaultFlatTitleBarListener(this));
		contentPane.add(bar);
		contentPane.setBackground(FlatColors.BACKGROUND.brighter());

		JLabel lblStatus = new JLabel("Status:");
		lblStatus.setForeground(FlatColors.WHITE.darker());
		lblStatus.setBounds(10, 41, 46, 14);
		contentPane.add(lblStatus);

		status = new JLabel("Loading....");
		status.setForeground(FlatColors.WHITE.darker());
		status.setBounds(96, 41, 358, 14);
		contentPane.add(status);

		FlatButton btnKill = new FlatButton("Kill", FlatIconFont.EXCLAMATION_TRIANGLE, man);
		btnKill.addActionListener(Priority.NORMAL, new ActionListener() {

			@Override
			public void onAction(MouseClickedHandler arg0) {
				int i = JOptionPane.showConfirmDialog(GUILaunchInfo.this, "Kill Minecraft?", "Kill it?", JOptionPane.YES_NO_OPTION);
				if (i == JOptionPane.YES_OPTION) {
					if (process != null) {
						process.destroy();
						status.setText("Minecraft was killed.");
					}
				}
			}
		});
		btnKill.setBounds(459, 352, 113, 35);
		btnKill.setAnchor(Anchor.RIGHT, Anchor.DOWN);
		contentPane.add(btnKill);

		FlatButton btnOpenLogFile = new FlatButton("Open log", man);
		btnOpenLogFile.setBounds(582, 352, 126, 35);
		btnOpenLogFile.setAnchor(Anchor.RIGHT, Anchor.DOWN);
		contentPane.add(btnOpenLogFile);

		scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 66, 698, 275);
		contentPane.add(scrollPane);
		log = new JTextPane();
		log.setText("");
		log.setBackground(FlatColors.HIGHLIGHTBACKGROUND);
		scrollPane.setViewportView(log);
		styleIncoming = log.addStyle("StyleIncoming", null);
		styleOutgoing = log.addStyle("StyleOutgoing", null);
		styleComment = log.addStyle("StyleComment", null);
		StyleConstants.setForeground(styleIncoming, FlatColors.SILVER);
		StyleConstants.setForeground(styleOutgoing, FlatColors.ALIZARINRED);
		StyleConstants.setForeground(styleComment, FlatColors.LIME);
		StyleConstants.setBold(styleOutgoing, true);
	}

	// public void log(LogEntry entry) {
	// if (log.size() > 1000) {
	// log.remove(0);
	// }
	// log.add(entry);
	// String html = "<HTML><BODY>";
	// for (LogEntry e : log) {
	// switch (e.getType()) {
	// case DEBUG:
	// html += "<span style=\"color:green;\">";
	// break;
	// case ERROR:
	// html += "<span style=\"color:red;\">";
	// break;
	// case INFO:
	// case SYSO:
	// case LAUNCHER:
	// case MINECRAFT:
	// html += "<span style=\"color:orange;\">";
	// break;
	// case WARNING:
	// html += "<span style=\"color:yellow;\">";
	// break;
	// default:
	// break;
	// }
	// html += e.getText() + "</span><br>";
	// }
	// html += "</HTML></BODY>";
	// txtpnFg.setText(html);
	//
	// JScrollBar b = scrollPane.getVerticalScrollBar();
	// b.setValue(b.getMaximum() - b.getVisibleAmount());
	//
	// }

	public void l(String line) {
		try {
			StyledDocument doc = log.getStyledDocument();
			if (line.contains("INFO")) {
				if (line.contains(">")) {
					doc.insertString(doc.getLength(), line + "\n", styleComment);
				} else {
					doc.insertString(doc.getLength(), line + "\n", styleIncoming);
				}
			} else if (line.contains("ERROR") || line.contains("WARNING") || line.contains("WARN") || line.contains("MINECRAFT")) {
				doc.insertString(doc.getLength(), line + "\n", styleOutgoing);
			} else {
				doc.insertString(doc.getLength(), line + "\n", styleIncoming);
			}
			log.repaint();
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					try {
						JScrollBar b = scrollPane.getVerticalScrollBar();
						b.setValue(b.getMaximum() - b.getVisibleAmount());
					} catch (Exception ex) {
					}
				}
			});
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		// SwingUtilities.invokeLater(new Runnable() {
		//
		// @Override
		// public void run() {
		// JScrollBar b = scrollPane.getVerticalScrollBar();
		// b.setValue(b.getMaximum() - b.getVisibleAmount());
		// }
		//
		// });
	}
}
