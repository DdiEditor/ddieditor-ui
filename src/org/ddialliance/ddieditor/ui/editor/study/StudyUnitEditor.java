package org.ddialliance.ddieditor.ui.editor.study;

import java.text.MessageFormat;

import org.ddialliance.ddieditor.ui.InfoPerspective;
import org.ddialliance.ddieditor.ui.dbxml.StudyUnits;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddieditor.ui.editor.EditorInput;
import org.ddialliance.ddieditor.ui.editor.EditorInput.EDITOR_MODE_TYPE;
import org.ddialliance.ddieditor.ui.model.Simple;
import org.ddialliance.ddieditor.ui.model.StudyUnit;
import org.ddialliance.ddieditor.ui.util.SWTResourceManager;
import org.ddialliance.ddieditor.ui.view.Messages;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
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
 * Study Unit Editor
 * 
 * $Revision: $
 * 
 * @author dak
 *
 */
public class StudyUnitEditor extends Editor implements ISelectionListener {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, StudyUnitEditor.class);
	public static final String ID = "org.ddialliance.ddieditor.ui.editor.study.StudyUnitEditor";

	// Member variables:
	private StudyUnit studyUnit;
	private Text nameText;
	private StyledText citationStyledText;
	private StyledText abstractStyledText;
	private Text agencyText;
	private Text titleText;
	private Text subtitleText;
	private Text creatorText;
	private Text publisherText;
	private Text contributorText;
	private Text PublicationDateText;
	private Text languageText;
	private IEditorSite site;
	
	/**
	 * Constructor of Study Unit Editor 
	 */
	public StudyUnitEditor() {
		super(Messages.getString("StudyUnitEditor.label.StudyUnitEditorLabel.StudyUnitEditor"), 
				Messages.getString("StudyUnitEditor.label.useTheEditorLabel.Description"));
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
		return MessageFormat.format(Messages.getString("perspective.switch.dialogtext"), Messages
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
		
		TabItem tbtmStudyIdentification = new TabItem(tabFolder, SWT.NONE);
		tbtmStudyIdentification.setText("Study Identification");
		
		Group group = new Group(tabFolder, SWT.NONE);
		group.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		GridLayout gridLayout_2 = new GridLayout();
		gridLayout_2.numColumns = 2;
		group.setLayout(gridLayout_2);
		tbtmStudyIdentification.setControl(group);
		
		Label lblTitle = new Label(group, SWT.NONE);
		lblTitle.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblTitle.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblTitle.setText("Title:");
		
		titleText = new Text(group, SWT.BORDER);
		titleText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblSubtitle = new Label(group, SWT.NONE);
		lblSubtitle.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblSubtitle.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSubtitle.setText("Subtitle:");
		
		subtitleText = new Text(group, SWT.BORDER);
		subtitleText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblCreator = new Label(group, SWT.NONE);
		lblCreator.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblCreator.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblCreator.setText("Creator:");
		
		creatorText = new Text(group, SWT.BORDER);
		creatorText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblPublisher = new Label(group, SWT.NONE);
		lblPublisher.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblPublisher.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPublisher.setText("Publisher:");
		
		publisherText = new Text(group, SWT.BORDER);
		publisherText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblContributor = new Label(group, SWT.NONE);
		lblContributor.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblContributor.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblContributor.setText("Contributor:");
		
		contributorText = new Text(group, SWT.BORDER);
		contributorText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblPublicationdate = new Label(group, SWT.NONE);
		lblPublicationdate.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblPublicationdate.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPublicationdate.setText("PublicationDate:");
		
		PublicationDateText = new Text(group, SWT.BORDER);
		PublicationDateText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblLanguage = new Label(group, SWT.NONE);
		lblLanguage.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblLanguage.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblLanguage.setText("Language");
		
		languageText = new Text(group, SWT.BORDER);
		languageText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		{
			TabItem tbtmStudyUnit = new TabItem(tabFolder, SWT.NONE);
			tbtmStudyUnit.setText("Study Proposal");
			{
				Group grpStudyUnit = new Group(tabFolder, SWT.NONE);
				grpStudyUnit.setText("Study Proposal");
				grpStudyUnit.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
				tbtmStudyUnit.setControl(grpStudyUnit);
				GridLayout gridLayout = new GridLayout();
				gridLayout.numColumns = 2;
				grpStudyUnit.setLayout(gridLayout);
				
				// Name:
				{
					Label nameLabel = new Label(grpStudyUnit, SWT.NONE);
					nameLabel.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
					nameLabel.setBounds(0, 0, 65, 17);
					nameLabel.setText("Name");
				}
				{
					Text nameText = new Text(grpStudyUnit, SWT.BORDER);
					nameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
					nameText = nameText;
					nameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
					nameText.setBounds(0, 0, 75, 27);
				}
				
				// Citation (Title and Copyright):
				{
//					Group citationGroup = new Group(group, SWT.NONE);
//					citationGroup.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
//					tbtmStudyUnit.setControl(citationGroup);
//					GridLayout citationGridLayout = new GridLayout();
//					citationGridLayout.numColumns = 2;
//					group.setLayout(citationGridLayout);

					final Label citationLabel = new Label(grpStudyUnit, SWT.NONE);
					final GridData gd_citationLabel = new GridData(SWT.RIGHT, SWT.TOP, false, false);
					gd_citationLabel.horizontalIndent = 5;
					citationLabel.setLayoutData(gd_citationLabel);
					citationLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
					citationLabel.setText("Citation"); //$NON-NLS-1$

					citationStyledText = new StyledText(grpStudyUnit, SWT.WRAP | SWT.V_SCROLL | SWT.BORDER);
					citationStyledText.setText(studyUnit.getAttribute("Citation"));
					final GridData gd_originalStudyUnitTextStyledText = new GridData(SWT.FILL, SWT.CENTER, true, false);
					gd_originalStudyUnitTextStyledText.heightHint = 154;
					gd_originalStudyUnitTextStyledText.widthHint = 308;
					citationStyledText.setLayoutData(gd_originalStudyUnitTextStyledText);
					citationStyledText.addModifyListener(new ModifyListener() {
						public void modifyText(final ModifyEvent e) {
							log.debug("Description changed");
//							studyUnit.setDescr(citationStyledText.getText());
							editorStatus.setChanged();
						}
					});
					
				}
				
				// Abstract:
				{
					final Label abstractLabel = new Label(grpStudyUnit, SWT.NONE);
					final GridData gd_citationLabel = new GridData(SWT.RIGHT, SWT.TOP, false, false);
					gd_citationLabel.horizontalIndent = 5;
					abstractLabel.setLayoutData(gd_citationLabel);
					abstractLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
					abstractLabel.setText("Abstract"); //$NON-NLS-1$

					abstractStyledText = new StyledText(grpStudyUnit, SWT.WRAP | SWT.V_SCROLL | SWT.BORDER);
					abstractStyledText.setText(studyUnit.getAttribute("Abstract"));
					final GridData gd_originalStudyUnitTextStyledText = new GridData(SWT.FILL, SWT.CENTER, true, false);
					gd_originalStudyUnitTextStyledText.heightHint = 154;
					gd_originalStudyUnitTextStyledText.widthHint = 308;
					abstractStyledText.setLayoutData(gd_originalStudyUnitTextStyledText);
					abstractStyledText.addModifyListener(new ModifyListener() {
						public void modifyText(final ModifyEvent e) {
							log.debug("Description changed");
//							studyUnit.setDescr(citationStyledText.getText());
							editorStatus.setChanged();
						}
					});

				}

				{
					Label lblName = new Label(grpStudyUnit, SWT.NONE);
					lblName.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
					lblName.setBounds(0, 0, 65, 17);
					lblName.setText("Name");
				}
				{
					nameText = new Text(grpStudyUnit, SWT.BORDER);
					nameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
					nameText.setBounds(0, 0, 75, 27);
				}
				
				TabItem tbtmStudyFunding = new TabItem(tabFolder, SWT.NONE);
				tbtmStudyFunding.setText("Study Funding");
				
				Composite composite = new Composite(tabFolder, SWT.NONE);
				GridLayout gridLayout_1 = new GridLayout();
				gridLayout_1.numColumns = 2;
				composite.setLayout(gridLayout_1);
				tbtmStudyFunding.setControl(composite);
				
				Label lblAgency = new Label(composite, SWT.NONE);
				lblAgency.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
				lblAgency.setText("Agency");
				
				agencyText = new Text(composite, SWT.BORDER);
				agencyText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

				super.createPropertiesTab(tabFolder);
				
				// Clean dirt from initialization
				editorStatus.clearChanged();

			}
		}
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

		if (editorInput.getEditorMode().equals(EDITOR_MODE_TYPE.NEW)) {
			try {
				studyUnit = StudyUnits.createStudyUnit(editorInput.getId(), editorInput.getVersion(),
						editorInput.getParentId(), editorInput.getParentVersion(), null);
			} catch (Exception e) {
				log.error("StudyUnitEditor.init(): " + e.getMessage());
				String errMess = Messages.getString("StudyUnitEditor.mess.ErrorDuringCreateNewStudyUnit"); //$NON-NLS-1$
				ErrorDialog.openError(site.getShell(), Messages.getString("ErrorTitle"), null, new Status(IStatus.ERROR,
						ID, 0, errMess, e));
				System.exit(0);
			}
		} else if (editorInput.getEditorMode().equals(EDITOR_MODE_TYPE.EDIT)
				|| editorInput.getEditorMode().equals(EDITOR_MODE_TYPE.VIEW)) {
			try {
				studyUnit = StudyUnits.getStudyUnit(editorInput.getId(), editorInput.getVersion(),
						editorInput.getParentId(), editorInput.getParentVersion());
			} catch (Exception e) {
				String errMess = Messages.getString("StudyUnitEditor.mess.GetStudyUnitByIdError"); //$NON-NLS-1$
				ErrorDialog.openError(site.getShell(), Messages.getString("ErrorTitle"), null, new Status(IStatus.ERROR,
						ID, 0, errMess, e));
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
