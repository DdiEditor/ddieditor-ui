package org.ddialliance.ddieditor.ui.preference.page;

import org.ddialliance.ddieditor.ui.Activator;
import org.ddialliance.ddieditor.util.DdiEditorConfig;
import org.ddialliance.ddiftp.util.LanguageUtil;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class DdiPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	public DdiPreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("DDI preferences");

	}

	@Override
	public void createFieldEditors() {
		// agency
		StringFieldEditor agencyEditor = new StringFieldEditor(
				DdiEditorConfig.DDI_AGENCY, "&Agency:", getFieldEditorParent());
		addField(agencyEditor);

		// default ddi lang
		ComboFieldEditor comboFieldEditor = new ComboFieldEditor(
				DdiEditorConfig.DDI_LANGUAGE,
				"&Default language used when creating new human readable items:",
				LanguageUtil.getAvailableLanguages(), getFieldEditorParent());
		comboFieldEditor.load();
		addField(comboFieldEditor);

		// instrument program language
		StringFieldEditor instProLangEditor = new StringFieldEditor(
				DdiEditorConfig.DDI_INSTRUMENT_PROGRAM_LANG,
				"&Instrument programmig language:", getFieldEditorParent());
		addField(instProLangEditor);
	}

	@Override
	public void init(IWorkbench workbench) {
		// do nothing
	}
}
