package org.ddialliance.ddieditor.ui.command;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;

import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.persistenceaccess.PersistenceManager;
import org.ddialliance.ddieditor.persistenceaccess.filesystem.FilesystemManager;
import org.ddialliance.ddieditor.ui.editor.category.CategoryEditor;
import org.ddialliance.ddieditor.ui.editor.instrument.InstrumentEditor;
import org.ddialliance.ddieditor.ui.editor.instrument.QuestionConstructEditor;
import org.ddialliance.ddieditor.ui.editor.question.QuestionItemEditor;
import org.ddialliance.ddieditor.ui.editor.universe.UniverseEditor;
import org.ddialliance.ddieditor.ui.view.Messages;
import org.ddialliance.ddieditor.ui.view.ViewManager;
import org.ddialliance.ddiftp.util.Translator;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PlatformUI;

/**
 * RCP entry point to open DDI XML
 */
public class OpenDDI3File extends org.eclipse.core.commands.AbstractHandler {
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		FileDialog fileChooser = new FileDialog(PlatformUI.getWorkbench()
				.getDisplay().getActiveShell());
		fileChooser.setText(Messages
				.getString("OpenFileAction.filechooser.title"));
		fileChooser.setFilterExtensions(new String[] { "*.xml" });
		fileChooser.setFilterNames(new String[] { Messages
				.getString("OpenFileAction.filechooser.filternames") });
		final String fileName = fileChooser.open();

		if (fileName != null) {
			try {
				PlatformUI.getWorkbench().getProgressService().busyCursorWhile(
						new IRunnableWithProgress() {
							@Override
							public void run(IProgressMonitor monitor)
									throws InvocationTargetException,
									InterruptedException {
								try {
									// import ddi file into dbxml
									monitor.beginTask(Translator.trans(
											"OpenFileAction.importingfile",
											fileName), 3);

									PersistenceManager.getInstance();
									DdiManager ddiManager = DdiManager
											.getInstance();
									monitor.worked(1);

									// add resources
									File file = new File(fileName);
									FilesystemManager.getInstance()
											.addResource(file);
									monitor.worked(1);

									// set working resource
									PersistenceManager.getInstance()
											.setWorkingResource(file.getName());
									monitor.worked(1);
								} catch (Exception e) {
									throw new InvocationTargetException(e);
								} finally {
									monitor.done();
								}
							}
						});
			} catch (Exception e) {
				Throwable throwable = null;
				if (e instanceof InvocationTargetException
						&& e.getCause() != null) {
					throwable = e.getCause();
				} else {
					throwable = e;
				}
				String errMess = MessageFormat
						.format(
								Messages
										.getString("OpenFileAction.mess.OpenFileError"), throwable.getMessage()); //$NON-NLS-1$
				MessageDialog.openError(PlatformUI.getWorkbench().getDisplay()
						.getActiveShell(), Messages.getString("ErrorTitle"),
						errMess);
			}
		}
		
		// refresh views
		ViewManager.getInstance().addAllViewsToRefresh();
		ViewManager.getInstance().refesh();
		return null;
	}
}
