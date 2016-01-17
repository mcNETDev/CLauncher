package eu.hostcode.mclauncher.gui;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import de.dawik.mclauncher.util.Vars;
import de.tisan.flatui.components.fbutton.FlatButton;
import de.tisan.flatui.components.fcommons.FlatColors;
import de.tisan.flatui.components.fcommons.FlatLayoutManager;
import de.tisan.flatui.components.ficon.FlatIconFont;
import de.tisan.flatui.components.flisteners.MouseClickedHandler;
import de.tisan.flatui.components.flisteners.Priority;
import de.tisan.flatui.components.ftextbox.FlatTextBox;
import de.tisan.flatui.components.ftitlebar.DefaultFlatTitleBarListener;
import de.tisan.flatui.components.ftitlebar.FlatTitleBarListener;
import de.tisan.flatui.components.ftitlebar.FlatTitleBarWin10;

public class GUISetAccount extends JFrame {

	private JPanel contentPane;
	private JTextField textField_1;
	private JPasswordField passwordField_1;

	public static void main(String[] args) {
		new GUISetAccount().setVisible(true);
	}

	public GUISetAccount() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		setUndecorated(true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 313, 142);
		contentPane = new JPanel();
		contentPane.setBorder(null);
		setContentPane(contentPane);
		contentPane.setLayout(null);
		FlatLayoutManager man = FlatLayoutManager.get(this);
		FlatTitleBarWin10 bar = new FlatTitleBarWin10(man, "Add Minecraft Account");
		bar.setMaximizable(false);
		bar.setBounds(0, 0, getWidth(), 30);
		bar.addFlatTitleBarListener(new DefaultFlatTitleBarListener(this));
		bar.addFlatTitleBarListener(new FlatTitleBarListener() {

			@Override
			public void onWindowDragged() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onMinimizeButtonReleased() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onMinimizeButtonPressed() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onMinimizeButtonMouseMove() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onMaximizeButtonReleased() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onMaximizeButtonPressed() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onMaximizeButtonMouseMove() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onImageClicked() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onCloseButtonReleased() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onCloseButtonPressed() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onCloseButtonMouseMove() {
				// TODO Auto-generated method stub

			}
		});
		contentPane.add(bar);
		contentPane.setBackground(FlatColors.BACKGROUND.darker());

		JLabel lblEmail = new JLabel("E-Mail / Username:");
		lblEmail.setBounds(31, 56, 101, 14);
		lblEmail.setForeground(FlatColors.WHITE.darker());
		contentPane.add(lblEmail);

		JLabel lblAddMinecraftAccount = new JLabel("Add Minecraft Account");
		lblAddMinecraftAccount.setBounds(67, 11, 157, 14);
		contentPane.add(lblAddMinecraftAccount);

		FlatTextBox box2 = new FlatTextBox(man, false);
		box2.setBounds(142, 50, 157, 20);
		contentPane.add(box2);

		JLabel lblPassword = new JLabel("Password:");
		lblPassword.setBounds(31, 81, 101, 14);
		lblPassword.setForeground(FlatColors.WHITE.darker());
		contentPane.add(lblPassword);

		FlatTextBox box = new FlatTextBox(man, true);
		box.setBounds(142, 75, 157, 20);
		contentPane.add(box);

		FlatButton btnAdd = new FlatButton("OK", FlatIconFont.CHECK, man);
		btnAdd.addActionListener(Priority.NORMAL, new de.tisan.flatui.components.flisteners.ActionListener() {

			@Override
			public void onAction(MouseClickedHandler arg0) {
				Vars.get().pass = box.getText();
				Vars.get().user = box2.getText();
				try {
					Vars.get().saveVars();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
				dispose();
				new GUIMain().setVisible(true);
			}
		});
		btnAdd.setBounds(210, 106, 89, 23);
		contentPane.add(btnAdd);

	}
}
