package org.ddialliance.ddieditor.ui.command;

import java.io.File;

import org.ddialliance.ddieditor.model.resource.DDIResourceDocument;
import org.ddialliance.ddieditor.model.resource.DDIResourceType;
import org.ddialliance.ddieditor.model.resource.StorageDocument;
import org.ddialliance.ddieditor.model.resource.StorageType;
import org.ddialliance.ddieditor.persistenceaccess.PersistenceManager;
import org.ddialliance.ddieditor.persistenceaccess.dbxml.DbXmlManager;
import org.ddialliance.ddieditor.ui.dialogs.NewDDI3FileDialog;
import org.ddialliance.ddieditor.ui.perspective.InfoPerspective;
import org.ddialliance.ddieditor.ui.view.InfoView;
import org.ddialliance.ddieditor.ui.view.View;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

public class NewDDI3File extends org.eclipse.core.commands.AbstractHandler {
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// input file name
		NewDDI3FileDialog dialog = new NewDDI3FileDialog(PlatformUI
				.getWorkbench().getDisplay().getActiveShell());
		dialog.open();
		// TODO check for empty file name
		if (dialog.fileName == null && dialog.fileName.equals("")) {
			new DDIFtpException("file name null", new Throwable());
		}
		File file = new File(dialog.fileName);
		
		// create dbxml container
		String containerName = file.getName().substring(0,
				file.getName().lastIndexOf("."));
		String connection = containerName + ".dbxml";
		try {
			DbXmlManager.getInstance().openContainer(new File(connection));
			//DbXmlManager.getInstance().addResource(file);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// add storage
		StorageDocument storageDoc = StorageDocument.Factory.newInstance();
		StorageType storage = storageDoc.addNewStorage();
		storage.setId(containerName);
		storage.setConnection(connection);
		storage.setManager(DbXmlManager.class.getName());		
		try {
			PersistenceManager.getInstance().createStorage(storageDoc);
		} catch (DDIFtpException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// index storage and add resources
		DDIResourceDocument ddiResourceDocument = DDIResourceDocument.Factory
				.newInstance();
		DDIResourceType ddiResource = ddiResourceDocument
				.addNewDDIResource();
		ddiResource.setOrgName(file.getName());
		
		try {
			PersistenceManager.getInstance().createResource(
					ddiResourceDocument, containerName);
		} catch (DDIFtpException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// update info view
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
				// TODO Auto-generated catch block
				e.printStackTrace();
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
