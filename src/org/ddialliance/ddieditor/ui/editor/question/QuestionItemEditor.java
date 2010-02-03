package org.ddialliance.ddieditor.ui.editor.question;

/**
 * Question Item Editor.
 * 
 */
/*
 * $Author$ 
 * $Date$ 
 * $Revision$
 */

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ddialliance.ddi3.xml.xmlbeans.reusable.RepresentationType;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.dbxml.concept.Concepts;
import org.ddialliance.ddieditor.ui.dbxml.question.QuestionItemDao;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddieditor.ui.editor.EditorInput;
import org.ddialliance.ddieditor.ui.editor.FilteredItemsSelection;
import org.ddialliance.ddieditor.ui.editor.TableEditingSupport;
import org.ddialliance.ddieditor.ui.editor.EditorInput.EditorModeType;
import org.ddialliance.ddieditor.ui.editor.question.ResponseTypeDetail.RESPONSE_TYPES;
import org.ddialliance.ddieditor.ui.model.Language;
import org.ddialliance.ddieditor.ui.model.question.QuestionItem;
import org.ddialliance.ddieditor.ui.model.question.QuestionItemLiteralText;
import org.ddialliance.ddieditor.ui.model.reference.ResponseTypeReference;
import org.ddialliance.ddieditor.ui.view.Messages;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.PartInitException;

public class QuestionItemEditor extends Editor implements ISelectionListener {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			QuestionItemEditor.class);
	public static final String ID = "org.ddialliance.ddieditor.ui.editor.question.QuestionItemEditor";

	public QuestionItemEditor() {
		super(
				Messages
						.getString("QuestionItemEditor.label.questionItemEditorLabel.QuestionItemEditor"),
				Messages
						.getString("QuestionItemEditor.label.useTheEditorLabel.Description"));
		this.dao = new QuestionItemDao(); 
	}

	/**
	 * 
	 * Question Item Text - table content provider
	 * 
	 */
	public class QITTableContentProvider implements IStructuredContentProvider {

		/*
		 * Get Translated Question Item Texts
		 * 
		 * - i.e. Question Text with "true" translated attribute.
		 * 
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.IStructuredContentProvider#getElements(
		 * java.lang.Object)
		 */
		public Object[] getElements(Object parent) {

			log.debug("QITTableContentProvider.getElements()");

			List translatedQuestionList = new ArrayList();

			if (!editorInput.getEditorMode().equals(EditorModeType.NEW)) {
				String[] languageCodes = Language
						.getLanguageCodesExcludingOrginalLanguage(questionItem
								.getOriginalLanguageCode());
				for (int i = 0; i < languageCodes.length; i++) {
					QuestionItemLiteralText questionItemLiteralText;
					try {
						// Get translated text - language by language
						questionItemLiteralText = questionItem
								.getQuestionItemLiteralText(true,
										languageCodes[i]);
					} catch (Exception e) {
						return (new Object[0]);
					}
					if (questionItemLiteralText != null) {
						translatedQuestionList.add(questionItemLiteralText);
					}
				}
			}

			return translatedQuestionList.toArray();
		}

		public void dispose() {
			log.debug("TableContentProvider.dispose()");
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			log.debug("TableContentProvider.inputChanged()");
		}

	}

	// Question Item Text - table label provider
	public class QITTableLabelProvider extends LabelProvider implements
			ITableLabelProvider {

		public void createColumns(TableViewer viewer) {
			Table table = viewer.getTable();
			String[] titles = {
					Messages
							.getString("QITTableLabelProvider.label.TranslatedQuestionText"),
					Messages.getString("QITTableLabelProvider.label.Language") };
			int[] bounds = { 500, 60, };

			for (int i = 0; i < titles.length; i++) {
				TableViewerColumn column = new TableViewerColumn(viewer,
						SWT.NONE);
				column.getColumn().setText(titles[i]);
				column.getColumn().setWidth(bounds[i]);
				column.getColumn().setResizable(true);
				column.setEditingSupport(new TableEditingSupport(viewer, i,
						editorStatus, questionItem.getOriginalLanguageCode()));
				table.setHeaderVisible(true);
				table.setLinesVisible(true);
			}
		}

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		/**
		 * Returns the label text of a given column for the element
		 */
		public String getColumnText(Object element, int columnIndex) {

			log.debug("TableLabelProvider.getColumnText()");
			QuestionItemLiteralText questionItemLiteralText = (QuestionItemLiteralText) element;

			switch (columnIndex) {
			case 0:
				log.debug("Text: " + questionItemLiteralText.getText());
				return questionItemLiteralText.getText();
			case 1:
				log.debug("Language: "
						+ Language.getLanguage(questionItemLiteralText
								.getLanguageCode()));
				return Language.getLanguage(questionItemLiteralText
						.getLanguageCode());
			default:
				String errMess = Messages
						.getString("QITTableLabelProvider.mess.UnexpectedColumnIndexFound");
				ErrorDialog.openError(site.getShell(), Messages
						.getString("ErrorTitle"), null, new Status(
						IStatus.ERROR, ID, 0, errMess, null));
				throw new RuntimeException(
						Messages
								.getString("QITTableLabelProvider.mess.UnexpectedColumnIndexFound")); //$NON-NLS-1$
			}
		}

	}

	// Member variables:
	private QuestionItem questionItem;
	private IEditorSite site;
	private StyledText originalQuestionTextStyledText;
	private Combo langCombo;
	private ComboViewer responseComboViewer;
	private Composite ResponseTypeCodeComposite;
	private Composite translatedButtonComposite = null;
	private TableViewer tableViewer;
	private Table table;

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

		// Concept:
		final Composite LabelComposite = new Composite(questionGroup, SWT.NONE);
		LabelComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false));
		LabelComposite.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));
		final GridLayout gridLayout_3 = new GridLayout();
		gridLayout_3.marginWidth = 0;
		gridLayout_3.marginHeight = 0;
		LabelComposite.setLayout(gridLayout_3);

		final Composite codeComposite = new Composite(questionGroup, SWT.NONE);
		codeComposite.setRedraw(true);
		codeComposite.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));
		codeComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));
		final GridLayout gridLayout_6 = new GridLayout();
		gridLayout_6.marginHeight = 0;
		gridLayout_6.marginWidth = 0;
		codeComposite.setLayout(gridLayout_6);

		final FilteredItemsSelection filteredItemsSelection = new FilteredItemsSelection();

		// - Get available Concepts:
		List<LightXmlObjectType> conceptReferenceList = new ArrayList();
		try {
			conceptReferenceList = Concepts.getConceptsLight("", "", "", "");
		} catch (Exception e1) {
			String errMess = Messages
					.getString("QuestionItemEditor.mess.ConceptRetrievalError"); //$NON-NLS-1$
			ErrorDialog.openError(site.getShell(), Messages
					.getString("ErrorTitle"), null, new Status(IStatus.ERROR,
					ID, 0, errMess, e1));
		}

		// - Create Concept selection composite:
		try {
			filteredItemsSelection
					.createPartControl(
							LabelComposite,
							codeComposite,
							"",
							Messages
									.getString("QuestionItemEditor.label.conceptLabel.Concept"),
							conceptReferenceList, questionItem.getConceptRef());
		} catch (Exception e2) {
			String errMess = Messages
					.getString("QuestionItemEditor.mess.CreateFilteredItemsSelectionError"); //$NON-NLS-1$
			ErrorDialog.openError(site.getShell(), Messages
					.getString("ErrorTitle"), null, new Status(IStatus.ERROR,
					ID, 0, errMess, e2));
		}
		filteredItemsSelection.addSelectionListener(Messages
				.getString("QuestionItemEditor.label.SelectConseptReference"),
				conceptReferenceList, new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						LightXmlObjectType result = (LightXmlObjectType) filteredItemsSelection
								.getResult();
						if (result != null) {
							try {
								questionItem.setConceptRef(result.getId());
							} catch (Exception e1) {
								String errMess = MessageFormat
										.format(
												Messages
														.getString("QuestionItemEditor.mess.SetConceptRefError"), e1.getMessage()); //$NON-NLS-1$
								ErrorDialog.openError(site.getShell(), Messages
										.getString("ErrorTitle"), null,
										new Status(IStatus.ERROR, ID, 0,
												errMess, e1));
							}
							editorStatus.setChanged();
						}
					}
				});

		// Question Item Text:
		final Label questionTextLabel = new Label(questionGroup, SWT.NONE);
		final GridData gd_questionTextLabel = new GridData(SWT.RIGHT, SWT.TOP,
				false, false);
		gd_questionTextLabel.horizontalIndent = 5;
		questionTextLabel.setLayoutData(gd_questionTextLabel);
		questionTextLabel.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));
		questionTextLabel
				.setText(Messages
						.getString("QuestionItemEditor.label.questionTextLabel.QuestionText")); //$NON-NLS-1$

		originalQuestionTextStyledText = new StyledText(questionGroup, SWT.WRAP
				| SWT.V_SCROLL | SWT.BORDER);
		String text = "";
		if (!editorInput.getEditorMode().equals(EditorModeType.NEW)) {
			try {
				// Get original text = not translated text - the language don't
				// matter
				text = questionItem.getText(false, "");
			} catch (Exception e1) {
				String errMess = Messages
						.getString("QuestionItemEditor.mess.QuestionItemTextRetrievalError"); //$NON-NLS-1$
				ErrorDialog.openError(site.getShell(), Messages
						.getString("ErrorTitle"), null, new Status(
						IStatus.ERROR, ID, 0, errMess, e1));
			}
		}
		originalQuestionTextStyledText.setText(text);
		final GridData gd_originalQuestionTextStyledText = new GridData(
				SWT.FILL, SWT.CENTER, true, false);
		gd_originalQuestionTextStyledText.heightHint = 154;
		gd_originalQuestionTextStyledText.widthHint = 309;
		originalQuestionTextStyledText
				.setLayoutData(gd_originalQuestionTextStyledText);

		final Label originalLanguageLabel = new Label(questionGroup, SWT.NONE);
		originalLanguageLabel.setBackground(Display.getCurrent()
				.getSystemColor(SWT.COLOR_WHITE));
		final GridData gd_originalLanguageLabel = new GridData(SWT.RIGHT,
				SWT.CENTER, false, false);
		gd_originalLanguageLabel.horizontalIndent = 5;
		originalLanguageLabel.setLayoutData(gd_originalLanguageLabel);
		originalLanguageLabel
				.setText(Messages
						.getString("QuestionItemEditor.label.originalLanguageLabel.OriginalLanguage")); //$NON-NLS-1$
		originalQuestionTextStyledText.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				log.debug("Original QI text changed");
				questionItem.setText(originalQuestionTextStyledText.getText(),
						questionItem.getOriginalLanguageCode());
				editorStatus.setChanged();
			}
		});

		langCombo = new Combo(questionGroup, SWT.READ_ONLY);
		langCombo.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				// TODO If original language is changed - what happens with
				// translated questions?
				// questionItem.setLanguage(LANGUAGES_CODE[DEFAULT_LANGUAGE_INDEX]);
				editorStatus.setChanged();
			}
		});
		langCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));
		langCombo.setItems(Language.getLanguages());
		langCombo.setText(Language.getLanguage(questionItem
				.getOriginalLanguageCode()));

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
		ResponseTypeCodeComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				false, false));
		final GridLayout gridLayout_5 = new GridLayout();
		gridLayout_5.marginHeight = 0;
		gridLayout_5.marginWidth = 0;
		gridLayout_5.numColumns = 1;
		ResponseTypeCodeComposite.setLayout(gridLayout_5);

		final ResponseTypeDetail responseTypeDetail = new ResponseTypeDetail(
				questionItem, ResponseTypeLabelComposite,
				ResponseTypeCodeComposite, editorStatus, site);
		responseComboViewer.getCombo().setItems(
				responseTypeDetail.getResponseTypeLabels());

		if (!editorInput.getEditorMode().equals(EditorModeType.NEW)) {
			RESPONSE_TYPES responseType = ResponseTypeDetail
					.getResponseType(questionItem.getResponseDomain());
			responseTypeDetail.setDetails(questionItem.getResponseDomain());
		}

		// - get Response Domain Reference
		RepresentationType responseDomainRef = null;
		if (!editorInput.getEditorMode().equals(EditorModeType.NEW)) {
			try {
				responseDomainRef = questionItem.getResponseDomain();
			} catch (Exception e1) {
				String errMess = MessageFormat
						.format(
								Messages
										.getString("QuestionItemEditor.mess.ResponseDomainReferenceRetrievalError"), e1.getMessage()); //$NON-NLS-1$
				ErrorDialog.openError(site.getShell(), Messages
						.getString("ErrorTitle"), null, new Status(
						IStatus.ERROR, ID, 0, errMess, e1));
			}
		}

		// - get list of Response Types e.g. CODE, NUMERIC, etc.
		responseDomainReferenceList = ResponseTypeDetail
				.getResponseDomainReferenceList();
		responseComboViewer.setInput(responseDomainReferenceList);
		int index = 0;
		if (!editorInput.getEditorMode().equals(EditorModeType.NEW)) {
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
					RepresentationType repType = questionItem
							.setResponseDomain(rt, "");
					if (repType == null) {
						String errMess = MessageFormat
								.format(
										Messages
												.getString("QuestionItemEditor.mess.QuestionItemResponseTypeNotSupported"),
										ResponseTypeDetail
												.getResponseTypeLabel(rt)); //$NON-NLS-1$
						ErrorDialog.openError(site.getShell(), Messages
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
		composite_4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				false, false, 2, 1));
		composite_4.setLayout(new GridLayout());

		// Translated Questions Tab:
		// --------------------------

		// - Question Root Composite:
		final Composite translatedComposite = new Composite(questionTabFolder,
				SWT.NONE);
		translatedComposite.setLayout(new GridLayout());
		translatedComposite.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));

		// - Question Item Tab Item:
		TabItem simpleTabItem = new TabItem(questionTabFolder, SWT.NONE);
		simpleTabItem.setControl(translatedComposite);
		simpleTabItem
				.setText(Messages
						.getString("QuestionItemEditor.label.translatedQuestionsTabItem.TranslatedQuestion"));

		// - Translated Question Group
		final Group translatedQuestionsGroup = new Group(translatedComposite,
				SWT.NONE);
		translatedQuestionsGroup.setLayoutData(new GridData(756, 647));
		translatedQuestionsGroup.setLayout(new GridLayout());
		translatedQuestionsGroup.setBackground(Display.getCurrent()
				.getSystemColor(SWT.COLOR_WHITE));
		translatedQuestionsGroup
				.setText(Messages
						.getString("QuestionItemEditor.label.translatedQuestionsGroup.TranslatedQuestions")); //$NON-NLS-1$

		final Button translatableButton = new Button(translatedQuestionsGroup,
				SWT.CHECK);
		translatableButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				boolean translatable = translatableButton.getSelection();
				if (translatable) {
					translatedButtonComposite.setEnabled(true);
					// questionItem.set
					editorStatus.setChanged();
				} else {
					if (tableViewer.getTable().getItemCount() > 0
							&& MessageDialog
									.openConfirm(
											site.getShell(),
											Messages.getString("ConfirmTitle"),
											Messages
													.getString("QuestionItemEditor.mess.ConfirmCleanTranslatedQuestionItemList"))) {
						tableViewer.getTable().removeAll();
						translatedButtonComposite.setEnabled(false);
						editorStatus.setChanged();
					} else {
						// Restore status
						translatableButton.setSelection(true);
					}
				}
			}
		});
		translatableButton.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));
		translatableButton
				.setText(Messages
						.getString("QuestionItemEditor.label.translatableButton.Translatable")); //$NON-NLS-1$

		// Define - Question Item Table Viewer:
		tableViewer = new TableViewer(translatedQuestionsGroup, SWT.MULTI
				| SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		table = tableViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		tableViewer.setContentProvider(new QITTableContentProvider());
		QITTableLabelProvider tableLabelProvider = new QITTableLabelProvider();
		tableLabelProvider.createColumns(tableViewer);
		tableViewer.setLabelProvider(tableLabelProvider);
		tableViewer.setInput(getEditorSite());
		// Make the selection available
		getSite().setSelectionProvider(tableViewer);

		translatedButtonComposite = new Composite(translatedQuestionsGroup,
				SWT.NONE);
		translatedButtonComposite.setBackground(Display.getCurrent()
				.getSystemColor(SWT.COLOR_WHITE));
		final GridLayout gridLayout_2 = new GridLayout();
		gridLayout_2.numColumns = 2;
		translatedButtonComposite.setLayout(gridLayout_2);

		// - "Add" button:
		final Button addButton = new Button(translatedButtonComposite, SWT.NONE);
		addButton.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));
		addButton.setText(Messages
				.getString("QuestionItemEditor.label.addButton.Add")); //$NON-NLS-1$
		addButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				QuestionItemLiteralText questionItemLiteralText = questionItem
						.addText("", "", true, true);
				// QuestionItemLiteralText questionItemLiteralText = new
				// QuestionItemLiteralText("", "", true, true);
				tableViewer.add(questionItemLiteralText);
				table.setTopIndex(table.getItemCount());
				table.select(table.getItemCount() - 1);
				tableViewer.editElement(questionItemLiteralText, 0);
			}
		});

		// - "Remove" button:
		final Button removeButton = new Button(translatedButtonComposite,
				SWT.NONE);
		removeButton.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));
		removeButton.setText("Remove");
		removeButton.setText(Messages
				.getString("QuestionItemEditor.label.removeButton.Remove")); //$NON-NLS-1$
		removeButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ISelection selection = tableViewer.getSelection();
				if (selection instanceof IStructuredSelection) {
					Iterator iterator = ((IStructuredSelection) selection)
							.iterator();
					while (iterator.hasNext()) {
						Object obj = iterator.next();
						questionItem.removeText((QuestionItemLiteralText) obj);
						tableViewer.remove(obj);
					}
				}
			}
		});

		// Check or un-check translatable check-box and enable/disable Add /
		// Remove buttons accordingly
		if (tableViewer.getTable().getItemCount() > 0) {
			translatableButton.setSelection(true);
		} else {
			translatedButtonComposite.setEnabled(false);
		}

		// Create Property Tab Item:
		createPropertiesTab(questionTabFolder);
		
		// ddi xml tab
		createXmlTab(questionItem);

		// Clean dirt from initialization
		editorStatus.clearChanged();
	}

	public Viewer getViewer() {
		log.debug("QuestionItemEditor.getViewer()");
		return this.tableViewer;
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		log.debug("QuestionItemEditor.doSave()");
		super.doSave(monitor);
		// Update Question Item Document with Question Item texts
		questionItem.update();
		try {
			questionItem.validate();
		} catch (Exception e1) {
			String errMess = Messages
					.getString("QuestionItemEditor.mess.ValidationError"); //$NON-NLS-1$
			ErrorDialog.openError(site.getShell(), Messages
					.getString("ErrorTitle"), null, new Status(IStatus.ERROR,
					ID, 0, errMess, e1));
			return;
		}
		try {
			if (editorInput.getEditorMode().equals(EditorModeType.NEW)) {
				this.dao.create(questionItem);
				editorInput.setEditorMode(EditorModeType.EDIT);
			} else if (editorInput.getEditorMode()
					.equals(EditorModeType.EDIT)) {
				this.dao.update(questionItem);
			} else if (editorInput.getEditorMode()
					.equals(EditorModeType.VIEW)) {
				log.error("*** Saved ignored! ***");
			}
			updateParentView();
		} catch (Exception e) {
			String errMess = MessageFormat
					.format(
							Messages
									.getString("QuestionItemEditor.mess.ErrorDuringSave"), e.getMessage()); //$NON-NLS-1$
			ErrorDialog.openError(site.getShell(), Messages
					.getString("ErrorTitle"), null, new Status(IStatus.ERROR,
					ID, 0, errMess, e));
			return;
		}
		editorStatus.clearChanged();
		log.debug("QuestionItemEditor.doSave(1): " + editorStatus.getStatus());
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		// Initialize the Editor Part:
		super.init(site, input);
		// Initialize Question Item Editor Part:
		this.editorInput = (EditorInput) input;
		if (log.isDebugEnabled()) {
			log.debug("QuestionItemEditor.init() - Name: "
					+ editorInput.getName());
			log.debug("QuestionItemEditor.init() - ID: " + editorInput.getId());
			log.debug("QuestionItemEditor.init() - Parent ID: "
					+ editorInput.getParentId());
			log.debug("QuestionItemEditor.init() - Editor Mode: "
					+ editorInput.getEditorMode());
		}

		QuestionItemDao questionItems = new QuestionItemDao();
		if (editorInput.getEditorMode().equals(EditorModeType.NEW)) {
			try {
				questionItem = questionItems.create(editorInput
						.getId(), editorInput.getVersion(), editorInput
						.getParentId(), editorInput.getParentVersion());
			} catch (Exception e) {
				log.error("QuestionItemEditor.init(): " + e.getMessage());
				String errMess = Messages
						.getString("QuestionItemEditor.mess.ErrorDuringCreateNewQuestionItem"); //$NON-NLS-1$
				ErrorDialog.openError(site.getShell(), Messages
						.getString("ErrorTitle"), null, new Status(
						IStatus.ERROR, ID, 0, errMess, e));
				System.exit(0);
			}
		} else if (editorInput.getEditorMode().equals(EditorModeType.EDIT)
				|| editorInput.getEditorMode().equals(EditorModeType.VIEW)) {
			try {
				questionItem = questionItems.getModel(editorInput
						.getId(), editorInput.getVersion(), editorInput
						.getParentId(), editorInput.getParentVersion());
			} catch (Exception e) {
				String errMess = Messages
						.getString("QuestionItemEditor.mess.GetQuestionItemByIdError"); //$NON-NLS-1$
				ErrorDialog.openError(site.getShell(), Messages
						.getString("ErrorTitle"), null, new Status(
						IStatus.ERROR, ID, 0, errMess, e));
				System.exit(0);
			}
		} else {
			String errMess = MessageFormat
					.format(
							Messages
									.getString("QuestionItemEditor.mess.UnknownEditorMode"), editorInput.getEditorMode()); //$NON-NLS-1$
			ErrorDialog.openError(site.getShell(), Messages
					.getString("ErrorTitle"), null, new Status(IStatus.ERROR,
					ID, 0, errMess, null));
			System.exit(0);
		}

		this.site = site;
		setSite(site);
		setInput(editorInput);
		setPartName(editorInput.getId());
	}
}
