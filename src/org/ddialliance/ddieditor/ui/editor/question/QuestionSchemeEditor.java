package org.ddialliance.ddieditor.ui.editor.question;

/*
 * $Author$ 
 * $Date$ 
 * $Revision$
 */

import java.text.MessageFormat;

import org.ddialliance.ddieditor.ui.dbxml.question.QuestionSchemeDao;
import org.ddialliance.ddieditor.ui.editor.LabelDescriptionEditor;
import org.ddialliance.ddieditor.ui.perspective.QuestionsPerspective;
import org.ddialliance.ddieditor.ui.view.Messages;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;

/**
 * Question Scheme Editor
 */
public class QuestionSchemeEditor extends LabelDescriptionEditor {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, QuestionSchemeEditor.class);
	public static final String ID = "org.ddialliance.ddieditor.ui.editor.question.QuestionSchemeEditor";

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
}
