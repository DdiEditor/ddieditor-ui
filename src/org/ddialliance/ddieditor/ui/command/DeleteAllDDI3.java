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
			MessageDialog.openError(PlatformUI.getWorkbench().getDisplay()
					.getActiveShell(), Messages.getString("ErrorTitle"), e
					.getMessage());
		}

		// yes - no dialog
		if (!CommandHelper.confirmResourceDeletion(resources)) {
			return null;
		}

		// close open editors belonging to resources
		CommandHelper.closeEditors(resources);

		try {
			PlatformUI.getWorkbench().getProgressService().busyCursorWhile(
					new IRunnableWithProgress() {
						@Override
						public void run(IProgressMonitor monitor)
								throws InvocationTargetException,
								InterruptedException {
							try {
								int tasks = resources.size() + 1;
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
								CommandHelper.refreshViews();
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
