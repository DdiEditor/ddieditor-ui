package org.ddialliance.ddieditor.ui.editor;

import java.text.MessageFormat;
import java.util.List;

import org.ddialliance.ddieditor.ui.ConceptsPerspective;
import org.ddialliance.ddieditor.ui.dbxml.ConceptSchemes;
import org.ddialliance.ddieditor.ui.editor.EditorInput.EDITOR_MODE_TYPE;
import org.ddialliance.ddieditor.ui.editor.concept.ConceptSchemeEditor;
import org.ddialliance.ddieditor.ui.model.ConceptScheme;
import org.ddialliance.ddieditor.ui.model.Simple;
import org.ddialliance.ddieditor.ui.view.Messages;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

public class SimpleEditor extends Editor {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, ConceptSchemeEditor.class);

	// Member variables:
	private Simple simpleElement;
	private IEditorSite site;
	private StyledText simpleDescrStyledText;
	private TableViewer tableViewer;

	private enum TABITEM_INDEX {
		SIMPLE
	};

	
	/**
	 * Create contents of the editor part
	 * 
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		log.debug("Editor.createPartControl called");
	}

	
	/**
	 * Create Simple content of Editor Part i.e. Label and Description.
	 * 
	 * @param parent
	 * @param headerEditorTitle
	 * @param headerEditorDescr
	 * @param editorEntiryName
	 * 			- Entity Name e.g. Question Scheme
	 * @param nbrTabItems
	 * 			- requested number of Tab Items
	 */
	public void createSimplePartControl(Composite parent, String headerEditorTitle, String headerEditorDescr, String editorEntiryName, 
			int nbrTabItems, final Simple simpleElement) {
		log.debug("SimpleEditor.createSimplePartControl called");
		
		this.simpleElement = simpleElement;

		List<TabItem> tabItemList = super.createStandardPartControl(parent, headerEditorTitle, headerEditorDescr,
				TABITEM_INDEX.values().length);

		// Simple Tab:
		// -----------

		// - Simple Root Composite:
		final Composite simpleRootComposite = new Composite(tabFolder, SWT.NONE);
		simpleRootComposite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		final GridLayout gridLayout = new GridLayout();
		simpleRootComposite.setLayout(gridLayout);
		TabItem simpleTabItem = tabItemList.get(TABITEM_INDEX.SIMPLE.ordinal());
		simpleTabItem.setControl(simpleRootComposite);
		simpleTabItem.setText(editorEntiryName);

		// - Simple Group
		final Group simpleGroup = new Group(simpleRootComposite, SWT.NONE);
		final GridData gd_simpleGroup = new GridData(SWT.FILL, SWT.CENTER, true, true);
		gd_simpleGroup.heightHint = 632;
		gd_simpleGroup.widthHint = 861;
		simpleGroup.setLayoutData(gd_simpleGroup);
		simpleGroup.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		final GridLayout gridLayout_1 = new GridLayout();
		gridLayout_1.numColumns = 2;
		simpleGroup.setLayout(gridLayout_1);
		simpleGroup.setText(editorEntiryName);

		// Simple Label:
		final Label labelLabel = new Label(simpleGroup, SWT.NONE);
		final GridData gd_conceptLabel = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
		gd_conceptLabel.horizontalIndent = 5;
		labelLabel.setLayoutData(gd_conceptLabel);
		labelLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		labelLabel.setText(Messages.getString("SimpleEditor.label.Label")); //$NON-NLS-1$

		final Text labelText = new Text(simpleGroup, SWT.BORDER);
		final GridData gd_labelText = new GridData(SWT.FILL, SWT.CENTER, true, false);
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
		final Label simpleDescrLabel = new Label(simpleGroup, SWT.NONE);
		final GridData gd_simpleDescrLabel = new GridData(SWT.RIGHT, SWT.TOP, false, false);
		gd_simpleDescrLabel.horizontalIndent = 5;
		simpleDescrLabel.setLayoutData(gd_simpleDescrLabel);
		simpleDescrLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		simpleDescrLabel.setText(Messages.getString("SimpleEditor.label.DescriptionText.Label")); //$NON-NLS-1$

		simpleDescrStyledText = new StyledText(simpleGroup, SWT.WRAP | SWT.V_SCROLL | SWT.BORDER);
		simpleDescrStyledText.setText(simpleElement.getDescr());
		final GridData gd_originalConceptTextStyledText = new GridData(SWT.FILL, SWT.CENTER, true, false);
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

		// Clean dirt from initialization
		editorStatus.clearChanged();
	}

	public Viewer getViewer() {
		log.debug("SimpleEditor.getViewer()");
		return this.tableViewer;
	}

//	@Override
//	public void doSave(IProgressMonitor monitor) {
//		log.debug("ConceptSchemeEditor.doSave()");
//		super.doSave(monitor);
//
//		try {
//			simpleElement.validate();
//		} catch (Exception e1) {
//			String errMess = Messages.getString("ConceptSchemeEditor.mess.ValidationError"); //$NON-NLS-1$
//			ErrorDialog.openError(site.getShell(), Messages.getString("ErrorTitle"), null, new Status(IStatus.ERROR,
//					ID, 0, errMess, null));
//			return;
//		}
//		try {
//			if (editorInput.getEditorMode().equals(EDITOR_MODE_TYPE.NEW)) {
//				ConceptSchemes.create(conceptScheme);
//				editorInput.setEditorMode(EDITOR_MODE_TYPE.EDIT);
//			} else if (editorInput.getEditorMode().equals(EDITOR_MODE_TYPE.EDIT)) {
//				ConceptSchemes.update(conceptScheme);
//			} else if (editorInput.getEditorMode().equals(EDITOR_MODE_TYPE.VIEW)) {
//				log.debug("*** Saved ignored! ***");
//			}
//		} catch (Exception e) {
//			String errMess = Messages.getString("ConceptSchemeEditor.mess.ErrorDuringSave"); //$NON-NLS-1$
//			ErrorDialog.openError(site.getShell(), Messages.getString("ErrorTitle"), null, new Status(IStatus.ERROR,
//					ID, 0, errMess, e));
//			return;
//		}
//		editorInput.getParentView().refreshView();
//		editorStatus.clearChanged();
//		log.debug("ConceptSchemeEditor.doSave(1): " + editorStatus.getStatus());
//	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		// Initialize the Stander Editor Part:
		super.init(site, input);
//		// Initialize Simple Editor Part:
//		this.editorInput = (EditorInput) input;
//		if (log.isDebugEnabled()) {
//			log.debug("ConceptSchemeEditor.init() - Name: " + editorInput.getName());
//			log.debug("ConceptSchemeEditor.init() - ID: " + editorInput.getId());
//			log.debug("ConceptSchemeEditor.init() - Parent ID: " + editorInput.getParentId());
//			log.debug("ConceptSchemeEditor.init() - Editor Mode: " + editorInput.getEditorMode());
//		}
//
//		ConceptSchemes.init(((EditorInput) input).getProperties());
//
//		if (editorInput.getEditorMode().equals(EDITOR_MODE_TYPE.NEW)) {
//			try {
//				conceptScheme = ConceptSchemes.createConceptScheme(editorInput.getId(), editorInput.getVersion(),
//						editorInput.getParentId(), editorInput.getParentVersion());
//			} catch (Exception e) {
//				log.error("ConceptSchemeEditor.init(): " + e.getMessage());
//				String errMess = Messages.getString("ConceptSchemeEditor.mess.ErrorDuringCreateNewConceptScheme"); //$NON-NLS-1$
//				ErrorDialog.openError(site.getShell(), Messages.getString("ErrorTitle"), null, new Status(IStatus.ERROR,
//						ID, 0, errMess, e));
//				System.exit(0);
//			}
//		} else if (editorInput.getEditorMode().equals(EDITOR_MODE_TYPE.EDIT)
//				|| editorInput.getEditorMode().equals(EDITOR_MODE_TYPE.VIEW)) {
//			try {
//				conceptScheme = ConceptSchemes.getConceptScheme(editorInput.getId(), editorInput.getVersion(),
//						editorInput.getParentId(), editorInput.getParentVersion());
//			} catch (Exception e) {
//				String errMess = Messages.getString("ConceptSchemeEditor.mess.GetConceptSchemeByIdError"); //$NON-NLS-1$
//				ErrorDialog.openError(site.getShell(), Messages.getString("ErrorTitle"), null, new Status(IStatus.ERROR,
//						ID, 0, errMess, e));
//				System.exit(0);
//			}
//		} else {
//			String errMess = MessageFormat.format(
//					Messages.getString("ConceptSchemeEditor.mess.UnknownEditorMode"), editorInput.getEditorMode()); //$NON-NLS-1$
//			MessageDialog.openError(site.getShell(), Messages.getString("ErrorTitle"), errMess);
//			System.exit(0);
//		}
//
//		this.site = site;
//		setSite(site);
//		setInput(editorInput);
//		setPartName(editorInput.getId());
	}

}
