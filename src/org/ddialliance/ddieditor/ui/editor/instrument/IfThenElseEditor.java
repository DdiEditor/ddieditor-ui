package org.ddialliance.ddieditor.ui.editor.instrument;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.xmlbeans.XmlObject;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.ProgrammingLanguageCodeType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.ReferenceType;
import org.ddialliance.ddieditor.logic.urn.ddi.ReferenceResolution;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectListDocument;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.Activator;
import org.ddialliance.ddieditor.ui.dbxml.instrument.IfThenElseDao;
import org.ddialliance.ddieditor.ui.dialogs.translationdialoginput.DescriptionTdI;
import org.ddialliance.ddieditor.ui.dialogs.translationdialoginput.LabelTdI;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddieditor.ui.editor.EditorInput;
import org.ddialliance.ddieditor.ui.editor.EditorInput.EditorModeType;
import org.ddialliance.ddieditor.ui.editor.widgetutil.genericmodifylistener.TextStyledTextModyfiListener;
import org.ddialliance.ddieditor.ui.editor.widgetutil.referenceselection.ReferenceSelectionAdapter;
import org.ddialliance.ddieditor.ui.editor.widgetutil.referenceselection.ReferenceSelectionCombo;
import org.ddialliance.ddieditor.ui.editor.widgetutil.table.TableColumnSort;
import org.ddialliance.ddieditor.ui.model.ElementType;
import org.ddialliance.ddieditor.ui.model.IModel;
import org.ddialliance.ddieditor.ui.model.ModelIdentifingType;
import org.ddialliance.ddieditor.ui.model.instrument.IfThenElse;
import org.ddialliance.ddieditor.ui.perspective.IAutoChangePerspective;
import org.ddialliance.ddieditor.ui.util.DialogUtil;
import org.ddialliance.ddieditor.ui.util.LanguageUtil;
import org.ddialliance.ddieditor.ui.util.swtdesigner.ResourceManager;
import org.ddialliance.ddieditor.ui.view.TreeMenuProvider;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.Translator;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.ddialliance.ddiftp.util.xml.XmlBeansUtil;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

public class IfThenElseEditor extends Editor {
	public static final String ID = "org.ddialliance.ddieditor.ui.editor.instrument.IfThenElseEditor";
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			IfThenElseEditor.class);
	private List<LightXmlObjectType> questionRefList;
	private IfThenElse modelImpl;
	private Table table;

	public IfThenElseEditor() {
		super(
				Translator
						.trans("IfThenElseEditor.label.IfThenElseEditorLabel.IfThenElseEditor"),
				Translator
						.trans("IfThenElseEditor.label.useTheEditorLabel.Description"),
				ID);
		this.dao = new IfThenElseDao();
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
		this.modelImpl = (IfThenElse) model;
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout());
		super.createPartControl(parent);
		createTabFolder(getComposite_1());

		// main tab
		TabItem tabItem = createTabItem(Translator
				.trans("IfThenElse.editor.tabdisplaytext"));
		Group group = createGroup(tabItem,
				Translator.trans("IfThenElse.editor.groupdisplaytext"));

		// if condition
		ProgrammingLanguageCodeType ifProgrammingLanguageCode = modelImpl
				.getIfCondition();
		Text conditionTxt = createTextInput(group,
				Translator.trans("IfThenElse.editor.if"),
				ifProgrammingLanguageCode == null ? ""
						: ifProgrammingLanguageCode.getStringValue(), false);
		conditionTxt.addModifyListener(new TextStyledTextModyfiListener(
				modelImpl, ProgrammingLanguageCodeType.class,
				getEditorIdentification()));

		// if condition lang
		String programmingLanguage = ifProgrammingLanguageCode == null ? getDefaultCodeProgrammingLanguage()
				: ifProgrammingLanguageCode.getProgrammingLanguage();

		Text programmingLanguageTxt = createTextInput(group,
				Translator.trans("StatementItem.editor.programlang"),
				programmingLanguage, false);
		programmingLanguageTxt
				.addModifyListener(new TextStyledTextModyfiListener(modelImpl,
						ModelIdentifingType.Type_A.class,
						getEditorIdentification()));

		// if question ref
		createLabel(group, Translator.trans("IfThenElse.editor.ifquestionref"));

		table = new Table(group, SWT.VIRTUAL | SWT.FULL_SELECTION | SWT.MULTI
				| SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		GridData fileTableGd = new GridData(// );
				GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL
						| GridData.VERTICAL_ALIGN_FILL | GridData.GRAB_VERTICAL);
		table.setRedraw(true);
		table.setLayoutData(fileTableGd);
		table.setLinesVisible(true);
		table.setHeaderVisible(false);
		fileTableGd.horizontalSpan = 1;
		Editor.resizeTableFont(table);

		// file table header
		String[] columnNames = { "" };

		// create table columns
		TableColumnSort sortListener = new TableColumnSort(table);
		for (int i = 0; i < columnNames.length; i++) {
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(columnNames[i]);
			column.addListener(SWT.Selection, sortListener);
			column.setResizable(true);
			column.setWidth(130);
		}

		// quei ref table content
		try {
			updatequeiRefTable();
		} catch (DDIFtpException e) {
			Editor.showError(e, ID);
		}

		// quei ref table listeners
		// popup menu
		Menu menu = new Menu(table);

		// menu add
		final MenuItem addMenuItem = new MenuItem(menu, SWT.CASCADE);
		addMenuItem.setSelection(true);
		addMenuItem.setText(Translator.trans("View.label.addMenuItem.Add"));
		addMenuItem.setImage(ResourceManager.getPluginImage(
				Activator.getDefault(), "icons/new_wiz.gif"));
		addMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				popupMenuAction(PopupAction.ADD);
			}
		});

		// menu edit
		MenuItem editMenuItem = new MenuItem(menu, SWT.NONE);
		editMenuItem.setText(Translator.trans("View.label.editMenuItem.Edit"));
		editMenuItem.setImage(ResourceManager.getPluginImage(
				Activator.getDefault(), "icons/editor_area.gif"));
		editMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				popupMenuAction(PopupAction.EDIT);
			}
		});

		// menu remove
		final MenuItem removeMenuItem = new MenuItem(menu, SWT.NONE);
		removeMenuItem.setText(Translator
				.trans("View.label.removeMenuItem.Remove"));
		removeMenuItem.setImage(ResourceManager.getPluginImage(
				Activator.getDefault(), "icons/delete_obj.gif"));
		removeMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				popupMenuAction(PopupAction.REMOVE);
			}
		});
		table.setMenu(menu);

		table.addMouseListener(new MouseListener() {
			@Override
			public void mouseUp(MouseEvent e) {
				// do nothing
			}

			@Override
			public void mouseDown(MouseEvent e) {
				// do noting
			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				TableItem[] items = ((Table) e.getSource()).getSelection();
				for (int i = 0; i < items.length; i++) {
					LightXmlObjectType lightXmlObject = (LightXmlObjectType) items[i]
							.getData();
					try {
						TreeMenuProvider.defineInputAndOpenEditor(ElementType
								.getElementType(lightXmlObject.getElement()),
								ElementType.QUESTION_SCHEME, lightXmlObject,
								EditorModeType.EDIT,
								((EditorInput) getEditorInput())
										.getResourceId(), null);
					} catch (DDIFtpException e1) {
						showError(e1);
					}
				}
			}
		});

		// then ref
		List<LightXmlObjectType> controlConstructRefList = new ArrayList<LightXmlObjectType>();
		try {
			controlConstructRefList = DdiManager.getInstance()
					.getControlConstructsLight();
		} catch (Exception e) {
			showError(e);
		}

		ReferenceSelectionCombo thenRefSelectCombo = createRefSelection(group,
				Translator.trans("IfThenElse.editor.thenref"),
				Translator.trans("IfThenElse.editor.thenref"),
				modelImpl.getThenReference(), controlConstructRefList, false);
		thenRefSelectCombo.addSelectionListener(Translator
				.trans("IfThenElse.editor.thenref"),
				new ReferenceSelectionAdapter(thenRefSelectCombo, modelImpl,
						ModelIdentifingType.Type_C.class,
						getEditorIdentification()));

		// else ref
		ReferenceSelectionCombo elseRefSelectCombo = createRefSelection(group,
				Translator.trans("IfThenElse.editor.elseref"),
				Translator.trans("IfThenElse.editor.elseref"),
				modelImpl.getElseReference(), controlConstructRefList, false);
		elseRefSelectCombo.addSelectionListener(Translator
				.trans("IfThenElse.editor.elseref"),
				new ReferenceSelectionAdapter(elseRefSelectCombo, modelImpl,
						ModelIdentifingType.Type_D.class,
						getEditorIdentification()));

		// description tab
		// name
		TabItem tabItem2 = createTabItem(Translator
				.trans("editor.label.description"));
		Group group2 = createGroup(tabItem2,
				Translator.trans("editor.label.description"));

		try {
			// label
			Text txt = createLabelInput(group2,
					Translator.trans("editor.label.label"), modelImpl
							.getDocument().getIfThenElse().getLabelList(),
					modelImpl.getDocument().getIfThenElse().getId());
			createTranslation(group2,
					Translator.trans("editor.button.translate"), modelImpl
							.getDocument().getIfThenElse().getLabelList(),
					new LabelTdI(), "", txt);

			// description
			StyledText styledText = createStructuredStringInput(group2,
					Translator.trans("editor.label.description"),
					modelImpl.getDocument().getIfThenElse()
							.getDescriptionList(), modelImpl.getDocument()
							.getIfThenElse().getId());
			createTranslation(group2,
					Translator.trans("editor.button.translate"),
					modelImpl.getDocument().getIfThenElse()
							.getDescriptionList(), new DescriptionTdI(), "",
					styledText);
		} catch (DDIFtpException e) {
			DialogUtil
					.errorDialog(getEditorSite(), ID, null, e.getMessage(), e);
		}

		// id tab
		createPropertiesTab(getTabFolder());

		// xml tab
		createXmlTab(modelImpl);

		// preview tab
		createPreviewTab(modelImpl);

		editorStatus.clearChanged();
	}

	public void updatequeiRefTable() throws DDIFtpException {
		if (modelImpl.getIfQuestionReferences()==null) { // guard
			return;
		}
		
		String[] queiRefIds = new String[modelImpl.getIfQuestionReferences()
				.size()];
		int count = 0;
		for (ReferenceType ref : modelImpl.getIfQuestionReferences()) {
			ReferenceResolution refS = new ReferenceResolution(ref);
			queiRefIds[count] = refS.getId();
			count++;
		}

		// clear items
		try {
			TableItem[] items = table.getItems();
			for (int i = 0; i < items.length; i++) {
				items[i].dispose();
			}
		} catch (Exception e) {
			// do nothing
			// swt null items in table exception
		}

		// insert into table
		for (int i = 0; i < queiRefIds.length; i++) {
			tableAdd(queiRefIds[i]);
		}
	}

	private void tableAdd(String id) {
		LightXmlObjectListDocument lightXmlList = null;
		try {
			lightXmlList = DdiManager.getInstance().getQuestionItemsLight(id,
					null, null, null);
		} catch (Exception e) {
			showError(e);
		}
		if (lightXmlList.getLightXmlObjectList().getLightXmlObjectList()
				.isEmpty()) {
			return;
		}
		LightXmlObjectType lightXmlObject = lightXmlList
				.getLightXmlObjectList().getLightXmlObjectList().get(0);
		TableItem item = new TableItem(table, SWT.NONE);
		try {
			if (lightXmlObject.getLabelList().size() > 0) {
				item.setText(0, XmlBeansUtil
						.getTextOnMixedElement((XmlObject) XmlBeansUtil
								.getLangElement(
										LanguageUtil.getDisplayLanguage(),
										lightXmlObject.getLabelList())));
			}
		} catch (DDIFtpException e) {
			showError(e);
		} catch (Exception e) {
			showError(e);
		}
		item.setData(lightXmlObject);
	}

	private enum PopupAction {
		ADD, EDIT, REMOVE
	};

	private void popupMenuAction(PopupAction action) {
		TableItem[] tableItems = table.getSelection();
		// guard
		if (tableItems.length <= 0 && !action.equals(PopupAction.ADD)) {
			return;
		}

		boolean update = false;
		// add
		if (action.equals(PopupAction.ADD)) {
			List<LightXmlObjectType> controlConstructRefList = new ArrayList<LightXmlObjectType>();
			try {
				controlConstructRefList = DdiManager.getInstance()
						.getQuestionItemsLight(null, null, null, null)
						.getLightXmlObjectList().getLightXmlObjectList();
			} catch (Exception e) {
				DialogUtil.errorDialog(getSite().getShell(), ID, null,
						e.getMessage(), e);
			}

			// weed out prechoosen ones
			for (Iterator<LightXmlObjectType> iterator = controlConstructRefList
					.iterator(); iterator.hasNext();) {
				LightXmlObjectType lightXmlObjectType = iterator.next();
				for (ReferenceType ref : modelImpl.getIfQuestionReferences()) {
					if (new ReferenceResolution(ref).getId().equals(
							lightXmlObjectType.getId())) {
						iterator.remove();
						break;
					}
				}
			}

			// labelQueryResult = null;
			MenuPopupAddDialog addDialog = new MenuPopupAddDialog(getSite()
					.getShell(),
					Translator.trans("IfThenElse.editor.ifquestionref"),
					Translator.trans("IfThenElse.editor.ifquestionref"),
					controlConstructRefList, modelImpl);
			addDialog.open();

			if (addDialog.getResult() != null) {
				update = true;

				// swt add
				tableAdd(addDialog.getResult().getId());
				table.redraw();

				// xml add
				modelImpl.addNewQuestionReference(addDialog.getResult());

			}
		}

		// edit and remove
		for (int i = 0; i < tableItems.length; i++) {
			LightXmlObjectType selectedLightXmlObject = (LightXmlObjectType) tableItems[i]
					.getData();
			// edit
			if (action.equals(PopupAction.EDIT)) {
				try {
					TreeMenuProvider.defineInputAndOpenEditor(
							ElementType.getElementType(selectedLightXmlObject
									.getElement()),
							ElementType.QUESTION_SCHEME,
							selectedLightXmlObject, EditorModeType.EDIT,
							((EditorInput) getEditorInput()).getResourceId(),
							null);
				} catch (DDIFtpException e) {
					showError(e);
				}
			}
			// remove
			else if (action.equals(PopupAction.REMOVE)) {
				int count = 0;
				for (Iterator<ReferenceType> iterator = modelImpl
						.getIfQuestionReferences().iterator(); iterator
						.hasNext(); count++) {
					ReferenceType type = iterator.next();

					// remove from xml
					if (selectedLightXmlObject.getId().equals(
							new ReferenceResolution(type).getId())) {
						iterator.remove();

						// remove from table
						update = true;
						for (int j = 0; j < tableItems.length; j++) {
							if (((LightXmlObjectType) tableItems[i].getData())
									.getId().equals(
											selectedLightXmlObject.getId())) {
								table.remove(j);
								table.redraw();
							}
						}
						break;
					}
				}
			} else {
				log.warn("Action not specified!");
			}
		}

		// update
		if (update) {
			this.editorStatus.setChanged();
		}
	}

	@Override
	public String getPerspectiveSwitchDialogText() {
		return MessageFormat.format(
				Translator.trans("perspective.switch.dialogtext"),
				Translator.trans("perspective.instruments"));
	}

	class MenuPopupAddDialog extends Dialog {
		Log log = LogFactory.getLog(LogType.SYSTEM, MenuPopupAddDialog.class);
		Combo combo;
		ReferenceSelectionCombo selectCombo;
		LightXmlObjectType result;
		int beforeAfter;

		String title;
		String label;
		List<LightXmlObjectType> refs;
		IModel modelImpl;

		public MenuPopupAddDialog(Shell parentShell, String title,
				String label, List<LightXmlObjectType> refs, IModel modelImpl) {
			super(parentShell);
			this.title = title;
			this.label = label;
			this.refs = refs;
			this.modelImpl = modelImpl;
		}

		@Override
		protected Control createDialogArea(Composite parent) {
			// dialog setup
			this.getShell().setText(title);

			// group
			Editor editor = new Editor();
			Group group = editor.createGroup(parent, label);
			group.setLayoutData(new GridData(700, 75));

			// selection
			selectCombo = editor.createRefSelection(group,
					Translator.trans("IfThenElse.editor.ifquestionref"),
					Translator.trans("IfThenElse.editor.ifquestionref"),
					ReferenceType.Factory.newInstance(), refs, false);
			selectCombo.addSelectionListener(
					Translator.trans("IfThenElse.editor.ifquestionref"),
					new MenuPopupSelectionAdapter(this));

			return null;
		}

		public LightXmlObjectType getResult() {
			return selectCombo.getResult();
		}
	}

	class MenuPopupSelectionAdapter implements SelectionListener {
		MenuPopupAddDialog dialog;

		public MenuPopupSelectionAdapter(MenuPopupAddDialog dialog) {
			super();
			this.dialog = dialog;
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			dialog.result = dialog.selectCombo.getResult();
			dialog.beforeAfter = dialog.combo.getSelectionIndex();
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			// do nothing
		}
	}

}
