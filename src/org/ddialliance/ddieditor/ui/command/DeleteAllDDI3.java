package org.ddialliance.ddieditor.ui.command;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.List;

import org.ddialliance.ddieditor.model.resource.DDIResourceType;
import org.ddialliance.ddieditor.model.resource.StorageType;
import org.ddialliance.ddieditor.persistenceaccess.PersistenceManager;
import org.ddialliance.ddieditor.ui.perspective.InfoPerspective;
import org.ddialliance.ddieditor.ui.view.InfoView;
import org.ddialliance.ddieditor.ui.view.Messages;
import org.ddialliance.ddieditor.ui.view.View;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

/**
 * RCP entry point to remove all loaded ddi 3 resource
 */
public class DeleteAllDDI3 extends org.eclipse.core.commands.AbstractHandler {
	List<DDIResourceType> resources;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			resources = PersistenceManager.getInstance().getResources();
		} catch (DDIFtpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			PlatformUI.getWorkbench().getProgressService().busyCursorWhile(
					new IRunnableWithProgress() {
						@Override
						public void run(IProgressMonitor monitor)
								throws InvocationTargetException,
								InterruptedException {
							try {
								int tasks = resources.size() + 2;
								monitor.beginTask("Deleting DDI3 resources",
										tasks);
								for (DDIResourceType ddiResource : resources) {
									StorageType storage = PersistenceManager
											.getInstance()
											.getStorageByResourceOrgName(
													ddiResource.getOrgName());
									PersistenceManager.getInstance()
											.deleteResource(
													ddiResource.getOrgName());
									PersistenceManager.getInstance()
											.deleteStorage(storage.getId());
									storage = null;
									monitor.worked(1);
								}

								// refresh view
								// TODO refactor boiler plate code to refresh a
								// view into a rcp command
								final IWorkbenchWindow[] workbenchWindows = PlatformUI
										.getWorkbench().getWorkbenchWindows();

								IWorkbenchPage workbenchPage = null;
								PlatformUI.getWorkbench().getDisplay()
										.asyncExec(new Runnable() {
											@Override
											public void run() {
												try {
													PlatformUI
															.getWorkbench()
															.showPerspective(
																	InfoPerspective.ID,
																	workbenchWindows[0]);
												} catch (WorkbenchException e) {
													// TODO Auto-generated catch
													// block
													e.printStackTrace();
												}
											}
										});
								IViewPart iViewPart = workbenchWindows[0]
										.getActivePage().findView(InfoView.ID);
								if (iViewPart == null) {
									iViewPart = workbenchPage
											.showView(InfoView.ID);
								}
								monitor.worked(1);

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
		return null;
	}
}
