package org.ddialliance.ddieditor.ui.view;

import org.ddialliance.ddieditor.ui.editor.widgetutil.lightxmlobjectdnd.LightXmlObjectDragListener;
import org.ddialliance.ddieditor.ui.editor.widgetutil.lightxmlobjectdnd.LightXmlObjectTransfer;
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
				Messages.getString("InstrumentView.label.selectLabel.NavigatorDescription"),
				Messages.getString("InstrumentView.label.label.Instrument"),
				ElementType.CONTROL_CONSTRUCT_SCHEME, "", ID);
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
				new LightXmlObjectDragListener(treeViewer, ID));		
	}
}