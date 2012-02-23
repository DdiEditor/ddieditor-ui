package org.ddialliance.ddieditor.ui.command;

import java.io.File;

import org.ddialliance.ddieditor.logic.validation.DdiSchemaValidator;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.marker.MarkerListDocument;
import org.ddialliance.ddieditor.persistenceaccess.PersistenceManager;
import org.ddialliance.ddieditor.ui.dialogs.DisplayNoteDialog;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PlatformUI;

public class ValidateDDI3 extends org.eclipse.core.commands.AbstractHandler {
	private Log log = LogFactory.getLog(LogType.SYSTEM, ValidateDDI3.class);

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		String resourceId = null;
		File temp = null;

		MarkerListDocument markerListDoc = null;
		DdiSchemaValidator ddiSchemaValidator = null;

		try {
			// working resource
			resourceId = PersistenceManager.getInstance().getWorkingResource();

			// ddi file to validate
			temp = File.createTempFile("ddischemavalidate-", ".xml");
			temp.deleteOnExit();
			PersistenceManager.getInstance().exportResoure(resourceId, temp);

			// marker list
			markerListDoc = MarkerListDocument.Factory.newInstance();
			markerListDoc.addNewMarkerList();

			// schema validate
			ddiSchemaValidator = new DdiSchemaValidator();
			ddiSchemaValidator.validate(temp, resourceId, markerListDoc);

			// debug result
			String txt = "Schema Validation";
			DisplayNoteDialog dialog = new DisplayNoteDialog(PlatformUI
					.getWorkbench().getDisplay().getActiveShell(), txt, txt,
					txt, ddiSchemaValidator.markerListDoc.xmlText(DdiManager
							.getInstance().getXmlOptions()));
			dialog.open();
		} catch (Exception e) {
			Editor.showError(e, "");
			throw new ExecutionException("Error", e);
		}
		return null;
	}
}
