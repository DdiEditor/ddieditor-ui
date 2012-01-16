package org.ddialliance.ddieditor.ui.command;

import java.text.MessageFormat;
import java.util.List;

import org.ddialliance.ddieditor.model.resource.DDIResourceType;
import org.ddialliance.ddieditor.model.resource.StorageType;
import org.ddialliance.ddieditor.persistenceaccess.PersistenceManager;
import org.ddialliance.ddieditor.ui.view.ViewManager;
import org.ddialliance.ddieditor.ui.view.dynamicview.DynamicView;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.Translator;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

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
					.getActiveShell(), Translator.trans("ErrorTitle"),
					e.getMessage());
		}

		// yes - no dialog
		if (!CommandHelper.confirmResourceDeletion(resources)) {
			return null;
		}

		// close open editors belonging to resources
		CommandHelper.closeEditors(resources);

		try {
			for (DDIResourceType ddiResource : resources) {
				StorageType storage = PersistenceManager.getInstance()
						.getStorageByResourceOrgName(ddiResource.getOrgName());
				PersistenceManager.getInstance().deleteResource(
						ddiResource.getOrgName());
				PersistenceManager.getInstance().deleteStorage(storage.getId());
				storage = null;
			}
		} catch (Exception e) {
			String errMess = MessageFormat
					.format(Translator
							.trans("OpenFileAction.mess.OpenFileError"), e.getMessage()); //$NON-NLS-1$
			MessageDialog.openError(PlatformUI.getWorkbench().getDisplay()
					.getActiveShell(), Translator.trans("ErrorTitle"), errMess);
		}

		// refresh view
		ViewManager.getInstance().addAllViewsToRefresh();
		ViewManager.getInstance().refesh();

		// cleanup dynamic view
		DynamicView.closeUpdateDynamicView();

		return null;
	}
}
