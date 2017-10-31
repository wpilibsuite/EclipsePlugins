package edu.wpi.first.wpilib.plugins.cpp;

import java.io.File;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IManagedBuildInfo;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.ITool;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
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

	private static final String USER_LIBS_PATH = "\"${WPILIB}/user/cpp/lib\"";
	private static final String NEW_LIBS_PATH = "\"${WPILIB}/common/current/lib/linux/athena/shared\"";
	private static final String OLD_LIBS_PATH = "\"${WPILIB}/cpp/current/lib\"";
	private static final String BETA1_LIBS_PATH = "\"${WPILIB}/cpp/current/lib/linux/athena/shared\"";
	private static final String REF_LIBS_PATH = "\"${WPILIB}/cpp/current/reflib/linux/athena/shared\"";
	private static final String USER_INCLUDE_PATH = "\"${WPILIB}/user/cpp/include\"";
	private static final String COMPILER_OPTIONS = "-c -fmessage-length=0 -pthread";
	private static final String LINKER_OPTIONS = "-pthread -Wl,-rpath,/opt/GenICam_v3_0_NI/bin/Linux32_ARM,-rpath,/usr/local/frc/lib";

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

	public String getCommonDir() {
		return WPILibCore.getDefault().getWPILibBaseDir()
				+ File.separator + "common" + File.separator + "current";
	}

	@Override
	public void earlyStartup() {
		new CPPInstaller(getCurrentVersion()).installIfNecessary(true);
		Properties props = WPILibCore.getDefault().getProjectProperties(null);
    	WPILibCore.getDefault().saveGlobalProperties(props);
		updateProjects();
	}

	public void updateProjects() {
		WPILibCPPPlugin.logInfo("Updating projects");

		// Get the root of the workspace
	    IWorkspace workspace = ResourcesPlugin.getWorkspace();
	    IWorkspaceRoot root = workspace.getRoot();
	    // Get all projects in the workspace
	    IProject[] projects = root.getProjects();
	    // Loop over all projects
	    for (IProject project : projects) {
			  try {
				  if(project.hasNature("edu.wpi.first.wpilib.plugins.core.nature.FRCProjectNature") && project.hasNature("org.eclipse.cdt.core.ccnature")){
					updateVariables(project);
				  }
			  } catch (CoreException e) {
				WPILibCPPPlugin.logError("Error updating projects.", e);
			  }
	    }
	}

	public void updateVariables(IProject project) {
		final IManagedBuildInfo buildInfo = ManagedBuildManager.getBuildInfo(project, true);
		final IConfiguration config = buildInfo.getDefaultConfiguration();
		final ITool[] tools = config.getRootFolderInfo().getTools();
		IOption option;

		for (final ITool tool : tools) {
			if (tool.getId().contains(".cpp.linker.")) {
				if(WPILibCore.getDefault().getManageLibraries())
				{
					option = tool.getOptionBySuperClassId("gnu.cpp.link.option.libs");
					if(option != null) {
						try {
							String[] libs = option.getLibraries();
							List<String> libList = new ArrayList<String>();

							File dir = new File(WPILibCore.getDefault().getWPILibBaseDir() + File.separator + "user" + File.separator + "cpp" + File.separator + "lib");
							File[] filesList = dir.listFiles();
							for (File file : filesList) {
								if (file.isFile()) {
									String[] splitFileName = file.getName().substring(3).split("[.]");
									if(splitFileName[splitFileName.length-1].equals("so") || splitFileName[splitFileName.length-1].equals("a"))
									{
										String libName = file.getName().substring(3, file.getName().length()-splitFileName[splitFileName.length-1].length()-1);
										if(!libList.contains(libName))
										{
											libList.add(libName);
											WPILibCPPPlugin.logInfo("Adding library to cpp link: " + file.getName());
										}
									}
								}
							}
							libList.add("wpi");
							option.setValue(libList.toArray(new String[libList.size()]));
						} catch (final BuildException e) {
							WPILibCPPPlugin.logError("Error retrieving library information from tool option", e);
						}
					}
				}

				try {
					option = tool.getOptionBySuperClassId("gnu.cpp.link.option.paths");
					String[] paths = option.getBasicStringListValue();
					List<String> libPathsList = new ArrayList<String>(Arrays.asList(paths));
					if(!libPathsList.contains(USER_LIBS_PATH))
					{
						libPathsList.add(USER_LIBS_PATH);
						option.setValue(libPathsList.toArray(new String[libPathsList.size()]));
					}
					if (!libPathsList.contains(REF_LIBS_PATH))
					{
						libPathsList.add(REF_LIBS_PATH);
						option.setValue(libPathsList.toArray(new String[libPathsList.size()]));
					}
					if (!libPathsList.contains(NEW_LIBS_PATH))
					{
						libPathsList.add(NEW_LIBS_PATH);
						option.setValue(libPathsList.toArray(new String[libPathsList.size()]));
					}
					if (libPathsList.contains(OLD_LIBS_PATH))
					{
						libPathsList.remove(OLD_LIBS_PATH);
						option.setValue(libPathsList.toArray(new String[libPathsList.size()]));
					}
					if (libPathsList.contains(BETA1_LIBS_PATH))
					{
						libPathsList.remove(BETA1_LIBS_PATH);
						option.setValue(libPathsList.toArray(new String[libPathsList.size()]));
					}
				} catch (final BuildException e) {
					WPILibCPPPlugin.logError("Error checking library paths", e);
				}

				try {
					option = tool.getOptionBySuperClassId("gnu.cpp.link.option.flags");
					option.setValue(LINKER_OPTIONS);
				} catch (final BuildException e) {
					WPILibCPPPlugin.logError("Error updating linker options", e);
				}
			} else if (tool.getId().contains(".cpp.compiler")) {
				try {
					option = tool.getOptionBySuperClassId("gnu.cpp.compiler.option.include.paths");
					String[] paths = option.getBasicStringListValue();
					List<String> includePathsList = new ArrayList<String>(Arrays.asList(paths));
					if(!includePathsList.contains(USER_INCLUDE_PATH))
					{
						includePathsList.add(USER_INCLUDE_PATH);
						option.setValue(includePathsList.toArray(new String[includePathsList.size()]));
					}
				} catch (final BuildException e) {
					WPILibCPPPlugin.logError("Error checking include paths", e);
				}

				try {
					option = tool.getOptionBySuperClassId("gnu.cpp.compiler.option.other.other");
					option.setValue(COMPILER_OPTIONS);
				} catch (final BuildException e) {
					WPILibCPPPlugin.logError("Error updating compiler options", e);
				}
			}
		}
	}

	public static void logInfo(String msg) {
		getDefault().getLog().log(new Status(Status.INFO, PLUGIN_ID, Status.OK, msg, null));
	}

	public static void logError(String msg, Exception e) {
		getDefault().getLog().log(new Status(Status.ERROR, PLUGIN_ID, Status.OK, msg, e));
	}
}
