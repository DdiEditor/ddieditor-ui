package org.ddialliance.ddieditor.ui.editor.study;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.xmlbeans.XmlObject;
import org.ddialliance.ddi3.xml.xmlbeans.conceptualcomponent.UniverseDocument;
import org.ddialliance.ddi3.xml.xmlbeans.conceptualcomponent.UniverseType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.CoverageDocument;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.CreatorType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.GeographicCoverageType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.InternationalCodeValueType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.ReferenceType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.TemporalCoverageType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.TopicalCoverageType;
import org.ddialliance.ddi3.xml.xmlbeans.studyunit.KindOfDataDocument;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectListDocument;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.dbxml.studyunit.StudyUnitDao;
import org.ddialliance.ddieditor.ui.editor.DateTimeWidget;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddieditor.ui.editor.EditorInput;
import org.ddialliance.ddieditor.ui.editor.EditorInput.EditorModeType;
import org.ddialliance.ddieditor.ui.editor.widgetutil.table.TableColumnSort;
import org.ddialliance.ddieditor.ui.model.Language;
import org.ddialliance.ddieditor.ui.model.ModelAccessor;
import org.ddialliance.ddieditor.ui.model.studyunit.StudyUnit;
import org.ddialliance.ddieditor.ui.perspective.InfoPerspective;
import org.ddialliance.ddieditor.ui.util.LanguageUtil;
import org.ddialliance.ddieditor.ui.util.swtdesigner.SWTResourceManager;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.Translator;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.ddialliance.ddiftp.util.xml.XmlBeansUtil;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

/**
 * Study unit editor
 */
public class StudyUnitEditor extends Editor {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			StudyUnitEditor.class);
	public static final String ID = "org.ddialliance.ddieditor.ui.editor.study.StudyUnitEditor";

	private StyledText abstractStyledText;
	private Text titleText;
	private Text subTitleText;
	private Text altTitleText;
	private Text creatorText;
	private Text publisherText;
	private Text contributorText;
	private DateTimeWidget publicationDateTime;
	private Combo langCombo;
	private StyledText purposeStyledText;
	private IEditorSite site;
	private StudyUnit modelImpl;
	private Table uniRefTable;
	private Table keywordTable;
	private Table subjectTable;
	private Table fundingTable;
	private Table creatorTable;

	/**
	 * Default constructor
	 */
	public StudyUnitEditor() {
		super(
				Translator
						.trans("StudyUnitEditor.label.StudyUnitEditorLabel.StudyUnitEditor"),
				Translator
						.trans("StudyUnitEditor.label.useTheEditorLabel.Description"),
				ID);
		this.dao = new StudyUnitDao();
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
		this.modelImpl = (StudyUnit) model;

		// TODO Only disable editing if it is a DDA installation
		// Disable editing of Study unit - update should be done in Stud Jour.
		EditorInput editorInput = (EditorInput) input;
		editorInput.setEditorMode(EditorModeType.VIEW);
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout());
		// TODO Get descriptions
		super.createPartControl(parent);

		// Study Unit Tab Folder:
		// ------------------
		TabFolder tabFolder = createTabFolder(getComposite_1());
		tabFolder.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1));

		TabItem tbtmCitation = new TabItem(tabFolder, SWT.NONE);
		tbtmCitation
				.setText(Translator.trans("StudyUnitEditor.label.Citation"));

		Group grpStudyCitation = new Group(tabFolder, SWT.NONE);
		grpStudyCitation.setText(Translator
				.trans("StudyUnitEditor.label.StudyCitation"));
		grpStudyCitation.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		GridLayout gridLayout_2 = new GridLayout();
		gridLayout_2.numColumns = 2;
		grpStudyCitation.setLayout(gridLayout_2);
		tbtmCitation.setControl(grpStudyCitation);

		Label lblTitle = new Label(grpStudyCitation, SWT.NONE);
		lblTitle.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblTitle.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblTitle.setText(Translator.trans("StudyUnitEditor.label.Title"));

		titleText = new Text(grpStudyCitation, SWT.BORDER);
		titleText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		titleText.setText(modelImpl.getCitationTitle("da"));
		titleText.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				// TODO Handle LanguageCode
				modelImpl.setCitationTitle(titleText.getText(), "da");
				editorStatus.setChanged();
			}
		});
		super.setControl(titleText);

		Label lblSubtitle = new Label(grpStudyCitation, SWT.NONE);
		lblSubtitle.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblSubtitle.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblSubtitle.setText(Translator.trans("StudyUnitEditor.label.Subtitle"));

		subTitleText = new Text(grpStudyCitation, SWT.BORDER);
		subTitleText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		subTitleText.setText(modelImpl.getCitationSubTitle("da"));
		subTitleText.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				// TODO Handle LanguageCode
				modelImpl.setCitationSubTitle(subTitleText.getText(), "da");
				editorStatus.setChanged();
			}
		});
		super.setControl(subTitleText);

		Label lblAltTitle = new Label(grpStudyCitation, SWT.NONE);
		lblAltTitle.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblAltTitle.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblAltTitle.setText(Translator
				.trans("StudyUnitEditor.label.AlternateTitle"));

		altTitleText = new Text(grpStudyCitation, SWT.BORDER);
		altTitleText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		altTitleText.setText(modelImpl.getCitationAltTitle("da"));
		altTitleText.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				// TODO Handle LanguageCode
				modelImpl.setCitationAltTitle(altTitleText.getText(), "da");
				editorStatus.setChanged();
			}
		});
		super.setControl(altTitleText);

		// creator table
		createLabel(grpStudyCitation,
				Translator.trans("StudyUnitEditor.label.Creator"));
		creatorTable = new Table(grpStudyCitation, SWT.VIRTUAL
				| SWT.FULL_SELECTION | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL
				| SWT.BORDER);

		GridData filTableGd_1 = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.GRAB_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL
				| GridData.GRAB_VERTICAL);

		// file table header
		String[] columnNameNames = { Translator.trans("citation.creator.name"),
				Translator.trans("citation.creator.affiliation") };

		creatorTable.setRedraw(true);
		creatorTable.setLayoutData(filTableGd_1);
		creatorTable.setLinesVisible(true);
		creatorTable.setHeaderVisible(true);

		// creator table create table columns
		TableColumnSort sortcreatorTableListener = new TableColumnSort(
				creatorTable);
		for (int i = 0; i < columnNameNames.length; i++) {
			TableColumn column = new TableColumn(creatorTable, SWT.NONE);
			column.setText(columnNameNames[i]);
			column.addListener(SWT.Selection, sortcreatorTableListener);
			column.setResizable(true);
			column.setWidth(130);
		}

		// creator table update
		try {
			updateCreatorTable();
		} catch (DDIFtpException e) {
			String errMess = Translator
					.trans("StudyUnitEditor.mess.SetcreatorError"); //$NON-NLS-1$
			ErrorDialog.openError(site.getShell(), Translator
					.trans("ErrorTitle"), null, new Status(IStatus.ERROR, ID,
					0, errMess, e));
		}
		creatorTable.addSelectionListener(new TableListener());

		// publisher
		Label lblPublisher = new Label(grpStudyCitation, SWT.NONE);
		lblPublisher
				.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblPublisher.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblPublisher.setText(Translator
				.trans("StudyUnitEditor.label.Publisher"));

		publisherText = new Text(grpStudyCitation, SWT.BORDER);
		publisherText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		publisherText.setText(modelImpl.getCitationPublisher("da"));
		publisherText.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				// TODO Handle LanguageCode
				modelImpl.setCitationPublisher(publisherText.getText(), "da");
				editorStatus.setChanged();
			}
		});
		super.setControl(publisherText);

		Label lblContributor = new Label(grpStudyCitation, SWT.NONE);
		lblContributor.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		lblContributor.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblContributor.setText(Translator
				.trans("StudyUnitEditor.label.Contributor"));

		contributorText = new Text(grpStudyCitation, SWT.BORDER);
		contributorText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		contributorText.setText(modelImpl.getCitationContributor("da"));
		contributorText.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				// TODO Handle LanguageCode
				modelImpl.setCitationContributor(contributorText.getText(),
						"da");
				editorStatus.setChanged();
			}
		});
		super.setControl(contributorText);

		Label lblPublicationdate = new Label(grpStudyCitation, SWT.NONE);
		lblPublicationdate.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		lblPublicationdate.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
				false, false, 1, 1));
		lblPublicationdate.setText(Translator
				.trans("StudyUnitEditor.label.PublicationDate"));

		publicationDateTime = new DateTimeWidget(grpStudyCitation);

		String time = "";
		try {
			time = (String) modelImpl.getCitationPublicationDate();
		} catch (Exception e) {
			ErrorDialog.openError(site.getShell(), Translator
					.trans("ErrorTitle"), null, new Status(IStatus.ERROR, ID,
					0, e.getMessage(), e));
		}
		if (!time.equals("")) {
			try {
				// TODO Improve Date handling
				Calendar calendar = Translator.formatIso8601DateTime(time);
				publicationDateTime.setSelection(calendar.getTime());
			} catch (DDIFtpException e) {
				ErrorDialog.openError(site.getShell(), Translator
						.trans("ErrorTitle"), null, new Status(IStatus.ERROR,
						ID, 0, e.getMessage(), e));
			}
		}
		publicationDateTime.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Date date = publicationDateTime.getSelection();
				String dateTime = Translator.formatIso8601DateTime(date
						.getTime());
				modelImpl.setCitationPublicationDate(dateTime);
				editorStatus.setChanged();
			}
		});
		super.setControl(publicationDateTime);

		Label lblLanguage = new Label(grpStudyCitation, SWT.NONE);
		lblLanguage.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblLanguage.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblLanguage.setText(Translator.trans("StudyUnitEditor.label.Language"));

		langCombo = new Combo(grpStudyCitation, SWT.READ_ONLY);
		langCombo.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				modelImpl.setCitationLanguage(Language
						.getLanguageCode(langCombo.getText()));
				editorStatus.setChanged();
			}
		});
		langCombo
				.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		langCombo.setItems(Language.getLanguages());
		try {
			langCombo.select(Language.getLanguageIndex(modelImpl
					.getCitationLanguage()));
		} catch (Exception e) {
			ErrorDialog.openError(site.getShell(), Translator
					.trans("ErrorTitle"), null, new Status(IStatus.ERROR, ID,
					0, e.getMessage(), e));
		}
		super.setControl(langCombo);

		// Study Unit Abstract Tab Item:
		// -----------------------------
		TabItem tbtmAbstract = new TabItem(tabFolder, SWT.NONE);
		tbtmAbstract
				.setText(Translator.trans("StudyUnitEditor.label.Abstract"));
		Group grpAbstract = new Group(tabFolder, SWT.NONE);
		grpAbstract.setText(Translator
				.trans("StudyUnitEditor.label.StudyAbstract"));
		grpAbstract.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		tbtmAbstract.setControl(grpAbstract);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		grpAbstract.setLayout(gridLayout);

		// Abstract:
		final Label abstractLabel = new Label(grpAbstract, SWT.NONE);
		abstractLabel.setAlignment(SWT.RIGHT);
		final GridData gd_citationLabel = new GridData(SWT.RIGHT, SWT.FILL,
				false, true);
		gd_citationLabel.horizontalIndent = 5;
		abstractLabel.setLayoutData(gd_citationLabel);
		abstractLabel.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));
		abstractLabel.setText(Translator
				.trans("StudyUnitEditor.label.AbstractLabel")); //$NON-NLS-1$

		abstractStyledText = new StyledText(grpAbstract, SWT.WRAP
				| SWT.V_SCROLL | SWT.BORDER);
		try {
			abstractStyledText.setText(modelImpl.getAbstractContent());
		} catch (DDIFtpException e) {
			String errMess = Translator
					.trans("StudyUnitEdiror.mess.SetAbstractError"); //$NON-NLS-1$
			ErrorDialog.openError(site.getShell(), Translator
					.trans("ErrorTitle"), null, new Status(IStatus.ERROR, ID,
					0, e.getMessage(), e));
		}

		final GridData gd_originalStudyUnitTextStyledText = new GridData(
				SWT.FILL, SWT.FILL, true, true);
		gd_originalStudyUnitTextStyledText.heightHint = 328;
		gd_originalStudyUnitTextStyledText.widthHint = 308;
		abstractStyledText.setLayoutData(gd_originalStudyUnitTextStyledText);
		abstractStyledText.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				modelImpl.setAbstractContent(abstractStyledText.getText(), "da");
				editorStatus.setChanged();
			}
		});
		super.setControl(abstractStyledText);

		// Study Unit Universe Reference Tab Item
		// --------------------------------------
		// - Universe Reference Tab Item
		TabItem tbtmUniverseReference = createTabItem(Translator
				.trans("StudyUnitEditor.label.UniverseReference"));

		// - Universe Reference Group
		Group grpStudyUniverseReference = createGroup(tbtmUniverseReference,
				Translator
						.trans("StudyUnitEditor.label.StudyUniverseReference"));

		createLabel(grpStudyUniverseReference, "Universes");
		uniRefTable = new Table(grpStudyUniverseReference, SWT.VIRTUAL
				| SWT.FULL_SELECTION | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL
				| SWT.BORDER);

		GridData fileTableGd = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.GRAB_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL
				| GridData.GRAB_VERTICAL);

		uniRefTable.setRedraw(true);
		fileTableGd.horizontalSpan = 1;
		uniRefTable.setLayoutData(fileTableGd);
		uniRefTable.setLinesVisible(true);
		uniRefTable.setHeaderVisible(true);

		try {
			updateUniRefTable();
		} catch (DDIFtpException e) {
			String errMess = Translator
					.trans("StudyUnitEditor.mess.SetUniverseRefError"); //$NON-NLS-1$
			ErrorDialog.openError(site.getShell(), Translator
					.trans("ErrorTitle"), null, new Status(IStatus.ERROR, ID,
					0, errMess, e));
		}
		uniRefTable.addSelectionListener(new TableListener());

		// Study Unit Funding Tab Item:
		// ----------------------------
		TabItem tbtmFunding = new TabItem(tabFolder, SWT.NONE);
		tbtmFunding.setText(Translator.trans("StudyUnitEditor.label.Funding"));
		Group grpFunding = createGroup(tbtmFunding,
				Translator
						.trans("StudyUnitEditor.label.StudyFundingInformation"));
		createLabel(grpFunding, "Funding");

		// Funding Information
		fundingTable = new Table(grpFunding, SWT.VIRTUAL | SWT.FULL_SELECTION
				| SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		GridData fundingGd = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.GRAB_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL
				| GridData.GRAB_VERTICAL);

		fundingTable.setRedraw(true);
		fundingGd.horizontalSpan = 1;
		fundingTable.setLayoutData(fundingGd);
		fundingTable.setLinesVisible(true);
		fundingTable.setHeaderVisible(true);

		// file table header
		String[] fundingColumnNames = { Translator.trans("ID"),
				Translator.trans("StudyUnitEditor.label.Description") };

		// create table columns
		TableColumnSort fundingSort = new TableColumnSort(fundingTable);
		for (int i = 0; i < fundingColumnNames.length; i++) {
			TableColumn column = new TableColumn(fundingTable, SWT.NONE);
			column.setText(fundingColumnNames[i]);
			column.addListener(SWT.Selection, fundingSort);
			column.setResizable(true);
			column.setWidth(130);
		}

		try {
			updateFundingTable(modelImpl
					.getFundingAgencyOrganizationReferences());
		} catch (Exception e) {
			String errMess = Translator
					.trans("StudyUnitEditor.mess.SetUniverseRefError"); //$NON-NLS-1$
			ErrorDialog.openError(site.getShell(), Translator
					.trans("ErrorTitle"), null, new Status(IStatus.ERROR, ID,
					0, errMess, e));
		}
		fundingTable.addSelectionListener(new TableListener());

		// Study Unit Purpose Tab Item:
		// -----------------------------
		TabItem tbtmPurpose = new TabItem(tabFolder, SWT.NONE);
		tbtmPurpose.setText(Translator.trans("StudyUnitEditor.label.Purpose"));

		Group grpPurpose = new Group(tabFolder, SWT.NONE);
		grpPurpose.setText(Translator
				.trans("StudyUnitEditor.label.StudyPurpose"));
		grpPurpose.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		tbtmPurpose.setControl(grpPurpose);
		GridLayout gridLayout_3 = new GridLayout();
		gridLayout_3.numColumns = 2;
		grpPurpose.setLayout(gridLayout_3);

		// Purpose:
		final Label purposeLabel = new Label(grpPurpose, SWT.NONE);
		purposeLabel.setAlignment(SWT.RIGHT);
		final GridData gd_purposeLabel = new GridData(SWT.RIGHT, SWT.TOP,
				false, false);
		gd_purposeLabel.horizontalIndent = 5;
		purposeLabel.setLayoutData(gd_purposeLabel);
		purposeLabel.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));
		purposeLabel.setText(Translator
				.trans("StudyUnitEditor.label.PurposeLabel")); //$NON-NLS-1$

		purposeStyledText = new StyledText(grpPurpose, SWT.WRAP | SWT.V_SCROLL
				| SWT.BORDER);
		purposeStyledText.setText(modelImpl.getPurposeContent("da"));
		final GridData gd_purposeStudyUnitTextStyledText = new GridData(
				SWT.FILL, SWT.CENTER, true, false);
		gd_purposeStudyUnitTextStyledText.heightHint = 154;
		gd_purposeStudyUnitTextStyledText.widthHint = 308;
		purposeStyledText.setLayoutData(gd_purposeStudyUnitTextStyledText);
		purposeStyledText.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent event) {
				modelImpl.setPurposeContent(purposeStyledText.getText(), "da");
				editorStatus.setChanged();
			}
		});
		setControl(purposeStyledText);

		// Study Unit Coverage Tab Item:
		// -----------------------------
		TabItem tbtmCoverage = new TabItem(tabFolder, SWT.NONE);
		tbtmCoverage
				.setText(Translator.trans("StudyUnitEditor.label.Coverage"));

		// Coverage:
		Group grpCoverage = new Group(tabFolder, SWT.NONE);
		grpCoverage.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
				1, 1));
		grpCoverage.setText(Translator.trans("StudyUnitEditor.label.Coverage"));
		grpCoverage.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		GridLayout gridLayout_4 = new GridLayout();
		gridLayout_4.numColumns = 1;
		grpCoverage.setLayout(gridLayout_4);

		CoverageDocument coverageDoc = ((StudyUnit) model).getCoverage();

		// Topical Coverage:
		Group grpTopicalCoverage = new Group(grpCoverage, SWT.NONE);
		grpTopicalCoverage.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true, 1, 1));
		grpTopicalCoverage.setText(Translator
				.trans("StudyUnitEditor.label.TopicalCoverage"));
		grpTopicalCoverage.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		tbtmCoverage.setControl(grpCoverage);
		GridLayout gridLayout_5 = new GridLayout();
		gridLayout_5.numColumns = 2;
		grpTopicalCoverage.setLayout(gridLayout_5);

		// keyword table
		createLabel(grpTopicalCoverage,
				Translator.trans("StudyUnitEditor.label.Keywords"));
		keywordTable = new Table(grpTopicalCoverage, SWT.VIRTUAL
				| SWT.FULL_SELECTION | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL
				| SWT.BORDER);

		keywordTable.setRedraw(true);
		filTableGd_1.horizontalSpan = 1;
		keywordTable.setLayoutData(filTableGd_1);
		keywordTable.setLinesVisible(true);
		keywordTable.setHeaderVisible(true);

		// file table header
		String[] columnNames = { Translator.trans("ID"),
				Translator.trans("Description") };

		// keyword table file table header
		String[] columnName = { Translator.trans("StudyUnitEditor.label.Value") };

		// create table columns
		TableColumnSort sortListener = new TableColumnSort(uniRefTable);
		for (int i = 0; i < columnNames.length; i++) {
			TableColumn column = new TableColumn(uniRefTable, SWT.NONE);
			column.setText(columnNames[i]);
			column.addListener(SWT.Selection, sortListener);
			column.setResizable(true);
			column.setWidth(130);
		}

		// keyword table create table columns
		TableColumnSort sortKeywordTableListener = new TableColumnSort(
				keywordTable);
		for (int i = 0; i < columnName.length; i++) {
			TableColumn column = new TableColumn(keywordTable, SWT.NONE);
			column.setText(columnName[i]);
			column.addListener(SWT.Selection, sortListener);
			column.setResizable(true);
			column.setWidth(130);
		}

		// update keyword table
		try {
			updateKeywordTable();
		} catch (DDIFtpException e) {
			String errMess = Translator
					.trans("StudyUnitEditor.mess.SetKeyWordError"); //$NON-NLS-1$
			ErrorDialog.openError(site.getShell(), Translator
					.trans("ErrorTitle"), null, new Status(IStatus.ERROR, ID,
					0, errMess, e));
		}
		keywordTable.addSelectionListener(new TableListener());

		// subject table
		createLabel(grpTopicalCoverage,
				Translator.trans("StudyUnitEditor.label.Subjects"));
		subjectTable = new Table(grpTopicalCoverage, SWT.VIRTUAL
				| SWT.FULL_SELECTION | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL
				| SWT.BORDER);

		subjectTable.setRedraw(true);
		subjectTable.setLayoutData(filTableGd_1);
		subjectTable.setLinesVisible(true);
		subjectTable.setHeaderVisible(true);

		// subject table create table columns
		String[] columnNamesTable = { Translator
				.trans("StudyUnitEditor.label.Value") };

		TableColumnSort sortSubjectTableListener = new TableColumnSort(
				subjectTable);
		for (int i = 0; i < columnName.length; i++) {
			TableColumn column = new TableColumn(subjectTable, SWT.NONE);
			column.setText(columnNamesTable[i]);
			column.addListener(SWT.Selection, sortSubjectTableListener);
			column.setResizable(true);
			column.setWidth(130);
		}

		// subject table update
		try {
			updateSubjectTable();
		} catch (DDIFtpException e) {
			String errMess = Translator
					.trans("StudyUnitEditor.mess.SetsubjectError"); //$NON-NLS-1$
			ErrorDialog.openError(site.getShell(), Translator
					.trans("ErrorTitle"), null, new Status(IStatus.ERROR, ID,
					0, errMess, e));
		}
		subjectTable.addSelectionListener(new TableListener());

		// spatial coverage
		Group grpSpatialCoverage = new Group(grpCoverage, SWT.NONE);
		grpSpatialCoverage.setLayoutData(new GridData(SWT.FILL, SWT.RIGHT,
				true, false, 1, 1));
		grpSpatialCoverage.setText(Translator
				.trans("StudyUnitEditor.label.SpatialCoverage"));
		grpSpatialCoverage.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		GridLayout gridLayout_6 = new GridLayout();
		gridLayout_6.numColumns = 2;
		grpSpatialCoverage.setLayout(gridLayout_6);

		createLabel(grpSpatialCoverage,
				Translator.trans("StudyUnitEditor.label.Description"));
		String description = "";
		if (coverageDoc != null) {
			GeographicCoverageType geographicCoverage = coverageDoc
					.getCoverage().getSpatialCoverage();
			if (!geographicCoverage.getDescriptionList().isEmpty()) {
			description = XmlBeansUtil.getTextOnMixedElement(geographicCoverage
					.getDescriptionArray(0));
			}
		}
		createTextAreaInput(grpSpatialCoverage, description, false);

		createLabel(grpSpatialCoverage,
				Translator.trans("StudyUnitEditor.label.TopLevelName"));
		String name = "";
		if (coverageDoc != null) {
			try {
				name = coverageDoc.getCoverage().getSpatialCoverage()
						.getTopLevelReference().getLevelNameArray(0);
			} catch (Exception e) {
				// do nothing
				// exception hack -to many null pointer checks
			}
		}
		createText(grpSpatialCoverage, name);

		createLabel(grpSpatialCoverage,
				Translator.trans("StudyUnitEditor.label.LowestLevelName"));
		name = "";
		if (coverageDoc != null) {
			try {
				name = coverageDoc.getCoverage().getSpatialCoverage()
						.getLowestLevelReference().getLevelNameArray(0);
			} catch (Exception e) {
				// do nothing
				// exception hack -to many null pointer checks
			}
		}
		createText(grpSpatialCoverage, name);

		// Temporal Coverage:
		Group grpTemporalCoverage = new Group(grpCoverage, SWT.NONE);
		grpTemporalCoverage.setLayoutData(new GridData(SWT.FILL, SWT.RIGHT,
				true, false, 1, 1));
		grpTemporalCoverage.setText(Translator
				.trans("StudyUnitEditor.label.TemporalCoverage"));
		grpTemporalCoverage.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		GridLayout gridLayout_7 = new GridLayout();
		gridLayout_7.numColumns = 2;
		grpTemporalCoverage.setLayout(gridLayout_7);

		createLabel(grpTemporalCoverage,
				Translator.trans("StudyUnitEditor.label.StartDate"));
		DateTimeWidget startDateTime = new DateTimeWidget(grpTemporalCoverage);
		startDateTime.setEnabled(false);

		TemporalCoverageType temporalCoverage = null;
		String dateTime = "";
		if (coverageDoc != null) {
			temporalCoverage = coverageDoc.getCoverage().getTemporalCoverage();
			if (temporalCoverage != null) {
				try {
					dateTime = temporalCoverage.getReferenceDateArray(0)
							.getStartDate().toString();
				} catch (Exception e) {
					// do nothing
					// exception hack -to many null pointer checks
				}
			}
		}
		if (!dateTime.equals("")) {
			try {
				// TODO Improve Date handling
				Calendar calendar = Translator.formatIso8601DateTime(dateTime);
				startDateTime.setSelection(calendar.getTime());
			} catch (DDIFtpException e) {
				ErrorDialog.openError(site.getShell(), Translator
						.trans("ErrorTitle"), null, new Status(IStatus.ERROR,
						ID, 0, e.getMessage(), e));
			}
		}

		createLabel(grpTemporalCoverage,
				Translator.trans("StudyUnitEditor.label.EndDate"));
		DateTimeWidget endDateTime = new DateTimeWidget(grpTemporalCoverage);
		endDateTime.setEnabled(false);

		dateTime = "";
		if (temporalCoverage != null) {
			try {
				dateTime = temporalCoverage.getReferenceDateArray(0)
						.getEndDate().toString();
			} catch (Exception e) {
				// do nothing
				// exception hack -to many null pointer checks
			}
		}
		
		if (!dateTime.equals("")) {
			try {
				// TODO Improve Date handling
				Calendar calendar = Translator.formatIso8601DateTime(dateTime);
				endDateTime.setSelection(calendar.getTime());
			} catch (DDIFtpException e) {
				ErrorDialog.openError(site.getShell(), Translator
						.trans("ErrorTitle"), null, new Status(IStatus.ERROR,
						ID, 0, e.getMessage(), e));
			}
		}

		// other tab
		KindOfDataDocument kindOfDataDoc = modelImpl.getKindOfData();
		String kindOfDataString = "";
		if (kindOfDataDoc != null) {
			kindOfDataString = kindOfDataDoc.getKindOfData().getStringValue();
		}
		if (!kindOfDataString.equals("")) {
			// tab item
			TabItem tabiOther = new TabItem(tabFolder, SWT.NONE);
			tabiOther.setText(Translator.trans("StudyUnitEditor.label.Other"));

			Group grpOther = new Group(tabFolder, SWT.NONE);
			grpOther.setText(Translator.trans("StudyUnitEditor.label.Other"));
			grpOther.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			GridLayout gridLayout_8 = new GridLayout();
			gridLayout_8.numColumns = 2;
			grpOther.setLayout(gridLayout_8);
			tabiOther.setControl(grpOther);

			// kindofdata text
			createLabel(grpOther,
					Translator.trans("StudyUnitEditor.label.KindOfData"));
			createText(grpOther, kindOfDataString);
		}

		// continue with standard tab items:
		createPropertiesTab(tabFolder);
		StyledText xmlText = createXmlTab(modelImpl);
		xmlText.setEnabled(true);

		// preview tab
		createPreviewTab(modelImpl);

		// Clean dirt from initialization
		editorStatus.clearChanged();
	}

	@Override
	public String getPreferredPerspectiveId() {
		return InfoPerspective.ID;
	}

	@Override
	public String getPerspectiveSwitchDialogText() {
		return MessageFormat.format(
				Translator.trans("perspective.switch.dialogtext"),
				Translator.trans("perspective.overview"));
	}

	public void updateUniRefTable() throws DDIFtpException {

		String[] uniRefIds = modelImpl.getUniverseRefId();

		// clear file formats
		TableItem[] items = null;
		try {
			items = uniRefTable.getItems();
			for (int i = 0; i < items.length; i++) {
				items[i].dispose();
			}
		} catch (Exception e) {
			// swt null items in table exception
		}

		// insert into table
		for (int i = 0; i < uniRefIds.length; i++) {
			LightXmlObjectListDocument lightXmlList = null;
			try {
				// get Universe
				lightXmlList = DdiManager.getInstance().getUniversesLight(
						uniRefIds[i], null, null, null);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (lightXmlList.getLightXmlObjectList().getLightXmlObjectList()
					.isEmpty()) {
				continue;
			}
			LightXmlObjectType lightXmlObject = lightXmlList
					.getLightXmlObjectList().getLightXmlObjectList().get(0);
			TableItem item = new TableItem(uniRefTable, SWT.NONE);
			item.setText(0, "" + lightXmlObject.getId());
			try {
				if (lightXmlObject.getLabelList().size() > 0) {
					item.setText(1, XmlBeansUtil
							.getTextOnMixedElement((XmlObject) XmlBeansUtil
									.getLangElement(
											LanguageUtil.getDisplayLanguage(),
											lightXmlObject.getLabelList())));
					// get sub-universe - if any exist
					UniverseDocument universeDoc = DdiManager.getInstance()
							.getUniverse(lightXmlObject.getId(),
									lightXmlObject.getVersion(),
									lightXmlObject.getParentId(),
									lightXmlObject.getParentVersion());
					List<UniverseType> subUniverseList = universeDoc
							.getUniverse().getSubUniverseList();
					for (UniverseType universeType : subUniverseList) {
						item = new TableItem(uniRefTable, SWT.NONE);
						item.setText(0, "" + universeType.getId());
						if (universeType.getLabelList().size() > 0) {
							item.setText(
									1,
									"\t"
											+ XmlBeansUtil
													.getTextOnMixedElement((XmlObject) XmlBeansUtil.getLangElement(
															LanguageUtil
																	.getDisplayLanguage(),
															universeType
																	.getLabelList())));
						}
					}
				}
			} catch (DDIFtpException e) {
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			item.setData(lightXmlObject);
		}
	}

	public void updateFundingTable(List<ReferenceType> list) {
		// clear table
		TableItem[] items = null;
		try {
			items = fundingTable.getItems();
			for (int i = 0; i < items.length; i++) {
				items[i].dispose();
			}
		} catch (Exception e) {
			// swt null items in table exception
		}

		// insert into table
		for (ReferenceType referenceType : list) {
			referenceType.getIDArray(0);
			LightXmlObjectListDocument lightXmlObjectDoc = null;
			try {
				lightXmlObjectDoc = ModelAccessor.resolveReference(
						referenceType, "Organization");
			} catch (DDIFtpException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// guard
			if (lightXmlObjectDoc.getLightXmlObjectList()
					.getLightXmlObjectList().isEmpty()) {
				return;
			}
			LightXmlObjectType lightXmlObject = lightXmlObjectDoc
					.getLightXmlObjectList().getLightXmlObjectList().get(0);

			TableItem item = new TableItem(fundingTable, SWT.NONE);
			item.setText(0, "" + lightXmlObject.getId());

			try {
				item.setText(1, XmlBeansUtil
						.getTextOnMixedElement((XmlObject) XmlBeansUtil
								.getLangElement(
										LanguageUtil.getDisplayLanguage(),
										lightXmlObject.getLabelList())));
			} catch (DDIFtpException e) {
				e.printStackTrace();
			}
			item.setData(lightXmlObject);
		}
	}

	public void updateKeywordTable() throws DDIFtpException {
		CoverageDocument coverageDoc = ((StudyUnit) model).getCoverage();
		if (coverageDoc == null) {
			return;
		}
		TopicalCoverageType topicalCoverage = coverageDoc.getCoverage()
				.getTopicalCoverage();
		List<InternationalCodeValueType> keywords = topicalCoverage
				.getKeywordList();

		// clear table
		TableItem[] items = null;
		try {
			items = keywordTable.getItems();
			for (int i = 0; i < items.length; i++) {
				items[i].dispose();
			}
		} catch (Exception e) {
			// swt null items in table exception
			e.printStackTrace();
		}

		// insert into table
		for (InternationalCodeValueType keyword : keywords) {
			if (keyword.getLang() == null
					|| keyword.getLang().equals(
							LanguageUtil.getDisplayLanguage())) {
				TableItem item = new TableItem(keywordTable, SWT.NONE);
				item.setText(0, "" + keyword.getStringValue());
			}
		}
	}

	private void updateSubjectTable() throws DDIFtpException {
		// clear table
		TableItem[] items = null;
		try {
			items = subjectTable.getItems();
			for (int i = 0; i < items.length; i++) {
				items[i].dispose();
			}
		} catch (Exception e) {
			// swt null items in table exception
			e.printStackTrace();
		}

		// insert into table
		CoverageDocument coverageDoc = ((StudyUnit) model).getCoverage();
		if (coverageDoc == null) {
			return;
		}
		List<InternationalCodeValueType> subjects = coverageDoc.getCoverage()
				.getTopicalCoverage().getSubjectList();

		for (InternationalCodeValueType subject : subjects) {
			if (subject.getLang() == null
					|| subject.getLang().equals(
							LanguageUtil.getDisplayLanguage())) {
				TableItem item = new TableItem(subjectTable, SWT.NONE);
				item.setText(0, "" + subject.getStringValue());
			}
		}
	}

	void updateCreatorTable() throws DDIFtpException {
		List<CreatorType> creators = modelImpl.getDocument().getStudyUnit()
				.getCitation().getCreatorList();
		if (creators.isEmpty()) {
			return;
		}

		// clear table
		TableItem[] items = null;
		try {
			items = creatorTable.getItems();
			for (int i = 0; i < items.length; i++) {
				items[i].dispose();
			}
		} catch (Exception e) {
			// swt null items in table exception
			e.printStackTrace();
		}

		// insert into table
		for (CreatorType creator : creators) {
			TableItem item = new TableItem(creatorTable, SWT.NONE);
			item.setText(0, creator.getStringValue());
			if (creator.getAffiliation() != null) {
				item.setText(1, creator.getAffiliation());
			} else {
				item.setText(1, "");
			}
		}
	}

	// currently not used as edit is not supported, yet!
	class TableListener implements SelectionListener {
		public TableListener() {

		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			// do nothing
		}

		@Override
		public void widgetSelected(SelectionEvent event) {
			TableItem[] selections = ((Table) event.widget).getSelection();

			// guard
			if (selections.length == 0) {
				return;
			}
		}
	}
}
