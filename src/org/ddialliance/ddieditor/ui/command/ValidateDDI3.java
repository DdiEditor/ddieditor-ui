package org.ddialliance.ddieditor.ui.command;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.ddialliance.ddieditor.logic.validation.DdiSchemaValidator;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.marker.MarkerListDocument;
import org.ddialliance.ddieditor.persistenceaccess.PersistenceManager;
import org.ddialliance.ddieditor.ui.dialogs.DisplayNoteDialog;
import org.ddialliance.ddieditor.ui.dialogs.ValidateDialog;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddiftp.util.Translator;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;

public class ValidateDDI3 extends org.eclipse.core.commands.AbstractHandler {
	private Log log = LogFactory.getLog(LogType.SYSTEM, ValidateDDI3.class);

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final ValidateDialog dialog = new ValidateDialog(PlatformUI
				.getWorkbench().getDisplay().getActiveShell());
		int returnCode = dialog.open();
		if (returnCode == Window.CANCEL) {
			return null;
		}

		try {
			PlatformUI.getWorkbench().getProgressService()
					.busyCursorWhile(new IRunnableWithProgress() {
						@Override
						public void run(IProgressMonitor monitor)
								throws InvocationTargetException,
								InterruptedException {
							// monitor
							monitor.beginTask(Translator.trans("validation"), 1);

							// schema validate
							PlatformUI
									.getWorkbench()
									.getDisplay()
									.asyncExec(
											new ValidateJob(dialog.ddiResource
													.getOrgName()));
							monitor.worked(1);
						}
					});
		} catch (Exception e) {
			throw new ExecutionException(e.getMessage(), e);
		}
		return null;
	}

	class ValidateJob implements Runnable {
		String resourceId = null;
		File temp = null;

		MarkerListDocument markerListDoc = null;
		DdiSchemaValidator ddiSchemaValidator = null;

		public ValidateJob(String resourceId) {
			this.resourceId = resourceId;
		}

		@Override
		public void run() {
			try {
				// working resource
				resourceId = PersistenceManager.getInstance()
						.getWorkingResource();

				// ddi file to validate
				temp = File.createTempFile("ddischemavalidate-", ".xml");
				temp.deleteOnExit();
				PersistenceManager.getInstance()
						.exportResoure(resourceId, temp);

				// marker list
				markerListDoc = MarkerListDocument.Factory.newInstance();
				markerListDoc.addNewMarkerList();

				// schema validate
				ddiSchemaValidator = new DdiSchemaValidator();
				ddiSchemaValidator.validate(temp, resourceId, markerListDoc);

				// debug result
				String txt = Translator.trans("validation.schema");
				DisplayNoteDialog dialog = new DisplayNoteDialog(PlatformUI
						.getWorkbench().getDisplay().getActiveShell(), txt,
						txt, txt,
						ddiSchemaValidator.markerListDoc.xmlText(DdiManager
								.getInstance().getXmlOptions()));
				dialog.open();
			} catch (Exception e) {
				Editor.showError(e, "");
			}
		}
	}
}
