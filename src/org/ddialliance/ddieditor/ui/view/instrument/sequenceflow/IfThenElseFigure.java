package org.ddialliance.ddieditor.ui.view.instrument.sequenceflow;

import java.util.ArrayList;
import java.util.List;

import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.view.instrument.sequenceflow.figureutil.ActivityFigure;
import org.ddialliance.ddieditor.ui.view.instrument.sequenceflow.figureutil.FixedAnchor;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Point;

public class IfThenElseFigure extends ActivityFigure {
	public FixedAnchor thenAnchor;
	public ActivityFigure then;
	public ActivityFigure elze;
	public List<ActivityFigure> elseIf = new ArrayList<ActivityFigure>();

	public IfThenElseFigure(Label name, List<Label> attributes, LightXmlObjectType lightXmlObject) {
		super(name, attributes, lightXmlObject);

		thenAnchor = new FixedAnchor(this);
		thenAnchor.place = new Point(2, 1);
		sourceAnchors.put("then", thenAnchor);
	}
}
