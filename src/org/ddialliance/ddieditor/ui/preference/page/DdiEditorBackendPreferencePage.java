package org.ddialliance.ddieditor.ui.preference.page;

import org.ddialliance.ddieditor.ui.Activator;
import org.ddialliance.ddieditor.util.DdiEditorConfig;
import org.ddialliance.ddiftp.util.Translator;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class DdiEditorBackendPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	public DdiEditorBackendPreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription(Translator.trans("pref.dbxmlbackend.headline"));
	}

	@Override
	public void createFieldEditors() {
		DirectoryFieldEditor dbXmlEnvFieldEditor = new DirectoryFieldEditor(
				DdiEditorConfig.DBXML_ENVIROMENT_HOME,
				Translator.trans("pref.dbxmlbackend.path"),
				getFieldEditorParent());
		dbXmlEnvFieldEditor.load();
		addField(dbXmlEnvFieldEditor);
	}

	@Override
	public void init(IWorkbench workbench) {
	}
}
