package org.ddialliance.ddieditor.ui.view.instrument.sequenceflow;

import java.util.List;

import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.view.instrument.sequenceflow.figureutil.ActivityFigure;
import org.eclipse.draw2d.Label;

public class SequenceFigure extends ActivityFigure {
	public SequenceFigure(Label name, List<Label> attributes,
			LightXmlObjectType lightXmlObject) {
		super(name, attributes, lightXmlObject);
	}
}
