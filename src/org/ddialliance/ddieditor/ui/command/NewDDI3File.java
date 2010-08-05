package org.ddialliance.ddieditor.ui.command;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.apache.xmlbeans.XmlOptions;
import org.ddialliance.ddi3.xml.xmlbeans.instance.DDIInstanceDocument;
import org.ddialliance.ddieditor.logic.identification.IdentificationManager;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.persistenceaccess.filesystem.FilesystemManager;
import org.ddialliance.ddieditor.ui.dialogs.NewDDI3FileDialog;
import org.ddialliance.ddieditor.ui.perspective.InfoPerspective;
import org.ddialliance.ddieditor.ui.view.InfoView;
import org.ddialliance.ddieditor.ui.view.View;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.ddialliance.ddiftp.util.xml.XmlBeansUtil;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

/**
 * RCP entry point to create a ddi 3 resource
 */
public class NewDDI3File extends org.eclipse.core.commands.AbstractHandler {
	private Log log = LogFactory.getLog(LogType.EXCEPTION, NewDDI3File.class);

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// input file name
		NewDDI3FileDialog dialog = new NewDDI3FileDialog(PlatformUI
				.getWorkbench().getDisplay().getActiveShell());
		dialog.open();
		if (dialog.fileName == null && dialog.fileName.equals("")) {
			new DDIFtpException("file name null", new Throwable());
		}

		File file = new File(dialog.fileName);
		try {
			// new instance
			DDIInstanceDocument ddiInstanceDoc = DDIInstanceDocument.Factory
					.newInstance();
			ddiInstanceDoc.addNewDDIInstance();
			IdentificationManager.getInstance().addIdentification(
					ddiInstanceDoc.getDDIInstance(), null, null);
			IdentificationManager.getInstance().addVersionInformation(
					ddiInstanceDoc.getDDIInstance(), null, null);
			XmlBeansUtil.addXsiAttributes(ddiInstanceDoc);

			// prepare output
			XmlOptions xmlOptions = new XmlOptions();
			xmlOptions.setSaveOuter();
			xmlOptions.setSavePrettyPrint();
			String node = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
					+ DdiManager.getInstance().getDdi3NamespaceHelper()
							.substitutePrefixesFromElementsKeepXsiDefs(
									ddiInstanceDoc.getDDIInstance().xmlText(
											xmlOptions));

			// create file
			FileWriter fstream = new FileWriter(file);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(node);
			out.close();

			// import file
			FilesystemManager.getInstance().addResource(file);
		} catch (Exception e) {
			log.error(e);
			throw new ExecutionException(e.getMessage(), e);
		} finally {
			// cleanup ;- )
			file.delete();
		}

		// update info view
		// TODO refactor boiler plate code to refresh a
		// view into a rcp command
		final IWorkbenchWindow[] workbenchWindows = PlatformUI.getWorkbench()
				.getWorkbenchWindows();

		IWorkbenchPage workbenchPage = workbenchWindows[0].getActivePage();
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				try {
					PlatformUI.getWorkbench().showPerspective(
							InfoPerspective.ID, workbenchWindows[0]);
				} catch (WorkbenchException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		IViewPart iViewPart = workbenchWindows[0].getActivePage().findView(
				InfoView.ID);
		if (iViewPart == null) {
			try {
				iViewPart = workbenchPage.showView(InfoView.ID);
			} catch (PartInitException e) {
				throw new ExecutionException(e.getMessage(), e);
			}
		}

		// refresh in async to avoid swt thread
		// violation
		final View view = (View) iViewPart;
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				view.refreshView();
			}
		});
		return null;
	}
}
