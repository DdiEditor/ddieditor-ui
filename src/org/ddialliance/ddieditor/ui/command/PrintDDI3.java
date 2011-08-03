package org.ddialliance.ddieditor.ui.command;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;

import org.apache.commons.io.FileUtils;
import org.ddialliance.ddieditor.persistenceaccess.PersistenceManager;
import org.ddialliance.ddieditor.ui.dialogs.PrintDDI3Dialog;
import org.ddialliance.ddieditor.ui.view.Messages;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.PlatformUI;

public class PrintDDI3 extends org.eclipse.core.commands.AbstractHandler {
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		System.out.println("PrintDDI3.execute()");
		// select resource to print
		final PrintDDI3Dialog printDDI3Dialog = new PrintDDI3Dialog(PlatformUI
				.getWorkbench().getDisplay().getActiveShell());
		int returnCode = printDDI3Dialog.open();
		if (returnCode == Window.CANCEL) {
			return null;
		}

		// do print
		try {
			PlatformUI.getWorkbench().getProgressService()
					.busyCursorWhile(new IRunnableWithProgress() {
						@Override
						public void run(IProgressMonitor monitor)
								throws InvocationTargetException,
								InterruptedException {
							try {
								monitor.beginTask(
										Messages.getString("PrintDDI3Action.tooltip.PrintDDI3"),
										1);

								PlatformUI.getWorkbench().getDisplay()
										.asyncExec(new Runnable() {
											@Override
											public void run() {
												// export
												File temp = null;
												try {
													temp = File
															.createTempFile(
																	"PrintDDI3",
																	".xml");
													temp.deleteOnExit();

													PersistenceManager
															.getInstance()
															.exportResoure(
																	printDDI3Dialog.ddiResource
																			.getOrgName(),
																	temp);
													// TODO specify path to style sheet in XML document instead of copying it
													FileUtils.copyFileToDirectory(new File("resources/ddi3_1.xsl"), new File(temp.getParent()));
												} catch (Exception e) {
													MessageDialog
															.openError(
																	PlatformUI
																			.getWorkbench()
																			.getDisplay()
																			.getActiveShell(),
																	Messages.getString("PrintDDI3Action.mess.PrintDDI3Error"),
																	e.getMessage());
												}
												// print
												Program.launch(temp.getAbsolutePath());
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
					.format(Messages
							.getString("PrintDDI3Action.mess.PrintDDI3Error"), e.getMessage()); //$NON-NLS-1$
			MessageDialog.openError(PlatformUI.getWorkbench().getDisplay()
					.getActiveShell(), Messages.getString("ErrorTitle"),
					errMess);
		}
		return null;
	}
}
