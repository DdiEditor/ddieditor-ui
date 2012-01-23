package org.ddialliance.ddieditor.ui.perspective;

import org.ddialliance.ddieditor.ui.view.CodeView;
import org.ddialliance.ddieditor.ui.view.ConceptView;
import org.ddialliance.ddieditor.ui.view.QuestionItemView;
import org.ddialliance.ddieditor.ui.view.UniverseView;
import org.ddialliance.ddieditor.ui.view.VariableView;
import org.ddialliance.ddieditor.ui.view.dynamicview.DynamicView;
import org.ddialliance.ddieditor.ui.view.instrument.sequenceflow.SequenceflowView;
import org.ddialliance.ddieditor.ui.view.variable.questionrelation.VariableQuestionView;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class CurationPerspective implements IPerspectiveFactory {
	public static final String ID = "org.ddialliance.ddieditor.ui.perspective.CurationPerspective";

	@Override
	public void createInitialLayout(IPageLayout layout) {
		PerspectiveUtil.createLeftFolder(layout, ConceptView.ID,
				UniverseView.ID, VariableView.ID, QuestionItemView.ID,
				CodeView.ID);

		PerspectiveUtil.createRightFolder(layout, VariableQuestionView.ID,
				SequenceflowView.ID, DynamicView.ID);
	}
}
