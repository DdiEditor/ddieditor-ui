package org.ddialliance.ddieditor.ui.view.instrument.sequenceflow.figureutil;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FreeformFigure;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.ScalableFreeformLayeredPane;

public class Chart extends ScalableFreeformLayeredPane implements
		FreeformFigure {
	public Chart() {
		setLayoutManager(new FreeformLayout());
		setBorder(new MarginBorder(5));
		setBackgroundColor(ColorConstants.white);
		setOpaque(true);

	}
}
