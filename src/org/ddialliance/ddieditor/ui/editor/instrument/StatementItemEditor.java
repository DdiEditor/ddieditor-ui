package org.ddialliance.ddieditor.ui.editor.instrument;

import java.text.MessageFormat;
import java.util.List;

import org.apache.xmlbeans.XmlOptions;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.ConditionalTextDocument;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.ConditionalTextType;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.DisplayTextDocument;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.DynamicTextType;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.TextType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.CodeType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.ProgrammingLanguageCodeType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.ReferenceType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.StructuredStringType;
import org.ddialliance.ddieditor.ui.dbxml.instrument.StatementItemDao;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddieditor.ui.editor.EditorInput;
import org.ddialliance.ddieditor.ui.editor.EditorInput.EditorModeType;
import org.ddialliance.ddieditor.ui.editor.genericgetset.GenericGetSetStringValue;
import org.ddialliance.ddieditor.ui.editor.genericgetset.GenericTextStyledTextModyfiListener;
import org.ddialliance.ddieditor.ui.model.instrument.StatementItem;
import org.ddialliance.ddieditor.ui.perspective.IAutoChangePerspective;
import org.ddialliance.ddieditor.ui.perspective.InstrumentPerspective;
import org.ddialliance.ddieditor.ui.util.DialogUtil;
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
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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

		DynamicTextType displayText = (DynamicTextType) XmlBeansUtil
				.getDefaultLangElement(model.getDocument().getStatementItem()
						.getDisplayTextList());
		ConditionalTextType condition = null;
		CodeType expression;
		ProgrammingLanguageCodeType programmingLanguage;
		ReferenceType sourceQuestionReference;

		boolean displayTextIsNew, conditionIsNew = false, expressionIsNew = false, programmingLanguageIsNew = false, sourceQuestionReferenceIsNew = false;

		// create new elements
		if (displayText == null) {
			displayText = DisplayTextDocument.Factory.newInstance()
					.addNewDisplayText();
			try {
				XmlBeansUtil.addTranslationAttributes(displayText, Translator
						.getLocaleLanguage(), false, true);
			} catch (Exception e) {
				DialogUtil.errorDialog(site, ID, e.getMessage(), e);
			}
			displayText.addNewText();
			displayTextIsNew = true;

			condition = newCondition();
			conditionIsNew = true;
			try {
				XmlBeansUtil.addTranslationAttributes(condition, Translator
						.getLocale().getCountry(), false, true);
			} catch (Exception e) {
				DialogUtil.errorDialog(site, ID, e.getMessage(), e);
			}

			expression = newExpression(condition);
			expressionIsNew = true;

			programmingLanguage = newProgrammingLanguage(expression);
			programmingLanguageIsNew = true;

			sourceQuestionReference = newSourceQuestionReference(expression);
			sourceQuestionReferenceIsNew = true;
		}
		// check existing elements
		else {
			displayTextIsNew = false;
			for (TextType test : displayText.getTextList()) {
				if (test instanceof ConditionalTextType) {
					condition = (ConditionalTextType) test;
					conditionIsNew = false;
				}
			}
			if (condition == null) {
				condition = newCondition();
				conditionIsNew = true;
				
				expression = newExpression(condition);
				expressionIsNew = true;

				programmingLanguage = newProgrammingLanguage(expression);
				programmingLanguageIsNew = true;

				sourceQuestionReference = newSourceQuestionReference(expression);
				sourceQuestionReferenceIsNew = true;
			} else {
				expression = condition.getExpression();
				if (expression != null) {
					expressionIsNew = false;
					if (!expression.getCodeList().isEmpty()) {
						programmingLanguage = expression.getCodeList().get(0);
						programmingLanguageIsNew = false;
					} else {
						programmingLanguage = newProgrammingLanguage(expression);
						programmingLanguageIsNew = true;
					}
					if (expression.getSourceQuestionReferenceList().isEmpty()) {
						sourceQuestionReference = newSourceQuestionReference(expression);
						sourceQuestionReferenceIsNew = true;
					} else {
						sourceQuestionReference = expression
								.getSourceQuestionReferenceList().get(0);
						sourceQuestionReferenceIsNew = false;
					}
				} else {
					expression = newExpression(condition);
					expressionIsNew = true;

					programmingLanguage = newProgrammingLanguage(expression);
					programmingLanguageIsNew = true;

					sourceQuestionReference = newSourceQuestionReference(expression);
					sourceQuestionReferenceIsNew = true;
				}
			}
		}

		// statement
		// text type in display text is one only by convention i ddi spec
		// other langs is set on parent DisplayText!!!
		StyledText statementTxt = createTextAreaInput(group, Messages
				.getString("StatementItem.editor.textarea"), XmlBeansUtil
				.getTextOnMixedElement(displayText.getTextList().get(0)),
				displayTextIsNew);
		statementTxt.addModifyListener(new GenericTextStyledTextModyfiListener(
				displayText, null,
				GenericTextStyledTextModyfiListener.GET_TEXT_ON_MIXED_ELEMENT,
				GenericTextStyledTextModyfiListener.SET_TEXT_ON_MIXED_ELEMENT,
				model.getDocument().getStatementItem().getDisplayTextList(),
				editorStatus, site, ID));

		// question source
		// 

		// condition
		Composite error = createErrorComposite(group, "");
		Text conditionTxt = createTextInput(group, Messages
				.getString("StatementItem.editor.code"), condition
				.getExpression().getCodeList().get(0).getStringValue(),
				conditionIsNew);
		conditionTxt.addModifyListener(new GenericTextStyledTextModyfiListener(
				condition, new ProgrammingLanguageGetSet(), null, null,
				displayText.getTextList(), editorStatus, site, ID));

		// app lang
		Text programmingLanguageTxt = createTextInput(group, Messages
				.getString("StatementItem.editor.programlang"),
				condition.getExpression().getCodeList().get(0)
						.getProgrammingLanguage() == null ? "" : condition
						.getExpression().getCodeList().get(0)
						.getProgrammingLanguage(), displayTextIsNew);

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
		editorStatus.clearChanged();
	}

	public class ProgrammingLanguageGetSet implements GenericGetSetStringValue {
		@Override
		public String getStringValue(Object obj) {
			if (obj instanceof ConditionalTextType) {
				ConditionalTextType xmlObject = (ConditionalTextType) obj;
				return xmlObject.getExpression().getCodeList().get(0)
						.getStringValue();
			}
			return null;
		}

		@Override
		public void setStringValue(Object obj, Object text) {
			if (obj instanceof ConditionalTextType) {
				ConditionalTextType xmlObject = (ConditionalTextType) obj;
				xmlObject.getExpression().getCodeList().get(0).setStringValue(
						text.toString());
			}
		}

		@Override
		public Object getObject(Object obj) {
			if (obj instanceof ConditionalTextType) {
				ConditionalTextType xmlObject = (ConditionalTextType) obj;
				return xmlObject.getExpression().getCodeList().get(0);
			}
			return null;
		}
	}

	public class StatementModyfiListener implements ModifyListener {
		private StructuredStringType name;
		private List<StructuredStringType> list;
		private EditorStatus editorStatus;

		public StatementModyfiListener(StructuredStringType item,
				List<StructuredStringType> list, EditorStatus editorStatus) {
			this.name = name;
			this.list = list;
			this.editorStatus = editorStatus;
		}

		@Override
		public void modifyText(ModifyEvent e) {
			editorStatus.setChanged();
			StyledText text = (StyledText) e.getSource();
			XmlBeansUtil.setTextOnMixedElement(name, text.getText());
			if ((Boolean) text.getData(Editor.NEW_ITEM)) {
				list.add(name);
				for (StructuredStringType test : list) {
					if (XmlBeansUtil.getTextOnMixedElement(name).equals(
							text.getText())) {
						name = test;
					}
				}
				text.setData(Editor.NEW_ITEM, Boolean.FALSE);
			}
		}
	}

	private ConditionalTextType newCondition() {
		ConditionalTextType conditionNew = ConditionalTextDocument.Factory
				.newInstance().addNewConditionalText();
		return conditionNew;
	}

	private CodeType newExpression(ConditionalTextType condition) {
		return condition.addNewExpression();
	}

	private ProgrammingLanguageCodeType newProgrammingLanguage(
			CodeType expression) {
		return expression.addNewCode();
	}

	private ReferenceType newSourceQuestionReference(CodeType expression) {
		return expression.addNewSourceQuestionReference();
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
