package org.ddialliance.ddieditor.ui.editor.question;

/**
 * Question Scheme Editor.
 * 
 */
/*
 * $Author$ 
 * $Date$ 
 * $Revision$
 */

import java.text.MessageFormat;

import org.ddialliance.ddieditor.ui.dbxml.QuestionSchemes;
import org.ddialliance.ddieditor.ui.editor.EditorInput;
import org.ddialliance.ddieditor.ui.editor.LabelDescriptionEditor;
import org.ddialliance.ddieditor.ui.editor.EditorInput.EditorModeType;
import org.ddialliance.ddieditor.ui.model.LabelDescription;
import org.ddialliance.ddieditor.ui.model.question.QuestionScheme;
import org.ddialliance.ddieditor.ui.perspective.QuestionsPerspective;
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

public class QuestionSchemeEditor extends LabelDescriptionEditor {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, QuestionSchemeEditor.class);
	public static final String ID = "org.ddialliance.ddieditor.ui.editor.question.QuestionSchemeEditor";

	// Member variables:
	private QuestionScheme questionScheme;
	private IEditorSite site;
	
	public QuestionSchemeEditor() {
		super(Messages
				.getString("QuestionSchemeEditor.label.QuestionSchemeEditorLabel.QuestionSchemeEditor"), Messages
				.getString("QuestionSchemeEditor.label.useTheEditorLabel.Description"), Messages
				.getString("QuestionSchemeEditor.label.QuestionSchemeTabItem"));
	}

	public String getPreferredPerspectiveId() {
		return QuestionsPerspective.ID;
	}

	public String getPerspectiveSwitchDialogText() {
		return MessageFormat.format(Messages.getString("perspective.switch.dialogtext"), Messages
				.getString("perspective.questions"));
	}

	/**
	 * Create contents of the editor part
	 * 
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout());
		log.debug("QuestionSchemeEditor.createPartControl called");
		super.createPartControl(parent);
	}
	
	@Override
	public void doSave(IProgressMonitor monitor) {
		log.debug("QuestionSchemeEditor.doSave()");
		super.doSave(monitor);

		try {
			questionScheme.validate();
		} catch (Exception e1) {
			String errMess = Messages.getString("QuestionSchemeEditor.mess.ValidationError"); //$NON-NLS-1$
			ErrorDialog.openError(site.getShell(), Messages.getString("ErrorTitle"), null, new Status(IStatus.ERROR,
					ID, 0, errMess, null));
			return;
		}
		try {
			if (editorInput.getEditorMode().equals(EditorModeType.NEW)) {
				QuestionSchemes.create(questionScheme);
				editorInput.setEditorMode(EditorModeType.EDIT);
			} else if (editorInput.getEditorMode().equals(EditorModeType.EDIT)) {
				QuestionSchemes.update(questionScheme);
			} else if (editorInput.getEditorMode().equals(EditorModeType.VIEW)) {
				log.debug("*** Saved ignored! ***");
			}
		} catch (Exception e) {
			String errMess = Messages.getString("QuestionSchemeEditor.mess.ErrorDuringSave"); //$NON-NLS-1$
			ErrorDialog.openError(site.getShell(), Messages.getString("ErrorTitle"), null, new Status(IStatus.ERROR,
					ID, 0, errMess, e));
			return;
		}
		editorInput.getParentView().refreshView();
		editorStatus.clearChanged();
		log.debug("QuestionSchemeEditor.doSave(1): " + editorStatus.getStatus());
	}
	
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		
		// Initialize Question Scheme Editor Part:
		this.editorInput = (EditorInput) input;
		if (log.isDebugEnabled()) {
			log.debug("QuestionSchemeEditor.init() - Name: " + editorInput.getName());
			log.debug("QuestionSchemeEditor.init() - ID: " + editorInput.getId());
			log.debug("QuestionSchemeEditor.init() - Parent ID: " + editorInput.getParentId());
			log.debug("QuestionSchemeEditor.init() - Editor Mode: " + editorInput.getEditorMode());
		}

		QuestionSchemes.init(((EditorInput) input).getProperties());

		if (editorInput.getEditorMode().equals(EditorModeType.NEW)) {
			try {
				questionScheme = QuestionSchemes.createQuestionScheme(editorInput.getId(), editorInput.getVersion(),
						editorInput.getParentId(), editorInput.getParentVersion());
			} catch (Exception e) {
				log.error("QuestionSchemeEditor.init(): " + e.getMessage());
				String errMess = Messages.getString("QuestionSchemeEditor.mess.ErrorDuringCreateNewQuestionScheme"); //$NON-NLS-1$
				ErrorDialog.openError(site.getShell(), Messages.getString("ErrorTitle"), null, new Status(IStatus.ERROR,
						ID, 0, errMess, e));
				System.exit(0);
			}
		} else if (editorInput.getEditorMode().equals(EditorModeType.EDIT)
				|| editorInput.getEditorMode().equals(EditorModeType.VIEW)) {
			try {
				questionScheme = QuestionSchemes.getQuestionScheme(editorInput.getId(), editorInput.getVersion(),
						editorInput.getParentId(), editorInput.getParentVersion());
			} catch (Exception e) {
				String errMess = Messages.getString("QuestionSchemeEditor.mess.GetQuestionSchemeByIdError"); //$NON-NLS-1$
				ErrorDialog.openError(site.getShell(), Messages.getString("ErrorTitle"), null, new Status(IStatus.ERROR,
						ID, 0, errMess, e));
				System.exit(0);
			}
		} else {
			String errMess = MessageFormat.format(
					Messages.getString("QuestionSchemeEditor.mess.UnknownEditorMode"), editorInput.getEditorMode()); //$NON-NLS-1$
			MessageDialog.openError(site.getShell(), Messages.getString("ErrorTitle"), errMess);
			System.exit(0);
		}
		
		// Initialize the Simple Editor Part with Question Scheme:
		super.init(site, input, (LabelDescription) questionScheme);

		this.site = site;
		setSite(site);
		setInput(editorInput);
		setPartName(editorInput.getId());

	}
	
}
