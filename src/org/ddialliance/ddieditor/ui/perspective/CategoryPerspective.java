package org.ddialliance.ddieditor.ui.perspective;

/**
 * Concepts Perspective.
 * 
 */
/*
 * $Author: ddajvj $ 
 * $Date: 2009-10-08 09:04:10 +0200 (Thu, 08 Oct 2009) $ 
 * $Revision: 616 $
 */

import org.ddialliance.ddieditor.ui.view.CategoryView;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class CategoryPerspective implements IPerspectiveFactory {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, CategoryPerspective.class);
	public static final String ID = "org.ddialliance.ddieditor.ui.perspective.CategoryPerspective";

	public void createInitialLayout(IPageLayout layout) {
		if (log.isDebugEnabled()) {
			log.debug("Create");	
		}
		addViewShortcuts(layout);
		layout.addView(CategoryView.ID, IPageLayout.LEFT, 0.50f, layout.getEditorArea());
		addFastViews(layout);
	}

	private void addViewShortcuts(IPageLayout layout) {
		// System.currentTimeMillis();
	}

	private void addFastViews(IPageLayout layout) {
		// System.currentTimeMillis();
	}
}

