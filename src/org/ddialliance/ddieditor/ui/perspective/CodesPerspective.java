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

//import org.ddialliance.ddieditor.ui.view.ConceptView;
import org.ddialliance.ddieditor.ui.view.CodeView;
import org.ddialliance.ddieditor.ui.view.dynamicview.DynamicView;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class CodesPerspective implements IPerspectiveFactory {
	public static final String ID = "org.ddialliance.ddieditor.ui.perspective.CodesPerspective";

	public void createInitialLayout(IPageLayout layout) {
		PerspectiveUtil.createLeftFolder(layout, CodeView.ID);
		PerspectiveUtil.createRightFolder(layout, DynamicView.ID);
	}
}
