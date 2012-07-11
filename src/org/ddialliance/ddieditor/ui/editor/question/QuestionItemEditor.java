package org.ddialliance.ddieditor.ui.editor.question;

/*
 * $Author$ 
 * $Date$ 
 * $Revision$
 */

import java.util.ArrayList;
import java.util.List;

import org.ddialliance.ddi3.xml.xmlbeans.datacollection.DynamicTextType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.NameType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.ReferenceType;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.dbxml.concept.ConceptDao;
import org.ddialliance.ddieditor.ui.dbxml.question.QuestionItemDao;
import org.ddialliance.ddieditor.ui.dialogs.translationdialoginput.DynamicTextTdI;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddieditor.ui.editor.widgetutil.genericmodifylistener.TextStyledTextModyfiListener;
import org.ddialliance.ddieditor.ui.editor.widgetutil.referenceselection.ReferenceSelectionAdapter;
import org.ddialliance.ddieditor.ui.editor.widgetutil.referenceselection.ReferenceSelectionCombo;
import org.ddialliance.ddieditor.ui.model.ElementType;
import org.ddialliance.ddieditor.ui.model.ModelIdentifingType;
import org.ddialliance.ddieditor.ui.model.question.QuestionItem;
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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
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
 * Question Item Editor
 */
public class QuestionItemEditor extends Editor {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			QuestionItemEditor.class);
	public static final String ID = "org.ddialliance.ddieditor.ui.editor.question.QuestionItemEditor";
	private QuestionItem modelImpl;
	private TableViewer tableViewer;

	public QuestionItemEditor() {
		super(
				Translator
						.trans("QuestionItemEditor.label.questionItemEditorLabel.QuestionItemEditor"),
				Translator
						.trans("QuestionItemEditor.label.useTheEditorLabel.Description"),
				ID);
		super.dao = new QuestionItemDao();
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout());
		super.createPartControl(parent);
		TabFolder questionTabFolder = createTabFolder(getComposite_1());

		// - Question Root Composite:
		final Composite questionComposite = new Composite(questionTabFolder,
				SWT.NONE);
		questionComposite.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));
		questionComposite.setLayout(new GridLayout());

		// - Question Item Tab Item:
		TabItem questionTabItem = new TabItem(questionTabFolder, SWT.NONE);
		questionTabItem.setControl(questionComposite);
		questionTabItem.setText(Translator
				.trans("QuestionItemEditor.label.questionTabItem.Question")); //$NON-NLS-1$

		// - Question Group
		final Group questionGroup = new Group(questionComposite, SWT.NONE);
		final GridData gd_questionGroup = new GridData(SWT.FILL, SWT.CENTER,
				true, true);
		gd_questionGroup.heightHint = 646;
		gd_questionGroup.widthHint = 755;
		questionGroup.setLayoutData(gd_questionGroup);
		questionGroup.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));
		final GridLayout gridLayout_1 = new GridLayout();
		gridLayout_1.numColumns = 2;
		questionGroup.setLayout(gridLayout_1);
		questionGroup.setText("Question Item");

		// Name
		NameType name = modelImpl.getDocument().getQuestionItem()
				.getQuestionItemNameList().size() == 0 ? null : modelImpl
				.getDocument().getQuestionItem().getQuestionItemNameList()
				.get(0);

		Text nameTxt = createTextInput(questionGroup,
				Translator.trans("QuestionItemEditor.label.Name"),
				name == null ? "" : name.getStringValue(), false);
		nameTxt.addModifyListener(new TextStyledTextModyfiListener(modelImpl,
				NameType.class, getEditorIdentification()));
		nameTxt.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				Text textTxt = ((Text) event.getSource());
				setEditorTabName(textTxt.getText());
			}
		});

		// Concept Reference:

		// - Get available Concepts:
		List<LightXmlObjectType> conceptReferenceList = new ArrayList();

		try {
			conceptReferenceList = new ConceptDao().getLightXmlObject("", "",
					"", "");
		} catch (Exception e1) {
			String errMess = Translator
					.trans("QuestionItemEditor.mess.ConceptRetrievalError"); //$NON-NLS-1$
			ErrorDialog.openError(getSite().getShell(), Translator
					.trans("ErrorTitle"), null, new Status(IStatus.ERROR, ID,
					0, errMess, e1));
		}

		// - Create Concept Reference selection combo box
		ReferenceSelectionCombo refSelecCombo = createRefSelection(
				questionGroup,
				Translator
						.trans("QuestionItemEditor.label.conceptLabel.Concept"),
				Translator
						.trans("QuestionItemEditor.label.conceptLabel.Concept"),
				modelImpl.getConceptReferenceType(), conceptReferenceList,
				false, ElementType.CONCEPT);

		refSelecCombo.addSelectionListener(Translator
				.trans("QuestionItemEditor.label.conceptLabel.Concept"),
				new ReferenceSelectionAdapter(refSelecCombo, model,
						ReferenceType.class, getEditorIdentification()));

		// - Question Text
		DynamicTextType questionText = null;
		try {
			if (modelImpl.getQuestionText() != null) {
				questionText = (DynamicTextType) XmlBeansUtil.getLangElement(
						LanguageUtil.getDisplayLanguage(),
						modelImpl.getQuestionText());
			}
		} catch (DDIFtpException e) {
			DialogUtil
					.errorDialog(getEditorSite(), ID, null, e.getMessage(), e);
		}

		StyledText questionTxt = createTextAreaInput(
				questionGroup,
				Translator
						.trans("QuestionItemEditor.label.questionTextLabel.QuestionText"),
				questionText == null ? "" : XmlBeansUtil
						.getTextOnMixedElement(questionText.getTextList()
								.get(0)), false);

		questionTxt.addModifyListener(new TextStyledTextModyfiListener(
				modelImpl, ModelIdentifingType.Type_A.class,
				getEditorIdentification()));

		try {
			createTranslation(questionGroup,
					Translator.trans("editor.button.translate"),
					modelImpl.getQuestionText(), new DynamicTextTdI(), "",
					questionTxt);
		} catch (DDIFtpException e) {
			DialogUtil
					.errorDialog(getEditorSite(), ID, null, e.getMessage(), e);
		}

		// show relevant Response Type parameters
		try {
			createResponseType(questionGroup);
		} catch (DDIFtpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		final Composite composite_4 = new Composite(questionGroup, SWT.NONE);
		composite_4.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));
		composite_4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 2, 1));
		composite_4.setLayout(new GridLayout());

		// Create Property Tab Item:
		createPropertiesTab(questionTabFolder);

		// ddi xml tab
		createXmlTab(modelImpl);

		// preview tab
		createPreviewTab(modelImpl);

		editorStatus.clearChanged();
	}

	public Viewer getViewer() {
		return this.tableViewer;
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
		this.modelImpl = (QuestionItem) model;
	}
}
