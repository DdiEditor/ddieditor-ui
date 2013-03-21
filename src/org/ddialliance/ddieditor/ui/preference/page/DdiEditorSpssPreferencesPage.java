package org.ddialliance.ddieditor.ui.preference.page;

import org.ddialliance.ddieditor.ui.Activator;
import org.ddialliance.ddieditor.util.DdiEditorConfig;
import org.ddialliance.ddiftp.util.Translator;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class DdiEditorSpssPreferencesPage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	String[][] SPSS_DELIMITER_LIST = { { "COMMA", "," }, { "DOT", "." } };
	
	public DdiEditorSpssPreferencesPage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription(Translator.trans("spsspreferencespage.title"));
	}

	@Override
	public void createFieldEditors() {
		// read in charset
		StringFieldEditor agencyEditor = new StringFieldEditor(
				DdiEditorConfig.SPSS_IMPORT_CHARSET,
				Translator.trans("spsspreferencespage.charset"),
				getFieldEditorParent());
		addField(agencyEditor);

		ComboFieldEditor spssDelimiterCombo = new ComboFieldEditor(
				DdiEditorConfig.SPSS_IMPORT_DECIMAL_SEPERATOR,
				Translator.trans("spsspreferencespage.delimiter"),
				SPSS_DELIMITER_LIST, getFieldEditorParent());
		spssDelimiterCombo.load();
		addField(spssDelimiterCombo);
	}

	@Override
	public void init(IWorkbench workbench) {
		// do nothing
	}
}
