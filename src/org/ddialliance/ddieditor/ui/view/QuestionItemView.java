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

import org.ddialliance.ddieditor.ui.model.ElementType;

public class QuestionItemView extends View {
	public static final String ID = "org.ddialliance.ddieditor.ui.view.QuestionItemView";

	public QuestionItemView() {
		super(
				ViewContentType.QuestionContent,
				Messages
						.getString("QuestionItemView.label.questionItemNavigationLabel.QuestionItemNavigation"),
				Messages
						.getString("QuestionItemView.label.selectLabel.NavigatorDescription"),
				Messages
						.getString("QuestionItemView.label.questionItemLabel.QuestionItem"),
				ElementType.QUESTION_SCHEME,
				Messages
						.getString("QuestionItemView.label.questionItemsTreeGroup.QuestionItemStructure"));
	}

}
