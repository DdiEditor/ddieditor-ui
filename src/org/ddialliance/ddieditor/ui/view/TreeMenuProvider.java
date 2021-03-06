package org.ddialliance.ddieditor.ui.view;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.conceptual.ConceptualElement;
import org.ddialliance.ddieditor.model.conceptual.ConceptualType;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.model.resource.DDIResourceType;
import org.ddialliance.ddieditor.persistenceaccess.PersistenceManager;
import org.ddialliance.ddieditor.persistenceaccess.maintainablelabel.MaintainableLightLabelQueryResult;
import org.ddialliance.ddieditor.ui.Activator;
import org.ddialliance.ddieditor.ui.command.CommandHelper;
import org.ddialliance.ddieditor.ui.dbxml.category.CategoryDao;
import org.ddialliance.ddieditor.ui.dbxml.category.CategorySchemeDao;
import org.ddialliance.ddieditor.ui.dbxml.code.CodeSchemeDao;
import org.ddialliance.ddieditor.ui.dbxml.concept.ConceptDao;
import org.ddialliance.ddieditor.ui.dbxml.concept.ConceptSchemeDao;
import org.ddialliance.ddieditor.ui.dbxml.instrument.ComputationItemDao;
import org.ddialliance.ddieditor.ui.dbxml.instrument.ControlConstructSchemeDao;
import org.ddialliance.ddieditor.ui.dbxml.instrument.IfThenElseDao;
import org.ddialliance.ddieditor.ui.dbxml.instrument.InstrumentDao;
import org.ddialliance.ddieditor.ui.dbxml.instrument.LoopDao;
import org.ddialliance.ddieditor.ui.dbxml.instrument.QuestionConstructDao;
import org.ddialliance.ddieditor.ui.dbxml.instrument.SequenceDao;
import org.ddialliance.ddieditor.ui.dbxml.instrument.StatementItemDao;
import org.ddialliance.ddieditor.ui.dbxml.question.MultipleQuestionItemDao;
import org.ddialliance.ddieditor.ui.dbxml.question.QuestionItemDao;
import org.ddialliance.ddieditor.ui.dbxml.question.QuestionSchemeDao;
import org.ddialliance.ddieditor.ui.dbxml.universe.UniverseDao;
import org.ddialliance.ddieditor.ui.dbxml.universe.UniverseSchemeDao;
import org.ddialliance.ddieditor.ui.dbxml.variable.VariableDao;
import org.ddialliance.ddieditor.ui.dbxml.variable.VariableSchemeDao;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddieditor.ui.editor.EditorInput;
import org.ddialliance.ddieditor.ui.editor.EditorInput.EditorModeType;
import org.ddialliance.ddieditor.ui.model.ElementType;
import org.ddialliance.ddieditor.ui.util.DialogUtil;
import org.ddialliance.ddieditor.ui.util.MessageUtil;
import org.ddialliance.ddieditor.ui.util.swtdesigner.ResourceManager;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.Translator;
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
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

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
			ElementType rootElement) {
		this.treeViewer = treeViewer;
		this.currentView = currentView;
		this.rootElement = rootElement;
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
		// commented out 20120518 -dead code never used
		// try {
		// if (ElementType.withOpenMenuItem(rootElement.getElementName())) {
		// final MenuItem openMenuItem = new MenuItem(menu, SWT.CASCADE);
		// openMenuItem.setSelection(true);
		// openMenuItem.setText(Translator
		//						.trans("View.label.openMenuItem.Open")); //$NON-NLS-1$
		// openMenuItem.setImage(ResourceManager.getPluginImage(
		// Activator.getDefault(), "icons/new_wiz.gif"));
		// openMenuItem.setData("name", "OPEN");
		// openMenuItem.addSelectionListener(new SelectionAdapter() {
		// public void widgetSelected(final SelectionEvent e) {
		// TreeItem[] t = treeViewer.getTree().getSelection();
		// if (t.length != 1) {
		// MessageDialog.openInformation(
		// currentView.getSite().getShell(),
		//									Translator.trans("InfoTitle"), Translator.trans("Editor.mess.NotSupported")); //$NON-NLS-1$
		// return;
		// }
		// openPerspective(treeViewer, currentView);
		// }
		// });
		// }
		// } catch (DDIFtpException e) {
		// DialogUtil.errorDialog(currentView.getSite().getShell(),
		// currentView.ID, "Error", e.getMessage(), e);
		// }

		// menu new
		final MenuItem newMenuItem = new MenuItem(menu, SWT.CASCADE);
		newMenuItem.setSelection(true);
		newMenuItem.setText(Translator.trans("View.label.newItemMenuItem.New")); //$NON-NLS-1$
		newMenuItem.setImage(ResourceManager.getPluginImage(
				Activator.getDefault(), "icons/new_wiz.gif"));
		newMenuItem.setData("name", "NEW");

		// menu edit
		editMenuItem = new MenuItem(menu, SWT.NONE);
		editMenuItem.setText(Translator.trans("View.label.editMenuItem.Edit")); //$NON-NLS-1$
		editMenuItem.setImage(ResourceManager.getPluginImage(
				Activator.getDefault(), "icons/editor_area.gif"));
		editMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent event) {
				TreeItem[] t = treeViewer.getTree().getSelection();
				if (t.length != 1) { // guard
					MessageDialog.openInformation(
							currentView.getSite().getShell(),
							Translator.trans("InfoTitle"), Translator.trans("Editor.mess.NotSupported")); //$NON-NLS-1$
					return;
				}

				// conceptual type
				if (t[0].getData() instanceof ConceptualType) {
					openPerspective(treeViewer, currentView);
					return;
				}

				// all other
				openEditor(treeViewer, currentView, EditorModeType.EDIT, null);
			}
		});

		// menu delete
		final MenuItem deleteMenuItem = new MenuItem(menu, SWT.NONE);
		deleteMenuItem.setText(Translator
				.trans("View.label.deleteMenuItem.Delete")); //$NON-NLS-1$
		deleteMenuItem.setImage(ResourceManager.getPluginImage(
				Activator.getDefault(), "icons/delete_obj.gif"));
		deleteMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				deleteItem(EditorModeType.EDIT);
			}
		});

		// sub menu
		createSubMenu(newMenuItem, rootElement);
	}

	public void createSubMenu(MenuItem newMenuItem,
			final ElementType rootElement) {
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
					createElementNewMenuItem(type);
				}

				// ddi resource
				else if (inputSelection.getSelection() instanceof DDIResourceType) {
					cleanPreviousMenuItems();
					ArrayList<ConceptualType> newItems = new ArrayList<ConceptualType>(
							Arrays.asList(ConceptualType.values()));

					// comment out 20120518 - adding new items is a good thing
					// :)
					// TreeItem[] items = treeViewer.getTree().getSelection()[0]
					// .getItems();
					// for (int i = 0; i < items.length; i++) {
					// Object obj = items[i].getData();
					// newItems.remove(obj);
					// }

					for (ConceptualType conceptualType : newItems) {
						if (conceptualType.equals(ConceptualType.STUDY)) {
							continue;
						}
						if (conceptualType
								.equals(ConceptualType.LOGIC_INSTRUMENT)) {
							try {
								PersistenceManager
										.getInstance()
										.setWorkingResource(
												((DDIResourceType) inputSelection
														.getSelection())
														.getOrgName());
								boolean check = DdiManager
										.getInstance()
										.getStudyUnitsLight(null, null, null,
												null).getLightXmlObjectList()
										.getLightXmlObjectList().isEmpty();
								if (check) {
									continue;
								}
							} catch (Exception e) {
								// do nothing
							}
						}
						try {
							createDDIResourceNewMenuItem(conceptualType,
									inputSelection.resourceId);
						} catch (DDIFtpException e) {
							// do nothing
						}
					}
				}
				// log guard
				else if (log.isWarnEnabled()) {
					log.warn("No match found > no menuitems > do fix");
				}
			}

			private void cleanPreviousMenuItems() {
				MenuItem[] menuItems = subMenu.getItems();
				for (int i = 0; i < menuItems.length; i++) {
					menuItems[i].dispose();
				}
			}

			private void createElementNewMenuItem(final ElementType type) {
				MenuItem menuItem = new MenuItem(subMenu, SWT.NONE);
				menuItem.setText(type.getTranslatedDisplayMessageEntry());
				menuItem.setImage(ResourceManager.getPluginImage(
						Activator.getDefault(), "icons/new_wiz.gif"));

				menuItem.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(final SelectionEvent e) {
						openEditor(treeViewer, currentView, EditorModeType.NEW,
								type);
					}
				});
				List<ElementType> subElements = type.getSubElements();
				if (subElements != null) {
					for (Iterator<ElementType> iterator = subElements
							.iterator(); iterator.hasNext();) {
						final ElementType elementType = iterator.next();
						menuItem = new MenuItem(subMenu, SWT.NONE);
						menuItem.setText(elementType
								.getTranslatedDisplayMessageEntry());
						menuItem.setImage(ResourceManager.getPluginImage(
								Activator.getDefault(), "icons/new_wiz.gif"));

						menuItem.addSelectionListener(new SelectionAdapter() {
							public void widgetSelected(final SelectionEvent e) {
								openEditor(treeViewer, currentView,
										EditorModeType.NEW, elementType);

							}
						});
					}
				}
			}

			private void createDDIResourceNewMenuItem(
					final ConceptualType conceptualType, final String resourceId)
					throws DDIFtpException {
				final ElementType elementType = ElementType
						.getElementTypeByConceptualType(conceptualType);
				MenuItem menuItem = new MenuItem(subMenu, SWT.NONE);
				menuItem.setText(Translator.trans(conceptualType.name()));
				menuItem.setImage(ResourceManager.getPluginImage(
						Activator.getDefault(), "icons/new_wiz.gif"));

				menuItem.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(final SelectionEvent e) {
						EditorInput input = new EditorInput(null, resourceId,
								null, null, null, null, elementType, null,
								EditorModeType.NEW);

						executeOpenEditor(input, EditorModeType.NEW,
								currentView, currentView.getSite().getShell());
					}
				});
			}
		});
	}

	private void deleteItem(EditorModeType mode) {
		boolean doRefeshOverView = false;
		boolean doOpenConfirm = true;
		boolean doDelete = false;

		// selection
		ISelection selection = treeViewer.getSelection();
		Object obj = ((IStructuredSelection) selection).getFirstElement();

		if (obj instanceof DDIResourceType) {
			DDIResourceType ddiResource = (DDIResourceType) obj;
			List<DDIResourceType> resources = new ArrayList<DDIResourceType>();
			resources.add(ddiResource);
			CommandHelper.deleteResources(resources);
			return;
		}

		if (obj instanceof ConceptualType) {
			// TODO implement delete on conceptual element
			DialogUtil.infoDialog(currentView.getSite().getShell(),
					this.currentView.ID, Translator.trans("ConfirmTitle"),
					Translator.trans(
							"editor.editelement.notimplemented",
							"ConceptualType: "
									+ Translator.trans(((ConceptualType) obj)
											.name())));
		}

		if (obj instanceof ConceptualElement) {
			ConceptualElement ce = (ConceptualElement) obj;
			obj = ce.getValue();
		}

		// MaintainableLightLabelQueryResult
		if (obj instanceof MaintainableLightLabelQueryResult) {
			doOpenConfirm = false;
			if (DialogUtil.deleteDialogMll(currentView.getSite().getShell(),
					Translator.trans("ConfirmTitle"),
					(MaintainableLightLabelQueryResult) obj)) {
				doDelete = true;
			}

			LightXmlObjectType result = ((MaintainableLightLabelQueryResult) obj)
					.getMaintainableTargetAsLightXmlObject();
			obj = result;
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
								Translator.trans("ErrorTitle"),
								Translator.trans("View.mess.EditorNewError") + "\n" + e.getMessage(), e); //$NON-NLS-1$
			}

			if (doOpenConfirm) {
				doDelete = MessageDialog.openConfirm(currentView.getSite()
						.getShell(), Translator.trans("ConfirmTitle"),
						MessageFormat.format(
								Translator.trans("View.mess.ConfirmDeletion"),
								entityType.getTranslatedDisplayMessageEntry(),
								lightXmlObject.getId()));
			}

			if (doDelete) {
				try {
					InputSelection inputSelection = defineSelection(treeViewer,
							currentView.ID);
					ElementType parentElementType = inputSelection
							.getParentElementType();

					// TODO only instantiate when needed!!!
					QuestionItemDao questionItemDao = new QuestionItemDao();
					List<LightXmlObjectType> multipleQuestionItemList = null;

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
												Translator
														.trans("ConfirmTitle"),
												MessageFormat.format(
														Translator
																.trans("View.mess.ConfirmDeleteConcepts"),
														conceptList.size()))) {
							break;
						}
						new ConceptSchemeDao().delete(lightXmlObject.getId(),
								lightXmlObject.getVersion(),
								lightXmlObject.getParentId(),
								lightXmlObject.getParentVersion());
						doRefeshOverView = true;
						break;
					case CONCEPT:
						new ConceptDao().delete(lightXmlObject.getId(),
								lightXmlObject.getVersion(),
								lightXmlObject.getParentId(),
								lightXmlObject.getParentVersion());

						break;
					case CODE_SCHEME:
						new CodeSchemeDao().delete(lightXmlObject.getId(),
								lightXmlObject.getVersion(),
								lightXmlObject.getParentId(),
								lightXmlObject.getParentVersion());
						doRefeshOverView = true;
						break;
					case UNIVERSE_SCHEME:
						new UniverseSchemeDao().delete(lightXmlObject.getId(),
								lightXmlObject.getVersion(),
								lightXmlObject.getParentId(),
								lightXmlObject.getParentVersion());
						doRefeshOverView = true;
						break;
					case UNIVERSE:
						new UniverseDao().delete(lightXmlObject.getId(),
								lightXmlObject.getVersion(),
								lightXmlObject.getParentId(),
								lightXmlObject.getParentVersion());
						break;
					case QUESTION_SCHEME:
						multipleQuestionItemList = new MultipleQuestionItemDao()
								.getLightXmlObject(lightXmlObject);
						questionItemDao
								.setParentElementType(ElementType.QUESTION_SCHEME);
						List<LightXmlObjectType> questionItemList = questionItemDao
								.getLightXmlObject(lightXmlObject);

						// confirm deletions of x number of question items
						// TODO needs inclusion of refs to instrumentation
						// elements
						if ((multipleQuestionItemList.size() > 0 || questionItemList
								.size() > 0)
								&& !MessageDialog
										.openConfirm(
												currentView.getSite()
														.getShell(),
												Translator
														.trans("ConfirmTitle"),
												MessageFormat.format(
														Translator
																.trans("View.mess.ConfirmDeleteMultipleQuestionItemQuestionItem"),
														multipleQuestionItemList
																.size(),
														questionItemList.size()))) {
							break;
						}
						new QuestionSchemeDao().delete(lightXmlObject.getId(),
								lightXmlObject.getVersion(),
								lightXmlObject.getParentId(),
								lightXmlObject.getParentVersion());
						doRefeshOverView = true;
						break;
					case MULTIPLE_QUESTION_ITEM:
						questionItemDao
								.setParentElementType(ElementType.MULTIPLE_QUESTION_ITEM);
						questionItemList = questionItemDao
								.getLightXmlObject(lightXmlObject);

						// confirm deletions of x number of question items
						// TODO needs inclusion of refs to instrumentation
						// elements
						if (questionItemList.size() > 0
								&& !MessageDialog
										.openConfirm(
												currentView.getSite()
														.getShell(),
												Translator
														.trans("ConfirmTitle"),
												MessageFormat.format(
														Translator
																.trans("View.mess.ConfirmDeleteQuestionItem"),
														questionItemList.size()))) {
							break;
						}

						new MultipleQuestionItemDao().delete(
								lightXmlObject.getId(),
								lightXmlObject.getVersion(),
								lightXmlObject.getParentId(),
								lightXmlObject.getParentVersion());
						break;
					case QUESTION_ITEM:
						questionItemDao.setParentElementType(parentElementType);
						questionItemDao.delete(lightXmlObject.getId(),
								lightXmlObject.getVersion(),
								lightXmlObject.getParentId(),
								lightXmlObject.getParentVersion());
						break;
					case CATEGORY:
						new CategoryDao().delete(lightXmlObject.getId(),
								lightXmlObject.getVersion(),
								lightXmlObject.getParentId(),
								lightXmlObject.getParentVersion());
						break;
					case CATEGORY_SCHEME:
						new CategorySchemeDao().delete(lightXmlObject.getId(),
								lightXmlObject.getVersion(),
								lightXmlObject.getParentId(),
								lightXmlObject.getParentVersion());
						doRefeshOverView = true;
						break;
					case INSTRUMENT:
						new InstrumentDao().delete(lightXmlObject.getId(),
								lightXmlObject.getVersion(),
								lightXmlObject.getParentId(),
								lightXmlObject.getParentVersion());
						doRefeshOverView = true;
						break;
					case QUESTION_CONSTRUCT:
						new QuestionConstructDao().delete(
								lightXmlObject.getId(),
								lightXmlObject.getVersion(),
								lightXmlObject.getParentId(),
								lightXmlObject.getParentVersion());
						break;
					case STATEMENT_ITEM:
						new StatementItemDao().delete(lightXmlObject.getId(),
								lightXmlObject.getVersion(),
								lightXmlObject.getParentId(),
								lightXmlObject.getParentVersion());
						break;
					case IF_THEN_ELSE:
						new IfThenElseDao().delete(lightXmlObject.getId(),
								lightXmlObject.getVersion(),
								lightXmlObject.getParentId(),
								lightXmlObject.getParentVersion());
						break;
					case SEQUENCE:
						new SequenceDao().delete(lightXmlObject.getId(),
								lightXmlObject.getVersion(),
								lightXmlObject.getParentId(),
								lightXmlObject.getParentVersion());
						break;
					case CONTROL_CONSTRUCT_SCHEME:
						new ControlConstructSchemeDao().delete(
								lightXmlObject.getId(),
								lightXmlObject.getVersion(),
								lightXmlObject.getParentId(),
								lightXmlObject.getParentVersion());
						doRefeshOverView = true;
						break;
					case LOOP:
						new LoopDao().delete(lightXmlObject.getId(),
								lightXmlObject.getVersion(),
								lightXmlObject.getParentId(),
								lightXmlObject.getParentVersion());
						break;
					case COMPUTATION_ITEM:
						new ComputationItemDao().delete(lightXmlObject.getId(),
								lightXmlObject.getVersion(),
								lightXmlObject.getParentId(),
								lightXmlObject.getParentVersion());
						break;
					case VARIABLE_SCHEME:
						new VariableSchemeDao().delete(lightXmlObject.getId(),
								lightXmlObject.getVersion(),
								lightXmlObject.getParentId(),
								lightXmlObject.getParentVersion());
						doRefeshOverView = true;
						break;
					case VARIABLE:
						new VariableDao().delete(lightXmlObject.getId(),
								lightXmlObject.getVersion(),
								lightXmlObject.getParentId(),
								lightXmlObject.getParentVersion());
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

					// close open editors
					IEditorReference[] openEditors = PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow().getActivePage()
							.getEditorReferences();
					EditorInput editorInput = null;
					for (int i = 0; i < openEditors.length; i++) {
						editorInput = ((EditorInput) openEditors[i].getEditorInput());
						if (editorInput.getId().equals(
								((LightXmlObjectType) obj).getId())) {
							PlatformUI
									.getWorkbench()
									.getActiveWorkbenchWindow()
									.getActivePage()
									.closeEditor(
											openEditors[i].getEditor(false),
											true);
						}
					}
				} catch (PartInitException ex) {
					DialogUtil
							.errorDialog(
									currentView.getSite().getShell(),
									currentView.ID,
									Translator.trans("ErrorTitle"),
									Translator
											.trans("View.mess.EditorUIDeleteError") + "\n" + ex.getMessage(), ex); //$NON-NLS-1$
				} catch (Exception e) {
					DialogUtil
							.errorDialog(
									currentView.getSite().getShell(),
									currentView.ID,
									Translator.trans("ErrorTitle"),
									Translator
											.trans("View.mess.EditorDeleteError") + "\n" + e.getMessage(), e); //$NON-NLS-1$
				}
				// comment out 20120520 not used -view refreshed instead used
				// removeItemFromMenu(lightXmlObject);
			}
		}

		// refresh info view
		if (doRefeshOverView) {
			ViewManager.getInstance().addViewsToRefresh(Editor.ID);
		}

		// current view
		treeViewer.refresh();
		treeViewer.getControl().setRedraw(true);
		// treeViewer.expandToLevel(2); comment out 20120520
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
