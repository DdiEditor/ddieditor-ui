package org.ddialliance.ddieditor.ui.command;

import org.ddialliance.ddieditor.ui.editor.EditorInput;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class CloseOpenEditors extends org.eclipse.core.commands.AbstractHandler {
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IEditorReference[] openEditors = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage()
				.getEditorReferences();
		String[] resourceIds = event.getParameter(this.getClass().getName()
				+ ".resourceId").split(CommandHelper.DELIMTER);
		for (int i = 0; i < openEditors.length; i++) {
			try {
				for (int j = 0; j < resourceIds.length; j++) {
					if (((EditorInput) openEditors[i].getEditorInput())
							.getResourceId().equals(resourceIds[j])) {
						PlatformUI.getWorkbench().getActiveWorkbenchWindow()
								.getActivePage().closeEditor(
										openEditors[i].getEditor(false), true);
					}	
				}
			} catch (PartInitException e) {
				throw new ExecutionException(e.getMessage(), e);
			}
		}
		return null;
	}
}
