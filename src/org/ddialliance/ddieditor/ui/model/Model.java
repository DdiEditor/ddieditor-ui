package org.ddialliance.ddieditor.ui.model;


/**
 * Model.
 *  - provides 'get' and 'set' methods for accessing Id and Version.
 * 
 */
/*
 * $Author$ 
 * $Date$ 
 * $Revision$
 */


public abstract class Model implements IModel {
	
	private String id;
	private String version;
	private String parentId;
	private String parentVersion;

	/**
	 * Constructor
	 * 
	 * @param id
	 * @param version
	 * @param parentId
	 * @param parentVersion
	 */
	public Model(String id, String version, String parentId, String parentVersion) {
		
		this.id = id;
		this.version = version;
		this.parentId = parentId;
		this.parentVersion = parentVersion;
	}
	
	/* (non-Javadoc)
	 * @see org.ddialliance.ddieditor.ui.model.IModel#getId()
	 */
	public String getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see org.ddialliance.ddieditor.ui.model.IModel#getVersion()
	 */
	public String getVersion() {
		return version;
	}

	/* (non-Javadoc)
	 * @see org.ddialliance.ddieditor.ui.model.IModel#setParentId(java.lang.String)
	 */
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	/* (non-Javadoc)
	 * @see org.ddialliance.ddieditor.ui.model.IModel#getParentId()
	 */
	public String getParentId() {
		return this.parentId;
	}

	/* (non-Javadoc)
	 * @see org.ddialliance.ddieditor.ui.model.IModel#setParentVersion(java.lang.String)
	 */
	public void setParentVersion(String parentVersion) {
		this.parentVersion = parentVersion;
	}

	/* (non-Javadoc)
	 * @see org.ddialliance.ddieditor.ui.model.IModel#getParentVersion()
	 */
	public String getParentVersion() {
		return this.parentVersion;
	}
	
	public void validate() throws Exception {
		// No error found:
		return;
	}
}
