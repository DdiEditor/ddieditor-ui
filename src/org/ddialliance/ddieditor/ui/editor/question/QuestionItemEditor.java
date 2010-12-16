package org.ddialliance.ddieditor.ui.editor.question;

/*
 * $Author$ 
 * $Date$ 
 * $Revision$
 */

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.ddialliance.ddi3.xml.xmlbeans.conceptualcomponent.ConceptDocument;
import org.ddialliance.ddi3.xml.xmlbeans.conceptualcomponent.ConceptType;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.DynamicTextType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.ReferenceType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.RepresentationType;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.dbxml.concept.ConceptDao;
import org.ddialliance.ddieditor.ui.dbxml.question.QuestionItemDao;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddieditor.ui.editor.EditorInput.EditorModeType;
import org.ddialliance.ddieditor.ui.editor.question.ResponseTypeDetail.RESPONSE_TYPES;
import org.ddialliance.ddieditor.ui.editor.widgetutil.genericmodifylistener.TextStyledTextModyfiListener;
import org.ddialliance.ddieditor.ui.editor.widgetutil.referenceselection.ReferenceSelectionAdapter;
import org.ddialliance.ddieditor.ui.editor.widgetutil.referenceselection.ReferenceSelectionCombo;
import org.ddialliance.ddieditor.ui.model.ModelIdentifingType;
import org.ddialliance.ddieditor.ui.model.question.QuestionItem;
import org.ddialliance.ddieditor.ui.model.reference.ResponseTypeReference;
import org.ddialliance.ddieditor.ui.model.translationdialoginput.DynamicTextTdI;
import org.ddialliance.ddieditor.ui.util.DialogUtil;
import org.ddialliance.ddieditor.ui.util.LanguageUtil;
import org.ddialliance.ddieditor.ui.view.Messages;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.ddialliance.ddiftp.util.xml.XmlBeansUtil;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.PartInitException;

/**
 * Question Item Editor
 */
public class QuestionItemEditor extends Editor implements ISelectionListener {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			QuestionItemEditor.class);
	public static final String ID = "org.ddialliance.ddieditor.ui.editor.question.QuestionItemEditor";
	private QuestionItem modelImpl;
	private ComboViewer responseComboViewer;
	private Composite ResponseTypeCodeComposite;
	private TableViewer tableViewer;

	public QuestionItemEditor() {
		super(
				Messages
						.getString("QuestionItemEditor.label.questionItemEditorLabel.QuestionItemEditor"),
				Messages
						.getString("QuestionItemEditor.label.useTheEditorLabel.Description"));
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
		questionTabItem
				.setText(Messages
						.getString("QuestionItemEditor.label.questionTabItem.Question")); //$NON-NLS-1$

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

		// Concept Reference:

		// - Get available Concepts:
		List<LightXmlObjectType> conceptReferenceList = new ArrayList();

		try {
			conceptReferenceList = new ConceptDao().getLightXmlObject("", "",
					"", "");
		} catch (Exception e1) {
			String errMess = Messages
					.getString("QuestionItemEditor.mess.ConceptRetrievalError"); //$NON-NLS-1$
			ErrorDialog.openError(getSite().getShell(), Messages
					.getString("ErrorTitle"), null, new Status(IStatus.ERROR,
					ID, 0, errMess, e1));
		}

		// - Create Concept Reference selection combobox
		ReferenceSelectionCombo refSelecCombo = createRefSelection(questionGroup,
				Messages.getString("QuestionItemEditor.label.conceptLabel.Concept") + ":",
				Messages.getString("QuestionItemEditor.label.conceptLabel.Concept"),
				modelImpl.getConceptReferenceType(), conceptReferenceList, false);
		refSelecCombo.addSelectionListener(Messages.getString("QuestionItemEditor.label.conceptLabel.Concept"),
				new ReferenceSelectionAdapter(refSelecCombo, model, ReferenceType.class, getEditorIdentification()));

		// - Question Text
		DynamicTextType questionText = null;
		try {
			if (modelImpl.getQuestionText() != null) {
				questionText = (DynamicTextType) XmlBeansUtil
						.getLangElement(LanguageUtil.getDisplayLanguage(), modelImpl.getQuestionText());
			}
		} catch (DDIFtpException e) {
			DialogUtil
					.errorDialog(getEditorSite(), ID, null, e.getMessage(), e);
		}

		StyledText questionTxt = createTextAreaInput(
				questionGroup,
				Messages
						.getString("QuestionItemEditor.label.questionTextLabel.QuestionText"),
				questionText == null ? "" : XmlBeansUtil
						.getTextOnMixedElement(questionText.getTextList()
								.get(0)), false);

		questionTxt.addModifyListener(new TextStyledTextModyfiListener(
				modelImpl, ModelIdentifingType.Type_A.class,
				getEditorIdentification()));

		try {
			createTranslation(questionGroup, Messages
					.getString("editor.button.translate"), modelImpl
					.getQuestionText(), new DynamicTextTdI(), "", questionTxt);
		} catch (DDIFtpException e) {
			DialogUtil
					.errorDialog(getEditorSite(), ID, null, e.getMessage(), e);
		}

		// Response Type:
		final Label questionResponseTypeLabel = new Label(questionGroup,
				SWT.NONE);
		final GridData gd_questionResponseTypeLabel = new GridData(SWT.RIGHT,
				SWT.CENTER, false, false);
		gd_questionResponseTypeLabel.horizontalIndent = 5;
		questionResponseTypeLabel.setLayoutData(gd_questionResponseTypeLabel);
		questionResponseTypeLabel.setBackground(Display.getCurrent()
				.getSystemColor(SWT.COLOR_WHITE));
		questionResponseTypeLabel
				.setText(Messages
						.getString("QuestionItemEditor.label.questionResponseTypeLabel.QuestionResponseType")); //$NON-NLS-1$

		responseComboViewer = new ComboViewer(questionGroup);
		final Combo combo = responseComboViewer.getCombo();
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		responseComboViewer
				.setContentProvider(new org.eclipse.jface.viewers.ArrayContentProvider());
		responseComboViewer
				.setLabelProvider(new org.eclipse.jface.viewers.LabelProvider());
		final List<ResponseTypeReference> responseDomainReferenceList;

		responseComboViewer.getCombo().setRedraw(true);
		responseComboViewer.getCombo().setLayoutData(
				new GridData(SWT.FILL, SWT.CENTER, true, false));

		final Composite ResponseTypeLabelComposite = new Composite(
				questionGroup, SWT.NONE);
		ResponseTypeLabelComposite.setLayoutData(new GridData(SWT.RIGHT,
				SWT.CENTER, false, false));
		ResponseTypeLabelComposite.setBackground(Display.getCurrent()
				.getSystemColor(SWT.COLOR_WHITE));
		final GridLayout gridLayout_4 = new GridLayout();
		gridLayout_4.marginWidth = 0;
		gridLayout_4.marginHeight = 0;
		ResponseTypeLabelComposite.setLayout(gridLayout_4);

		ResponseTypeCodeComposite = new Composite(questionGroup, SWT.NONE);
		ResponseTypeCodeComposite.setRedraw(true);
		ResponseTypeCodeComposite.setBackground(Display.getCurrent()
				.getSystemColor(SWT.COLOR_WHITE));
		ResponseTypeCodeComposite.setLayoutData(new GridData(SWT.FILL,
				SWT.CENTER, false, false));
		final GridLayout gridLayout_5 = new GridLayout();
		gridLayout_5.marginHeight = 0;
		gridLayout_5.marginWidth = 0;
		gridLayout_5.numColumns = 1;
		ResponseTypeCodeComposite.setLayout(gridLayout_5);

		final ResponseTypeDetail responseTypeDetail = new ResponseTypeDetail(
				modelImpl, ResponseTypeLabelComposite,
				ResponseTypeCodeComposite, editorStatus,
				(IEditorSite) getSite());
		responseComboViewer.getCombo().setItems(
				responseTypeDetail.getResponseTypeLabels());

		if (!getEditorInputImpl().getEditorMode().equals(EditorModeType.NEW)) {
			RESPONSE_TYPES responseType = ResponseTypeDetail
					.getResponseType(modelImpl.getResponseDomain());
			responseTypeDetail.setDetails(modelImpl.getResponseDomain());
		}

		// - get Response Domain Reference
		RepresentationType responseDomainRef = null;
		if (!getEditorInputImpl().getEditorMode().equals(EditorModeType.NEW)) {
			try {
				responseDomainRef = modelImpl.getResponseDomain();
			} catch (Exception e1) {
				String errMess = MessageFormat
						.format(
								Messages
										.getString("QuestionItemEditor.mess.ResponseDomainReferenceRetrievalError"), e1.getMessage()); //$NON-NLS-1$
				ErrorDialog.openError(getSite().getShell(), Messages
						.getString("ErrorTitle"), null, new Status(
						IStatus.ERROR, ID, 0, errMess, e1));
			}
		}

		// - get list of Response Types e.g. CODE, NUMERIC, etc.
		responseDomainReferenceList = ResponseTypeDetail
				.getResponseDomainReferenceList();
		responseComboViewer.setInput(responseDomainReferenceList);
		int index = 0;
		if (!getEditorInputImpl().getEditorMode().equals(EditorModeType.NEW)) {
			for (Iterator iterator = responseDomainReferenceList.iterator(); iterator
					.hasNext();) {
				ResponseTypeReference responseDomainReference = (ResponseTypeReference) iterator
						.next();
				if (responseDomainReference.getResponseDomain() != null
						&& responseDomainReference.getResponseDomain().equals(
								ResponseTypeDetail
										.getResponseType(responseDomainRef))) {
					break;
				}
				index++;
			}
		}
		responseComboViewer.getCombo().select(index);

		responseComboViewer.getCombo().addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				// Save new Response Type
				int index = responseComboViewer.getCombo().getSelectionIndex();
				if (index >= 0) {
					RESPONSE_TYPES rt = ((ResponseTypeReference) responseDomainReferenceList
							.get(index)).getResponseDomain();
					// Save Responds Type without details - saved by
					// ResponseTypeDetail modify listener
					RepresentationType repType = modelImpl.setResponseDomain(
							rt, "");
					if (repType == null) {
						String errMess = MessageFormat
								.format(
										Messages
												.getString("QuestionItemEditor.mess.QuestionItemResponseTypeNotSupported"),
										ResponseTypeDetail
												.getResponseTypeLabel(rt)); //$NON-NLS-1$
						ErrorDialog.openError(getSite().getShell(), Messages
								.getString("ErrorTitle"), null, new Status(
								IStatus.ERROR, ID, 0, errMess, null),
								IStatus.ERROR);
						responseComboViewer.getCombo().select(0);
						responseTypeDetail.dispose();
						return;
					}
					// Show corresponding Response Type details
					responseTypeDetail.setDetails(repType);
					editorStatus.setChanged();
				}
			}
		});

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

		// Clean dirt from initialization
		editorStatus.clearChanged();
	}

	public Viewer getViewer() {
		log.debug("QuestionItemEditor.getViewer()");
		return this.tableViewer;
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		log.debug("QuestionItemEditor.init()");
		super.init(site, input);
		this.modelImpl = (QuestionItem) model;
	}
}
