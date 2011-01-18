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
import org.ddialliance.ddieditor.ui.view.variable.questionrelation.VariableQuestionView;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class QuestionsPerspective implements IPerspectiveFactory {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, QuestionsPerspective.class);
	public static final String ID = "org.ddialliance.ddieditor.ui.perspective.QuestionsPerspective";

	public void createInitialLayout(IPageLayout layout) {
		if (log.isDebugEnabled()) {
			log.debug("Create");	
		}
		addViewShortcuts(layout);
		layout.addView(QuestionItemView.ID, IPageLayout.LEFT, 0.55f, layout.getEditorArea());
		layout.addView(VariableQuestionView.ID, IPageLayout.RIGHT, 0.70f, layout
				.getEditorArea());		
		addFastViews(layout);
	}

	private void addViewShortcuts(IPageLayout layout) {
		// System.currentTimeMillis();
	}

	private void addFastViews(IPageLayout layout) {
		// System.currentTimeMillis();
	}
}
