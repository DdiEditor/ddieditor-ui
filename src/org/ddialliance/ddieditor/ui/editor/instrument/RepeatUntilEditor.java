package org.ddialliance.ddieditor.ui.editor.instrument;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.ddialliance.ddi3.xml.xmlbeans.reusable.ProgrammingLanguageCodeType;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.persistenceaccess.maintainablelabel.MaintainableLightLabelQueryResult;
import org.ddialliance.ddieditor.ui.dbxml.instrument.RepeatUntilDao;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddieditor.ui.editor.widgetutil.genericmodifylistener.TextStyledTextModyfiListener;
import org.ddialliance.ddieditor.ui.editor.widgetutil.referenceselection.ReferenceSelectionAdapter;
import org.ddialliance.ddieditor.ui.editor.widgetutil.referenceselection.ReferenceSelectionCombo;
import org.ddialliance.ddieditor.ui.model.ModelIdentifingType;
import org.ddialliance.ddieditor.ui.model.instrument.RepeatUntil;
import org.ddialliance.ddieditor.ui.model.translationdialoginput.DescriptionTdI;
import org.ddialliance.ddieditor.ui.model.translationdialoginput.LabelTdI;
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

public class RepeatUntilEditor extends Editor {
	public static final String ID = "org.ddialliance.ddieditor.ui.editor.instrument.RepeatUntilEditor";
	RepeatUntil modelImpl;

	public RepeatUntilEditor() {
		super(
				Messages
						.getString("RepeatUntilEditor.label.RepeatUntilEditorLabel.RepeatUntilEditor"),
				Messages
						.getString("RepeatUntilEditor.label.useTheEditorLabel.Description"));
		this.dao = new RepeatUntilDao();
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
		modelImpl = (RepeatUntil) model;
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		parent.setLayout(new GridLayout());
		createTabFolder(getComposite_1());

		// main tab
		TabItem tabItem = createTabItem(Messages
				.getString("RepeatUntil.editor.tabdisplaytext"));
		Group group = createGroup(tabItem, Messages
				.getString("RepeatUntil.editor.groupdisplaytext"));

		// until condition
		Composite error = createErrorComposite(group, "");
		ProgrammingLanguageCodeType ifProgrammingLanguageCode = modelImpl
				.getUntilCondition();
		Text conditionTxt = createTextInput(group, Messages
				.getString("RepeatUntil.editor.until"),
				ifProgrammingLanguageCode == null ? ""
						: ifProgrammingLanguageCode.getStringValue(), false);
		conditionTxt.addModifyListener(new TextStyledTextModyfiListener(
				modelImpl, ProgrammingLanguageCodeType.class,
				getEditorIdentification()));

		// until condition lang
		String programmingLanguage = ifProgrammingLanguageCode == null ? ""
				: ifProgrammingLanguageCode.getProgrammingLanguage();
		if (programmingLanguage == null) {
			programmingLanguage = "";
		}
		Text programmingLanguageTxt = createTextInput(group, Messages
				.getString("RepeatUntil.editor.untilprogramlang"),
				programmingLanguage, false);
		programmingLanguageTxt
				.addModifyListener(new TextStyledTextModyfiListener(modelImpl,
						ModelIdentifingType.Type_A.class,
						getEditorIdentification()));

		// condition ref
		MaintainableLightLabelQueryResult controlConstructRefListTemp = null;
		try {
			controlConstructRefListTemp = DdiManager.getInstance()
					.getInstrumentLabel(null, null, null, null);
		} catch (DDIFtpException e) {
			DialogUtil.errorDialog(getSite().getShell(), ID, null, e
					.getMessage(), e);
		}
		List<LightXmlObjectType> controlConstructRefList = new ArrayList<LightXmlObjectType>();
		for (LinkedList<LightXmlObjectType> lightXmlObjectList : controlConstructRefListTemp
				.getResult().values()) {
			controlConstructRefList.addAll(lightXmlObjectList);
		}
		controlConstructRefListTemp = null;

		ReferenceSelectionCombo thenRefSelectCombo = createRefSelection(group,
				Messages.getString("RepeatUntil.editor.untilref"), Messages
						.getString("RepeatUntil.editor.untilref"), modelImpl
						.getUntilReference(), controlConstructRefList, false);
		thenRefSelectCombo.addSelectionListener(Messages
				.getString("RepeatUntil.editor.untilref"),
				controlConstructRefList, new ReferenceSelectionAdapter(
						thenRefSelectCombo, modelImpl,
						ModelIdentifingType.Type_B.class,
						getEditorIdentification()));

		// description tab
		// name
		TabItem tabItem2 = createTabItem(Messages
				.getString("editor.label.description"));
		Group group2 = createGroup(tabItem2, Messages
				.getString("editor.label.description"));

		try {
			Text txt = createLabelInput(group2, Messages
					.getString("editor.label.label"), modelImpl.getDocument()
					.getRepeatUntil().getLabelList(), modelImpl.getDocument()
					.getRepeatUntil().getId());
			createTranslation(group2, Messages
					.getString("editor.button.translate"), modelImpl
					.getDocument().getControlConstruct().getLabelList(),
					new LabelTdI(), "", txt);
			StyledText styledText = createStructuredStringInput(group2,
					Messages.getString("editor.label.description"), modelImpl
							.getDocument().getRepeatUntil()
							.getDescriptionList(), modelImpl.getDocument()
							.getRepeatUntil().getId());
			createTranslation(group2, Messages
					.getString("editor.button.translate"), modelImpl
					.getDocument().getControlConstruct().getDescriptionList(),
					new DescriptionTdI(), "", styledText);
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
