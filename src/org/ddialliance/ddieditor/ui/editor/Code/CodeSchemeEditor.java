package org.ddialliance.ddieditor.ui.editor.Code;

import java.text.MessageFormat;

import org.ddialliance.ddieditor.ui.ConceptsPerspective;
import org.ddialliance.ddieditor.ui.dbxml.CodeSchemes;
import org.ddialliance.ddieditor.ui.dbxml.ConceptSchemes;
import org.ddialliance.ddieditor.ui.editor.EditorInput;
import org.ddialliance.ddieditor.ui.editor.SimpleEditor;
import org.ddialliance.ddieditor.ui.editor.EditorInput.EDITOR_MODE_TYPE;
import org.ddialliance.ddieditor.ui.editor.concept.ConceptSchemeEditor;
import org.ddialliance.ddieditor.ui.model.CodeScheme;
import org.ddialliance.ddieditor.ui.model.ConceptScheme;
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

public class CodeSchemeEditor extends SimpleEditor{
	private static Log log = LogFactory.getLog(LogType.SYSTEM, CodeSchemeEditor.class);
	public static final String ID = "org.ddialliance.ddieditor.ui.editor.concept.CodeSchemeEditor";

	// Member variables:
	private CodeScheme codeScheme;
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
		log.debug("CodeSchemeEditor.createPartControl called");
		// TODO Get descriptions
		super.createSimplePartControl(parent, Messages
				.getString("CodeSchemeEditor.label.CodeSchemeEditorLabel.CodeSchemeEditor"), Messages
				.getString("CodeSchemeEditor.label.useTheEditorLabel.Description"), Messages
				.getString("CodeSchemeEditor.label.CodeSchemeTabItem"), 1, (Simple) codeScheme);
	}
	
	@Override
	public void doSave(IProgressMonitor monitor) {
		log.debug("CodeSchemeEditor.doSave()");
		super.doSave(monitor);

		try {
			codeScheme.validate();
		} catch (Exception e1) {
			String errMess = Messages.getString("CodeSchemeEditor.mess.ValidationError"); //$NON-NLS-1$
			ErrorDialog.openError(site.getShell(), Messages.getString("ErrorTitle"), null, new Status(IStatus.ERROR,
					ID, 0, errMess, null));
			return;
		}
		try {
			if (editorInput.getEditorMode().equals(EDITOR_MODE_TYPE.NEW)) {
				CodeSchemes.create(codeScheme);
				editorInput.setEditorMode(EDITOR_MODE_TYPE.EDIT);
			} else if (editorInput.getEditorMode().equals(EDITOR_MODE_TYPE.EDIT)) {
				CodeSchemes.update(codeScheme);
			} else if (editorInput.getEditorMode().equals(EDITOR_MODE_TYPE.VIEW)) {
				log.debug("*** Saved ignored! ***");
			}
		} catch (Exception e) {
			String errMess = Messages.getString("CodeSchemeEditor.mess.ErrorDuringSave"); //$NON-NLS-1$
			ErrorDialog.openError(site.getShell(), Messages.getString("ErrorTitle"), null, new Status(IStatus.ERROR,
					ID, 0, errMess, e));
			return;
		}
		editorInput.getParentView().refreshView();
		editorStatus.clearChanged();
		log.debug("CodeSchemeEditor.doSave(1): " + editorStatus.getStatus());
	}
	
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		// Initialize the Simple Editor Part:
		super.init(site, input);
		
		// Initialize Concept Scheme Editor Part:
		this.editorInput = (EditorInput) input;
		if (log.isDebugEnabled()) {
			log.debug("CodeSchemeEditor.init() - Name: " + editorInput.getName());
			log.debug("CodeSchemeEditor.init() - ID: " + editorInput.getId());
			log.debug("CodeSchemeEditor.init() - Parent ID: " + editorInput.getParentId());
			log.debug("CodeSchemeEditor.init() - Editor Mode: " + editorInput.getEditorMode());
		}

		CodeSchemes.init(((EditorInput) input).getProperties());

		if (editorInput.getEditorMode().equals(EDITOR_MODE_TYPE.NEW)) {
			try {
				codeScheme = CodeSchemes.createCodeScheme(editorInput.getId(), editorInput.getVersion(),
						editorInput.getParentId(), editorInput.getParentVersion());
			} catch (Exception e) {
				log.error("CodeSchemeEditor.init(): " + e.getMessage());
				String errMess = Messages.getString("CodeSchemeEditor.mess.ErrorDuringCreateNewConcept"); //$NON-NLS-1$
				ErrorDialog.openError(site.getShell(), Messages.getString("ErrorTitle"), null, new Status(IStatus.ERROR,
						ID, 0, errMess, e));
				System.exit(0);
			}
		} else if (editorInput.getEditorMode().equals(EDITOR_MODE_TYPE.EDIT)
				|| editorInput.getEditorMode().equals(EDITOR_MODE_TYPE.VIEW)) {
			try {
				codeScheme = CodeSchemes.getCodeScheme(editorInput.getId(), editorInput.getVersion(),
						editorInput.getParentId(), editorInput.getParentVersion());
			} catch (Exception e) {
				String errMess = Messages.getString("CodeSchemeEditor.mess.GetConceptByIdError"); //$NON-NLS-1$
				ErrorDialog.openError(site.getShell(), Messages.getString("ErrorTitle"), null, new Status(IStatus.ERROR,
						ID, 0, errMess, e));
				System.exit(0);
			}
		} else {
			String errMess = MessageFormat.format(
					Messages.getString("CodeSchemeEditor.mess.UnknownEditorMode"), editorInput.getEditorMode()); //$NON-NLS-1$
			MessageDialog.openError(site.getShell(), Messages.getString("ErrorTitle"), errMess);
			System.exit(0);
		}
		
		this.site = site;
		setSite(site);
		setInput(editorInput);
		setPartName(editorInput.getId());

	}

}
