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

import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.Activator;
import org.ddialliance.ddieditor.ui.dbxml.code.CodeSchemes;
import org.ddialliance.ddieditor.ui.dbxml.concept.ConceptSchemes;
import org.ddialliance.ddieditor.ui.dbxml.concept.Concepts;
import org.ddialliance.ddieditor.ui.dbxml.instrument.IfThenElseDao;
import org.ddialliance.ddieditor.ui.dbxml.instrument.InstrumentDao;
import org.ddialliance.ddieditor.ui.dbxml.instrument.StatementItemDao;
import org.ddialliance.ddieditor.ui.dbxml.question.QuestionItemDao;
import org.ddialliance.ddieditor.ui.dbxml.question.QuestionSchemeDao;
import org.ddialliance.ddieditor.ui.editor.EditorInput.EditorModeType;
import org.ddialliance.ddieditor.ui.model.ElementType;
import org.ddialliance.ddieditor.ui.util.DialogUtil;
import org.ddialliance.ddieditor.ui.util.MessageUtil;
import org.ddialliance.ddieditor.ui.util.swtdesigner.ResourceManager;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ArmEvent;
import org.eclipse.swt.events.ArmListener;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PartInitException;

public class TreeMenuProvider extends TreeMenu {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			TreeMenuProvider.class);
	public static final String ELEMENT_TYPE = "ep";
	final TreeViewer treeViewer;
	final View currentView;
	final ElementType rootElement;
	final Menu menu;
	MenuItem editMenuItem = null;
	private List<ElementType> subElements;
	private boolean withOpen;

	/**
	 * Constructor for TreeMenuProvider
	 * 
	 * @param treeViewer
	 * @param currentView
	 * @param rootElement
	 *            root element type
	 * @param subElements
	 *            list of sub elements types
	 */
	public TreeMenuProvider(TreeViewer treeViewer, View currentView,
			ElementType rootElement, List<ElementType> subElements) {
		this.treeViewer = treeViewer;
		this.currentView = currentView;
		this.rootElement = rootElement;
		this.subElements = subElements;
		this.withOpen = true;
		menu = new Menu(treeViewer.getTree());
	}

	public void setMenu() {
		// double click edit
		treeViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(final DoubleClickEvent event) {
				openEditor(treeViewer, currentView, EditorModeType.EDIT, null);
			}
		});

		// menu
		menu.setDefaultItem(editMenuItem);
		treeViewer.getTree().setMenu(menu);
		
		// menu open
		try {
			if (ElementType.withOpenMenuItem(rootElement.getElementName())) {
				final MenuItem openMenuItem = new MenuItem(menu, SWT.CASCADE);
				openMenuItem.setSelection(true);
				openMenuItem.setText(Messages.getString("View.label.openMenuItem.Open")); //$NON-NLS-1$
				openMenuItem.setImage(ResourceManager.getPluginImage(Activator.getDefault(), "icons/new_wiz.gif"));
				openMenuItem.setData("name", "OPEN");
				openMenuItem.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(final SelectionEvent e) {
						TreeItem[] t = treeViewer.getTree().getSelection();
						if (t.length != 1) {
							MessageDialog
									.openInformation(
											currentView.getSite().getShell(),
											Messages.getString("InfoTitle"), Messages.getString("Editor.mess.NotSupported")); //$NON-NLS-1$
							return;
						}
						openPerspective(treeViewer, currentView);
					}
				});
			}
		} catch (DDIFtpException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// menu new
		final MenuItem newMenuItem = new MenuItem(menu, SWT.CASCADE);
		newMenuItem.setSelection(true);
		newMenuItem.setText(Messages
				.getString("View.label.newItemMenuItem.New")); //$NON-NLS-1$
		newMenuItem.setImage(ResourceManager.getPluginImage(Activator
				.getDefault(), "icons/new_wiz.gif"));
		newMenuItem.setData("name", "NEW");

		// menu edit
		editMenuItem = new MenuItem(menu, SWT.NONE);
		editMenuItem
				.setText(Messages.getString("View.label.editMenuItem.Edit")); //$NON-NLS-1$
		editMenuItem.setImage(ResourceManager.getPluginImage(Activator
				.getDefault(), "icons/editor_area.gif"));
		editMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				TreeItem[] t = treeViewer.getTree().getSelection();
				if (t.length != 1) {
					MessageDialog
							.openInformation(
									currentView.getSite().getShell(),
									Messages.getString("InfoTitle"), Messages.getString("Editor.mess.NotSupported")); //$NON-NLS-1$
					return;
				}
				openEditor(treeViewer, currentView, EditorModeType.EDIT, null);
			}
		});

		// menu delete
		final MenuItem deleteMenuItem = new MenuItem(menu, SWT.NONE);
		deleteMenuItem.setText(Messages
				.getString("View.label.deleteMenuItem.Delete")); //$NON-NLS-1$
		deleteMenuItem.setImage(ResourceManager.getPluginImage(Activator
				.getDefault(), "icons/delete_obj.gif"));
		deleteMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				// TreeItem[] t = treeViewer.getTree().getSelection();
				// for (int i = 0; i < t.length; i++) {
				// log.debug(t[i].getText() + ", ");
				// log.debug(t[i].getData("parentId"));
				// }
				// TODO Distinguish between Schemes and Items
				deleteItem(EditorModeType.EDIT);
			}
		});

		// sub menu
		final Menu subMenu = new Menu(newMenuItem);
		newMenuItem.setMenu(subMenu);
		MenuItem menuItem = new MenuItem(subMenu, SWT.NONE);
		menuItem.setText("initial");
		
		newMenuItem.addArmListener(new ArmListener() {
			public void widgetArmed(final ArmEvent event) {
				LightXmlObjectType lightXmlObject = defineSelection(treeViewer, "none");

				System.out.println("TreeMenuProvider.setMenu().new ArmListener() {...}.widgetArmed()");
				System.out.println("lightXmlObject: "+lightXmlObject);
				System.out.println("type: "+lightXmlObject.getElement());
				ElementType type = null;
				try {
					type = ElementType.getElementType(lightXmlObject
							.getElement());
				} catch (DDIFtpException e) {
					// TODO error dialog for view
				}

				// clean previous items
				MenuItem[] menuItems = subMenu.getItems();
				for (int i = 0; i < menuItems.length; i++) {
					menuItems[i].dispose();
				}

				// create menu itemssearch?hl=da&q=google+news+danmark&sourceid=navclient-ff&rlz=1B3GGGL_daDK248DK248&ie=UTF-8&aq=0&oq=google+news
				if (type.equals(rootElement)) {
					createNewMenuItem(type, true);
					for (ElementType subType : subElements) {
						createNewMenuItem(subType, false);
					}
				} else {
					createNewMenuItem(type, false);
				}
			}

			private void createNewMenuItem(final ElementType type,
					boolean isRoot) {
				System.out.println("TreeMenuProvider.setMenu().new ArmListener() {...}.createNewMenuItem()");
				MenuItem menuItem = new MenuItem(subMenu, SWT.NONE);
				menuItem.setText(type.getTranslatedDisplayMessageEntry());
				menuItem.setImage(ResourceManager.getPluginImage(Activator
						.getDefault(), "icons/new_wiz.gif"));

				menuItem.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(final SelectionEvent e) {
						openEditor(treeViewer, currentView, EditorModeType.NEW,
								type);
					}
				});
			}
		});
	}

	private void deleteItem(EditorModeType mode) {
		ISelection selection = treeViewer.getSelection();
		Object obj = ((IStructuredSelection) selection).getFirstElement();
		LightXmlObjectType lightXmlObject = (LightXmlObjectType) obj;
		ElementType entityType = null;
		try {
			entityType = ElementType
					.getElementType(lightXmlObject.getElement());
		} catch (DDIFtpException e) {
			DialogUtil
					.errorDialog(
							currentView.getSite().getShell(),
							currentView.ID,
							Messages.getString("ErrorTitle"),
							Messages.getString("View.mess.EditorNewError") + "\n" + e.getMessage(), e); //$NON-NLS-1$
		}

		if (MessageDialog.openConfirm(currentView.getSite().getShell(),
				Messages.getString("ConfirmTitle"), MessageFormat.format(
						Messages.getString("View.mess.ConfirmDeletion"),
						entityType.getTranslatedDisplayMessageEntry(),
						lightXmlObject.getId()))) {
			try {
				switch (entityType) {
				case FILE:
					MessageUtil.currentNotSupported(currentView.getSite()
							.getShell());
					break;
				case CONCEPT_SCHEME:
					List<LightXmlObjectType> conceptList = ConceptSchemes
							.getConceptsLight(lightXmlObject.getId(),
									lightXmlObject.getVersion());
					if (conceptList.size() > 0
							&& !MessageDialog
									.openConfirm(
											currentView.getSite().getShell(),
											Messages.getString("ConfirmTitle"),
											MessageFormat
													.format(
															Messages
																	.getString("View.mess.ConfirmDeleteConcepts"),
															conceptList.size()))) {
						break;
					}
					ConceptSchemes.delete(lightXmlObject.getId(),
							lightXmlObject.getVersion(), lightXmlObject
									.getParentId(), lightXmlObject
									.getParentVersion());
					break;
				case CONCEPT:
					Concepts.delete(lightXmlObject.getId(), lightXmlObject
							.getVersion(), lightXmlObject.getParentId(),
							lightXmlObject.getParentVersion());
					break;
				case CODE_SCHEME:
					List<LightXmlObjectType> codeList = CodeSchemes
							.getCodesLight(lightXmlObject.getId(),
									lightXmlObject.getVersion());
					if (codeList.size() > 0
							&& !MessageDialog
									.openConfirm(
											currentView.getSite().getShell(),
											Messages.getString("ConfirmTitle"),
											MessageFormat
													.format(
															Messages
																	.getString("View.mess.ConfirmDeleteCodes"),
															codeList.size()))) {
						break;
					}
					CodeSchemes.delete(lightXmlObject.getId(), lightXmlObject
							.getVersion(), lightXmlObject.getParentId(),
							lightXmlObject.getParentVersion());
					break;
				case CODE:
					MessageUtil.currentNotSupported(currentView.getSite()
							.getShell());
					break;
				case QUESTION_SCHEME:
					QuestionSchemeDao dao = new QuestionSchemeDao();
					List<LightXmlObjectType> questionItemList = new QuestionItemDao()
							.getLightXmlObject(lightXmlObject);

					// confirm deletions of x number of question items
					// TODO needs inclusion of refs to instrumentation elements
					if (questionItemList.size() > 0
							&& !MessageDialog
									.openConfirm(
											currentView.getSite().getShell(),
											Messages.getString("ConfirmTitle"),
											MessageFormat
													.format(
															Messages
																	.getString("View.mess.ConfirmDeleteQuestionItem"),
															questionItemList
																	.size()))) {
						break;
					}
					dao.delete(lightXmlObject.getId(), lightXmlObject
							.getVersion(), lightXmlObject.getParentId(),
							lightXmlObject.getParentVersion());
					break;
				case QUESTION_ITEM:
					new QuestionItemDao().delete(lightXmlObject.getId(),
							lightXmlObject.getVersion(), lightXmlObject
									.getParentId(), lightXmlObject
									.getParentVersion());
					break;
				case INSTRUMENT:
					new InstrumentDao().delete(lightXmlObject.getId(),
							lightXmlObject.getVersion(), lightXmlObject
									.getParentId(), lightXmlObject
									.getParentVersion());

					break;
				case STATEMENT_ITEM:
					new StatementItemDao().delete(lightXmlObject.getId(),
							lightXmlObject.getVersion(), lightXmlObject
									.getParentId(), lightXmlObject
									.getParentVersion());

					break;

				case IF_THEN_ELSE:
					new IfThenElseDao().delete(lightXmlObject.getId(),
							lightXmlObject.getVersion(), lightXmlObject
									.getParentId(), lightXmlObject
									.getParentVersion());

					break;
				default:
					DDIFtpException e = new DDIFtpException(
							"Editor type not supported: " + entityType,
							new Throwable());
					DialogUtil.errorDialog(currentView.getSite().getShell(),
							this.currentView.ID,
							"Editor type is not supported", e.getMessage(), e);
					break;
				}
			} catch (PartInitException ex) {
				DialogUtil
						.errorDialog(
								currentView.getSite().getShell(),
								currentView.ID,
								Messages.getString("ErrorTitle"),
								Messages
										.getString("View.mess.EditorUIDeleteError") + "\n" + ex.getMessage(), ex); //$NON-NLS-1$
			} catch (Exception e) {
				DialogUtil
						.errorDialog(
								currentView.getSite().getShell(),
								currentView.ID,
								Messages.getString("ErrorTitle"),
								Messages
										.getString("View.mess.EditorDeleteError") + "\n" + e.getMessage(), e); //$NON-NLS-1$
			}
			removeItemFromMenu(lightXmlObject);
			treeViewer.refresh();
			treeViewer.getControl().setRedraw(true);
			treeViewer.expandToLevel(2);
			treeViewer.getTree().setFocus();
		}
	}

	/**
	 * Removes an element in the tree list
	 * 
	 * @param lightXmlObject
	 *            to search for and remove
	 */
	private void removeItemFromMenu(LightXmlObjectType lightXmlObject) {
		Object[] expanded = treeViewer.getExpandedElements();
		for (int i = 0; i < expanded.length; i++) {
			if (expanded[i] instanceof List<?>) {
				for (LightXmlObjectType inList : ((List<LightXmlObjectType>) expanded[i])) {
					if (inList.equals(lightXmlObject)) {
						treeViewer.remove(inList);
					}
				}
			}
		}
	}
}
