package org.ddialliance.ddieditor.ui.view.instrument.sequenceflow.figureutil;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.graphics.Color;

public abstract class ActivityFigure extends Figure {
	public LightXmlObjectType lightXmlObject;
	public static Color backgroundColor = new Color(null, 232, 242, 254);
	private SubFigure subFigure = new SubFigure();

	public FixedAnchor inAnchor, outAnchor;
	protected Hashtable targetAnchors = new Hashtable();
	protected Hashtable sourceAnchors = new Hashtable();

	public ActivityFigure(Label name, List<Label> attributes, LightXmlObjectType lightXmlObject) {
		this.lightXmlObject = lightXmlObject;
		
		ToolbarLayout layout = new ToolbarLayout();
		setLayoutManager(layout);
		setBorder(new LineBorder(ColorConstants.black, 1));
		setBackgroundColor(backgroundColor);
		setOpaque(true);

		// label
		add(name);

		// attributes
		for (Label label : attributes) {
			subFigure.add(label);
		}
		add(subFigure);

		// anchors
		inAnchor = new FixedAnchor(this);
		inAnchor.place = new Point(1, 0);
		targetAnchors.put("in_proc", inAnchor);
		outAnchor = new FixedAnchor(this);
		outAnchor.place = new Point(1, 2);
		sourceAnchors.put("out_proc", outAnchor);
	}

	public SubFigure getSubFigure() {
		return subFigure;
	}

	public ConnectionAnchor createConnectionAnchorAt(Point p) {
		ConnectionAnchor closest = null;
		long min = Long.MAX_VALUE;
		Hashtable conn = getSourceConnectionAnchors();
		conn.putAll(getTargetConnectionAnchors());
		Enumeration e = conn.elements();
		while (e.hasMoreElements()) {
			ConnectionAnchor c = (ConnectionAnchor) e.nextElement();
			org.eclipse.draw2d.geometry.Point p2 = c.getLocation(null);

			long d = p.getDistance2(p2);
			if (d < min) {
				min = d;
				closest = c;
			}
		}
		return closest;
	}

	public ConnectionAnchor getSourceConnectionAnchor(String name) {
		return (ConnectionAnchor) sourceAnchors.get(name);
	}

	public ConnectionAnchor getTargetConnectionAnchor(String name) {
		return (ConnectionAnchor) targetAnchors.get(name);
	}

	public String getSourceAnchorName(ConnectionAnchor c) {
		Enumeration e = sourceAnchors.keys();
		String name;
		while (e.hasMoreElements()) {
			name = (String) e.nextElement();
			if (sourceAnchors.get(name).equals(c))
				return name;
		}
		return null;
	}

	public String getTargetAnchorName(ConnectionAnchor c) {
		Enumeration e = targetAnchors.keys();
		String name;
		while (e.hasMoreElements()) {
			name = (String) e.nextElement();
			if (targetAnchors.get(name).equals(c))
				return name;
		}
		return null;
	}

	public ConnectionAnchor getSourceConnectionAnchorAt(Point p) {
		ConnectionAnchor closest = null;
		long min = Long.MAX_VALUE;
		Enumeration e = getSourceConnectionAnchors().elements();
		while (e.hasMoreElements()) {
			ConnectionAnchor c = (ConnectionAnchor) e.nextElement();
			Point p2 = c.getLocation(null);
			long d = p.getDistance2(p2);
			if (d < min) {
				min = d;
				closest = c;
			}
		}
		return closest;
	}

	public Hashtable getSourceConnectionAnchors() {
		return sourceAnchors;
	}

	public ConnectionAnchor getTargetConnectionAnchorAt(Point p) {
		ConnectionAnchor closest = null;
		long min = Long.MAX_VALUE;
		Enumeration e = getTargetConnectionAnchors().elements();
		while (e.hasMoreElements()) {
			ConnectionAnchor c = (ConnectionAnchor) e.nextElement();
			Point p2 = c.getLocation(null);
			long d = p.getDistance2(p2);
			if (d < min) {
				min = d;
				closest = c;
			}
		}
		return closest;
	}

	public Hashtable getTargetConnectionAnchors() {
		return targetAnchors;
	}
}