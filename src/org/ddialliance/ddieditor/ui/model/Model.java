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


public class Model {
	
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
	
	/**
	 * Get ID element.
	 * 
	 * @return String
	 * 			ID string.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Get version of element.
	 * 
	 * @return String
	 * 			Version string
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Set Parent ID.
	 * 
	 * @param String
	 *            Parent ID string
	 */
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	/**
	 * Get Parent ID.
	 * 
	 * @return String
	 * 			Parent ID string
	 */
	public String getParentId() {
		return this.parentId;
	}

	/**
	 * Set Parent version
	 * 
	 * @param String
	 *            Parent Version string
	 */
	public void setParentVersion(String parentVersion) {
		this.parentVersion = parentVersion;
	}

	/**
	 * Get Parent version
	 * 
	 * @return String
	 * 			Parent Version string
	 */
	public String getParentVersion() {
		return this.parentVersion;
	}

}
