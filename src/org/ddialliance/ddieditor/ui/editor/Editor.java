package org.ddialliance.ddieditor.ui.editor;

import java.util.ArrayList;
import java.util.List;

import org.ddialliance.ddieditor.ui.IAddAttr;
import org.ddialliance.ddieditor.ui.view.Messages;
import org.ddialliance.ddieditor.ui.view.QuestionItemView;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
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
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import com.swtdesigner.SWTResourceManager;

public class Editor extends EditorPart {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, Editor.class);

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
	protected TabFolder tabFolder;
	private Text urnText;
	private Text idText;
	public static final String ID = "org.ddialliance.ddieditor.ui.editor.Editor";
	public EditorStatus editorStatus = new EditorStatus();
	private IEditorSite site;
	protected EditorInput editorInput;
	
	public static enum FIELD_TYPE {
		DIGIT, LETTER, LETTER_DIGIT
	};

	// Verify field data and report eventually errors
	public static void verifyField(FIELD_TYPE ft, VerifyEvent e, IEditorSite currentSite) {
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
				bars.getStatusLineManager().setMessage(Messages.getString("Editor.mess.UseOnlyDigits")); //$NON-NLS-1$
			}
			break;
		case LETTER:
			// Allow letters and backspace
			if (Character.isLetter(myChar) || myChar == '\b') {
				e.doit = true;
				bars.getStatusLineManager().setMessage("");
			} else {
				bars.getStatusLineManager().setMessage(Messages.getString("Editor.mess.UseOnlyLetters")); //$NON-NLS-1$
			}
			break;
		case LETTER_DIGIT:
			// Allow letters and/or digits and backspace
			if (Character.isLetterOrDigit(myChar) || myChar == '\b') {
				e.doit = true;
				bars.getStatusLineManager().setMessage("");
			} else {
				bars.getStatusLineManager().setMessage(Messages.getString("Editor.mess.UseOnlyLettersDigits")); //$NON-NLS-1$
			}
			break;
		}
	}

	// Test only:
	private void runAddAttrExtension(Composite parent) {
		log.debug("Editor.runAddAttrExtension()");
		try {
			IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(
					"org.ddialliance.ddieditor.extensionpoints.addattr");
			for (IConfigurationElement e : config) {
				log.debug("Debug: " + e.getAttribute("new_attribute"));
				Object o = e.createExecutableExtension("class");
				if (o instanceof IAddAttr) {
					site.getShell().setText(e.getAttribute("title"));
					((IAddAttr) o).AddAttr(parent, e.getAttribute("new_attribute"));
				}
			}
		} catch (Exception ex) {
			log.debug(ex.getMessage());
			System.exit(0);
		}
	}
	
	private void createPropertiesTab(TabFolder tabFolder) {
		
		// Properties Tab:
		// ----------------
		final TabItem propertiesTabItem = new TabItem(tabFolder, SWT.NONE);
		propertiesTabItem.setText(Messages.getString("Editor.label.propertiesTabItem.Properties")); //$NON-NLS-1$

		final Group propertiesGroup = new Group(tabFolder, SWT.NONE);
		propertiesGroup.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		propertiesGroup.setText(Messages.getString("Editor.label.propertiesGroup.Properties")); //$NON-NLS-1$
		final GridLayout gridLayout_3 = new GridLayout();
		gridLayout_3.numColumns = 2;
		propertiesGroup.setLayout(gridLayout_3);
		propertiesTabItem.setControl(propertiesGroup);

		final Label idLabel = new Label(propertiesGroup, SWT.RIGHT);
		idLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		idLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		idLabel.setText(Messages.getString("Editor.label.idLabel.ID")); //$NON-NLS-1$

		idText = new Text(propertiesGroup, SWT.BORDER | SWT.READ_ONLY);
		idText.setText(Messages.getString("Editor.label.idText.UniqueIdentificationOfQuestionItem")); //$NON-NLS-1$
		final GridData gd_idText = new GridData(SWT.FILL, SWT.CENTER, true, false);
		idText.setLayoutData(gd_idText);
		idText.setText(editorInput.getId());
		idText.setEnabled(false);

		final Label action = new Label(propertiesGroup, SWT.RIGHT);
		action.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		action.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		action.setText(Messages.getString("Editor.label.action.Action")); //$NON-NLS-1$

		final Combo actionCombo = new Combo(propertiesGroup, SWT.READ_ONLY);
		actionCombo.setEnabled(false);
		final GridData gd_actionCombo = new GridData(SWT.FILL, SWT.CENTER, true, false);
		actionCombo.setLayoutData(gd_actionCombo);
		actionCombo.setItems(ACTIONS);
		actionCombo.select(0);

		final Label urnLabel = new Label(propertiesGroup, SWT.RIGHT);
		urnLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		urnLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		urnLabel.setText(Messages.getString("Editor.label.urnLabel.URN")); //$NON-NLS-1$

		urnText = new Text(propertiesGroup, SWT.READ_ONLY | SWT.BORDER);
		final GridData gd_urnText = new GridData(SWT.FILL, SWT.CENTER, true, false);
		urnText.setLayoutData(gd_urnText);

		final Label label_3 = new Label(propertiesGroup, SWT.NONE);
		label_3.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

		final Button versionalButton = new Button(propertiesGroup, SWT.CHECK);
		versionalButton.setLayoutData(new GridData());
		versionalButton.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		versionalButton.setText(Messages.getString("Editor.label.versionalButton.Versional")); //$NON-NLS-1$
		versionalButton.setSelection(true); // TODO: Get versional state
		versionalButton.setEnabled(false);

		final Label versional = new Label(propertiesGroup, SWT.NONE);
		versional.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

		final Group versionGroup = new Group(propertiesGroup, SWT.NONE);
		versionGroup.setLayoutData(new GridData(422, SWT.DEFAULT));
		versionGroup.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		versionGroup.setText(Messages.getString("Editor.label.versionGroup.Version")); //$NON-NLS-1$
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		versionGroup.setLayout(gridLayout);

		final Label versionLabel = new Label(versionGroup, SWT.NONE);
		versionLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
		versionLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		versionLabel.setText(Messages.getString("Editor.label.versionLabel.Number")); //$NON-NLS-1$

		final Text versionText = new Text(versionGroup, SWT.READ_ONLY | SWT.BORDER);
		versionText.setEditable(false);
		versionText.setText(editorInput.getVersion() == null ? "" : editorInput.getVersion());
		versionText.setEnabled(false);

		final Label versionDateLabel = new Label(versionGroup, SWT.NONE);
		versionDateLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		versionDateLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		versionDateLabel.setText(Messages.getString("Editor.label.versionDateLabel.Date")); //$NON-NLS-1$

		final DateTime dateTime = new DateTime(versionGroup, SWT.NONE);
		dateTime.setEnabled(false);
		dateTime.setLayoutData(new GridData());
		dateTime.setDate(2008, 0, 1);

		final Label responsibelLabel = new Label(versionGroup, SWT.NONE);
		responsibelLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		responsibelLabel.setText(Messages.getString("Editor.label.responsibelLabel.Responsibel")); //$NON-NLS-1$

		final Text responsibelText = new Text(versionGroup, SWT.READ_ONLY | SWT.BORDER);
		responsibelText.setEditable(false);
		responsibelText.setText(editorInput.getUser());
		responsibelText.setEnabled(false);
		final GridData gd_responsibelText = new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1);
		gd_responsibelText.widthHint = 226;
		responsibelText.setLayoutData(gd_responsibelText);

		final Label label_2 = new Label(propertiesGroup, SWT.NONE);
		label_2.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

	}

	/**
	 * Create contents of the editor part
	 * 
	 * @param parent
	 */
	public void createPartControl(Composite parent) {
		log.debug("Editor.createPartControl called");
	}
	
	
	
	/**
	 * Create standard contents of the editor part
	 * 
	 * @param parent
	 * @return List<TabItem> List of Tab Items requested by caller.
	 */
	public List<TabItem> createStandardPartControl(Composite parent, String headerEditorTitle,
			String headerEditorDescr, int nbrTabItems) {

		log.debug("Editor.createPartControl called");

		// General Editor GUI layout:
		//---------------------------
		final Composite composite_1 = new Composite(parent, SWT.BORDER);
		composite_1.setRedraw(true);
		final GridData gd_composite_1 = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd_composite_1.widthHint = 539;
		gd_composite_1.heightHint = 573;
		composite_1.setLayoutData(gd_composite_1);
		composite_1.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		composite_1.setLayout(new GridLayout());

		final Composite composite_2 = new Composite(composite_1, SWT.NONE);
		composite_2.setBackground(SWTResourceManager.getColor(230, 230, 250));
		final GridData gd_composite_2 = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd_composite_2.heightHint = 21;

		composite_2.setLayoutData(gd_composite_2);
		composite_2.setLayout(new GridLayout());

		final Label questionItemEditorLabel = new Label(composite_2, SWT.WRAP);
		questionItemEditorLabel.setFont(SWTResourceManager.getFont("Sans", 14, SWT.BOLD));
		questionItemEditorLabel.setBackground(SWTResourceManager.getColor(230, 230, 250));
		questionItemEditorLabel.setText(headerEditorTitle);

		final Label useTheEditorLabel = new Label(composite_2, SWT.WRAP);
		useTheEditorLabel.setLayoutData(new GridData(471, SWT.DEFAULT));
		useTheEditorLabel.setBackground(SWTResourceManager.getColor(230, 230, 250));
		useTheEditorLabel.setText(headerEditorDescr);

		tabFolder = new TabFolder(composite_1, SWT.BOTTOM);
		final GridData gd_tabFolder = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd_tabFolder.heightHint = 610;
		tabFolder.setLayoutData(gd_tabFolder);
		tabFolder.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		
		// Create customized editor tabs:
		
		List<TabItem> listTabItems = new ArrayList<TabItem>();
		for (int i = 0; i < nbrTabItems; i++) {
			listTabItems.add(new TabItem(tabFolder, SWT.NONE));
		}
				
		// Properties Tab:
		// --------------------------
		createPropertiesTab(tabFolder);
		
		//
		site.getPage().addSelectionListener(QuestionItemView.ID, (ISelectionListener) this);
		log.debug("Part Properties: " + getPartProperties());

		// Clean dirt from initialization
		editorStatus.clearChanged();

		return listTabItems;
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
		MessageDialog.openError(site.getShell(),
				Messages.getString("ErrorTitle"), Messages.getString("Editor.mess.SaveAsNotSupported")); //$NON-NLS-1$
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
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
}
