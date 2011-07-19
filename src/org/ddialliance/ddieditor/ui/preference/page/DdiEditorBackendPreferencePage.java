package org.ddialliance.ddieditor.ui.preference.page;

import org.ddialliance.ddieditor.ui.Activator;
import org.ddialliance.ddieditor.util.DdiEditorConfig;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class DdiEditorBackendPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	public DdiEditorBackendPreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("DDIEditor backend preferences");
	}

	@Override
	public void createFieldEditors() {
		// TODO used commeton 20110718
		// DirectoryFieldEditor dbXmlInstall = new DirectoryFieldEditor(
		// PreferenceConstants.DDIEDITOR_DBXML_HOME,
		// "&Directory of Oracle &Berkley XML instalation:",
		// getFieldEditorParent());
		// dbXmlInstall.load();
		// addField(dbXmlInstall);

		DirectoryFieldEditor dbXmlEnv = new DirectoryFieldEditor(
				DdiEditorConfig.DBXML_ENVIROMENT_HOME,
				"Directory of DDIEditor XML &enviroment:",
				getFieldEditorParent());
		dbXmlEnv.load();
		addField(dbXmlEnv);
	}

	@Override
	public void init(IWorkbench workbench) {
	}

}
