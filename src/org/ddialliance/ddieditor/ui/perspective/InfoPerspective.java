package org.ddialliance.ddieditor.ui.perspective;

import org.ddialliance.ddieditor.ui.view.InfoView;
import org.ddialliance.ddieditor.ui.view.dynamicview.DynamicView;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * Info (overview) Perspective.
 * 
 */
public class InfoPerspective  implements IPerspectiveFactory {
	public static final String ID = "org.ddialliance.ddieditor.ui.perspective.InfoPerspective";

	public void createInitialLayout(IPageLayout layout) {
		PerspectiveUtil.createLeftFolder(layout, InfoView.ID);
		PerspectiveUtil.createRightFolder(layout, DynamicView.ID);	
	}
}
