package org.ddialliance.ddieditor.ui.preference;

import java.io.File;

import org.ddialliance.ddieditor.ui.Activator;
import org.ddialliance.ddieditor.util.DdiEditorConfig;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;

public class PreferenceUtil {
	static IPreferenceStore store = Activator.getDefault().getPreferenceStore();
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			PreferenceUtil.class);

	public static String getDdiAgency() {
		return store.getString(DdiEditorConfig.DDI_AGENCY);
	}

	public static String getDdiInstumentProgramLanguage() {
		return store.getString(DdiEditorConfig.DDI_INSTRUMENT_PROGRAM_LANG);
	}

	public static File getLastBrowsedPath() {
		String path = store.getString(PreferenceConstants.LAST_BROWSED_PATH);
		if (path != null) {
			return new File(path);
		}
		return null;
	}

	public static void setLastBrowsedPath(String path) {
		if (path != null && !path.equals("")) {
			File f = new File(path);
			if (f.isDirectory()) {
				path = f.getAbsolutePath();
			} else {
				path = f.getParent();
			}
			// log.debug(path);
			store.setValue(PreferenceConstants.LAST_BROWSED_PATH, path);
		}
	}
	
	public static void setPathFilter(FileDialog dialog) {
		if (store.getString(PreferenceConstants.LAST_BROWSED_PATH)!="") {
			// log.debug(getLastBrowsedPath());
			dialog.setFilterPath(getLastBrowsedPath().getAbsolutePath());
		}
	}

	public static void setPathFilter(DirectoryDialog dialog) {
		if (store.getString(PreferenceConstants.LAST_BROWSED_PATH)!="") {
			// log.debug(getLastBrowsedPath());
			dialog.setFilterPath(getLastBrowsedPath().getAbsolutePath());
		}
	}
}
