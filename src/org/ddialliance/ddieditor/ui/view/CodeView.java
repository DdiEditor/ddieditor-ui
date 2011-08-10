package org.ddialliance.ddieditor.ui.view;

/**
 * Code View.
 * 
 */
import org.ddialliance.ddieditor.ui.model.ElementType;
import org.ddialliance.ddiftp.util.Translator;

public class CodeView extends View {
	public static final String ID = "org.ddialliance.ddieditor.ui.view.CodeView";

	public CodeView() {
		super(
				ViewContentType.CodeContent,
				Translator
						.trans("CodeView.label.codeNavigationLabel.CodeNavigation"),
				Translator
						.trans("CodeView.label.selectLabel.NavigatorDescription"),
				Translator.trans("CodeView.label.codeLabel.Code"),
				ElementType.CODE_SCHEME, Translator
						.trans("CodeView.label.codeTreeGroup.CodeStructure"),
				ID);
	}
}
