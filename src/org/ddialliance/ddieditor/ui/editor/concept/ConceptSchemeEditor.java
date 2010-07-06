package org.ddialliance.ddieditor.ui.editor.concept;

/**
 * Concept Scheme Editor.
 * 
 */
/*
 * $Author$ 
 * $Date$ 
 * $Revision$
 */

import java.text.MessageFormat;

import org.ddialliance.ddieditor.ui.dbxml.IDao;
import org.ddialliance.ddieditor.ui.dbxml.concept.ConceptSchemeDao;
import org.ddialliance.ddieditor.ui.editor.EditorInput;
import org.ddialliance.ddieditor.ui.editor.LabelDescriptionEditor;
import org.ddialliance.ddieditor.ui.editor.EditorInput.EditorModeType;
import org.ddialliance.ddieditor.ui.model.LabelDescription;
import org.ddialliance.ddieditor.ui.model.concept.ConceptScheme;
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

public class ConceptSchemeEditor extends LabelDescriptionEditor {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, ConceptSchemeEditor.class);
	public static final String ID = "org.ddialliance.ddieditor.ui.editor.concept.ConceptSchemeEditor";
	
	public ConceptSchemeEditor() {
		super(Messages
				.getString("ConceptSchemeEditor.label.ConceptSchemeEditorLabel.ConceptSchemeEditor"), Messages
				.getString("ConceptSchemeEditor.label.useTheEditorLabel.Description"), Messages
				.getString("ConceptSchemeEditor.label.ConceptSchemeTabItem"));
		dao = (IDao) new ConceptSchemeDao();
	}

	public String getPreferredPerspectiveId() {
		return ConceptsPerspective.ID;
	}

	public String getPerspectiveSwitchDialogText() {
		return MessageFormat.format(Messages.getString("perspective.switch.dialogtext"), Messages
				.getString("perspective.concepts"));
	}
	
}
