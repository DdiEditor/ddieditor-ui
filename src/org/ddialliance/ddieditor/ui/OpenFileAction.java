package org.ddialliance.ddieditor.ui;

import java.text.MessageFormat;

import org.ddialliance.ddieditor.ui.dbxml.DbXml;
import org.ddialliance.ddieditor.ui.view.Messages;
import org.ddialliance.ddieditor.ui.view.QuestionItemView;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

public class OpenFileAction extends Action implements IWorkbenchAction {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, OpenFileAction.class);

	private IWorkbenchWindow window;

	public void dispose() {

	}

	public OpenFileAction(IWorkbenchWindow window) {
		this.window = window;
		setId("org.ddialliance.ddieditor.ui.OpenFileAction");
		setText(Messages.getString("OpenFileAction.menu.OpenFile"));
		setToolTipText(Messages.getString("OpenFileAction.tooltip.OpenFile"));
	}

	public void run() {
		log.debug("OpenFileAction.run()");
		FileDialog fileChooser = new FileDialog(window.getShell());
		fileChooser.setText(Messages.getString("OpenFileAction.filechooser.title"));
		fileChooser.setFilterExtensions(new String[] { "*.xml" });
		fileChooser.setFilterNames(new String[] { Messages.getString("OpenFileAction.filechooser.filternames") });
		String fileName = fileChooser.open();
		log.info("OpenFileAction.run(): " + fileName);
		if (fileName != null) {
			try {
				DbXml.open(fileName);
				// DbXml.open();
				// IViewPart view = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(QuestionItemView.ID);
				// view.notify();
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(QuestionItemView.ID);
			} catch (Exception e) {
				String errMess = MessageFormat.format(
						Messages.getString("OpenFileAction.mess.OpenFileError"), e.getMessage()); //$NON-NLS-1$
				MessageDialog.openError(window.getShell(), Messages.getString("ErrorTitle"), errMess);
			}
		}

	}
}
