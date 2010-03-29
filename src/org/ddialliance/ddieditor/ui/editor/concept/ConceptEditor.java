package org.ddialliance.ddieditor.ui.editor.concept;

/**
 * Concept Editor.
 * 
 */
/*
 * $Author$ 
 * $Date$ 
 * $Revision$
 */

import java.text.MessageFormat;

import org.ddialliance.ddieditor.ui.dbxml.IDao;
import org.ddialliance.ddieditor.ui.dbxml.concept.Concepts;
import org.ddialliance.ddieditor.ui.editor.EditorInput;
import org.ddialliance.ddieditor.ui.editor.LabelDescriptionEditor;
import org.ddialliance.ddieditor.ui.editor.EditorInput.EditorModeType;
import org.ddialliance.ddieditor.ui.model.LabelDescription;
import org.ddialliance.ddieditor.ui.model.concept.Concept;
import org.ddialliance.ddieditor.ui.perspective.ConceptsPerspective;
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

public class ConceptEditor extends LabelDescriptionEditor {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			ConceptEditor.class);
	public static final String ID = "org.ddialliance.ddieditor.ui.editor.concept.ConceptEditor";

	// Member variables:
	private Concept modelImpl;
	private IEditorSite site;

	public ConceptEditor() {
		super(
				Messages
						.getString("ConceptEditor.label.ConceptEditorLabel.ConceptEditor"),
				Messages
						.getString("ConceptEditor.label.useTheEditorLabel.Description"),
				Messages.getString("ConceptEditor.label.ConceptTabItem"));
		dao = (IDao) new Concepts();
	}

	public String getPreferredPerspectiveId() {
		return ConceptsPerspective.ID;
	}

	public String getPerspectiveSwitchDialogText() {
		return MessageFormat.format(Messages
				.getString("perspective.switch.dialogtext"), Messages
				.getString("perspective.concepts"));
	}

	/**
	 * Create contents of the editor part
	 * 
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout());
		log.debug("ConceptEditor.createPartControl called");
		super.createPartControl(parent);
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		log.debug("ConceptEditor.doSave()");
		super.doSave(monitor);

		try {
			modelImpl.validate();
		} catch (Exception e1) {
			String errMess = Messages
					.getString("ConceptEditor.mess.ValidationError"); //$NON-NLS-1$
			ErrorDialog.openError(site.getShell(), Messages
					.getString("ErrorTitle"), null, new Status(IStatus.ERROR,
					ID, 0, errMess, null));
			return;
		}
		try {
			if (editorInput.getEditorMode().equals(EditorModeType.NEW)) {
				dao.create(modelImpl);
				editorInput.setEditorMode(EditorModeType.EDIT);
			} else if (editorInput.getEditorMode()
					.equals(EditorModeType.EDIT)) {
				dao.update(modelImpl);
			} else if (editorInput.getEditorMode()
					.equals(EditorModeType.VIEW)) {
				log.debug("*** Saved ignored! ***");
			}
		} catch (Exception e) {
			String errMess = Messages
					.getString("ConceptEditor.mess.ErrorDuringSave"); //$NON-NLS-1$
			ErrorDialog.openError(site.getShell(), Messages
					.getString("ErrorTitle"), null, new Status(IStatus.ERROR,
					ID, 0, errMess, e));
			return;
		}
		updateParentView();
		editorStatus.clearChanged();
		log.debug("ConceptEditor.doSave(1): " + editorStatus.getStatus());
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {

		// Initialize the Simple Editor Part with Concept:
		super.init(site, input);
		log.debug("ConceptEditor.init()");
		this.modelImpl = (Concept) model;
	}
}
