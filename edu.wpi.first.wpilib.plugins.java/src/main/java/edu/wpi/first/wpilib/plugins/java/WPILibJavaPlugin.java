package edu.wpi.first.wpilib.plugins.java;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IAccessRule;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import edu.wpi.first.wpilib.plugins.core.WPILibCore;
import edu.wpi.first.wpilib.plugins.core.ant.AntPropertiesParser;
import edu.wpi.first.wpilib.plugins.java.installer.JavaInstaller;

/**
 * The activator class controls the plug-in life cycle
 */
public class WPILibJavaPlugin extends AbstractUIPlugin implements IStartup {

	// The plug-in ID
	public static final String PLUGIN_ID = "WPILib_Java_Robot_Development"; //$NON-NLS-1$

	// The shared instance
	private static WPILibJavaPlugin plugin;

	private static final String[] wpiClasspathLibraries = {
		"wpilib",
		"networktables",
		"opencv",
		"cscore",
		"wpiutil"
	};

	/**
	 * The constructor
	 */
	public WPILibJavaPlugin() {
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
	public static WPILibJavaPlugin getDefault() {
		return plugin;
	}

	public String getCurrentVersion() {
		try {
			Properties props = new AntPropertiesParser(WPILibJavaPlugin.class.getResourceAsStream("/resources/configuration.properties")).getProperties();
			if (props.getProperty("version").startsWith("$")) {
				return "DEVELOPMENT";
			} else {
				return props.getProperty("version");
			}
		} catch (CoreException e) {
			WPILibJavaPlugin.logError("Error getting properties.", e);
			return "DEVELOPMENT";
		}
	}
	public String getJavaPath() {
		return WPILibCore.getDefault().getWPILibBaseDir()
				+ File.separator + "java" + File.separator + "current";
	}

	public Properties getProjectProperties(IProject project) {
		Properties defaults = WPILibCore.getDefault().getProjectProperties(project);
		Properties props;
		try {
			File file = new File(WPILibCore.getDefault().getWPILibBaseDir()+"/java/current/ant/build.properties");
			props = new AntPropertiesParser(new FileInputStream(file)).getProperties(defaults);
		} catch (Exception e) {
			WPILibJavaPlugin.logError("Error getting properties.", e);
			props = new Properties(defaults);
		}
		return props;
	}

	public void updateProjects() {
		WPILibJavaPlugin.logInfo("Updating projects");

			// Get the root of the workspace
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IWorkspaceRoot root = workspace.getRoot();
			// Get all projects in the workspace
			IProject[] projects = root.getProjects();
			// Loop over all projects
			for (IProject project : projects) {
				try {
					if(project.hasNature("edu.wpi.first.wpilib.plugins.core.nature.FRCProjectNature") && project.hasNature("org.eclipse.jdt.core.javanature")){
					updateVariables(project);
					} else {
					}
				} catch (CoreException e) {
				WPILibJavaPlugin.logError("Error updating projects.", e);
				}
			}
	}

	public void updateVariables(IProject project) throws CoreException {
		Properties props = WPILibJavaPlugin.getDefault().getProjectProperties(project);

		IJavaProject javaProject = JavaCore.create(project);

		// Ensure the classpath has all the proper libraries
		try {
			boolean[] foundLibraries = new boolean[wpiClasspathLibraries.length];

			List<IClasspathEntry> newClasspathList = new ArrayList<IClasspathEntry>(Arrays.asList(javaProject.getRawClasspath()));
			for (IClasspathEntry entry : newClasspathList) {
				// Only grab var entries, and check to find if they're in our list
				if (entry.getEntryKind() != IClasspathEntry.CPE_VARIABLE) {
					continue;
				}
				IPath path = entry.getPath();
				for (int i = 0; i < wpiClasspathLibraries.length; i++) {
					if (path.equals(new Path(wpiClasspathLibraries[i]))) {
						// Found existing classpath.
						foundLibraries[i] = true;
						break;
					}
				}
			}

			IAccessRule[] emptyAccessArray = new IAccessRule[0];
			IClasspathAttribute[] emptySourceAttribute = new IClasspathAttribute[0];
			for (int i = 0; i < foundLibraries.length; i++) {
				if (!foundLibraries[i]) {
					// If not found, add to classpath
					newClasspathList.add(JavaCore.newVariableEntry(new Path(wpiClasspathLibraries[i]),
							new Path(wpiClasspathLibraries[i] + ".sources"), null, emptyAccessArray, emptySourceAttribute, false));
					WPILibJavaPlugin.logInfo("Adding: " + wpiClasspathLibraries[i] + " to project: " + project.getName());
				}
			}

			javaProject.setRawClasspath(newClasspathList.toArray(new IClasspathEntry[newClasspathList.size()]), null);
		} catch (JavaModelException e) {
			WPILibJavaPlugin.logError("Error creating wpilib classpath", e);
		}

		//Update variables for wpilib, ntcore, wpiutil, cscore and opencv
		try {
			JavaCore.setClasspathVariable("wpilib", new Path(props.getProperty("wpilib.jar")), null);
			JavaCore.setClasspathVariable("wpilib.sources", new Path(props.getProperty("wpilib.sources")), null);
			JavaCore.setClasspathVariable("networktables", new Path(props.getProperty("ntcore.jar")), null);
			JavaCore.setClasspathVariable("networktables.sources", new Path(props.getProperty("ntcore.sources")), null);
			JavaCore.setClasspathVariable("wpiutil", new Path(props.getProperty("wpiutil.jar")), null);
			JavaCore.setClasspathVariable("wpiutil.sources", new Path(props.getProperty("wpiutil.sources")), null);
			JavaCore.setClasspathVariable("cscore", new Path(props.getProperty("cscore.jar")), null);
			JavaCore.setClasspathVariable("cscore.sources", new Path(props.getProperty("cscore.sources")), null);
			JavaCore.setClasspathVariable("opencv", new Path(props.getProperty("opencv.jar")), null);
			JavaCore.setClasspathVariable("opencv.sources", new Path(props.getProperty("opencv.sources")), null);
			JavaCore.setClasspathVariable("USERLIBS_DIR", new Path(WPILibCore.getDefault().getWPILibBaseDir() + File.separator + "user" + File.separator + "java" + File.separator + "lib"), null);
		} catch (JavaModelException|NullPointerException e) {
			// Classpath variables didn't get set
			WPILibJavaPlugin.logError("Error setting classpath variables", e);
		}

		//Loop through files in $WPILIB$\\user\\java\\lib and add all jars to classpath
		if(WPILibCore.getDefault().getManageLibraries())
		{
			try{
				List<IClasspathEntry> newClasspathList = new ArrayList<IClasspathEntry>(Arrays.asList(javaProject.getRawClasspath()));
				File dir = new File(WPILibCore.getDefault().getWPILibBaseDir() + File.separator + "user" + File.separator + "java" + File.separator + "lib");
				File[] filesList = dir.listFiles();
				IPath userLibPath = new Path("USERLIBS_DIR/");
				for(Iterator<IClasspathEntry> iterator = newClasspathList.iterator(); iterator.hasNext();)
				{
					IClasspathEntry classpathEntry = iterator.next();
					if(userLibPath.isPrefixOf(classpathEntry.getPath()))
					{
						File f = new File(dir.getPath() + File.separator + classpathEntry.getPath().lastSegment());
						if(!f.exists())
						{
							WPILibJavaPlugin.logInfo("Library missing, removing: " + classpathEntry.getPath().lastSegment());
							iterator.remove();
						}
					}
				}

				for (File file : filesList) {
					if (file.isFile()) {
						String fileNameSplit[] = file.getName().split("[.]");
						if(fileNameSplit[fileNameSplit.length-1].equalsIgnoreCase("jar"))
						{
							IPath filePath = new Path("USERLIBS_DIR/" + file.getName());
							boolean alreadyAdded = false;
							for(IClasspathEntry entry : newClasspathList)	//check if file is already on path
							{
								if(entry.getPath().equals(filePath))
								{
									alreadyAdded = true;
								}
							}
							if(!alreadyAdded)
							{
								newClasspathList.add(JavaCore.newVariableEntry(filePath, null, null, false));
								WPILibJavaPlugin.logInfo("Adding: " + file.getName() + " to project: " + project.getName());
							}
						}
					}
				}
				javaProject.setRawClasspath(newClasspathList.toArray(new IClasspathEntry[newClasspathList.size()]), null);
			} catch (JavaModelException e) {
				WPILibJavaPlugin.logError("Error updating classpath", e);
			}
		}
	}

	@Override
	public void earlyStartup() {
		new JavaInstaller(getCurrentVersion()).installIfNecessary(true);
		Properties props = WPILibCore.getDefault().getProjectProperties(null);
		WPILibCore.getDefault().saveGlobalProperties(props);
		updateProjects();
	}

	public static void logInfo(String msg) {
		getDefault().getLog().log(new Status(Status.INFO, PLUGIN_ID, Status.OK, msg, null));
	}

	public static void logError(String msg, Exception e) {
		getDefault().getLog().log(new Status(Status.ERROR, PLUGIN_ID, Status.OK, msg, e));
	}
}
