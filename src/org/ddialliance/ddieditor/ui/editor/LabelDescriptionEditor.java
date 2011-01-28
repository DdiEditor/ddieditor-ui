package org.ddialliance.ddieditor.ui.editor;

import org.ddialliance.ddieditor.ui.model.ILabelDescription;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

/**
 * Generic editor supporting label and description editing on an element
 */
public class LabelDescriptionEditor extends Editor {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			LabelDescriptionEditor.class);

	private String editorEntityName;
	private ILabelDescription simpleElement;
	private TableViewer tableViewer;
	
	public LabelDescriptionEditor(String headerEditorTitle,
			String headerEditorDescr, String editorEntityName, String editorID) {
		super(headerEditorTitle, headerEditorDescr, editorID);
		this.editorEntityName = editorEntityName;
		this.ID = editorID;
	}

	/**
	 * Create contents of the editor part
	 * 
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout());
		super.createPartControl(parent);

		createTabFolder(getComposite_1());
		createLabelDescriptionTab(getTabFolder(), editorEntityName,
				simpleElement);
		createPropertiesTab(getTabFolder());
		createXmlTab(simpleElement);
		// Clean dirt from initialization
		editorStatus.clearChanged();
	}

	public Viewer getViewer() {
		return this.tableViewer;
	}

	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
		this.simpleElement = (ILabelDescription) model;
	}
}
