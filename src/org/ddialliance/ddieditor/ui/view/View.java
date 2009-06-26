package org.ddialliance.ddieditor.ui.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Properties;

import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.Activator;
import org.ddialliance.ddieditor.ui.dbxml.ConceptSchemes;
import org.ddialliance.ddieditor.ui.dbxml.Concepts;
import org.ddialliance.ddieditor.ui.dbxml.QuestionItems;
import org.ddialliance.ddieditor.ui.dbxml.QuestionSchemes;
import org.ddialliance.ddieditor.ui.dbxml.Util;
import org.ddialliance.ddieditor.ui.editor.EditorInput;
import org.ddialliance.ddieditor.ui.editor.EditorInput.EDITOR_MODE_TYPE;
import org.ddialliance.ddieditor.ui.editor.EditorInput.ENTITY_TYPE;
import org.ddialliance.ddieditor.ui.editor.concept.ConceptEditor;
import org.ddialliance.ddieditor.ui.editor.concept.ConceptSchemeEditor;
import org.ddialliance.ddieditor.ui.editor.question.QuestionItemEditor;
import org.ddialliance.ddieditor.ui.editor.question.QuestionSchemeEditor;
import org.ddialliance.ddieditor.ui.model.ConceptScheme;
import org.ddialliance.ddieditor.ui.util.ResourceManager;
import org.ddialliance.ddieditor.ui.util.SWTResourceManager;
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
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.internal.actions.HelpContentsAction;
import org.eclipse.ui.part.ViewPart;

public class View extends ViewPart {
	/**
	 * View Member variable
	 */
	private static Log log = LogFactory.getLog(LogType.SYSTEM, View.class);

	public enum ViewContentType {
		StudyContent, UniverseContent, ConceptContent, QuestionContent, InstumentationContent;
	}

	private ViewContentType viewContentType;
	private String viewTitle = "viewTitle";
	private String viewDescr = "viewDescr";
	private String viewEntityName = "viewEntityName";
	private String rootElementName;
	private String viewTreeLabel = "viewTreeLabel";
	protected View currentView = this;
	private List<String> menuLabels;
	private Action collapseAllAction;
	private Action expandAllAction;
	private Action refreshAction;
	private MenuItem editMenuItem;
	public TreeViewer treeViewer;
	private HelpContentsAction helpContentsAction;
	private Tree tree;
	private Text filterText;
	private Properties properties = new Properties();
	final PatternFilter nameFilter = new PatternFilter();
	public static final String ID = "org.ddialliance.ddieditor.ui.view.View";

	/**
	 * Constructor
	 * 
	 * @param viewContentType 
	 * 			-e.g. QuestionContent
	 * @param viewTitle 
	 * 			- e.g. Question Item Navigation
	 * @param viewDescr
	 * 			- e.g. Select a Question Item and choose a function for ....
	 * @param viewEntityName
	 * 			- e.g. Question
	 * @param rootElementName
	 * 			- e.g. QuestionScheme
	 * @param viewTreeLabel
	 * 			- e.g. Question Structure
	 * @param menuLabels
	 * 			List of Menu Labels e.g. "Question Scheme", "Question Item"
	 */
	public View(ViewContentType viewContentType, String viewTitle, String viewDescr, String viewEntityName, String rootElementName,
			String viewTreeLabel, List<String> menuLabels) {

		this.viewContentType = viewContentType;
		this.viewTitle = viewTitle;
		this.viewDescr = viewDescr;
		this.viewEntityName = viewEntityName;
		this.rootElementName = rootElementName;
		this.viewTreeLabel = viewTreeLabel;
		this.menuLabels = menuLabels;

		try {
			properties.load(new FileInputStream("resources" + File.separator + "ddieditor-ui.properties"));
		} catch (IOException e) {
			System.err.println("Error during property load:" + e.getMessage());
			System.exit(0);
		}
	}
	
	private void refreshTreeViewer(TreeViewer treeViewer) {
		treeViewer.getControl().setRedraw(false);
		treeViewer.refresh();
		treeViewer.getControl().setRedraw(true);
		treeViewer.expandAll();
		treeViewer.getTree().setFocus();
	}
	
	public void refreshView() {
		refreshTreeViewer(treeViewer);
	}
	
	public void set(TreeContentProvider contentProvider) {
		treeViewer.setContentProvider(contentProvider);
	}
	
	public void setLabelProvider(LabelProvider labelProvider) {
		treeViewer.setLabelProvider(labelProvider);
	}

	/**
	 * Create contents of the view part and initialize it
	 * 
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {

		log.debug("Generic createPartControl called");

		final Composite composite_2 = new Composite(parent, SWT.NONE);
		composite_2.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		composite_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite_2.setLayout(new GridLayout());

		final Composite composite_1 = new Composite(composite_2, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		final GridLayout gridLayout_1 = new GridLayout();
		composite_1.setLayout(gridLayout_1);
		composite_1.setBackground(SWTResourceManager.getColor(230, 230, 250));

		final Label navigationTitle = new Label(composite_1, SWT.WRAP);
		navigationTitle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		navigationTitle.setBackground(SWTResourceManager.getColor(230, 230, 250));
		navigationTitle.setFont(SWTResourceManager.getFont("Sans", 14, SWT.BOLD));
		navigationTitle.setText(viewTitle);

		final Label selectLabel = new Label(composite_1, SWT.WRAP);
		final GridData gd_selectLabel = new GridData(SWT.FILL, SWT.CENTER, false, false);
		gd_selectLabel.widthHint = 468;
		selectLabel.setLayoutData(gd_selectLabel);
		selectLabel.setBackground(SWTResourceManager.getColor(230, 230, 250));
		selectLabel.setText(viewDescr);

		// Prepare TreeViewer
		Composite composite = new Composite(composite_2, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		composite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);

		final Label viewEntityNameLabel = new Label(composite, SWT.NONE);
		viewEntityNameLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		viewEntityNameLabel.setText(viewEntityName);

		filterText = new Text(composite, SWT.BORDER);
		filterText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		filterText.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				// on a CR we want to filter
				if (e.keyCode == SWT.CR) {
					nameFilter.setPattern(filterText.getText());
					treeViewer.getControl().setRedraw(false);
					treeViewer.refresh();
					treeViewer.getControl().setRedraw(true);
					treeViewer.expandAll();
					treeViewer.getTree().setFocus();
				}
			}
		});

		// Define group
		final Group treeGroup = new Group(composite, SWT.NONE);
		treeGroup.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		final GridLayout gridLayout = new GridLayout();
		treeGroup.setLayout(gridLayout);
		final GridData gd_treeGroup = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gd_treeGroup.widthHint = 460;
		gd_treeGroup.heightHint = 640;
		treeGroup.setLayoutData(gd_treeGroup);
		treeGroup.setText(viewTreeLabel);

		// Define TreeViewer
		treeViewer = new TreeViewer(treeGroup, SWT.SINGLE | SWT.BORDER);
		treeViewer.setContentProvider(new TreeContentProvider(getViewSite()));
		treeViewer.setLabelProvider(new LabelProvider());
		treeViewer.setLabelProvider(new TreeLabelProvider());
		try {
			treeViewer.setInput(viewContentType);
		} catch (Exception e1) {
			log.error("treeViewer.setInput failed: " + e1.getMessage());
			MessageDialog
					.openInformation(
							getViewSite().getShell(),
							Messages.getString("ErrorTitle"), Messages.getString("View.mess.QuestionItemTreeViewerSetInputError") + ":\n" + e1.getMessage()); //$NON-NLS-1$
		}
		treeViewer.addFilter(nameFilter);
		treeViewer.expandAll();

		// Define Tree
		tree = treeViewer.getTree();
		final GridData gd_tree = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd_tree.heightHint = 540;
		gd_tree.widthHint = 458;
		tree.setLayoutData(gd_tree);

		// Define Tree Pop-up Menu
		TreeMenuProvider treeMenuProvider = new TreeMenuProvider(treeViewer, currentView, viewEntityName, rootElementName, menuLabels, properties);
		treeMenuProvider.setMenu();

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
				treeViewer.getControl().setRedraw(false);
				treeViewer.refresh();
				treeViewer.getControl().setRedraw(true);
				treeViewer.expandAll();
			}
		};

		refreshAction.setImageDescriptor(ResourceManager.getPluginImageDescriptor(Activator.getDefault(),
				"icons/refresh.gif"));

		helpContentsAction = new HelpContentsAction();

	}

	/**
	 * Initialize the tool bar
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
	 * Initialize the menu
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
