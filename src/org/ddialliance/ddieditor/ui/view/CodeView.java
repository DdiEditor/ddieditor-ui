package org.ddialliance.ddieditor.ui.view;

import java.util.Arrays;
import java.util.List;

import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.swt.widgets.Composite;

public class CodeView extends View {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, CodeView.class);
	public static final String ID = "org.ddialliance.ddieditor.ui.view.CodeView";
	public static final List<String> newMenuLabelList = Arrays.asList("Code Scheme", "Code");

	public CodeView() {
		super(
				ViewContentType.CodeContent,
				Messages
						.getString("CodeView.label.codeNavigationLabel.CodeNavigation"),
				Messages
						.getString("CodeView.lable.selectLabel.NavigatorDescription"),
				Messages.getString("CodeView.lable.codeLabel.Code"),
				"CodeScheme",
				Messages
						.getString("CodeView.lable.codeTreeGroup.CodeStructure"),
				newMenuLabelList);
	}

	public void createPartControl(Composite parent) {

		log.debug("");

		super.createPartControl(parent);

	}

}
