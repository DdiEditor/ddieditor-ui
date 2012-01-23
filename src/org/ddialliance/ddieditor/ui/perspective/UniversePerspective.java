package org.ddialliance.ddieditor.ui.perspective;

import org.ddialliance.ddieditor.ui.view.UniverseView;
import org.ddialliance.ddieditor.ui.view.dynamicview.DynamicView;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * Universe Perspective
 */
public class UniversePerspective implements IPerspectiveFactory {
	public static final String ID = "org.ddialliance.ddieditor.ui.perspective.UniversePerspective";

	public void createInitialLayout(IPageLayout layout) {
		PerspectiveUtil.createLeftFolder(layout, UniverseView.ID);
		PerspectiveUtil.createRightFolder(layout, DynamicView.ID);
	}
}