package org.ddialliance.ddieditor.ui.dbxml;

import java.util.List;

import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.model.IModel;
import org.ddialliance.ddiftp.util.DDIFtpException;

public interface IDao {

	/**
	 * Get Light XML Object
	 * 
	 * @param lightXmlObject
	 * @return List<LightXmlObjectType>
	 * @throws Exception
	 */
	public abstract List<LightXmlObjectType> getLightXmlObject(
			LightXmlObjectType lightXmlObject) throws Exception;

	/**
	 * Get Light XML Object
	 * 
	 * @param id
	 * @param version
	 * @param parentId
	 * @param parentVersion
	 * @return List<LightXmlObjectType>
	 * @throws Exception
	 */
	public abstract List<LightXmlObjectType> getLightXmlObject(String id,
			String version, String parentId, String parentVersion)
			throws Exception;

	/**
	 * Get model
	 * 
	 * @param id
	 * @param version
	 * @param parentId
	 * @param parentVersion
	 * @return IModel
	 * @throws Exception
	 */
	public abstract IModel getModel(String id, String version, String parentId,
			String parentVersion) throws Exception;

	/**
	 * Create model
	 * 
	 * @param id
	 * @param version
	 * @param parentId
	 * @param parentVersion
	 * @return IModel
	 * @throws Exception
	 */
	public abstract IModel create(String id, String version, String parentId,
			String parentVersion) throws Exception;

	/**
	 * Create
	 * 
	 * @param model
	 *            model
	 * @param parentId
	 *            parent id
	 * @param parentVersion
	 *            parent version
	 * @throws DDIFtpException
	 */
	public abstract void create(IModel model) throws DDIFtpException;

	/**
	 * 
	 * Update
	 * 
	 * @param model
	 *            model
	 * @throws DDIFtpException
	 */
	public abstract void update(IModel model) throws DDIFtpException;

	/**
	 * 
	 * Delete
	 * 
	 * @param id
	 *            Identification
	 * @param version
	 *            Version
	 * @param parentId
	 *            Parent Identification
	 * @param parentVersion
	 *            Parent Version
	 * @throws Exception
	 */
	public abstract void delete(String id, String version, String parentId,
			String parentVersion) throws Exception;

}