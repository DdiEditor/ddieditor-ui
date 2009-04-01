package org.ddialliance.ddieditor.ui.view;

import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Properties;

import org.apache.xmlbeans.XmlCursor;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.model.lightxmlobject.impl.LightXmlObjectTypeImpl;
import org.ddialliance.ddieditor.ui.Activator;
import org.ddialliance.ddieditor.ui.dbxml.QuestionItems;
import org.ddialliance.ddieditor.ui.dbxml.QuestionSchemes;
import org.ddialliance.ddieditor.ui.dbxml.Util;
import org.ddialliance.ddieditor.ui.editor.EditorInput;
import org.ddialliance.ddieditor.ui.editor.QuestionItemEditor;
import org.ddialliance.ddieditor.ui.editor.QuestionSchemeEditor;
import org.ddialliance.ddieditor.ui.editor.EditorInput.EDITOR_MODE_TYPE;
import org.ddialliance.ddieditor.ui.editor.EditorInput.EDITOR_TYPE;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
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
import org.eclipse.ui.part.ViewPart;

import com.swtdesigner.ResourceManager;
import com.swtdesigner.SWTResourceManager;

public class View extends ViewPart {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, View.class);
	private Action collapseAllAction;
	private Action expandAllAction;
	private Action refreshAction;
	private MenuItem editMenuItem;
	public TreeViewer treeViewer;
	private HelpContentsAction helpContentsAction;

	/**
	 * Ligth XML Object - Tree Viewer Content provider
	 */
	class TreeContentProvider implements IStructuredContentProvider, ITreeContentProvider {

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
			log.debug("Get Element with Id: " + ((List<LightXmlObjectType>) inputElement).get(0).getId());
			// return ((List<LightXmlObjectType>) inputElement).toArray();
			try {
				return QuestionSchemes.getQuestionSchemesLight(
						((List<LightXmlObjectType>) inputElement).get(0).getId(),
						((List<LightXmlObjectType>) inputElement).get(0).getId()).toArray();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		/**
		 * Get child elements of parent.
		 * 
		 * @param Parent
		 *            Element
		 * @return Object[]
		 */
		public Object[] getChildren(Object parentElement) {
			log.debug("Get Child of Parent Id: " + ((LightXmlObjectType) parentElement).getId());
			if (parentElement instanceof LightXmlObjectType) {
				LightXmlObjectType lightXmlObjectType = (LightXmlObjectType) parentElement;
				if (!lightXmlObjectType.getElement().equals("QuestionScheme")) {
					return (new Object[0]);
				}

				try {
					log.debug("Read children");
					return QuestionItems.getQuestionItemsLight(lightXmlObjectType).toArray();
				} catch (Exception e) {
					MessageDialog.openError(getViewSite().getShell(), Messages.getString("ErrorTitle"), Messages
							.getString("View.mess.GetChildError")
							+ "\n" + e.getMessage()); //$NON-NLS-1$
				}
			}
			return (new Object[0]);
		}

		/**
		 * @param element
		 *            Element
		 * @return Object Parent Element
		 */
		public Object getParent(Object element) {
			System.out.println("TreeContentProvider.getParent()");
			return null;
		}

		public boolean hasChildren(Object element) {
			System.out.println("TreeContentProvider.hasChildren()");
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

			// TODO Get Label of default language - not just the first one
			if (element instanceof LightXmlObjectTypeImpl) {
				LightXmlObjectType lightXmlObjectType = (LightXmlObjectType) element;
				log.debug("Get Text of Element with Id: " + lightXmlObjectType.getId());
				if (lightXmlObjectType.getLabelList().size() > 0) {
					XmlCursor xmlCursor = lightXmlObjectType.getLabelList().get(0).newCursor();
					xmlCursor.toLastAttribute();
					xmlCursor.toNextToken();
					String result = xmlCursor.getChars();
					xmlCursor.dispose();
					log.debug("Text: " + result);
					return (result);
				}
				return (Messages.getString("View.mess.NoLabelSpecified")); //$NON-NLS-1$
			}
			return new String();
		}

		public Image getImage(Object element) {
			return null;
		}
	}

	/**
	 * View Member variable
	 */
	private Tree tree;
	private Text questionItemText;
	private Properties properties = new Properties();
	final PatternFilter questionItemNameFilter = new PatternFilter();
	public static final String ID = "org.ddialliance.ddieditor.ui.view.View";

	/**
	 * Constructor
	 */
	public View() {
		super();

		try {
			properties.load(new FileInputStream("resources" + File.separator + "ddieditor-ui.properties"));
		} catch (IOException e) {
			System.err.println("Warning:" + e.getMessage());
			System.exit(0);
		}
	}

	private void openEditor(EDITOR_MODE_TYPE mode) {
		EDITOR_TYPE type = null;

		ISelection selection = treeViewer.getSelection();
		Object obj = ((IStructuredSelection) selection).getFirstElement();
		LightXmlObjectType item = (LightXmlObjectType) obj;

		if (item.getElement().equals("QuestionScheme")) {
			System.out.println("QuestionScheme");
			type = EDITOR_TYPE.QUESTION_SCHEME;
		} else if (item.getElement().equals("QuestionItem")) {
			System.out.println("QuestionItem");
			type = EDITOR_TYPE.QUESTION_ITEM;
		} else {
			// TODO Error handling
			System.err.println("Element Type not supported: " + obj);
			System.exit(0);
			;

		}
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		EditorInput input = new EditorInput(item.getId(), item.getVersion(), item.getParentId(), item
				.getParentVersion(), type, mode, properties);
		try {
			switch (type) {
			case QUESTION_SCHEME:
				page.openEditor(input, QuestionSchemeEditor.ID);
				break;
			case QUESTION_ITEM:
				page.openEditor(input, QuestionItemEditor.ID);
				break;
			default:
				// TODO error handling
				System.err.println("Editor Type not supported: " + type);
				System.exit(0);
				break;
			}

			// Notify any listeners of the view with the actual data of the view
			treeViewer.setSelection(treeViewer.getSelection());
		} catch (PartInitException ex) {
			MessageDialog.openError(getViewSite().getShell(), Messages.getString("ErrorTitle"), Messages
					.getString("View.mess.EditorOpenError" + "\n" + ex.getMessage())); //$NON-NLS-1$
		}
	}

	private void deleteItem(EDITOR_TYPE type, EDITOR_MODE_TYPE mode) {
		ISelection selection = treeViewer.getSelection();
		Object obj = ((IStructuredSelection) selection).getFirstElement();
		LightXmlObjectType item = (LightXmlObjectType) obj;
		if (MessageDialog.openConfirm(getViewSite().getShell(), Messages.getString("ConfirmTitle"), MessageFormat
				.format(Messages.getString("View.mess.ConfirmDeletion"), Util.getTextOnMixedElement(item)))) {
			try {
				switch (type) {
				case QUESTION_SCHEME:
					QuestionSchemes
							.delete(item.getId(), item.getVersion(), item.getParentId(), item.getParentVersion());
					break;
				case QUESTION_ITEM:
					QuestionItems.delete(item.getId(), item.getVersion(), item.getParentId(), item.getParentVersion());
					break;
				default:
					// TODO error handling
					System.err.println("Editor Type not supported: " + type);
					System.exit(0);
					break;
				}
				firePropertyChange(IEditorPart.PROP_DIRTY);
			} catch (PartInitException ex) {
				MessageDialog.openError(getViewSite().getShell(), Messages.getString("ErrorTitle"), Messages
						.getString("View.mess.EditorUIDeleteError") + "\n" + ex.getMessage()); //$NON-NLS-1$
			} catch (Exception e) {
				MessageDialog.openError(getViewSite().getShell(), Messages.getString("ErrorTitle"), Messages
						.getString("View.mess.EditorDeleteError") + "\n" + e.getMessage()); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Get list of Ligth XML objects.
	 * 
	 * @return List<LightXmlObjectType>
	 * @throws Exception
	 */
	public List<LightXmlObjectType> getInitialInput() throws Exception {
		log.debug("View.getInitialInput()");

		List<LightXmlObjectType> questionScheme = new QuestionSchemes().getQuestionSchemesLight();

		return questionScheme;
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
		composite_2.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		composite_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite_2.setLayout(new GridLayout());

		final Composite composite_1 = new Composite(composite_2, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		final GridLayout gridLayout_1 = new GridLayout();
		composite_1.setLayout(gridLayout_1);
		composite_1.setBackground(SWTResourceManager.getColor(230, 230, 250));

		final Label questionItemNavigationLabel = new Label(composite_1, SWT.WRAP);
		questionItemNavigationLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		questionItemNavigationLabel.setBackground(SWTResourceManager.getColor(230, 230, 250));
		questionItemNavigationLabel.setFont(SWTResourceManager.getFont("Sans", 14, SWT.BOLD));
		questionItemNavigationLabel.setText(Messages
				.getString("View.label.questionItemNavigationLabel.QuestionItemNavigation")); //$NON-NLS-1$

		final Label selectLabel = new Label(composite_1, SWT.WRAP);
		final GridData gd_selectLabel = new GridData(SWT.FILL, SWT.CENTER, false, false);
		gd_selectLabel.widthHint = 468;
		selectLabel.setLayoutData(gd_selectLabel);
		selectLabel.setBackground(SWTResourceManager.getColor(230, 230, 250));
		selectLabel.setText(Messages.getString("View.lable.selectLabel.NavigatorDescription")); //$NON-NLS-1$

		// Prepare TreeViewer
		Composite composite = new Composite(composite_2, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		composite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);

		final Label questionItemLabel = new Label(composite, SWT.NONE);
		questionItemLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		questionItemLabel.setText(Messages.getString("View.lable.questionItemLabel.QuestionItem")); //$NON-NLS-1$

		questionItemText = new Text(composite, SWT.BORDER);
		questionItemText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		questionItemText.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				if (log.isDebugEnabled()) {
					log.debug("createPartControl.modifyText(): " + questionItemText.getText());
					log.debug("Debug: " + questionItemText.getText().length());
				}
				questionItemNameFilter.setPattern(questionItemText.getText());
			}
		});

		// Define group
		final Group questionItemsTreeGroup = new Group(composite, SWT.NONE);
		questionItemsTreeGroup.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		final GridLayout gridLayout = new GridLayout();
		questionItemsTreeGroup.setLayout(gridLayout);
		final GridData gd_questionItemsTreeGroup = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gd_questionItemsTreeGroup.widthHint = 460;
		gd_questionItemsTreeGroup.heightHint = 640;
		questionItemsTreeGroup.setLayoutData(gd_questionItemsTreeGroup);
		questionItemsTreeGroup.setText(Messages.getString("View.lable.questionItemsTreeGroup.QuestionItemStructure")); //$NON-NLS-1$

		// Define TreeViewer
		treeViewer = new TreeViewer(questionItemsTreeGroup, SWT.SINGLE | SWT.BORDER);
		treeViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(final DoubleClickEvent event) {
				openEditor(EDITOR_MODE_TYPE.EDIT);
			}
		});
		treeViewer.setContentProvider(new TreeContentProvider());
		treeViewer.setLabelProvider(new LabelProvider());
		treeViewer.setLabelProvider(new TreeLabelProvider());
		try {
			treeViewer.setInput(getInitialInput());
		} catch (Exception e1) {
			log.error("treeViewer.setInput failed: " + e1.getMessage());
			MessageDialog
					.openInformation(
							getViewSite().getShell(),
							Messages.getString("ErrorTitle"), Messages.getString("View.mess.QuestionItemTreeViewerSetInputError") + ":\n" + e1.getMessage()); //$NON-NLS-1$
		}
		// TODO addFilter causes the Question Items to be retreived once more
		// (retrieved first time by call of getInitialInput())!
		treeViewer.addFilter(questionItemNameFilter);
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
		newMenuItem.setText(Messages.getString("View.label.newItemMenuItem.New")); //$NON-NLS-1$
		newMenuItem.setImage(ResourceManager.getPluginImage(Activator.getDefault(), "icons/new_wiz.gif"));
		newMenuItem.setData("name", "NEW");
		Menu submenu = new Menu(newMenuItem);
		newMenuItem.setMenu(submenu);

		// Define NEW QUESTION -> SCHEME Pop-up Menu Item
		final MenuItem questionSchemeMenuItem = new MenuItem(submenu, SWT.NONE);
		questionSchemeMenuItem.setSelection(true);
		questionSchemeMenuItem.setText(Messages.getString("View.label.questionSchemeMenuItem.QuestionScheme")); //$NON-NLS-1$
		questionSchemeMenuItem.setImage(ResourceManager.getPluginImage(Activator.getDefault(), "icons/new_wiz.gif"));
		questionSchemeMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				openEditor(EDITOR_MODE_TYPE.NEW);
			}
		});

		// Define NEW QUESTION -> ITEM Pop-up Menu Item
		final MenuItem newItemMenuItem = new MenuItem(submenu, SWT.NONE);
		newItemMenuItem.setText(Messages.getString("View.label.newItemMenuItem.QuestionItem")); //$NON-NLS-1$
		newItemMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				String parentId = "";
				String parentVersion = "";

				ISelection selection = treeViewer.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();
				LightXmlObjectType item = (LightXmlObjectType) obj;
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				if (item.getElement().equals("QuestionItem")) {
					parentId = item.getParentId();
					parentVersion = item.getParentVersion();
				} else {
					parentId = item.getId();
					parentVersion = item.getVersion();
				}
				// TODO Handling of version not done - currently always 1
				EditorInput input = new EditorInput(null, "1", parentId, parentVersion, EDITOR_TYPE.QUESTION_ITEM,
						EDITOR_MODE_TYPE.NEW, properties);
				try {
					// TODO Use this.openEditor()
					page.openEditor(input, QuestionItemEditor.ID);
					// notify any listeners of the view with the actual data of
					// the view
					treeViewer.setSelection(treeViewer.getSelection());
				} catch (PartInitException ex) {
					MessageDialog.openError(getViewSite().getShell(), Messages.getString("ErrorTitle"), Messages
							.getString("View.mess.EditorOpenError")
							+ "\n" + ex.getMessage()); //$NON-NLS-1$
				}
			}
		});

		// Disable "New Question -> Scheme" if a Question Item is selected
		newMenuItem.addArmListener(new ArmListener() {
			public void widgetArmed(final ArmEvent e) {
				ISelection selection = treeViewer.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();
				LightXmlObjectType item = (LightXmlObjectType) obj;
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				if (item.getElement().equals("QuestionItem")) {
					questionSchemeMenuItem.setEnabled(false);
				}
			}
		});

		newItemMenuItem.setImage(ResourceManager.getPluginImage(Activator.getDefault(), "icons/new_wiz.gif"));

		// Define DELETE Pop-up Menu Item
		final MenuItem deleteMenuItem = new MenuItem(menu, SWT.NONE);
		deleteMenuItem.setText(Messages.getString("View.label.deleteMenuItem.Delete")); //$NON-NLS-1$
		deleteMenuItem.setImage(ResourceManager.getPluginImage(Activator.getDefault(), "icons/delete_obj.gif"));
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
		editMenuItem.setText(Messages.getString("View.label.editMenuItem.Edit")); //$NON-NLS-1$
		editMenuItem.setImage(ResourceManager.getPluginImage(Activator.getDefault(), "icons/editor_area.gif"));
		editMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				TreeItem[] t = tree.getSelection();
				if (t.length != 1) {
					MessageDialog.openInformation(getViewSite().getShell(),
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
	 * Create the actions
	 */
	private void createActions() {

		expandAllAction = new Action(Messages.getString("View.label.expandAllAction.ExpandAll")) { //$NON-NLS-1$)
			public void run() {
				treeViewer.expandAll();
			}
		};
		expandAllAction.setImageDescriptor(ResourceManager.getPluginImageDescriptor(Activator.getDefault(),
				"icons/expand_all.gif"));

		collapseAllAction = new Action(Messages.getString("View.label.collapseAllAction.CollapseAll")) { //$NON-NLS-1$)
			public void run() {
				treeViewer.collapseAll();
			}
		};
		collapseAllAction.setImageDescriptor(ResourceManager.getPluginImageDescriptor(Activator.getDefault(),
				"icons/collapse_all.gif"));

		refreshAction = new Action("Refresh") {
			public void run() {
				treeViewer.refresh(false);
			}
		};

		refreshAction.setImageDescriptor(ResourceManager.getPluginImageDescriptor(Activator.getDefault(),
				"icons/refresh.gif"));

		helpContentsAction = new HelpContentsAction();

		// Create the actions
	}

	/**
	 * Initialise the tool bar
	 */
	private void initializeToolBar() {
		IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();

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
		IMenuManager menuManager = getViewSite().getActionBars().getMenuManager();

		menuManager.add(helpContentsAction);
	}

	@Override
	public void setFocus() {
		// Set the focus
	}

}
