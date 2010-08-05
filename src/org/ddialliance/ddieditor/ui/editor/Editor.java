package org.ddialliance.ddieditor.ui.editor;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.xmlbeans.XmlObject;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.ConstructNameDocument;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.DateType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.NameType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.ReferenceType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.StructuredStringType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.VersionRationaleDocument;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.Activator;
import org.ddialliance.ddieditor.ui.dbxml.IDao;
import org.ddialliance.ddieditor.ui.dialogs.TranslationDialog;
import org.ddialliance.ddieditor.ui.dialogs.TranslationDialogInput;
import org.ddialliance.ddieditor.ui.editor.EditorInput.EditorModeType;
import org.ddialliance.ddieditor.ui.editor.widgetutil.genericmodifylistener.TextStyledTextModyfiListener;
import org.ddialliance.ddieditor.ui.editor.widgetutil.referenceselection.ReferenceSelectionCombo;
import org.ddialliance.ddieditor.ui.editor.widgetutil.tab.DDITabItemAction;
import org.ddialliance.ddieditor.ui.editor.widgetutil.tab.PropertyTabItemAction;
import org.ddialliance.ddieditor.ui.editor.widgetutil.tab.TabFolderListener;
import org.ddialliance.ddieditor.ui.model.IModel;
import org.ddialliance.ddieditor.ui.model.LabelDescription;
import org.ddialliance.ddieditor.ui.model.translationdialoginput.DescriptionTdI;
import org.ddialliance.ddieditor.ui.model.translationdialoginput.LabelTdI;
import org.ddialliance.ddieditor.ui.perspective.IAutoChangePerspective;
import org.ddialliance.ddieditor.ui.util.DialogUtil;
import org.ddialliance.ddieditor.ui.util.LanguageUtil;
import org.ddialliance.ddieditor.ui.util.swtdesigner.SWTResourceManager;
import org.ddialliance.ddieditor.ui.view.InfoView;
import org.ddialliance.ddieditor.ui.view.Messages;
import org.ddialliance.ddieditor.ui.view.View;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.ReflectionUtil;
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
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
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
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TypedListener;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
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

	public static final String ID = "org.ddialliance.ddieditor.ui.editor.Editor";
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
	public static String PROPERTY_TAB_ID = "property";

	protected IModel model;
	protected IDao dao;

	protected Group labelDescriptionTabGroup; // May be used for expanding Label

	// Description Tab content

	/**
	 * Default constructor. Usage to gain access to create widget methods <br>
	 * Note: Builds an empty editor input.
	 */
	public Editor() {
		// editorInput = new EditorInput(null, null, null, null, null,
		// null);

	}

	/**
	 * Constructor
	 * 
	 * @param title
	 *            title
	 * @param description
	 *            description
	 */
	public Editor(String title, String description) {
		this.title = title;
		this.description = description;
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		EditorInput editorInput = (EditorInput) input;
		if (editorInput.getEditorMode().equals(EditorModeType.NEW)) {
			try {
				model = dao.create("", "", editorInput.getParentId(),
						editorInput.getParentVersion());
			} catch (Exception e) {
				throw new PartInitException(Messages
						.getString("editor.init.error.create"),
						new DDIFtpException(e));
			} catch (Throwable t) {
				DDIFtpException e = new DDIFtpException(Messages
						.getString("editor.init.error.create"));
				e.setRealThrowable(t);
				throw new PartInitException(Messages
						.getString("editor.init.error.create"), e);
			}
		} else if (editorInput.getEditorMode().equals(EditorModeType.EDIT)
				|| editorInput.getEditorMode().equals(EditorModeType.VIEW)) {
			try {
				model = dao.getModel(editorInput.getId(), editorInput
						.getVersion(), editorInput.getParentId(), editorInput
						.getParentVersion());
			} catch (Exception e) {
				throw new PartInitException(Messages
						.getString("editor.init.error.retrieval"),
						new DDIFtpException(e));
			}
		} else {
			throw new PartInitException(Messages
					.getString("editor.init.error.editmodeunsupported"),
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

		// name
		setPartName(model.getId());
		setTitleToolTip(Messages.getString(editorInput.getElementType()
				.getDisplayMessageEntry())); // TODO
		// i18n
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout());

		// general ui layout
		composite = new Composite(parent, SWT.BORDER);
		composite.setLayout(new GridLayout());
		composite.setRedraw(true);

		final GridData gd_composite_1 = new GridData(SWT.FILL, SWT.FILL, true,
				true);
		gd_composite_1.widthHint = 539;
		gd_composite_1.heightHint = 573;
		composite.setLayoutData(gd_composite_1);
		composite.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));

		final Composite composite_2 = new Composite(composite, SWT.NONE);
		composite_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		composite_2.setBackground(SWTResourceManager.getColor(230, 230, 250));
		composite_2.setLayout(new GridLayout());

		// title
		final Label titleLabel = new Label(composite_2, SWT.WRAP);
		titleLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		titleLabel.setFont(SWTResourceManager.getFont("Sans", 14, SWT.BOLD));

		titleLabel.setBackground(SWTResourceManager.getColor(230, 230, 250));
		titleLabel.setText(title);

		// description
		final Label descriptionLabel = new Label(composite_2, SWT.WRAP);
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
		} catch (Exception e1) {
			DialogUtil.errorDialog((IEditorSite) getSite(), ID, Messages
					.getString("ErrorTitle"), Messages
					.getString("Editor.mess.ValidationErrorDuringSave"), e1);
			return;
		}
		try {
			if (getEditorInputImpl().getEditorMode().equals(EditorModeType.NEW)) {
				dao.create(model);
				getEditorInputImpl().setEditorMode(EditorModeType.EDIT);
			} else if (getEditorInputImpl().getEditorMode().equals(
					EditorModeType.EDIT)) {
				dao.update(model);
			} else if (getEditorInputImpl().getEditorMode().equals(
					EditorModeType.VIEW)) {
				log.debug("*** Saved ignored! ***");
			}
		} catch (Exception e) {
			DialogUtil.errorDialog((IEditorSite) getSite(), ID, Messages
					.getString("ErrorTitle"), Messages
					.getString("Editor.mess.ErrorDuringSave"), e);
			return;
		}
		editorStatus.clearChanged();
		updateParentView();
		updateInfoView();
	}

	@Override
	public void doSaveAs() {
		MessageDialog
				.openError(
						getSite().getShell(),
						Messages.getString("ErrorTitle"), Messages.getString("Editor.mess.SaveAsNotSupported")); //$NON-NLS-1$
	}

	@Override
	public boolean isSaveAsAllowed() {
		// save as not supported
		return false;
	}

	@Override
	public void setFocus() {
		// Set the focus
	}

	public EditorInput getEditorInputImpl() {
		return (EditorInput) super.getEditorInput();
	}

	/**
	 * Update Info View - if it exists in active page - else ignore update
	 */
	private void updateInfoView() {

		IWorkbenchWindow[] workbenchWindows = PlatformUI.getWorkbench()
				.getWorkbenchWindows();
		IWorkbenchPage[] iPages = workbenchWindows[0].getPages();
		if (iPages.length > 1) {
			log.error("Nbr. pages per window (only one is expected): "
					+ iPages.length);
		}
		IViewPart iViewPart = iPages[0].findView(InfoView.ID);
		if (iViewPart != null) {
			((View) iViewPart).refreshView();
		}
	}

	/**
	 * Update parent view by firing a property change event
	 */
	public void updateParentView() {
		// update view
		firePropertyChange(IEditorPart.PROP_INPUT);
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		// do nothing
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
		return group;
	}

	public void createPropertiesTab(TabFolder tabFolder) {
		TabItem tabItem = createTabItem(Messages
				.getString("Editor.label.propertiesTabItem.Properties"));
		Group group = createGroup(tabItem, Messages
				.getString("Editor.label.propertiesGroup.Properties"));

		boolean isMaintainable = false;
		try {
			isMaintainable = DdiManager.getInstance().getDdi3NamespaceHelper()
					.isMaintainable(model.getDocument());
		} catch (DDIFtpException e) {
			DialogUtil.errorDialog(group.getShell(), ID,
					"Maintainable check error", e.getMessage(), e);
		}

		// action
		createLabel(group, Messages.getString("Editor.label.action.Action"));
		Combo actionCombo = createCombo(group, new String[] { "1", "2", "3" });

		// urn
		StyledText urnText = createTextAreaInput(group, Messages
				.getString("Editor.label.urnLabel.URN"), "", null);
		urnText.setEditable(false);

		// agency
		Text agencyText = null;
		if (isMaintainable) {
			createLabel(group, Messages
					.getString("Editor.label.agencyLabel.agency"));
			agencyText = createText(group, "", null);
			agencyText.setEditable(false);
		}

		// id
		createLabel(group, Messages.getString("Editor.label.idLabel.ID"));
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
			createLabel(group, Messages
					.getString("Editor.label.versionGroup.Version"));
			versionText = createText(group, "", null);
			versionText.setEditable(false);

			// version responsibility
			createLabel(group, Messages
					.getString("Editor.label.responsibelLabel.Responsibel"));
			versionResponsibilityText = createText(group, "", null);
			versionResponsibilityText.setEditable(false);

			// version date
			createLabel(group, Messages
					.getString("Editor.label.versionDateLabel.Date"));
			versionDate = createText(group, "", null);
			versionDate.setEditable(false);

			// version rationale
			versionRationaleText = createTextAreaInput(group, Messages
					.getString("Editor.label.versionrationale"), "", null);
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
		TabItem tabItem = createTabItem(Messages
				.getString("editor.tabitem.ddi"));
		Group group = createGroup(tabItem, Messages
				.getString("editor.group.ddi"));
		StyledText styledText = createTextAreaInput(group, Messages
				.getString("editor.label.ddi"), "", false);
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

	public void createLabelDescriptionTab(Composite parent,
			String editorEntityName, final LabelDescription simpleElement) {
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
		final Group group = new Group(simpleRootComposite, SWT.NONE);
		final GridData gd_labelDescriptionGroup = new GridData(SWT.FILL,
				SWT.CENTER, true, true);
		gd_labelDescriptionGroup.heightHint = 632;
		gd_labelDescriptionGroup.widthHint = 861;
		group.setLayoutData(gd_labelDescriptionGroup);
		group.setBackground(Display.getCurrent()
				.getSystemColor(SWT.COLOR_WHITE));
		final GridLayout gridLayout_1 = new GridLayout();
		gridLayout_1.numColumns = 2;
		group.setLayout(gridLayout_1);
		group.setText(editorEntityName);
		labelDescriptionTabGroup = group;

		// Simple Label:
		final Label labelLabel = new Label(group, SWT.NONE);
		final GridData gd_conceptLabel = new GridData(SWT.RIGHT, SWT.CENTER,
				false, false);
		gd_conceptLabel.horizontalIndent = 5;
		labelLabel.setLayoutData(gd_conceptLabel);
		labelLabel.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));
		labelLabel.setText(Messages.getString("SimpleEditor.label.Label")); //$NON-NLS-1$

		final Text labelText = new Text(group, SWT.BORDER);
		final GridData gd_labelText = new GridData(SWT.FILL, SWT.CENTER, true,
				false);
		labelText.setLayoutData(gd_labelText);
		labelText.setText(simpleElement.getDisplayLabel());
		labelText.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				log.debug("Label changed");
				simpleElement.setLabel(labelText.getText());
				editorStatus.setChanged();
			}
		});
		createTranslation(group, Messages.getString("editor.button.translate"),
				simpleElement.getLabels(), new LabelTdI(), "", labelText);

		// Simple Description:
		final Label simpleDescrLabel = new Label(group, SWT.NONE);
		final GridData gd_simpleDescrLabel = new GridData(SWT.RIGHT, SWT.TOP,
				false, false);
		gd_simpleDescrLabel.horizontalIndent = 5;
		simpleDescrLabel.setLayoutData(gd_simpleDescrLabel);
		simpleDescrLabel.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));
		simpleDescrLabel.setText(Messages
				.getString("SimpleEditor.label.DescriptionText.Label")); //$NON-NLS-1$

		final StyledText simpleDescrStyledText = new StyledText(group, SWT.WRAP
				| SWT.V_SCROLL | SWT.BORDER);
		simpleDescrStyledText.setText(simpleElement.getDescr());
		final GridData gd_originalConceptTextStyledText = new GridData(
				SWT.FILL, SWT.CENTER, true, false);
		gd_originalConceptTextStyledText.heightHint = 154;
		gd_originalConceptTextStyledText.widthHint = 308;
		simpleDescrStyledText.setLayoutData(gd_originalConceptTextStyledText);
		simpleDescrStyledText.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				log.debug("Description changed");
				simpleElement.setDescr(simpleDescrStyledText.getText());
				editorStatus.setChanged();
			}
		});
		createTranslation(group, Messages.getString("editor.button.translate"),
				simpleElement.getDescrs(), new DescriptionTdI(), "",
				simpleDescrStyledText);
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
		Label label = new Label(group, SWT.NONE);
		label.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		label.setText(labelText);
		return label;
	}

	public Text createText(Group group, String initText, Boolean isNew) {
		Text text = new Text(group, SWT.BORDER);
		text
				.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
						1, 1));
		text.setText(initText);
		text.setData(NEW_ITEM, isNew);
		setControl(text);
		return text;
	}

	public Label createLabel(Composite composite, String labelText) {
		Label label = new Label(composite, SWT.NONE);
		label.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		label.setText(labelText);
		return label;
	}

	public Text createText(Composite composite, String initText) {
		Text text = new Text(composite, SWT.BORDER);
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

	public Combo createCombo(Group group, String[] options) {
		final Combo actionCombo = new Combo(group, SWT.READ_ONLY);
		actionCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		actionCombo.setItems(options);
		return actionCombo;
	}

	public Text createNameInput(Group group, String labelText,
			List<NameType> nameList, String parentLabel) throws DDIFtpException {
		NameType name = (NameType) XmlBeansUtil.getLangElement(LanguageUtil
				.getDisplayLanguage(), nameList);

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
		// Messages.getString("editor.button.translate"),
		// nameList, parentLabel);

		return nameTxt;
	}

	public StyledText createStructuredStringInput(Group group,
			String labelText, List<StructuredStringType> structuredStringList,
			String parentLabel) throws DDIFtpException {
		StructuredStringType structuredString = (StructuredStringType) XmlBeansUtil
				.getLangElement(LanguageUtil.getDisplayLanguage(),
						structuredStringList);

		StyledText styledText = createTextAreaInput(group, labelText,
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
		// Messages.getString("editor.button.translate"),
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

	/**
	 * Update parent widget with display element
	 * 
	 * @param translationDialog
	 */
	public void updateParentWidget(TranslationDialog translationDialog) {

		try {
			XmlObject xObj = (XmlObject) XmlBeansUtil.getLangElement(
					LanguageUtil.getDisplayLanguage(), translationDialog
							.getItems());
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
								Messages.getString("ErrorTitle"),
								Messages.getString("Editor.mess.InternalError"),
								new DDIFtpException(
										Messages
												.getString("Editor.mess.UnsupportedWidgetType")));
				return;
			}
		} catch (DDIFtpException e) {
			ErrorDialog.openError(getSite().getShell(), Messages
					.getString("ErrorTitle"), null, new Status(IStatus.ERROR,
					ID, 0, e.getMessage(), e));
		}
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
						cache.add(new CacheHolder(XmlBeansUtil
								.getTextOnMixedElement((XmlObject) object),
								(String) ReflectionUtil.invokeMethod(object,
										"getLang", false, null),
								(Boolean) ReflectionUtil.invokeMethod(object,
										"getTranslated", false, null),
								(Boolean) ReflectionUtil.invokeMethod(object,
										"getTranslatable", false, null)));
					} catch (Exception e1) {
						DialogUtil
								.errorDialog(
										(IEditorSite) getSite(),
										ID,
										Messages.getString("ErrorTitle"),
										Messages
												.getString("Editor.mess.InternalError"),
										e1);
						return;
					}
				}

				TranslationDialog translationDialog = new TranslationDialog(
						getEditorSite().getShell(), editorStatus, items,
						translationDialogOption, parentLabel, parentWidget);
				translationDialog.open();
				if (translationDialog.getReturnCode() == Window.OK) {
					updateParentWidget(translationDialog);
				} else {
					// Restore original items:
					for (Iterator iteratorItems = items.iterator(), iteratorCache = cache
							.iterator(); iteratorItems.hasNext();) {
						Object object = (Object) iteratorItems.next();
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
											Messages.getString("ErrorTitle"),
											Messages
													.getString("Editor.mess.InternalError"),
											e);
							return;
						}
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO
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
			ErrorDialog.openError(getSite().getShell(), Messages
					.getString("ErrorTitle"), null, new Status(IStatus.ERROR,
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

		StyledText styledText = new StyledText(group, SWT.WRAP | SWT.V_SCROLL
				| SWT.BORDER);
		styledText.setText(initText);
		styledText.setData(NEW_ITEM, isNew);
		final GridData gd_Text = new GridData(GridData.FILL_BOTH);
		styledText.setLayoutData(gd_Text);
		setControl(styledText);
		return styledText;
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
		return MessageFormat.format(Messages
				.getString("perspective.switch.dialogtext"),
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
			return (IEditorSite) getSite();
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
		log.debug("Verify char: '" + myChar + "'");

		int i = myChar;
		log.debug("Verify char(hex): " + Integer.toHexString(i));

		IActionBars bars = currentSite.getActionBars();

		switch (ft) {
		case DIGIT:
			// Allow 0-9 and backspace
			if (Character.isDigit(myChar) || myChar == 0x08) {
				e.doit = true;
				bars.getStatusLineManager().setMessage("");
			} else {
				bars.getStatusLineManager().setMessage(
						Messages.getString("Editor.mess.UseOnlyDigits")); //$NON-NLS-1$
			}
			break;
		case LETTER:
			// Allow letters and backspace
			if (Character.isLetter(myChar) || myChar == '\b') {
				e.doit = true;
				bars.getStatusLineManager().setMessage("");
			} else {
				bars.getStatusLineManager().setMessage(
						Messages.getString("Editor.mess.UseOnlyLetters")); //$NON-NLS-1$
			}
			break;
		case LETTER_DIGIT:
			// Allow letters and/or digits and backspace
			if (Character.isLetterOrDigit(myChar) || myChar == '\b') {
				e.doit = true;
				bars.getStatusLineManager().setMessage("");
			} else {
				bars.getStatusLineManager().setMessage(
						Messages.getString("Editor.mess.UseOnlyLettersDigits")); //$NON-NLS-1$
			}
			break;
		}
	}
}
