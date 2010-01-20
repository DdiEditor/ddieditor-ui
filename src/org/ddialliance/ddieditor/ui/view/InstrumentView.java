package org.ddialliance.ddieditor.ui.view;

import java.util.Arrays;

import org.ddialliance.ddieditor.ui.model.ElementType;
import org.eclipse.swt.widgets.Composite;

public class InstrumentView extends View {
	public static final String ID = "org.ddialliance.ddieditor.ui.view.InstrumentView";

	public InstrumentView() {
		super(ViewContentType.InstrumentationContent, Messages
				.getString("InstrumentView.label.navigation"), Messages
				.getString("InstrumentView.label.label.Instrument"), Messages
				.getString("InstrumentView.label.selectLabel.NavigatorDescription"),
				ElementType.CONTROL_CONSTRUCT_SCHEME, "", Arrays.asList(
						ElementType.IF_THEN_ELSE, ElementType.REPEAT_UNTIL,
						ElementType.REPEAT_WHILE, ElementType.LOOP,
						ElementType.SEQUENCE, ElementType.COMPUTATION_ITEM,
						ElementType.STATEMENT_ITEM,
						ElementType.QUESTION_CONSTRUCT));
	}

	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
	}
}