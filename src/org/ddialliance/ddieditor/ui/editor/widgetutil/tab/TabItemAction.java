package org.ddialliance.ddieditor.ui.editor.widgetutil.tab;

import org.ddialliance.ddiftp.util.DDIFtpException;

/**
 * Extend to define a tab item actions 
 */
public abstract class TabItemAction {
	String id;
	
	/**
	 * Constructor
	 * @param id value of data object of tab item with key Editor.TAB_ID
	 */
	public TabItemAction(String id) {
		super();
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Action to perform on tab item selection
	 * @return value
	 * @throws DDIFtpException
	 */
	public abstract Object action() throws DDIFtpException;
}
