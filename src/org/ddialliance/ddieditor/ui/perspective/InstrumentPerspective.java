package org.ddialliance.ddieditor.ui.perspective;

import org.ddialliance.ddieditor.ui.view.InstrumentView;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class InstrumentPerspective implements IPerspectiveFactory {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			InstrumentPerspective.class);
	public static final String ID = "org.ddialliance.ddieditor.ui.perspective.InstrumentPerspective";

	public void createInitialLayout(IPageLayout layout) {
		if (log.isDebugEnabled()) {
			log.debug("Create");
		}
		addViewShortcuts(layout);
		layout.addView(InstrumentView.ID, IPageLayout.LEFT, 0.50f, layout
				.getEditorArea());
		addFastViews(layout);
	}

	private void addViewShortcuts(IPageLayout layout) {
		//
	}

	private void addFastViews(IPageLayout layout) {
		//
	}
}
