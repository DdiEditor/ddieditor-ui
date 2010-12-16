package org.ddialliance.ddieditor.ui.view.instrument.sequenceflow.figureutil;

import org.eclipse.draw2d.ManhattanConnectionRouter;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.PolylineDecoration;

public class ConnectionFigure extends PolylineConnection {
	public ConnectionFigure() {
		setTargetDecoration(new PolylineDecoration());
		setConnectionRouter(new ManhattanConnectionRouter());
	}
}
