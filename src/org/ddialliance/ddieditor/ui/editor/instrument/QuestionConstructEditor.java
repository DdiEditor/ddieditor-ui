package org.ddialliance.ddieditor.ui.editor.instrument;

import java.util.List;

import org.ddialliance.ddi3.xml.xmlbeans.datacollection.SpecificSequenceType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.InternationalStringType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.ReferenceType;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.dbxml.instrument.QuestionConstructDao;
import org.ddialliance.ddieditor.ui.dialogs.translationdialoginput.DescriptionTdI;
import org.ddialliance.ddieditor.ui.dialogs.translationdialoginput.LabelTdI;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddieditor.ui.editor.widgetutil.genericselectionlistener.GenericComboSelectionListener;
import org.ddialliance.ddieditor.ui.editor.widgetutil.referenceselection.ReferenceSelectionAdapter;
import org.ddialliance.ddieditor.ui.editor.widgetutil.referenceselection.ReferenceSelectionCombo;
import org.ddialliance.ddieditor.ui.model.ElementType;
import org.ddialliance.ddieditor.ui.model.IModel;
import org.ddialliance.ddieditor.ui.model.instrument.QuestionConstruct;
import org.ddialliance.ddieditor.ui.util.DialogUtil;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.Translator;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

public class QuestionConstructEditor extends Editor {
	public static final String ID = "org.ddialliance.ddieditor.ui.editor.instrument.QuestionConstructEditor";
	QuestionConstruct modelImpl;

	public QuestionConstructEditor() {
		super(Translator.trans("QuestionConstructEditor.label"), Translator
				.trans("QuestionConstructEditor.label.Description"), ID);
		this.dao = new QuestionConstructDao();
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
		this.modelImpl = (QuestionConstruct) model;
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		parent.setLayout(new GridLayout());
		createTabFolder(getComposite_1());

		// main tab
		TabItem tabItem = createTabItem(Translator.trans("QuestionConstruct"));
		Group group = createGroup(tabItem,
				Translator.trans("QuestionConstruct"));

		// QuestionReference - id
		List<LightXmlObjectType> questionRefList;
		List<LightXmlObjectType> mixedQuestionRefList = null;
		try {
			questionRefList = DdiManager.getInstance()
					.getQuestionItemsLight(null, null, null, null)
					.getLightXmlObjectList().getLightXmlObjectList();
			mixedQuestionRefList = DdiManager.getInstance()
					.getMultipleQuestionItemsLight(null, null, null, null)
					.getLightXmlObjectList().getLightXmlObjectList();
			mixedQuestionRefList.addAll(questionRefList);
		} catch (Exception e) {
			DialogUtil.errorDialog(getSite().getShell(), ID, null,
					e.getMessage(), e);
		}
		ReferenceSelectionCombo questionRefSelectCombo = createRefSelection(
				group, Translator.trans("IfThenElse.editor.ifquestionref"),
				Translator.trans("IfThenElse.editor.ifquestionref"),
				modelImpl.getQuestionReference(), mixedQuestionRefList, false, ElementType.QUESTION_ITEM);
		questionRefSelectCombo.addSelectionListener(Translator
				.trans("IfThenElse.editor.ifquestionref"),
				new ReferenceSelectionAdapter(questionRefSelectCombo,
						modelImpl, ReferenceType.class,
						getEditorIdentification()));

		// ResponseUnit
		createLabel(group,
				Translator.trans("QuestionConstructEditor.label.ResponseUnit"));
		String[] options = new String[QuestionConstruct.ResponceUnit.values().length + 1];
		options[0] = "";
		for (int i = 0; i < QuestionConstruct.ResponceUnit.values().length; i++) {
			options[i + 1] = QuestionConstruct.ResponceUnit.values()[i]
					.getLabel();
		}
		Combo responseUnitCombo = createCombo(group, options);
		if (modelImpl.getResponseUnitConverted() != null) {
			responseUnitCombo.select(modelImpl.getResponseUnitConverted());
		}
		responseUnitCombo
				.addSelectionListener(new ResponceUnitSelectionListener(
						modelImpl, InternationalStringType.class,
						getEditorIdentification()));

		// ResponseSequence - Describes the sequencing of the response
		String responseSequence = modelImpl.getResponseSequence() == null ? ""
				: modelImpl.getResponseSequence().getItemSequenceType()
						.toString();

		Combo sequencecombo = createSequenceCombo(
				group,
				Translator
						.trans("QuestionConstructEditor.label.ResponseSequence"),
				defineItemSequenceSelection(responseSequence));

		sequencecombo.addSelectionListener(new SequenceComboSelectionListener(
				modelImpl, SpecificSequenceType.class,
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
							.getDocument().getQuestionConstruct()
							.getLabelList(), modelImpl.getDocument()
							.getControlConstruct().getId());
			createTranslation(group2,
					Translator.trans("editor.button.translate"),
					modelImpl.getDocument().getControlConstruct()
							.getLabelList(), new LabelTdI(), "", txt);

			// description
			StyledText styledText = createStructuredStringInput(group2,
					Translator.trans("editor.label.description"), modelImpl
							.getDocument().getControlConstruct()
							.getDescriptionList(), modelImpl.getDocument()
							.getQuestionConstruct().getId());
			createTranslation(group2,
					Translator.trans("editor.button.translate"), modelImpl
							.getDocument().getControlConstruct()
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

	class SequenceComboSelectionListener extends GenericComboSelectionListener {
		public SequenceComboSelectionListener(IModel model, Class modifyClass,
				EditorIdentification editorIdentification) {
			super(model, modifyClass, editorIdentification);
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			int selected = ((Combo) e.widget).getSelectionIndex();
			if (selected == 4) {
				// TODO do item sequence type other stuff
			}
			editorIdentification.getEditorStatus().setChanged();
			applyChange(Editor.getSequenceIndex(Editor.getSequenceOptions()[selected]), modifyClass);
		}
	}

	class ResponceUnitSelectionListener extends GenericComboSelectionListener {
		public ResponceUnitSelectionListener(IModel model, Class modifyClass,
				EditorIdentification editorIdentification) {
			super(model, modifyClass, editorIdentification);
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			int selected = ((Combo) e.widget).getSelectionIndex();
			if (selected == 4) {
				// TODO do item other stuff, popup dialog
			}
			editorIdentification.getEditorStatus().setChanged();
			applyChange(selected, InternationalStringType.class);
		}
	}
}
