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

import org.ddialliance.ddieditor.ui.dbxml.concept.ConceptDao;
import org.ddialliance.ddieditor.ui.editor.LabelDescriptionEditor;
import org.ddialliance.ddieditor.ui.perspective.ConceptsPerspective;
import org.ddialliance.ddiftp.util.Translator;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;

public class ConceptEditor extends LabelDescriptionEditor {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			ConceptEditor.class);
	public static final String ID = "org.ddialliance.ddieditor.ui.editor.concept.ConceptEditor";

	public ConceptEditor() {
		super(
				Translator
						.trans("ConceptEditor.label.ConceptEditorLabel.ConceptEditor"),
				Translator
						.trans("ConceptEditor.label.useTheEditorLabel.Description"),
				Translator.trans("ConceptEditor.label.ConceptTabItem"), ID);
		super.dao = new ConceptDao();
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
