package de.dawik.mclauncher.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;

import org.tukaani.xz.XZInputStream;

import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.impl.FileVolumeManager;
import com.github.junrar.rarfile.FileHeader;
import com.google.common.io.Files;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

public class CLUtil {
	public static void listf(String directoryName, ArrayList<File> files, boolean withFolders) throws IOException {
		File directory = new File(directoryName);
		File[] fList = directory.listFiles();
		if (fList != null) {
			for (File file : fList) {
				if (file.isFile()) {
					files.add(file);
				} else if (file.isDirectory()) {
					CLUtil.listf(file.getAbsolutePath(), files, withFolders);
					if (withFolders) {
						files.add(file);
					}
				}
			}
		}
	}

	public static void listJars(String directoryName, ArrayList<File> files) throws IOException {
		File directory = new File(directoryName);
		File[] fList = directory.listFiles();
		if (fList != null) {
			for (File file : fList) {
				if (file.isFile() && file.getName().endsWith(".jar")) {
					files.add(file);
				} else if (file.isDirectory()) {
					CLUtil.listJars(file.getAbsolutePath(), files);
				}
			}
		}
	}

	public static void unRAR(String file, String to) throws IOException {
		File f = new File(file);
		Archive a = null;
		try {
			a = new Archive(new FileVolumeManager(f));
		} catch (RarException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (a != null) {
			a.getMainHeader().print();
			FileHeader fh = a.nextFileHeader();
			while (fh != null) {
				try {
					if (fh.isDirectory()) {
						new File(to + fh.getFileNameString().trim()).mkdirs();
					} else {
						File out = new File(to + fh.getFileNameString().trim());
						if (!out.getParentFile().exists()) {
							System.out.println("create dir " + out.getParentFile().getAbsolutePath());
							out.getParentFile().mkdirs();
						}
						System.out.println(out.getAbsolutePath());
						FileOutputStream os = new FileOutputStream(out);
						a.extractFile(fh, os);
						os.close();
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (RarException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				fh = a.nextFileHeader();
			}
			a.close();
		}
	}

	public static void extractJar2(String file, String to) throws IOException {
		JarFile jfile = new JarFile(new File(file));
		Enumeration<JarEntry> e = jfile.entries();
		while (e.hasMoreElements()) {
			JarEntry ent = e.nextElement();
			File f = new File(to + "\\" + ent.getName());
			if (!f.exists()) {
				System.out.println("Create dir" + f.getAbsolutePath());
				f.getParentFile().mkdirs();
			}
			if (ent.isDirectory()) {
				f.mkdirs();
				System.out.println("Create dirs" + f.getAbsolutePath());
				continue;
			}
			ReadableByteChannel modchan = Channels.newChannel(jfile.getInputStream(ent));
			FileOutputStream modout = new FileOutputStream(f);
			modout.getChannel().transferFrom(modchan, 0, Long.MAX_VALUE);
			modout.close();
			modchan.close();
		}
	}

	public static void extractJar(String file, String to) throws IOException {
		JarFile jfile = new JarFile(new File(file));
		Enumeration<JarEntry> e = jfile.entries();
		while (e.hasMoreElements()) {
			JarEntry ent = e.nextElement();
			File f = new File(to + "\\" + ent.getName());
			if (!f.exists()) {
				System.out.println("Create dir" + f.getAbsolutePath());
				f.getParentFile().mkdirs();
			}
			if (ent.isDirectory()) {
				f.mkdirs();
				System.out.println("Create dirs" + f.getAbsolutePath());
				continue;
			}
			InputStream in = jfile.getInputStream(ent);
			FileOutputStream fos = new FileOutputStream(f);
			while (in.available() > 0) {
				fos.write(in.read());
			}
			fos.close();
			in.close();
		}
	}

	public static void downloadLibraries(String file) throws MalformedURLException, ParseException, IOException {
		File launcherProfiles = new File(file + "download\\install_profile.json");
		JSONParser p = new JSONParser();
		JSONObject obj = (JSONObject) p.parse(launcherProfiles.toURL().openStream());
		JSONObject versionInfo = (JSONObject) obj.get("versionInfo");
		JSONArray libraries = (JSONArray) versionInfo.get("libraries");
		for (Object lib2 : libraries) {
			JSONObject lib = (JSONObject) lib2;
			// if (lib.containsKey("clientreq")) {
			// Boolean client = (Boolean) lib.get("clientreq");
			// if (client) {
			String name = (String) lib.get("name");
			if (lib.containsKey("url")) {
				CLUtil.downloadMaven(name, new File(file + "libraries\\"), (String) lib.get("url"));
			} else {
				CLUtil.downloadMaven(name, new File(file + "libraries\\"));
			}
			// }
			// }
		}

		// System.exit(0);
	}

	private static void downloadMaven(String name, File folder, String... url) throws IOException {
		if (name.startsWith("net.minecraftforge:forge:")) {
			return;
		}
		String urll = "https://libraries.minecraft.net/";
		if ((url.length > 0) && (url[0] != null)) {
			urll = url[0];
		}
		String finalDownload = urll;
		String[] data = name.split(":");
		// System.out.println(Arrays.toString(data));
		if (data[0].contains(".")) {
			String[] packages = data[0].split("\\.");
			// System.out.println(Arrays.toString(packages));

			for (String packa : packages) {
				finalDownload += packa + "/";
			}
		} else {
			finalDownload += data[0] + "/";
		}
		finalDownload += data[1] + "/" + data[2] + "/" + data[1] + "-" + data[2] + ".jar";
		System.out.println(finalDownload);
		String filename = data[1] + "-" + data[2] + ".jar";
		try {
			File outF = new File(folder.getAbsolutePath() + "\\" + filename);
			ReadableByteChannel rbcc = Channels.newChannel(new URL(finalDownload).openStream());
			if (outF.exists()) {
				outF.delete();
			}
			if (!outF.getParentFile().exists()) {
				outF.getParentFile().mkdirs();
			}
			outF.createNewFile();
			FileOutputStream foss = new FileOutputStream(outF);
			foss.getChannel().transferFrom(rbcc, 0, Long.MAX_VALUE);
			rbcc.close();
			foss.close();
			System.out.println("Downloaded Lib to " + outF.getAbsolutePath());
		} catch (FileNotFoundException e) {
			finalDownload += ".pack.xz";
			String filenameN = filename + ".pack.xz";
			File outF = new File(folder.getAbsolutePath() + "\\" + filenameN);
			ReadableByteChannel rbcc = Channels.newChannel(new URL(finalDownload).openStream());
			if (outF.exists()) {
				outF.delete();
			}
			if (!outF.getParentFile().exists()) {
				outF.getParentFile().mkdirs();
			}
			outF.createNewFile();
			FileOutputStream foss = new FileOutputStream(outF);
			foss.getChannel().transferFrom(rbcc, 0, Long.MAX_VALUE);
			rbcc.close();
			foss.close();
			System.out.println("Failed to download Lib " + filenameN + " from " + finalDownload);

			CLUtil.unPackZX(outF, filename);

		}

	}

	private static void unPackZX(File outF, String name) throws IOException {
		String newF = outF.getParentFile().getAbsolutePath() + "\\" + name;
		System.out.println(newF);
		CLUtil.unpackLibrary(new File(newF), Files.toByteArray(outF));
	}

	public static void unpackLibrary(File output, byte[] data) throws IOException {
		if (output.exists()) {
			output.delete();
		}

		byte[] decompressed = CLUtil.readFully(new XZInputStream(new ByteArrayInputStream(data)));

		// Snag the checksum signature
		String end = new String(decompressed, decompressed.length - 4, 4);
		if (!end.equals("SIGN")) {
			System.out.println("Unpacking failed, signature missing " + end);
			return;
		}

		int x = decompressed.length;
		int len = ((decompressed[x - 8] & 0xFF)) | ((decompressed[x - 7] & 0xFF) << 8) | ((decompressed[x - 6] & 0xFF) << 16) | ((decompressed[x - 5] & 0xFF) << 24);

		File temp = File.createTempFile("art", ".pack");
		System.out.println("  Signed");
		System.out.println("  Checksum Length: " + len);
		System.out.println("  Total Length:    " + (decompressed.length - len - 8));
		System.out.println("  Temp File:       " + temp.getAbsolutePath());

		byte[] checksums = Arrays.copyOfRange(decompressed, decompressed.length - len - 8, decompressed.length - 8);

		// As Pack200 copies all the data from the input, this creates duplicate
		// data in memory.
		// Which on some systems triggers a OutOfMemoryError, to counter this,
		// we write the data
		// to a temporary file, force GC to run {I know, eww} and then unpack.
		// This is a tradeoff of disk IO for memory.
		// Should help mac users who have a lower standard max memory then the
		// rest of the world (-.-)
		OutputStream out = new FileOutputStream(temp);
		out.write(decompressed, 0, decompressed.length - len - 8);
		out.close();
		decompressed = null;
		data = null;
		System.gc();

		FileOutputStream jarBytes = new FileOutputStream(output);
		JarOutputStream jos = new JarOutputStream(jarBytes);

		Pack200.newUnpacker().unpack(temp, jos);

		JarEntry checksumsFile = new JarEntry("checksums.sha1");
		checksumsFile.setTime(0);
		jos.putNextEntry(checksumsFile);
		jos.write(checksums);
		jos.closeEntry();

		jos.close();
		jarBytes.close();
		temp.delete();
	}

	public static byte[] readFully(InputStream stream) throws IOException {
		byte[] data = new byte[4096];
		ByteArrayOutputStream entryBuffer = new ByteArrayOutputStream();
		int len;
		do {
			len = stream.read(data);
			if (len > 0) {
				entryBuffer.write(data, 0, len);
			}
		} while (len != -1);

		return entryBuffer.toByteArray();
	}
}