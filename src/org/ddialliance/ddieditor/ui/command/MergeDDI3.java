package org.ddialliance.ddieditor.ui.command;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

public class MergeDDI3 extends org.eclipse.core.commands.AbstractHandler {
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		MessageDialog.openError(PlatformUI.getWorkbench().getDisplay()
				.getActiveShell(), "Not implemeted",
				"Not implemeted");
		return null;
	}

}
