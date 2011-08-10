package org.ddialliance.ddieditor.ui.editor;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.xmlbeans.XmlObject;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.ConstructNameDocument;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.ItemSequenceTypeType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.DateType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.LabelType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.NameType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.ReferenceType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.StructuredStringType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.VersionRationaleDocument;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.persistenceaccess.PersistenceManager;
import org.ddialliance.ddieditor.ui.Activator;
import org.ddialliance.ddieditor.ui.dbxml.IDao;
import org.ddialliance.ddieditor.ui.dbxml.question.QuestionItemDao;
import org.ddialliance.ddieditor.ui.dialogs.TranslationDialog;
import org.ddialliance.ddieditor.ui.dialogs.TranslationDialogInput;
import org.ddialliance.ddieditor.ui.editor.EditorInput.EditorModeType;
import org.ddialliance.ddieditor.ui.editor.widgetutil.genericmodifylistener.TextStyledTextModyfiListener;
import org.ddialliance.ddieditor.ui.editor.widgetutil.referenceselection.ReferenceSelectionCombo;
import org.ddialliance.ddieditor.ui.editor.widgetutil.tab.DDITabItemAction;
import org.ddialliance.ddieditor.ui.editor.widgetutil.tab.PreviewTabItemAction;
import org.ddialliance.ddieditor.ui.editor.widgetutil.tab.PropertyTabItemAction;
import org.ddialliance.ddieditor.ui.editor.widgetutil.tab.TabFolderListener;
import org.ddialliance.ddieditor.ui.model.ElementType;
import org.ddialliance.ddieditor.ui.model.ILabelDescription;
import org.ddialliance.ddieditor.ui.model.IModel;
import org.ddialliance.ddieditor.ui.model.translationdialoginput.DescriptionTdI;
import org.ddialliance.ddieditor.ui.model.translationdialoginput.LabelTdI;
import org.ddialliance.ddieditor.ui.perspective.IAutoChangePerspective;
import org.ddialliance.ddieditor.ui.preference.PreferenceConstants;
import org.ddialliance.ddieditor.ui.util.DialogUtil;
import org.ddialliance.ddieditor.ui.util.LanguageUtil;
import org.ddialliance.ddieditor.ui.util.swtdesigner.SWTResourceManager;
import org.ddialliance.ddieditor.ui.view.ViewManager;
import org.ddialliance.ddieditor.util.DdiEditorConfig;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.Translator;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.ddialliance.ddiftp.util.xml.XmlBeansUtil;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TypedListener;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;

/**
 * Super class editor defining common initialization and holds widget creation<br>
 * <br>
 * DDI element editors are to extend this super class
 */
public class Editor extends EditorPart implements IAutoChangePerspective {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, Editor.class);

	public static String ID = "org.ddialliance.ddieditor.ui.editor.Editor";
	private final String ORG_ID = "org.ddialliance.ddieditor.ui.editor.Editor";
	public EditorStatus editorStatus = new EditorStatus();

	private String title = "";
	private String description = "";

	private Composite composite;
	private TabFolder tabFolder;
	private TabItem labelDescriptionTabItem;

	public static String NEW_ITEM = "new_item";
	public static String CONTROL_ID = "control_id";
	public static String TAB_ID = "id";
	public static String DDI_TAB_ID = "ddi";
	public static String PREVIEW_TAB_ID = "preview";
	public static String PROPERTY_TAB_ID = "property";

	protected IModel model;
	protected IDao dao;
	protected EditorInput editorInput;

	protected Group labelDescriptionTabGroup; // May be used for expanding Label

	// Description Tab content

	/**
	 * Default constructor. Usage to gain access to create widget methods <br>
	 * Note: Builds an empty editor input.
	 * 
	 * @wbp.parser.constructor
	 */
	public Editor() {
	}

	/**
	 * Constructor
	 * 
	 * @param title
	 *            title
	 * @param description
	 *            description
	 * @param editorID
	 *            ID of extending editor
	 */
	public Editor(String title, String description, String editorID) {
		this.title = title;
		this.description = description;
		this.ID = editorID;
	}

	public IModel getModel() {
		return model;
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		this.editorInput = (EditorInput) input;

		// working resource
		try {
			PersistenceManager.getInstance().setWorkingResource(
					editorInput.getResourceId());
		} catch (DDIFtpException e) {
			throw new PartInitException(
					Translator.trans("editor.init.error.create"), e);
		}

		// edit mode
		if (editorInput.getEditorMode().equals(EditorModeType.NEW)) {
			try {
				model = dao.create("", "", editorInput.getParentId(),
						editorInput.getParentVersion());
			} catch (Exception e) {
				throw new PartInitException(
						Translator.trans("editor.init.error.create"),
						new DDIFtpException(e));
			} catch (Throwable t) {
				DDIFtpException e = new DDIFtpException(
						Translator.trans("editor.init.error.create"));
				e.setRealThrowable(t);
				throw new PartInitException(
						Translator.trans("editor.init.error.create"), e);
			}
		} else if (editorInput.getEditorMode().equals(EditorModeType.EDIT)
				|| editorInput.getEditorMode().equals(EditorModeType.VIEW)) {
			try {
				if (editorInput.getElementType() == ElementType.QUESTION_ITEM) {
					((QuestionItemDao) dao).setParentElementType(editorInput
							.getParentElementType());
				}

				model = dao.getModel(editorInput.getId(),
						editorInput.getVersion(), editorInput.getParentId(),
						editorInput.getParentVersion());
			} catch (Exception e) {
				throw new PartInitException(
						Translator.trans("editor.init.error.retrieval"),
						new DDIFtpException(e));
			}
		} else {
			throw new PartInitException(
					Translator.trans("editor.init.error.editmodeunsupported"),
					new DDIFtpException());
		}

		// update input
		editorInput.setId(model.getId());
		// check for change in getLightElements vs getElement in DDIManager
		if (!(editorInput.getVersion() != null && model.getVersion() == null)) {
			editorInput.setVersion(model.getVersion());
		}
		editorInput.setParentId(model.getParentId());
		editorInput.setParentVersion(model.getParentVersion());
		setInput(editorInput);

		// part name
		if (editorInput.getLabelList() == null
				|| editorInput.getLabelList().isEmpty()) {
			setPartName(model.getId());
		} else {
			try {
				org.ddialliance.ddieditor.model.lightxmlobject.LabelType name = (org.ddialliance.ddieditor.model.lightxmlobject.LabelType) XmlBeansUtil
						.getDefaultLangElement(editorInput.getLabelList());
				if (name != null) {
					setPartName(XmlBeansUtil.getTextOnMixedElement(name));
				}
			} catch (DDIFtpException e) {
				setPartName(model.getId());
			}
		}
	}

	public void createPartControl(Composite parent) {
		final GridData gdParent = new GridData(SWT.FILL, SWT.FILL, true, true);
		gdParent.widthHint = 539;
		gdParent.heightHint = 573;

		parent.setLayout(new GridLayout());
		parent.setLayoutData(gdParent);
		parent.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));
		parent.setRedraw(true);
		composite = parent;

		final Composite titleComposite = new Composite(parent, SWT.NONE);
		titleComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		titleComposite
				.setBackground(SWTResourceManager.getColor(230, 230, 250));
		titleComposite.setLayout(new GridLayout());

		// title
		final Label titleLabel = new Label(titleComposite, SWT.WRAP);
		titleLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		titleLabel.setFont(SWTResourceManager.getFont("Sans", 14, SWT.BOLD));

		titleLabel.setBackground(SWTResourceManager.getColor(230, 230, 250));
		titleLabel.setText(title);

		// description
		final Label descriptionLabel = new Label(titleComposite, SWT.WRAP);
		final GridData gd_descriptionLabel = new GridData(SWT.FILL, SWT.CENTER,
				true, false);
		gd_descriptionLabel.widthHint = 471;
		descriptionLabel.setLayoutData(gd_descriptionLabel);
		descriptionLabel.setBackground(SWTResourceManager.getColor(230, 230,
				250));
		descriptionLabel.setText(description);

		// clean dirt from initialization
		editorStatus.clearChanged();
	}

	/**
	 * The EditorStatus class keeps track of the Editor status
	 */
	public class EditorStatus {
		private boolean changed = false;

		public void setChanged() {
			changed = true; // Set 'changed' before fire the property change!
			firePropertyChange(IEditorPart.PROP_DIRTY);
		}

		public boolean getStatus() {
			return changed;
		}

		public void clearChanged() {
			changed = false; // Set 'changed' before fire the property change!
			firePropertyChange(IEditorPart.PROP_DIRTY);
		}
	}

	@Override
	public boolean isDirty() {
		return editorStatus.getStatus();
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		try {
			model.validate();
		} catch (Exception e) {
			// Save with warning
			DialogUtil.infoDialog(getSite().getShell(), ID,
					Translator.trans("InfoTitle"), e.getMessage());
		}
		try {
			if (getEditorInputImpl().getEditorMode().equals(EditorModeType.NEW)) {
				if (getEditorInputImpl().getElementType() == ElementType.QUESTION_ITEM
						|| getEditorInputImpl().getElementType() == ElementType.SUB_QUESTION_ITEM) {
					((QuestionItemDao) dao)
							.setParentElementType(getEditorInputImpl()
									.getParentElementType());
				}
				dao.create(model);
				getEditorInputImpl().setEditorMode(EditorModeType.EDIT);
			} else if (getEditorInputImpl().getEditorMode().equals(
					EditorModeType.EDIT)) {
				dao.update(model);
			} else if (getEditorInputImpl().getEditorMode().equals(
					EditorModeType.VIEW)) {
				if (log.isDebugEnabled()) {
					log.debug("View mode: Save ignored");
				}
			}
		} catch (Exception e) {
			DialogUtil.errorDialog((IEditorSite) getSite(), ID,
					Translator.trans("ErrorTitle"),
					Translator.trans("Editor.mess.ErrorDuringSave"), e);
			return;
		}

		// add views to refresh list
		if (!ID.equals(ORG_ID)) {
			ViewManager.getInstance().addViewsToRefresh(ID);
			ViewManager.getInstance().refesh();
		}
		editorStatus.clearChanged();
	}

	@Override
	public void doSaveAs() {
		MessageDialog
				.openError(
						getSite().getShell(),
						Translator.trans("ErrorTitle"), Translator.trans("Editor.mess.SaveAsNotSupported")); //$NON-NLS-1$
	}

	@Override
	public boolean isSaveAsAllowed() {
		// save as not supported
		return false;
	}

	@Override
	public void setFocus() {
		composite.setFocus();

		// TODO hmm a lot ..., for tracking change who is calling who ...
		if (false) {// if (log.isDebugEnabled()) {
			Throwable t = new Throwable();
			for (int i = 0; i < t.getStackTrace().length; i++) {
				log.debug(t.getStackTrace()[i].getClassName() + "."
						+ t.getStackTrace()[i].getMethodName());
			}
		}

		// set working resource
		try {
			PersistenceManager.getInstance().setWorkingResource(
					getEditorInputImpl().getResourceId());
		} catch (DDIFtpException e) {
			DialogUtil.errorDialog((IEditorSite) getSite(), ID,
					Translator.trans("ErrorTitle"),
					Translator.trans("editor.init.error.create"), e);
		}
	}

	public EditorInput getEditorInputImpl() {
		return (EditorInput) super.getEditorInput();
	}

	/**
	 * Create error dialog displaying exception
	 * 
	 * @param e
	 *            exception to display
	 */
	public void showError(Exception e) {
		showError(e, ID, getSite().getShell());
	}

	public void setEditorTabName(String name) {
		setPartName(name);
	}
	
	/**
	 * Create error dialog displaying exception
	 * 
	 * @param e
	 *            exception to display
	 * @param id
	 *            of view part causing exception
	 * @param site
	 *            ui parent
	 */
	public static void showError(Exception e, String id, IWorkbenchPartSite site) {
		showError(e, id, site.getShell());
	}

	/**
	 * Create error dialog displaying exception <br>
	 * <br>
	 * The parent ui part is not accessible a shell parent for the dialog is
	 * retrieved from display active shell.
	 * 
	 * @param e
	 *            exception to display
	 * @param id
	 *            of view part causing exception
	 */
	public static void showError(Exception e, String id) {
		showError(e, id, PlatformUI.getWorkbench().getDisplay()
				.getActiveShell());
	}

	private static void showError(Exception e, String id, Shell shell) {
		// exception
		DDIFtpException ddiFtpException = null;
		if (!(e instanceof DDIFtpException)) {
			ddiFtpException = new DDIFtpException(e);
		} else {
			ddiFtpException = (DDIFtpException) e;
		}

		// id
		if (id == null || id.equals("")) {
			id = "Not defined";
		}

		// shell
		if (shell == null) {
			shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
		}

		// dialog
		DialogUtil.errorDialog(shell, ID, e.getMessage(),
				ddiFtpException.getMessage(), ddiFtpException);
	}

	// -----------------------------------------------------------------------
	// composite, tab folder and item operations
	// -----------------------------------------------------------------------
	protected Composite getComposite_1() {
		return composite;
	}

	public void setControl(Control widget) {
		if (getEditorInputImpl() != null
				&& getEditorInputImpl().getEditorMode().equals(
						EditorModeType.VIEW)) {
			widget.setEnabled(false);
		}
	}

	public TabFolder createTabFolder(Composite parent) {
		tabFolder = new TabFolder(parent, SWT.BOTTOM);
		tabFolder.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1));
		tabFolder.addSelectionListener(new TabFolderListener());
		return tabFolder;
	}

	public TabFolder getTabFolder() {
		return tabFolder;
	}

	public void setTabFolder(TabFolder tabFolder) {
		this.tabFolder = tabFolder;
	}

	public TabItem createTabItem(String tabText) {
		TabItem tabItem = new TabItem(getTabFolder(), SWT.NONE);
		tabItem.setText(tabText);
		return tabItem;
	}

	public Group createGroup(TabItem tabItem, String groupText) {
		Group group = new Group(getTabFolder(), SWT.NONE);
		group.setText(groupText);
		group.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		tabItem.setControl(group);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		group.setLayout(gridLayout);
		return group;
	}

	public Group createGroup(Composite composite, String groupText) {
		Group group = new Group(composite, SWT.NONE);
		group.setText(groupText);
		group.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		group.setLayout(gridLayout);

		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		return group;
	}

	public Group createSubGroup(Group group, String groupText) {
		Group subGroup = new Group(group, SWT.NONE);
		subGroup.setText(groupText);
		subGroup.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		subGroup.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, true, false, 2,
				1));
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		subGroup.setLayout(gridLayout);

		return subGroup;
	}

	public void createPropertiesTab(TabFolder tabFolder) {
		TabItem tabItem = createTabItem(Translator
				.trans("Editor.label.propertiesTabItem.Properties"));
		Group group = createGroup(tabItem,
				Translator.trans("Editor.label.propertiesGroup.Properties"));

		boolean isMaintainable = false;
		try {
			isMaintainable = DdiManager.getInstance().getDdi3NamespaceHelper()
					.isMaintainable(model.getDocument());
		} catch (DDIFtpException e) {
			DialogUtil.errorDialog(group.getShell(), ID,
					"Maintainable check error", e.getMessage(), e);
		}

		// action
		createLabel(group, Translator.trans("Editor.label.action.Action"));
		Combo actionCombo = createCombo(group, new String[] { "1", "2", "3" });

		// urn
		StyledText urnText = createTextAreaInput(group,
				Translator.trans("Editor.label.urnLabel.URN"), "", null);
		urnText.setEditable(false);

		// agency
		Text agencyText = null;
		if (isMaintainable) {
			createLabel(group,
					Translator.trans("Editor.label.agencyLabel.agency"));
			agencyText = createText(group, "", null);
			agencyText.setEditable(false);
		}

		// id
		createLabel(group, Translator.trans("Editor.label.idLabel.ID"));
		Text idText = createText(group, "", null);
		idText.setEditable(false);

		// version
		boolean isVersionable = false;
		try {
			isVersionable = DdiManager.getInstance().getDdi3NamespaceHelper()
					.isVersionable(model.getDocument());
		} catch (DDIFtpException e) {
			DialogUtil.errorDialog(group.getShell(), ID,
					"Versionable check error", e.getMessage(), e);
		}

		Text versionText = null;
		Text versionResponsibilityText = null;
		Text versionDate = null;
		StyledText versionRationaleText = null;
		if (isVersionable) {
			// version
			createLabel(group,
					Translator.trans("Editor.label.versionGroup.Version"));
			versionText = createText(group, "", null);
			versionText.setEditable(false);

			// version responsibility
			createLabel(
					group,
					Translator.trans("Editor.label.responsibelLabel.Responsibel"));
			versionResponsibilityText = createText(group, "", null);
			versionResponsibilityText.setEditable(false);

			// version date
			createLabel(group,
					Translator.trans("Editor.label.versionDateLabel.Date"));
			versionDate = createText(group, "", null);
			versionDate.setEditable(false);

			// version rationale
			versionRationaleText = createTextAreaInput(group,
					Translator.trans("Editor.label.versionrationale"), "",
					null);
			versionRationaleText
					.addModifyListener(new TextStyledTextModyfiListener(model,
							VersionRationaleDocument.class,
							getEditorIdentification()));
		}

		// update on tab item click
		tabItem.setData(TAB_ID, PROPERTY_TAB_ID);
		PropertyTabItemAction action = new PropertyTabItemAction(ID, model,
				group.getShell(), actionCombo, urnText, agencyText, idText,
				versionText, versionResponsibilityText, versionDate,
				versionRationaleText);
		Listener[] list = getTabFolder().getListeners(SWT.Selection);
		Object obj = null;
		for (int i = 0; i < list.length; i++) {
			if (list[i] instanceof TypedListener) {
				obj = ((TypedListener) list[i]).getEventListener();
				if (obj instanceof TabFolderListener) {
					((TabFolderListener) obj).actionMap.put(PROPERTY_TAB_ID,
							action);
				}
				break;
			}
		}
	}

	public StyledText createXmlTab(IModel model) {
		TabItem tabItem = createTabItem(Translator
				.trans("editor.tabitem.ddi"));
		Group group = createGroup(tabItem,
				Translator.trans("editor.group.ddi"));
		StyledText styledText = createTextAreaInput(group,
				Translator.trans("editor.label.ddi"), "", false);
		// styledText.addModifyListener(new TextStyledTextModyfiListener(model,
		// sometype.class, getEditorIdentification()));

		// update on tab item click
		tabItem.setData(TAB_ID, DDI_TAB_ID);
		DDITabItemAction action = new DDITabItemAction(DDI_TAB_ID, model,
				styledText);
		Listener[] list = getTabFolder().getListeners(SWT.Selection);
		Object obj = null;
		for (int i = 0; i < list.length; i++) {
			if (list[i] instanceof TypedListener) {
				obj = ((TypedListener) list[i]).getEventListener();
				if (obj instanceof TabFolderListener) {
					((TabFolderListener) obj).actionMap.put(DDI_TAB_ID, action);
				}
				break;
			}
		}
		return styledText;
	}

	public void createPreviewTab(IModel model) {
		TabItem tabItem = createTabItem(Translator
				.trans("editor.tabitem.preview"));
		Group group = createGroup(tabItem,
				Translator.trans("editor.group.preview"));
		Browser browser = createBrowser(group,
				Translator.trans("editor.label.preview"));

		// update on tab item click
		tabItem.setData(TAB_ID, PREVIEW_TAB_ID);
		if (browser != null) {
			PreviewTabItemAction action;
			try {
				action = new PreviewTabItemAction(PREVIEW_TAB_ID, model,
						browser);
			} catch (DDIFtpException e) {
				DialogUtil.errorDialog(group.getShell(), ID,
						"Error on get display lable", e.getMessage(), e);
				return;
			}
			Listener[] list = getTabFolder().getListeners(SWT.Selection);
			Object obj = null;
			for (int i = 0; i < list.length; i++) {
				if (list[i] instanceof TypedListener) {
					obj = ((TypedListener) list[i]).getEventListener();
					if (obj instanceof TabFolderListener) {
						((TabFolderListener) obj).actionMap.put(PREVIEW_TAB_ID,
								action);
					}
					break;
				}
			}
		}
	}

	public void createLabelDescriptionTab(Composite parent,
			String editorEntityName, final ILabelDescription labelDescription) {
		Composite simpleRootComposite = new Composite(parent, SWT.NONE);
		simpleRootComposite.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));
		final GridLayout gridLayout = new GridLayout();
		simpleRootComposite.setLayout(gridLayout);

		// - Simple Tab Item:
		labelDescriptionTabItem = new TabItem(getTabFolder(), SWT.NONE);
		labelDescriptionTabItem.setControl(simpleRootComposite);
		labelDescriptionTabItem.setText(editorEntityName);

		// - Simple Group
		final Group group = createGroup(labelDescriptionTabItem,
				editorEntityName);
		labelDescriptionTabGroup = group;

		// Simple Label:
		final Label labelLabel = new Label(group, SWT.NONE);
		final GridData gd_conceptLabel = new GridData(SWT.RIGHT, SWT.CENTER,
				false, false);
		gd_conceptLabel.horizontalIndent = 5;
		labelLabel.setLayoutData(gd_conceptLabel);
		labelLabel.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));
		labelLabel.setText(Translator.trans("SimpleEditor.label.Label")); //$NON-NLS-1$

		final Text labelText = new Text(group, SWT.BORDER);
		final GridData gd_labelText = new GridData(SWT.FILL, SWT.CENTER, true,
				false);
		labelText.setLayoutData(gd_labelText);
		try {
			labelText.setText(labelDescription.getDisplayLabel());
			if (this.editorInput.getEditorMode().equals(EditorModeType.VIEW)) {
				labelText.setEnabled(false);
			}
		} catch (DDIFtpException e) {
			DialogUtil.errorDialog(group.getShell(), ID,
					"Error on get display lable", e.getMessage(), e);
			return;
		}
		labelText.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				try {
					labelDescription.setDisplayLabel(labelText.getText());
					setPartName(labelText.getText());
				} catch (DDIFtpException e1) {
					showError(e1);
				}
				editorStatus.setChanged();
			}
		});

		// translation
		try {
			createTranslation(group,
					Translator.trans("editor.button.translate"),
					labelDescription.getLabels(), new LabelTdI(), "", labelText);
		} catch (DDIFtpException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// Simple Description:
		final Label simpleDescrLabel = new Label(group, SWT.NONE);
		final GridData gd_simpleDescrLabel = new GridData(SWT.RIGHT, SWT.TOP,
				false, false);
		gd_simpleDescrLabel.horizontalIndent = 5;
		simpleDescrLabel.setLayoutData(gd_simpleDescrLabel);
		simpleDescrLabel.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));
		simpleDescrLabel.setText(Translator
				.trans("SimpleEditor.label.DescriptionText.Label")); //$NON-NLS-1$

		final StyledText simpleDescrStyledText = new StyledText(group, SWT.WRAP
				| SWT.V_SCROLL | SWT.BORDER);
		try {
			simpleDescrStyledText.setText(labelDescription.getDisplayDescr());
			if (this.editorInput.getEditorMode().equals(EditorModeType.VIEW)) {
				simpleDescrStyledText.setEnabled(false);
			}
		} catch (DDIFtpException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		final GridData gd_originalConceptTextStyledText = new GridData(
				SWT.FILL, SWT.CENTER, true, false);
		gd_originalConceptTextStyledText.heightHint = 154;
		gd_originalConceptTextStyledText.widthHint = 308;
		simpleDescrStyledText.setLayoutData(gd_originalConceptTextStyledText);
		simpleDescrStyledText.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				try {
					labelDescription.setDisplayDescr(simpleDescrStyledText
							.getText());
					editorStatus.setChanged();
				} catch (DDIFtpException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		// translation
		try {
			createTranslation(group,
					Translator.trans("editor.button.translate"),
					labelDescription.getDescrs(), new DescriptionTdI(), "",
					simpleDescrStyledText);
		} catch (DDIFtpException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public Group getLabelDescriptionTabGroup() {
		return labelDescriptionTabGroup;
	}

	public TabItem getLabelDescriptionTabItem() {
		return labelDescriptionTabItem;
	}

	public void setLabelDescriptionTabItem(TabItem labelDescriptionTabItem) {
		this.labelDescriptionTabItem = labelDescriptionTabItem;
	}

	// -----------------------------------------------------------------------
	// widget operations
	// -----------------------------------------------------------------------
	public Label createLabel(Group group, String labelText) {
		return createLabelImpl(new Label(group, SWT.NONE), labelText);
	}

	public Label createLabel(Composite composite, String labelText) {
		return createLabelImpl(new Label(composite, SWT.NONE), labelText);
	}

	private Label createLabelImpl(Label label, String labelText) {
		label.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		label.setText(labelText);
		return label;
	}

	public Text createText(Group group, String initText, Boolean isNew) {
		Text text = new Text(group, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text.setText(initText);
		text.setData(NEW_ITEM, isNew);
		setControl(text);
		return text;
	}

	public Text createText(Composite composite, String initText) {
		Text text = new Text(composite, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text.setText(initText);
		setControl(text);
		return text;
	}

	public Button createButton(Composite composite, String buttonText) {
		Button button = new Button(composite, 0);
		button.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				2, 1));
		button.setText(buttonText);
		return button;
	}

	public Button createCheckBox(Composite composite, String label,
			String buttonText) {
		createLabel(composite, label);
		Button check = new Button(composite, SWT.CHECK);
		check.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		check.setText(buttonText);
		check.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));
		return check;
	}

	public Combo createCombo(Composite composite, String[] options) {
		final Combo combo = new Combo(composite, SWT.READ_ONLY);
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));
		combo.setItems(options);
		return combo;
	}
	
	public String getDefaultCodeProgrammingLanguage() {
		String programmingLanguage = "";
		if (((EditorInput) getEditorInput()).mode
				.equals(EditorInput.EditorModeType.NEW)) {
			programmingLanguage = Activator.getDefault().getPreferenceStore()
					.getString(DdiEditorConfig.DDI_INSTRUMENT_PROGRAM_LANG);
		}
		return programmingLanguage;
	}

	public static String[] getSequenceOptions() {
		String[] sequenceOptions = { "",
				ItemSequenceTypeType.IN_ORDER_OF_APPEARANCE.toString(),
				ItemSequenceTypeType.RANDOM.toString(),
				ItemSequenceTypeType.ROTATE.toString(),
				ItemSequenceTypeType.OTHER.toString() };
		return sequenceOptions;
	}

	public static int defineItemSequenceSelection(String itemSequence) {
		String[] sequenceOptions = getSequenceOptions();
		for (int i = 0; i < sequenceOptions.length; i++) {
			if (itemSequence.equals(sequenceOptions[i])) {
				return i;
			}
		}
		return -1;
	}

	public Combo createSequenceCombo(Group group, String labelText,
			Integer focus) {
		createLabel(group, labelText);
		String[] sequenceOptions = getSequenceOptions();
		Combo combo = createCombo(group, sequenceOptions);
		if (focus != null && focus > -1 && focus < sequenceOptions.length) {
			combo.select(focus);
		}
		return combo;
	}

	public Text createNameInput(Group group, String labelText,
			List<NameType> nameList, String parentLabel) throws DDIFtpException {
		NameType name = (NameType) XmlBeansUtil.getLangElement(
				LanguageUtil.getDisplayLanguage(), nameList);

		Text nameTxt = createTextInput(group, labelText, name == null ? ""
				: name.getStringValue(), name == null ? Boolean.TRUE
				: Boolean.FALSE);

		if (name == null) {
			name = ConstructNameDocument.Factory.newInstance()
					.addNewConstructName();
			name.setTranslatable(true);
			name.setTranslated(!nameList.isEmpty());
			name.setLang(LanguageUtil.getOriginalLanguage());
		}

		nameTxt.addModifyListener(new NameTypeModyfiListener(name, nameList,
				editorStatus));

		// createTranslation(group,
		// Translator.trans("editor.button.translate"),
		// nameList, parentLabel);

		return nameTxt;
	}

	public Text createLabelInput(Group group, String labelText,
			List<LabelType> labelList, String parentLabel) {
		LabelType label = null;
		try {
			label = (LabelType) XmlBeansUtil.getLangElement(
					LanguageUtil.getDisplayLanguage(), labelList);
		} catch (DDIFtpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Text text = createTextInput(group, labelText, label == null ? ""
				: XmlBeansUtil.getTextOnMixedElement(label),
				label == null ? Boolean.TRUE : Boolean.FALSE);

		if (label == null) {
			label = org.ddialliance.ddi3.xml.xmlbeans.reusable.LabelDocument.Factory
					.newInstance().addNewLabel();
			label.setTranslatable(true);
			label.setTranslated(!labelList.isEmpty());
			label.setLang(LanguageUtil.getOriginalLanguage());
		}

		text.addModifyListener(new LabelTypeModyfiListener(label, labelList,
				this));

		return text;
	}

	public StyledText createStructuredStringInput(Group group,
			String labelText, List<StructuredStringType> structuredStringList,
			String parentLabel) throws DDIFtpException {
		StructuredStringType structuredString = (StructuredStringType) XmlBeansUtil
				.getLangElement(LanguageUtil.getDisplayLanguage(),
						structuredStringList);

		StyledText styledText = createTextAreaInput(
				group,
				labelText,
				structuredString == null ? "" : XmlBeansUtil
						.getTextOnMixedElement(structuredString),
				structuredString == null ? Boolean.TRUE : Boolean.FALSE);

		if (structuredString == null) {
			structuredString = StructuredStringType.Factory.newInstance();
			structuredString.setTranslatable(true);
			if (structuredStringList == null || structuredStringList.isEmpty()) {
				structuredString.setTranslated(false);
			} else {
				structuredString.setTranslated(true);
			}
			structuredString.setLang(LanguageUtil.getOriginalLanguage());
		}
		styledText.addModifyListener(new StructuredStringTypeModyfiListener(
				structuredString, structuredStringList, editorStatus));

		// createTranslation(group,
		// Translator.trans("editor.button.translate"),
		// structuredStringList, parentLabel);
		return styledText;
	}

	/**
	 * 
	 * @param group
	 * @param buttonText
	 * @param items
	 * @param translationDialogOption
	 * @param parentLabel
	 * @param editor
	 *            Editor implementing IEditor interface
	 * @return
	 */
	public Button createTranslation(Group group, String buttonText,
			final List items,
			final TranslationDialogInput translationDialogOption,
			final String parentLabel, Widget parentWidget) {
		Button button = createButton(group, buttonText);
		button.addSelectionListener(createTranslationSelectionListener(items,
				translationDialogOption, parentLabel, parentWidget));
		return button;
	}

	private SelectionListener createTranslationSelectionListener(
			final List items,
			final TranslationDialogInput translationDialogOption,
			final String parentLabel, final Widget parentWidget) {
		SelectionListener listener = new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				// compile org strings as list
				List<CacheHolder> cache = new ArrayList<CacheHolder>();
				for (Object object : items) {
					try {
						// commented out in revision 1546
						// cache.add(new CacheHolder(XmlBeansUtil
						// .getTextOnMixedElement((XmlObject) object),
						// (String) ReflectionUtil.invokeMethod(object,
						// "getLang", false, null),
						// (Boolean) ReflectionUtil.invokeMethod(object,
						// "getTranslated", false, null),
						// (Boolean) ReflectionUtil.invokeMethod(object,
						// "getTranslatable", false, null)));

						String lang = XmlBeansUtil.getXmlAttributeValue(
								((XmlObject) object).xmlText(), "lang=\"");
						if (lang == null) {
							lang = "";
						}
						String translatable = XmlBeansUtil
								.getXmlAttributeValue(((XmlObject) object)
										.xmlText().toString(),
										"translatable=\"");
						if (translatable == null) {
							translatable = "true";
						}
						String translated = XmlBeansUtil.getXmlAttributeValue(
								((XmlObject) object).xmlText().toString(),
								"translated=\"");
						if (translated == null) {
							translated = "false";
						}

						cache.add(new CacheHolder(XmlBeansUtil
								.getTextOnMixedElement((XmlObject) object),
								lang, translatable.equals("true") ? true
										: false,
								translated.equals("true") ? true : false));
					} catch (Exception e1) {
						DialogUtil
								.errorDialog(
										(IEditorSite) getSite(),
										ID,
										Translator.trans("ErrorTitle"),
										Translator.trans("Editor.mess.InternalError"),
										e1);
						return;
					}
				}

				TranslationDialog translationDialog = new TranslationDialog(
						event.display.getActiveShell(), editorStatus, items,
						translationDialogOption, parentLabel, parentWidget);
				translationDialog.open();
				if (translationDialog.getReturnCode() == Window.OK) {
					for (int i = 0; i < items.size(); i++) {
						// remove empty items
						if (XmlBeansUtil.getTextOnMixedElement(
								(XmlObject) items.get(i)).equals("")) {
							items.remove(i);
						}
					}
					updateParentWidget(translationDialog);
				} else {
					for (Iterator iterator = items.iterator(); iterator
							.hasNext();) {
						Object object2 = (Object) iterator.next();

					}
					// restore original items
					for (Iterator iteratorItems = items.iterator(), iteratorCache = cache
							.iterator(); iteratorCache.hasNext();) {
						Object object = iteratorItems.next();
						CacheHolder cacheHolder = (CacheHolder) iteratorCache
								.next();
						try {
							// restore
							translationDialog.setXmlText(object,
									cacheHolder.text);
							translationDialog.setXmlLang(object,
									cacheHolder.lang);
							translationDialog.setTranslateable(object,
									cacheHolder.translated);
							translationDialog.setTranslated(object,
									cacheHolder.translateable);
						} catch (Exception e) {
							DialogUtil
									.errorDialog(
											(IEditorSite) getSite(),
											ID,
											Translator.trans("ErrorTitle"),
											Translator.trans("Editor.mess.InternalError"),
											e);
							return;
						}
					}
					// Remove exceeding items
					for (int i = items.size() - 1; i > cache.size() - 1; i--) {
						items.remove(i);
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// not implemented
			}

			class CacheHolder {
				public String text;
				public String lang;
				public boolean translateable;
				public boolean translated;

				public CacheHolder(String text, String lang,
						boolean translated, boolean translateable) {
					super();
					this.text = text;
					this.lang = lang;
					this.translated = translated;
					this.translateable = translateable;
				}
			}
		};
		return listener;
	}

	/**
	 * Update parent widget with display element
	 * 
	 * @param translationDialog
	 */
	public void updateParentWidget(TranslationDialog translationDialog) {

		try {
			XmlObject xObj = (XmlObject) XmlBeansUtil.getLangElement(
					LanguageUtil.getDisplayLanguage(),
					translationDialog.getItems());
			String text = "";
			if (xObj != null) {
				text = XmlBeansUtil.getTextOnMixedElement(xObj);
			}

			Widget widget = translationDialog.getParentWidget();
			if (widget instanceof Text) {
				((Text) widget).setText(text);
			} else if (widget instanceof StyledText) {
				((StyledText) widget).setText(text);
			} else {
				DialogUtil
						.errorDialog(
								(IEditorSite) getSite(),
								ID,
								Translator.trans("ErrorTitle"),
								Translator.trans("Editor.mess.InternalError"),
								new DDIFtpException(
										Translator.trans("Editor.mess.UnsupportedWidgetType")));
				return;
			}
		} catch (DDIFtpException e) {
			ErrorDialog.openError(getSite().getShell(), Translator
					.trans("ErrorTitle"), null, new Status(IStatus.ERROR,
					ID, 0, e.getMessage(), e));
		}
	}

	public ReferenceSelectionCombo createRefSelection(Group group,
			String labelText, String searchTittle,
			final ReferenceType reference,
			List<LightXmlObjectType> referenceList, boolean isNew) {
		Composite labelComposite = new Composite(group, SWT.NONE);
		labelComposite.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));

		labelComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false));
		labelComposite.setLayout(new GridLayout());

		Composite composite = new Composite(group, SWT.NONE);
		composite.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));
		composite
				.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		composite.setLayout(new GridLayout());

		final ReferenceSelectionCombo referenceSelectionCombo = new ReferenceSelectionCombo(
				isNew);
		String preIdValue = "";
		if (reference != null && !reference.getIDList().isEmpty()) {
			preIdValue = reference.getIDList().get(0).getStringValue();
		}
		try {
			referenceSelectionCombo.createPartControl(labelComposite,
					composite, "", labelText, referenceList, preIdValue);
		} catch (Exception e) {
			ErrorDialog.openError(getSite().getShell(), Translator
					.trans("ErrorTitle"), null, new Status(IStatus.ERROR,
					ID, 0, e.getMessage(), e));
		}
		return referenceSelectionCombo;
	}

	public Text createTextInput(Group group, String labelText, String initText,
			Boolean isNew) {
		// label
		Label label = new Label(group, SWT.NONE);
		label.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		label.setText(labelText);

		// input
		Text text = createText(group, initText, isNew);
		setControl(text);
		return text;
	}

	public Composite createErrorComposite(Composite parent,
			String controlIdentification) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setVisible(false);
		composite.setEnabled(false);
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		composite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 2, 0));
		composite.setData("", controlIdentification);

		Label label = new Label(composite, SWT.RIGHT);
		label.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		label.setVisible(false);
		label.setEnabled(false);
		return composite;
	}

	public StyledText createTextAreaInput(Group group, String labelText,
			String initText, Boolean isNew) {
		final Label label = new Label(group, SWT.NONE);
		final GridData gd_Label = new GridData(SWT.RIGHT, SWT.TOP, false, false);
		label.setLayoutData(gd_Label);
		label.setBackground(Display.getCurrent()
				.getSystemColor(SWT.COLOR_WHITE));
		label.setText(labelText);

		return createTextAreaInput(group, initText, isNew);
	}

	public StyledText createTextAreaInput(Group group, String initText,
			Boolean isNew) {
		StyledText styledText = new StyledText(group, SWT.WRAP | SWT.V_SCROLL
				| SWT.BORDER);
		styledText.setText(initText);
		final GridData gd_Text = new GridData(GridData.FILL_BOTH);
		styledText.setLayoutData(gd_Text);
		setControl(styledText);
		styledText.setData(NEW_ITEM, isNew);
		return styledText;
	}

	public Browser createBrowser(Group group, String labelText) {
		createLabel(group, labelText);
		return createBrowser(group);
	}

	public Browser createBrowser(Group group) {
		Browser browser = null;
		// ubuntu-11.4 bug related debug
		// System.out.println(System.getProperty("java.library.path"));
		// System.out.println(System
		// .getProperty("org.eclipse.swt.browser.UseWebKitGTK"));
		try {
			if (!System.getProperty("os.version").equals("2.6.38-10-generic")) {
				browser = new Browser(group, SWT.EMBEDDED | SWT.BORDER);
			} else {
				log.fatal("ubuntu-11.4 bug, org.eclipse.swt.browser.UseWebKitGTK", new Throwable());
			}
		} catch (Exception e) {
			log.fatal(e, new Throwable());
		}
		if (browser != null) {
			browser.setFont(getFont("Arial Narrow", 8, SWT.NORMAL));
			final GridData gd_Text = new GridData(GridData.FILL_BOTH);
			browser.setLayoutData(gd_Text);
		}
		return browser;
	}

	public static Font getFont(String name, int height, int style) {
		FontRegistry reg = JFaceResources.getFontRegistry();
		Font returnedFont = reg.defaultFont();
		FontDescriptor fdesc = FontDescriptor.createFrom(name, height, style);
		Font f = fdesc.createFont(returnedFont.getDevice());

		return f;
	}

	public void createDateInput(Group group, String labelText, String initDate,
			DateTimeWidget dateTimeWidget, SelectionAdapter selectionAdapter)
			throws DDIFtpException {
		Label label = new Label(group, SWT.NONE);
		label.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		label.setText(labelText);

		dateTimeWidget = new DateTimeWidget(group);
		if (!initDate.equals("")) {
			Calendar calendar = Translator.formatIso8601DateTime(initDate);
			dateTimeWidget.setSelection(calendar.getTime());
		}
		dateTimeWidget.addSelectionListener(selectionAdapter);
		setControl(dateTimeWidget);
	}

	public DateType getDate(Date date) {
		String dateTime = Translator.formatIso8601DateTime(date.getTime());
		DateType dateType = DateType.Factory.newInstance();
		dateType.setSimpleDate(dateTime);
		return dateType;
	}

	@Override
	public String getPerspectiveSwitchDialogText() {
		return MessageFormat.format(
				Translator.trans("perspective.switch.dialogtext"),
				getEditorInputImpl().getElementType().getPerspectiveId());
	}

	@Override
	public String getPreferredPerspectiveId() {
		return getEditorInputImpl().getElementType().getPerspectiveId();
	}

	public EditorIdentification getEditorIdentification() {
		return new EditorIdentification(ID, editorStatus);
	}

	public class EditorIdentification {
		String ID;
		EditorStatus editorStatus;

		public EditorIdentification(String iD, EditorStatus editorStatus) {
			super();
			ID = iD;
			this.editorStatus = editorStatus;
		}

		public String getID() {
			return ID;
		}

		public EditorStatus getEditorStatus() {
			return editorStatus;
		}

		public IEditorSite getSite() {
			return getEditorSite();
		}
	}

	public static enum FIELD_TYPE {
		DIGIT, LETTER, LETTER_DIGIT
	};

	/**
	 * Verify field data and report eventually errors
	 * 
	 * @param ft
	 *            Field type e.g. DIGIT, LETTER, LETTER_DIGIT
	 * @param e
	 *            Verify event
	 * @param currentSite
	 *            Current Site
	 */
	public static void verifyField(FIELD_TYPE ft, VerifyEvent e,
			IEditorSite currentSite) {
		char myChar;

		// Assume we don't allow it
		e.doit = false;
		// Get the character typed
		myChar = e.character;
		// log.debug("Verify char: '" + myChar + "'");

		int i = myChar;
		// log.debug("Verify char(hex): " + Integer.toHexString(i));

		IActionBars bars = currentSite.getActionBars();

		switch (ft) {
		case DIGIT:
			// Allow 0-9 and backspace
			if (Character.isDigit(myChar) || myChar == 0x08) {
				e.doit = true;
				bars.getStatusLineManager().setMessage("");
			} else {
				bars.getStatusLineManager().setMessage(
						Translator.trans("Editor.mess.UseOnlyDigits")); //$NON-NLS-1$
			}
			break;
		case LETTER:
			// Allow letters and backspace
			if (Character.isLetter(myChar) || myChar == '\b') {
				e.doit = true;
				bars.getStatusLineManager().setMessage("");
			} else {
				bars.getStatusLineManager().setMessage(
						Translator.trans("Editor.mess.UseOnlyLetters")); //$NON-NLS-1$
			}
			break;
		case LETTER_DIGIT:
			// Allow letters and/or digits and backspace
			if (Character.isLetterOrDigit(myChar) || myChar == '\b') {
				e.doit = true;
				bars.getStatusLineManager().setMessage("");
			} else {
				bars.getStatusLineManager().setMessage(
						Translator.trans("Editor.mess.UseOnlyLettersDigits")); //$NON-NLS-1$
			}
			break;
		}
	}

	public static void verifyField(FIELD_TYPE digit, VerifyEvent e,
			IWorkbenchPartSite site) {
		verifyField(digit, e, (IEditorSite) site);
	}

	public static void resizeTableFont(Table table) {
		// read in prefs on table font size
		int size = new Integer(Activator.getDefault().getPreferenceStore()
				.getString(PreferenceConstants.FONT_SIZE_TABLE_SIZE))
				.intValue();
		// resize table font
		int fontSize;
		switch (size) {
		case 1:
			fontSize = 8;
			break;
		case 2:
			fontSize = 10;
			break;
		case 3:
			fontSize = 12;
			break;
		default:
			fontSize = 8;
			break;
		}
		table.setFont(getFont("Arial Narrow", fontSize, SWT.NORMAL));
		return;
	}

	public static void resizeColumn(TableViewerColumn column) {
		// TODO Auto-generated method stub

	}
}
