package org.ddialliance.ddieditor.ui.command;

import java.util.ArrayList;
import java.util.List;

import org.ddialliance.ddieditor.model.resource.DDIResourceType;
import org.ddialliance.ddieditor.model.resource.StorageType;
import org.ddialliance.ddieditor.persistenceaccess.PersistenceManager;
import org.ddialliance.ddieditor.ui.dialogs.DeleteDDI3Dialog;
import org.ddialliance.ddieditor.ui.view.ViewManager;
import org.ddialliance.ddieditor.ui.view.dynamicview.DynamicView;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.Translator;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;

/**
 * RCP entry point to remove a loaded ddi 3 resource
 */
public class DeleteSingleDDI3 extends org.eclipse.core.commands.AbstractHandler {
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		DeleteDDI3Dialog dialog = new DeleteDDI3Dialog(PlatformUI
				.getWorkbench().getDisplay().getActiveShell());
		dialog.open();
		DDIResourceType ddiResource = dialog.getResult();
		if (dialog.getReturnCode() == Window.CANCEL) {
			return null;
		}

		List<DDIResourceType> resources = new ArrayList<DDIResourceType>();
		resources.add(ddiResource);

		CommandHelper.deleteResources(resources);

		return null;
	}
}
