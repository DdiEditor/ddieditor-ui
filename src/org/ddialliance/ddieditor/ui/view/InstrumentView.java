package org.ddialliance.ddieditor.ui.view;

import org.ddialliance.ddieditor.ui.model.ElementType;

public class InstrumentView extends View {
	public static final String ID = "org.ddialliance.ddieditor.ui.view.InstrumentView";

	public InstrumentView() {
		super(
				ViewContentType.InstrumentationContent,
				Messages.getString("InstrumentView.label.navigation"),
				Messages.getString("InstrumentView.label.label.Instrument"),
				Messages
						.getString("InstrumentView.label.selectLabel.NavigatorDescription"),
				ElementType.CONTROL_CONSTRUCT_SCHEME, "");
	}
}