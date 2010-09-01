package org.ddialliance.ddieditor.ui.command;

import java.util.Iterator;
import java.util.List;

import org.ddialliance.ddieditor.model.resource.DDIResourceType;
import org.ddialliance.ddieditor.ui.util.DialogUtil;
import org.ddialliance.ddieditor.ui.view.Messages;
import org.ddialliance.ddiftp.util.Translator;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IParameter;
import org.eclipse.core.commands.Parameterization;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.commands.common.NotDefinedException;
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
				.getWorkbench().getActiveWorkbenchWindow().getService(
						ICommandService.class);
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
				new Parameterization[] { new Parameterization(closeEParm, param
						.toString()) });

		IHandlerService handlerService = (IHandlerService) PlatformUI
				.getWorkbench().getActiveWorkbenchWindow().getService(
						IHandlerService.class);
		try {
			handlerService.executeCommand(parmCommand, null);
		} catch (Exception e) {
			throw new ExecutionException(e.getMessage(), e);
		}
	}

	public static void refreshViews() throws ExecutionException {
		IHandlerService handlerService = (IHandlerService) PlatformUI
				.getWorkbench().getActiveWorkbenchWindow().getService(
						IHandlerService.class);
		try {
			handlerService.executeCommand(RefreshViews.class.getName(), null);
		} catch (Exception e) {
			throw new ExecutionException(e.getMessage(), e);
		}
	}

	public static boolean confirmResourceDeletion(
			List<DDIResourceType> resources) {
		StringBuilder deletion = new StringBuilder();
		for (Iterator<DDIResourceType> iterator = resources.iterator(); iterator
				.hasNext();) {
			DDIResourceType type = iterator.next();
			deletion.append(type.getOrgName());
			if (iterator.hasNext()) {
				deletion.append(", ");
			}
		}
		return DialogUtil.yesNoDialog(Messages
				.getString("delete.resource.title"), Translator
				.trans("delete.resource.confirm", new Object[] { deletion
						.toString() }));
	}
}
