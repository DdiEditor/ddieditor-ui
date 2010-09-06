package org.ddialliance.ddieditor.ui.preference;

import org.ddialliance.ddieditor.ui.Activator;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Class used to initialize default preference values
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#
	 * initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();

		// ddi
		store.setDefault(PreferenceConstants.DDI_AGENCY, "dk.dda");
		store.setDefault(PreferenceConstants.DDI_LANGUAGE, "da");

		// ddieditor-ui
		store.setDefault(PreferenceConstants.DDIEDITORUI_LANGUAGE, "da");

		// ddieditor
		store.setDefault(PreferenceConstants.DDIEDITOR_DBXML_HOME, "");
		store.setDefault(PreferenceConstants.DDIEDITOR_DBXML_ENVIROMENT, ".");
		
		// auto change perspective
		store.setDefault(PreferenceConstants.AUTO_CHANGE_PERSPECTIVE, "true");
	}
}
