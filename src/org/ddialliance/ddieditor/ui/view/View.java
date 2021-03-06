package org.ddialliance.ddieditor.ui.view;

import org.ddialliance.ddieditor.ui.Activator;
import org.ddialliance.ddieditor.ui.model.ElementType;
import org.ddialliance.ddieditor.ui.util.swtdesigner.ResourceManager;
import org.ddialliance.ddieditor.ui.util.swtdesigner.SWTResourceManager;
import org.ddialliance.ddiftp.util.Translator;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.part.ViewPart;

/**
 * Generic View
 */
public class View extends ViewPart implements IPropertyListener {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, View.class);

	public enum ViewContentType {
		StudyLevelContent, UniverseContent, ConceptContent, CodeContent, CategoryContent, QuestionContent, InstrumentationContent, VariableContent;
	}

	private ViewContentType viewContentType;
	private String viewTitle = "viewTitle";
	private String viewDescr = "viewDescr";
	private String viewEntityName = "viewEntityName";
	private ElementType rootElement;
	private String viewTreeLabel = "viewTreeLabel";
	protected View currentView = this;
	private Action collapseAllAction;
	private Action expandAllAction;
	private Action refreshAction;
	public TreeViewer treeViewer;
	private Tree tree;
	private Text filterText;
	final PatternFilter nameFilter = new PatternFilter();
	public String ID = "org.ddialliance.ddieditor.ui.view.View";

	/**
	 * Constructor
	 * 
	 * @param viewContentType
	 *            -e.g. QuestionContent
	 * @param viewTitle
	 *            - e.g. Question Item Navigation
	 * @param viewDescr
	 *            - e.g. Select a Question Item and choose a function for ....
	 * @param viewEntityName
	 *            - e.g. Question
	 * @param rootElement
	 *            - e.g. QuestionScheme
	 * @param viewTreeLabel
	 *            - e.g. Question Structure
	 * @param subElements
	 *            List of Pop-up Menu Labels e.g. "Question Scheme",
	 *            "Question Item" If null no Pop-up Menu is created.
	 * @param viewID
	 *            ID of extending view.
	 */
	public View(ViewContentType viewContentType, String viewTitle,
			String viewDescr, String viewEntityName, ElementType rootElement,
			String viewTreeLabel, String viewID) {

		this.viewContentType = viewContentType;
		this.viewTitle = viewTitle;
		this.viewDescr = viewDescr;
		this.viewEntityName = viewEntityName;
		this.viewTreeLabel = viewTreeLabel;
		this.rootElement = rootElement;
		this.ID = viewID;
	}

	@Override
	public void addPartPropertyListener(IPropertyChangeListener listener) {
		super.addPartPropertyListener(listener);
	}

	private void refreshTreeViewer(TreeViewer treeViewer) {
		treeViewer.refresh();
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
		if (log.isDebugEnabled()) {
			log.debug("Generic createPartControl called");
		}
		parent.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				// check if refresh is needed
				ViewManager.getInstance().refesh();
			}
		});
		parent.setLayout(new GridLayout());
		parent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));

		final Composite titleComposite = new Composite(parent, SWT.NONE);
		titleComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));
		final GridLayout gridLayout_1 = new GridLayout();
		titleComposite.setLayout(gridLayout_1);
		titleComposite
				.setBackground(SWTResourceManager.getColor(230, 230, 250));

		final Label navigationTitle = new Label(titleComposite, SWT.WRAP);
		navigationTitle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));
		navigationTitle.setBackground(SWTResourceManager
				.getColor(230, 230, 250));
		navigationTitle.setFont(SWTResourceManager
				.getFont("Sans", 14, SWT.BOLD));
		navigationTitle.setText(viewTitle);

		final Label selectLabel = new Label(titleComposite, SWT.WRAP);
		selectLabel.setRedraw(true);
		selectLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));
		selectLabel.setBackground(SWTResourceManager.getColor(230, 230, 250));
		selectLabel.setText(viewDescr);

		// Prepare TreeViewer
		Composite treeViewerCompsite = new Composite(parent, SWT.NONE);
		treeViewerCompsite.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, true));
		treeViewerCompsite.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		treeViewerCompsite.setLayout(layout);

		final Label viewEntityNameLabel = new Label(treeViewerCompsite,
				SWT.NONE);
		viewEntityNameLabel.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));
		viewEntityNameLabel.setText(viewEntityName);

		filterText = new Text(treeViewerCompsite, SWT.BORDER);
		filterText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false));
		filterText.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				// on a CR we want to filter
				if (e.keyCode == SWT.CR) {
					nameFilter.setPattern(filterText.getText());
					treeViewer.refresh();
					treeViewer.getTree().setFocus();
				}
			}
		});

		// Define group
		final Group treeGroup = new Group(treeViewerCompsite, SWT.NONE);
		treeGroup.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));
		final GridLayout gridLayout = new GridLayout();
		treeGroup.setLayout(gridLayout);
		treeGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2,
				1));
		treeGroup.setText(viewTreeLabel);

		// Define TreeViewer
		treeViewer = new TreeViewer(treeGroup, SWT.SINGLE | SWT.BORDER);
		treeViewer.setContentProvider(new TreeContentProvider(getViewSite()));
		treeViewer.setLabelProvider(new TreeLabelProvider());
		treeViewer.setComparer(new XmlObjectComparer());
		try {
			treeViewer.setInput(viewContentType);
		} catch (Exception e1) {
			log.error("treeViewer.setInput failed: " + e1.getMessage());
			MessageDialog
					.openInformation(
							getViewSite().getShell(),
							Translator.trans("ErrorTitle"), Translator.trans("View.mess.TreeViewerSetInputError") + ":\n" + e1.getMessage()); //$NON-NLS-1$
		}
		
		treeViewer.addFilter(nameFilter);		 
		
		// hack to avoid wrong resource ids on level 2 items
		if (ID.equals(InfoView.ID)) {
			treeViewer.expandToLevel(3);
			refreshView();
			treeViewer.collapseAll();
		} else {
			treeViewer.expandToLevel(2);
		}
		
		// Define Tree
		tree = treeViewer.getTree();
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// Define Tree Pop-up Menu
		TreeMenuProvider treeMenuProvider = new TreeMenuProvider(treeViewer,
				currentView, rootElement);
		treeMenuProvider.setMenu();

		createActions();
		initializeToolBar();
		initializeMenu();
	}

	/**
	 * Create the actions
	 */
	private void createActions() {
		expandAllAction = new Action(
				Translator.trans("View.label.expandAllAction.ExpandAll")) { //$NON-NLS-1$)
			public void run() {
				treeViewer.expandAll();
			}
		};
		expandAllAction.setImageDescriptor(ResourceManager
				.getPluginImageDescriptor(Activator.getDefault(),
						"icons/expand_all.gif"));

		collapseAllAction = new Action(
				Translator.trans("View.label.collapseAllAction.CollapseAll")) { //$NON-NLS-1$)
			public void run() {
				treeViewer.collapseAll();
			}
		};
		collapseAllAction.setImageDescriptor(ResourceManager
				.getPluginImageDescriptor(Activator.getDefault(),
						"icons/collapse_all.gif"));

		refreshAction = new Action(
				Translator.trans("View.label.collapseAllAction.Refresh")) { //$NON-NLS-1$)
			public void run() {
				refreshView();
			}
		};

		refreshAction.setImageDescriptor(ResourceManager
				.getPluginImageDescriptor(Activator.getDefault(),
						"icons/refresh.gif"));
	}

	/**
	 * Initialize the tool bar
	 */
	private void initializeToolBar() {
		IToolBarManager toolbarManager = getViewSite().getActionBars()
				.getToolBarManager();
		toolbarManager.add(new Separator());
		toolbarManager.add(expandAllAction);
		toolbarManager.add(collapseAllAction);
		toolbarManager.add(new Separator());
		toolbarManager.add(refreshAction);
	}

	/**
	 * Initialise the menu
	 */
	private void initializeMenu() {
		IMenuManager menuManager = getViewSite().getActionBars()
				.getMenuManager();
	}

	@Override
	public void setFocus() {
		// enable access to tree from keyboard
		treeViewer.getControl().forceFocus();
	}

	@Override
	public void propertyChanged(Object source, int propId) {
		// if (propId == IEditorPart.PROP_INPUT) {
		// refreshView();
		// }
	}
}
