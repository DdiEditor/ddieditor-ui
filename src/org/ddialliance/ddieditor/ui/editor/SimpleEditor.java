package org.ddialliance.ddieditor.ui.editor;

/**
 * Generic Simple Editor (support for Label and Description).
 * 
 */
/*
 * $Author$ 
 * $Date$ 
 * $Revision$
 */

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
import org.eclipse.swt.widgets.TabFolder;
import com.swtdesigner.SWTResourceManager;

public class SimpleEditor extends Editor {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, SimpleEditor.class);

	// Member variables:
	private String editorEntityName;
	private Simple simpleElement;
	private IEditorSite site;
	private StyledText simpleDescrStyledText;
	private TableViewer tableViewer;

	public SimpleEditor(String headerEditorTitle, String headerEditorDescr, String editorEntityName) {

		super(headerEditorTitle, headerEditorDescr);
		
		this.editorEntityName = editorEntityName;
	}
	
	/**
	 * Create contents of the editor part
	 * 
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout());
		log.debug("Editor.createPartControl called");
		super.createPartControl(parent);
		
		// Simple Tab Folder:
		// ------------------
		TabFolder tabFolder = new TabFolder(getComposite_1(), SWT.BOTTOM);
		tabFolder.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		// - Simple Root Composite:
		Composite simpleRootComposite = new Composite(tabFolder, SWT.NONE);
		simpleRootComposite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		final GridLayout gridLayout = new GridLayout();
		simpleRootComposite.setLayout(gridLayout);
		
		// - Simple Tab Item:
		TabItem simpleTabItem = new TabItem(tabFolder, SWT.NONE);
		simpleTabItem.setControl(simpleRootComposite);
		simpleTabItem.setText(editorEntityName);

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
		simpleGroup.setText(editorEntityName);

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
		
		super.createPropertiesTab(tabFolder);

		// Clean dirt from initialization
		editorStatus.clearChanged();
	}

	public Viewer getViewer() {
		log.debug("SimpleEditor.getViewer()");
		return this.tableViewer;
	}

//	@Override
	public void init(IEditorSite site, IEditorInput input, 	Simple simpleElement) throws PartInitException {
		this.simpleElement = simpleElement;
		// Initialize the Stander Editor Part:
		super.init(site, input);
	}

}
