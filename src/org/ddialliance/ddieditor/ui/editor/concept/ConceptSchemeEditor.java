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
import org.ddialliance.ddieditor.ui.editor.LabelDescriptionEditor;
import org.ddialliance.ddieditor.ui.perspective.ConceptsPerspective;
import org.ddialliance.ddiftp.util.Translator;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;

public class ConceptSchemeEditor extends LabelDescriptionEditor {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			ConceptSchemeEditor.class);
	public static final String ID = "org.ddialliance.ddieditor.ui.editor.concept.ConceptSchemeEditor";

	public ConceptSchemeEditor() {
		super(
				Translator
						.trans("ConceptSchemeEditor.label.ConceptSchemeEditorLabel.ConceptSchemeEditor"),
				Translator
						.trans("ConceptSchemeEditor.label.useTheEditorLabel.Description"),
				Translator
						.trans("ConceptSchemeEditor.label.ConceptSchemeTabItem"),
				ID);
		dao = (IDao) new ConceptSchemeDao();
	}

	public String getPreferredPerspectiveId() {
		return ConceptsPerspective.ID;
	}

	public String getPerspectiveSwitchDialogText() {
		return MessageFormat.format(
				Translator.trans("perspective.switch.dialogtext"),
				Translator.trans("perspective.concepts"));
	}

}
