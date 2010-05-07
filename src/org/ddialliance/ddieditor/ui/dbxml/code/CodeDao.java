package org.ddialliance.ddieditor.ui.dbxml.code;

import java.util.List;

import org.ddialliance.ddi3.xml.xmlbeans.conceptualcomponent.ConceptDocument;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.CodeDocument;
import org.ddialliance.ddieditor.logic.identification.IdentificationManager;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.dbxml.IDao;
import org.ddialliance.ddieditor.ui.model.ElementType;
import org.ddialliance.ddieditor.ui.model.IModel;
import org.ddialliance.ddieditor.ui.model.code.Code;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;

/**
 * CodeDao (DBXML).
 * 
 */
/*
 * $Author: ddadak $ $Date: 2010-04-07 11:47:28 +0200 (Wed, 07 Apr 2010) $
 * $Revision: 1086 $
 */

public class CodeDao  implements IDao {

	private static Log log = LogFactory.getLog(LogType.SYSTEM, CodeDao.class);

	/**
	 * Get Code - light version
	 * 
	 * @param parentCodeScheme
	 * @return List<LightXmlObjectType>
	 * @throws Exception
	 */
	@Override
	public List<LightXmlObjectType> getLightXmlObject(LightXmlObjectType parentCodeScheme) throws Exception {

		log.debug("CodeDao.getLightXmlObject()");

		return getLightXmlObject("", "", parentCodeScheme.getId(), parentCodeScheme.getVersion());
	}

	/**
	 * 
	 * Get Light version of Code
	 * 
	 * @param id
	 * @param version
	 * @param parentId
	 * @param parentVersion
	 * @return List<LightXmlObjectType>
	 * @throws Exception
	 */
	@Override
	public List<LightXmlObjectType> getLightXmlObject(String id, String version, String parentId, String parentVersion)
//			throws Exception {
	{
		log.debug("CodeDao.getLightXmlObject()");

		// TODO Remove comments
//		List<LightXmlObjectType> lightXmlObjectTypeList = DdiManager.getInstance().getCodeLight(id, version,
//				parentId, parentVersion).getLightXmlObjectList().getLightXmlObjectList();

//		return lightXmlObjectTypeList;
		return null;
	}

	/**
	 * Create Code object
	 * 
	 * @param id
	 * @param version
	 * @param parentId
	 * @param parentVersion
	 * @return Concept
	 * @throws Exception
	 */
	@Override
	public Code create(String id, String version, String parentId, String parentVersion) throws Exception {
		log.debug("CodeDao.create()");

		// TODO Remove comments
//		CodeDocument doc = CodeDocument.Factory.newInstance();
//		IdentificationManager.getInstance().addIdentification(doc.addNewCode(),
//				ElementType.getElementType("Concept").getIdPrefix(), null);
//		IdentificationManager.getInstance().addVersionInformation(doc.getCode(), null, null);
//		Code model = new Code(doc, parentId, parentVersion);
//		return model;
		return null;
	}

	/**
	 * 
	 * Get Code
	 * 
	 * @param id
	 * @param version
	 * @param parentId
	 * @param parentVersion
	 * @return Code
	 * @throws Exception
	 */
	@Override
	public Code getModel(String id, String version, String parentId, String parentVersion) throws Exception {
		log.debug("CodeDao.getModel(" + id + ")");

		// TODO Remove comments
//		CodeDocument codeDocument = DdiManager.getInstance().getCode(id, version, parentId, parentVersion);
//		Code model = new Code(codeDocument, parentId, parentVersion);
//
//		return model;
		return null;
	}

	/**
	 * Create new DBXML Code
	 * 
	 * @param model
	 *            Code instance
	 * @param parentId
	 *            Id. of Code Scheme
	 * @param parentVersion
	 *            Version of Code Scheme
	 * @throws DDIFtpException
	 */
	static public void create(Code model) throws DDIFtpException {
		try {
			DdiManager.getInstance().createElement(model.getDocument(), model.getParentId(),
					model.getParentVersion(), "CodeScheme");
		} catch (DDIFtpException e) {
			log.error("Create DBXML Code error: " + e.getMessage());
			throw e;
		}
	}

	/**
	 * 
	 * Update DBXML Code corresponding to the given Code instance
	 * 
	 * @param model
	 *            Concept instance
	 * @throws DDIFtpException
	 */
	static public void update(Code model) throws DDIFtpException {
		// TODO Version Control - not supported
		log.debug("Update DBXML Code:\n" + model.getDocument());
		try {
			DdiManager.getInstance().updateElement(model.getDocument(), model.getId(), model.getVersion());
		} catch (DDIFtpException e) {
			log.error("Update DBXML Code error: " + e.getMessage());
			throw e;
		}
	}

	/**
	 * 
	 * Delete DBXML Code
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
	@Override
	public void delete(String id, String version, String parentId, String parentVersion) throws Exception {
		log.debug("Delete DBXML Code");
		Code model = getModel(id, version, parentId, parentVersion);
		try {
			DdiManager.getInstance().deleteElement(model.getDocument(), model.getParentId(),
					model.getParentVersion(), "Code");
		} catch (DDIFtpException e) {
			log.error("Delete DBXML Code error: " + e.getMessage());
			throw e;
		}
	}

	@Override
	public void create(IModel model) throws DDIFtpException {
		DdiManager.getInstance().createElement(model.getDocument(), model.getParentId(), model.getParentVersion(),
				"Code");
	}

	@Override
	public void update(IModel model) throws DDIFtpException {
		DdiManager.getInstance().updateElement(model.getDocument(), model.getId(), model.getVersion());
	}
}