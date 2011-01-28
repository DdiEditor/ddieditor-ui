package org.ddialliance.ddieditor.ui.editor.variable;

import java.text.MessageFormat;

import org.ddialliance.ddieditor.ui.dbxml.IDao;
import org.ddialliance.ddieditor.ui.dbxml.variable.VariableSchemeDao;
import org.ddialliance.ddieditor.ui.editor.LabelDescriptionEditor;
import org.ddialliance.ddieditor.ui.perspective.VariablePerspective;
import org.ddialliance.ddieditor.ui.view.Messages;

public class VariableSchemeEditor extends LabelDescriptionEditor {
	public static final String ID = "org.ddialliance.ddieditor.ui.editor.variable.VariableSchemeEditor";

	public VariableSchemeEditor() {
		super(
				Messages.getString("VariableSchemeEditor.label"),
				Messages.getString("VariableSchemeEditor.label.useTheEditorLabel.Description"),
				Messages.getString("VariableScheme"), ID);
		dao = (IDao) new VariableSchemeDao();
	}

	public String getPreferredPerspectiveId() {
		return VariablePerspective.ID;
	}

	public String getPerspectiveSwitchDialogText() {
		return MessageFormat.format(
				Messages.getString("perspective.switch.dialogtext"),
				Messages.getString("perspective.concepts"));
	}
}
