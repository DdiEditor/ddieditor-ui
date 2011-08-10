package org.ddialliance.ddieditor.ui.editor.variable;

import java.text.MessageFormat;

import org.ddialliance.ddieditor.ui.dbxml.IDao;
import org.ddialliance.ddieditor.ui.dbxml.variable.VariableSchemeDao;
import org.ddialliance.ddieditor.ui.editor.LabelDescriptionEditor;
import org.ddialliance.ddieditor.ui.perspective.VariablePerspective;
import org.ddialliance.ddiftp.util.Translator;

public class VariableSchemeEditor extends LabelDescriptionEditor {
	public static final String ID = "org.ddialliance.ddieditor.ui.editor.variable.VariableSchemeEditor";

	public VariableSchemeEditor() {
		super(
				Translator.trans("VariableSchemeEditor.label"),
				Translator
						.trans("VariableSchemeEditor.label.useTheEditorLabel.Description"),
				Translator.trans("VariableScheme"), ID);
		dao = (IDao) new VariableSchemeDao();
	}

	public String getPreferredPerspectiveId() {
		return VariablePerspective.ID;
	}

	public String getPerspectiveSwitchDialogText() {
		return MessageFormat.format(
				Translator.trans("perspective.switch.dialogtext"),
				Translator.trans("perspective.concepts"));
	}
}
