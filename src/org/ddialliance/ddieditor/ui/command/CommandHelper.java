package org.ddialliance.ddieditor.ui.command;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.List;

import org.ddialliance.ddi3.xml.xmlbeans.instance.DDIInstanceDocument;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.resource.DDIResourceType;
import org.ddialliance.ddieditor.model.resource.StorageType;
import org.ddialliance.ddieditor.persistenceaccess.PersistenceManager;
import org.ddialliance.ddieditor.persistenceaccess.filesystem.FilesystemManager;
import org.ddialliance.ddieditor.ui.dialogs.NewDDI3FileDialog;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddieditor.ui.util.DialogUtil;
import org.ddialliance.ddieditor.ui.view.ViewManager;
import org.ddialliance.ddieditor.ui.view.dynamicview.DynamicView;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.Translator;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IParameter;
import org.eclipse.core.commands.Parameterization;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;

/**
 * Helper convenience utility for commands
 */
public class CommandHelper {
	public static String DELIMTER = "__";

	public static void closeEditors(List<DDIResourceType> resources)
			throws ExecutionException {
		ICommandService commandService = (ICommandService) PlatformUI
				.getWorkbench().getActiveWorkbenchWindow()
				.getService(ICommandService.class);
		Command closeE = commandService.getCommand(CloseOpenEditors.class
				.getName());

		IParameter closeEParm = null;

		try {
			closeEParm = closeE.getParameter(CloseOpenEditors.class.getName()
					+ ".resourceId");
		} catch (NotDefinedException e) {
			throw new ExecutionException(e.getMessage(), e);
		}

		StringBuilder param = new StringBuilder();
		for (Iterator<DDIResourceType> iterator = resources.iterator(); iterator
				.hasNext();) {
			DDIResourceType ddiResource = iterator.next();
			param.append(ddiResource.getOrgName());
			if (iterator.hasNext()) {
				param.append(DELIMTER);
			}
		}
		ParameterizedCommand parmCommand = new ParameterizedCommand(closeE,
				new Parameterization[] { new Parameterization(closeEParm,
						param.toString()) });

		IHandlerService handlerService = (IHandlerService) PlatformUI
				.getWorkbench().getActiveWorkbenchWindow()
				.getService(IHandlerService.class);
		try {
			handlerService.executeCommand(parmCommand, null);
		} catch (Exception e) {
			throw new ExecutionException(e.getMessage(), e);
		}
	}

	public static void refreshViews() throws ExecutionException {
		IHandlerService handlerService = (IHandlerService) PlatformUI
				.getWorkbench().getService(IHandlerService.class);
		try {
			handlerService.executeCommand(RefreshViews.class.getName(), null);
		} catch (Exception e) {
			throw new ExecutionException(e.getMessage(), e);
		}
	}

	public static void deleteResources(List<DDIResourceType> resources) {
		StringBuilder deletion = new StringBuilder();
		for (Iterator<DDIResourceType> iterator = resources.iterator(); iterator
				.hasNext();) {
			DDIResourceType type = iterator.next();
			deletion.append(type.getOrgName());
			if (iterator.hasNext()) {
				deletion.append(", ");
			}
		}
		boolean yesNo = DialogUtil.yesNoDialog(Translator
				.trans("delete.resource.title"),
				Translator.trans("delete.resource.confirm",
						new Object[] { deletion.toString() }));
		// no
		if (!yesNo) {
			return;
		}

		// close editors
		try {
			closeEditors(resources);
		} catch (ExecutionException e) {
			new Editor().showError(e);
		}

		// terminate resource
		try {
			for (DDIResourceType ddiResource : resources) {
				StorageType storage = PersistenceManager.getInstance()
						.getStorageByResourceOrgName(ddiResource.getOrgName());
				PersistenceManager.getInstance().deleteResource(
						ddiResource.getOrgName());
				PersistenceManager.getInstance().deleteStorage(storage.getId());
				storage = null;
			}
		} catch (DDIFtpException e) {
			new Editor().showError(e);
		}

		// refresh view
		ViewManager.getInstance().addAllViewsToRefresh();
		ViewManager.getInstance().refesh();

		// cleanup dynamic view
		DynamicView.closeUpdateDynamicView();
	}

	public static void createResource(DDIInstanceDocument ddiInstance) {
		// input file name
		NewDDI3FileDialog dialog = new NewDDI3FileDialog(PlatformUI
				.getWorkbench().getDisplay().getActiveShell());
		int returnCode = dialog.open();
		if (returnCode == Window.CANCEL) {
			return;
		}
		if (dialog.fileName != null && dialog.fileName.equals("")) {
			Editor.showError((Exception) new Throwable(),
					Translator.trans("file name null"));
		}

		File file = new File(dialog.fileName);
		try {
			// prepare output
			String node = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
					+ DdiManager
							.getInstance()
							.getDdi3NamespaceHelper()
							.substitutePrefixesFromElementsKeepXsiDefs(
									ddiInstance.getDDIInstance().xmlText(
											DdiManager.getInstance()
													.getXmlOptions()));

			// create file
			FileWriter fstream = new FileWriter(file);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(node);
			out.close();

			// import file
			FilesystemManager.getInstance().addResource(file);
		} catch (Exception e) {
			new Editor().showError(new DDIFtpException(e));
		} finally {
			// cleanup ;- )
			file.delete();
		}

		// update info view
		ViewManager.getInstance().addAllViewsToRefresh();
		ViewManager.getInstance().refesh();
	}

	public static void openDdi3() throws ExecutionException {
		IHandlerService handlerService = (IHandlerService) PlatformUI
				.getWorkbench().getService(IHandlerService.class);
		try {
			handlerService.executeCommand(OpenDDI3File.class.getName(), null);
		} catch (Exception e) {
			throw new ExecutionException(e.getMessage(), e);
		}
	}
}
