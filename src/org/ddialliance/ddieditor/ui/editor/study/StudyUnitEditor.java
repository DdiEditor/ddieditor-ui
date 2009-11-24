package org.ddialliance.ddieditor.ui.editor.study;

/**
 * Study Unit Editor.
 * 
 */
/*
 * $Author$ 
 * $Date$ 
 * $Revision$
 */

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.dbxml.studyunit.StudyUnits;
import org.ddialliance.ddieditor.ui.dbxml.universe.UniverseDao;
import org.ddialliance.ddieditor.ui.editor.DateTimeWidget;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddieditor.ui.editor.EditorInput;
import org.ddialliance.ddieditor.ui.editor.FilteredItemsSelection;
import org.ddialliance.ddieditor.ui.editor.EditorInput.EditorModeType;
import org.ddialliance.ddieditor.ui.model.Language;
import org.ddialliance.ddieditor.ui.model.studyunit.StudyUnit;
import org.ddialliance.ddieditor.ui.perspective.InfoPerspective;
import org.ddialliance.ddieditor.ui.util.swtdesigner.SWTResourceManager;
import org.ddialliance.ddieditor.ui.view.Messages;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.Translator;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.PartInitException;

/**
 * Study Unit Editor.
 * 
 */
/*
 * $Author$ $Date$
 * $Revision$
 */
public class StudyUnitEditor extends Editor implements ISelectionListener {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			StudyUnitEditor.class);
	public static final String ID = "org.ddialliance.ddieditor.ui.editor.study.StudyUnitEditor";

	// Member variables:
	private StudyUnit studyUnit;
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
	/**
	 * Constructor of Study Unit Editor
	 */
	public StudyUnitEditor() {
		super(
				Messages
						.getString("StudyUnitEditor.label.StudyUnitEditorLabel.StudyUnitEditor"),
				Messages
						.getString("StudyUnitEditor.label.useTheEditorLabel.Description"));
	}

	/**
	 * Get corresponding ID of preferred perspective.
	 * 
	 * @return String
	 */
	public String getPreferredPerspectiveId() {
		return InfoPerspective.ID;
	}

	/**
	 * Get Perspective Switch dialog text.
	 * 
	 * @return String
	 */
	public String getPerspectiveSwitchDialogText() {
		return MessageFormat.format(Messages
				.getString("perspective.switch.dialogtext"), Messages
				.getString("perspective.overview"));
	}

	/**
	 * Create contents of the editor part
	 * 
	 * Calls the 'Editor' super class.
	 * 
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout());
		log.debug("StudyUnitEditor.createPartControl called");
		// TODO Get descriptions
		super.createPartControl(parent);

		// Study Unit Tab Folder:
		// ------------------
		TabFolder tabFolder = new TabFolder(getComposite_1(), SWT.BOTTOM);
		tabFolder.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		TabItem tbtmCitation = new TabItem(tabFolder, SWT.NONE);
		tbtmCitation.setText(Messages.getString("StudyUnitEditor.label.Citation"));

		Group grpStudyCitation = new Group(tabFolder, SWT.NONE);
		grpStudyCitation.setText(Messages.getString("StudyUnitEditor.label.StudyCitation"));
		grpStudyCitation.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		GridLayout gridLayout_2 = new GridLayout();
		gridLayout_2.numColumns = 2;
		grpStudyCitation.setLayout(gridLayout_2);
		tbtmCitation.setControl(grpStudyCitation);

		Label lblTitle = new Label(grpStudyCitation, SWT.NONE);
		lblTitle.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblTitle.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblTitle.setText(Messages.getString("StudyUnitEditor.label.Title"));

		titleText = new Text(grpStudyCitation, SWT.BORDER);
		titleText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		titleText.setText(studyUnit.getCitationTitle("da"));
		titleText.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				log.debug("Title changed");
				// TODO Handle LanguageCode
				studyUnit.setCitationTitle(titleText.getText(), "da");
				editorStatus.setChanged();
			}
		});
		super.setControl(titleText);

		Label lblSubtitle = new Label(grpStudyCitation, SWT.NONE);
		lblSubtitle.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblSubtitle.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSubtitle.setText(Messages.getString("StudyUnitEditor.label.Subtitle"));

		subTitleText = new Text(grpStudyCitation, SWT.BORDER);
		subTitleText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		subTitleText.setText(studyUnit.getCitationSubTitle("da"));
		subTitleText.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				log.debug("SubTitle changed");
				// TODO Handle LanguageCode
				studyUnit.setCitationSubTitle(subTitleText.getText(), "da");
				editorStatus.setChanged();
			}
		});
		super.setControl(subTitleText);

		Label lblAltTitle = new Label(grpStudyCitation, SWT.NONE);
		lblAltTitle.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblAltTitle.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblAltTitle.setText(Messages.getString("StudyUnitEditor.label.AlternateTitle"));

		altTitleText = new Text(grpStudyCitation, SWT.BORDER);
		altTitleText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		altTitleText.setText(studyUnit.getCitationAltTitle("da"));
		altTitleText.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				log.debug("Alt. Title changed");
				// TODO Handle LanguageCode
				studyUnit.setCitationAltTitle(altTitleText.getText(), "da");
				editorStatus.setChanged();
			}
		});
		super.setControl(altTitleText);

		Label lblCreator = new Label(grpStudyCitation, SWT.NONE);
		lblCreator.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblCreator.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblCreator.setText(Messages.getString("StudyUnitEditor.label.Creator"));

		creatorText = new Text(grpStudyCitation, SWT.BORDER);
		creatorText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		creatorText.setText(studyUnit.getCitationCreator("da"));
		creatorText.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				log.debug("Creator changed");
				// TODO Handle LanguageCode
				studyUnit.setCitationCreator(creatorText.getText(), "da");
				editorStatus.setChanged();
			}
		});
		super.setControl(creatorText);

		// publisher
		Label lblPublisher = new Label(grpStudyCitation, SWT.NONE);
		lblPublisher.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblPublisher.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPublisher.setText(Messages.getString("StudyUnitEditor.label.Publisher"));

		publisherText = new Text(grpStudyCitation, SWT.BORDER);
		publisherText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		publisherText.setText(studyUnit.getCitationPublisher("da"));
		publisherText.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				log.debug("Publisher changed");
				// TODO Handle LanguageCode
				studyUnit.setCitationPublisher(publisherText.getText(), "da");
				editorStatus.setChanged();
			}
		});
		super.setControl(publisherText);

		Label lblContributor = new Label(grpStudyCitation, SWT.NONE);
		lblContributor.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblContributor.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblContributor.setText(Messages.getString("StudyUnitEditor.label.Contributor"));

		contributorText = new Text(grpStudyCitation, SWT.BORDER);
		contributorText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		contributorText.setText(studyUnit.getCitationContributor("da"));
		contributorText.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				log.debug("Contributor changed");
				// TODO Handle LanguageCode
				studyUnit.setCitationContributor(contributorText.getText(), "da");
				editorStatus.setChanged();
			}
		});
		super.setControl(contributorText);

		Label lblPublicationdate = new Label(grpStudyCitation, SWT.NONE);
		lblPublicationdate.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblPublicationdate.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPublicationdate.setText(Messages.getString("StudyUnitEditor.label.PublicationDate"));

		publicationDateTime = new DateTimeWidget(grpStudyCitation);

		String time = "";
		try {
			time = (String) studyUnit.getCitationPublicationDate();
		} catch (Exception e2) {
			ErrorDialog.openError(site.getShell(), Messages.getString("ErrorTitle"), null, new Status(IStatus.ERROR,
					ID, 0, e2.getMessage(), e2));
		}
		if (!time.equals("")) {
			try {
				// TODO Improve Date handling
				Calendar calendar = Translator.formatIso8601DateTime(time);
				publicationDateTime.setSelection(calendar.getTime());
			} catch (DDIFtpException e1) {
				ErrorDialog.openError(site.getShell(), Messages.getString("ErrorTitle"), null, new Status(
						IStatus.ERROR, ID, 0, e1.getMessage(), e1));
			}
		}
		publicationDateTime.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Date date = publicationDateTime.getSelection();
				String dateTime = Translator.formatIso8601DateTime(date.getTime());
				studyUnit.setCitationPublicationDate(dateTime);
				editorStatus.setChanged();
			}
		});
		super.setControl(publicationDateTime);

		Label lblLanguage = new Label(grpStudyCitation, SWT.NONE);
		lblLanguage.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblLanguage.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblLanguage.setText(Messages.getString("StudyUnitEditor.label.Language"));

		langCombo = new Combo(grpStudyCitation, SWT.READ_ONLY);
		langCombo.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				studyUnit.setCitationLanguage(Language.getLanguageCode(langCombo.getText()));
				editorStatus.setChanged();
			}
		});
		langCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		langCombo.setItems(Language.getLanguages());
		try {
			langCombo.select(Language.getLanguageIndex(studyUnit.getCitationLanguage()));
		} catch (Exception e1) {
			ErrorDialog.openError(site.getShell(), Messages.getString("ErrorTitle"), null, new Status(IStatus.ERROR,
					ID, 0, e1.getMessage(), e1));
		}
		super.setControl(langCombo);

		// Study Unit Abstract Tab Item:
		// -----------------------------
		TabItem tbtmAbstract = new TabItem(tabFolder, SWT.NONE);
		tbtmAbstract.setText(Messages.getString("StudyUnitEditor.label.Abstract"));
		Group grpAbstract = new Group(tabFolder, SWT.NONE);
		grpAbstract.setText(Messages.getString("StudyUnitEditor.label.StudyAbstract"));
		grpAbstract.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		tbtmAbstract.setControl(grpAbstract);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		grpAbstract.setLayout(gridLayout);

		// Abstract:
		final Label abstractLabel = new Label(grpAbstract, SWT.NONE);
		abstractLabel.setAlignment(SWT.RIGHT);
		final GridData gd_citationLabel = new GridData(SWT.RIGHT, SWT.FILL, false, true);
		gd_citationLabel.horizontalIndent = 5;
		abstractLabel.setLayoutData(gd_citationLabel);
		abstractLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		abstractLabel.setText(Messages.getString("StudyUnitEditor.label.AbstractLabel")); //$NON-NLS-1$

		abstractStyledText = new StyledText(grpAbstract, SWT.WRAP | SWT.V_SCROLL | SWT.BORDER);
		abstractStyledText.setText(studyUnit.getAbstractContent("da"));
		final GridData gd_originalStudyUnitTextStyledText = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd_originalStudyUnitTextStyledText.heightHint = 328;
		gd_originalStudyUnitTextStyledText.widthHint = 308;
		abstractStyledText.setLayoutData(gd_originalStudyUnitTextStyledText);
		abstractStyledText.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				log.debug("Abstract Content changed");
				studyUnit.setAbstractContent(abstractStyledText.getText(), "da");
				editorStatus.setChanged();
			}
		});
		super.setControl(abstractStyledText);
		
		// Study Unit Universe Reference Tab Item
		// --------------------------------------
		// Note: Currently references to Universe Scheme is not supported.
		//
		// - Universe Reference Tab Item
        TabItem tbtmUniverseReference = new TabItem(tabFolder, SWT.NONE);
        tbtmUniverseReference.setText(Messages.getString("StudyUnitEditor.label.UniverseReference"));
			
        // - Universe Reference Group
        Group grpStudyUniverseReference = new Group(tabFolder, SWT.NONE);
        grpStudyUniverseReference.setText(Messages.getString("StudyUnitEditor.label.StudyUniverseReference"));
        grpStudyUniverseReference.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        GridLayout gridLayout_6 = new GridLayout();
        gridLayout_6.numColumns = 2;
        grpStudyUniverseReference.setLayout(gridLayout_6);
        tbtmUniverseReference.setControl(grpStudyUniverseReference);
        
        // Universe Reference
		final Composite LabelComposite = new Composite(grpStudyUniverseReference, SWT.NONE);
		LabelComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false));
		LabelComposite.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));
		final GridLayout gridLayout_a = new GridLayout();
		gridLayout_a.marginWidth = 0;
		gridLayout_a.marginHeight = 0;
		LabelComposite.setLayout(gridLayout_a);

		final Composite codeComposite = new Composite(grpStudyUniverseReference, SWT.NONE);
		codeComposite.setRedraw(true);
		codeComposite.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));
		codeComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));
		final GridLayout gridLayout_b = new GridLayout();
		gridLayout_b.marginHeight = 0;
		gridLayout_b.marginWidth = 0;
		codeComposite.setLayout(gridLayout_b);

		final FilteredItemsSelection filteredItemsSelection = new FilteredItemsSelection();

		// - Get available Universes:
		List<LightXmlObjectType> universeReferenceList = new ArrayList();
		try {
			universeReferenceList = new UniverseDao().getLightXmlObject("", "", "", "");
		} catch (Exception e1) {
			String errMess = Messages.getString("StudyUnitEditor.mess.UniverseRetrievalError"); //$NON-NLS-1$
			ErrorDialog.openError(site.getShell(), Messages.getString("ErrorTitle"), null, new Status(IStatus.ERROR,
					ID, 0, errMess, e1));
		}
		
		// - Create Universe selection composite:
		try {
			filteredItemsSelection.createPartControl(LabelComposite, codeComposite, "", "Universe",
					universeReferenceList, studyUnit.getUniverseRefId());
		} catch (Exception e2) {
			String errMess = Messages.getString("StudyUnitEditor.mess.CreateFilteredItemSelectionError"); //$NON-NLS-1$
			ErrorDialog.openError(site.getShell(), Messages.getString("ErrorTitle"), null, new Status(IStatus.ERROR,
					ID, 0, errMess, e2));
		}
		filteredItemsSelection.addSelectionListener(
				Messages.getString("StudyUnitEditor.label.SelectUniverseReference"), universeReferenceList,
				new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						LightXmlObjectType result = (LightXmlObjectType) filteredItemsSelection.getResult();
						if (result != null) {
							try {
								studyUnit.setUniverseRefId(result.getId());
							} catch (Exception e1) {
								String errMess = MessageFormat.format(Messages
										.getString("StudyUnitEditor.mess.SetUniverseRefError"), e1.getMessage()); //$NON-NLS-1$
								ErrorDialog.openError(site.getShell(), Messages.getString("ErrorTitle"), null,
										new Status(IStatus.ERROR, ID, 0, errMess, e1));
							}
							editorStatus.setChanged();
						}
					}
				});

		// Study Unit Funding Tab Item:
		// ----------------------------
		TabItem tbtmFunding = new TabItem(tabFolder, SWT.NONE);
		tbtmFunding.setText(Messages.getString("StudyUnitEditor.label.Funding"));

		Group grpFunding = new Group(tabFolder, SWT.NONE);
		grpFunding.setText(Messages.getString("StudyUnitEditor.label.StudyFundingInformation"));
		grpFunding.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		tbtmFunding.setControl(grpFunding);
		GridLayout gridLayout_4 = new GridLayout();
		gridLayout_4.numColumns = 2;
		grpFunding.setLayout(gridLayout_4);

		// Funding Information
		// TODO Change Funding Information from discrete fields to table
		Label lblAgency = new Label(grpFunding, SWT.NONE);
		lblAgency.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblAgency.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblAgency.setText(Messages.getString("StudyUnitEditor.label.Agency"));

		agencyText = new Text(grpFunding, SWT.BORDER);
		agencyText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		try {
			agencyText.setText(studyUnit.getFundingAgencyID());
		} catch (DDIFtpException e1) {
			String errMess = Messages.getString("StudyUnitEditor.mess.GetFundingTextError"); //$NON-NLS-1$
			ErrorDialog.openError(site.getShell(), Messages.getString("ErrorTitle"), null, new Status(IStatus.ERROR,
					ID, 0, errMess, e1));
		}
		agencyText.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				log.debug("Agency changed");
				try {
					studyUnit.setFundingAgencyID(agencyText.getText());
					studyUnit.setFundingIdentifyingAgency(identifyingAgencyText.getText());
				} catch (DDIFtpException e2) {
					String errMess = Messages.getString("StudyUnitEditor.mess.SetFundingInfoError"); //$NON-NLS-1$
					ErrorDialog.openError(site.getShell(), Messages.getString("ErrorTitle"), null, new Status(
							IStatus.ERROR, ID, 0, errMess, e2));
				}
				editorStatus.setChanged();
			}
		});
		super.setControl(agencyText);

		Label lblIdentifyingAgency = new Label(grpFunding, SWT.NONE);
		lblIdentifyingAgency.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblIdentifyingAgency.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblIdentifyingAgency.setText(Messages.getString("StudyUnitEditor.label.IdentifyingAgency"));

		identifyingAgencyText = new Text(grpFunding, SWT.BORDER);
		identifyingAgencyText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		try {
			identifyingAgencyText.setText(studyUnit.getFundingIdentifyingAgency());
		} catch (DDIFtpException e1) {
			String errMess = Messages.getString("StudyUnitEditor.mess.GetFundingIdentifyingAgencyError"); //$NON-NLS-1$
			ErrorDialog.openError(site.getShell(), Messages.getString("ErrorTitle"), null, new Status(IStatus.ERROR,
					ID, 0, errMess, e1));
		}
		identifyingAgencyText.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				log.debug("Identifying Agency changed");
				try {
					studyUnit.setFundingAgencyID(agencyText.getText());
					studyUnit.setFundingIdentifyingAgency(identifyingAgencyText.getText());
				} catch (DDIFtpException e1) {
					String errMess = Messages.getString("StudyUnitEditor.mess.SetFundingAgencyInfoError"); //$NON-NLS-1$
					ErrorDialog.openError(site.getShell(), Messages.getString("ErrorTitle"), null, new Status(
							IStatus.ERROR, ID, 0, errMess, e1));
				}
				editorStatus.setChanged();
			}
		});
		super.setControl(identifyingAgencyText);

		// Study Unit Purpose Tab Item:
		// -----------------------------
		TabItem tbtmPurpose = new TabItem(tabFolder, SWT.NONE);
		tbtmPurpose.setText(Messages.getString("StudyUnitEditor.label.Purpose"));

		Group grpPurpose = new Group(tabFolder, SWT.NONE);
		grpPurpose.setText(Messages.getString("StudyUnitEditor.label.StudyPurpose"));
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
		purposeLabel.setText(Messages.getString("StudyUnitEditor.label.PurposeLabel")); //$NON-NLS-1$

		purposeStyledText = new StyledText(grpPurpose, SWT.WRAP | SWT.V_SCROLL | SWT.BORDER);
		purposeStyledText.setText(studyUnit.getPurposeContent("da"));
		final GridData gd_purposeStudyUnitTextStyledText = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd_purposeStudyUnitTextStyledText.heightHint = 154;
		gd_purposeStudyUnitTextStyledText.widthHint = 308;
		purposeStyledText.setLayoutData(gd_purposeStudyUnitTextStyledText);
		purposeStyledText.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				log.debug("Purpose changed");
				studyUnit.setPurposeContent(purposeStyledText.getText(), "da");
				editorStatus.setChanged();
			}
		});
		super.setControl(purposeStyledText);

		super.createPropertiesTab(tabFolder);

		// Clean dirt from initialization
		editorStatus.clearChanged();

	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		log.debug("StudyUnitEditor.doSave()");
		super.doSave(monitor);

		try {
			studyUnit.validate();
		} catch (Exception e1) {
			String errMess = Messages.getString("StudyUnitEditor.mess.ValidationError"); //$NON-NLS-1$
			ErrorDialog.openError(site.getShell(), Messages.getString("ErrorTitle"), null, new Status(IStatus.ERROR,
					ID, 0, errMess, null));
			return;
		}
		try {
			if (editorInput.getEditorMode().equals(EditorModeType.NEW)) {
				StudyUnits.create(studyUnit);
				editorInput.setEditorMode(EditorModeType.EDIT);
			} else if (editorInput.getEditorMode().equals(EditorModeType.EDIT)) {
				StudyUnits.update(studyUnit);
			} else if (editorInput.getEditorMode().equals(EditorModeType.VIEW)) {
				log.debug("*** Saved ignored! ***");
			}
		} catch (Exception e) {
			String errMess = Messages.getString("StudyUnitEditor.mess.ErrorDuringSave"); //$NON-NLS-1$
			ErrorDialog.openError(site.getShell(), Messages.getString("ErrorTitle"), null, new Status(IStatus.ERROR,
					ID, 0, errMess, e));
			return;
		}
		editorInput.getParentView().refreshView();
		editorStatus.clearChanged();
		log.debug("StudyUnitEditor.doSave(1): " + editorStatus.getStatus());
	}

	/**
	 * Study Unit Editor initialization.
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {

		// Initialize Study Unit Editor Part:
		this.editorInput = (EditorInput) input;
		if (log.isDebugEnabled()) {
			log.debug("StudyUnitEditor.init() - Name: " + editorInput.getName());
			log.debug("StudyUnitEditor.init() - ID: " + editorInput.getId());
			log.debug("StudyUnitEditor.init() - Parent ID: " + editorInput.getParentId());
			log.debug("StudyUnitEditor.init() - Editor Mode: " + editorInput.getEditorMode());
		}

		StudyUnits.init(((EditorInput) input).getProperties());

		if (editorInput.getEditorMode().equals(EditorModeType.NEW)) {
			try {
				studyUnit = StudyUnits.createStudyUnit(editorInput.getId(), editorInput.getVersion(), editorInput
						.getParentId(), editorInput.getParentVersion(), null);
			} catch (Exception e) {
				log.error("StudyUnitEditor.init(): " + e.getMessage());
				String errMess = Messages.getString("StudyUnitEditor.mess.ErrorDuringCreateNewStudyUnit"); //$NON-NLS-1$
				ErrorDialog.openError(site.getShell(), Messages.getString("ErrorTitle"), null, new Status(
						IStatus.ERROR, ID, 0, errMess, e));
				System.exit(0);
			}
		} else if (editorInput.getEditorMode().equals(EditorModeType.EDIT)
				|| editorInput.getEditorMode().equals(EditorModeType.VIEW)) {
			try {
				studyUnit = StudyUnits.getStudyUnit(editorInput.getId(), editorInput.getVersion(), editorInput
						.getParentId(), editorInput.getParentVersion());
			} catch (Exception e) {
				String errMess = Messages.getString("StudyUnitEditor.mess.GetStudyUnitByIdError"); //$NON-NLS-1$
				ErrorDialog.openError(site.getShell(), Messages.getString("ErrorTitle"), null, new Status(
						IStatus.ERROR, ID, 0, errMess, e));
				System.exit(0);
			}
		} else {
			String errMess = MessageFormat.format(
					Messages.getString("StudyUnitEditor.mess.UnknownEditorMode"), editorInput.getEditorMode()); //$NON-NLS-1$
			MessageDialog.openError(site.getShell(), Messages.getString("ErrorTitle"), errMess);
			System.exit(0);
		}

		this.site = site;
		setSite(site);
		setInput(editorInput);
		setPartName(editorInput.getId());

	}
}
