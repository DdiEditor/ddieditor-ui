package org.ddialliance.ddieditor.ui.editor.widgetutil.lightxmlobjectdnd;

import java.util.Iterator;

import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class LightXmlObjectDragListener implements DragSourceListener {
	private final Viewer viewer;
	String rcpPartId;

	public LightXmlObjectDragListener(Viewer viewer, String rcpPartId) {
		super();
		this.viewer = viewer;
		this.rcpPartId = rcpPartId;
	}

	@Override
	public void dragStart(DragSourceEvent event) {
		// do nothing
	}

	@Override
	public void dragSetData(DragSourceEvent event) {
		IStructuredSelection selection = (IStructuredSelection) viewer
				.getSelection();

		// set data on event
		int[] indices = null;
		if (LightXmlObjectTransfer.getInstance()
				.isSupportedType(event.dataType)) {
			Object control = ((DragSource) event.getSource()).getControl();

			// indices
			// table
			if (control instanceof Table) {
				Table table = ((Table) control);
				indices = table.getSelectionIndices();
			}
			// tree
			else if (control instanceof Tree) {
				Tree tree = ((Tree) control);
				TreeItem[] items = tree.getSelection();
				indices = new int[items.length];
				for (int i = 0; i < tree.getItems().length; i++) {
					if (tree.getItems()[i].getItemCount() > 0) {
						// selection index is relative to a parent treeItem add
						// parent level to be use full
						indices[i] = -1;
						// defineIndexOfSelectedTreeItems(items, indices,
						// tree.getItems()[i]);
					}
				}
				tree.getSelection();
			}
			// guard
			else {
				new DDIFtpException("Event source not surpported: "
						+ control.getClass().getName(), new Throwable());
				return;
			}

			// set event data
			int count = 0;
			LightXmlObjectTransferVO[] result = new LightXmlObjectTransferVO[indices.length];
			for (Iterator iterator = selection.iterator(); iterator.hasNext(); count++) {
				result[count] = new LightXmlObjectTransferVO(rcpPartId,
						indices[count], (LightXmlObjectType) iterator.next());
			}
			event.data = result;
		}
	}

	@Override
	public void dragFinished(DragSourceEvent event) {
		// do nothing
	}

	private void defineIndexOfSelectedTreeItems(TreeItem[] selectedItems,
			int[] indices, TreeItem treeItem) {
		for (int i = 0; i < treeItem.getItemCount(); i++) {
			if (treeItem.getItems()[i].getItemCount() > 0) {
				defineIndexOfSelectedTreeItems(selectedItems, indices,
						treeItem.getItems()[i]);
			}
			for (int j = 0; j < selectedItems.length; j++) {
				System.out.println(selectedItems[j] + " ~ "
						+ treeItem.getItems()[i]);
				if (selectedItems[j].equals(treeItem.getItems()[i])) {
					indices[j] = i;
				}
			}
		}
	}
}
