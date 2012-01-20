package org.ddialliance.ddieditor.ui.perspective;

import org.ddialliance.ddieditor.ui.view.VariableView;
import org.ddialliance.ddieditor.ui.view.dynamicview.DynamicView;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class VariablePerspective implements IPerspectiveFactory {
	public static final String ID = "org.ddialliance.ddieditor.ui.perspective.VariablePerspective";

	public void createInitialLayout(IPageLayout layout) {
		PerspectiveUtil.createLeftFolder(layout, VariableView.ID);
		PerspectiveUtil.createRightFolder(layout, DynamicView.ID);
	}
}
