package eu.hostcode.mclauncher.gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.AbstractListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.io.FileUtils;

import de.tisan.flatui.components.fbutton.FlatButton;
import de.tisan.flatui.components.fcommons.FlatColors;
import de.tisan.flatui.components.fcommons.FlatLayoutManager;
import de.tisan.flatui.components.ficon.FlatIconFont;
import de.tisan.flatui.components.flisteners.ActionListener;
import de.tisan.flatui.components.flisteners.MouseClickedHandler;
import de.tisan.flatui.components.flisteners.Priority;
import de.tisan.flatui.components.ftitlebar.DefaultFlatTitleBarListener;
import de.tisan.flatui.components.ftitlebar.FlatTitleBarWin10;
import eu.hostcode.mclauncher.Starter;

public class GUIMain extends JFrame {

	private JPanel contentPane;
	private ArrayList<String> packs;

	public GUIMain() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		setUndecorated(true);
		packs = new ArrayList<>();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setBorder(null);
		contentPane.setLayout(null);

		FlatLayoutManager man = FlatLayoutManager.get(this);
		FlatTitleBarWin10 bar = new FlatTitleBarWin10(man, "Packs");
		bar.setMaximizable(false);
		bar.setBounds(0, 0, getWidth(), 30);
		bar.addFlatTitleBarListener(new DefaultFlatTitleBarListener(this));
		contentPane.add(bar);
		contentPane.setBackground(FlatColors.BACKGROUND);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 41, 120, 248);
		contentPane.add(scrollPane);
		JList<String> list = new JList();
		scrollPane.setViewportView(list);
		list.setModel(new AbstractListModel<String>() {

			@Override
			public int getSize() {
				return packs.size();
			}

			@Override
			public String getElementAt(int paramInt) {
				if (packs.size() >= (paramInt + 1)) {
					return packs.get(paramInt);
				}
				return "";
			}
		});
		list.repaint();
		list.updateUI();

		FlatButton btnEdit = new FlatButton("Edit Account", FlatIconFont.PENCIL, man);
		btnEdit.setBackground(FlatColors.ORANGE.darker());
		btnEdit.addActionListener(Priority.NORMAL, new ActionListener() {

			@Override
			public void onAction(MouseClickedHandler arg0) {
				dispose();
				new GUISetAccount().setVisible(true);
			}
		});
		btnEdit.setBounds(272, 259, 168, 30);
		contentPane.add(btnEdit);

		FlatButton btninfo = new FlatButton("", FlatIconFont.INFO, man);
		btninfo.setBounds(390, 41, 45, 45);
		btninfo.addActionListener(Priority.NORMAL, new ActionListener() {

			@Override
			public void onAction(MouseClickedHandler arg0) {
				JOptionPane.showMessageDialog(GUIMain.this, "CLauncher (c) 2016 by Daniel Wieckhorst");
			}
		});
		contentPane.add(btninfo);

		FlatButton btnRemovePack = new FlatButton("Remove Pack", FlatIconFont.TIMES, man);
		btnRemovePack.setBackground(FlatColors.RED);
		btnRemovePack.addActionListener(Priority.NORMAL, new ActionListener() {

			@Override
			public void onAction(MouseClickedHandler arg0) {
				if ((list.getSelectedValue() != null) && !list.getSelectedValue().equalsIgnoreCase("")) {
					if (JOptionPane.showConfirmDialog(GUIMain.this, "Delete all pack data?", "DELETE ALL PACK DATA?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
						String filePath = System.getenv("APPDATA") + "\\CLauncher\\Packs\\" + list.getSelectedValue() + "\\";
						File f = new File(filePath);
						if (f.isDirectory()) {
							try {
								FileUtils.deleteDirectory(f);
							} catch (IOException e) {
								e.printStackTrace();
							}
							packs.clear();
							filePath = System.getenv("APPDATA") + "\\CLauncher\\Packs\\";
							f = new File(filePath);
							if (!f.exists()) {
								f.mkdirs();
							}
							String[] packss = f.list();
							for (String s : packss) {
								packs.add(s);
							}
							if (packs.size() > 0) {
								list.setSelectedIndex(0);
							}
						}
					}
				}
				list.updateUI();
			}
		});
		btnRemovePack.setBounds(140, 123, 184, 30);
		contentPane.add(btnRemovePack);

		// JButton btnNewButton = new JButton("DELETE ALL DATA");
		// btnNewButton.setBounds(140, 227, 120, 23);
		// contentPane.add(btnNewButton);
		FlatButton btnStart = new FlatButton("Start Pack", FlatIconFont.CHEVRON_RIGHT, man);
		btnStart.setBackground(FlatColors.GREEN);
		btnStart.addActionListener(Priority.NORMAL, new ActionListener() {

			@Override
			public void onAction(MouseClickedHandler arg0) {
				if ((list.getSelectedValue() != null) && !list.getSelectedValue().equalsIgnoreCase("")) {
					String pack = list.getSelectedValue();
					GUILaunchInfo.get().l("Start Pack: " + pack);
					GUILaunchInfo.get().toFront();
					GUILaunchInfo.get().requestFocus();
					Starter.downloadAndStart(pack, GUILaunchInfo.get());
				}
			}
		});
		btnStart.setBounds(140, 41, 184, 30);
		contentPane.add(btnStart);

		FlatButton btnAddPack = new FlatButton("Add Pack", FlatIconFont.PLUS, man);
		btnAddPack.setBounds(140, 82, 184, 30);
		contentPane.add(btnAddPack);
		btnAddPack.addActionListener(Priority.NORMAL, new ActionListener() {

			@Override
			public void onAction(MouseClickedHandler arg0) {
				String s = JOptionPane.showInputDialog("Pack Code").replace(" ", "");
				if ((s != null) && !packs.contains(s) && !s.isEmpty()) {
					packs.add(s);
					list.repaint();
					list.updateUI();
					if (packs.size() > 0) {
						list.setSelectedIndex(0);
					}
				}
			}
		});

		String filePath = System.getenv("APPDATA") + "\\CLauncher\\Packs\\";
		File f = new File(filePath);
		if (!f.exists()) {
			f.mkdirs();
		}
		String[] packss = f.list();
		for (String s : packss) {
			packs.add(s);
		}
		if (packs.size() > 0) {
			list.setSelectedIndex(0);
		}
	}
}
