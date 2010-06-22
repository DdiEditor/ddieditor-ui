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
//	public static final List<String> newMenuLabelList = Arrays.asList(
//			"Code Scheme", "Code");

	public CodeView() {
		super(
				ViewContentType.CodeContent,
				Messages
						.getString("CodeView.label.codeNavigationLabel.CodeNavigation"),
				Messages
						.getString("CodeView.lable.selectLabel.NavigatorDescription"),
				Messages.getString("CodeView.lable.codeLabel.Code"),
				ElementType.CODE_SCHEME,
				Messages
						.getString("CodeView.lable.codeTreeGroup.CodeStructure"),
				Arrays.asList(ElementType.CODE));
	}

	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

	}
}
