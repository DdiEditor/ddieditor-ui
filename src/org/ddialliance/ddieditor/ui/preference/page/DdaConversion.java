package org.ddialliance.ddieditor.ui.preference.page;

import org.ddialliance.ddieditor.ui.Activator;
import org.ddialliance.ddieditor.util.DdiEditorConfig;
import org.ddialliance.ddiftp.util.Translator;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class DdaConversion extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	public DdaConversion() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription(Translator.trans("pref.ddaconversion.studyinfodir.headline"));
	}

	@Override
	public void createFieldEditors() {
		DirectoryFieldEditor ddaDbLess = new DirectoryFieldEditor(
				DdiEditorConfig.DDA_CONVERSION_STUDYINFO_DIR,
				Translator.trans("pref.ddaconversion.studyinfodir"),
				getFieldEditorParent());
		ddaDbLess.load();
		addField(ddaDbLess);
	}

	@Override
	public void init(IWorkbench workbench) {
	}
}
