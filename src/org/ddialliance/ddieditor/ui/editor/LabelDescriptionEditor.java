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

import org.ddialliance.ddieditor.ui.model.LabelDescription;
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

public class LabelDescriptionEditor extends Editor {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			LabelDescriptionEditor.class);

	// Member variables:
	private String editorEntityName;
	private LabelDescription simpleElement;
	private IEditorSite site;
	private TableViewer tableViewer;

	public LabelDescriptionEditor(String headerEditorTitle, String headerEditorDescr,
			String editorEntityName) {

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

		createTabFolder(getComposite_1());
		createLabelDescriptionTab(getTabFolder(), editorEntityName,
				simpleElement);
		createPropertiesTab(getTabFolder());
		createXmlTab(simpleElement);
		// Clean dirt from initialization
		editorStatus.clearChanged();
	}

	public Viewer getViewer() {
		log.debug("SimpleEditor.getViewer()");
		return this.tableViewer;
	}

	// @Override
	public void init(IEditorSite site, IEditorInput input, LabelDescription simpleElement)
			throws PartInitException {
		this.simpleElement = simpleElement;
		// Initialize the Stander Editor Part:
		super.init(site, input);
	}
	
	public void getList() {
		//this.site.
	}
}
