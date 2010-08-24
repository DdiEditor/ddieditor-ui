package org.ddialliance.ddieditor.ui.preference.page;

import org.ddialliance.ddieditor.ui.Activator;
import org.ddialliance.ddieditor.ui.preference.PreferenceConstants;
import org.ddialliance.ddieditor.ui.view.Messages;
import org.ddialliance.ddiftp.util.LanguageUtil;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
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

		addField(new BooleanFieldEditor(
				PreferenceConstants.AUTO_CHANGE_PERSPECTIVE, "&"
						+ Messages.getString("pref.autotoggle.field"),
				getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {
	}
}
