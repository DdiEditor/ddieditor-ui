package org.ddialliance.ddieditor.ui.perspective;

import org.ddialliance.ddieditor.ui.view.InstrumentView;
import org.ddialliance.ddieditor.ui.view.instrument.sequenceflow.SequenceflowView;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class InstrumentPerspective implements IPerspectiveFactory {
	public static final String ID = "org.ddialliance.ddieditor.ui.perspective.InstrumentPerspective";

	public void createInitialLayout(IPageLayout layout) {
		layout.addView(SequenceflowView.ID, IPageLayout.RIGHT, 0.70f, layout
				.getEditorArea()); 
		layout.addView(InstrumentView.ID, IPageLayout.LEFT, 0.55f, layout
				.getEditorArea());		
	}
}
