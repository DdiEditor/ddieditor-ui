package org.ddialliance.ddieditor.ui.util;

import org.ddialliance.ddieditor.ui.view.Messages;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.Translator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PlatformUI;

public class DialogUtil {
	/**
	 * Construct error dialog
	 * 
	 * @param site
	 *            interface between an editor part and the workbench
	 * @param pluginId
	 *            ID of plug in e.g. editor, view, perspective ...
	 * @param errorMessage
	 *            error message to display
	 * @param e
	 *            exception or throwable
	 */
	public static void errorDialog(IEditorSite site, String pluginId, String title,
			String errorMessage, Throwable e) {
		errorDialog(site.getShell(), pluginId, title, errorMessage, e);
	}

	/**
	 * Construct error dialog
	 * 
	 * @param shell
	 *            parent shell
	 * 
	 * @param pluginId
	 *            ID of plug in e.g. editor, view, perspective ...
	 * @param errorMessage
	 *            error message to display
	 * @param e
	 *            exception or throwable
	 */
	public static void errorDialog(Shell shell, String pluginId, String title, 
			String errorMessage, Throwable e) {
		checkException(e);
		if (title==null) {
			title = Messages.getString("ErrorTitle");
		}
		
		ErrorDialog.openError(shell, title, null,
				new Status(IStatus.ERROR, pluginId, 0, errorMessage, e));
	}

	private static void checkException(Throwable e) {
		if (!(e instanceof DDIFtpException)) {
			// construct a ddi ftp exception for logging purposes
			new DDIFtpException(new Exception(e));
		}
	}
	
	public static boolean yesNoDialog(String title, String message) {
		return MessageDialog.openConfirm(PlatformUI.getWorkbench()
				.getDisplay().getActiveShell(), title, message);
	}
}
