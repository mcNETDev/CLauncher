package de.dawik.mclauncher.util;

import eu.hostcode.mclauncher.gui.GUILaunchInfo;
import sk.tomsik68.mclauncher.api.ui.IProgressMonitor;

public class MyProgressMonitor implements IProgressMonitor {

	@Override
	public void setStatus(String paramString) {
		GUILaunchInfo.get().l(paramString);
	}

	@Override
	public void setProgress(int paramInt) {
	}

	@Override
	public void setMax(int paramInt) {
	}

	@Override
	public void incrementProgress(int paramInt) {
	}

}
