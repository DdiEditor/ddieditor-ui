package org.ddialliance.ddieditor.ui.command;

import java.io.File;

import org.ddialliance.ddieditor.persistenceaccess.PersistenceManager;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.Translator;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.ui.PlatformUI;

public class ExportResourceList extends
		org.eclipse.core.commands.AbstractHandler {
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		DirectoryDialog dirChooser = new DirectoryDialog(PlatformUI
				.getWorkbench().getDisplay().getActiveShell());
		dirChooser.setText(Translator
				.trans("ExportDDI3Action.filechooser.title"));
		String path = dirChooser.open();
		// TODO check for null or empty on path
		try {
			PersistenceManager.getInstance().exportResourceList(
					new File(path + File.separator + "resource-list.xml"));
		} catch (DDIFtpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
