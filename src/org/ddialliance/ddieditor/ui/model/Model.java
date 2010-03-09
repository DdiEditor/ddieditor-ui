package org.ddialliance.ddieditor.ui.model;

import org.apache.xmlbeans.XmlObject;
import org.ddialliance.ddiftp.util.xml.XmlBeansUtil;

/**
 * Model. - provides 'get' and 'set' methods for accessing Id and Version.
 * 
 */
public abstract class Model implements IModel {

	private String id;
	private String version;
	private String parentId;
	private String parentVersion;
	private String agency;
	protected boolean create = false;

	/**
	 * Constructor
	 * 
	 * @param id
	 * @param version
	 * @param parentId
	 * @param parentVersion
	 */
	public Model(String id, String version, String parentId,
			String parentVersion, String agency) {

		this.id = id;
		this.version = version;
		this.parentId = parentId;
		this.parentVersion = parentVersion;
		this.agency = agency;
	}

	public Model(XmlObject xmlObject, String parentId, String parentVersion) {
		id = XmlBeansUtil.getXmlAttributeValue(xmlObject.xmlText(), "id=\"");
		version = XmlBeansUtil.getXmlAttributeValue(xmlObject.xmlText(),
				"version=\"");
		this.agency = XmlBeansUtil.getXmlAttributeValue(xmlObject.xmlText(),
				"agency=\"");
		this.parentId = parentId;
		this.parentVersion = parentVersion;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ddialliance.ddieditor.ui.model.IModel#getId()
	 */
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ddialliance.ddieditor.ui.model.IModel#getVersion()
	 */
	public String getVersion() {
		return version;
	}

	public String getAgency() {
		return agency;
	}

	public void setAgency(String agency) {
		this.agency = agency;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.ddialliance.ddieditor.ui.model.IModel#setParentId(java.lang.String)
	 */
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ddialliance.ddieditor.ui.model.IModel#getParentId()
	 */
	public String getParentId() {
		return this.parentId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.ddialliance.ddieditor.ui.model.IModel#setParentVersion(java.lang.
	 * String)
	 */
	public void setParentVersion(String parentVersion) {
		this.parentVersion = parentVersion;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ddialliance.ddieditor.ui.model.IModel#getParentVersion()
	 */
	public String getParentVersion() {
		return this.parentVersion;
	}

	@Override
	public void validate() throws Exception {
		// No error found:
		return;
	}

	public void applyChange(Object value, Class<?> type) throws Exception {
		this.create = true;
		executeChange(value, type);
		this.create = false;
	}

	public abstract void executeChange(Object value, Class<?> type)
			throws Exception;
}
