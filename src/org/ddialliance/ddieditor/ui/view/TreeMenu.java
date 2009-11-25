package org.ddialliance.ddieditor.ui.view;

import java.util.Properties;

import org.ddialliance.ddieditor.model.conceptual.ConceptualElement;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.editor.EditorInput;
import org.ddialliance.ddieditor.ui.editor.EditorInput.EditorModeType;
import org.ddialliance.ddieditor.ui.editor.code.CodeSchemeEditor;
import org.ddialliance.ddieditor.ui.editor.concept.ConceptEditor;
import org.ddialliance.ddieditor.ui.editor.concept.ConceptSchemeEditor;
import org.ddialliance.ddieditor.ui.editor.instrument.InstrumentEditor;
import org.ddialliance.ddieditor.ui.editor.instrument.StatementItemEditor;
import org.ddialliance.ddieditor.ui.editor.question.QuestionItemEditor;
import org.ddialliance.ddieditor.ui.editor.question.QuestionSchemeEditor;
import org.ddialliance.ddieditor.ui.editor.study.StudyUnitEditor;
import org.ddialliance.ddieditor.ui.model.ElementType;
import org.ddialliance.ddieditor.ui.util.MessageUtil;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class TreeMenu {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			TreeMenuProvider.class);

	public static enum NEW_TYPE {
		SCHEME, ITEM
	};

	static public void openEditor(TreeViewer treeViewer, View currentView,
			Properties properties, EditorModeType mode) {
		LightXmlObjectType item = null;

		ISelection selection = treeViewer.getSelection();
		Object obj = ((IStructuredSelection) selection).getFirstElement();
		if (obj instanceof ConceptualElement) {
			ConceptualElement conceptElement = (ConceptualElement) obj;
			item = conceptElement.getValue();
		} else if (obj instanceof LightXmlObjectType) {
			item = (LightXmlObjectType) obj;
		}

		ElementType entityType = getSelectedEntityType(currentView, item);
		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		EditorInput input = new EditorInput(item.getId(), item.getVersion(),
				item.getParentId(), item.getParentVersion(), entityType, mode,
				currentView, properties);
		try {
			switch (entityType) {
			case FILE:
				MessageUtil.currentNotSupported(currentView.getSite()
						.getShell());
				break;
			case STUDY_UNIT:
				page.openEditor(input, StudyUnitEditor.ID);
				break;
			case CONCEPT_SCHEME:
				page.openEditor(input, ConceptSchemeEditor.ID);
				break;
			case CONCEPT:
				page.openEditor(input, ConceptEditor.ID);
				break;
			case CODE_SCHEME:
				page.openEditor(input, CodeSchemeEditor.ID);
				break;
			case QUESTION_SCHEME:
				page.openEditor(input, QuestionSchemeEditor.ID);
				break;
			case QUESTION_ITEM:
				page.openEditor(input, QuestionItemEditor.ID);
				break;
			case INSTRUMENT:
				page.openEditor(input, InstrumentEditor.ID);
				break;
			case QUESTION_CONSTRUCT:
				page.openEditor(input, null);
				break;
			case STATEMENT_ITEM:
				page.openEditor(input, StatementItemEditor.ID);
				break;
			default:
				// TODO error handling
				log.error("Editor Type not supported: " + entityType);
				System.exit(0);
				break;
			}

			// Notify any listeners of the view with the actual data of the view
			treeViewer.setSelection(treeViewer.getSelection());
		} catch (PartInitException ex) {
			MessageDialog
					.openError(
							currentView.getSite().getShell(),
							Messages.getString("ErrorTitle"),
							Messages.getString("View.mess.EditorOpenError") + "\n" + ex.getMessage()); //$NON-NLS-1$
		}
	}

	static public void newItem(TreeViewer treeViewer, View currentView,
			Properties properties, NEW_TYPE newType, String parentId,
			String parentVersion) {
		String editorID = null;
		ElementType newEntityType = null;
		LightXmlObjectType item = null;

		ISelection selection = treeViewer.getSelection();
		Object obj = ((IStructuredSelection) selection).getFirstElement();
		if (obj instanceof ConceptualElement) {
			ConceptualElement conceptElement = (ConceptualElement) obj;
			item = conceptElement.getValue();
		} else if (obj instanceof LightXmlObjectType) {
			item = (LightXmlObjectType) obj;
		}

		ElementType entityType = getSelectedEntityType(currentView, item);

		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		try {
			switch (entityType) {
			case FILE:
				MessageUtil.currentNotSupported(currentView.getSite()
						.getShell());
				break;
			case STUDY_UNIT:
				editorID = StudyUnitEditor.ID;
				newEntityType = ElementType.STUDY_UNIT;
				break;
			case CONCEPT_SCHEME:
				if (newType.equals(NEW_TYPE.SCHEME)) {
					editorID = ConceptSchemeEditor.ID;
					newEntityType = ElementType.CONCEPT_SCHEME;
				} else {
					editorID = ConceptEditor.ID;
					newEntityType = ElementType.CONCEPT;
				}
				break;
			case CONCEPT:
				editorID = ConceptEditor.ID;
				newEntityType = ElementType.CONCEPT;
				break;
			case CODE_SCHEME:
				if (newType.equals(NEW_TYPE.SCHEME)) {
					editorID = CodeSchemeEditor.ID;
					newEntityType = ElementType.CODE_SCHEME;
				} else {
					// TODO Implement Code Editor
					System.out
							.println("************** Code Editor not supported *******************");
					// editorID = CodeEditor.ID;
					// newEntityType = EditorInput.ENTITY_TYPE.CODE;
				}
				break;
			case QUESTION_SCHEME:
				if (newType.equals(NEW_TYPE.SCHEME)) {
					editorID = QuestionSchemeEditor.ID;
					newEntityType = ElementType.QUESTION_SCHEME;
				} else {
					editorID = QuestionItemEditor.ID;
					newEntityType = ElementType.QUESTION_ITEM;
				}
				break;
			case QUESTION_ITEM:
				editorID = QuestionItemEditor.ID;
				newEntityType = ElementType.QUESTION_ITEM;
				break;
			case INSTRUMENT:
				editorID = InstrumentEditor.ID;
				newEntityType = ElementType.INSTRUMENT;
				break;
			default:
				System.err.println("Entity Type not supported: "
						+ entityType);
				System.exit(0);
				break;
			}
			EditorInput input = new EditorInput(null, null, parentId,
					parentVersion, newEntityType, EditorModeType.NEW,
					currentView, properties);
			page.openEditor(input, editorID);

			// Notify any listeners of the view with the actual data of the view
			treeViewer.setSelection(treeViewer.getSelection());
		} catch (PartInitException ex) {
			MessageDialog
					.openError(
							currentView.getSite().getShell(),
							Messages.getString("ErrorTitle"),
							Messages.getString("View.mess.EditorOpenError") + "\n" + ex.getMessage()); //$NON-NLS-1$
		}
	}

	public static ElementType getSelectedEntityType(View currentView,
			LightXmlObjectType item) {
		try {
			return ElementType.getElementType(item.getElement());
		} catch (DDIFtpException e) {
			MessageDialog.openError(currentView.getSite().getShell(), Messages
					.getString("ErrorTitle"), e.getMessage());
			// TODO dig into error dialogs
			// ErrorDialog.openError(currentView.getSite().getShell(), Messages
			// .getString("ErrorTitle"), null, new Status(IStatus.ERROR,
			// null, 0, e.getMessage(), e));
		}
		return null;
	}
}
