package org.ddialliance.ddieditor.ui.perspective;

import org.ddialliance.ddieditor.ui.view.CodeView;
import org.ddialliance.ddieditor.ui.view.InfoView;
import org.ddialliance.ddieditor.ui.view.QuestionItemView;
import org.ddialliance.ddieditor.ui.view.VariableView;
import org.ddialliance.ddieditor.ui.view.instrument.sequenceflow.SequenceflowView;
import org.ddialliance.ddieditor.ui.view.variable.questionrelation.VariableQuestionView;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class CurationPerspective implements IPerspectiveFactory {
	public static final String ID = "org.ddialliance.ddieditor.ui.perspective.CurationPerspective";

	@Override
	public void createInitialLayout(IPageLayout layout) {
		IFolderLayout leftFolder = layout.createFolder("topLeft",
				IPageLayout.LEFT, 0.55f, layout.getEditorArea());
		leftFolder.addView(InfoView.ID);
		leftFolder.addView(VariableQuestionView.ID);
		leftFolder.addView(SequenceflowView.ID);
		leftFolder.addView(CodeView.ID);
		leftFolder.addView(VariableView.ID);
		leftFolder.addView(QuestionItemView.ID);
	}
}
