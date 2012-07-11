package org.ddialliance.ddieditor.ui.command;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.ddialliance.ddieditor.logic.validation.DdiSchemaValidator;
import org.ddialliance.ddieditor.model.marker.MarkerListDocument;
import org.ddialliance.ddieditor.persistenceaccess.PersistenceManager;
import org.ddialliance.ddieditor.ui.dialogs.ValidateDialog;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddieditor.ui.view.validate.ValidateView;
import org.ddialliance.ddiftp.util.Translator;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchWindow;
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

	public class ValidateJob implements Runnable {
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

				// validate view
				IWorkbenchWindow windows[] = PlatformUI.getWorkbench()
						.getWorkbenchWindows();
				boolean found = false;
				for (int i = 0; i < windows.length; i++) {
					for (IViewReference iViewReference : windows[i]
							.getActivePage().getViewReferences()) {
						IViewPart viewPart = windows[i].getActivePage()
								.findView(iViewReference.getId());
						if (viewPart != null
								&& viewPart.getClass() == ValidateView.class) {
							try {
								((ValidateView) viewPart)
										.refresh(markerListDoc);

								// select right combo item
								// String[] test = ((ValidateView)
								// viewPart).combo.getItems();
								
								// combo.getItems() returns the 'possible empty
								// list of strings' thanks SWT!
								
								// for (int j = 0; j < test.length; j++) {
								// if (test[i].equals(resourceId)) {
								// ((ValidateView) viewPart).combo.select(i);
								// }
								// }
								
								windows[i].getActivePage().activate(viewPart);
							} catch (Exception e) {
								Editor.showError(e, null);
							}
							found = true;
							break;
						}
					}
					if (found) {
						break;
					}
				}
				if (!found) {
					// create view
					IViewPart viewPart = PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow().getActivePage()
							.showView(ValidateView.ID);
					((ValidateView) viewPart).refresh(markerListDoc);
				}
			} catch (Exception e) {
				Editor.showError(e, "");
			}
		}
	}
}
