package org.ddialliance.ddieditor.ui.editor.instrument;

import java.util.ArrayList;
import java.util.List;

import org.ddialliance.ddi3.xml.xmlbeans.reusable.ProgrammingLanguageCodeType;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.dbxml.instrument.ComputationItemDao;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddieditor.ui.editor.widgetutil.genericmodifylistener.TextStyledTextModyfiListener;
import org.ddialliance.ddieditor.ui.editor.widgetutil.referenceselection.ReferenceSelectionAdapter;
import org.ddialliance.ddieditor.ui.editor.widgetutil.referenceselection.ReferenceSelectionCombo;
import org.ddialliance.ddieditor.ui.model.ModelIdentifingType;
import org.ddialliance.ddieditor.ui.model.instrument.ComputationItem;
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

public class ComputationItemEditor extends Editor {
	public static final String ID = "org.ddialliance.ddieditor.ui.editor.instrument.ComputationItemEditor";
	ComputationItem modelImpl;

	public ComputationItemEditor() {
		super(
				Messages
						.getString("ComputationItemEditor.label.ComputationItemEditorLabel.ComputationItemEditor"),
				Messages
						.getString("ComputationItemEditor.label.useTheEditorLabel.Description"));
		this.dao = new ComputationItemDao();
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
		modelImpl = (ComputationItem) model;
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		parent.setLayout(new GridLayout());
		createTabFolder(getComposite_1());

		// main tab
		TabItem tabItem = createTabItem(Messages
				.getString("ComputationItem.editor.tabdisplaytext"));
		Group group = createGroup(tabItem, Messages
				.getString("ComputationItem.editor.groupdisplaytext"));

		// computation condition
		Composite error = createErrorComposite(group, "");
		ProgrammingLanguageCodeType ifProgrammingLanguageCode = modelImpl
				.getUntilCondition();
		Text conditionTxt = createTextInput(group, Messages
				.getString("ComputationItem.editor.computation"),
				ifProgrammingLanguageCode == null ? ""
						: ifProgrammingLanguageCode.getStringValue(), false);
		conditionTxt.addModifyListener(new TextStyledTextModyfiListener(
				modelImpl, ProgrammingLanguageCodeType.class,
				getEditorIdentification()));

		// computation condition lang
		String programmingLanguage = ifProgrammingLanguageCode == null ? ""
				: ifProgrammingLanguageCode.getProgrammingLanguage();
		if (programmingLanguage == null) {
			programmingLanguage = "";
		}
		Text programmingLanguageTxt = createTextInput(group, Messages
				.getString("ComputationItem.editor.computationprogramlang"),
				programmingLanguage, false);
		programmingLanguageTxt
				.addModifyListener(new TextStyledTextModyfiListener(modelImpl,
						ModelIdentifingType.Type_A.class,
						getEditorIdentification()));

		// variable ref
		List<LightXmlObjectType> variableRefList = new ArrayList<LightXmlObjectType>();
		try {
			variableRefList = DdiManager.getInstance().getVariablesLight(null,
					null, null, null).getLightXmlObjectList()
					.getLightXmlObjectList();
		} catch (Exception e) {
			DialogUtil.errorDialog(getSite().getShell(), ID, null, e
					.getMessage(), e);
		}

		ReferenceSelectionCombo thenRefSelectCombo = createRefSelection(
				group,
				Messages
						.getString("ComputationItem.editor.computationvariableref"),
				Messages
						.getString("ComputationItem.editor.computationvariableref"),
				modelImpl.getAssignedVariableReference(), variableRefList,
				false);
		thenRefSelectCombo.addSelectionListener(Messages.getString("ComputationItem.editor.computationvariableref"),
				new ReferenceSelectionAdapter(thenRefSelectCombo, modelImpl, ModelIdentifingType.Type_B.class,
						getEditorIdentification()));

		// description tab
		TabItem tabItem2 = createTabItem(Messages
				.getString("editor.label.description"));
		Group group2 = createGroup(tabItem2, Messages
				.getString("editor.label.description"));

		try {
			Text txt = createLabelInput(group2, Messages.getString("editor.label.label"), modelImpl.getDocument()
					.getComputationItem().getLabelList(), modelImpl.getDocument().getComputationItem().getId());

			createTranslation(group2, Messages.getString("editor.button.translate"), modelImpl.getDocument()
					.getComputationItem().getLabelList(), new LabelTdI(), "", txt);

			StyledText styledText = createStructuredStringInput(group2, Messages.getString("editor.label.description"),
					modelImpl.getDocument().getComputationItem().getDescriptionList(), modelImpl.getDocument()
							.getComputationItem().getId());
			createTranslation(group2, Messages.getString("editor.button.translate"), modelImpl.getDocument()
					.getComputationItem().getDescriptionList(), new DescriptionTdI(), "", styledText);
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
