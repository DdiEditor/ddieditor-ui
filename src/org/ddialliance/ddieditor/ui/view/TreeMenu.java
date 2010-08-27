package org.ddialliance.ddieditor.ui.view;

import java.util.List;

import org.ddialliance.ddieditor.model.conceptual.ConceptualElement;
import org.ddialliance.ddieditor.model.conceptual.ConceptualType;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.model.resource.DDIResourceType;
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
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

/**
 * Tree helper, opens perspectives and editors based on the current selected
 * element in the tree
 */
public class TreeMenu {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			TreeMenuProvider.class);

	public void openPerspective(TreeViewer treeViewer, View currentView) {
		InputSelection inputSelection = defineSelection(treeViewer,
				currentView.ID);
		Object obj = inputSelection.getSelection();
		if (!(obj instanceof LightXmlObjectType)) {
			return;
		}
		LightXmlObjectType lightXmlObject = (LightXmlObjectType) obj;
		String elementName = lightXmlObject.getElement();
		try {
			String perspectiveId = ElementType.getPerspectiveId(elementName);
			if (perspectiveId.equals("")) {
				return;
			}

			// open perspective
			PlatformUI.getWorkbench().showPerspective(
					perspectiveId,
					PlatformUI.getWorkbench().getActiveWorkbenchWindow()
							.getActivePage().getWorkbenchWindow());

		} catch (DDIFtpException e) {
			DialogUtil.errorDialog(currentView.getSite().getShell(),
					currentView.ID, "Error", e.getMessage(), e);
		} catch (WorkbenchException e) {
			DDIFtpException ex = new DDIFtpException(e.getMessage(), e);
			DialogUtil.errorDialog(currentView.getSite().getShell(),
					currentView.ID, "Error", ex.getMessage(), ex);
		}
	}

	public void openEditor(TreeViewer treeViewer, View currentView,
			EditorModeType mode, ElementType entityType) {
		InputSelection inputSelection = defineSelection(treeViewer,
				currentView.ID);

		// define editor input
		EditorInput input = null;

		// ddi resource type
		if (inputSelection.getSelection() instanceof DDIResourceType) {
			entityType = ElementType.FILE;
			input = new EditorInput(inputSelection.getResourceId(),
					((DDIResourceType) inputSelection.getSelection())
							.getOrgName(), null, null, null, entityType, mode);
		}

		// light xml object
		if (inputSelection.getSelection() instanceof LightXmlObjectType) {
			LightXmlObjectType lightXmlObject = (LightXmlObjectType) inputSelection
					.getSelection();

			// guard
			if (entityType == null) {
				try {
					entityType = ElementType.getElementType(lightXmlObject
							.getElement());
				} catch (DDIFtpException e) {
					DialogUtil.errorDialog(currentView.getSite().getShell(),
							currentView.ID, null, e.getMessage(), e);
					return;
				}
			}

			String parentId = "";
			String parentVersion = "";
			ElementType selectEntityType = null;
			try {
				selectEntityType = ElementType.getElementType(lightXmlObject
						.getElement());
			} catch (DDIFtpException e) {
				DialogUtil.errorDialog(currentView.getSite().getShell(),
						currentView.ID, null, e.getMessage(), e);
				return;
			}
			if (entityType.equals(selectEntityType)) {
				parentId = lightXmlObject.getParentId();
				parentVersion = lightXmlObject.getParentVersion();
			} else {
				parentId = lightXmlObject.getId();
				parentVersion = lightXmlObject.getVersion();
			}
			input = new EditorInput(inputSelection.getResourceId(),
					lightXmlObject.getId(), lightXmlObject.getVersion(),
					parentId, parentVersion, entityType, mode);
		}

		if (log.isDebugEnabled()) {
			log.debug("EditorMode:" + mode + ", elementType: " + entityType);
		}

		// open editor
		if (input == null) {
			DDIFtpException e = new DDIFtpException(
					"editor.editelement.notimplemented",
					new Object[] { inputSelection.getSelection().getClass()
							.getName() }, new Throwable());
			DialogUtil.errorDialog(currentView.getSite().getShell(),
					currentView.ID, "Error", e.getMessage(), e);
			return;
		}

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
					currentView.ID, "Error", e.getMessage() + input, e);
			return;
		}

		// notify any listeners of the view with the actual data of the view
		treeViewer.setSelection(treeViewer.getSelection());
	}

	public InputSelection defineSelection(TreeViewer treeViewer, String ID) {
		TreeSelection selection = (TreeSelection) treeViewer.getSelection();
		InputSelection inputSelection = new InputSelection();
		// resource id
		if (selection.getPaths()[0].getFirstSegment() instanceof DDIResourceType) {
			inputSelection.setResourceId(((DDIResourceType) selection
					.getPaths()[0].getFirstSegment()).getOrgName());
		}

		// selection obj
		Object obj = null;
		try {
			obj = ((IStructuredSelection) selection).getFirstElement();
		} catch (Exception e) {
			DialogUtil.errorDialog(treeViewer.getTree().getShell(), ID,
					"Error", e.getMessage(), e);
		}

		// light xml object resolvement
		LightXmlObjectType lightXmlObject = null;
		if (obj instanceof LightXmlObjectType) {
			lightXmlObject = (LightXmlObjectType) obj;
		}
		// maintainable light label query result
		else if (obj instanceof MaintainableLightLabelQueryResult) {
			MaintainableLightLabelQueryResult result = (MaintainableLightLabelQueryResult) obj;
			lightXmlObject = LightXmlObjectType.Factory.newInstance();
			lightXmlObject.setElement(result.getMaintainableTarget());
			lightXmlObject.setId(result.getId());
			lightXmlObject.setVersion(result.getVersion());
		}
		// list
		else if (obj instanceof List) {
			List list = ((List) obj);
			if (!list.isEmpty()) {
				if (list.get(0) instanceof LightXmlObjectType) {
					lightXmlObject = (LightXmlObjectType) list.get(0);
				}
			}
		}
		// conceptual element
		else if (obj instanceof ConceptualElement) {
			ConceptualElement result = (ConceptualElement) obj;
			lightXmlObject = (LightXmlObjectType) result.getValue();
		} else if (obj instanceof ConceptualType) {
			// mapping between ddieditor.model
			// and ddieditor-ui.model aka perspective
			ConceptualType conTypeObj = (ConceptualType) obj;
			lightXmlObject = LightXmlObjectType.Factory.newInstance();
			if (conTypeObj.equals(ConceptualType.STUDY)) {
				lightXmlObject.setElement(ElementType.STUDY_UNIT
						.getElementName());
			} else if (conTypeObj.equals(ConceptualType.LOGIC_Universe)) {
				lightXmlObject.setElement(ElementType.UNIVERSE_SCHEME
						.getElementName());
			} else if (conTypeObj.equals(ConceptualType.LOGIC_concepts)) {
				lightXmlObject.setElement(ElementType.CONCEPT_SCHEME
						.getElementName());
			} else if (conTypeObj.equals(ConceptualType.LOGIC_category)) {
				lightXmlObject.setElement(ElementType.CATEGORY_SCHEME
						.getElementName());
			} else if (conTypeObj.equals(ConceptualType.LOGIC_code)) {
				lightXmlObject.setElement(ElementType.CODE_SCHEME
						.getElementName());
			} else if (conTypeObj.equals(ConceptualType.LOGIC_questions)) {
				lightXmlObject.setElement(ElementType.QUESTION_SCHEME
						.getElementName());
			} else if (conTypeObj.equals(ConceptualType.LOGIC_instumentation)) {
				lightXmlObject.setElement(ElementType.INSTRUMENT
						.getElementName());
			}
		}
		if (lightXmlObject != null) {
			inputSelection.setSelection(lightXmlObject);
		}

		// ddi resource type DDIResourceTypeImpl
		if (obj instanceof DDIResourceType) {
			inputSelection.setSelection(obj);
		}

		// log values
		if (log.isDebugEnabled()) {
			log.debug("Top selection: class: "
					+ selection.getPaths()[0].getFirstSegment().getClass()
							.getName() + ", value: "
					+ selection.getPaths()[0].getFirstSegment());
			log.debug(inputSelection);
		}

		// not recognized!
		if (inputSelection.getSelection() == null) {
			DDIFtpException e = new DDIFtpException("Not recognized: "
					+ obj.getClass() + " , value: " + obj, new Throwable());
			DialogUtil.errorDialog(treeViewer.getTree().getShell(), ID,
					"Error", e.getMessage(), e);
			return null;
		}
		return inputSelection;
	}
}
