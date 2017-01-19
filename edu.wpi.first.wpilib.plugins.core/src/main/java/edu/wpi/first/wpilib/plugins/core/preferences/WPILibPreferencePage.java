package edu.wpi.first.wpilib.plugins.core.preferences;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import edu.wpi.first.wpilib.plugins.core.WPILibCore;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */

public class WPILibPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {
	IntegerFieldEditor teamNumberEditor;
	private BooleanFieldEditor autoManageLibraries;

	public WPILibPreferencePage() {
		super(GRID);
		setPreferenceStore(WPILibCore.getDefault().getPreferenceStore());
		setDescription("A preference page for changing all workspace level settings.");
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
		teamNumberEditor =	new IntegerFieldEditor(PreferenceConstants.TEAM_NUMBER,
						"&Team Number:", getFieldEditorParent());
		addField(teamNumberEditor);
		autoManageLibraries = new BooleanFieldEditor(PreferenceConstants.MANAGE_LIBRARIES,
				"&Auto Manage User Libraries", getFieldEditorParent());
		addField(autoManageLibraries);
	}
	
	private List<String> getInstalledVersions() {
		File[] dirs = new File(WPILibCore.getDefault().getWPILibBaseDir()+File.separator+"tools")
							.listFiles(new FileFilter() {
			@Override public boolean accept(File f) {
				return f.isDirectory();
			}
		});
		List<String> versions = new ArrayList<String>();
		for (File dir : dirs) {
			versions.add(dir.getName());
		}
		Collections.sort(versions);
		return versions;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
		WPILibCore.logInfo("Preferences initialized.");
		Properties props = WPILibCore.getDefault().getProjectProperties(null);
		getPreferenceStore().setValue(PreferenceConstants.TEAM_NUMBER,
				Integer.parseInt(props.getProperty("team-number", "0")));
	}
	
	@Override public void performApply() {
		performOk();
	}

		@Override public boolean performOk() {
			Properties props = WPILibCore.getDefault().getProjectProperties(null);
			props.setProperty("team-number", teamNumberEditor.getStringValue());
			WPILibCore.getDefault().saveGlobalProperties(props);
		return super.performOk();
	}
}
