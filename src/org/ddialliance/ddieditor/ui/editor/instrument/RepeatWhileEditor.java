package org.ddialliance.ddieditor.ui.editor.instrument;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.ddialliance.ddi3.xml.xmlbeans.reusable.ProgrammingLanguageCodeType;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.persistenceaccess.maintainablelabel.MaintainableLightLabelQueryResult;
import org.ddialliance.ddieditor.ui.dbxml.instrument.RepeatWhileDao;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddieditor.ui.editor.widgetutil.genericmodifylistener.TextStyledTextModyfiListener;
import org.ddialliance.ddieditor.ui.editor.widgetutil.referenceselection.ReferenceSelectionAdapter;
import org.ddialliance.ddieditor.ui.editor.widgetutil.referenceselection.ReferenceSelectionCombo;
import org.ddialliance.ddieditor.ui.model.ModelIdentifingType;
import org.ddialliance.ddieditor.ui.model.instrument.RepeatWhile;
import org.ddialliance.ddieditor.ui.util.DialogUtil;
import org.ddialliance.ddieditor.ui.view.Messages;
import org.ddialliance.ddiftp.util.DDIFtpException;
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
				Messages
						.getString("RepeatWhileEditor.label.RepeatWhileEditorLabel.RepeatWhileEditor"),
				Messages
						.getString("RepeatWhileEditor.label.useTheEditorLabel.Description"));
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
		TabItem tabItem = createTabItem(Messages
				.getString("RepeatWhile.editor.tabdisplaytext"));
		Group group = createGroup(tabItem, Messages
				.getString("RepeatWhile.editor.groupdisplaytext"));

		// while condition
		Composite error = createErrorComposite(group, "");
		ProgrammingLanguageCodeType ifProgrammingLanguageCode = modelImpl
				.getWhileCondition();
		Text conditionTxt = createTextInput(group, Messages
				.getString("RepeatWhile.editor.while"),
				ifProgrammingLanguageCode == null ? ""
						: ifProgrammingLanguageCode.getStringValue(), false);
		conditionTxt.addModifyListener(new TextStyledTextModyfiListener(
				modelImpl, ProgrammingLanguageCodeType.class,
				getEditorIdentification()));

		// while condition lang
		String programmingLanguage = ifProgrammingLanguageCode == null ? ""
				: ifProgrammingLanguageCode.getProgrammingLanguage();
		if (programmingLanguage == null) {
			programmingLanguage = "";
		}
		Text programmingLanguageTxt = createTextInput(group, Messages
				.getString("RepeatWhile.editor.whileprogramlang"),
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
				Messages.getString("RepeatWhile.editor.whileref"), Messages
						.getString("RepeatWhile.editor.whileref"), modelImpl
						.getWhileReference(), controlConstructRefList, false);
		thenRefSelectCombo.addSelectionListener(Messages
				.getString("RepeatWhile.editor.whileref"),
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
			createLabelInput(group2, Messages.getString("editor.label.name"),
					modelImpl.getDocument().getRepeatWhile().getLabelList(),
					modelImpl.getDocument().getRepeatWhile().getId());

			createStructuredStringInput(group2, Messages
					.getString("editor.label.description"), modelImpl
					.getDocument().getRepeatWhile().getDescriptionList(),
					modelImpl.getDocument().getRepeatWhile().getId());

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
