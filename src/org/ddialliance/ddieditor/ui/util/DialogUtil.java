package org.ddialliance.ddieditor.ui.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.ddialliance.ddieditor.persistenceaccess.maintainablelabel.MaintainableLightLabelQueryResult;
import org.ddialliance.ddieditor.ui.Activator;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.Translator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
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
	 * @param title
	 *            dialog title [if null arg then translated 'ErrorTitle' is
	 *            supplied]
	 * @param errorMessage
	 *            error message to display
	 * @param e
	 *            exception or throwable
	 */
	public static void errorDialog(IEditorSite site, String pluginId,
			String title, String errorMessage, Throwable e) {
		errorDialog(site.getShell(), pluginId, title, errorMessage, e);
	}

	/**
	 * Construct error dialog
	 * 
	 * @param shell
	 *            parent shell
	 * @param pluginId
	 *            ID of plug in e.g. editor, view, perspective ...
	 * @param title
	 *            dialog title [if null arg then translated 'ErrorTitle' is
	 *            supplied]
	 * @param errorMessage
	 *            error message to display
	 * @param e
	 *            exception or throwable
	 */
	public static void errorDialog(Shell shell, String pluginId, String title,
			String errorMessage, Throwable e) {
		checkException(e);
		if (title == null) {
			title = Translator.trans("ErrorTitle");
		}

		// exception or real throwable
		Throwable t = null;
		if (e instanceof DDIFtpException
				&& ((DDIFtpException) e).getRealThrowable() != null) {
			t = ((DDIFtpException) e).getRealThrowable();
		}
		if (t == null) {
			t = e;
		}

		// stack trace
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		t.printStackTrace(printWriter);
		final String trace = stringWriter.toString();
		List<Status> childStatuses = new ArrayList<Status>();
		for (String line : trace.split(System.getProperty("line.separator"))) {
			childStatuses.add(new Status(IStatus.ERROR, pluginId, line));
		}

		// multi status
		MultiStatus multiStatus = new MultiStatus(Activator.PLUGIN_ID,
				IStatus.ERROR, childStatuses.toArray(new Status[] {}),
				t.getLocalizedMessage(), t);

		// show error
		ErrorDialog.openError(null, pluginId, errorMessage, multiStatus);
	}

	private static void checkException(Throwable e) {
		if (!(e instanceof DDIFtpException)) {
			// construct a ddi ftp exception for logging purposes
			new DDIFtpException(new Exception(e));
		}
	}

	public static boolean yesNoDialog(String title, String message) {
		return MessageDialog.openConfirm(PlatformUI.getWorkbench().getDisplay()
				.getActiveShell(), title, message);
	}

	public static int customConfirmDialog(String title, String message,
			String[] labels) {
		MessageDialog m = new MessageDialog(PlatformUI.getWorkbench()
				.getDisplay().getActiveShell(), title, null, message,
				MessageDialog.CONFIRM, labels, 0);
		int result = m.open();
		System.out.println(labels[result] + ": " + result);
		return result;
	}

	public static void infoDialog(Shell shell, String pluginId, String title,
			String infoMessage) {
		if (title == null) {
			title = Translator.trans("InfoTitle");
		}

		MessageDialog.openInformation(shell, title, infoMessage);
	}

	/**
	 * Confirm delete dialog for maintainable light label objects
	 * 
	 * @param shell
	 *            to open in
	 * @param title
	 *            of dialog
	 * @param mLightLabelQueryResult
	 *            to delete
	 * @return confirmation
	 */
	public static boolean deleteDialogMll(Shell shell, String title,
			MaintainableLightLabelQueryResult mLightLabelQueryResult) {
		try {
			return MessageDialog
					.openConfirm(
							shell,
							title,
							MessageFormat.format(
									Translator
											.trans("View.mess.ConfirmDeletionMaintainableLightLabelQueryResult"),
									mLightLabelQueryResult.getTargetLabel(),
									mLightLabelQueryResult
											.getSubElementLabels()));
		} catch (DDIFtpException e) {
			Editor.showError(e, DialogUtil.class.getName());
		}
		return false;
	}
}
