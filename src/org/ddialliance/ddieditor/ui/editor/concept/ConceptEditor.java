package org.ddialliance.ddieditor.ui.editor.concept;

import java.text.MessageFormat;

import org.ddialliance.ddieditor.ui.ConceptsPerspective;
import org.ddialliance.ddieditor.ui.dbxml.ConceptSchemes;
import org.ddialliance.ddieditor.ui.dbxml.Concepts;
import org.ddialliance.ddieditor.ui.editor.EditorInput;
import org.ddialliance.ddieditor.ui.editor.SimpleEditor;
import org.ddialliance.ddieditor.ui.editor.EditorInput.EDITOR_MODE_TYPE;
import org.ddialliance.ddieditor.ui.model.Concept;
import org.ddialliance.ddieditor.ui.model.Simple;
import org.ddialliance.ddieditor.ui.view.Messages;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

public class ConceptEditor extends SimpleEditor {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, ConceptEditor.class);
	public static final String ID = "org.ddialliance.ddieditor.ui.editor.concept.ConceptEditor";

	// Member variables:
	private Concept concept;
	private IEditorSite site;
	
	public String getPreferredPerspectiveId() {
		return ConceptsPerspective.ID;
	}

	public String getPerspectiveSwitchDialogText() {
		return MessageFormat.format(Messages.getString("perspective.switch.dialogtext"), Messages
				.getString("perspective.concepts"));
	}

	/**
	 * Create contents of the editor part
	 * 
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		log.debug("ConceptEditor.createPartControl called");
		// TODO Get descriptions
		super.createSimplePartControl(parent, Messages
				.getString("ConceptEditor.label.ConceptEditorLabel.ConceptEditor"), Messages
				.getString("ConceptEditor.label.useTheEditorLabel.Description"), Messages
				.getString("ConceptEditor.label.ConceptTabItem"), 1, (Simple) concept);
	}
	
	@Override
	public void doSave(IProgressMonitor monitor) {
		log.debug("ConceptEditor.doSave()");
		super.doSave(monitor);

		try {
			concept.validate();
		} catch (Exception e1) {
			String errMess = Messages.getString("ConceptEditor.mess.ValidationError"); //$NON-NLS-1$
			ErrorDialog.openError(site.getShell(), Messages.getString("ErrorTitle"), null, new Status(IStatus.ERROR,
					ID, 0, errMess, null));
			return;
		}
		try {
			if (editorInput.getEditorMode().equals(EDITOR_MODE_TYPE.NEW)) {
				Concepts.create(concept);
				editorInput.setEditorMode(EDITOR_MODE_TYPE.EDIT);
			} else if (editorInput.getEditorMode().equals(EDITOR_MODE_TYPE.EDIT)) {
				Concepts.update(concept);
			} else if (editorInput.getEditorMode().equals(EDITOR_MODE_TYPE.VIEW)) {
				log.debug("*** Saved ignored! ***");
			}
		} catch (Exception e) {
			String errMess = Messages.getString("ConceptEditor.mess.ErrorDuringSave"); //$NON-NLS-1$
			ErrorDialog.openError(site.getShell(), Messages.getString("ErrorTitle"), null, new Status(IStatus.ERROR,
					ID, 0, errMess, e));
			return;
		}
		editorInput.getParentView().refreshView();
		editorStatus.clearChanged();
		log.debug("ConceptEditor.doSave(1): " + editorStatus.getStatus());
	}
	
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		// Initialize the Simple Editor Part:
		super.init(site, input);
		
		// Initialize Concept Editor Part:
		this.editorInput = (EditorInput) input;
		if (log.isDebugEnabled()) {
			log.debug("ConceptEditor.init() - Name: " + editorInput.getName());
			log.debug("ConceptEditor.init() - ID: " + editorInput.getId());
			log.debug("ConceptEditor.init() - Parent ID: " + editorInput.getParentId());
			log.debug("ConceptEditor.init() - Editor Mode: " + editorInput.getEditorMode());
		}

		Concepts.init(((EditorInput) input).getProperties());

		if (editorInput.getEditorMode().equals(EDITOR_MODE_TYPE.NEW)) {
			try {
				concept = Concepts.createConcept(editorInput.getId(), editorInput.getVersion(),
						editorInput.getParentId(), editorInput.getParentVersion());
			} catch (Exception e) {
				log.error("ConceptEditor.init(): " + e.getMessage());
				String errMess = Messages.getString("ConceptEditor.mess.ErrorDuringCreateNewConcept"); //$NON-NLS-1$
				ErrorDialog.openError(site.getShell(), Messages.getString("ErrorTitle"), null, new Status(IStatus.ERROR,
						ID, 0, errMess, e));
				System.exit(0);
			}
		} else if (editorInput.getEditorMode().equals(EDITOR_MODE_TYPE.EDIT)
				|| editorInput.getEditorMode().equals(EDITOR_MODE_TYPE.VIEW)) {
			try {
				concept = Concepts.getConcept(editorInput.getId(), editorInput.getVersion(),
						editorInput.getParentId(), editorInput.getParentVersion());
			} catch (Exception e) {
				String errMess = Messages.getString("ConceptEditor.mess.GetConceptByIdError"); //$NON-NLS-1$
				ErrorDialog.openError(site.getShell(), Messages.getString("ErrorTitle"), null, new Status(IStatus.ERROR,
						ID, 0, errMess, e));
				System.exit(0);
			}
		} else {
			String errMess = MessageFormat.format(
					Messages.getString("ConceptSchemeEditor.mess.UnknownEditorMode"), editorInput.getEditorMode()); //$NON-NLS-1$
			MessageDialog.openError(site.getShell(), Messages.getString("ErrorTitle"), errMess);
			System.exit(0);
		}
		
		this.site = site;
		setSite(site);
		setInput(editorInput);
		setPartName(editorInput.getId());

	}

}
