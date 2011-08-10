package org.ddialliance.ddieditor.ui.editor.instrument;

import java.text.MessageFormat;

import org.ddialliance.ddieditor.ui.dbxml.IDao;
import org.ddialliance.ddieditor.ui.dbxml.instrument.ControlConstructSchemeDao;
import org.ddialliance.ddieditor.ui.editor.LabelDescriptionEditor;
import org.ddialliance.ddieditor.ui.perspective.InstrumentPerspective;
import org.ddialliance.ddiftp.util.Translator;

public class ControlConstructSchemeEditor extends LabelDescriptionEditor {
	public static final String ID = "org.ddialliance.ddieditor.ui.editor.instrument.ControlConstructSchemeEditor";

	public ControlConstructSchemeEditor() {
		super(
				Translator.trans("ControlConstructSchemeEditor.label"),
				Translator
						.trans("ControlConstructSchemeEditor.label.useTheEditorLabel.Description"),
				Translator
						.trans("ControlConstructSchemeEditor.label.ConceptSchemeTabItem"),
				ID);
		dao = (IDao) new ControlConstructSchemeDao();
	}

	public String getPreferredPerspectiveId() {
		return InstrumentPerspective.ID;
	}

	public String getPerspectiveSwitchDialogText() {
		return MessageFormat.format(
				Translator.trans("perspective.switch.dialogtext"),
				Translator.trans("perspective.concepts"));
	}
}
