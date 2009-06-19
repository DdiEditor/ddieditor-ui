package org.ddialliance.ddieditor.ui.view;

import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.conceptual.ConceptualElement;
import org.ddialliance.ddieditor.model.conceptual.ConceptualType;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.model.resource.DDIResourceType;
import org.ddialliance.ddieditor.model.resource.StorageType;
import org.ddialliance.ddieditor.persistenceaccess.PersistenceManager;
import org.ddialliance.ddieditor.ui.Activator;
import org.ddialliance.ddieditor.ui.dbxml.QuestionItems;
import org.ddialliance.ddieditor.ui.dbxml.QuestionSchemes;
import org.ddialliance.ddieditor.ui.dbxml.Util;
import org.ddialliance.ddieditor.ui.editor.EditorInput;
import org.ddialliance.ddieditor.ui.editor.EditorInput.EDITOR_MODE_TYPE;
import org.ddialliance.ddieditor.ui.editor.question.QuestionItemEditor;
import org.ddialliance.ddieditor.ui.editor.question.QuestionSchemeEditor;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.ddialliance.ddiftp.util.xml.XmlBeansUtil;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ArmEvent;
import org.eclipse.swt.events.ArmListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.internal.actions.HelpContentsAction;

import com.swtdesigner.ResourceManager;
import com.swtdesigner.SWTResourceManager;

public class InfoFileView extends View {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			InfoFileView.class);
	public static final String ID = "org.ddialliance.ddieditor.ui.view.InfoFileView";
	private List<ConceptualElement> conceptualList;
	public static final List<String> menuLabelList = Arrays.asList("Concept Scheme", "Concept");


	private Action collapseAllAction;
	private Action expandAllAction;
	private Action refreshAction;
	private MenuItem editMenuItem;
	public TreeViewer treeViewer;
	private HelpContentsAction helpContentsAction;

	/**
	 * View Member variable
	 */
	private Tree tree;
	private Text itemText;
	private Properties properties = new Properties();
	final PatternFilter nameFilter = new PatternFilter();

	/**
	 * Constructor
	 */
	public InfoFileView() {
		// TODO Define Info title and description
		super(ViewContentType.QuestionContent, "Info", "Info Description", "Info", "Info", "Info structure",
				menuLabelList);

		// load properties
		try {
			properties.load(new FileInputStream("resources" + File.separator
					+ "ddieditor-ui.properties"));
		} catch (IOException e) {
			System.err.println("Warning:" + e.getMessage());
			System.exit(0);
		}
	}

	public static enum EDITOR_TYPE {
		QUESTION_SCHEME, QUESTION_ITEM
	};

	/**
	 * Opens editor
	 * 
	 * @param mode
	 */
	private void openEditor(EDITOR_MODE_TYPE mode) {
		EditorInput input = null;
		EditorInput.ENTITY_TYPE type = null;

		ISelection selection = treeViewer.getSelection();
		Object obj = ((IStructuredSelection) selection).getFirstElement();

		if (obj instanceof ConceptualElement) {
			LightXmlObjectType item = ((ConceptualElement)obj).getValue();
			if (item.getElement().equals("QuestionScheme")) {
				log.debug("QuestionScheme");
				type = EditorInput.ENTITY_TYPE.QUESTION_SCHEME;
			} 

			input = new EditorInput(item.getId(), item.getVersion(), item
					.getParentId(), item.getParentVersion(), type,
					mode, this, properties);
		}

		// open editor
		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		try {
			switch (type) {
			case CONCEPT_SCHEME:
				// TODO Implement Concept Scheme Editor
				page.openEditor(input, QuestionSchemeEditor.ID);
				break;
			case QUESTION_SCHEME:
				page.openEditor(input, QuestionSchemeEditor.ID);
				break;
			default:
				MessageDialog
				.openError(
						getViewSite().getShell(),
						Messages.getString("ErrorTitle"),
						Messages
								.getString("View.mess.EditorOpenError")+" type: "+obj.getClass().getName()+" is not supported");
				break;
			}

			// Notify any listeners of the view with the actual data of the view
			treeViewer.setSelection(treeViewer.getSelection());
		} catch (PartInitException ex) {
			MessageDialog
					.openError(
							getViewSite().getShell(),
							Messages.getString("ErrorTitle"),
							Messages
									.getString("View.mess.EditorOpenError" + "\n" + ex.getMessage())); 
		}
	}

	/**
	 * Delete action
	 * 
	 * @param type
	 * @param mode
	 */
	private void deleteItem(EDITOR_TYPE type, EDITOR_MODE_TYPE mode) {
		ISelection selection = treeViewer.getSelection();
		Object obj = ((IStructuredSelection) selection).getFirstElement();
		LightXmlObjectType item = (LightXmlObjectType) obj;
		if (MessageDialog.openConfirm(getViewSite().getShell(), Messages
				.getString("ConfirmTitle"), MessageFormat.format(Messages
				.getString("View.mess.ConfirmDeletion"), Util
				.getTextOnMixedElement(item)))) {
			try {
				switch (type) {
				case QUESTION_SCHEME:
					QuestionSchemes.delete(item.getId(), item.getVersion(),
							item.getParentId(), item.getParentVersion());
					break;
				case QUESTION_ITEM:
					QuestionItems.delete(item.getId(), item.getVersion(), item
							.getParentId(), item.getParentVersion());
					break;
				default:
					// TODO error handling
					System.err.println("Editor Type not supported: " + type);
					System.exit(0);
					break;
				}
				firePropertyChange(IEditorPart.PROP_DIRTY);
			} catch (PartInitException ex) {
				MessageDialog
						.openError(
								getViewSite().getShell(),
								Messages.getString("ErrorTitle"),
								Messages
										.getString("View.mess.EditorUIDeleteError") + "\n" + ex.getMessage()); //$NON-NLS-1$
			} catch (Exception e) {
				MessageDialog
						.openError(
								getViewSite().getShell(),
								Messages.getString("ErrorTitle"),
								Messages
										.getString("View.mess.EditorDeleteError") + "\n" + e.getMessage()); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Get list of Light XML objects.
	 * 
	 * @return list of resources
	 * @throws Exception
	 */
	public List<DDIResourceType> getInitialInput() throws Exception {
		log.debug("View.getInitialInput()");
		return PersistenceManager.getInstance().getResources();
	}

	/**
	 * Create contents of the view part and initialize it
	 * 
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {

		log.debug("createPartControl called");

		final Composite composite_2 = new Composite(parent, SWT.NONE);
		composite_2.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));
		composite_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite_2.setLayout(new GridLayout());

		final Composite composite_1 = new Composite(composite_2, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));
		final GridLayout gridLayout_1 = new GridLayout();
		composite_1.setLayout(gridLayout_1);
		composite_1.setBackground(SWTResourceManager.getColor(230, 230, 250));

		final Label questionItemNavigationLabel = new Label(composite_1,
				SWT.WRAP);
		questionItemNavigationLabel.setLayoutData(new GridData(SWT.FILL,
				SWT.CENTER, false, false));
		questionItemNavigationLabel.setBackground(SWTResourceManager.getColor(
				230, 230, 250));
		questionItemNavigationLabel.setFont(SWTResourceManager.getFont("Sans",
				14, SWT.BOLD));
		questionItemNavigationLabel
				.setText(Messages
						.getString("View.label.questionItemNavigationLabel.QuestionItemNavigation")); //$NON-NLS-1$

		final Label selectLabel = new Label(composite_1, SWT.WRAP);
		final GridData gd_selectLabel = new GridData(SWT.FILL, SWT.CENTER,
				false, false);
		gd_selectLabel.widthHint = 468;
		selectLabel.setLayoutData(gd_selectLabel);
		selectLabel.setBackground(SWTResourceManager.getColor(230, 230, 250));
		selectLabel.setText(Messages
				.getString("View.lable.selectLabel.NavigatorDescription")); //$NON-NLS-1$

		// Prepare TreeViewer
		Composite composite = new Composite(composite_2, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		composite.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);

		final Label itemLabel = new Label(composite, SWT.NONE);
		itemLabel.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));
		itemLabel.setText(Messages
				.getString("View.lable.questionItemLabel.QuestionItem")); //$NON-NLS-1$

		itemText = new Text(composite, SWT.BORDER);
		itemText
				.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		itemText.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				if (log.isDebugEnabled()) {
					log.debug("createPartControl.modifyText(): "
							+ itemText.getText());
					log.debug("Debug: " + itemText.getText().length());
				}
				nameFilter.setPattern(itemText.getText());
			}
		});

		// Define group
		final Group treeGroup = new Group(composite, SWT.NONE);
		treeGroup.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));
		final GridLayout gridLayout = new GridLayout();
		treeGroup.setLayout(gridLayout);
		final GridData gd_treeGroup = new GridData(SWT.FILL, SWT.FILL, true,
				true, 2, 1);
		gd_treeGroup.widthHint = 460;
		gd_treeGroup.heightHint = 640;
		treeGroup.setLayoutData(gd_treeGroup);
		treeGroup
				.setText(Messages
						.getString("View.lable.questionItemsTreeGroup.QuestionItemStructure")); //$NON-NLS-1$

		// Define TreeViewer
		treeViewer = new TreeViewer(treeGroup, SWT.SINGLE | SWT.BORDER);
		treeViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(final DoubleClickEvent event) {
				openEditor(EDITOR_MODE_TYPE.EDIT);
			}
		});
		treeViewer.setContentProvider(new TreeContentProvider());
		treeViewer.setLabelProvider(new LabelProvider());
		treeViewer.setLabelProvider(new TreeLabelProvider());
		try {
//			treeViewer.setInput(getInitialInput());
			// TODO zzzzzzzzzzzz
			treeViewer.setInput("olsen");
		} catch (Exception e1) {
			log.error("treeViewer.setInput failed: " + e1.getMessage());
			MessageDialog
					.openInformation(
							getViewSite().getShell(),
							Messages.getString("ErrorTitle"), Messages.getString("View.mess.QuestionItemTreeViewerSetInputError") + ":\n" + e1.getMessage()); //$NON-NLS-1$
		}
		// TODO addFilter causes the Question Items to be retreived once more
		// (retrieved first time by call of getInitialInput())!
		treeViewer.addFilter(nameFilter);
		treeViewer.expandAll();

		// Define Tree
		tree = treeViewer.getTree();
		final GridData gd_tree = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd_tree.heightHint = 540;
		gd_tree.widthHint = 458;
		tree.setLayoutData(gd_tree);

		// Define Tree Pop-up Menu
		final Menu menu = new Menu(tree);
		menu.setDefaultItem(editMenuItem);
		tree.setMenu(menu);

		// Define NEW Pop-up Menu Item
		final MenuItem newMenuItem = new MenuItem(menu, SWT.CASCADE);
		newMenuItem.setSelection(true);
		newMenuItem.setText(Messages
				.getString("View.label.newItemMenuItem.New")); //$NON-NLS-1$
		newMenuItem.setImage(ResourceManager.getPluginImage(Activator
				.getDefault(), "icons/new_wiz.gif"));
		newMenuItem.setData("name", "NEW");
		Menu submenu = new Menu(newMenuItem);
		newMenuItem.setMenu(submenu);

		// Define NEW QUESTION -> SCHEME Pop-up Menu Item
		final MenuItem schemeMenuItem = new MenuItem(submenu, SWT.NONE);
		schemeMenuItem.setSelection(true);
		schemeMenuItem.setText(Messages
				.getString("View.label.questionSchemeMenuItem.QuestionScheme")); //$NON-NLS-1$
		schemeMenuItem.setImage(ResourceManager.getPluginImage(Activator
				.getDefault(), "icons/new_wiz.gif"));
		schemeMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				openEditor(EDITOR_MODE_TYPE.NEW);
			}
		});

		// Define NEW QUESTION -> ITEM Pop-up Menu Item
		final MenuItem newItemMenuItem = new MenuItem(submenu, SWT.NONE);
		newItemMenuItem.setText(Messages
				.getString("View.label.newItemMenuItem.QuestionItem")); //$NON-NLS-1$
		newItemMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				String parentId = "";
				String parentVersion = "";

				ISelection selection = treeViewer.getSelection();
				Object obj = ((IStructuredSelection) selection)
						.getFirstElement();
				LightXmlObjectType item = (LightXmlObjectType) obj;
				IWorkbenchPage page = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage();
				if (item.getElement().equals("QuestionItem")) {
					parentId = item.getParentId();
					parentVersion = item.getParentVersion();
				} else {
					parentId = item.getId();
					parentVersion = item.getVersion();
				}
				// TODO Handling of version not done - currently always 1
				// EditorInput input = new EditorInput(null, "1", parentId,
				// parentVersion, EDITOR_TYPE.QUESTION_ITEM,
				// EDITOR_MODE_TYPE.NEW, properties);
				// try {
				// // TODO Use this.openEditor()
				// page.openEditor(input, QuestionItemEditor.ID);
				// // notify any listeners of the view with the actual data of
				// // the view
				// treeViewer.setSelection(treeViewer.getSelection());
				// } catch (PartInitException ex) {
				// MessageDialog.openError(getViewSite().getShell(), Messages
				// .getString("ErrorTitle"), Messages
				// .getString("View.mess.EditorOpenError")
				//							+ "\n" + ex.getMessage()); //$NON-NLS-1$
				// }
			}
		});

		// Disable "New Question -> Scheme" if a Question Item is selected
		newMenuItem.addArmListener(new ArmListener() {
			public void widgetArmed(final ArmEvent e) {
				ISelection selection = treeViewer.getSelection();
				Object obj = ((IStructuredSelection) selection)
						.getFirstElement();
				LightXmlObjectType item = (LightXmlObjectType) obj;
				IWorkbenchPage page = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage();
				if (item.getElement().equals("QuestionItem")) {
					schemeMenuItem.setEnabled(false);
				}
			}
		});

		newItemMenuItem.setImage(ResourceManager.getPluginImage(Activator
				.getDefault(), "icons/new_wiz.gif"));

		// Define DELETE Pop-up Menu Item
		final MenuItem deleteMenuItem = new MenuItem(menu, SWT.NONE);
		deleteMenuItem.setText(Messages
				.getString("View.label.deleteMenuItem.Delete")); //$NON-NLS-1$
		deleteMenuItem.setImage(ResourceManager.getPluginImage(Activator
				.getDefault(), "icons/delete_obj.gif"));
		deleteMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				TreeItem[] t = tree.getSelection();
				for (int i = 0; i < t.length; i++) {
					log.debug(t[i].getText() + ", ");
					log.debug(t[i].getData("parentId"));
				}
				// TODO Distinguish between Question Schemes and Items
				deleteItem(EDITOR_TYPE.QUESTION_ITEM, EDITOR_MODE_TYPE.EDIT);
			}
		});

		// Define EDIT Pop-up Menu Item
		editMenuItem = new MenuItem(menu, SWT.NONE);
		editMenuItem
				.setText(Messages.getString("View.label.editMenuItem.Edit")); //$NON-NLS-1$
		editMenuItem.setImage(ResourceManager.getPluginImage(Activator
				.getDefault(), "icons/editor_area.gif"));
		editMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				TreeItem[] t = tree.getSelection();
				if (t.length != 1) {
					MessageDialog
							.openInformation(
									getViewSite().getShell(),
									Messages.getString("InfoTitle"), Messages.getString("Editor.mess.NotSupported")); //$NON-NLS-1$
					return;
				}
				openEditor(EDITOR_MODE_TYPE.EDIT);
			}
		});

		createActions();
		initializeToolBar();
		initializeMenu();
	}

	/**
	 * Create the actions on tree viewer
	 */
	private void createActions() {

		expandAllAction = new Action(Messages
				.getString("View.label.expandAllAction.ExpandAll")) { //$NON-NLS-1$)
			public void run() {
				treeViewer.expandAll();
			}
		};
		expandAllAction.setImageDescriptor(ResourceManager
				.getPluginImageDescriptor(Activator.getDefault(),
						"icons/expand_all.gif"));

		collapseAllAction = new Action(Messages
				.getString("View.label.collapseAllAction.CollapseAll")) { //$NON-NLS-1$)
			public void run() {
				treeViewer.collapseAll();
			}
		};
		collapseAllAction.setImageDescriptor(ResourceManager
				.getPluginImageDescriptor(Activator.getDefault(),
						"icons/collapse_all.gif"));

		refreshAction = new Action("Refresh") {
			public void run() {
				treeViewer.refresh(false);
			}
		};

		refreshAction.setImageDescriptor(ResourceManager
				.getPluginImageDescriptor(Activator.getDefault(),
						"icons/refresh.gif"));

		helpContentsAction = new HelpContentsAction();

		// Create the actions
	}

	/**
	 * Initialise the tool bar
	 */
	private void initializeToolBar() {
		IToolBarManager toolbarManager = getViewSite().getActionBars()
				.getToolBarManager();

		toolbarManager.add(new Separator());

		toolbarManager.add(expandAllAction);

		toolbarManager.add(collapseAllAction);

		toolbarManager.add(new Separator());

		toolbarManager.add(refreshAction);

		toolbarManager.add(new Separator());

		toolbarManager.add(helpContentsAction);
	}

	/**
	 * Initialise the menu
	 */
	private void initializeMenu() {
		IMenuManager menuManager = getViewSite().getActionBars()
				.getMenuManager();

		menuManager.add(helpContentsAction);
	}

	@Override
	public void setFocus() {
		// Set the focus
	}

	/**
	 * Ligth XML Object - Tree Viewer Content provider
	 */
	class TreeContentProvider implements IStructuredContentProvider,
			ITreeContentProvider {

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			System.out.println("TreeContentProvider.inputChanged()");
		}

		public void dispose() {

		}

		/**
		 * Get root elements.
		 * 
		 * @param Input
		 *            Element
		 * @return Object[]
		 */
		public Object[] getElements(Object inputElement) {
			DDIResourceType[] array = {};
			try {
				List<DDIResourceType> list = PersistenceManager.getInstance()
						.getResources();
				array = new DDIResourceType[list.size()];
				array = list.toArray(array);
			} catch (Exception e) {
				new DDIFtpException("Error in retrieve resources", e);
			}
			return array;
		}

		/**
		 * Get child elements of parent.
		 * 
		 * @param Parent
		 *            Element
		 * @return Object[]
		 */
		public Object[] getChildren(Object parentElement) {
			// default
			Object[] guard = new Object[0];

			// list storages
			// if (parentElement instanceof StorageType) {
			// StorageType storage = (StorageType) parentElement;
			// DDIResourceType[] array = new DDIResourceType[storage
			// .getDDIResourceList().size()];
			// return storage.getDDIResourceList().toArray(array);
			// }
			
			// list conceptual types
			if (parentElement instanceof DDIResourceType) {
				// TODO add to hashmap resource id ~ conceptual overview
				// add lookup in cach list!
				if (conceptualList == null) {
					try {
						PersistenceManager.getInstance().setWorkingResource(
								((DDIResourceType) parentElement).getOrgName());

						conceptualList = DdiManager.getInstance()
								.getConceptualOverview();
					} catch (DDIFtpException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				}
				List<ConceptualType> list = new ArrayList<ConceptualType>();
				for (ConceptualElement conceptualElement : conceptualList) {
					if (!list.contains(conceptualElement.getType())) {
						list.add(conceptualElement.getType());
					}
				}
				ConceptualType[] result = new ConceptualType[list.size()];
				return list.toArray(result);
			}
			// list conceptual elements
			else if (parentElement instanceof ConceptualType) {
				try {
					List<ConceptualElement> list = new ArrayList<ConceptualElement>();
					for (ConceptualElement conceptualElement : conceptualList) {
						if (conceptualElement.getType().equals(parentElement)) {
							list.add(conceptualElement);
						}
					}
					ConceptualElement[] array = new ConceptualElement[list
							.size()];
					return list.toArray(array);
				} catch (Exception e) {
					// TODO msg view
					e.printStackTrace();
				}
				return guard;
			}
			// guard
			else {
				if (log.isDebugEnabled()) {
					log.debug("No children for: "
							+ (parentElement == null ? "null" : parentElement
									.getClass().getName()) + "\n"
							+ parentElement);
				}
				return guard;
			}
		}

		/**
		 * @param element
		 *            Element
		 * @return Object Parent Element
		 */
		public Object getParent(Object element) {
			return null;
		}

		public boolean hasChildren(Object element) {
			return getChildren(element).length > 0;
		}

		public void propertyChange(PropertyChangeEvent arg0) {
			System.out.println("TreeContentProvider.propertyChange()");
		}
	}

	/**
	 * Ligth XML Object - Tree Viewer Label provider
	 */
	class TreeLabelProvider extends LabelProvider {

		public String getText(Object element) {
			if (element instanceof DDIResourceType) {
				return ((DDIResourceType) element).getOrgName();
			} else if (element instanceof StorageType) {
				return ((StorageType) element).getId();
			} else if (element instanceof ConceptualType) {
				return element.toString();
			} else if (element instanceof ConceptualElement) {
				List<org.ddialliance.ddieditor.model.lightxmlobject.LabelType> labels = ((ConceptualElement) element)
						.getValue().getLabelList();
				if (!labels.isEmpty()) {
					return XmlBeansUtil
							.getTextOnMixedElement(((ConceptualElement) element)
									.getValue().getLabelList().get(0));
				} else {
					return ((ConceptualElement) element).getValue().getId();
				}
			} else {
				// guard
				if (log.isDebugEnabled()) {
					log.debug("No label defined for: "
							+ (element == null ? "null" : element.getClass()
									.getName()) + "\n" + element);
				}
				return "- na -";
			}
		}

		public Image getImage(Object element) {
			return null;
		}
	}
}
