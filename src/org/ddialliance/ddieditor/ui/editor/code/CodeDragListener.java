package org.ddialliance.ddieditor.ui.editor.code;

import org.ddialliance.ddieditor.ui.editor.widgetutil.lightxmlobjectdnd.LightXmlObjectDragListener;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;

public class CodeDragListener  implements DragSourceListener {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, LightXmlObjectDragListener.class);
	private final Viewer viewer;
	String rcpPartId;

	public CodeDragListener(Viewer viewer, String rcpPartId) {
		super();
		this.viewer = viewer;
		this.rcpPartId = rcpPartId;
	}

	@Override
	public void dragStart(DragSourceEvent event) {
		// do nothing
		event.doit = true;
	}

	@Override
	public void dragSetData(DragSourceEvent event) {
		IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();

		event.data = selection.getFirstElement();
	}

	@Override
	public void dragFinished(DragSourceEvent event) {
		// do nothing
	}
}
