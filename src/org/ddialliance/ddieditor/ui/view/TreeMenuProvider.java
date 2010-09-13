package org.ddialliance.ddieditor.ui.view;

import java.text.MessageFormat;
import java.util.List;

import org.ddialliance.ddieditor.model.conceptual.ConceptualElement;
import org.ddialliance.ddieditor.model.conceptual.ConceptualType;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.model.resource.DDIResourceType;
import org.ddialliance.ddieditor.model.resource.StorageType;
import org.ddialliance.ddieditor.persistenceaccess.PersistenceManager;
import org.ddialliance.ddieditor.ui.Activator;
import org.ddialliance.ddieditor.ui.dbxml.code.CodeSchemeDao;
import org.ddialliance.ddieditor.ui.dbxml.concept.ConceptDao;
import org.ddialliance.ddieditor.ui.dbxml.concept.ConceptSchemeDao;
import org.ddialliance.ddieditor.ui.dbxml.instrument.ControlConstructSchemeDao;
import org.ddialliance.ddieditor.ui.dbxml.instrument.IfThenElseDao;
import org.ddialliance.ddieditor.ui.dbxml.instrument.InstrumentDao;
import org.ddialliance.ddieditor.ui.dbxml.instrument.LoopDao;
import org.ddialliance.ddieditor.ui.dbxml.instrument.StatementItemDao;
import org.ddialliance.ddieditor.ui.dbxml.question.QuestionItemDao;
import org.ddialliance.ddieditor.ui.dbxml.question.QuestionSchemeDao;
import org.ddialliance.ddieditor.ui.dbxml.universe.UniverseDao;
import org.ddialliance.ddieditor.ui.dbxml.universe.UniverseSchemeDao;
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
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ArmEvent;
import org.eclipse.swt.events.ArmListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PartInitException;

/**
 * Provides menus to view list items
 */
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
		menu = new Menu(treeViewer.getTree());
	}

	public void setMenu() {
		// double click edit
		treeViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(final DoubleClickEvent event) {
				Object obj = ((TreeSelection) event.getSelection())
						.getFirstElement();
				if (obj instanceof ConceptualType) {
					openPerspective(treeViewer, currentView);
					return;
				}
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
				openMenuItem.setText(Messages
						.getString("View.label.openMenuItem.Open")); //$NON-NLS-1$
				openMenuItem.setImage(ResourceManager.getPluginImage(Activator
						.getDefault(), "icons/new_wiz.gif"));
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
		} catch (DDIFtpException e) {
			DialogUtil.errorDialog(currentView.getSite().getShell(),
					currentView.ID, "Error", e.getMessage(), e);
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
				deleteItem(EditorModeType.EDIT);
			}
		});

		// sub menu
		createSubMenu(newMenuItem, subElements);
	}

	public void createSubMenu(MenuItem newMenuItem,
			final List<ElementType> subElements) {
		final Menu subMenu = new Menu(newMenuItem);
		newMenuItem.setMenu(subMenu);
		MenuItem menuItem = new MenuItem(subMenu, SWT.NONE);
		menuItem.setText("initial");

		newMenuItem.addArmListener(new ArmListener() {
			public void widgetArmed(final ArmEvent event) {
				InputSelection inputSelection = defineSelection(treeViewer,
						"na");
				// remember breakpoints in this code freezes xorg
				// if (log.isDebugEnabled()) {
				// log.debug("Selection: "
				// + inputSelection.getSelection().getClass()
				// .getSimpleName() + ", value: "
				// + inputSelection.getSelection());
				// }

				// light xml object
				if (inputSelection.getSelection() instanceof LightXmlObjectType) {
					LightXmlObjectType lightXmlObject = (LightXmlObjectType) inputSelection
							.getSelection();

					ElementType type = null;
					try {
						type = ElementType.getElementType(lightXmlObject
								.getElement());
					} catch (DDIFtpException e) {
						DialogUtil.errorDialog(
								currentView.getSite().getShell(),
								currentView.ID, "Error", e.getMessage(), e);
					}
					cleanPreviousMenuItems();
					// if (log.isDebugEnabled()) {
					// log.debug("ElementType: " + type.toString());
					// }

					// create menu
					if (type.equals(rootElement)) {
						createNewMenuItem(type, true);
						for (ElementType subType : subElements) {
							if (subType.equals(rootElement)) {
								continue;
							}
							createNewMenuItem(subType, false);
						}
					} else {
						createNewMenuItem(type, false);
					}
				}

				// ddi resource
				else if (inputSelection.getSelection() instanceof DDIResourceType) {
					cleanPreviousMenuItems();
					createNewMenuItem(ElementType.FILE, true);
				}
				// log guard
				else if (log.isDebugEnabled()) {
					log.debug("No match found > no menuitems > do fix");
				}
			}

			private void cleanPreviousMenuItems() {
				MenuItem[] menuItems = subMenu.getItems();
				for (int i = 0; i < menuItems.length; i++) {
					menuItems[i].dispose();
				}
			}

			private void createNewMenuItem(final ElementType type,
					boolean isRoot) {
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
		if (obj instanceof DDIResourceType) {
			// TODO delete DDIResourceType stuff
			DDIResourceType ddiResource = (DDIResourceType) obj;
			try {
				StorageType storage = PersistenceManager.getInstance()
						.getStorageByResourceOrgName(ddiResource.getOrgName());
				PersistenceManager.getInstance().deleteResource(
						ddiResource.getOrgName());
				PersistenceManager.getInstance().deleteStorage(storage.getId());
				storage = null;
			} catch (DDIFtpException e) {
				DialogUtil.errorDialog(currentView.getSite().getShell(),
						currentView.ID, Messages.getString("ErrorTitle"),
						Messages.getString("Error deleting resource"), e);
			}
		}

		if (obj instanceof ConceptualElement) {
			ConceptualElement ce = (ConceptualElement) obj;
			obj = ce.getValue();
		}

		if (obj instanceof LightXmlObjectType) {
			LightXmlObjectType lightXmlObject = (LightXmlObjectType) obj;
			ElementType entityType = null;
			try {
				entityType = ElementType.getElementType(lightXmlObject
						.getElement());
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
						List<LightXmlObjectType> conceptList = new ConceptDao()
								.getLightXmlObject(lightXmlObject);
						if (conceptList.size() > 0
								&& !MessageDialog
										.openConfirm(
												currentView.getSite()
														.getShell(),
												Messages
														.getString("ConfirmTitle"),
												MessageFormat
														.format(
																Messages
																		.getString("View.mess.ConfirmDeleteConcepts"),
																conceptList
																		.size()))) {
							break;
						}
						new ConceptSchemeDao().delete(lightXmlObject.getId(),
								lightXmlObject.getVersion(), lightXmlObject
										.getParentId(), lightXmlObject
										.getParentVersion());
						break;
					case CONCEPT:
						new ConceptDao().delete(lightXmlObject.getId(),
								lightXmlObject.getVersion(), lightXmlObject
										.getParentId(), lightXmlObject
										.getParentVersion());

						break;
					case CODE_SCHEME:
						new CodeSchemeDao().delete(lightXmlObject.getId(),
								lightXmlObject.getVersion(), lightXmlObject
										.getParentId(), lightXmlObject
										.getParentVersion());
						break;
					case UNIVERSE_SCHEME:
						new UniverseSchemeDao().delete(lightXmlObject.getId(),
								lightXmlObject.getVersion(), lightXmlObject
										.getParentId(), lightXmlObject
										.getParentVersion());
						break;
					case UNIVERSE:
						new UniverseDao().delete(lightXmlObject.getId(),
								lightXmlObject.getVersion(), lightXmlObject
										.getParentId(), lightXmlObject
										.getParentVersion());
						break;
					case QUESTION_SCHEME:
						QuestionSchemeDao dao = new QuestionSchemeDao();
						List<LightXmlObjectType> questionItemList = new QuestionItemDao()
								.getLightXmlObject(lightXmlObject);

						// confirm deletions of x number of question items
						// TODO needs inclusion of refs to instrumentation
						// elements
						if (questionItemList.size() > 0
								&& !MessageDialog
										.openConfirm(
												currentView.getSite()
														.getShell(),
												Messages
														.getString("ConfirmTitle"),
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
					case CONTROL_CONSTRUCT_SCHEME:
						new ControlConstructSchemeDao().delete(lightXmlObject
								.getId(), lightXmlObject.getVersion(),
								lightXmlObject.getParentId(), lightXmlObject
										.getParentVersion());
						break;
					case LOOP:
						new LoopDao().delete(lightXmlObject
								.getId(), lightXmlObject.getVersion(),
								lightXmlObject.getParentId(), lightXmlObject
										.getParentVersion());
						break;
						
					default:
						DDIFtpException e = new DDIFtpException(
								"Editor type not supported: " + entityType,
								new Throwable());
						DialogUtil.errorDialog(
								currentView.getSite().getShell(),
								this.currentView.ID,
								"Editor type is not supported", e.getMessage(),
								e);
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
			}
		}
		treeViewer.refresh();
		treeViewer.getControl().setRedraw(true);
		treeViewer.expandToLevel(2);
		treeViewer.getTree().setFocus();
	}

	/**
	 * Removes an element in the tree list
	 * 
	 * @param element
	 *            to search for and remove
	 */
	private void removeItemFromMenu(Object element) {
		Object[] expanded = treeViewer.getExpandedElements();
		for (int i = 0; i < expanded.length; i++) {
			if (expanded[i] instanceof List<?>) {
				((List<LightXmlObjectType>) expanded[i]).remove(element);
			}
		}
	}
}
