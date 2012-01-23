package org.ddialliance.ddieditor.ui.perspective;

import org.ddialliance.ddieditor.ui.view.CategoryView;
import org.ddialliance.ddieditor.ui.view.dynamicview.DynamicView;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * Category Perspective
 */
public class CategoryPerspective implements IPerspectiveFactory {
	public static final String ID = "org.ddialliance.ddieditor.ui.perspective.CategoryPerspective";

	public void createInitialLayout(IPageLayout layout) {
		PerspectiveUtil.createLeftFolder(layout, CategoryView.ID);
		PerspectiveUtil.createRightFolder(layout, DynamicView.ID);
	}
}
