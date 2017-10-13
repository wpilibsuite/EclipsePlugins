package edu.wpi.first.wpilib.plugins.core.installer;

import java.io.InputStream;
import org.eclipse.jface.preference.IPreferenceStore;

import edu.wpi.first.wpilib.plugins.core.WPILibCore;
import edu.wpi.first.wpilib.plugins.core.preferences.PreferenceConstants;

public class CommonInstaller extends AbstractInstaller {

	public CommonInstaller(String version) {
		super(version, WPILibCore.getDefault().getPreferenceStore().getString(PreferenceConstants.LIBRARY_INSTALLED), WPILibCore.getDefault().getWPILibBaseDir() + "/common/current");
	}

	@Override
	protected String getFeatureName() {
		return "common";
	}

	@Override
	protected void updateInstalledVersion(String version) {
		IPreferenceStore prefs = WPILibCore.getDefault().getPreferenceStore();
		prefs.setValue(PreferenceConstants.LIBRARY_INSTALLED, version);

	}

	@Override
	protected InputStream getInstallResourceStream() {
		return CommonInstaller.class.getResourceAsStream("/resources/common.zip");
	}
}
