package de.dawik.mclauncher.util;

import java.io.File;

import sk.tomsik68.mclauncher.api.login.IProfile;
import sk.tomsik68.mclauncher.impl.common.Platform;
import sk.tomsik68.mclauncher.impl.login.yggdrasil.YDProfileIO;

public class GetProfiles {
	// https://github.com/tomsik68/mclauncher-api/wiki/API-Guide%3A-Profile-Saving%2CLoading
	public static IProfile[] get() {
		YDProfileIO io = new YDProfileIO(new File(Platform.getCurrentPlatform().getWorkingDirectory().getAbsolutePath()));
		IProfile[] profiles = null;
		try {
			profiles = io.read();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return profiles;
	}

}
