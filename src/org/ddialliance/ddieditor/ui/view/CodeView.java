package org.ddialliance.ddieditor.ui.view;

/**
 * Code View.
 * 
 */
import java.util.Arrays;

import org.ddialliance.ddieditor.ui.model.ElementType;
import org.eclipse.swt.widgets.Composite;

public class CodeView extends View {
	public static final String ID = "org.ddialliance.ddieditor.ui.view.CodeView";

	public CodeView() {
		super(ViewContentType.CodeContent, Messages.getString("CodeView.label.codeNavigationLabel.CodeNavigation"),
				Messages.getString("CodeView.label.selectLabel.NavigatorDescription"), Messages
						.getString("CodeView.label.codeLabel.Code"), ElementType.CODE_SCHEME, Messages
						.getString("CodeView.label.codeTreeGroup.CodeStructure"), Arrays
						.asList(ElementType.CODE_SCHEME));
	}
}
