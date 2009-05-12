package org.ddialliance.ddieditor.ui;

import org.ddialliance.ddieditor.ui.view.QuestionItemView;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class QuestionsPerspective implements IPerspectiveFactory {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, QuestionsPerspective.class);
	public static final String ID = "org.ddialliance.ddieditor.ui.QuestionsPerspective";

	public void createInitialLayout(IPageLayout layout) {
		log.debug("QuestionsPerspective.createInitialLayout()");
		addViewShortcuts(layout);
		layout.addView(QuestionItemView.ID, IPageLayout.LEFT, 0.50f, layout.getEditorArea());
		addFastViews(layout);
	}

	private void addViewShortcuts(IPageLayout layout) {
		System.currentTimeMillis();
	}

	private void addFastViews(IPageLayout layout) {
		System.currentTimeMillis();
	}
}
