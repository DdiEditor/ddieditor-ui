package org.ddialliance.ddieditor.ui.model;

import org.apache.xmlbeans.XmlObject;
import org.ddialliance.ddiftp.util.DDIFtpException;

public interface IModel {

	/**
	 * Get ID element.
	 * 
	 * @return String ID string.
	 */
	public abstract String getId();

	/**
	 * Get version of element.
	 * 
	 * @return String Version string
	 */
	public abstract String getVersion();

	public abstract String getAgency();
	
	public abstract void setAgency(String agency);
	
	/**
	 * Set Parent ID.
	 * 
	 * @param String
	 *            Parent ID string
	 */
	public abstract void setParentId(String parentId);

	/**
	 * Get Parent ID.
	 * 
	 * @return String Parent ID string
	 */
	public abstract String getParentId();

	/**
	 * Set Parent version
	 * 
	 * @param String
	 *            Parent Version string
	 */
	public abstract void setParentVersion(String parentVersion);

	/**
	 * Get Parent version
	 * 
	 * @return String Parent Version string
	 */
	public abstract String getParentVersion();

	public XmlObject getDocument() throws DDIFtpException;

	/**
	 * Validates the Code Scheme before it is saved. It e.g. checks if all
	 * mandatory attributes has been given.
	 * 
	 * @throws Exception
	 */
	public void validate() throws Exception;

	/**
	 * Apply change to the model
	 * 
	 * @param value
	 *            the change
	 * @param type
	 *            type of ddi to apply change too
	 * @throws Exception
	 */
	public void applyChange(Object value, Class<?> type) throws Exception;
}