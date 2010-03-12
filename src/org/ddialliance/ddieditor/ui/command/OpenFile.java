package org.ddialliance.ddieditor.ui.command;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;

import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.persistenceaccess.PersistenceManager;
import org.ddialliance.ddieditor.persistenceaccess.filesystem.FilesystemManager;
import org.ddialliance.ddieditor.ui.view.InfoView;
import org.ddialliance.ddieditor.ui.view.Messages;
import org.ddialliance.ddieditor.ui.view.View;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * RCP entry point to open ddi xml
 */
public class OpenFile extends org.eclipse.core.commands.AbstractHandler {
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		FileDialog fileChooser = new FileDialog(PlatformUI.getWorkbench()
				.getDisplay().getActiveShell());
		fileChooser.setText(Messages
				.getString("OpenFileAction.filechooser.title"));
		fileChooser.setFilterExtensions(new String[] { "*.xml" });
		fileChooser.setFilterNames(new String[] { Messages
				.getString("OpenFileAction.filechooser.filternames") });
		final String fileName = fileChooser.open();

		if (fileName != null) {
			try {
				PlatformUI.getWorkbench().getProgressService().busyCursorWhile(
						new IRunnableWithProgress() {
							@Override
							public void run(IProgressMonitor monitor)
									throws InvocationTargetException,
									InterruptedException {
								try {
									// import ddi file into dbxml
									monitor.beginTask("Importing file: "
											+ fileName, 4);

									PersistenceManager.getInstance();
									DdiManager.getInstance();
									monitor.worked(1);

									// add resources
									File file = new File(fileName);
									FilesystemManager.getInstance()
											.addResource(file);
									monitor.worked(1);

									// set working resource
									PersistenceManager.getInstance()
											.setWorkingResource(file.getName());
									monitor.worked(1);

									// refresh view
									monitor
											.setTaskName("Refreshing info view ...");

									IWorkbenchWindow[] workbenchWindows = PlatformUI
											.getWorkbench()
											.getWorkbenchWindows();
									IViewPart iViewPart = workbenchWindows[0]
											.getActivePage().findView(
													InfoView.ID);
									if (iViewPart == null) {
										iViewPart = workbenchWindows[0]
												.getActivePage().showView(
														InfoView.ID);
									}

									// refresh in async to avoid swt thread
									// violation
									final View view = (View) iViewPart;
									PlatformUI.getWorkbench().getDisplay()
											.asyncExec(new Runnable() {
												@Override
												public void run() {
													view.refreshView();
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
		}
		return null;
	}
}
