package org.ddialliance.ddieditor.ui.preference;

import org.ddialliance.ddieditor.ui.view.Messages;

/**
 * Constant definitions for plug-in preferences.
 */
public class PreferenceConstants {

	// ddieditor-ui
	public static final String DDIEDITORUI_LANGUAGE = "ddieditorui.language";
	public static final String AUTO_CHANGE_PERSPECTIVE = "auto.change.perspective";
	public static final String CONFIRM_DDIEDITOR_EXIT = "confirm.ddieditor.exit";
	public static final String[][] FONT_SIZE_LIST = {
			{ Messages.getString("pref.fontsize.small"), "1" },
			{ Messages.getString("pref.fontsize.medium"), "2" },
			{ Messages.getString("pref.fontsize.large"), "3" } };
	public static final String FONT_SIZE_TABLE_SIZE = "ddieditorui.fontsize.table";
}
