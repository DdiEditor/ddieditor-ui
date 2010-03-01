package org.ddialliance.ddieditor.ui.editor.instrument;

import org.ddialliance.ddi3.xml.xmlbeans.reusable.CodeType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.ProgrammingLanguageCodeType;
import org.ddialliance.ddieditor.ui.dbxml.instrument.LoopDao;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddieditor.ui.editor.widgetutil.genericmodifylistener.TextStyledTextModyfiListener;
import org.ddialliance.ddieditor.ui.model.ModelIdentifingType;
import org.ddialliance.ddieditor.ui.model.instrument.Loop;
import org.ddialliance.ddieditor.ui.perspective.IAutoChangePerspective;
import org.ddialliance.ddieditor.ui.view.Messages;
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

	public LoopEditor() {
		super(
				Messages
						.getString("LoopEditorEditor.label.LoopEditorEditorLabel.LoopEditor"),
				Messages
						.getString("LoopEditorEditor.label.useTheEditorLabel.Description"));
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
		TabItem tabItem = createTabItem(Messages
				.getString("ComputationItem.editor.tabdisplaytext"));
		Group group = createGroup(tabItem, Messages
				.getString("ComputationItem.editor.groupdisplaytext"));

		// init value
		Composite error = createErrorComposite(group, "");
		ProgrammingLanguageCodeType programmingLanguageCode = modelImpl
				.getCode(modelImpl.getInitialValue());
		Text conditionTxt = createTextInput(group, Messages
				.getString("ComputationItem.editor.computation"),
				programmingLanguageCode == null ? "" : programmingLanguageCode
						.getStringValue(), false);
		conditionTxt.addModifyListener(new TextStyledTextModyfiListener(
				modelImpl, ProgrammingLanguageCodeType.class,
				getEditorIdentification()));

		// init value program lang
		String programmingLanguage = programmingLanguageCode == null ? ""
				: programmingLanguageCode.getProgrammingLanguage();
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

		// init value source question ref
		modelImpl.getQuestionReference(modelImpl.getInitialValue());

		// loop while

		// loop while program lang

		// loop while source question ref

		// step value

		// step value program lang

		// step value source question ref

		// control construct ref

		// id tab
		createPropertiesTab(getTabFolder());

		// xml tab
		createXmlTab(modelImpl);

		editorStatus.clearChanged();
	}
}
