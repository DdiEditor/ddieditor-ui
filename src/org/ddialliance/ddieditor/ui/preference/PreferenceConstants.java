package org.ddialliance.ddieditor.ui.preference;

import org.ddialliance.ddieditor.ui.view.Messages;

/**
 * Constant definitions for plug-in preferences.
 */
public class PreferenceConstants {
	// ddi
	public static final String DDI_AGENCY = "ddi.agency";
	public static final String DDI_LANGUAGE = "ddi.lang";

	// ddieditor-ui
	public static final String DDIEDITORUI_LANGUAGE = "ddieditorui.language";
	public static final String AUTO_CHANGE_PERSPECTIVE = "auto.change.perspective";
	public static final String[][] FONT_SIZE_LIST = {
			{ Messages.getString("pref.fontsize.small"), "1" },
			{ Messages.getString("pref.fontsize.medium"), "2" },
			{ Messages.getString("pref.fontsize.large"), "3" } };
	public static final String FONT_SIZE_TABLE_SIZE = "ddieditorui.fontsize.table";

	// ddieditor
	public static final String DDIEDITOR_DBXML_HOME = "ddieditor.dbxml.home";
	public static final String DDIEDITOR_DBXML_ENVIROMENT = "ddieditor.dbxml.enviroment";
}
