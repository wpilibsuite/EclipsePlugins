package edu.wpi.first.wpilib.plugins.cpp.launching;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.io.File;

import org.eclipse.cdt.debug.core.ICDTLaunchConfigurationConstants;
import org.eclipse.cdt.debug.core.executables.Executable;
import org.eclipse.cdt.debug.core.executables.ExecutablesManager;
import org.eclipse.cdt.debug.mi.core.IMILaunchConfigurationConstants;
import org.eclipse.cdt.launch.remote.IRemoteConnectionConfigurationConstants;
import org.eclipse.core.internal.resources.Resource;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;

import edu.wpi.first.wpilib.plugins.core.WPILibCore;
import edu.wpi.first.wpilib.plugins.cpp.WPILibCPPPlugin;
import edu.wpi.first.wpilib.plugins.core.launching.AntLauncher;

/**
 * Launch shortcut base functionality, common for deploying to the robot.
 * Retrieves the project the operation is being called on, and runs the correct
 * ant targets based on polymorphically determined data values
 *
 * @author Ryan O'Meara
 * @author Alex Henning
 */
@SuppressWarnings("restriction")
public class DeployLaunchShortcut implements ILaunchShortcut
{
	// Class constants - used to delineate types for launch shortcuts
	public static final String DEPLOY_TYPE = "edu.wpi.first.wpilib.plugins.core.deploy";
	
	private static final int DEBUG_KILL_WAIT_TRIES = 40;
	private static final int DEBUG_KILL_WAIT_MS = 500;

	/**
	 * Returns the launch type of the shortcut that was used, one of the
	 * constants defined in BaseLaunchShortcut
	 *
	 * @return Launch shortcut type
	 */
	public String getLaunchType()
	{
		return DEPLOY_TYPE;
	}

	@Override
	public void launch(ISelection selection, String mode)
	{
		// Extract resource from selection
		StructuredSelection sel = (StructuredSelection) selection;
		IProject activeProject = null;
		if (sel.getFirstElement() instanceof IProject) {
			activeProject = (IProject) sel.getFirstElement();
		} else {
			WPILibCPPPlugin.logError("Selection isn't a project: "+sel.toString(), null);
			return;
		}

		// Run config using project found in extracted resource, with indicated
		// mode
		runConfig(activeProject, mode, PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
	}

	@Override
	public void launch(IEditorPart editor, String mode)
	{
		// Extract resource from editor
		if (editor != null)	{
			IFileEditorInput input = (IFileEditorInput) editor.getEditorInput();
			IFile file = input.getFile();
			IProject activeProject = file.getProject();

			// If editor existed, run config using extracted resource in
			// indicated mode
			runConfig(activeProject, mode, editor.getSite().getWorkbenchWindow().getShell());
		} else {
			WPILibCPPPlugin.logError("Editor was null.", null);
			}

	}

	/**
	 *
	 * @param activeProj
	 *            The project that the script will be run on/from
	 * @param mode
	 *            The mode it will be run in (ILaunchManager.RUN_MODE or
	 *            ILaunchManager.DEBUG_MODE)
	 */
	public void runConfig(IProject activeProj, String mode, Shell shell) {

    // Checks to see if there are any build errors remaining.
    boolean buildSucceeded = true;
    // Unfortunately, build() does not return whether or not the
    // build succeded, so we instead must check the markers for
    // errors and hope that we don't accidentally catch any
    // false-positives.
	
	WPILibCPPPlugin.getDefault().updateVariables(activeProj);
	
    try {
      activeProj.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
    }
    catch (CoreException e) {
      buildSucceeded = false;
      WPILibCPPPlugin.logError("Build failed.", e);
    }

    try {
      IMarker[] problems = activeProj.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
      for (int i = 0; i < problems.length; i++) {
        if (problems[i].getAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO)
              == IMarker.SEVERITY_ERROR
            && problems[i].getType() == "org.eclipse.cdt.core.problem") {
          buildSucceeded = false;
          break;
        }
      }
    }
    catch (CoreException e) {
      buildSucceeded = false;
      WPILibCPPPlugin.logError("Failed to check whether the build worked.", e);
    }

    if (!buildSucceeded) {
      boolean cont = MessageDialog.openQuestion(shell, "Build Failed",
          "The build failed; Are you sure that you want to continue the deploy?");
      if (!cont) return;
    }

    // Check to ensure that there really is a binary file to upload; this is
    // only really relevant if the build fails, but we check anyways.
		Collection<Executable> exes =
        ExecutablesManager.getExecutablesManager().getExecutablesForProject(activeProj);
    if (!(exes.size() > 1
        || new File(activeProj.getLocation().toOSString() + File.separator
                    + "Debug" + File.separator + "FRCUserProgram").isFile())) {
      MessageDialog.openWarning(shell, "Bad Executable",
          "No executable binary was found to upload to the robot.");
      return;
    }

		if(mode.equals(ILaunchManager.RUN_MODE)) {
			// Regular deploys are done with an ant script for now, for both
			// C++ and Java.
			WPILibCPPPlugin.logInfo("Running ant file: " + activeProj.getLocation().toOSString() + File.separator + "build.xml");
			WPILibCPPPlugin.logInfo("Targets: deploy, Mode: " + mode);
			AntLauncher.runAntFile(new File (activeProj.getLocation().toOSString() + File.separator + "build.xml"), "deploy", null, mode);
		} else {
			// Debug deploys are done with the Eclipse Remote System Explorer,
			// which lets it work with Eclipse's C++ debugger.

			// Kill running program before using RSE as RSE can't
			WPILibCPPPlugin.logInfo("Running ant file: " + activeProj.getLocation().toOSString() + File.separator + "build.xml");
			WPILibCPPPlugin.logInfo("Targets: kill-program, Mode: " + mode);
			ILaunch killAnt = AntLauncher.runAntFile(new File (activeProj.getLocation().toOSString() + File.separator + "build.xml"), "kill-program", null, mode);
			int tries = 0;
			try{
				while(!killAnt.isTerminated() && tries < DEBUG_KILL_WAIT_TRIES){
					Thread.sleep(DEBUG_KILL_WAIT_MS);
					tries++;
				}
			} catch (InterruptedException e) {};
			// TODO: figure out UI issues. that's why this is undocumented
			ILaunchConfigurationWorkingCopy config;
			try {
				config = getRemoteDebugConfig(activeProj);
				DebugUITools.launch(config.doSave(), mode);
			} catch (CoreException e) {
				WPILibCPPPlugin.logError("Debug attach failed.", e);
			}

			try {
				activeProj.refreshLocal(Resource.DEPTH_INFINITE, null);
			} catch (Exception e) {}
		}
	}

	private ILaunchConfigurationWorkingCopy getRemoteDebugConfig(IProject activeProj) throws CoreException
	{
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType type = manager.getLaunchConfigurationType(ICDTLaunchConfigurationConstants.ID_LAUNCH_C_REMOTE_APP);
		int teamNumber = WPILibCore.getDefault().getTeamNumber(activeProj);
		String remote_connection = RSEUtils.getTarget(teamNumber).getName();

		ILaunchConfigurationWorkingCopy config = type.newInstance(null, activeProj.getName());
		config.setAttribute(ICDTLaunchConfigurationConstants.ATTR_COREFILE_PATH, "");
		config.setAttribute(ICDTLaunchConfigurationConstants.ATTR_DEBUGGER_ENABLE_REGISTER_BOOKKEEPING, false);
		config.setAttribute(ICDTLaunchConfigurationConstants.ATTR_DEBUGGER_ENABLE_VARIABLE_BOOKKEEPING, false);
		config.setAttribute(ICDTLaunchConfigurationConstants.ATTR_DEBUGGER_ID, "org.eclipse.rse.remotecdt.RemoteGDBDebugger");
		config.setAttribute(ICDTLaunchConfigurationConstants.ATTR_DEBUGGER_REGISTER_GROUPS, "");
		config.setAttribute(ICDTLaunchConfigurationConstants.ATTR_DEBUGGER_START_MODE, "remote");
		config.setAttribute(ICDTLaunchConfigurationConstants.ATTR_DEBUGGER_STOP_AT_MAIN, false);
		config.setAttribute(ICDTLaunchConfigurationConstants.ATTR_USE_TERMINAL, true);
		config.setAttribute(ICDTLaunchConfigurationConstants.ATTR_PROJECT_NAME, activeProj.getName());
		Collection<Executable> exes = ExecutablesManager.getExecutablesManager().getExecutablesForProject(activeProj);
		config.setAttribute(ICDTLaunchConfigurationConstants.ATTR_PROGRAM_NAME,
				exes.size() > 0 ? exes.toArray(new Executable[0])[0].getPath().makeRelativeTo(activeProj.getLocation()).toString():
			 "Debug/FRCUserProgram");

		config.setAttribute("org.eclipse.cdt.dsf.gdb.DEBUG_NAME", "arm-frc-linux-gnueabi-gdb");
		config.setAttribute(IRemoteConnectionConfigurationConstants.ATTR_REMOTE_PATH, "/home/lvuser/FRCUserProgram");
		config.setAttribute(IRemoteConnectionConfigurationConstants.ATTR_REMOTE_CONNECTION, remote_connection);
		config.setAttribute(IRemoteConnectionConfigurationConstants.ATTR_SKIP_DOWNLOAD_TO_TARGET, false);
		config.setAttribute(IRemoteConnectionConfigurationConstants.ATTR_GDBSERVER_PORT, "2345");
		config.setAttribute(IRemoteConnectionConfigurationConstants.ATTR_GDBSERVER_COMMAND, "gdbserver");

		List<String> solibs = new ArrayList<>();
		solibs.add(WPILibCPPPlugin.getDefault().getCPPDir() + "/lib");
		config.setAttribute(IMILaunchConfigurationConstants.ATTR_DEBUGGER_SOLIB_PATH, solibs);
		config.setAttribute(IMILaunchConfigurationConstants.ATTR_DEBUGGER_AUTO_SOLIB, true);
		config.setAttribute(IMILaunchConfigurationConstants.ATTR_DEBUGGER_STOP_ON_SOLIB_EVENTS, false);
		return config;
	}
}
