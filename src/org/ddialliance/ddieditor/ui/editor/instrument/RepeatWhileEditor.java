package org.ddialliance.ddieditor.ui.editor.instrument;

import java.util.ArrayList;
import java.util.List;

import org.ddialliance.ddi3.xml.xmlbeans.reusable.ProgrammingLanguageCodeType;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.dbxml.instrument.RepeatWhileDao;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddieditor.ui.editor.widgetutil.genericmodifylistener.TextStyledTextModyfiListener;
import org.ddialliance.ddieditor.ui.editor.widgetutil.referenceselection.ReferenceSelectionAdapter;
import org.ddialliance.ddieditor.ui.editor.widgetutil.referenceselection.ReferenceSelectionCombo;
import org.ddialliance.ddieditor.ui.model.ElementType;
import org.ddialliance.ddieditor.ui.model.ModelIdentifingType;
import org.ddialliance.ddieditor.ui.model.instrument.RepeatWhile;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.Translator;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

public class RepeatWhileEditor extends Editor {

	public static final String ID = "org.ddialliance.ddieditor.ui.editor.instrument.RepeatWhileEditor";
	RepeatWhile modelImpl;

	public RepeatWhileEditor() {
		super(
				Translator
						.trans("RepeatWhileEditor.label.RepeatWhileEditorLabel.RepeatWhileEditor"),
				Translator
						.trans("RepeatWhileEditor.label.useTheEditorLabel.Description"),
				ID);
		this.dao = new RepeatWhileDao();
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
		modelImpl = (RepeatWhile) model;
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		parent.setLayout(new GridLayout());
		createTabFolder(getComposite_1());

		// main tab
		TabItem tabItem = createTabItem(Translator
				.trans("RepeatWhile.editor.tabdisplaytext"));
		Group group = createGroup(tabItem,
				Translator.trans("RepeatWhile.editor.groupdisplaytext"));

		// while condition
		ProgrammingLanguageCodeType ifProgrammingLanguageCode = modelImpl
				.getWhileCondition();
		Text conditionTxt = createTextInput(group,
				Translator.trans("RepeatWhile.editor.while"),
				ifProgrammingLanguageCode == null ? ""
						: ifProgrammingLanguageCode.getStringValue(), false);
		conditionTxt.addModifyListener(new TextStyledTextModyfiListener(
				modelImpl, ProgrammingLanguageCodeType.class,
				getEditorIdentification()));

		// while condition lang
		String programmingLanguage = ifProgrammingLanguageCode == null ? getDefaultCodeProgrammingLanguage()
				: ifProgrammingLanguageCode.getProgrammingLanguage();

		Text programmingLanguageTxt = createTextInput(group,
				Translator.trans("RepeatWhile.editor.whileprogramlang"),
				programmingLanguage, false);
		programmingLanguageTxt
				.addModifyListener(new TextStyledTextModyfiListener(modelImpl,
						ModelIdentifingType.Type_A.class,
						getEditorIdentification()));

		// control construct ref
		List<LightXmlObjectType> controlConstructRefList = new ArrayList<LightXmlObjectType>();
		try {
			controlConstructRefList = DdiManager.getInstance()
					.getControlConstructsLight();
		} catch (Exception e) {
			showError(e);
		}

		ReferenceSelectionCombo thenRefSelectCombo = createRefSelection(group,
				Translator.trans("RepeatWhile.editor.whileref"),
				Translator.trans("RepeatWhile.editor.whileref"),
				modelImpl.getWhileReference(), controlConstructRefList, false, ElementType.CONTROL_CONSTRUCT_SCHEME);
		thenRefSelectCombo.addSelectionListener(Translator
				.trans("RepeatWhile.editor.whileref"),
				new ReferenceSelectionAdapter(thenRefSelectCombo, modelImpl,
						ModelIdentifingType.Type_B.class,
						getEditorIdentification()));

		// description tab
		// name
		TabItem tabItem2 = createTabItem(Translator
				.trans("editor.label.description"));
		Group group2 = createGroup(tabItem2,
				Translator.trans("editor.label.description"));

		try {
			createLabelInput(group2, Translator.trans("editor.label.name"),
					modelImpl.getDocument().getRepeatWhile().getLabelList(),
					modelImpl.getDocument().getRepeatWhile().getId());

			createStructuredStringInput(group2,
					Translator.trans("editor.label.description"), modelImpl
							.getDocument().getRepeatWhile()
							.getDescriptionList(), modelImpl.getDocument()
							.getRepeatWhile().getId());

		} catch (DDIFtpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// id tab
		createPropertiesTab(getTabFolder());

		// xml tab
		createXmlTab(modelImpl);

		editorStatus.clearChanged();
	}
}
