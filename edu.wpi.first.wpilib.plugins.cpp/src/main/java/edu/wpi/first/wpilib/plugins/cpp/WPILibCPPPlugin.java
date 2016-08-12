package edu.wpi.first.wpilib.plugins.cpp;

import java.io.File;
import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.cdt.core.cdtvariables.IUserVarSupplier;
import org.eclipse.cdt.core.cdtvariables.IStorableCdtVariables;
import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import edu.wpi.first.wpilib.plugins.core.WPILibCore;
import edu.wpi.first.wpilib.plugins.core.ant.AntPropertiesParser;
import edu.wpi.first.wpilib.plugins.cpp.installer.CPPInstaller;
import edu.wpi.first.wpilib.plugins.cpp.preferences.PreferenceConstants;

/**
 * The activator class controls the plug-in life cycle
 */
public class WPILibCPPPlugin extends AbstractUIPlugin implements IStartup {

	// The plug-in ID
	public static final String PLUGIN_ID = "WPILib_CPP_Robot_Development"; //$NON-NLS-1$

	// The shared instance
	private static WPILibCPPPlugin plugin;
	
	/**
	 * The constructor
	 */
	public WPILibCPPPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static WPILibCPPPlugin getDefault() {
		return plugin;
	}

	public String getCurrentVersion() {
		try {
			Properties props = new AntPropertiesParser(WPILibCPPPlugin.class.getResourceAsStream("/resources/configuration.properties")).getProperties();
			if (props.getProperty("version").startsWith("$")) {
				return "DEVELOPMENT";
			} else {
				return props.getProperty("version");
			}
		} catch (CoreException e) {
			return "DEVELOPMENT";
		}
	}

	public String getCPPDir() {
		return WPILibCore.getDefault().getWPILibBaseDir()
				+ File.separator + "cpp" + File.separator + "current";
	}

	@Override
	public void earlyStartup() {
		new CPPInstaller(getCurrentVersion()).installIfNecessary(true);
		Properties props = WPILibCore.getDefault().getProjectProperties(null);
    	WPILibCore.getDefault().saveGlobalProperties(props);
		updateVariables();
	}

	@Override
	public void startup() {
		
	}
	
	private void updateVariables() {
		try {
			
			IUserVarSupplier varSupplier = CCorePlugin.getDefault().getUserVarSupplier();
			IStorableCdtVariables vars = varSupplier.getWorkspaceVariablesCopy();
			
			File dir = new File(WPILibCore.getDefault().getWPILibBaseDir() + File.separator + "user" + File.separator + "cpp" + File.separator + "lib");
			File[] filesList = dir.listFiles();
			String libNames = "";
			for (File file : filesList) {
				if (file.isFile()) {
					libNames += "-l" + file.getName().substring(3).split("[.]")[0] + " ";
					WPILibCPPPlugin.logInfo(file.getName());
				}
			}
			WPILibCPPPlugin.logInfo(libNames);
			vars.createMacro("WPI_USER_LIBS", 1, libNames);
			varSupplier.setWorkspaceVariables(vars);
		} catch (CoreException e) {
			WPILibCPPPlugin.logError("Error updating CPP Variables", e);
		}
	}

	public static void logInfo(String msg) {
		getDefault().getLog().log(new Status(Status.INFO, PLUGIN_ID, Status.OK, msg, null));
	}

	public static void logError(String msg, Exception e) {
		getDefault().getLog().log(new Status(Status.ERROR, PLUGIN_ID, Status.OK, msg, e));
	}
}
