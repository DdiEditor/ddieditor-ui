package org.ddialliance.ddieditor.ui.util;

import org.ddialliance.ddieditor.ui.view.Messages;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.ui.IEditorSite;

public class DialogUtil {
	public static void errorDialog(IEditorSite site, String editorId, String errorMessage, Throwable e) {
		ErrorDialog.openError(site.getShell(), Messages
				.getString("ErrorTitle"), null, new Status(
				IStatus.ERROR, editorId, 0, errorMessage, e));
	}
}
