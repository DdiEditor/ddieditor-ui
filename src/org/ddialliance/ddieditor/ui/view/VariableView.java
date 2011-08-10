package org.ddialliance.ddieditor.ui.view;

import org.ddialliance.ddieditor.ui.model.ElementType;
import org.ddialliance.ddiftp.util.Translator;

public class VariableView extends View {
	public static final String ID = "org.ddialliance.ddieditor.ui.view.VariableView";

	public VariableView() {
		super(ViewContentType.VariableContent, Translator
				.trans("VariableView.label.navigation"), Translator
				.trans("VariableView.label.selectLabel.NavigatorDescription"),
				Translator.trans("VariableView.label.label.VariableScheme"),
				ElementType.VARIABLE_SCHEME, "", ID);
	}
}
