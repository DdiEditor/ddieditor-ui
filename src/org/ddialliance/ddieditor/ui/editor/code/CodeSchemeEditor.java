package org.ddialliance.ddieditor.ui.editor.code;

/**
 * Code Scheme Editor.
 * 
 */
/*
 * $Author$ 
 * $Date$ 
 * $Revision$
 */

import java.text.MessageFormat;

import org.ddialliance.ddieditor.ui.dbxml.CodeSchemes;
import org.ddialliance.ddieditor.ui.editor.EditorInput;
import org.ddialliance.ddieditor.ui.editor.LabelDescriptionEditor;
import org.ddialliance.ddieditor.ui.editor.EditorInput.EditorModeType;
import org.ddialliance.ddieditor.ui.model.LabelDescription;
import org.ddialliance.ddieditor.ui.model.code.CodeScheme;
import org.ddialliance.ddieditor.ui.perspective.CodesPerspective;
import org.ddialliance.ddieditor.ui.view.Messages;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

public class CodeSchemeEditor extends LabelDescriptionEditor{
	private static Log log = LogFactory.getLog(LogType.SYSTEM, CodeSchemeEditor.class);
	public static final String ID = "org.ddialliance.ddieditor.ui.editor.code.CodeSchemeEditor";

	// Member variables:
	private CodeScheme codeScheme;
	private IEditorSite site;
	
	public CodeSchemeEditor() {
		super(Messages
				.getString("CodeSchemeEditor.label.CodeSchemeEditorLabel.CodeSchemeEditor"), Messages
				.getString("CodeSchemeEditor.label.useTheEditorLabel.Description"), Messages
				.getString("CodeSchemeEditor.label.CodeSchemeTabItem"));
	}

	public String getPreferredPerspectiveId() {
		return CodesPerspective.ID;
	}

	public String getPerspectiveSwitchDialogText() {
		return MessageFormat.format(Messages.getString("perspective.switch.dialogtext"), Messages
				.getString("perspective.codes"));
	}

	/**
	 * Create contents of the editor part
	 * 
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout());
		log.debug("CodeSchemeEditor.createPartControl called");
		super.createPartControl(parent);
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
			if (editorInput.getEditorMode().equals(EditorModeType.NEW)) {
				CodeSchemes.create(codeScheme);
				editorInput.setEditorMode(EditorModeType.EDIT);
			} else if (editorInput.getEditorMode().equals(EditorModeType.EDIT)) {
				CodeSchemes.update(codeScheme);
			} else if (editorInput.getEditorMode().equals(EditorModeType.VIEW)) {
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
		
		// Initialize Code Scheme Editor Part:
		this.editorInput = (EditorInput) input;
		if (log.isDebugEnabled()) {
			log.debug("CodeSchemeEditor.init() - Name: " + editorInput.getName());
			log.debug("CodeSchemeEditor.init() - ID: " + editorInput.getId());
			log.debug("CodeSchemeEditor.init() - Parent ID: " + editorInput.getParentId());
			log.debug("CodeSchemeEditor.init() - Editor Mode: " + editorInput.getEditorMode());
		}

		CodeSchemes.init(((EditorInput) input).getProperties());

		if (editorInput.getEditorMode().equals(EditorModeType.NEW)) {
			try {
				codeScheme = CodeSchemes.createCodeScheme(editorInput.getId(), editorInput.getVersion(),
						editorInput.getParentId(), editorInput.getParentVersion());
			} catch (Exception e) {
				log.error("CodeSchemeEditor.init(): " + e.getMessage());
				String errMess = Messages.getString("CodeSchemeEditor.mess.ErrorDuringCreateNewCode"); //$NON-NLS-1$
				ErrorDialog.openError(site.getShell(), Messages.getString("ErrorTitle"), null, new Status(IStatus.ERROR,
						ID, 0, errMess, e));
				System.exit(0);
			}
		} else if (editorInput.getEditorMode().equals(EditorModeType.EDIT)
				|| editorInput.getEditorMode().equals(EditorModeType.VIEW)) {
			try {
				codeScheme = CodeSchemes.getCodeScheme(editorInput.getId(), editorInput.getVersion(),
						editorInput.getParentId(), editorInput.getParentVersion());
			} catch (Exception e) {
				String errMess = Messages.getString("CodeSchemeEditor.mess.GetCodeByIdError"); //$NON-NLS-1$
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
		
		// Initialize the Simple Editor Part with Code Scheme:
		super.init(site, input, (LabelDescription) codeScheme);
		
		this.site = site;
		setSite(site);
		setInput(editorInput);
		setPartName(editorInput.getId());

	}

}
