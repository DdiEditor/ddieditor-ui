package org.ddialliance.ddieditor.ui.editor.instrument;

import java.text.MessageFormat;

import org.ddialliance.ddieditor.ui.dbxml.IDao;
import org.ddialliance.ddieditor.ui.dbxml.instrument.ControlConstructSchemeDao;
import org.ddialliance.ddieditor.ui.editor.LabelDescriptionEditor;
import org.ddialliance.ddieditor.ui.perspective.InstrumentPerspective;
import org.ddialliance.ddieditor.ui.view.Messages;

public class ControlConstructSchemeEditor extends LabelDescriptionEditor {
	public static final String ID = "org.ddialliance.ddieditor.ui.editor.instrument.ControlConstructSchemeEditor";
	
	public ControlConstructSchemeEditor() {
		super(Messages
				.getString("ControlConstructSchemeEditor.label"), Messages
				.getString("ControlConstructSchemeEditor.label.useTheEditorLabel.Description"), Messages
				.getString("ControlConstructSchemeEditor.label.ConceptSchemeTabItem"));
		dao = (IDao) new ControlConstructSchemeDao();
	}

	public String getPreferredPerspectiveId() {
		return InstrumentPerspective.ID;
	}

	public String getPerspectiveSwitchDialogText() {
		return MessageFormat.format(Messages.getString("perspective.switch.dialogtext"), Messages
				.getString("perspective.concepts"));
	}
}
