package org.ddialliance.ddieditor.ui.command;

import org.apache.xmlbeans.XmlOptions;
import org.ddialliance.ddieditor.persistenceaccess.PersistenceManager;
import org.ddialliance.ddieditor.ui.dialogs.DisplayNoteDialog;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PlatformUI;

public class DisplayDebugInfo extends org.eclipse.core.commands.AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		StringBuilder result = new StringBuilder();
		try {
			result.append("Working Resource: \n"
					+ PersistenceManager.getInstance().getWorkingResource());
			result.append("\n\nWorking Storage: \n"
					+ PersistenceManager.getInstance().getWorkingStorage());
			result.append("\n\nResourceList: \n");
			XmlOptions xmlOptions = new XmlOptions();
			xmlOptions.setSaveAggressiveNamespaces();
			xmlOptions.setSaveOuter();
			xmlOptions.setSavePrettyPrint();
			result.append(PersistenceManager.getInstance().getResourceList()
					.xmlText(xmlOptions));
		} catch (Exception e) {
			throw new ExecutionException("Error", e);
		}
		String txt = "Debug Info";
		DisplayNoteDialog dialog = new DisplayNoteDialog(PlatformUI
				.getWorkbench().getDisplay().getActiveShell(), txt, txt, txt,
				result.toString());
		dialog.open();
		return null;
	}
}
