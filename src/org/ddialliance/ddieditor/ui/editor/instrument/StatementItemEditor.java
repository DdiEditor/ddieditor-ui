package org.ddialliance.ddieditor.ui.editor.instrument;

import java.text.MessageFormat;
import java.util.List;

import org.apache.xmlbeans.XmlOptions;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.NameType;
import org.ddialliance.ddieditor.ui.dbxml.instrument.StatementItemDao;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddieditor.ui.editor.EditorInput;
import org.ddialliance.ddieditor.ui.editor.TranslationDialog;
import org.ddialliance.ddieditor.ui.editor.EditorInput.EditorModeType;
import org.ddialliance.ddieditor.ui.model.instrument.StatementItem;
import org.ddialliance.ddieditor.ui.perspective.IAutoChangePerspective;
import org.ddialliance.ddieditor.ui.perspective.InstrumentPerspective;
import org.ddialliance.ddieditor.ui.view.Messages;
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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

public class StatementItemEditor extends Editor implements
		IAutoChangePerspective {
	public static final String ID = "org.ddialliance.ddieditor.ui.editor.instrument.StatementItemEditor";
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			StatementItemEditor.class);
	private StatementItem model;
	private StatementItemDao dao;
	private IEditorSite site;

	public StatementItemEditor() {
		super(
				Messages
						.getString("StatementItemEditor.label.StatementItemEditorLabel.StatementItemEditor"),
				Messages
						.getString("StatementItemEditor.label.useTheEditorLabel.Description"));
		dao = new StatementItemDao();
	}

	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		// TODO formalize boiler plate code ...
		this.editorInput = (EditorInput) input;

		if (editorInput.getEditorMode().equals(EditorModeType.NEW)) {
			try {
				model = dao.create(editorInput.getId(), editorInput
						.getVersion(), editorInput.getParentId(), editorInput
						.getParentVersion());
			} catch (Exception e) {
				log.error("StatementItemEditor.init(): " + e.getMessage());
				String errMess = Messages
						.getString("StatementItemEditor.mess.ErrorDuringCreateNewInstrument"); //$NON-NLS-1$
				ErrorDialog.openError(site.getShell(), Messages
						.getString("ErrorTitle"), null, new Status(
						IStatus.ERROR, ID, 0, errMess, e));
				System.exit(0);
			}
		} else if (editorInput.getEditorMode().equals(EditorModeType.EDIT)
				|| editorInput.getEditorMode().equals(EditorModeType.VIEW)) {
			try {
				model = dao.getModel(editorInput.getId(), editorInput
						.getVersion(), editorInput.getParentId(), editorInput
						.getParentVersion());
			} catch (Exception e) {
				String errMess = Messages
						.getString("StatementItemEditor.mess.GetInstrumentByIdError"); //$NON-NLS-1$
				ErrorDialog.openError(site.getShell(), Messages
						.getString("ErrorTitle"), null, new Status(
						IStatus.ERROR, ID, 0, errMess, e));
				System.exit(0);
			}
		} else {
			String errMess = MessageFormat
					.format(
							Messages
									.getString("InstrumentSchemeEditor.mess.UnknownEditorMode"), editorInput.getEditorMode()); //$NON-NLS-1$
			MessageDialog.openError(site.getShell(), Messages
					.getString("ErrorTitle"), errMess);
			System.exit(0);
		}
		super.init(site, input);

		this.site = site;
		setSite(site);
		setInput(editorInput);
		setPartName(editorInput.getId());
	}

	public class TextInputModifyListener implements ModifyListener {
		private NameType name;
		private Text text;

		public TextInputModifyListener(Text text, NameType name) {
			this.name = name;
			this.text = text;
		}

		@Override
		public void modifyText(ModifyEvent e) {
			editorStatus.setChanged();
			name.setStringValue(text.getText());
		}
	}

	public class CreateNewSelectionListener implements SelectionListener {
		private Button action;
		private Text text;
		private NameType existName;
		private TextInputModifyListener listener;
		private List items;
		private String parentLabel;

		/**
		 * Constructor
		 * 
		 * @param action
		 * @param existName
		 * @param text
		 * @param listener
		 * @param items
		 * @param parentLabel
		 */
		public CreateNewSelectionListener(Button action, NameType existName,
				Text text, TextInputModifyListener listener, List items,
				String parentLabel) {
			this.action = action;
			this.existName = existName;
			this.text = text;
			this.listener = listener;
			this.items = items;
			this.parentLabel = parentLabel;
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			// 
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			// create new item
			existName = model.getDocument().getStatementItem()
					.addNewConstructName();
			existName.setLang(Translator.getLocale().getISO3Language());
			editorStatus.setChanged();

			// change action button
			action.setText(Messages.getString("editor.button.translate"));
			action.update();
			Listener[] listerners = action.getListeners(SWT.Selection);
			action.removeListener(SWT.Selection, listerners[0]);
			e.data = TranslationDialog.OPEN_DIALOG.NO;
			action.addSelectionListener(createTranslationSelectionListener(
					items, parentLabel));

			// modify text input
			text.setVisible(true);
			text.removeModifyListener(listener);
			text
					.addModifyListener(new TextInputModifyListener(text,
							existName));
		}

//		public void updateListener() {
//			action.addSelectionListener(createTranslationSelectionListener(
//					items, parentLabel));
//		}
	}

	/**
	 * Create contents of the editor part
	 * 
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout());
		super.createPartControl(parent);

		createTabFolder(getComposite_1());

		// name
		TabItem tabItem = createTabItem(Messages
				.getString("StatementItem.editor.tabdisplaytext"));
		final Group group = createGroup(tabItem, Messages
				.getString("StatementItem.editor.groupdisplaytext"));

		createLabel(group, Messages.getString("editor.label.name"));
		NameType existName = (NameType) (XmlBeansUtil
				.getDefaultLangElement(model.getDocument().getStatementItem()
						.getConstructNameList()));

		final Text text = createText(group, "");
		TextInputModifyListener textInputListener = new TextInputModifyListener(
				text, existName);
		text.addModifyListener(textInputListener);
		text.setVisible(false);

		final Button action = createButton(group, "");
		if (existName == null) {
			action.setText("  "+Messages.getString("editor.button.create")+"  ");
			action.addSelectionListener(new CreateNewSelectionListener(action,
					existName, text, textInputListener, model.getDocument()
							.getStatementItem().getConstructNameList(), model
							.getDocument().getStatementItem().getId()));
		} else {
			action.setText(Messages.getString("editor.button.translate"));
			action.addSelectionListener(createTranslationSelectionListener(
					model.getDocument().getStatementItem()
							.getConstructNameList(), model.getDocument()
							.getStatementItem().getId()));
			text.setText(existName.getStringValue());
			text.setVisible(true);
		}

		// translation
		TabItem tabItem2 = createTabItem("Translation");
		Group group2 = createGroup(tabItem2, "groupText");
		createTranslation(group2, "Translate", model.getDocument()
				.getStatementItem().getConstructNameList(), model.getId());

		// id
		createPropertiesTab(getTabFolder());
		editorStatus.clearChanged();
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		super.doSave(monitor);
		XmlOptions ops = new XmlOptions();
		ops.setSavePrettyPrint();
		try {
			if (editorInput.getEditorMode().equals(EditorModeType.NEW)) {
				dao.create(model);
				editorInput.setEditorMode(EditorModeType.EDIT);
			} else if (editorInput.getEditorMode().equals(EditorModeType.EDIT)) {
				dao.update(model);
			} else if (editorInput.getEditorMode().equals(EditorModeType.VIEW)) {
				log.debug("*** Saved ignored! ***");
			}
		} catch (Exception e) {
			String errMess = Messages
					.getString("StatementItemEditor.mess.ErrorDuringSave"); //$NON-NLS-1$
			ErrorDialog.openError(site.getShell(), Messages
					.getString("ErrorTitle"), null, new Status(IStatus.ERROR,
					ID, 0, errMess, e));
			return;
		}
		editorInput.getParentView().refreshView();
		editorStatus.clearChanged();
	}

	@Override
	public String getPreferredPerspectiveId() {
		return InstrumentPerspective.ID;
	}

	@Override
	public String getPerspectiveSwitchDialogText() {
		return MessageFormat.format(Messages
				.getString("perspective.switch.dialogtext"), Messages
				.getString("perspective.instruments"));
	}
}
