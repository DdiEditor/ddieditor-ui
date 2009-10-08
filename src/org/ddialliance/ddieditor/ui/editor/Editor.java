package org.ddialliance.ddieditor.ui.editor;

/**
 * Generic Editor.
 * 
 */
/*
 * $Author$ 
 * $Date$ 
 * $Revision$
 */

import java.util.Calendar;
import java.util.Date;

import org.ddialliance.ddi_3_0.xml.xmlbeans.reusable.DateType;
import org.ddialliance.ddieditor.ui.IAddAttr;
import org.ddialliance.ddieditor.ui.editor.EditorInput.EditorModeType;
import org.ddialliance.ddieditor.ui.model.LabelDescription;
import org.ddialliance.ddieditor.ui.util.swtdesigner.SWTResourceManager;
import org.ddialliance.ddieditor.ui.view.Messages;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.Translator;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

/**
 * The Editor Class consist of a header with a Tabfolder. The Tabfolder contains
 * a set of TabItems. The number is configurable but minimum is one Property
 * TabItem.
 * 
 * @author ddadak
 * 
 */
public class Editor extends EditorPart {

	private static Log log = LogFactory.getLog(LogType.SYSTEM, Editor.class);

	private String headerEditorTitle = "";
	private String headerEditorDescr = "";
	private TabFolder tabFolder;
	private TabItem labelDescriptionTabItem;

	public TabItem getLabelDescriptionTabItem() {
		return labelDescriptionTabItem;
	}

	public void setLabelDescriptionTabItem(TabItem labelDescriptionTabItem) {
		this.labelDescriptionTabItem = labelDescriptionTabItem;
	}

	public TabFolder getTabFolder() {
		return tabFolder;
	}

	public void setTabFolder(TabFolder tabFolder) {
		this.tabFolder = tabFolder;
	}

	public Editor(String headerEditorTitle, String headerEditorDescr) {
		this.headerEditorTitle = headerEditorTitle;
		this.headerEditorDescr = headerEditorDescr;
	}

	/**
	 * The EditorStatus class keeps track of the Editor status.
	 */
	public class EditorStatus {
		private boolean changed = false;

		public void setChanged() {
			log.debug("EditorStatus.setChanged()");
			changed = true; // Set 'changed' before fire the property change!
			firePropertyChange(IEditorPart.PROP_DIRTY);
		}

		public boolean getStatus() {
			log.debug("EditorStatus.getStatus()");
			return changed;
		}

		public void clearChanged() {
			log.debug("EditorStatus.clearChanged()");
			changed = false; // Set 'changed' before fire the property change!
			firePropertyChange(IEditorPart.PROP_DIRTY);
		}
	}

	// TODO Define Actions via property file
	private static final String[] ACTIONS = { "", "Add", "Update", "Delete" };

	// Member variables:
	private Text urnText;
	private Text idText;
	public static final String ID = "org.ddialliance.ddieditor.ui.editor.Editor";
	public EditorStatus editorStatus = new EditorStatus();
	private IEditorSite site;
	protected EditorInput editorInput;
	private Composite composite;

	public static enum FIELD_TYPE {
		DIGIT, LETTER, LETTER_DIGIT
	};

	/**
	 * Verify field data and report eventually errors
	 * 
	 * @param ft
	 *            Field type e.g. DIGIT, LETTER, LETTER_DIGIT
	 * @param e
	 *            Verify event
	 * @param currentSite
	 *            Current Site
	 */
	public static void verifyField(FIELD_TYPE ft, VerifyEvent e,
			IEditorSite currentSite) {
		char myChar;

		// Assume we don't allow it
		e.doit = false;
		// Get the character typed
		myChar = e.character;
		log.debug("Verify char: '" + myChar + "'");

		int i = myChar;
		log.debug("Verify char(hex): " + Integer.toHexString(i));

		IActionBars bars = currentSite.getActionBars();

		switch (ft) {
		case DIGIT:
			// Allow 0-9 and backspace
			if (Character.isDigit(myChar) || myChar == 0x08) {
				e.doit = true;
				bars.getStatusLineManager().setMessage("");
			} else {
				bars.getStatusLineManager().setMessage(
						Messages.getString("Editor.mess.UseOnlyDigits")); //$NON-NLS-1$
			}
			break;
		case LETTER:
			// Allow letters and backspace
			if (Character.isLetter(myChar) || myChar == '\b') {
				e.doit = true;
				bars.getStatusLineManager().setMessage("");
			} else {
				bars.getStatusLineManager().setMessage(
						Messages.getString("Editor.mess.UseOnlyLetters")); //$NON-NLS-1$
			}
			break;
		case LETTER_DIGIT:
			// Allow letters and/or digits and backspace
			if (Character.isLetterOrDigit(myChar) || myChar == '\b') {
				e.doit = true;
				bars.getStatusLineManager().setMessage("");
			} else {
				bars.getStatusLineManager().setMessage(
						Messages.getString("Editor.mess.UseOnlyLettersDigits")); //$NON-NLS-1$
			}
			break;
		}
	}

	// Test only:
	private void runAddAttrExtension(Composite parent) {
		log.debug("Editor.runAddAttrExtension()");
		try {
			IConfigurationElement[] config = Platform
					.getExtensionRegistry()
					.getConfigurationElementsFor(
							"org.ddialliance.ddieditor.extensionpoints.addattr");
			for (IConfigurationElement e : config) {
				log.debug("Debug: " + e.getAttribute("new_attribute"));
				Object o = e.createExecutableExtension("class");
				if (o instanceof IAddAttr) {
					site.getShell().setText(e.getAttribute("title"));
					((IAddAttr) o).AddAttr(parent, e
							.getAttribute("new_attribute"));
				}
			}
		} catch (Exception ex) {
			log.debug(ex.getMessage());
			System.exit(0);
		}
	}

	public void createTabFolder(Composite parent) {
		tabFolder = new TabFolder(parent, SWT.BOTTOM);
		tabFolder.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1));
	}

	public TabItem createTabItem(String tabText) {
		TabItem tabItem = new TabItem(getTabFolder(), SWT.NONE);
		tabItem.setText(tabText);
		return tabItem;
	}

	public Group createGroup(TabItem tabItem, String groupText) {
		Group group = new Group(getTabFolder(), SWT.NONE);
		group.setText(groupText);
		group.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		tabItem.setControl(group);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		group.setLayout(gridLayout);
		return group;
	}

	public void createTextInput(Group group, String labelText, String initText,
			Text text, ModifyListener modifyListener) {
		// label
		Label softwareLabel = new Label(group, SWT.NONE);
		softwareLabel.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		softwareLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		softwareLabel.setText(labelText);

		// input
		text = new Text(group, SWT.BORDER);
		text
				.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
						1, 1));
		text.setText(initText);
		text.addModifyListener(modifyListener);
		setControl(text);
	}
	
	public void createTextAreaInput(Group group, String labelText, String initText,
			Text text, ModifyListener modifyListener) {
		final Label label = new Label(group,
				SWT.NONE);
		final GridData gd_Label = new GridData(SWT.RIGHT, SWT.TOP,
				false, false);
		//gd_simpleDescrLabel.horizontalIndent = 5;
		label.setLayoutData(gd_Label);
		label.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));
		label.setText(labelText);

		final StyledText styledText = new StyledText(
				group, SWT.WRAP | SWT.V_SCROLL | SWT.BORDER);
		styledText.setText(initText);
		final GridData gd_Text = new GridData(
				SWT.FILL, SWT.CENTER, true, false);
		gd_Text.heightHint = 154;
		gd_Text.widthHint = 308;
		styledText.setLayoutData(gd_Text);
		styledText.addModifyListener(modifyListener); 
		setControl(text);
	}
	
	public void createDateInput(Group group, String labelText, String initDate,
			DateTimeWidget dateTimeWidget, SelectionAdapter selectionAdapter) {
		Label label = new Label(group, SWT.NONE);
		label.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
				false, false, 1, 1));
		label.setText(labelText);
		
		dateTimeWidget = new DateTimeWidget(group);
		if (!initDate.equals("")) {
			try {
				// TODO Improve Date handling
				Calendar calendar = Translator.formatIso8601DateTime(initDate);
				dateTimeWidget.setSelection(calendar.getTime());
			} catch (DDIFtpException e1) {
				ErrorDialog.openError(site.getShell(), Messages
						.getString("ErrorTitle"), null, new Status(
						IStatus.ERROR, ID, 0, e1.getMessage(), e1));
			}
		}
		dateTimeWidget.addSelectionListener(selectionAdapter);
		setControl(dateTimeWidget);
	}
	
	public DateType getDate(Date date) {
		String dateTime = Translator.formatIso8601DateTime(date
				.getTime());
		DateType dateType = DateType.Factory.newInstance();
		dateType.setSimpleDate(dateTime);
		return dateType;
	}

	public void createLabelDescriptionTab(Composite parent,
			String editorEntityName, final LabelDescription simpleElement) {
		Composite simpleRootComposite = new Composite(parent, SWT.NONE);
		simpleRootComposite.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));
		final GridLayout gridLayout = new GridLayout();
		simpleRootComposite.setLayout(gridLayout);

		// - Simple Tab Item:
		labelDescriptionTabItem = new TabItem(getTabFolder(), SWT.NONE);
		labelDescriptionTabItem.setControl(simpleRootComposite);
		labelDescriptionTabItem.setText(editorEntityName);

		// - Simple Group
		final Group labelDescriptionGroup = new Group(simpleRootComposite,
				SWT.NONE);
		final GridData gd_labelDescriptionGroup = new GridData(SWT.FILL,
				SWT.CENTER, true, true);
		gd_labelDescriptionGroup.heightHint = 632;
		gd_labelDescriptionGroup.widthHint = 861;
		labelDescriptionGroup.setLayoutData(gd_labelDescriptionGroup);
		labelDescriptionGroup.setBackground(Display.getCurrent()
				.getSystemColor(SWT.COLOR_WHITE));
		final GridLayout gridLayout_1 = new GridLayout();
		gridLayout_1.numColumns = 2;
		labelDescriptionGroup.setLayout(gridLayout_1);
		labelDescriptionGroup.setText(editorEntityName);

		// Simple Label:
		final Label labelLabel = new Label(labelDescriptionGroup, SWT.NONE);
		final GridData gd_conceptLabel = new GridData(SWT.RIGHT, SWT.CENTER,
				false, false);
		gd_conceptLabel.horizontalIndent = 5;
		labelLabel.setLayoutData(gd_conceptLabel);
		labelLabel.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));
		labelLabel.setText(Messages.getString("SimpleEditor.label.Label")); //$NON-NLS-1$

		final Text labelText = new Text(labelDescriptionGroup, SWT.BORDER);
		final GridData gd_labelText = new GridData(SWT.FILL, SWT.CENTER, true,
				false);
		labelText.setLayoutData(gd_labelText);
		labelText.setText(simpleElement.getLabel());
		labelText.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				log.debug("Label changed");
				simpleElement.setLabel(labelText.getText());
				editorStatus.setChanged();
			}
		});

		// Simple Description:
		final Label simpleDescrLabel = new Label(labelDescriptionGroup,
				SWT.NONE);
		final GridData gd_simpleDescrLabel = new GridData(SWT.RIGHT, SWT.TOP,
				false, false);
		gd_simpleDescrLabel.horizontalIndent = 5;
		simpleDescrLabel.setLayoutData(gd_simpleDescrLabel);
		simpleDescrLabel.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));
		simpleDescrLabel.setText(Messages
				.getString("SimpleEditor.label.DescriptionText.Label")); //$NON-NLS-1$

		final StyledText simpleDescrStyledText = new StyledText(
				labelDescriptionGroup, SWT.WRAP | SWT.V_SCROLL | SWT.BORDER);
		simpleDescrStyledText.setText(simpleElement.getDescr());
		final GridData gd_originalConceptTextStyledText = new GridData(
				SWT.FILL, SWT.CENTER, true, false);
		gd_originalConceptTextStyledText.heightHint = 154;
		gd_originalConceptTextStyledText.widthHint = 308;
		simpleDescrStyledText.setLayoutData(gd_originalConceptTextStyledText);
		simpleDescrStyledText.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				log.debug("Description changed");
				simpleElement.setDescr(simpleDescrStyledText.getText());
				editorStatus.setChanged();
			}
		});
	}

	public void createPropertiesTab(TabFolder tabFolder) {

		// Properties Tab Item:
		// --------------------
		final TabItem propertiesTabItem = new TabItem(tabFolder, SWT.NONE);
		propertiesTabItem.setText(Messages
				.getString("Editor.label.propertiesTabItem.Properties")); //$NON-NLS-1$

		final Group propertiesGroup = new Group(tabFolder, SWT.NONE);
		propertiesGroup.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));
		propertiesGroup.setText(Messages
				.getString("Editor.label.propertiesGroup.Properties")); //$NON-NLS-1$
		final GridLayout gridLayout_3 = new GridLayout();
		gridLayout_3.numColumns = 2;
		propertiesGroup.setLayout(gridLayout_3);
		propertiesTabItem.setControl(propertiesGroup);

		final Label idLabel = new Label(propertiesGroup, SWT.RIGHT);
		idLabel
				.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		idLabel.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));
		idLabel.setText(Messages.getString("Editor.label.idLabel.ID")); //$NON-NLS-1$

		idText = new Text(propertiesGroup, SWT.BORDER | SWT.READ_ONLY);
		idText
				.setText(Messages
						.getString("Editor.label.idText.UniqueIdentificationOfQuestionItem"));
		idText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		idText.setText(editorInput.getId());
		idText.setEnabled(false);

		final Label action = new Label(propertiesGroup, SWT.RIGHT);
		action.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		action.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));
		action.setText(Messages.getString("Editor.label.action.Action")); //$NON-NLS-1$

		final Combo actionCombo = new Combo(propertiesGroup, SWT.READ_ONLY);
		actionCombo.setEnabled(false);
		actionCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));
		actionCombo.setItems(ACTIONS);
		actionCombo.select(0);

		final Label urnLabel = new Label(propertiesGroup, SWT.RIGHT);
		urnLabel
				.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		urnLabel.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));
		urnLabel.setText(Messages.getString("Editor.label.urnLabel.URN")); //$NON-NLS-1$

		urnText = new Text(propertiesGroup, SWT.READ_ONLY | SWT.BORDER);
		urnText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		final Label label_3 = new Label(propertiesGroup, SWT.NONE);
		label_3.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));

		final Button versionalButton = new Button(propertiesGroup, SWT.CHECK);
		versionalButton.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));
		versionalButton.setText(Messages
				.getString("Editor.label.versionalButton.Versional")); //$NON-NLS-1$
		versionalButton.setSelection(true); // TODO: Get versional state
		versionalButton.setEnabled(false);

		final Label versional = new Label(propertiesGroup, SWT.NONE);
		versional.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));

		final Group versionGroup = new Group(propertiesGroup, SWT.NONE);
		versionGroup.setLayoutData(new GridData(422, SWT.DEFAULT));
		versionGroup.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));
		versionGroup.setText(Messages
				.getString("Editor.label.versionGroup.Version")); //$NON-NLS-1$
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		versionGroup.setLayout(gridLayout);

		final Label versionLabel = new Label(versionGroup, SWT.NONE);
		versionLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false,
				false));
		versionLabel.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));
		versionLabel.setText(Messages
				.getString("Editor.label.versionLabel.Number")); //$NON-NLS-1$

		final Text versionText = new Text(versionGroup, SWT.READ_ONLY
				| SWT.BORDER);
		versionText.setEditable(false);
		versionText.setText(editorInput.getVersion() == null ? "" : editorInput
				.getVersion());
		versionText.setEnabled(false);

		final Label versionDateLabel = new Label(versionGroup, SWT.NONE);
		versionDateLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
				false, false));
		versionDateLabel.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));
		versionDateLabel.setText(Messages
				.getString("Editor.label.versionDateLabel.Date")); //$NON-NLS-1$

		final DateTime dateTime = new DateTime(versionGroup, SWT.NONE);
		dateTime.setEnabled(false);
		dateTime.setDate(2008, 0, 1);

		final Label responsibelLabel = new Label(versionGroup, SWT.NONE);
		responsibelLabel.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));
		responsibelLabel.setText(Messages
				.getString("Editor.label.responsibelLabel.Responsibel")); //$NON-NLS-1$

		final Text responsibelText = new Text(versionGroup, SWT.READ_ONLY
				| SWT.BORDER);
		responsibelText.setEditable(false);
		responsibelText.setText(editorInput.getUser());
		responsibelText.setEnabled(false);
		final GridData gd_responsibelText = new GridData(SWT.LEFT, SWT.CENTER,
				false, false, 3, 1);
		gd_responsibelText.widthHint = 226;
		responsibelText.setLayoutData(gd_responsibelText);

		final Label label_2 = new Label(propertiesGroup, SWT.NONE);
		label_2.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));

	}

	/**
	 * Create contents of the editor part
	 * 
	 * @param parent
	 */
	public void createPartControl(Composite parent) {
		log.debug("Editor.createPartControl called");
		parent.setLayout(new GridLayout());

		// General Editor GUI layout:
		// ---------------------------
		composite = new Composite(parent, SWT.BORDER);
		composite.setLayout(new GridLayout());
		composite.setRedraw(true);

		final GridData gd_composite_1 = new GridData(SWT.FILL, SWT.FILL, true,
				true);
		gd_composite_1.widthHint = 539;
		gd_composite_1.heightHint = 573;
		composite.setLayoutData(gd_composite_1);
		composite.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));

		final Composite composite_2 = new Composite(composite, SWT.NONE);
		composite_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		composite_2.setBackground(SWTResourceManager.getColor(230, 230, 250));
		composite_2.setLayout(new GridLayout());

		final Label questionItemEditorLabel = new Label(composite_2, SWT.WRAP);
		questionItemEditorLabel.setLayoutData(new GridData(SWT.FILL,
				SWT.CENTER, true, false, 1, 1));
		questionItemEditorLabel.setFont(SWTResourceManager.getFont("Sans", 14,
				SWT.BOLD));

		questionItemEditorLabel.setBackground(SWTResourceManager.getColor(230,
				230, 250));
		questionItemEditorLabel.setText(headerEditorTitle);

		final Label useTheEditorLabel = new Label(composite_2, SWT.WRAP);
		final GridData gd_useTheEditorLabel = new GridData(SWT.FILL,
				SWT.CENTER, true, false);
		gd_useTheEditorLabel.widthHint = 471;
		useTheEditorLabel.setLayoutData(gd_useTheEditorLabel);
		useTheEditorLabel.setBackground(SWTResourceManager.getColor(230, 230,
				250));
		useTheEditorLabel.setText(headerEditorDescr);

		// Create Properties Tab - test only:
		// - should normally be commented out!
		// --------------------------
		// createPropertiesTab(tabFolder);

		// site.getPage().addSelectionListener(QuestionItemView.ID,
		// (ISelectionListener) this);
		log.debug("Part Properties: " + getPartProperties());

		// Clean dirt from initialization
		editorStatus.clearChanged();
	}

	@Override
	public void setFocus() {
		log.debug("Editor.setFocus()");
		// Set the focus
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		log.debug("Editor.doSave()");
	}

	@Override
	public void doSaveAs() {
		log.error("Editor.doSaveAs()");
		MessageDialog
				.openError(
						site.getShell(),
						Messages.getString("ErrorTitle"), Messages.getString("Editor.mess.SaveAsNotSupported")); //$NON-NLS-1$
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		this.site = site;
	}

	@Override
	public boolean isDirty() {
		log.debug("Editor.isDirty(): " + editorStatus.getStatus());
		return editorStatus.getStatus();
	}

	@Override
	public boolean isSaveAsAllowed() {
		log.debug("Editor.isSaveAsAllowed(): False");
		// Save as not supported
		return false;
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		log.debug("Editor.selectionChanged()");

	}

	protected Composite getComposite_1() {
		return composite;
	}

	public void setControl(Control widget) {
		if (editorInput.getEditorMode().equals(EditorModeType.VIEW)) {
			widget.setEnabled(false);
		}
	}
}
