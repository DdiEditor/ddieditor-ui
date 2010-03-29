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

import org.ddialliance.ddieditor.ui.dbxml.question.QuestionSchemeDao;
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
	private QuestionScheme modelImpl;
	private IEditorSite site;
	public QuestionSchemeEditor() {
		super(Messages
				.getString("QuestionSchemeEditor.label.QuestionSchemeEditorLabel.QuestionSchemeEditor"), Messages
				.getString("QuestionSchemeEditor.label.useTheEditorLabel.Description"), Messages
				.getString("QuestionSchemeEditor.label.QuestionSchemeTabItem"));
		dao = new QuestionSchemeDao();
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
			modelImpl.validate();
		} catch (Exception e1) {
			String errMess = Messages.getString("QuestionSchemeEditor.mess.ValidationError"); //$NON-NLS-1$
			ErrorDialog.openError(site.getShell(), Messages.getString("ErrorTitle"), null, new Status(IStatus.ERROR,
					ID, 0, errMess, null));
			return;
		}
		try {
			if (editorInput.getEditorMode().equals(EditorModeType.NEW)) {
				dao.create(modelImpl);
				editorInput.setEditorMode(EditorModeType.EDIT);
			} else if (editorInput.getEditorMode().equals(EditorModeType.EDIT)) {
				dao.update(modelImpl);
			} else if (editorInput.getEditorMode().equals(EditorModeType.VIEW)) {
				log.debug("*** Saved ignored! ***");
			}
		} catch (Exception e) {
			String errMess = Messages.getString("QuestionSchemeEditor.mess.ErrorDuringSave"); //$NON-NLS-1$
			ErrorDialog.openError(site.getShell(), Messages.getString("ErrorTitle"), null, new Status(IStatus.ERROR,
					ID, 0, errMess, e));
			return;
		}
		updateParentView();
		editorStatus.clearChanged();
		log.debug("QuestionSchemeEditor.doSave(1): " + editorStatus.getStatus());
	}
	
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		
		super.init(site, input);
		log.debug("QuestionItemEditor.init()");
		this.modelImpl = (QuestionScheme) model;
	}
	
}
