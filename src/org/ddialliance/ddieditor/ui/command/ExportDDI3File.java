package org.ddialliance.ddieditor.ui.command;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;

import org.ddialliance.ddieditor.persistenceaccess.PersistenceManager;
import org.ddialliance.ddieditor.ui.dialogs.ExportDDI3Dialog;
import org.ddialliance.ddieditor.ui.view.Messages;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;

/**
 * RCP entry point to export ddi xml
 */
public class ExportDDI3File extends org.eclipse.core.commands.AbstractHandler {
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// export input
		final ExportDDI3Dialog exportDDI3Dialog = new ExportDDI3Dialog(
				PlatformUI.getWorkbench().getDisplay().getActiveShell());
		int returnCode = exportDDI3Dialog.open();
		if (returnCode == Window.CANCEL) {
			return null;
		}


		// do export
		try {
			PlatformUI.getWorkbench().getProgressService().busyCursorWhile(
					new IRunnableWithProgress() {
						@Override
						public void run(IProgressMonitor monitor)
								throws InvocationTargetException,
								InterruptedException {
							try {
								// export
								monitor
										.beginTask(
												Messages
														.getString("ExportDDI3Action.tooltip.ExportDDI3"),
												1);

								PlatformUI.getWorkbench().getDisplay()
										.asyncExec(new Runnable() {
											@Override
											public void run() {
												try {
													PersistenceManager
															.getInstance()
															.exportResoure(
																	exportDDI3Dialog.ddiResource
																			.getOrgName(),
																	new File(
																			exportDDI3Dialog.path
																					+ File.separator
																					+ exportDDI3Dialog.fileName));
												} catch (DDIFtpException e) {
													MessageDialog
															.openError(
																	PlatformUI
																			.getWorkbench()
																			.getDisplay()
																			.getActiveShell(),
																	Messages
																			.getString("ErrorTitle"),
																	e
																			.getMessage());
												}
											}
										});
								monitor.worked(1);
							} catch (Exception e) {
								throw new InvocationTargetException(e);
							} finally {
								monitor.done();
							}
						}
					});
		} catch (Exception e) {
			String errMess = MessageFormat
					.format(
							Messages
									.getString("OpenFileAction.mess.OpenFileError"), e.getMessage()); //$NON-NLS-1$
			MessageDialog.openError(PlatformUI.getWorkbench().getDisplay()
					.getActiveShell(), Messages.getString("ErrorTitle"),
					errMess);
		}
		return null;
	}
}
