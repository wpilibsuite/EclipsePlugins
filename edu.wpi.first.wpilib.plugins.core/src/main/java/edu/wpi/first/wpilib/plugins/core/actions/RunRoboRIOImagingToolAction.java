package edu.wpi.first.wpilib.plugins.core.actions;

import edu.wpi.first.wpilib.plugins.core.WPILibCore;
import java.io.File;
import java.io.FileNotFoundException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * Our sample action implements workbench action delegate.
 * The action proxy will be created by the workbench and
 * shown in the UI. When the user tries to use the action,
 * this delegate will be created and execution will be 
 * delegated to it.
 * @see IWorkbenchWindowActionDelegate
 */
public class RunRoboRIOImagingToolAction implements IWorkbenchWindowActionDelegate {
	/**
	 * The constructor.
	 */
	public RunRoboRIOImagingToolAction() {
	}

	/**
	 * The action has been activated. The argument of the
	 * method represents the 'real' action sitting
	 * in the workbench UI.
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {
		File toolLocation = new File(System.getenv("ProgramFiles(X86)") + File.separator
				+ "National Instruments" + File.separator
				+ "LabVIEW 2017" + File.separator
				+ "project" + File.separator
				+ "roboRIO Tool");
		File tool = new File(toolLocation, "roboRIO_ImagingTool.exe");

		if (tool.exists()) {
			String[] cmd = {tool.getAbsolutePath()};
			try {
				DebugPlugin.exec(cmd, toolLocation);
			} catch (CoreException e) {
				WPILibCore.logError("Error running roboRIO Imaging Tool.", e);
			}
		} else {
			WPILibCore.logError("Error running roboRIO Imaging Tool.", new FileNotFoundException("Could not locate file: " + tool.getPath()));
		}
	}

	/**
	 * Selection in the workbench has been changed. We 
	 * can change the state of the 'real' action here
	 * if we want, but this can only happen after 
	 * the delegate has been created.
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

	/**
	 * We can use this method to dispose of any system
	 * resources we previously allocated.
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
	}

	/**
	 * We will cache window object in order to
	 * be able to provide parent shell for the message dialog.
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window) {
	}
}
