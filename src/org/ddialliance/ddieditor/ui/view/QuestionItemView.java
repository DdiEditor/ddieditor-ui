package org.ddialliance.ddieditor.ui.view;

/**
 * Question Item View.
 * 
 */
/*
 * $Author$ 
 * $Date$ 
 * $Revision$
 */

import java.util.Arrays;
import java.util.List;

import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.swt.widgets.Composite;

public class QuestionItemView extends View {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			QuestionItemView.class);
	public static final String ID = "org.ddialliance.ddieditor.ui.view.QuestionItemView";
	public static final List<String> newMenuLabelList = Arrays.asList(
			"Question Scheme", "Question Item");

	public QuestionItemView() {
		super(
				ViewContentType.QuestionContent,
				Messages
						.getString("QuestionItemView.label.questionItemNavigationLabel.QuestionItemNavigation"),
				Messages
						.getString("QuestionItemView.lable.selectLabel.NavigatorDescription"),
				Messages
						.getString("QuestionItemView.lable.questionItemLabel.Question"),
				"QuestionScheme",
				Messages
						.getString("QuestionItemView.lable.questionItemsTreeGroup.QuestionItemStructure"),
				newMenuLabelList);
	}

	public void createPartControl(Composite parent) {

		log.debug("");

		super.createPartControl(parent);

	}
}
