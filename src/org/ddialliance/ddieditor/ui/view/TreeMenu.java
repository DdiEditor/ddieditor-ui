package org.ddialliance.ddieditor.ui.view;

import java.util.List;

import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.persistenceaccess.maintainablelabel.MaintainableLightLabelQueryResult;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddieditor.ui.editor.EditorInput;
import org.ddialliance.ddieditor.ui.editor.EditorInput.EditorModeType;
import org.ddialliance.ddieditor.ui.model.ElementType;
import org.ddialliance.ddieditor.ui.util.DialogUtil;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class TreeMenu {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			TreeMenuProvider.class);

	public void openEditor(TreeViewer treeViewer, View currentView,
			EditorModeType mode, ElementType entityType) {
		LightXmlObjectType lightXmlObject = defineSelection(treeViewer,
				currentView.ID);

		// legacy code to check up on!!
		// case FILE:
		// MessageUtil.currentNotSupported(currentView.getSite().getShell());
		// break;

		// guard
		if (entityType == null) {
			try {
				entityType = ElementType.getElementType(lightXmlObject
						.getElement());
			} catch (DDIFtpException e) {
				DialogUtil.errorDialog(currentView.getSite().getShell(),
						currentView.ID, null, e.getMessage(), e);
			}
		}

		if (log.isDebugEnabled()) {
			log.debug("EditorMode:" + mode + ", elementType: " + entityType
					+ ", xmlLightObject: " + lightXmlObject);
		}

		// open editor
		EditorInput input = new EditorInput(lightXmlObject.getId(),
				lightXmlObject.getVersion(), lightXmlObject.getParentId(),
				lightXmlObject.getParentVersion(), entityType, mode);

		try {
			Editor editor = (Editor) PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage().openEditor(
							input, entityType.getEditorId());
			
			// add update on save listener
			editor.addPropertyListener(currentView);

			// set editor as dirty on new
			if (mode.equals(EditorModeType.NEW)) {
				editor.editorStatus.setChanged();
			}
		} catch (PartInitException e) {
			DialogUtil.errorDialog(currentView.getSite().getShell(),
					currentView.ID, "Error", e.getMessage(), e);
		}

		// notify any listeners of the view with the actual data of the view
		treeViewer.setSelection(treeViewer.getSelection());
	}

	public LightXmlObjectType defineSelection(TreeViewer treeViewer, String ID) {
		ISelection selection = treeViewer.getSelection();
		Object obj = null;
		try {
			obj = ((IStructuredSelection) selection).getFirstElement();
		} catch (Exception e) {
			DialogUtil.errorDialog(treeViewer.getTree().getShell(), ID,
					"Error", e.getMessage(), e);
		}

		LightXmlObjectType lightXmlObject = null;
		if (obj instanceof LightXmlObjectType) {
			lightXmlObject = (LightXmlObjectType) obj;
		} else if (obj instanceof MaintainableLightLabelQueryResult) {
			MaintainableLightLabelQueryResult result = (MaintainableLightLabelQueryResult) obj;
			lightXmlObject = LightXmlObjectType.Factory.newInstance();
			lightXmlObject.setElement(result.getMaintainableTarget());
			lightXmlObject.setId(result.getId());
			lightXmlObject.setVersion(result.getVersion());
			lightXmlObject.setParentId(result.getParentId());
			lightXmlObject.setParentVersion(result.getParentVersion());
		} else if (obj instanceof List) {
			List list = ((List) obj);
			if (!list.isEmpty()) {
				if (list.get(0) instanceof LightXmlObjectType) {
					lightXmlObject = (LightXmlObjectType) list.get(0);
				}
			}
		} else {
			DDIFtpException e = new DDIFtpException("Not recognized: "
					+ obj.getClass() + " , value: " + obj, new Throwable());
			// TODO error dialog for view
		}
		return lightXmlObject;
	}
}
