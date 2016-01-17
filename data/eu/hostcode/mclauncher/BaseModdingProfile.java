package eu.hostcode.mclauncher;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.dawik.mclauncher.util.CLUtil;
import sk.tomsik68.mclauncher.api.mods.IModdingProfile;

public class BaseModdingProfile implements IModdingProfile {
	private String filePath;
	private String mcv = "NOTSET";
	private String fv = "NOTSET";

	public BaseModdingProfile(String filePath, String minecraft_version, String forge_version) {
		this.filePath = filePath;
		mcv = minecraft_version;
		fv = forge_version;
	}

	@Override
	public File[] injectBeforeLibs(String paramString) {
		File[] f = new File[] {};
		ArrayList<File> files = new ArrayList<File>();
		try {
			String filePath2 = System.getenv("APPDATA") + "\\CLauncher\\Forge\\" + mcv + "\\" + fv + "\\libraries\\";
			File ff = new File(filePath2);
			if (!ff.exists()) {
				ff.mkdirs();
			}
			CLUtil.listJars(filePath2, files);
			CLUtil.listJars(filePath + "minecraft\\libraries\\", files);
			for (File sf : files) {
				System.out.println(sf.getAbsolutePath());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		f = files.toArray(new File[files.size()]);
		return f;
	}

	@Override
	public File[] injectAfterLibs(String paramString) {
		return null;
	}

	@Override
	public boolean isLibraryAllowed(String paramString) {
		return true;
	}

	@Override
	public File getCustomGameJar() {
		return null;
	}

	@Override
	public String getMainClass() {
		return "net.minecraft.launchwrapper.Launch";
	}

	@Override
	public String[] changeMinecraftArguments(String[] paramArrayOfString) {
		return null;
	}

	@Override
	public List<String> getLastParameters() {
		return Arrays.asList(new String[] { "--tweakClass", "net.minecraftforge.fml.common.launcher.FMLTweaker" });
	}
}
