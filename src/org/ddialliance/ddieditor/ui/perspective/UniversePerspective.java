package org.ddialliance.ddieditor.ui.perspective;

/**
 * Universe Perspective.
 * 
 */
/*
 * $Author: ddadak $ 
 * $Date: 2010-06-15 09:20:17 +0200 (Tue, 15 Jun 2010) $ 
 * $Revision: 1220 $
 */

import org.ddialliance.ddieditor.ui.view.UniverseView;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class UniversePerspective implements IPerspectiveFactory {
		private static Log log = LogFactory.getLog(LogType.SYSTEM, UniversePerspective.class);
		public static final String ID = "org.ddialliance.ddieditor.ui.perspective.UniversePerspective";

		public void createInitialLayout(IPageLayout layout) {
			if (log.isDebugEnabled()) {
				log.debug("Create +++++++++");	
			}
			addViewShortcuts(layout);
			layout.addView(UniverseView.ID, IPageLayout.LEFT, 0.50f, layout.getEditorArea());
			addFastViews(layout);
		}

		private void addViewShortcuts(IPageLayout layout) {
			// System.currentTimeMillis();
		}

		private void addFastViews(IPageLayout layout) {
			// System.currentTimeMillis();
		}
	}