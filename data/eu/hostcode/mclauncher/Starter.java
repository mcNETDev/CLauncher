package eu.hostcode.mclauncher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Scanner;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;

import de.dawik.mclauncher.util.CLUtil;
import de.dawik.mclauncher.util.Login;
import de.dawik.mclauncher.util.MyProgressMonitor;
import de.dawik.mclauncher.util.Vars;
import eu.hostcode.mclauncher.gui.GUILaunchInfo;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import sk.tomsik68.mclauncher.api.common.mc.MinecraftInstance;
import sk.tomsik68.mclauncher.api.login.ISession;
import sk.tomsik68.mclauncher.api.versions.IVersion;
import sk.tomsik68.mclauncher.impl.login.legacy.LegacyProfile;
import sk.tomsik68.mclauncher.impl.versions.mcdownload.MCDownloadVersionList;

public class Starter {
	private static Process proc;

	public static void downloadAndStart(String pack, GUILaunchInfo info) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				String filePath = System.getenv("APPDATA") + "\\CLauncher\\Packs\\" + pack + "\\";
				File f = new File(filePath);
				if (!f.exists()) {
					f.mkdirs();
				}
				try {
					info.status.setText("Load latest update version");
					Scanner s = new Scanner(new URL(Main.BASE_URL + pack + "/latestversion.ver").openStream());
					int version = 0;
					int onlineVersion = 0;
					boolean doDownload = false;
					boolean existUpdate = true;
					boolean packExist = false;
					boolean update = true;
					if (s.hasNextLine()) {
						try {
							onlineVersion = Integer.valueOf(s.nextLine());
							if (onlineVersion < 0) {
								JOptionPane.showMessageDialog(null, "latestversion.ver file ist not correct", "ERROR", JOptionPane.ERROR_MESSAGE);
								update = false;
								existUpdate = false;
							}
						} catch (NumberFormatException ex) {
							JOptionPane.showMessageDialog(null, "latestversion.ver file ist not correct", "ERROR", JOptionPane.ERROR_MESSAGE);
							update = false;
							existUpdate = false;
						}
					}
					File fpackSettings = new File(filePath + "settings.json");
					if (fpackSettings.exists()) {
						JSONParser p = new JSONParser();
						JSONObject obj = (JSONObject) p.parse(fpackSettings.toURL().openStream());
						version = (int) obj.get("version");
						packExist = true;
					} else {
						existUpdate = false;
						doDownload = true;
					}
					info.status.setText("Compare Versions...");
					if (version < onlineVersion) {
						doDownload = true;
					}
					String minecraft_version = "";
					String forge_version = "";
					if (doDownload) {
						if (existUpdate) {
							info.status.setText("Update ?");
							info.l("INFO Found New version!");
							int i = JOptionPane.showConfirmDialog(null, "Update!", "Download new Version?", JOptionPane.YES_NO_OPTION);
							if (i == JOptionPane.NO_OPTION) {
								update = false;
							}
						}

						if (update) {
							info.status.setText("Download Pack...");
							URL website = new URL(Main.BASE_URL + pack + "/" + pack + "-" + onlineVersion + ".rar");
							ReadableByteChannel rbc = Channels.newChannel(website.openStream());
							File download = new File(filePath + "\\" + pack + "-" + onlineVersion + ".rar");
							if (download.exists()) {
								download.delete();
							}
							if (new File(filePath + "libraries\\").exists()) {
								FileUtils.deleteDirectory(new File(filePath + "libraries\\"));
							}
							if (fpackSettings.exists()) {
								fpackSettings.renameTo(new File(filePath + "settings- " + version + " .json"));
							}
							File fff = new File(filePath + "minecraft\\mods\\");
							info.status.setText("Delete old mods");
							if (fff.exists()) {
								FileUtils.deleteDirectory(fff);
							}
							FileOutputStream fos = new FileOutputStream(download);
							fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
							info.l("Downloaded!");

							rbc.close();
							fos.close();
							info.status.setText("Extract pack data");
							CLUtil.unRAR(filePath + pack + "-" + onlineVersion + ".rar", filePath);

							info.l("Ready unrar!");
							if (fpackSettings.exists()) {
								JSONParser p = new JSONParser();
								JSONObject obj = (JSONObject) p.parse(fpackSettings.toURL().openStream());
								forge_version = (String) obj.get("forge_version");
								minecraft_version = (String) obj.get("minecraft_version");
								System.out.println("Forge=" + forge_version);
								System.out.println("MC=" + minecraft_version);
							}

							File downloadlist = new File(filePath + "minecraft\\mods\\download.list");
							if (downloadlist.exists()) {
								Scanner sdl = new Scanner(downloadlist.toURL().openStream());
								while (sdl.hasNextLine()) {
									// TODO NOT WORKING
									String downloa = sdl.nextLine();
									if (downloa.startsWith("#")) {
										continue;
									}
									String name = downloa.substring(downloa.lastIndexOf("/") + 1);
									File mod = new File(filePath + "minecraft\\mods\\" + name);
									if (mod.exists()) {
										mod.delete();
									}
									ReadableByteChannel modchan = Channels.newChannel(new URL(downloa).openStream());
									FileOutputStream modout = new FileOutputStream(mod);
									modout.getChannel().transferFrom(modchan, 0, Long.MAX_VALUE);
									modout.close();
									modchan.close();
								}
							}
						}
					} else {
						info.l("Up to date");
					}
					if (fpackSettings.exists()) {
						JSONParser p = new JSONParser();
						JSONObject obj = (JSONObject) p.parse(fpackSettings.toURL().openStream());
						forge_version = (String) obj.get("forge_version");
						minecraft_version = (String) obj.get("minecraft_version");
					}

					if (!forge_version.isEmpty()) {
						String filePathf = System.getenv("APPDATA") + "\\CLauncher\\Forge\\" + minecraft_version + "\\" + forge_version + "\\";
						File ff = new File(filePathf);
						if (!ff.exists()) {
							ff.mkdirs();
						} // Download Forge
						File fdf = new File(filePathf + "download\\");
						if (!fdf.exists()) {
							fdf.mkdirs();
						}
						// Download forge
						String newForge = minecraft_version + "-" + forge_version;

						File installer = new File(filePathf + "download\\forge-" + newForge + "-installer.jar");
						if (!installer.exists()) {
							installer.getParentFile().mkdirs();
							URL forge = new URL("http://files.minecraftforge.net/maven/net/minecraftforge/forge/" + newForge + "/forge-" + newForge + "-installer.jar");
							ReadableByteChannel rbcc = Channels.newChannel(forge.openStream());
							FileOutputStream foss = new FileOutputStream(installer);
							foss.getChannel().transferFrom(rbcc, 0, Long.MAX_VALUE);
							rbcc.close();
							foss.close();
							info.l("Downloaded Forge!");
						}

						info.status.setText("Extract pack data");
						CLUtil.extractJar2(filePathf + "download\\forge-" + newForge + "-installer.jar", filePathf + "\\download\\");
						info.l("Ready forge unrar!");
						File funi = new File(filePathf + "download\\forge-" + minecraft_version + "-" + forge_version + "-universal.jar");
						File dir = new File(filePathf + "libraries\\");
						if (!dir.exists()) {
							dir.mkdirs();
						}
						File ffnew = new File(filePathf + "libraries\\forge-" + minecraft_version + "-" + forge_version + "-universal.jar");

						java.nio.file.Files.move(funi.toPath(), ffnew.toPath(), StandardCopyOption.ATOMIC_MOVE);
						CLUtil.downloadLibraries(filePathf);
						packExist = true;
					} else {
						info.l("CRITICAL FORGE NOT FOUND PACK CANT START");
						return;
					}
					if (packExist) {
						info.status.setText("Start Pack");
						// start mc
						String mcversion = "";
						if (fpackSettings.exists()) {
							JSONParser p = new JSONParser();
							JSONObject obj = (JSONObject) p.parse(fpackSettings.toURL().openStream());
							mcversion = (String) obj.get("minecraft_version");
							forge_version = (String) obj.get("forge_version");
							minecraft_version = (String) obj.get("minecraft_version");
						} else {
							info.l("no Minecraft Version Found! System will Exit now");
							System.exit(-1);
						}
						info.status.setText("Log into Minecraft...");
						ISession session = Login.login(new LegacyProfile(Vars.get().user, Vars.get().pass));
						MinecraftInstance mc = new MinecraftInstance(new File(filePath + "minecraft\\"));
						IVersion iversion = new MCDownloadVersionList(mc).retrieveVersionInfo(mcversion);
						info.status.setText("Install Minecraft assets... This may take a while");
						iversion.getInstaller().install(iversion, mc, new MyProgressMonitor());
						List<String> launch = iversion.getLauncher().getLaunchCommand(session, mc, null, iversion, new BaseLaunchSettings(),
								new BaseModdingProfile(filePath, minecraft_version, forge_version));
						String launchs = "";
						for (String ls : launch) {
							launchs += ls + " ";
						}
						System.out.println(launchs);
						// System.exit(0);

						ProcessBuilder pb = new ProcessBuilder(launch);
						info.l("Starting Minecraft...");
						info.status.setText("Start Minecraft...");

						// pb.redirectError(new
						// File("D:\\CLauncher\\mcerr.log"));
						// pb.redirectOutput(new
						// File("D:\\CLauncher\\mcout.log"));
						pb.directory(mc.getLocation());
						Starter.proc = pb.start();
						info.process = Starter.proc;
						info.status.setText("Start Minecraft Process...");
						BufferedReader br = new BufferedReader(new InputStreamReader(Starter.proc.getInputStream()));
						new Thread(new Runnable() {

							@Override
							public void run() {
								String line;
								while (Starter.proc.isAlive()) {
									try {
										line = br.readLine();
										if ((line != null) && (line.length() > 0)) {
											System.out.println(line);
											info.l("MINECRAFT: " + line);
										}
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
								info.status.setText("Minecraft Process Stopped!");
							}
						}).start();
						info.status.setText("Minecraft is running.");
					}
				} catch (Exception ex) {
					if (Starter.proc != null) {
						Starter.proc.destroy();
						info.l("ERROR " + ex.getMessage());
						info.l("Please restart minecraft!");
						Starter.proc = null;
					}
				}
			}
		}).start();
	}
}
