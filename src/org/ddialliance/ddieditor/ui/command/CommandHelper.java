package org.ddialliance.ddieditor.ui.command;

import java.util.List;

import org.ddialliance.ddieditor.model.resource.DDIResourceType;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IParameter;
import org.eclipse.core.commands.Parameterization;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;

public class CommandHelper {
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
		Parameterization[] params = new Parameterization[resources.size()];
		int count = 0;
		for (DDIResourceType ddiResource : resources) {
			params[count] = new Parameterization(closeEParm, ddiResource
					.getOrgName());
			count++;
		}
		ParameterizedCommand parmCommand = new ParameterizedCommand(closeE,
				params);

		IHandlerService handlerService = (IHandlerService) PlatformUI
				.getWorkbench().getActiveWorkbenchWindow().getService(
						IHandlerService.class);
		try {
			handlerService.executeCommand(parmCommand, null);
		} catch (Exception e) {
			throw new ExecutionException(e.getMessage(), e);
		}
	}
}
