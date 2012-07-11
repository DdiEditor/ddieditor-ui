package org.ddialliance.ddieditor.ui.editor.question;

import java.util.ArrayList;
import java.util.List;

import org.ddialliance.ddi3.xml.xmlbeans.datacollection.DynamicTextType;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.SpecificSequenceType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.NameType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.ReferenceType;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.dbxml.concept.ConceptDao;
import org.ddialliance.ddieditor.ui.dbxml.question.MultipleQuestionItemDao;
import org.ddialliance.ddieditor.ui.dialogs.translationdialoginput.DynamicTextTdI;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddieditor.ui.editor.widgetutil.genericmodifylistener.TextStyledTextModyfiListener;
import org.ddialliance.ddieditor.ui.editor.widgetutil.genericselectionlistener.GenericComboSelectionListener;
import org.ddialliance.ddieditor.ui.editor.widgetutil.referenceselection.ReferenceSelectionAdapter;
import org.ddialliance.ddieditor.ui.editor.widgetutil.referenceselection.ReferenceSelectionCombo;
import org.ddialliance.ddieditor.ui.model.ElementType;
import org.ddialliance.ddieditor.ui.model.IModel;
import org.ddialliance.ddieditor.ui.model.ModelIdentifingType;
import org.ddialliance.ddieditor.ui.model.question.MultipleQuestionItem;
import org.ddialliance.ddieditor.ui.util.DialogUtil;
import org.ddialliance.ddieditor.ui.util.LanguageUtil;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.Translator;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.ddialliance.ddiftp.util.xml.XmlBeansUtil;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

/**
 * Multi Question Item Editor
 */

public class MultipleQuestionItemEditor extends Editor {

	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			MultipleQuestionItemEditor.class);
	public static final String ID = "org.ddialliance.ddieditor.ui.editor.question.MultipleQuestionItemEditor";
	private MultipleQuestionItem modelImpl;
	private TableViewer tableViewer;

	public MultipleQuestionItemEditor() {
		super(
				Translator
						.trans("MultipleQuestionItemEditor.label.multipleQuestionItemEditorLabel.MultipleQuestionItemEditor"),
				Translator
						.trans("MultipleQuestionItemEditor.label.useTheEditorLabel.Description"),
				ID);
		super.dao = new MultipleQuestionItemDao();
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout());
		super.createPartControl(parent);
		TabFolder multiQuestionTabFolder = createTabFolder(getComposite_1());

		// - Multiple Question Item Root Composite:
		final Composite multiQuestionComposite = new Composite(
				multiQuestionTabFolder, SWT.NONE);
		multiQuestionComposite.setBackground(Display.getCurrent()
				.getSystemColor(SWT.COLOR_WHITE));
		multiQuestionComposite.setLayout(new GridLayout());

		// - Multiple Question Item Tab Item:
		TabItem multiQuestionTabItem = new TabItem(multiQuestionTabFolder,
				SWT.NONE);
		multiQuestionTabItem.setControl(multiQuestionComposite);
		multiQuestionTabItem
				.setText(Translator
						.trans("MultipleQuestionItemEditor.label.multipleQuestionTabItem.MultipleQuestionItem")); //$NON-NLS-1$

		// - Multiple Question Item Group
		final Group multiQuestionGroup = new Group(multiQuestionComposite,
				SWT.NONE);
		final GridData gd_questionGroup = new GridData(SWT.FILL, SWT.CENTER,
				true, true);
		gd_questionGroup.heightHint = 646;
		gd_questionGroup.widthHint = 755;
		multiQuestionGroup.setLayoutData(gd_questionGroup);
		multiQuestionGroup.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));
		final GridLayout gridLayout_1 = new GridLayout();
		gridLayout_1.numColumns = 2;
		multiQuestionGroup.setLayout(gridLayout_1);
		multiQuestionGroup
				.setText(Translator
						.trans("MultipleQuestionItemEditor.label.MultipleQuestionItem"));

		// Name
		NameType name = null;
		try {
			name = modelImpl.getDocument().getMultipleQuestionItem()
					.getMultipleQuestionItemNameList().size() == 0 ? null
					: modelImpl.getDocument().getMultipleQuestionItem()
							.getMultipleQuestionItemNameList().get(0);
		} catch (DDIFtpException e2) {
			String errMess = Translator
					.trans("MultipleQuestionItemEditor.mess.NameRetrievalError"); //$NON-NLS-1$
			ErrorDialog.openError(getSite().getShell(), Translator
					.trans("ErrorTitle"), null, new Status(IStatus.ERROR, ID,
					0, errMess, e2));
		}

		Text nameTxt = createTextInput(
				multiQuestionGroup,
				Translator.trans("MultipleQuestionItemEditor.label.Name") + ":",
				name == null ? "" : name.getStringValue(), false);
		nameTxt.addModifyListener(new TextStyledTextModyfiListener(modelImpl,
				NameType.class, getEditorIdentification()));

		// Concept Reference:

		// - Get available Concepts:
		List<LightXmlObjectType> conceptReferenceList = new ArrayList();
		try {
			conceptReferenceList = new ConceptDao().getLightXmlObject("", "",
					"", "");
		} catch (Exception e1) {
			String errMess = Translator
					.trans("MultipleQuestionItemEditor.mess.ConceptRetrievalError"); //$NON-NLS-1$
			ErrorDialog.openError(getSite().getShell(), Translator
					.trans("ErrorTitle"), null, new Status(IStatus.ERROR, ID,
					0, errMess, e1));
		}

		// - Create Concept Reference selection combobox
		ReferenceSelectionCombo refSelecCombo = createRefSelection(
				multiQuestionGroup,
				Translator
						.trans("MultipleQuestionItemEditor.label.conceptLabel.Concept")
						+ ":",
				Translator
						.trans("MultipleQuestionItemEditor.label.conceptLabel.Concept"),
				modelImpl.getConceptReferenceType(), conceptReferenceList,
				false, ElementType.CONCEPT);
		refSelecCombo
				.addSelectionListener(
						Translator
								.trans("MultipleQuestionItemEditor.label.conceptLabel.Concept"),
						new ReferenceSelectionAdapter(refSelecCombo, model,
								ReferenceType.class, getEditorIdentification()));

		// - SubQuestion Sequence combobox
		String subQuestionSequence = modelImpl.getSubQuestionSequence() == null ? ""
				: modelImpl.getSubQuestionSequence().getItemSequenceType()
						.toString();

		Combo sequencecombo = createSequenceCombo(
				multiQuestionGroup,
				Translator
						.trans("MultipleQuestionItemEditor.label.SubQuestionSequence"),
				defineItemSequenceSelection(subQuestionSequence));

		sequencecombo.addSelectionListener(new SequenceComboSelectionListener(
				modelImpl, SpecificSequenceType.class,
				getEditorIdentification()));

		// - Multiple Question Item Text
		DynamicTextType multiQuestionText = null;
		try {
			if (modelImpl.getQuestionText() != null) {
				multiQuestionText = (DynamicTextType) XmlBeansUtil
						.getLangElement(LanguageUtil.getDisplayLanguage(),
								modelImpl.getQuestionText());
			}
		} catch (DDIFtpException e) {
			DialogUtil
					.errorDialog(getEditorSite(), ID, null, e.getMessage(), e);
		}

		StyledText multiQuestionTxt = createTextAreaInput(
				multiQuestionGroup,
				Translator
						.trans("MultipleQuestionItemEditor.label.multipleQuestionItemTextLabel.MultipleQuestionItemText"),
				multiQuestionText == null ? "" : XmlBeansUtil
						.getTextOnMixedElement(multiQuestionText.getTextList()
								.get(0)), false);

		multiQuestionTxt.addModifyListener(new TextStyledTextModyfiListener(
				modelImpl, ModelIdentifingType.Type_A.class,
				getEditorIdentification()));

		try {
			createTranslation(multiQuestionGroup,
					Translator.trans("editor.button.translate"),
					modelImpl.getQuestionText(), new DynamicTextTdI(), "",
					multiQuestionTxt);
		} catch (DDIFtpException e) {
			DialogUtil
					.errorDialog(getEditorSite(), ID, null, e.getMessage(), e);
		}

		// Create Property Tab Item:
		createPropertiesTab(multiQuestionTabFolder);

		// ddi xml tab
		createXmlTab(modelImpl);

		// preview tab
		createPreviewTab(modelImpl);

		// Clean dirt from initialization
		editorStatus.clearChanged();
	}

	public Viewer getViewer() {
		log.debug("MultipleQuestionItemEditor.getViewer()");
		return this.tableViewer;
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		log.debug("MultipleQuestionItemEditor.init()");
		super.init(site, input);
		this.modelImpl = (MultipleQuestionItem) model;
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
}
