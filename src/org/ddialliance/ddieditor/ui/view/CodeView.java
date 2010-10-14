package org.ddialliance.ddieditor.ui.view;

/**
 * Code View.
 * 
 */
import org.ddialliance.ddieditor.ui.model.ElementType;

public class CodeView extends View {
	public static final String ID = "org.ddialliance.ddieditor.ui.view.CodeView";

	public CodeView() {
		super(
				ViewContentType.CodeContent,
				Messages
						.getString("CodeView.label.codeNavigationLabel.CodeNavigation"),
				Messages
						.getString("CodeView.label.selectLabel.NavigatorDescription"),
				Messages.getString("CodeView.label.codeLabel.Code"),
				ElementType.CODE_SCHEME,
				Messages
						.getString("CodeView.label.codeTreeGroup.CodeStructure"));
	}
}
