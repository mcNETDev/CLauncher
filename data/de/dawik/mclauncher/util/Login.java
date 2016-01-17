package de.dawik.mclauncher.util;

import sk.tomsik68.mclauncher.api.login.IProfile;
import sk.tomsik68.mclauncher.api.login.ISession;
import sk.tomsik68.mclauncher.impl.common.Platform;
import sk.tomsik68.mclauncher.impl.login.yggdrasil.YDLoginService;

public class Login {

	public static ISession login(IProfile prof) {
		ISession session = null;
		try {
			YDLoginService loginService = new YDLoginService();
			loginService.load(Platform.getCurrentPlatform().getWorkingDirectory());
			session = loginService.login(prof);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return session;
	}

	public static void logout(ISession se) {
		YDLoginService loginService = new YDLoginService();
		try {
			loginService.logout(se);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
