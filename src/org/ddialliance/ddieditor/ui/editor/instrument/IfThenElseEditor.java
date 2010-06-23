package org.ddialliance.ddieditor.ui.editor.instrument;

import java.text.MessageFormat;
import java.util.List;

import org.ddialliance.ddi3.xml.xmlbeans.reusable.ProgrammingLanguageCodeType;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.dbxml.instrument.IfThenElseDao;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddieditor.ui.editor.widgetutil.genericmodifylistener.TextStyledTextModyfiListener;
import org.ddialliance.ddieditor.ui.editor.widgetutil.referenceselection.ReferenceSelectionAdapter;
import org.ddialliance.ddieditor.ui.editor.widgetutil.referenceselection.ReferenceSelectionCombo;
import org.ddialliance.ddieditor.ui.model.ModelIdentifingType;
import org.ddialliance.ddieditor.ui.model.instrument.IfThenElse;
import org.ddialliance.ddieditor.ui.model.translationdialoginput.DescriptionTdI;
import org.ddialliance.ddieditor.ui.model.translationdialoginput.NameTdI;
import org.ddialliance.ddieditor.ui.perspective.IAutoChangePerspective;
import org.ddialliance.ddieditor.ui.util.DialogUtil;
import org.ddialliance.ddieditor.ui.view.Messages;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

public class IfThenElseEditor extends Editor implements IAutoChangePerspective {
	public static final String ID = "org.ddialliance.ddieditor.ui.editor.instrument.IfThenElseEditor";
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			IfThenElseEditor.class);
	private List<LightXmlObjectType> questionRefList;
	private IfThenElse modelImpl;

	public IfThenElseEditor() {
		super(
				Messages
						.getString("IfThenElseEditor.label.IfThenElseEditorLabel.IfThenElseEditor"),
				Messages
						.getString("IfThenElseEditor.label.useTheEditorLabel.Description"));
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
		TabItem tabItem = createTabItem(Messages
				.getString("IfThenElse.editor.tabdisplaytext"));
		Group group = createGroup(tabItem, Messages
				.getString("IfThenElse.editor.groupdisplaytext"));

		// if condition
		Composite error = createErrorComposite(group, "");
		ProgrammingLanguageCodeType ifProgrammingLanguageCode = modelImpl
				.getIfCondition();
		Text conditionTxt = createTextInput(group, Messages
				.getString("IfThenElse.editor.if"),
				ifProgrammingLanguageCode == null ? ""
						: ifProgrammingLanguageCode.getStringValue(), false);
		conditionTxt.addModifyListener(new TextStyledTextModyfiListener(
				modelImpl, ProgrammingLanguageCodeType.class,
				getEditorIdentification()));

		// if condition lang
		String programmingLanguage = ifProgrammingLanguageCode == null ? ""
				: ifProgrammingLanguageCode.getProgrammingLanguage();
		if (programmingLanguage == null) {
			programmingLanguage = "";
		}
		Text programmingLanguageTxt = createTextInput(group, Messages
				.getString("StatementItem.editor.programlang"),
				programmingLanguage, false);
		programmingLanguageTxt
				.addModifyListener(new TextStyledTextModyfiListener(modelImpl,
						ModelIdentifingType.Type_A.class,
						getEditorIdentification()));

		// if question ref
		try {
			questionRefList = DdiManager.getInstance().getQuestionItemsLight(
					null, null, null, null).getLightXmlObjectList()
					.getLightXmlObjectList();
		} catch (Exception e) {
			DialogUtil.errorDialog(getSite().getShell(), ID, null, e
					.getMessage(), e);
		}
		ReferenceSelectionCombo questionRefSelectCombo = createRefSelection(
				group, Messages.getString("IfThenElse.editor.ifquestionref"),
				Messages.getString("IfThenElse.editor.ifquestionref"),
				modelImpl.getIfQuestionReference(), questionRefList, false);
		questionRefSelectCombo.addSelectionListener(Messages
				.getString("IfThenElse.editor.ifquestionref"), questionRefList,
				new ReferenceSelectionAdapter(questionRefSelectCombo,
						modelImpl, ModelIdentifingType.Type_B.class,
						getEditorIdentification()));

		// then ref
		// MaintainableLightLabelQueryResult controlConstructRefListTemp = null;
		// try {
		// controlConstructRefListTemp = DdiManager.getInstance()
		// .getInstrumentLabel(null, null, null, null);
		// } catch (DDIFtpException e) {
		// DialogUtil.errorDialog(getSite().getShell(), ID, null, e
		// .getMessage(), e);
		// }
		List<LightXmlObjectType> controlConstructRefList = null;
		try {
			controlConstructRefList = DdiManager.getInstance()
					.getQuestionConstructsLight(null, null, null, null)
					.getLightXmlObjectList().getLightXmlObjectList();
		} catch (Exception e) {
			DialogUtil
			.errorDialog(getEditorSite(), ID, null, e.getMessage(), e);
		}// new ArrayList<LightXmlObjectType>();
		// for (LinkedList<LightXmlObjectType> lightXmlObjectList :
		// controlConstructRefListTemp
		// .getResult().values()) {
		// controlConstructRefList.addAll(lightXmlObjectList);
		// }
		// controlConstructRefListTemp = null;

		ReferenceSelectionCombo thenRefSelectCombo = createRefSelection(group,
				Messages.getString("IfThenElse.editor.thenref"), Messages
						.getString("IfThenElse.editor.thenref"), modelImpl
						.getThenReference(), controlConstructRefList, false);
		thenRefSelectCombo.addSelectionListener(Messages
				.getString("IfThenElse.editor.thenref"),
				controlConstructRefList, new ReferenceSelectionAdapter(
						thenRefSelectCombo, modelImpl,
						ModelIdentifingType.Type_C.class,
						getEditorIdentification()));

		// else ref
		ReferenceSelectionCombo elseRefSelectCombo = createRefSelection(group,
				Messages.getString("IfThenElse.editor.elseref"), Messages
						.getString("IfThenElse.editor.elseref"), modelImpl
						.getElseReference(), controlConstructRefList, false);
		elseRefSelectCombo.addSelectionListener(Messages
				.getString("IfThenElse.editor.elseref"),
				controlConstructRefList, new ReferenceSelectionAdapter(
						elseRefSelectCombo, modelImpl,
						ModelIdentifingType.Type_D.class,
						getEditorIdentification()));

		// description tab
		// name
		TabItem tabItem2 = createTabItem(Messages
				.getString("editor.label.description"));
		Group group2 = createGroup(tabItem2, Messages
				.getString("editor.label.description"));

		try {
			createNameInput(group2, Messages.getString("editor.label.name"),
					modelImpl.getDocument().getIfThenElse()
							.getConstructNameList(), modelImpl.getDocument()
							.getIfThenElse().getId());

			createTranslation(group2, Messages
					.getString("editor.button.translate"), modelImpl
					.getDocument().getIfThenElse().getConstructNameList(),
					new NameTdI(), "");
			
			createStructuredStringInput(group2, Messages
					.getString("editor.label.description"), modelImpl
					.getDocument().getIfThenElse().getDescriptionList(),
					modelImpl.getDocument().getIfThenElse().getId());
			createTranslation(group2, Messages
					.getString("editor.button.translate"), modelImpl
					.getDocument().getIfThenElse().getDescriptionList(),
					new DescriptionTdI(), "");
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
		return MessageFormat.format(Messages
				.getString("perspective.switch.dialogtext"), Messages
				.getString("perspective.instruments"));
	}
}
