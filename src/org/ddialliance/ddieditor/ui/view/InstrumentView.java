package org.ddialliance.ddieditor.ui.view;

import org.ddialliance.ddieditor.ui.editor.instrument.SequenceDragListener;
import org.ddialliance.ddieditor.ui.editor.widgetutil.LightXmlObjectTransfer;
import org.ddialliance.ddieditor.ui.model.ElementType;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;

public class InstrumentView extends View {
	public static final String ID = "org.ddialliance.ddieditor.ui.view.InstrumentView";

	public InstrumentView() {
		super(
				ViewContentType.InstrumentationContent,
				Messages.getString("InstrumentView.label.navigation"),
				Messages.getString("InstrumentView.label.label.Instrument"),
				Messages.getString("InstrumentView.label.selectLabel.NavigatorDescription"),
				ElementType.CONTROL_CONSTRUCT_SCHEME, "");
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		// dnd
		int operations = DND.DROP_MOVE | DND.DROP_COPY;
		Transfer[] transferTypes = new Transfer[] { LightXmlObjectTransfer
				.getInstance() };

		// jface
		this.treeViewer.addDragSupport(operations, transferTypes,
				new SequenceDragListener(treeViewer, ID));		
	}
}