package org.ddialliance.ddieditor.ui.preference.page;

import org.ddialliance.ddieditor.ui.Activator;
import org.ddialliance.ddieditor.ui.preference.PreferenceConstants;
import org.ddialliance.ddiftp.util.LanguageUtil;
import org.ddialliance.ddiftp.util.Translator;
import org.eclipse.jface.preference.BooleanFieldEditor;
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
		// ui language
		ComboFieldEditor comboFieldEditor = new ComboFieldEditor(
				PreferenceConstants.DDIEDITORUI_LANGUAGE,
				"&Default language used in user interface:",
				LanguageUtil.getAvailableLanguages(), getFieldEditorParent());
		comboFieldEditor.load();
		addField(comboFieldEditor);

		// table font size
		ComboFieldEditor comboFontEditor = new ComboFieldEditor(
				PreferenceConstants.FONT_SIZE_TABLE_SIZE,
				Translator.trans("pref.tablefontsize"),
				PreferenceConstants.FONT_SIZE_LIST, getFieldEditorParent());
		comboFieldEditor.load();
		addField(comboFontEditor);

		// auto change perspective
		BooleanFieldEditor autoChangePerspectiveFieldEditor = new BooleanFieldEditor(
				PreferenceConstants.AUTO_CHANGE_PERSPECTIVE, "&"
						+ Translator.trans("pref.autotoggle.field"),
				getFieldEditorParent());
		autoChangePerspectiveFieldEditor.load();
		addField(autoChangePerspectiveFieldEditor);

		// confirm exit
		BooleanFieldEditor confirmExitFieldEditor = new BooleanFieldEditor(
				PreferenceConstants.CONFIRM_DDIEDITOR_EXIT, "&"
						+ Translator.trans("pref.confirm.ddieditor.exit"),
				getFieldEditorParent());
		confirmExitFieldEditor.load();
		addField(confirmExitFieldEditor);
	}

	@Override
	public void init(IWorkbench workbench) {
	}
}
