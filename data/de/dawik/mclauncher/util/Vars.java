package de.dawik.mclauncher.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class Vars {
	public String user = "NO";
	public String pass = "NO";
	private String fileName;
	private File file;
	private static Vars instance;

	public static Vars get() {
		return (Vars.instance == null ? (Vars.instance = new Vars()) : Vars.instance);
	}

	private Vars() {
		try {
			fileName = System.getenv("APPDATA") + "\\CLauncher\\settings.ini";
			file = new File(fileName);
			if (!file.exists()) {
				new File(System.getenv("APPDATA") + "\\CLauncher\\").mkdirs();
				file.createNewFile();
				saveVars();
			}
			loadVars();
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
	}

	public void loadVars() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		System.out.println(reader.ready());
		ArrayList<String> lines = new ArrayList<String>();
		String l = "";
		while ((l = reader.readLine()) != null) {
			if (!l.startsWith("#")) {
				lines.add(l);
			}
		}
		reader.close();

		for (String ll : lines) {
			String prefix = ll.split("=")[0];
			String args;
			if (ll.split("=").length > 1) {
				args = ll.split("=")[1];
			} else {
				args = "";
			}
			switch (prefix) {
			case "user":
				user = args;
				break;
			case "pass":
				pass = args;
				break;
			}
		}
	}

	public void saveVars() throws IOException, URISyntaxException {
		BufferedWriter w = new BufferedWriter(new FileWriter(file));
		w.write("# Settings for CLauncher Client." + "\r\n");
		w.write("# by Daniel Wieckhorst (mcNET(Dev))" + "\r\n");
		w.write("# Please dont change nothing within this file. Use the options menu instead!" + "\r\n");
		w.write("user=" + user + "\r\n");
		w.write("pass=" + pass + "\r\n");
		w.flush();
		w.close();
	}
}
