package org.ddialliance.ddieditor.ui.view;

import org.ddialliance.ddieditor.ui.model.ElementType;

public class VariableView extends View {
	public static final String ID = "org.ddialliance.ddieditor.ui.view.VariableView";

	public VariableView() {
		super(
				ViewContentType.VariableContent,
				Messages.getString("VariableView.label.navigation"),
				Messages.getString("VariableView.label.label.VariableScheme"),
				Messages.getString("VariableView.label.selectLabel.NavigatorDescription"),
				ElementType.VARIABLE_SCHEME, "");
	}
}
