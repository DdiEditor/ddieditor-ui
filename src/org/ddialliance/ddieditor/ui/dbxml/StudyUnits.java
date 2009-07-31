package org.ddialliance.ddieditor.ui.dbxml;

/**
 * Study Units (DBXML).
 * 
 */
/*
 * $Author$ 
 * $Date$ 
 * $Revision$
 */

import java.io.File;
import java.util.List;

import org.ddialliance.ddi_3_0.xml.xmlbeans.studyunit.StudyUnitDocument;
import org.ddialliance.ddi_3_0.xml.xmlbeans.studyunit.StudyUnitType;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectListDocument;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectListType;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.persistenceaccess.PersistenceManager;
import org.ddialliance.ddieditor.persistenceaccess.SchemeQueryResult;
import org.ddialliance.ddieditor.ui.model.ConceptScheme;
import org.ddialliance.ddieditor.ui.model.StudyUnit;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;

public class StudyUnits extends XmlEntities {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, StudyUnits.class);
	

	/**
	 * Get Light Study Unit List
	 * 
	 * @return List<LightXmlObjectType>
	 * @throws Exception
	 */
	public static List<LightXmlObjectType> getStudyUnitLight() throws Exception {
		return getStudyUnitLight("", "");
	}

	/**
	 * 
	 * Get Light Study Unit List
	 * 
	 * @param id
	 * @param version
	 * @return List<LightXmlObjectType>
	 * @throws Exception
	 */
	public static List<LightXmlObjectType> getStudyUnitLight(String id, String version) throws Exception {
		log.debug("StudyUnit.getStudyUnitLight(). Id: "+id+" Version: "+version);

		DdiManager ddiManager = DdiManager.getInstance();

		LightXmlObjectListDocument listDoc = ddiManager.getStudyUnitLight(id, version, null, null);

		LightXmlObjectListType lightXmlObjectListType = listDoc.getLightXmlObjectList();

		List<LightXmlObjectType> listLightXmlObjectListType = lightXmlObjectListType.getLightXmlObjectList();

		return listLightXmlObjectListType;
	}

	/**
	 * 
	 * Get Light Concept List
	 * 
	 * - get children (Concepts) of given Study Unit
	 * 
	 * @param id
	 * @param version
	 * @return List<LightXmlObjectType>
	 * @throws Exception
	 */
	public static List<LightXmlObjectType> getConceptsLight(String id, String version) throws Exception {
		log.debug("StudyUnit.getConceptsLight(). Id: "+id+" Version: "+version);

		DdiManager ddiManager = DdiManager.getInstance();

		LightXmlObjectListDocument listDoc = ddiManager.getConceptsLight("", "", id, version);

		LightXmlObjectListType lightXmlObjectListType = listDoc.getLightXmlObjectList();

		List<LightXmlObjectType> listLightXmlObjectListType = lightXmlObjectListType.getLightXmlObjectList();

		return listLightXmlObjectListType;
	}


	/**
	 * Get Study Unit by Id
	 * 
	 * @param id
	 * @param parentId
	 * @return StudyUnitType
	 * @throws Exception
	 */
	public StudyUnit getStudyUnitById(String id, String parentId) throws Exception {
		log.debug("StudyUnit.getStudyUnitById()");
		SchemeQueryResult schemeQueryResult = DdiManager.getInstance().getStudyLabel(id, null, parentId, null);
		
		return null; // TEMP
	}

	/**
	 * Create Study Unit object
	 * 
	 * @param id
	 * @param version
	 * @param parentId
	 * @param parentVersion
	 * @param schemeQueryResult (or null)
	 * @return StudyUnit
	 * @throws Exception
	 */
	static public StudyUnit createStudyUnit(String id, String version, String parentId, String parentVersion,
			SchemeQueryResult schemeQueryResult)			throws Exception {
		log.debug("StudyUnit.createStudyUnit()");

		StudyUnitDocument studyUnitDocument = StudyUnitDocument.Factory.newInstance();

		StudyUnitType studyUnitType = studyUnitDocument.addNewStudyUnit();
		studyUnitType.setId(id);
		if (version != null) {
			studyUnitType.setVersion(version);
		}

		StudyUnit studyUnit = new StudyUnit(studyUnitDocument, parentId, parentVersion, schemeQueryResult);
		
		return studyUnit;
	}

	/**
	 * Get Study Unit
	 * 
	 * @param id
	 * @param version
	 * @param parentId
	 * @param parentVersion
	 * @return StudyUnit
	 * @throws Exception
	 */
	static public StudyUnit getStudyUnit(String id, String version, String parentId, String parentVersion)
			throws Exception {
		log.debug("StudyUnit.getStudyUnit()");

		SchemeQueryResult schemeQueryResult = DdiManager.getInstance().getStudyLabel(id, version,
				parentId, parentVersion);
		
		StudyUnit studyUnit = createStudyUnit(id, version, parentId, parentVersion, schemeQueryResult);
		
		System.out.println("Citation: "+studyUnit.getAttribute("Citation"));

		return studyUnit;
	}

	/**
	 * Create new DBXML Study Unit
	 * 
	 * @param studyUnit
	 *            Study Unit instance
	 * @param parentId
	 *            Id. of Conceptual Component
	 * @param parentVersion
	 *            Version of Conceptual Component
	 * @throws DDIFtpException
	 */
	static public void create(StudyUnit studyUnit) throws DDIFtpException {
		try {
			DdiManager.getInstance().createElement(studyUnit.getStudyUnitDocument(),
					studyUnit.getParentId(), studyUnit.getParentVersion(), "DDIInstance");
		} catch (DDIFtpException e) {
			log.error("Create DBXML Study Unit error: " + e.getMessage());
			
			throw new DDIFtpException(e.getMessage());
		}
		
		// TODO When is xml-file updated - when object saved?
		if (xml_export_filename.length() > 0) {
			File outFile = new File("resources" + File.separator + xml_export_filename);
			PersistenceManager.getInstance().exportResoure(DbXml.FULLY_DECLARED_NS_DOC, outFile);
		}
	}

	/**
	 * 
	 * Update DBXML Study Unit corresponding to the given StudyUnit
	 * instance
	 * 
	 * @param studyUnit
	 *            Study Unit instance
	 * @throws DDIFtpException
	 */
	static public void update(StudyUnit studyUnit) throws DDIFtpException {
		// TODO Version Control - not supported
		log.debug("Update DBXML Study Unit:\n" + studyUnit.getStudyUnitDocument());
		try {
			DdiManager.getInstance().updateElement(studyUnit.getStudyUnitDocument(), studyUnit.getId(),
					studyUnit.getVersion());
		} catch (DDIFtpException e) {
			log.error("Update DBXML Study Unit error: " + e.getMessage());
			
			throw new DDIFtpException(e.getMessage());
		}
		
		// TODO When is xml-file updated - when object saved?
		if (xml_export_filename.length() > 0) {
			File outFile = new File("resources" + File.separator + xml_export_filename);
			PersistenceManager.getInstance().exportResoure(DbXml.FULLY_DECLARED_NS_DOC, outFile);
		}
	}

	/**
	 * 
	 * Delete DBXML Study Unit
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
	static public void delete(String id, String version, String parentId, String parentVersion) throws Exception {
		log.debug("Delete DBXML Study Unit");
		StudyUnit studyUnit = getStudyUnit(id, version, parentId, parentVersion);
		try {
			DdiManager.getInstance().deleteElement(studyUnit.getStudyUnitDocument(), studyUnit.getParentId(),
					studyUnit.getParentVersion(), "DDIInstance");
		} catch (DDIFtpException e) {
			log.error("Delete DBXML Study Unit error: " + e.getMessage());
			
			throw new DDIFtpException(e.getMessage());
		}
		
		// TODO When is xml-file updated - when object saved?
		if (xml_export_filename.length() > 0) {
			File outFile = new File("resources" + File.separator + xml_export_filename);
			PersistenceManager.getInstance().exportResoure(DbXml.FULLY_DECLARED_NS_DOC, outFile);
		}
	}
}
