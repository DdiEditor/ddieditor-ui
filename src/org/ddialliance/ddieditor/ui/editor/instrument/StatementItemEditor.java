package org.ddialliance.ddieditor.ui.editor.instrument;

import java.text.MessageFormat;
import java.util.List;

import org.apache.xmlbeans.XmlOptions;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.TextType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.ProgrammingLanguageCodeType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.ReferenceType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.StructuredStringType;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.dbxml.instrument.StatementItemDao;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddieditor.ui.editor.EditorInput;
import org.ddialliance.ddieditor.ui.editor.EditorInput.EditorModeType;
import org.ddialliance.ddieditor.ui.editor.widgetutil.genericmodifylistener.TextStyledTextModyfiListener;
import org.ddialliance.ddieditor.ui.editor.widgetutil.referenceselection.ReferenceSelectionAdapter;
import org.ddialliance.ddieditor.ui.editor.widgetutil.referenceselection.ReferenceSelectionCombo;
import org.ddialliance.ddieditor.ui.model.ModelIdentifingType;
import org.ddialliance.ddieditor.ui.model.instrument.StatementItem;
import org.ddialliance.ddieditor.ui.perspective.IAutoChangePerspective;
import org.ddialliance.ddieditor.ui.perspective.InstrumentPerspective;
import org.ddialliance.ddieditor.ui.util.DialogUtil;
import org.ddialliance.ddieditor.ui.view.Messages;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.ddialliance.ddiftp.util.xml.XmlBeansUtil;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
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
	private List<LightXmlObjectType> questionRefList;

	public StatementItemEditor() {
		super(
				Messages
						.getString("StatementItemEditor.label.StatementItemEditorLabel.StatementItemEditor"),
				Messages
						.getString("StatementItemEditor.label.useTheEditorLabel.Description"));
		dao = new StatementItemDao();
	}

	@Override
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

		// main tab
		TabItem tabItem = createTabItem(Messages
				.getString("StatementItem.editor.tabdisplaytext"));
		Group group = createGroup(tabItem, Messages
				.getString("StatementItem.editor.groupdisplaytext"));

		// statement
		// text type in display text is one only by convention i ddi spec
		// other langs is set on parent DisplayText!!!
		// check for ddi bug best is LiteralText::Text
		StructuredStringType text = null;
		try {
			text = model.getText();
		} catch (DDIFtpException e) {
			DialogUtil.errorDialog(site, ID, null, e.getMessage(), e);
			return;
		}
		StyledText statementTxt = createTextAreaInput(group, Messages
				.getString("StatementItem.editor.textarea"), text == null ? ""
				: XmlBeansUtil.getTextOnMixedElement(text), false);
		statementTxt.addModifyListener(new TextStyledTextModyfiListener(model,
				TextType.class, getEditorIdentification()));

		// condition
		Composite error = createErrorComposite(group, "");
		ProgrammingLanguageCodeType programmingLanguageCode = model
				.getProgrammingLanguageCode();
		Text conditionTxt = createTextInput(group, Messages
				.getString("StatementItem.editor.code"),
				programmingLanguageCode == null ? "" : programmingLanguageCode
						.getStringValue(), false);
		conditionTxt.addModifyListener(new TextStyledTextModyfiListener(model,
				ProgrammingLanguageCodeType.class, getEditorIdentification()));

		// app lang
		String programmingLanguage = programmingLanguageCode == null ? ""
				: programmingLanguageCode.getProgrammingLanguage();
		if (programmingLanguage == null) {
			programmingLanguage = "";
		}
		Text programmingLanguageTxt = createTextInput(group, Messages
				.getString("StatementItem.editor.programlang"),
				programmingLanguage, false);
		programmingLanguageTxt
				.addModifyListener(new TextStyledTextModyfiListener(model,
						ModelIdentifingType.Type_A.class,
						getEditorIdentification()));

		// question source reference
		try {
			questionRefList = DdiManager.getInstance().getQuestionItemsLight(
					null, null, null, null).getLightXmlObjectList()
					.getLightXmlObjectList();
		} catch (Exception e) {
			DialogUtil.errorDialog(site, ID, null, e.getMessage(), e);
		}
		ReferenceSelectionCombo refSelecCombo = createRefSelection(group,
				Messages.getString("StatementItem.editor.questionref"),
				Messages.getString("StatementItem.editor.questionref"), model
						.getSourceQuestionReference(), questionRefList, false);
		refSelecCombo.addSelectionListener(Messages
				.getString("StatementItem.editor.questionref"),
				questionRefList, new ReferenceSelectionAdapter(refSelecCombo,
						model, ReferenceType.class, getEditorIdentification()));

		// description tab
		// name
		TabItem tabItem2 = createTabItem(Messages
				.getString("editor.label.description"));
		Group group2 = createGroup(tabItem2, Messages
				.getString("editor.label.description"));

		createNameInput(group2, Messages.getString("editor.label.name"), model
				.getDocument().getStatementItem().getConstructNameList(), model
				.getDocument().getStatementItem().getId());

		// description
		createStructuredStringInput(group2, Messages
				.getString("editor.label.description"), model.getDocument()
				.getStatementItem().getDescriptionList(), model.getDocument()
				.getStatementItem().getId());

		// id tab
		createPropertiesTab(getTabFolder());

		// xml tab
		createXmlTab(model);

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
