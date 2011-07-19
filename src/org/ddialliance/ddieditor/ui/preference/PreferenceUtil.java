package org.ddialliance.ddieditor.ui.preference;

import org.ddialliance.ddieditor.ui.Activator;
import org.ddialliance.ddieditor.util.DdiEditorConfig;
import org.eclipse.jface.preference.IPreferenceStore;

public class PreferenceUtil {
	static IPreferenceStore store = Activator.getDefault().getPreferenceStore();

	public static String getDdiAgency() {
		return store.getString(DdiEditorConfig.DDI_AGENCY);
	}

	public static String getDdiInstumentProgramLanguage() {
		return store.getString(DdiEditorConfig.DDI_INSTRUMENT_PROGRAM_LANG);
	}
}
