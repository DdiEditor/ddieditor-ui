package org.ddialliance.ddieditor.ui.perspective;

import org.ddialliance.ddieditor.ui.view.ConceptView;
import org.ddialliance.ddieditor.ui.view.dynamicview.DynamicView;
import org.ddialliance.ddieditor.ui.view.variable.questionrelation.VariableQuestionView;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * Concepts Perspective.
 */
public class ConceptsPerspective implements IPerspectiveFactory {
	public static final String ID = "org.ddialliance.ddieditor.ui.perspective.ConceptsPerspective";

	public void createInitialLayout(IPageLayout layout) {
		PerspectiveUtil.createLeftFolder(layout, ConceptView.ID);
		PerspectiveUtil.createRightFolder(layout, DynamicView.ID,
				VariableQuestionView.ID);
	}
}
