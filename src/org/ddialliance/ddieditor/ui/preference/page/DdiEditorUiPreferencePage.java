package org.ddialliance.ddieditor.ui.preference.page;

import org.ddialliance.ddieditor.ui.Activator;
import org.ddialliance.ddieditor.ui.preference.PreferenceConstants;
import org.ddialliance.ddiftp.util.LanguageUtil;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class DdiEditorUiPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	public DdiEditorUiPreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("DDIEditor UI preferences");
	}

	@Override
	public void createFieldEditors() {
		addField(new ComboFieldEditor(PreferenceConstants.DDIEDITORUI_LANGUAGE,
				"&Default language used in user interface:", LanguageUtil
						.getAvailableLanguages(), getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {
	}
}
