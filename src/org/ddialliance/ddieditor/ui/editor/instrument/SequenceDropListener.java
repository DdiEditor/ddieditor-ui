package org.ddialliance.ddieditor.ui.editor.instrument;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.xmlbeans.XmlObject;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.ReferenceType;
import org.ddialliance.ddieditor.logic.urn.ddi.ReferenceResolution;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.editor.Editor.EditorIdentification;
import org.ddialliance.ddieditor.ui.editor.instrument.SequenceEditor.SequenceTableContentProvider;
import org.ddialliance.ddieditor.ui.editor.widgetutil.LightXmlObjectTransfer;
import org.ddialliance.ddieditor.ui.editor.widgetutil.LightXmlObjectTransferVO;
import org.ddialliance.ddieditor.ui.model.instrument.Sequence;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;

/**
 * JFace drop listener/ viewer drop adapter for:<br>
 * Sequence control construct references transfered as light XML objects
 */
public class SequenceDropListener extends ViewerDropAdapter {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			SequenceDropListener.class);

	SequenceEditor sequenceEditor;
	SequenceTableContentProvider stcp;
	Object drop;

	public SequenceDropListener(SequenceEditor sequenceEditor) {
		super(sequenceEditor.getViewer());
		this.sequenceEditor = sequenceEditor;
		stcp = ((SequenceEditor.SequenceTableContentProvider) ((TableViewer) getViewer())
				.getContentProvider());
	}

	@Override
	public void drop(DropTargetEvent event) {
		super.drop(event);
		// log.debug(event);
	}

	int count = 0;
	int insertPosition = -1;
	boolean update = false;

	@Override
	public boolean performDrop(Object data) {
		if (data == null) { // guard
			new DDIFtpException("Data is null", new Throwable());
		}

		LightXmlObjectTransferVO lXmlObjectTransferVO = (LightXmlObjectTransferVO) data;
		update = false;
		count = 0; // count is before
		insertPosition = -1;

		// define xml location and insert
		Object selectedLightXmlObject = getCurrentTarget();
		for (Iterator<LightXmlObjectType> iterator = stcp.getItems().iterator(); iterator
				.hasNext(); count++) {
			LightXmlObjectType lightXmlObject = iterator.next();

			if (lightXmlObject.equals(selectedLightXmlObject)) {
				// define relative location
				int relativePosition = 4;
				if (getCurrentLocation() == LOCATION_BEFORE) {
					relativePosition = -1;
				} else if (getCurrentLocation() == LOCATION_AFTER) {
					relativePosition = 1;
				} else if (getCurrentLocation() == LOCATION_ON) {
					relativePosition = 1;
				} else if (getCurrentLocation() == LOCATION_NONE) {
					return false;
				}
				log.debug(getCurrentLocation() + " " + relativePosition);

				// update
				update = true;
				insertPosition = relativePosition == 0 ? count + 1 : count;
				if (insertPosition < 0) {
					insertPosition = 0;
				}
				break;
			}
		}

		// clean and refresh table
		if (update) {
			int from = 0;
			boolean found = false;
			for (int i = 0; i < stcp.getItems().size(); i++) {
				if (lXmlObjectTransferVO.lightXmlObject
						.valueEquals((XmlObject) stcp.getItems().get(i))) {
					found = true;
					from = i;
					// rcp
					log.debug("Items: " + stcp.getItems().remove(i));
					ReferenceType reference = new ReferenceResolution(
							(LightXmlObjectType) data).getReference();

					// xml
					for (ReferenceType controlConstructRef : sequenceEditor.modelImpl
							.getDocument().getSequence()
							.getControlConstructReferenceList()) {
						if (reference.valueEquals(controlConstructRef)) {
							sequenceEditor.modelImpl.getDocument()
									.getSequence()
									.getControlConstructReferenceList()
									.remove(controlConstructRef);
							break;
						}
					}
					break;
				}
			}

			// clean up
			// selectedLightXmlObject, count, insertPosition
			if (insertPosition < count) {
				count++;
			}

			// define operation
			switch (getCurrentOperation()) {
			case DND.DROP_COPY:
				// no clean up
				log.debug("DND.DROP_COPY");
				break;
			case DND.DROP_MOVE:
				log.debug("DND.DROP_MOVE");

				// rcp
				try {
					stcp.getItems().remove(lXmlObjectTransferVO.lightXmlObject);

					// stcp.getItems().remove(count);
					// ((TableViewer)
					// getViewer()).remove(selectedLightXmlObject);
					// ((SequenceEditor.) getViewer()).
					// Object[] test =
					// ((SequenceEditor.SequenceTableContentProvider)
					// ((TableViewer) getViewer()).
					// .getContentProvider()).

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case DND.DROP_LINK:
				// no clean up
				break;
			case DND.DROP_NONE:
				// no clean up
				break;
			default:
				break;
			}

			// insert
			// add xm
			sequenceEditor.modelImpl
					.getDocument()
					.getSequence()
					.getControlConstructReferenceList()
					.add(insertPosition,
							new ReferenceResolution((LightXmlObjectType) data)
									.getReference());

			// add rcp
			stcp.getItems().add(insertPosition, (LightXmlObjectType) data);

			// refresh table
			stcp.inputChanged(getViewer(), null, stcp.getItems());
			((TableViewer) getViewer()).refresh(true);
			sequenceEditor.editorStatus.setChanged();
		}

		return true;
	}

	private void cleanUp() {

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
	}

	@Override
	public void dragOver(DropTargetEvent event) {
		super.dragOver(event);
	}
}
