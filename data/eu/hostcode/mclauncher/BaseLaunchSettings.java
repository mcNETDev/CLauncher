package eu.hostcode.mclauncher;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import sk.tomsik68.mclauncher.api.common.ILaunchSettings;

public class BaseLaunchSettings implements ILaunchSettings {

	@Override
	public boolean isModifyAppletOptions() {
		return false;
	}

	@Override
	public File getJavaLocation() {
		return null;
	}

	@Override
	public List<String> getJavaArguments() {
		return Arrays.asList("-XX:+UseConcMarkSweepGC", "-XX:+CMSIncrementalMode", "-XX:-UseAdaptiveSizePolicy", "-Xmn128M");
	}

	@Override
	public String getInitHeap() {
		return "512M";
	}

	@Override
	public String getHeap() {
		return "4G";
	}

	@Override
	public Map<String, String> getCustomParameters() {
		return null;
	}

	@Override
	public List<String> getCommandPrefix() {
		return null;
	}

}
