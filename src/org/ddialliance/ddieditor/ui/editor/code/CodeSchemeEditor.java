package org.ddialliance.ddieditor.ui.editor.code;

import java.text.MessageFormat;


import org.ddialliance.ddieditor.ui.dbxml.code.CodeSchemeDao;
import org.ddialliance.ddieditor.ui.editor.LabelDescriptionEditor;
import org.ddialliance.ddieditor.ui.editor.EditorInput.EditorModeType;
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

/**
 * Code Scheme Editor
 */
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
			if (getEditorInputImpl().getEditorMode().equals(EditorModeType.NEW)) {
				CodeSchemeDao.create(codeScheme);
				getEditorInputImpl().setEditorMode(EditorModeType.EDIT);
			} else if (getEditorInputImpl().getEditorMode().equals(EditorModeType.EDIT)) {
				CodeSchemeDao.update(codeScheme);
			} else if (getEditorInputImpl().getEditorMode().equals(EditorModeType.VIEW)) {
				log.debug("*** Saved ignored! ***");
			}
		} catch (Exception e) {
			String errMess = Messages.getString("CodeSchemeEditor.mess.ErrorDuringSave"); //$NON-NLS-1$
			ErrorDialog.openError(site.getShell(), Messages.getString("ErrorTitle"), null, new Status(IStatus.ERROR,
					ID, 0, errMess, e));
			return;
		}
		updateParentView();
		editorStatus.clearChanged();
		log.debug("CodeSchemeEditor.doSave(1): " + editorStatus.getStatus());
	}
	
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		
		// Initialize Code Scheme Editor Part:
		super.setInput(input);
		if (log.isDebugEnabled()) {
			log.debug("CodeSchemeEditor.init() - Name: " + getEditorInputImpl().getName());
			log.debug("CodeSchemeEditor.init() - ID: " + getEditorInputImpl().getId());
			log.debug("CodeSchemeEditor.init() - Parent ID: " + getEditorInputImpl().getParentId());
			log.debug("CodeSchemeEditor.init() - Editor Mode: " + getEditorInputImpl().getEditorMode());
		}

		if (getEditorInputImpl().getEditorMode().equals(EditorModeType.NEW)) {
			try {
				codeScheme = CodeSchemeDao.createCodeScheme(getEditorInputImpl().getId(), getEditorInputImpl().getVersion(),
						getEditorInputImpl().getParentId(), getEditorInputImpl().getParentVersion());
			} catch (Exception e) {
				log.error("CodeSchemeEditor.init(): " + e.getMessage());
				String errMess = Messages.getString("CodeSchemeEditor.mess.ErrorDuringCreateNewCode"); //$NON-NLS-1$
				ErrorDialog.openError(site.getShell(), Messages.getString("ErrorTitle"), null, new Status(IStatus.ERROR,
						ID, 0, errMess, e));
				System.exit(0);
			}
		} else if (getEditorInputImpl().getEditorMode().equals(EditorModeType.EDIT)
				|| getEditorInputImpl().getEditorMode().equals(EditorModeType.VIEW)) {
			try {
				codeScheme = CodeSchemeDao.getCodeScheme(getEditorInputImpl().getId(), getEditorInputImpl().getVersion(),
						getEditorInputImpl().getParentId(), getEditorInputImpl().getParentVersion());
			} catch (Exception e) {
				String errMess = Messages.getString("CodeSchemeEditor.mess.GetCodeByIdError"); //$NON-NLS-1$
				ErrorDialog.openError(site.getShell(), Messages.getString("ErrorTitle"), null, new Status(IStatus.ERROR,
						ID, 0, errMess, e));
				System.exit(0);
			}
		} else {
			String errMess = MessageFormat.format(
					Messages.getString("CodeSchemeEditor.mess.UnknownEditorMode"), getEditorInputImpl().getEditorMode()); //$NON-NLS-1$
			MessageDialog.openError(site.getShell(), Messages.getString("ErrorTitle"), errMess);
			System.exit(0);
		}
		
		// Initialize the Simple Editor Part with Code Scheme:
		super.init(site, input);
		
		this.site = site;
		setSite(site);
		setInput(getEditorInputImpl());
		setPartName(getEditorInputImpl().getId());
	}
}
