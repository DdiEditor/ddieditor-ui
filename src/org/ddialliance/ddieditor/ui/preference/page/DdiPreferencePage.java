package org.ddialliance.ddieditor.ui.preference.page;

import org.ddialliance.ddieditor.ui.Activator;
import org.ddialliance.ddieditor.ui.preference.PreferenceConstants;
import org.ddialliance.ddieditor.ui.util.LanguageUtil;
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
		addField(new StringFieldEditor(PreferenceConstants.DDI_AGENCY,
				"&Agency:", getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceConstants.DDI_LANGUAGE,
				"&Default language used when creating new human readable items:", LanguageUtil
						.getAvailableLanguages(), getFieldEditorParent()));
	}
	
	@Override
	public void init(IWorkbench workbench) {
	}
}
