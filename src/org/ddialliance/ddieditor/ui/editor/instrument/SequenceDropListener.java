package org.ddialliance.ddieditor.ui.editor.instrument;

import java.util.Arrays;
import java.util.Collections;

import org.ddialliance.ddieditor.logic.urn.ddi.ReferenceResolution;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.editor.instrument.SequenceEditor.SequenceTableContentProvider;
import org.ddialliance.ddieditor.ui.editor.widgetutil.lightxmlobjectdnd.LightXmlObjectTransfer;
import org.ddialliance.ddieditor.ui.editor.widgetutil.lightxmlobjectdnd.LightXmlObjectTransferVO;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

/**
 * JFace drop listener/ viewer drop adapter for:<br>
 * Sequence control construct references transfered as light XML objects
 */
public class SequenceDropListener extends ViewerDropAdapter {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			SequenceDropListener.class);

	SequenceEditor sequenceEditor;
	SequenceTableContentProvider stcp;

	public SequenceDropListener(SequenceEditor sequenceEditor) {
		super(sequenceEditor.getViewer());
		this.sequenceEditor = sequenceEditor;
		stcp = ((SequenceEditor.SequenceTableContentProvider) ((TableViewer) getViewer())
				.getContentProvider());
	}

	@Override
	public void drop(DropTargetEvent event) {
		super.drop(event);
		// do nothing
	}

	@Override
	public boolean performDrop(Object data) {
		if (data == null) { // guard
			new DDIFtpException("Data is null", new Throwable());
			return false;
		}

		LightXmlObjectTransferVO[] transfers = (LightXmlObjectTransferVO[]) data;
		if (transfers.length < 1) { // guard
			new DDIFtpException("No transfers :- (", new Throwable());
			return false;
		}
		Table table = (Table) ((TableViewer) getViewer()).getControl();

		// logic flow:
		// 1 determine insert position
		// 2 delete
		// 3 add
		// 4 refresh table and table viewer

		// insert position
		int relativePosition = -1;
		if (getCurrentLocation() == LOCATION_BEFORE) {
			relativePosition = -1;
		} else if (getCurrentLocation() == LOCATION_AFTER) {
			relativePosition = 1;
		} else if (getCurrentLocation() == LOCATION_ON) {
			relativePosition = 1;
		} else if (getCurrentLocation() == LOCATION_NONE) {
			return false;
		}
		Object selectedLightXmlObject = getCurrentTarget();
		int insertPosition = -1;
		for (int i = 0; i < table.getItems().length; i++) {
			if (table.getItems()[i].getData().equals(selectedLightXmlObject)) {
				insertPosition = i + relativePosition;
				insertPosition--;
			}
		}
		if (insertPosition < 0) {
			insertPosition = 0;
		}
		if (log.isDebugEnabled()) {
			log.debug("Insert position: " + insertPosition);
		}

		// delete from table
		if (transfers[0].rcpPartId.equals(SequenceEditor.ID)) {
			// delete rcp
			int[] indices = table.getSelectionIndices();
			table.remove(indices);
			table.update();

			// resort indices descending
			Integer[] reverseIndices = new Integer[indices.length];
			for (int i = 0; i < indices.length; i++) {
				reverseIndices[i] = indices[i];
			}
			Arrays.sort(reverseIndices, Collections.reverseOrder());

			// delete xml
			for (int i = 0; i < reverseIndices.length; i++) {
				// delete editor items xml (light xml object)
				stcp.getItems().remove(reverseIndices[i].intValue());

				// delete model xml (reference xml)
				sequenceEditor.modelImpl.getDocument().getSequence()
						.getControlConstructReferenceList()
						.remove(reverseIndices[i].intValue());
			}

			if (log.isDebugEnabled()) {
				StringBuilder info = new StringBuilder();
				for (int i = 0; i < reverseIndices.length; i++) {
					info.append(reverseIndices[i]);
					info.append(", ");
				}
				log.debug("Deleted: " + info.toString());
			}
		}

		// add
		for (int i = 0; i < transfers.length; i++) {
			// add rcp table
			TableItem item = new TableItem(table, SWT.NONE, insertPosition);
			item.setText(new String[] {
					sequenceEditor.tableLabelProvider.getColumnText(
							transfers[i].lightXmlObject, 0),
					sequenceEditor.tableLabelProvider.getColumnText(
							transfers[i].lightXmlObject, 1),
					sequenceEditor.tableLabelProvider.getColumnText(
							transfers[i].lightXmlObject, 2) });
			item.setData(transfers[i].lightXmlObject);

			// add editor items xml (light xml object)
			stcp.getItems().add(insertPosition, transfers[i].lightXmlObject);

			// add model xml (reference xml)
			sequenceEditor.modelImpl
					.getDocument()
					.getSequence()
					.getControlConstructReferenceList()
					.add(insertPosition,
							new ReferenceResolution(transfers[i].lightXmlObject)
									.getReference());
		}

		// refresh table
		if (transfers.length > 0) {
			if (log.isDebugEnabled()) {
				log.debug("Table updated, refs: "
						+ sequenceEditor.modelImpl.getDocument().getSequence()
								.getControlConstructReferenceList().size()
						+ ", table: " + table.getItemCount()
						+ ", contentProvider: " + stcp.getItems().size());
			}

			stcp.inputChanged(getViewer(), null, stcp.getItems());
			((TableViewer) getViewer()).refresh(true);
			sequenceEditor.editorStatus.setChanged();

		}
		return true;
	}

	@Override
	public boolean validateDrop(Object target, int operation,
			TransferData transferType) {
		boolean result = LightXmlObjectTransfer.getInstance().isSupportedType(
				transferType)
				&& target instanceof LightXmlObjectType;
		if (!result) {
			log.warn("Not validating, target: " + target + ", transferType: "
					+ transferType.type);
		}
		return result;
	}

	@Override
	public void dragEnter(DropTargetEvent event) {
		super.dragEnter(event);
		// do nothing
	}

	@Override
	public void dragOver(DropTargetEvent event) {
		super.dragOver(event);
		// do nothing
	}
}
