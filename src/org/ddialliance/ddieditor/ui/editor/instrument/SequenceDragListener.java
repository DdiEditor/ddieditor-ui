package org.ddialliance.ddieditor.ui.editor.instrument;

import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.editor.widgetutil.LightXmlObjectTransfer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
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
		IStructuredSelection selection = (IStructuredSelection) viewer
				.getSelection();
		System.out.println("Start Drag: " + selection);
		System.out.println(event.doit);
		event.doit = true;
	}

	@Override
	public void dragSetData(DragSourceEvent event) {
		System.out.println("dragSetData");
		// Here you do the convertion to the type which is expected.

		IStructuredSelection selection = (IStructuredSelection) viewer
				.getSelection();
		LightXmlObjectType firstElement = (LightXmlObjectType) selection
				.getFirstElement();

		if (LightXmlObjectTransfer.getInstance()
				.isSupportedType(event.dataType)) {
			event.data = firstElement;
		}
		System.out.println(selection.getFirstElement());
	}

	@Override
	public void dragFinished(DragSourceEvent event) {
		System.out.println("Finshed Drag");
	}
}
