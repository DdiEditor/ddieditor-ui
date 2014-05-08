package org.ddialliance.ddieditor.ui.editor.instrument;

import java.util.ArrayList;
import java.util.List;

import org.ddialliance.ddi3.xml.xmlbeans.reusable.ProgrammingLanguageCodeType;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.dbxml.instrument.LoopDao;
import org.ddialliance.ddieditor.ui.dialogs.translationdialoginput.DescriptionTdI;
import org.ddialliance.ddieditor.ui.dialogs.translationdialoginput.LabelTdI;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddieditor.ui.editor.widgetutil.genericmodifylistener.TextStyledTextModyfiListener;
import org.ddialliance.ddieditor.ui.editor.widgetutil.referenceselection.ReferenceSelectionAdapter;
import org.ddialliance.ddieditor.ui.editor.widgetutil.referenceselection.ReferenceSelectionCombo;
import org.ddialliance.ddieditor.ui.model.ElementType;
import org.ddialliance.ddieditor.ui.model.ModelIdentifingType;
import org.ddialliance.ddieditor.ui.model.instrument.Loop;
import org.ddialliance.ddieditor.ui.perspective.IAutoChangePerspective;
import org.ddialliance.ddieditor.ui.util.DialogUtil;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.Translator;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

public class LoopEditor extends Editor implements IAutoChangePerspective {
	public static final String ID = "org.ddialliance.ddieditor.ui.editor.instrument.LoopEditor";
	Loop modelImpl;
	private List<LightXmlObjectType> questionRefList;

	public LoopEditor() {
		super(Translator.trans("LoopEditor.label"), Translator
				.trans("LoopEditor.label.Description"), ID);
		this.dao = new LoopDao();
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
		modelImpl = (Loop) model;
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		parent.setLayout(new GridLayout());
		createTabFolder(getComposite_1());

		// main tab
		TabItem tabItem = createTabItem(Translator.trans("Loop"));
		Group group = createGroup(tabItem, Translator.trans("Loop"));

		// loop variable ref
		List<LightXmlObjectType> variableRefList = new ArrayList<LightXmlObjectType>();
		try {
			variableRefList = DdiManager.getInstance()
					.getVariablesLight(null, null, null, null)
					.getLightXmlObjectList().getLightXmlObjectList();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ReferenceSelectionCombo variableRefSelectCombo = createRefSelection(
				group, Translator.trans("LoopEditor.label.variableref"),
				Translator.trans("LoopEditor.label.variableref"),
				modelImpl.getLoopVariableReference(), variableRefList, false, ElementType.VARIABLE);
		variableRefSelectCombo.addSelectionListener(Translator
				.trans("LoopEditor.label.variableref"),
				new ReferenceSelectionAdapter(variableRefSelectCombo,
						modelImpl, ModelIdentifingType.Type_K.class,
						getEditorIdentification()));

		// control construct ref
		List<LightXmlObjectType> controlConstructRefList = new ArrayList<LightXmlObjectType>();
		try {
			controlConstructRefList = DdiManager.getInstance()
					.getControlConstructsLight();
		} catch (Exception e) {
			showError(e);
		}

		ReferenceSelectionCombo controlConstructRefSelectCombo = createRefSelection(
				group, Translator.trans("ControlConstruct.ref"),
				Translator.trans("ControlConstruct.ref"),
				modelImpl.getControlConstructReference(),
				controlConstructRefList, false, ElementType.CONTROL_CONSTRUCT_SCHEME);
		controlConstructRefSelectCombo.addSelectionListener(Translator
				.trans("ControlConstruct.ref"),
				new ReferenceSelectionAdapter(controlConstructRefSelectCombo,
						modelImpl, ModelIdentifingType.Type_J.class,
						getEditorIdentification()));

		// init value
		// Composite error = createErrorComposite(group, "");
		Group initValueGroup = createSubGroup(group,
				Translator.trans("LoopEditor.label.InitValue"));

		ProgrammingLanguageCodeType initValue = modelImpl.getCode(modelImpl
				.getInitialValue());
		Text initValueTxt = createTextInput(initValueGroup,
				Translator.trans("LoopEditor.label.InitValue"),
				initValue == null ? "" : initValue.getStringValue(), false);
		initValueTxt.addModifyListener(new TextStyledTextModyfiListener(
				modelImpl, ModelIdentifingType.Type_A.class,
				getEditorIdentification()));

		// init value program lang
		String appLangInitValue = initValue == null ? getDefaultCodeProgrammingLanguage()
				: initValue.getProgrammingLanguage();

		Text appLangInitValueTxt = createTextInput(initValueGroup,
				Translator.trans("LoopEditor.label.InitValue.programlang"),
				appLangInitValue, false);
		appLangInitValueTxt.addModifyListener(new TextStyledTextModyfiListener(
				modelImpl, ModelIdentifingType.Type_B.class,
				getEditorIdentification()));

		// init value source question ref
		try {
			questionRefList = DdiManager.getInstance()
					.getQuestionItemsLight(null, null, null, null)
					.getLightXmlObjectList().getLightXmlObjectList();
		} catch (Exception e) {
			DialogUtil.errorDialog(getSite().getShell(), ID, null,
					e.getMessage(), e);
		}
		ReferenceSelectionCombo initValueQuestRefSelectCombo = createRefSelection(
				initValueGroup,
				Translator.trans("IfThenElse.editor.ifquestionref"),
				Translator.trans("IfThenElse.editor.ifquestionref"),
				modelImpl.getQuestionReference(modelImpl.getInitialValue()),
				questionRefList, false, ElementType.QUESTION_ITEM);
		initValueQuestRefSelectCombo.addSelectionListener(Translator
				.trans("IfThenElse.editor.ifquestionref"),
				new ReferenceSelectionAdapter(initValueQuestRefSelectCombo,
						modelImpl, ModelIdentifingType.Type_C.class,
						getEditorIdentification()));

		// loop while
		Group loopWhileGroup = createSubGroup(group,
				Translator.trans("LoopEditor.label.loopwhile"));
		ProgrammingLanguageCodeType loopWhile = modelImpl.getCode(modelImpl
				.getLoopWhile());
		Text loopWhileTxt = createTextInput(loopWhileGroup,
				Translator.trans("LoopEditor.label.loopwhile"),
				loopWhile == null ? "" : loopWhile.getStringValue(), false);
		loopWhileTxt.addModifyListener(new TextStyledTextModyfiListener(
				modelImpl, ModelIdentifingType.Type_D.class,
				getEditorIdentification()));

		// loop while program lang
		String appLangLoopWhile = loopWhile == null ? "" : loopWhile
				.getProgrammingLanguage();
		if (appLangLoopWhile == null) {
			appLangLoopWhile = "";
		}
		Text appLangLoopWhileTxt = createTextInput(loopWhileGroup,
				Translator.trans("LoopEditor.label.loopwhile.programlang"),
				appLangLoopWhile, false);
		appLangLoopWhileTxt.addModifyListener(new TextStyledTextModyfiListener(
				modelImpl, ModelIdentifingType.Type_E.class,
				getEditorIdentification()));

		// loop while source question ref
		ReferenceSelectionCombo loopWhileQuestRefSelectCombo = createRefSelection(
				loopWhileGroup,
				Translator.trans("IfThenElse.editor.ifquestionref"),
				Translator.trans("IfThenElse.editor.ifquestionref"),
				modelImpl.getQuestionReference(modelImpl.getLoopWhile()),
				questionRefList, false, ElementType.QUESTION_ITEM);
		loopWhileQuestRefSelectCombo.addSelectionListener(Translator
				.trans("IfThenElse.editor.ifquestionref"),
				new ReferenceSelectionAdapter(loopWhileQuestRefSelectCombo,
						modelImpl, ModelIdentifingType.Type_F.class,
						getEditorIdentification()));

		// step value
		Group stepValueGroup = createSubGroup(group,
				Translator.trans("LoopEditor.label.stepvalue"));
		ProgrammingLanguageCodeType stepValue = modelImpl.getCode(modelImpl
				.getStepValue());
		Text steepValueTxt = createTextInput(stepValueGroup,
				Translator.trans("LoopEditor.label.stepvalue"),
				stepValue == null ? "" : stepValue.getStringValue(), false);
		steepValueTxt.addModifyListener(new TextStyledTextModyfiListener(
				modelImpl, ModelIdentifingType.Type_G.class,
				getEditorIdentification()));

		// step value program lang
		String appLangSteepValue = stepValue == null ? "" : stepValue
				.getProgrammingLanguage();
		if (appLangSteepValue == null) {
			appLangSteepValue = "";
		}
		Text appLangSteepValueTxt = createTextInput(stepValueGroup,
				Translator.trans("LoopEditor.label.stepvalue.programlang"),
				appLangSteepValue, false);
		appLangSteepValueTxt
				.addModifyListener(new TextStyledTextModyfiListener(modelImpl,
						ModelIdentifingType.Type_H.class,
						getEditorIdentification()));

		// step value source question ref
		ReferenceSelectionCombo steepValueQuestRefSelectCombo = createRefSelection(
				stepValueGroup,
				Translator.trans("IfThenElse.editor.ifquestionref"),
				Translator.trans("IfThenElse.editor.ifquestionref"),
				modelImpl.getQuestionReference(modelImpl.getStepValue()),
				questionRefList, false, ElementType.QUESTION_ITEM);
		steepValueQuestRefSelectCombo.addSelectionListener(Translator
				.trans("IfThenElse.editor.ifquestionref"),
				new ReferenceSelectionAdapter(steepValueQuestRefSelectCombo,
						modelImpl, ModelIdentifingType.Type_I.class,
						getEditorIdentification()));

		// description tab
		TabItem tabItem2 = createTabItem(Translator
				.trans("editor.label.description"));
		Group group2 = createGroup(tabItem2,
				Translator.trans("editor.label.description"));

		try {
			Text txt = createLabelInput(group2,
					Translator.trans("editor.label.label"), modelImpl
							.getDocument().getLoop().getLabelList(), modelImpl
							.getDocument().getLoop().getId());

			createTranslation(group2,
					Translator.trans("editor.button.translate"), modelImpl
							.getDocument().getLoop().getLabelList(),
					new LabelTdI(), "", txt);

			StyledText styledText = createStructuredStringInput(group2,
					Translator.trans("editor.label.description"), modelImpl
							.getDocument().getLoop().getDescriptionList(),
					modelImpl.getDocument().getLoop().getId());
			createTranslation(group2,
					Translator.trans("editor.button.translate"), modelImpl
							.getDocument().getLoop().getDescriptionList(),
					new DescriptionTdI(), "", styledText);
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
}
