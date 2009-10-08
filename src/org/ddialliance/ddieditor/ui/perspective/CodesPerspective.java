package org.ddialliance.ddieditor.ui.perspective;

/**
 * Codes Perspective.
 * 
 */
/*
 * $Author$ 
 * $Date$ 
 * $Revision$
 */

import org.ddialliance.ddieditor.ui.view.ConceptView;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class CodesPerspective implements IPerspectiveFactory {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, CodesPerspective.class);
	public static final String ID = "org.ddialliance.ddieditor.ui.perspective.CodesPerspective";

	public void createInitialLayout(IPageLayout layout) {
		if (log.isDebugEnabled()) {
			log.debug("Create");	
		}
		addViewShortcuts(layout);
		layout.addView(ConceptView.ID, IPageLayout.LEFT, 0.50f, layout.getEditorArea());
		addFastViews(layout);
	}

	private void addViewShortcuts(IPageLayout layout) {
		// System.currentTimeMillis();
	}

	private void addFastViews(IPageLayout layout) {
		// System.currentTimeMillis();
	}
}
