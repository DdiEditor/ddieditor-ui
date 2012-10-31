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
				DdiEditorConfig.DDI_AGENCY, "&DDI-L Agency:",
				getFieldEditorParent());
		addField(agencyEditor);

		StringFieldEditor agencyNameEditor = new StringFieldEditor(
				DdiEditorConfig.DDI_AGENCY_NAME, "Agency &Name:",
				getFieldEditorParent());
		addField(agencyNameEditor);

		StringFieldEditor agencyDescriptionEditor = new StringFieldEditor(
				DdiEditorConfig.DDI_AGENCY_DESCRIPTION, "Agency &Description:",
				StringFieldEditor.UNLIMITED,
				StringFieldEditor.VALIDATE_ON_KEY_STROKE,
				getFieldEditorParent());
		addField(agencyDescriptionEditor);

		StringFieldEditor agencyIdentifierEditor = new StringFieldEditor(
				DdiEditorConfig.DDI_AGENCY_IDENTIFIER, "Agency &Identifier:",
				getFieldEditorParent());
		addField(agencyIdentifierEditor);
		
		StringFieldEditor agencyHpEditor = new StringFieldEditor(
				DdiEditorConfig.DDI_AGENCY_HP, "Agency &Home Page:",
				getFieldEditorParent());
		addField(agencyHpEditor);
		
		

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
