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

import org.ddialliance.ddieditor.ui.dbxml.IDao;
import org.ddialliance.ddieditor.ui.dbxml.code.CodeSchemeDao;
import org.ddialliance.ddieditor.ui.editor.LabelDescriptionEditor;
import org.ddialliance.ddieditor.ui.perspective.CodesPerspective;
import org.ddialliance.ddieditor.ui.view.Messages;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;

public class CodeSchemeEditor extends LabelDescriptionEditor{
	private static Log log = LogFactory.getLog(LogType.SYSTEM, CodeSchemeEditor.class);
	public static final String ID = "org.ddialliance.ddieditor.ui.editor.code.CodeSchemeEditor";

	public CodeSchemeEditor() {
		super(Messages
				.getString("CodeSchemeEditor.label.CodeSchemeEditorLabel.CodeSchemeEditor"), Messages
				.getString("CodeSchemeEditor.label.useTheEditorLabel.Description"), Messages
				.getString("CodeSchemeEditor.label.CodeSchemeTabItem"));
		dao = (IDao) new CodeSchemeDao();
	}

	public String getPreferredPerspectiveId() {
		return CodesPerspective.ID;
	}

	public String getPerspectiveSwitchDialogText() {
		return MessageFormat.format(Messages.getString("perspective.switch.dialogtext"), Messages
				.getString("perspective.codes"));
	}

}
