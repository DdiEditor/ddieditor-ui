package org.ddialliance.ddieditor.ui.view.variable.questionrelation;

import java.util.Iterator;

import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.editor.widgetutil.lightxmlobjectdnd.LightXmlObjectTransfer;
import org.ddialliance.ddieditor.ui.editor.widgetutil.lightxmlobjectdnd.LightXmlObjectTransferVO;
import org.ddialliance.ddieditor.ui.view.XmlObjectComparer;
import org.ddialliance.ddieditor.ui.view.variable.questionrelation.VariableQuestionView.FreeQuestionTableContentProvider;
import org.ddialliance.ddieditor.ui.view.variable.questionrelation.VariableQuestionView.QuestionVariableContentProvider;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;

public class VariableQuestionFreeDropListener extends ViewerDropAdapter {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			VariableQuestionRelationDropListener.class);
	TableViewer otherTabelViewer;

	public VariableQuestionFreeDropListener(TableViewer tabelViewer,
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

		LightXmlObjectType target = (LightXmlObjectType) getCurrentTarget();
		VariableQuestionRelationTransferVO[] transfers = (VariableQuestionRelationTransferVO[]) data;
		if (transfers.length < 1) { // guard
			new DDIFtpException("No transfers :- (", new Throwable());
			return false;
		}

		// logic flow:
		// 1 add quei to free quei
		// 2 remove quei form queivarrel
		// 3 refresh

		
		if (transfers[0].variQueiRelation.quei != null) {
			// add quei to free quei
			((FreeQuestionTableContentProvider) ((TableViewer) getViewer())
					.getContentProvider()).getItems().add(
					transfers[0].variQueiRelation.quei);
			((TableViewer) getViewer()).add(transfers[0].variQueiRelation.quei);
			
			// remove quei form queivarrel
			XmlObjectComparer xmlObjectComparer = new XmlObjectComparer();			
			for (Iterator iterator = ((QuestionVariableContentProvider) otherTabelViewer
					.getContentProvider()).getItems().iterator(); iterator
					.hasNext();) {
				VariableQuestionRelation variQueiRel = (VariableQuestionRelation) iterator
						.next();
				if (xmlObjectComparer.equals(transfers[0].variQueiRelation.quei, variQueiRel.quei)) {
					variQueiRel.quei = null;
					break;
				}
			}
			
			// refresh
			if (transfers.length > 0) {
				((TableViewer) getViewer()).refresh(true);
				otherTabelViewer.refresh(true);
			}
		}		
		return true;
	}

	@Override
	public boolean validateDrop(Object target, int operation,
			TransferData transferType) {
		boolean result = false;
		result = VariableQuestionRelationTransfer.getInstance()
				.isSupportedType(transferType);
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
