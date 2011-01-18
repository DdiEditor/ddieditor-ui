package org.ddialliance.ddieditor.ui.view.variable.questionrelation;

import java.util.Iterator;

import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.editor.widgetutil.lightxmlobjectdnd.LightXmlObjectTransfer;
import org.ddialliance.ddieditor.ui.editor.widgetutil.lightxmlobjectdnd.LightXmlObjectTransferVO;
import org.ddialliance.ddieditor.ui.view.XmlObjectComparer;
import org.ddialliance.ddieditor.ui.view.variable.questionrelation.VariableQuestionView.FreeQuestionTableContentProvider;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;

public class VariableQuestionRelationDropListener extends ViewerDropAdapter {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			VariableQuestionRelationDropListener.class);
	TableViewer otherTabelViewer;

	public VariableQuestionRelationDropListener(TableViewer tabelViewer,
			TableViewer otherTabelViewer) {
		super(tabelViewer);
		this.otherTabelViewer = otherTabelViewer;
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

		// logic flow:
		// 1 add quei to queivarrel
		// 2 remove transfered quei from free quei
		// 3 add prev quei to free quei
		// 4 refresh

		VariableQuestionRelation target = (VariableQuestionRelation) getCurrentTarget();
		LightXmlObjectType prevQuei = target.quei;

		// add quei to queivarrel
		target.quei = transfers[0].lightXmlObject;

		// remove transfered quei from free quei
		otherTabelViewer.remove(target.quei);
		XmlObjectComparer xmlObjectComparer = new XmlObjectComparer();
		for (Iterator iterator = ((FreeQuestionTableContentProvider) otherTabelViewer
				.getContentProvider()).getItems().iterator(); iterator
				.hasNext();) {
			LightXmlObjectType lightXmlObject = (LightXmlObjectType) iterator
					.next();
			if (xmlObjectComparer.equals(target.quei, lightXmlObject)) {
				iterator.remove();
				break;
			}
		}

		// add prev quei to free quei
		if (prevQuei != null) {
			
			((FreeQuestionTableContentProvider) otherTabelViewer
					.getContentProvider()).getItems().add(prevQuei);
			otherTabelViewer.add(prevQuei);
		}

		// refresh
		if (transfers.length > 0) {
			((TableViewer) getViewer()).refresh(true);
			otherTabelViewer.refresh(true);
		}
		return true;
	}

	@Override
	public boolean validateDrop(Object target, int operation,
			TransferData transferType) {
		boolean result = false;
		if (getCurrentLocation() == LOCATION_ON) {
			result = LightXmlObjectTransfer.getInstance().isSupportedType(
					transferType)
					&& target instanceof VariableQuestionRelation;
		}
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
