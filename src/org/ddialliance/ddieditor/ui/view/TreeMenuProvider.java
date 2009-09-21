package org.ddialliance.ddieditor.ui.view;


/**
 * Tree Menu Provider.
 * 
 */
/*
 * $Author$ 
 * $Date$ 
 * $Revision$
 */

import java.text.MessageFormat;
import java.util.List;
import java.util.Properties;

import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.Activator;
import org.ddialliance.ddieditor.ui.dbxml.CodeSchemes;
import org.ddialliance.ddieditor.ui.dbxml.ConceptSchemes;
import org.ddialliance.ddieditor.ui.dbxml.Concepts;
import org.ddialliance.ddieditor.ui.dbxml.QuestionItems;
import org.ddialliance.ddieditor.ui.dbxml.QuestionSchemes;
import org.ddialliance.ddieditor.ui.dbxml.Util;
import org.ddialliance.ddieditor.ui.editor.EditorInput;
import org.ddialliance.ddieditor.ui.editor.EditorInput.EDITOR_MODE_TYPE;
import org.ddialliance.ddieditor.ui.editor.EditorInput.ENTITY_TYPE;
import org.ddialliance.ddieditor.ui.editor.code.CodeSchemeEditor;
import org.ddialliance.ddieditor.ui.editor.concept.ConceptEditor;
import org.ddialliance.ddieditor.ui.editor.concept.ConceptSchemeEditor;
import org.ddialliance.ddieditor.ui.editor.question.QuestionItemEditor;
import org.ddialliance.ddieditor.ui.editor.question.QuestionSchemeEditor;
import org.ddialliance.ddieditor.ui.util.Entity;
import org.ddialliance.ddieditor.ui.util.ResourceManager;
import org.ddialliance.ddieditor.ui.view.InfoFileView.EDITOR_TYPE;
import org.ddialliance.ddieditor.ui.view.View.ViewContentType;
import org.ddialliance.ddieditor.ui.util.MessageUtil;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ArmEvent;
import org.eclipse.swt.events.ArmListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class TreeMenuProvider extends TreeMenu {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, TreeMenuProvider.class);

	final TreeViewer treeViewer;
	final View currentView;
	final String entityName;
	final String rootElementName;
	final Properties properties;
	final Menu menu;
	// EditorInput.ENTITY_TYPE entityType;
	MenuItem editMenuItem = null;
	private List<String> newMenuLabelList;
	
//	private static enum NEW_TYPE {SCHEME, ITEM};


	/**
	 * Constructor for TreeMenuProvider
	 * 
	 * @param treeViewer
	 * @param currentView
	 * @param entityName
	 * 			name of root entity e.g Concept Scheme, Concept, etc.
	 * @param rootElementName
	 * 			Element Name of root element e.g. ConceptScheme, QuestionScheme, etc.
	 * @param newMenuLabelList
	 * 			list of NEW label strings.
	 * @param properties
	 */
	public TreeMenuProvider(TreeViewer treeViewer, View currentView, String entityName, String rootElementName, List<String> newMenuLabelList,
			Properties properties) {
		
		this.treeViewer = treeViewer;
		this.currentView = currentView;
		this.entityName = entityName;
		this.rootElementName = rootElementName;
		this.newMenuLabelList = newMenuLabelList;
		this.properties = properties;
		menu = new Menu(treeViewer.getTree());
	}
	
	/**
	 * Return entity name of selected item
	 * 
	 * @return String
	 */
	private String getEntityName(EditorInput.ENTITY_TYPE entityType) {

		switch (entityType) {
		case CONCEPT_SCHEME:
			return Messages.getString("ConceptView.lable.conceptSchemeLabel.ConceptScheme");
		case CONCEPT:
			return Messages.getString("ConceptView.lable.conceptLabel.Concept");
		case CODE_SCHEME:
			return Messages.getString("codeView.lable.codeShemeLabel.CodeScheme");
		case QUESTION_SCHEME:
			return Messages.getString("QuestionItemView.lable.questionSchemeLabel.QuesitionScheme");
		case QUESTION_ITEM:
			return Messages.getString("QuestionItemView.lable.questionItemLabel.Question");
		default:
			// TODO error handling
			log.error("Editor Type not supported: " + entityType);
			System.exit(0);
		}
		return null;
	}

//	private void newItem(NEW_TYPE newType, String parentId, String parentVersion) {
//		String editorID = null;
//		EditorInput.ENTITY_TYPE selectedEntityType = null;
//		EditorInput.ENTITY_TYPE newEntityType = null;
//
//		ISelection selection = treeViewer.getSelection();
//		Object obj = ((IStructuredSelection) selection).getFirstElement();
//		LightXmlObjectType item = (LightXmlObjectType) obj;
//		selectedEntityType = Entity.getEntityType(item);
//		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
//		try {
//			switch (selectedEntityType) {
//			case CONCEPT_SCHEME:
//				if (newType.equals(NEW_TYPE.SCHEME)) {
//					editorID = ConceptSchemeEditor.ID;
//					newEntityType = EditorInput.ENTITY_TYPE.CONCEPT_SCHEME;
//				} else {
//					editorID = ConceptEditor.ID;
//					newEntityType = EditorInput.ENTITY_TYPE.CONCEPT;
//				}
//				break;
//			case CONCEPT:
//				editorID =ConceptEditor.ID;
//				newEntityType = EditorInput.ENTITY_TYPE.CONCEPT;
//				break;
//			case CODE_SCHEME:
//				if (newType.equals(NEW_TYPE.SCHEME)) {
//					editorID = CodeSchemeEditor.ID;
//					newEntityType = EditorInput.ENTITY_TYPE.CODE_SCHEME;
//				} else {
//					// TODO Implement Code Editor
//					System.out.println("************** Code Editor not supported *******************");
////					editorID = CodeEditor.ID;
////					newEntityType = EditorInput.ENTITY_TYPE.CODE;
//				}
//				break;
//			case QUESTION_SCHEME:
//				if (newType.equals(NEW_TYPE.SCHEME)) {
//					editorID = QuestionSchemeEditor.ID;
//					newEntityType = EditorInput.ENTITY_TYPE.QUESTION_SCHEME;
//				} else {
//					editorID = QuestionItemEditor.ID;
//					newEntityType = EditorInput.ENTITY_TYPE.QUESTION_ITEM;
//				}
//				break;
//			case QUESTION_ITEM:
//				editorID = QuestionItemEditor.ID;
//				newEntityType = EditorInput.ENTITY_TYPE.QUESTION_ITEM;
//				break;
//			default:
//				System.err.println("Entity Type not supported: " + selectedEntityType);
//				System.exit(0);
//				break;
//			}
//			EditorInput input = new EditorInput(null, null, parentId, parentVersion, newEntityType, EDITOR_MODE_TYPE.NEW,
//					currentView, properties);
//			page.openEditor(input, editorID);
//
//			// Notify any listeners of the view with the actual data of the view
//			treeViewer.setSelection(treeViewer.getSelection());
//		} catch (PartInitException ex) {
//			MessageDialog.openError(currentView.getSite().getShell(), Messages.getString("ErrorTitle"), Messages
//					.getString("View.mess.EditorOpenError") + "\n" + ex.getMessage()); //$NON-NLS-1$
//		}
//	}

	private void deleteItem(EDITOR_MODE_TYPE mode) {
		ISelection selection = treeViewer.getSelection();
		Object obj = ((IStructuredSelection) selection).getFirstElement();
		LightXmlObjectType item = (LightXmlObjectType) obj;
		EditorInput.ENTITY_TYPE entityType = Entity.getEntityType(item);
		if (MessageDialog.openConfirm(currentView.getSite().getShell(), Messages.getString("ConfirmTitle"),
				MessageFormat.format(Messages.getString("View.mess.ConfirmDeletion"), getEntityName(entityType), item.getId()))) {
			try {
				switch (entityType) {
				case FILE:
					MessageUtil.currentNotSupported(currentView.getSite().getShell());
					break;
				case CONCEPT_SCHEME:
					ConceptSchemes.init(properties);
					List<LightXmlObjectType> conceptList = ConceptSchemes.getConceptsLight(item.getId(), item.getVersion());
					if (conceptList.size() > 0
							&& !MessageDialog.openConfirm(currentView.getSite().getShell(), Messages
									.getString("ConfirmTitle"), MessageFormat.format(Messages
									.getString("View.mess.ConfirmDeleteConcepts"), conceptList.size()))) {
						break;
					}
					ConceptSchemes.delete(item.getId(), item.getVersion(), item.getParentId(), item
							.getParentVersion());
					break;
				case CONCEPT:
					Concepts.init(properties);
					Concepts.delete(item.getId(), item.getVersion(), item.getParentId(), item.getParentVersion());
					break;
				case CODE_SCHEME:
					CodeSchemes.init(properties);
					List<LightXmlObjectType> codeList = CodeSchemes.getCodesLight(item.getId(), item.getVersion());
					if (codeList.size() > 0
							&& !MessageDialog.openConfirm(currentView.getSite().getShell(), Messages
									.getString("ConfirmTitle"), MessageFormat.format(Messages
									.getString("View.mess.ConfirmDeleteCodes"), codeList.size()))) {
						break;
					}
					CodeSchemes.delete(item.getId(), item.getVersion(), item.getParentId(), item
							.getParentVersion());
					break;
				case CODE:
					MessageUtil.currentNotSupported(currentView.getSite().getShell());
					break;					
				case QUESTION_SCHEME:
					QuestionSchemes.init(properties);
					List<LightXmlObjectType> questionItemList = QuestionSchemes.getQuestionItemsLight(item.getId(),
							item.getVersion());
					if (questionItemList.size() > 0
							&& !MessageDialog.openConfirm(currentView.getSite().getShell(), Messages
									.getString("ConfirmTitle"), MessageFormat.format(Messages
									.getString("View.mess.ConfirmDeleteQuestionItem"), questionItemList.size()))) {
						break;
					}
					QuestionSchemes.delete(item.getId(), item.getVersion(), item.getParentId(), item
							.getParentVersion());
					break;
				case QUESTION_ITEM:
					QuestionItems.init(properties);
					QuestionItems.delete(item.getId(), item.getVersion(), item.getParentId(), item.getParentVersion());
					break;
				default:
					// TODO error handling
					System.err.println("Editor Type not supported: " + entityType);
					System.exit(0);
					break;
				}
				// TODO firePropertyChange(IEditorPart.PROP_DIRTY);
			} catch (PartInitException ex) {
				MessageDialog.openError(currentView.getSite().getShell(), Messages.getString("ErrorTitle"), Messages
						.getString("View.mess.EditorUIDeleteError") + "\n" + ex.getMessage()); //$NON-NLS-1$
			} catch (Exception e) {
				MessageDialog.openError(currentView.getSite().getShell(), Messages.getString("ErrorTitle"), Messages
						.getString("View.mess.EditorDeleteError") + "\n" + e.getMessage()); //$NON-NLS-1$
			}
			treeViewer.refresh();
			treeViewer.getControl().setRedraw(true);
			treeViewer.expandAll();
			treeViewer.getTree().setFocus();
		}
	}

	public void setMenu() {

		// Assign double click to edit function
		treeViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(final DoubleClickEvent event) {
				openEditor(treeViewer, currentView, properties, EDITOR_MODE_TYPE.EDIT);
			}
		});

		// Define Tree Pop-up Menu
		menu.setDefaultItem(editMenuItem);
		treeViewer.getTree().setMenu(menu);

		// Define NEW Pop-up Menu Item
		final MenuItem newMenuItem = new MenuItem(menu, SWT.CASCADE);
		newMenuItem.setSelection(true);
		newMenuItem.setText(Messages.getString("View.label.newItemMenuItem.New")); //$NON-NLS-1$
		newMenuItem.setImage(ResourceManager.getPluginImage(Activator.getDefault(), "icons/new_wiz.gif"));
		newMenuItem.setData("name", "NEW");
		Menu submenu = new Menu(newMenuItem);
		newMenuItem.setMenu(submenu);

		// Define NEW -> SCHEME Pop-up Menu Item
		final MenuItem schemeMenuItem = new MenuItem(submenu, SWT.NONE);
		schemeMenuItem.setSelection(true);
		schemeMenuItem.setText(newMenuLabelList.get(0));
		schemeMenuItem.setImage(ResourceManager.getPluginImage(Activator.getDefault(), "icons/new_wiz.gif"));
		schemeMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				ISelection selection = treeViewer.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();
				LightXmlObjectType item = (LightXmlObjectType) obj;
				newItem(treeViewer, currentView, properties, NEW_TYPE.SCHEME, item.getParentId(), item.getParentVersion());
			}
		});

		// Define NEW -> ITEM Pop-up Menu Item
		final MenuItem itemMenuItem = new MenuItem(submenu, SWT.NONE);
		itemMenuItem.setText(newMenuLabelList.get(1)); //$NON-NLS-1$
		itemMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				String parentId = "";
				String parentVersion = "";

				ISelection selection = treeViewer.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();
				LightXmlObjectType item = (LightXmlObjectType) obj;
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				if (item.getElement().equals(rootElementName)) {
					parentId = item.getId();
					parentVersion = item.getVersion();
				} else {
					parentId = item.getParentId();
					parentVersion = item.getParentVersion();
				}
				newItem(treeViewer, currentView, properties, NEW_TYPE.ITEM, parentId, parentVersion);
			}
		});

		// Disable "New -> Scheme" if a Item is selected
		newMenuItem.addArmListener(new ArmListener() {
			public void widgetArmed(final ArmEvent e) {
				ISelection selection = treeViewer.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();
				LightXmlObjectType item = (LightXmlObjectType) obj;
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				if (!item.getElement().equals(rootElementName)) {
					schemeMenuItem.setEnabled(false);
				} else {
					schemeMenuItem.setEnabled(true);
				}
			}
		});

		itemMenuItem.setImage(ResourceManager.getPluginImage(Activator.getDefault(), "icons/new_wiz.gif"));

		// Define DELETE Pop-up Menu Item
		final MenuItem deleteMenuItem = new MenuItem(menu, SWT.NONE);
		deleteMenuItem.setText(Messages.getString("View.label.deleteMenuItem.Delete")); //$NON-NLS-1$
		deleteMenuItem.setImage(ResourceManager.getPluginImage(Activator.getDefault(), "icons/delete_obj.gif"));
		deleteMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				TreeItem[] t = treeViewer.getTree().getSelection();
				for (int i = 0; i < t.length; i++) {
					log.debug(t[i].getText() + ", ");
					log.debug(t[i].getData("parentId"));
				}
				// TODO Distinguish between Schemes and Items
				deleteItem(EDITOR_MODE_TYPE.EDIT);
			}
		});

		// Define EDIT Pop-up Menu Item
		editMenuItem = new MenuItem(menu, SWT.NONE);
		editMenuItem.setText(Messages.getString("View.label.editMenuItem.Edit")); //$NON-NLS-1$
		editMenuItem.setImage(ResourceManager.getPluginImage(Activator.getDefault(), "icons/editor_area.gif"));
		editMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				TreeItem[] t = treeViewer.getTree().getSelection();
				if (t.length != 1) {
					MessageDialog.openInformation(currentView.getSite().getShell(),
							Messages.getString("InfoTitle"), Messages.getString("Editor.mess.NotSupported")); //$NON-NLS-1$
					return;
				}
				openEditor(treeViewer, currentView, properties, EDITOR_MODE_TYPE.EDIT);
			}
		});

	}
}
