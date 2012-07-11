package org.ddialliance.ddieditor.ui.command;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.ddialliance.ddieditor.model.resource.DDIResourceType;
import org.ddialliance.ddieditor.model.resource.StorageType;
import org.ddialliance.ddieditor.persistenceaccess.PersistenceManager;
import org.ddialliance.ddieditor.ui.dialogs.PrintDDI3Dialog;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddiftp.util.Translator;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class PrintDDI3 extends org.eclipse.core.commands.AbstractHandler {
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		// select resource to print
		final PrintDDI3Dialog printDDI3Dialog = new PrintDDI3Dialog(PlatformUI
				.getWorkbench().getDisplay().getActiveShell());
		int returnCode = printDDI3Dialog.open();
		if (returnCode == Window.CANCEL) {
			return null;
		}
		if (printDDI3Dialog.ddiResource == null) {
			System.out.println("Error");
			MessageDialog
			.openError(
					PlatformUI
							.getWorkbench()
							.getDisplay()
							.getActiveShell(),
							Translator
							.trans("PrintDDI3Action.tooltip.PrintDDI3"),
							Translator
							.trans("PrintDDI3Action.mess.ResourceNotSpecified"));
			return null;
		}

		// print the selected resource and pass it to the default browser with
		// the DDI 3 style sheet
		try {
			PlatformUI.getWorkbench().getProgressService()
					.busyCursorWhile(new IRunnableWithProgress() {
						@Override
						public void run(IProgressMonitor monitor)
								throws InvocationTargetException,
								InterruptedException {
							try {
								monitor.beginTask(
										Translator
												.trans("PrintDDI3Action.tooltip.PrintDDI3"),
										1);

								PlatformUI.getWorkbench().getDisplay()
										.asyncExec(new Runnable() {
											@Override
											public void run() {
												// export the resource
												File temp = null;
												try {
													temp = File
															.createTempFile(
																	"PrintDDI3",
																	".xml");
													temp.deleteOnExit();

													List<DDIResourceType> ddiResources = PersistenceManager.getInstance().getResources();
													List<String> resources = new ArrayList<String>();
													for (DDIResourceType ddiResource : ddiResources) {
														resources.add(ddiResource.getOrgName());
													}
													PersistenceManager
															.getInstance()
															.exportResoures(
																	printDDI3Dialog.ddiResource
																			.getOrgName(), resources,
																	temp);
													// TODO specify path to
													// style sheet in XML
													// document instead of
													// copying it
													FileUtils
															.copyDirectory(
																	new File(
																			"resources/ddixslt"),
																	new File(
																			temp.getParent()));
												} catch (Exception e) {
													MessageDialog
															.openError(
																	PlatformUI
																			.getWorkbench()
																			.getDisplay()
																			.getActiveShell(),
																	Translator
																			.trans("PrintDDI3Action.mess.PrintDDI3Error"),
																	e.getMessage());
												}
												
												// active the external browser
												// with the DDI document
												// - start by using application associated with file type
												if (!Program.launch(temp
														.getAbsolutePath())) {
													// - failed: then use browser
													try {
														PlatformUI
																.getWorkbench()
																.getBrowserSupport()
																.getExternalBrowser()
																.openURL(
																		new URL(
																				"file://"
																						+ temp.getAbsolutePath()));
													} catch (PartInitException e) {
														Editor.showError(e,
																"Print");
													} catch (MalformedURLException e) {
														Editor.showError(e,
																"Print");
													}
												}
											}
										});
								monitor.worked(1);
							} catch (Exception e) {
								throw new InvocationTargetException(e);
							} finally {
								monitor.done();
							}
						}
					});
		} catch (Exception e) {
			String errMess = MessageFormat
					.format(Translator
							.trans("PrintDDI3Action.mess.PrintDDI3Error"), e.getMessage()); //$NON-NLS-1$
			MessageDialog.openError(PlatformUI.getWorkbench().getDisplay()
					.getActiveShell(), Translator.trans("ErrorTitle"), errMess);
		}
		return null;
	}
}
