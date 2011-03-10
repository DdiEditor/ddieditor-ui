package org.ddialliance.ddieditor.ui.editor.study;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.xmlbeans.XmlObject;
import org.ddialliance.ddi3.xml.xmlbeans.conceptualcomponent.UniverseDocument;
import org.ddialliance.ddi3.xml.xmlbeans.conceptualcomponent.UniverseType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.ReferenceType;
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
import org.ddialliance.ddieditor.ui.view.Messages;
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
	private Text agencyText;
	private Text identifyingAgencyText;
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
	private Table fundingTable;

	/**
	 * Default constructor
	 */
	public StudyUnitEditor() {
		super(
				Messages.getString("StudyUnitEditor.label.StudyUnitEditorLabel.StudyUnitEditor"),
				Messages.getString("StudyUnitEditor.label.useTheEditorLabel.Description"),
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
		tbtmCitation.setText(Messages
				.getString("StudyUnitEditor.label.Citation"));

		Group grpStudyCitation = new Group(tabFolder, SWT.NONE);
		grpStudyCitation.setText(Messages
				.getString("StudyUnitEditor.label.StudyCitation"));
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
		lblTitle.setText(Messages.getString("StudyUnitEditor.label.Title"));

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
		lblSubtitle.setText(Messages
				.getString("StudyUnitEditor.label.Subtitle"));

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
		lblAltTitle.setText(Messages
				.getString("StudyUnitEditor.label.AlternateTitle"));

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

		Label lblCreator = new Label(grpStudyCitation, SWT.NONE);
		lblCreator.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblCreator.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblCreator.setText(Messages.getString("StudyUnitEditor.label.Creator"));

		creatorText = new Text(grpStudyCitation, SWT.BORDER);
		creatorText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		creatorText.setText(modelImpl.getCitationCreator("da"));
		creatorText.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				// TODO Handle LanguageCode
				modelImpl.setCitationCreator(creatorText.getText(), "da");
				editorStatus.setChanged();
			}
		});
		super.setControl(creatorText);

		// publisher
		Label lblPublisher = new Label(grpStudyCitation, SWT.NONE);
		lblPublisher
				.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblPublisher.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblPublisher.setText(Messages
				.getString("StudyUnitEditor.label.Publisher"));

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
		lblContributor.setText(Messages
				.getString("StudyUnitEditor.label.Contributor"));

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
		lblPublicationdate.setText(Messages
				.getString("StudyUnitEditor.label.PublicationDate"));

		publicationDateTime = new DateTimeWidget(grpStudyCitation);

		String time = "";
		try {
			time = (String) modelImpl.getCitationPublicationDate();
		} catch (Exception e) {
			ErrorDialog.openError(site.getShell(), Messages
					.getString("ErrorTitle"), null, new Status(IStatus.ERROR,
					ID, 0, e.getMessage(), e));
		}
		if (!time.equals("")) {
			try {
				// TODO Improve Date handling
				Calendar calendar = Translator.formatIso8601DateTime(time);
				publicationDateTime.setSelection(calendar.getTime());
			} catch (DDIFtpException e) {
				ErrorDialog.openError(site.getShell(), Messages
						.getString("ErrorTitle"), null, new Status(
						IStatus.ERROR, ID, 0, e.getMessage(), e));
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
		lblLanguage.setText(Messages
				.getString("StudyUnitEditor.label.Language"));

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
			ErrorDialog.openError(site.getShell(), Messages
					.getString("ErrorTitle"), null, new Status(IStatus.ERROR,
					ID, 0, e.getMessage(), e));
		}
		super.setControl(langCombo);

		// Study Unit Abstract Tab Item:
		// -----------------------------
		TabItem tbtmAbstract = new TabItem(tabFolder, SWT.NONE);
		tbtmAbstract.setText(Messages
				.getString("StudyUnitEditor.label.Abstract"));
		Group grpAbstract = new Group(tabFolder, SWT.NONE);
		grpAbstract.setText(Messages
				.getString("StudyUnitEditor.label.StudyAbstract"));
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
		abstractLabel.setText(Messages
				.getString("StudyUnitEditor.label.AbstractLabel")); //$NON-NLS-1$

		abstractStyledText = new StyledText(grpAbstract, SWT.WRAP
				| SWT.V_SCROLL | SWT.BORDER);
		abstractStyledText.setText(modelImpl.getAbstractContent("da"));
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
		TabItem tbtmUniverseReference = createTabItem(Messages
				.getString("StudyUnitEditor.label.UniverseReference"));

		// - Universe Reference Group
		Group grpStudyUniverseReference = createGroup(
				tbtmUniverseReference,
				Messages.getString("StudyUnitEditor.label.StudyUniverseReference"));

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

		// file table header
		String[] columnNames = { Translator.trans("ID"),
				Translator.trans("Description") };

		// create table columns
		TableColumnSort sortListener = new TableColumnSort(uniRefTable);
		for (int i = 0; i < columnNames.length; i++) {
			TableColumn column = new TableColumn(uniRefTable, SWT.NONE);
			column.setText(columnNames[i]);
			column.addListener(SWT.Selection, sortListener);
			column.setResizable(true);
			column.setWidth(130);
		}

		try {
			updateUniRefTable();
		} catch (DDIFtpException e) {
			String errMess = Messages
					.getString("StudyUnitEditor.mess.SetUniverseRefError"); //$NON-NLS-1$
			ErrorDialog.openError(site.getShell(), Messages
					.getString("ErrorTitle"), null, new Status(IStatus.ERROR,
					ID, 0, errMess, e));
		}
		uniRefTable.addSelectionListener(new TableListener());

		// Study Unit Funding Tab Item:
		// ----------------------------
		TabItem tbtmFunding = new TabItem(tabFolder, SWT.NONE);
		tbtmFunding
				.setText(Messages.getString("StudyUnitEditor.label.Funding"));
		Group grpFunding = createGroup(
				tbtmFunding,
				Messages.getString("StudyUnitEditor.label.StudyFundingInformation"));
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
				Translator.trans("Description") };

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
			opdateFundingTable(modelImpl
					.getFundingAgencyOrganizationReferences());
		} catch (Exception e) {
			String errMess = Messages
					.getString("StudyUnitEditor.mess.SetUniverseRefError"); //$NON-NLS-1$
			ErrorDialog.openError(site.getShell(), Messages
					.getString("ErrorTitle"), null, new Status(IStatus.ERROR,
					ID, 0, errMess, e));
		}
		fundingTable.addSelectionListener(new TableListener());

		// Study Unit Purpose Tab Item:
		// -----------------------------
		TabItem tbtmPurpose = new TabItem(tabFolder, SWT.NONE);
		tbtmPurpose
				.setText(Messages.getString("StudyUnitEditor.label.Purpose"));

		Group grpPurpose = new Group(tabFolder, SWT.NONE);
		grpPurpose.setText(Messages
				.getString("StudyUnitEditor.label.StudyPurpose"));
		grpPurpose.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		tbtmPurpose.setControl(grpPurpose);
		GridLayout gridLayout_5 = new GridLayout();
		gridLayout_5.numColumns = 2;
		grpPurpose.setLayout(gridLayout_5);

		// Purpose:
		final Label purposeLabel = new Label(grpPurpose, SWT.NONE);
		purposeLabel.setAlignment(SWT.RIGHT);
		final GridData gd_purposeLabel = new GridData(SWT.RIGHT, SWT.TOP,
				false, false);
		gd_purposeLabel.horizontalIndent = 5;
		purposeLabel.setLayoutData(gd_purposeLabel);
		purposeLabel.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));
		purposeLabel.setText(Messages
				.getString("StudyUnitEditor.label.PurposeLabel")); //$NON-NLS-1$

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

		createPropertiesTab(tabFolder);

		createXmlTab(modelImpl);

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
				Messages.getString("perspective.switch.dialogtext"),
				Messages.getString("perspective.overview"));
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

	public void opdateFundingTable(List<ReferenceType> list) {
		// clear file formats
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
			// TODO
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
