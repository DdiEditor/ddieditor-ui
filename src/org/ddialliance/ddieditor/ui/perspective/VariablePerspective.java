package org.ddialliance.ddieditor.ui.perspective;

import org.ddialliance.ddieditor.ui.view.VariableView;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class VariablePerspective implements IPerspectiveFactory {
	public static final String ID = "org.ddialliance.ddieditor.ui.perspective.VariablePerspective";

	public void createInitialLayout(IPageLayout layout) {
		addViewShortcuts(layout);
		layout.addView(VariableView.ID, IPageLayout.LEFT, 0.50f,
				layout.getEditorArea());
		addFastViews(layout);
	}

	private void addViewShortcuts(IPageLayout layout) {
		//
	}

	private void addFastViews(IPageLayout layout) {
		//
	}
}
