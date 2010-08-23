package org.ddialliance.ddieditor.ui.preference;

/**
 * Class used to initialize default preference values.
 * 
 */
/*
 * $Author$ 
 * $Date$ 
 * $Revision$
 */

import org.ddialliance.ddieditor.ui.Activator;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

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
		store.setDefault(PreferenceConstants.DDI_AGENCY, "ddi-dk-dda");
		store.setDefault(PreferenceConstants.DDI_LANGUAGE, "dan");

		// ddieditor-ui
		store.setDefault(PreferenceConstants.DDIEDITORUI_LANGUAGE, "dan");

		// ddieditor
		store.setDefault(PreferenceConstants.DDIEDITOR_DBXML_HOME, "");
		store.setDefault(PreferenceConstants.DDIEDITOR_DBXML_ENVIROMENT, ".");
		
		// auto change perspective
		store.setDefault(PreferenceConstants.AUTO_CHANGE_PERSPECTIVE, "true");
	}
}
