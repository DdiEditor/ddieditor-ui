package org.ddialliance.ddieditor.ui.editor.instrument;

import java.text.MessageFormat;

import org.ddialliance.ddi3.xml.xmlbeans.datacollection.TextType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.ProgrammingLanguageCodeType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.StructuredStringType;
import org.ddialliance.ddieditor.ui.dbxml.instrument.StatementItemDao;
import org.ddialliance.ddieditor.ui.dialogs.translationdialoginput.DescriptionTdI;
import org.ddialliance.ddieditor.ui.dialogs.translationdialoginput.DynamicTextTdI;
import org.ddialliance.ddieditor.ui.dialogs.translationdialoginput.LabelTdI;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddieditor.ui.editor.widgetutil.genericmodifylistener.TextStyledTextModyfiListener;
import org.ddialliance.ddieditor.ui.model.ModelIdentifingType;
import org.ddialliance.ddieditor.ui.model.instrument.StatementItem;
import org.ddialliance.ddieditor.ui.perspective.IAutoChangePerspective;
import org.ddialliance.ddieditor.ui.util.DialogUtil;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.Translator;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.ddialliance.ddiftp.util.xml.XmlBeansUtil;
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
	private StatementItem modelImpl;

	public StatementItemEditor() {
		super(
				Translator
						.trans("StatementItemEditor.label.StatementItemEditorLabel.StatementItemEditor"),
				Translator
						.trans("StatementItemEditor.label.useTheEditorLabel.Description"),
				ID);
		dao = new StatementItemDao();
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
		this.modelImpl = (StatementItem) model;
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
		TabItem tabItem = createTabItem(Translator
				.trans("StatementItem.editor.tabdisplaytext"));
		Group group = createGroup(tabItem,
				Translator.trans("StatementItem.editor.groupdisplaytext"));

		// statement
		// text type in display text is one only by convention i ddi spec
		// other langs is set on parent DisplayText!!!
		// check for ddi bug best is LiteralText::Text
		StructuredStringType text = null;
		try {
			text = modelImpl.getText();
		} catch (DDIFtpException e) {
			DialogUtil.errorDialog(getSite().getShell(), ID, null,
					e.getMessage(), e);
			return;
		}
		StyledText statementTxt = createTextAreaInput(group,
				Translator.trans("StatementItem.editor.textarea"),
				text == null ? "" : XmlBeansUtil.getTextOnMixedElement(text),
				false);
		statementTxt.addModifyListener(new TextStyledTextModyfiListener(model,
				TextType.class, getEditorIdentification()));
		createTranslation(
				group,
				Translator.trans("editor.button.translate"),
				modelImpl.getDocument().getStatementItem().getDisplayTextList(),
				new DynamicTextTdI(), "", statementTxt);

		// condition
		// Composite error = createErrorComposite(group, "");
		ProgrammingLanguageCodeType programmingLanguageCode = null;
		try {
			programmingLanguageCode = modelImpl.getProgrammingLanguageCode();
		} catch (DDIFtpException e) {
			DialogUtil
					.errorDialog(getEditorSite(), ID, null, e.getMessage(), e);
		}
		Text conditionTxt = createTextInput(
				group,
				Translator.trans("StatementItem.editor.code"),
				programmingLanguageCode == null ? "" : programmingLanguageCode
						.getStringValue(), false);
		conditionTxt.addModifyListener(new TextStyledTextModyfiListener(model,
				ProgrammingLanguageCodeType.class, getEditorIdentification()));

		// app lang
		String programmingLanguage = programmingLanguageCode == null ? getDefaultCodeProgrammingLanguage()
				: programmingLanguageCode.getProgrammingLanguage();

		Text programmingLanguageTxt = createTextInput(group,
				Translator.trans("StatementItem.editor.programlang"),
				programmingLanguage, false);
		programmingLanguageTxt
				.addModifyListener(new TextStyledTextModyfiListener(model,
						ModelIdentifingType.Type_A.class,
						getEditorIdentification()));

		// description tab
		TabItem tabItem2 = createTabItem(Translator
				.trans("editor.label.description"));
		Group group2 = createGroup(tabItem2,
				Translator.trans("editor.label.description"));

		try {
			// label
			Text txt = createLabelInput(group2,
					Translator.trans("editor.label.label"), modelImpl
							.getDocument().getStatementItem().getLabelList(),
					modelImpl.getDocument().getStatementItem().getId());
			createTranslation(group2,
					Translator.trans("editor.button.translate"), modelImpl
							.getDocument().getStatementItem().getLabelList(),
					new LabelTdI(), "", txt);

			// description
			StyledText styledText = createStructuredStringInput(group2,
					Translator.trans("editor.label.description"), modelImpl
							.getDocument().getStatementItem()
							.getDescriptionList(), modelImpl.getDocument()
							.getStatementItem().getId());
			createTranslation(group2,
					Translator.trans("editor.button.translate"), modelImpl
							.getDocument().getStatementItem()
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

		editorStatus.clearChanged();
	}

	@Override
	public String getPerspectiveSwitchDialogText() {
		return MessageFormat.format(
				Translator.trans("perspective.switch.dialogtext"),
				Translator.trans("perspective.instruments"));
	}
}
