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
import org.ddialliance.ddiftp.util.Translator;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;

/**
 * Question Scheme Editor
 */
public class QuestionSchemeEditor extends LabelDescriptionEditor {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			QuestionSchemeEditor.class);
	public static final String ID = "org.ddialliance.ddieditor.ui.editor.question.QuestionSchemeEditor";

	public QuestionSchemeEditor() {
		super(
				Translator
						.trans("QuestionSchemeEditor.label.QuestionSchemeEditorLabel.QuestionSchemeEditor"),
				Translator
						.trans("QuestionSchemeEditor.label.useTheEditorLabel.Description"),
				Translator
						.trans("QuestionSchemeEditor.label.QuestionSchemeTabItem"),
				ID);
		dao = new QuestionSchemeDao();
	}

	public String getPreferredPerspectiveId() {
		return QuestionsPerspective.ID;
	}

	public String getPerspectiveSwitchDialogText() {
		return MessageFormat.format(
				Translator.trans("perspective.switch.dialogtext"),
				Translator.trans("perspective.questions"));
	}
}
