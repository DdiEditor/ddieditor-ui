package org.ddialliance.ddieditor.ui.editor.instrument;

import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.editor.widgetutil.LightXmlObjectTransfer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;

public class SequenceDragListener implements DragSourceListener {

	private final Viewer viewer;

	public SequenceDragListener(Viewer viewer) {
		super();
		this.viewer = viewer;
	}

	@Override
	public void dragStart(DragSourceEvent event) {
		// do nothing
	}

	@Override
	public void dragSetData(DragSourceEvent event) {
		IStructuredSelection selection = (IStructuredSelection) viewer
				.getSelection();
		LightXmlObjectType firstElement = (LightXmlObjectType) selection
				.getFirstElement();

		// set data on event
		if (LightXmlObjectTransfer.getInstance()
				.isSupportedType(event.dataType)) {
			event.data = firstElement;
		}
	}

	@Override
	public void dragFinished(DragSourceEvent event) {
		// do nothing
	}	
}
