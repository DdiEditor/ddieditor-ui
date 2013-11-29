package org.ddialliance.ddieditor.ui.command;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import org.apache.commons.io.FileUtils;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.model.resource.DDIResourceType;
import org.ddialliance.ddieditor.persistenceaccess.PersistenceManager;
import org.ddialliance.ddieditor.ui.dialogs.PrintDDI3Dialog;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddieditor.ui.util.PrintUtil;
import org.ddialliance.ddieditor.util.DdiEditorConfig;
import org.ddialliance.ddiftp.util.DDIFtpException;
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
	File xmlFile = null;
	File htmlFile;
	String name = "DDI-L-";

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
			MessageDialog.openError(PlatformUI.getWorkbench().getDisplay()
					.getActiveShell(), Translator
					.trans("PrintDDI3Action.tooltip.PrintDDI3"), Translator
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
										.syncExec(new Runnable() {
											@Override
											public void run() {
												// export the resource
												try {
													List<DDIResourceType> ddiResources = PersistenceManager
															.getInstance()
															.getResources();
													List<String> resources = new ArrayList<String>();
													String workingResorce = PersistenceManager
															.getInstance()
															.getWorkingResource();

													for (DDIResourceType ddiResource : ddiResources) {
														resources
																.add(ddiResource
																		.getOrgName());
														PersistenceManager
																.getInstance()
																.setWorkingResource(
																		ddiResource
																				.getOrgName());
														for (LightXmlObjectType lightXmlObject : DdiManager
																.getInstance()
																.getStudyUnitsLight(
																		null,
																		null,
																		null,
																		null)
																.getLightXmlObjectList()
																.getLightXmlObjectList()) {
															name = DdiEditorConfig
																	.get(DdiEditorConfig.DDI_AGENCY_IDENTIFIER)
																	+ "-"
																	+ lightXmlObject
																			.getId();
														}
													}
													PersistenceManager
															.getInstance()
															.setWorkingResource(
																	workingResorce);

													xmlFile = File
															.createTempFile(
																	name,
																	".xml");
													htmlFile = File
															.createTempFile(
																	name + "-",
																	".html");
													xmlFile.deleteOnExit();
													htmlFile.deleteOnExit();

													PersistenceManager
															.getInstance()
															.exportResoures(
																	printDDI3Dialog.ddiResource
																			.getOrgName(),
																	resources,
																	xmlFile);

													// transformer
													Transformer transformer = new PrintUtil()
															.getCodebookTransformer(
																	printDDI3Dialog.numVarStatisticBoolean,
																	printDDI3Dialog.universerefOnVariablesBoolean,
																	printDDI3Dialog.addNaviagtionBarBoolean,
																	printDDI3Dialog.suppressGraphicsBoolean);

													// do transformation
													transformer
															.transform(
																	new StreamSource(
																			xmlFile.toURI()
																					.toURL()
																					.toString()),
																	new StreamResult(
																			htmlFile.toURI()
																					.toURL()
																					.toString()));

													// Copy to temp
													FileUtils
															.copyDirectory(
																	new File(
																			"resources/ddixslt"),
																	new File(
																			htmlFile.getParent()));
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
												// - start by using application
												// associated with file type
												if (!Program.launch(htmlFile
														.getAbsolutePath())) {
													// - failed: then use
													// browser
													try {
														PlatformUI
																.getWorkbench()
																.getBrowserSupport()
																.getExternalBrowser()
																.openURL(
																		new URL(
																				"file://"
																						+ htmlFile
																								.getAbsolutePath()));
													} catch (PartInitException e) {
														Editor.showError(e,
																"Browse error");
													} catch (MalformedURLException e) {
														Editor.showError(e,
																"Browse error");
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

							// save as zip file
							if (printDDI3Dialog.savePrintAsZipPath != null) {

								try {
									// zip file
									File zipFilePath = new File(
											printDDI3Dialog.savePrintAsZipPath
													+ File.separator + name
													+ ".zip");
									if (zipFilePath.exists()) {
										zipFilePath.delete();
									}
									ZipFile zipFile = new ZipFile(zipFilePath);

									ZipParameters parameters = new ZipParameters();
									parameters
											.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
									parameters
											.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

									// html
									parameters.setFileNameInZip(name + ".html");
									zipFile.addFile(htmlFile, parameters);

									// js
									zipFile.addFolder(
											new File(htmlFile.getParent()
													+ File.separator + "js"),
											parameters);

									// theme
									zipFile.addFolder(
											new File(htmlFile.getParent()
													+ File.separator + "theme"),
											parameters);
								} catch (ZipException e) {
									new DDIFtpException("Zip error", e);
								}
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