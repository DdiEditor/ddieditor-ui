package org.ddialliance.ddieditor.ui.perspective;

/**
 * Questions Perspective.
 * 
 */
/*
 * $Author$ 
 * $Date$ 
 * $Revision$
 */

import org.ddialliance.ddieditor.ui.view.QuestionItemView;
import org.ddialliance.ddieditor.ui.view.dynamicview.DynamicView;
import org.ddialliance.ddieditor.ui.view.variable.questionrelation.VariableQuestionView;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class QuestionsPerspective implements IPerspectiveFactory {
	public static final String ID = "org.ddialliance.ddieditor.ui.perspective.QuestionsPerspective";

	public void createInitialLayout(IPageLayout layout) {
		PerspectiveUtil.createLeftFolder(layout, QuestionItemView.ID);
		PerspectiveUtil.createRightFolder(layout, DynamicView.ID,
				VariableQuestionView.ID);
	}
}
