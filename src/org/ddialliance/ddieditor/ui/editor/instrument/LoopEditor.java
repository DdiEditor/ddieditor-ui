package org.ddialliance.ddieditor.ui.editor.instrument;

import java.util.ArrayList;
import java.util.List;

import org.ddialliance.ddi3.xml.xmlbeans.reusable.ProgrammingLanguageCodeType;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.dbxml.instrument.LoopDao;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddieditor.ui.editor.widgetutil.genericmodifylistener.TextStyledTextModyfiListener;
import org.ddialliance.ddieditor.ui.editor.widgetutil.referenceselection.ReferenceSelectionAdapter;
import org.ddialliance.ddieditor.ui.editor.widgetutil.referenceselection.ReferenceSelectionCombo;
import org.ddialliance.ddieditor.ui.model.ModelIdentifingType;
import org.ddialliance.ddieditor.ui.model.instrument.Loop;
import org.ddialliance.ddieditor.ui.model.translationdialoginput.DescriptionTdI;
import org.ddialliance.ddieditor.ui.model.translationdialoginput.LabelTdI;
import org.ddialliance.ddieditor.ui.perspective.IAutoChangePerspective;
import org.ddialliance.ddieditor.ui.util.DialogUtil;
import org.ddialliance.ddieditor.ui.view.Messages;
import org.ddialliance.ddiftp.util.DDIFtpException;
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
		super(Messages.getString("LoopEditor.label"), Messages
				.getString("LoopEditor.label.Description"));
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
		TabItem tabItem = createTabItem(Messages.getString("Loop"));
		Group group = createGroup(tabItem, Messages.getString("Loop"));

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
				group, Messages.getString("LoopEditor.label.variableref"),
				Messages.getString("LoopEditor.label.variableref"),
				modelImpl.getLoopVariableReference(), variableRefList, false);
		variableRefSelectCombo.addSelectionListener(Messages
				.getString("LoopEditor.label.variableref"), variableRefList,
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
				group, Messages.getString("ControlConstruct.ref"),
				Messages.getString("ControlConstruct.ref"),
				modelImpl.getControlConstructReference(),
				controlConstructRefList, false);
		controlConstructRefSelectCombo.addSelectionListener(Messages
				.getString("ControlConstruct.ref"), controlConstructRefList,
				new ReferenceSelectionAdapter(controlConstructRefSelectCombo,
						modelImpl, ModelIdentifingType.Type_J.class,
						getEditorIdentification()));

		// init value
		// Composite error = createErrorComposite(group, "");
		Group initValueGroup = createSubGroup(group,
				Messages.getString("LoopEditor.label.InitValue"));

		ProgrammingLanguageCodeType initValue = modelImpl.getCode(modelImpl
				.getInitialValue());
		Text initValueTxt = createTextInput(initValueGroup,
				Messages.getString("LoopEditor.label.InitValue"),
				initValue == null ? "" : initValue.getStringValue(), false);
		initValueTxt.addModifyListener(new TextStyledTextModyfiListener(
				modelImpl, ModelIdentifingType.Type_A.class,
				getEditorIdentification()));

		// init value program lang
		String appLangInitValue = initValue == null ? "" : initValue
				.getProgrammingLanguage();
		if (appLangInitValue == null) {
			appLangInitValue = "";
		}
		Text appLangInitValueTxt = createTextInput(initValueGroup,
				Messages.getString("LoopEditor.label.InitValue.programlang"),
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
				Messages.getString("IfThenElse.editor.ifquestionref"),
				Messages.getString("IfThenElse.editor.ifquestionref"),
				modelImpl.getQuestionReference(modelImpl.getInitialValue()),
				questionRefList, false);
		initValueQuestRefSelectCombo.addSelectionListener(Messages
				.getString("IfThenElse.editor.ifquestionref"), questionRefList,
				new ReferenceSelectionAdapter(initValueQuestRefSelectCombo,
						modelImpl, ModelIdentifingType.Type_C.class,
						getEditorIdentification()));

		// loop while
		Group loopWhileGroup = createSubGroup(group,
				Messages.getString("LoopEditor.label.loopwhile"));
		ProgrammingLanguageCodeType loopWhile = modelImpl.getCode(modelImpl
				.getLoopWhile());
		Text loopWhileTxt = createTextInput(loopWhileGroup,
				Messages.getString("LoopEditor.label.loopwhile"),
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
				Messages.getString("LoopEditor.label.loopwhile.programlang"),
				appLangLoopWhile, false);
		appLangLoopWhileTxt.addModifyListener(new TextStyledTextModyfiListener(
				modelImpl, ModelIdentifingType.Type_E.class,
				getEditorIdentification()));

		// loop while source question ref
		ReferenceSelectionCombo loopWhileQuestRefSelectCombo = createRefSelection(
				loopWhileGroup,
				Messages.getString("IfThenElse.editor.ifquestionref"),
				Messages.getString("IfThenElse.editor.ifquestionref"),
				modelImpl.getQuestionReference(modelImpl.getLoopWhile()),
				questionRefList, false);
		loopWhileQuestRefSelectCombo.addSelectionListener(Messages
				.getString("IfThenElse.editor.ifquestionref"), questionRefList,
				new ReferenceSelectionAdapter(loopWhileQuestRefSelectCombo,
						modelImpl, ModelIdentifingType.Type_F.class,
						getEditorIdentification()));

		// step value
		Group stepValueGroup = createSubGroup(group,
				Messages.getString("LoopEditor.label.stepvalue"));
		ProgrammingLanguageCodeType stepValue = modelImpl.getCode(modelImpl
				.getStepValue());
		Text steepValueTxt = createTextInput(stepValueGroup,
				Messages.getString("LoopEditor.label.stepvalue"),
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
				Messages.getString("LoopEditor.label.stepvalue.programlang"),
				appLangSteepValue, false);
		appLangSteepValueTxt
				.addModifyListener(new TextStyledTextModyfiListener(modelImpl,
						ModelIdentifingType.Type_H.class,
						getEditorIdentification()));

		// step value source question ref
		ReferenceSelectionCombo steepValueQuestRefSelectCombo = createRefSelection(
				stepValueGroup,
				Messages.getString("IfThenElse.editor.ifquestionref"),
				Messages.getString("IfThenElse.editor.ifquestionref"),
				modelImpl.getQuestionReference(modelImpl.getStepValue()),
				questionRefList, false);
		steepValueQuestRefSelectCombo.addSelectionListener(Messages
				.getString("IfThenElse.editor.ifquestionref"), questionRefList,
				new ReferenceSelectionAdapter(steepValueQuestRefSelectCombo,
						modelImpl, ModelIdentifingType.Type_I.class,
						getEditorIdentification()));

		// description tab
		TabItem tabItem2 = createTabItem(Messages
				.getString("editor.label.description"));
		Group group2 = createGroup(tabItem2,
				Messages.getString("editor.label.description"));

		try {
			Text txt = createLabelInput(group2,
					Messages.getString("editor.label.label"), modelImpl
							.getDocument().getLoop().getLabelList(), modelImpl
							.getDocument().getLoop().getId());

			createTranslation(group2,
					Messages.getString("editor.button.translate"), modelImpl
							.getDocument().getLoop().getLabelList(),
					new LabelTdI(), "", txt);

			StyledText styledText = createStructuredStringInput(group2,
					Messages.getString("editor.label.description"), modelImpl
							.getDocument().getLoop().getDescriptionList(),
					modelImpl.getDocument().getLoop().getId());
			createTranslation(group2,
					Messages.getString("editor.button.translate"), modelImpl
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
