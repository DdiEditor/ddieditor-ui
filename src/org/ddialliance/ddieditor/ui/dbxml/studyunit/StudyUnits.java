package org.ddialliance.ddieditor.ui.dbxml.studyunit;

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
import java.util.ArrayList;
import java.util.List;

import org.ddialliance.ddi_3_0.xml.xmlbeans.studyunit.StudyUnitDocument;
import org.ddialliance.ddi_3_0.xml.xmlbeans.studyunit.StudyUnitType;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectListDocument;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectListType;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.persistenceaccess.PersistenceManager;
import org.ddialliance.ddieditor.persistenceaccess.maintainablelabel.MaintainableLabelQueryResult;
import org.ddialliance.ddieditor.persistenceaccess.maintainablelabel.MaintainableLabelUpdateElement;
import org.ddialliance.ddieditor.ui.dbxml.DbXml;
import org.ddialliance.ddieditor.ui.dbxml.XmlEntities;
import org.ddialliance.ddieditor.ui.model.concept.ConceptScheme;
import org.ddialliance.ddieditor.ui.model.studyunit.StudyUnit;
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
		MaintainableLabelQueryResult studyUnitResult = DdiManager.getInstance().getStudyLabel(id, null, parentId, null);
		
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
			MaintainableLabelQueryResult maintainableLabelQueryResult)			throws Exception {
		
		log.debug("StudyUnit.createStudyUnit()");

//		StudyUnitDocument studyUnitDocument = StudyUnitDocument.Factory.newInstance();
//
//		StudyUnitType studyUnitType = studyUnitDocument.addNewStudyUnit();
//		studyUnitType.setId(id);
//		if (version != null) {
//			studyUnitType.setVersion(version);
//		}
		
//		StudyUnit studyUnit = new StudyUnit(studyUnitDocument, parentId, parentVersion, maintainableLabelQueryResult);
		
		StudyUnit studyUnit = new StudyUnit(id, version, parentId, parentVersion, maintainableLabelQueryResult);
		
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

		MaintainableLabelQueryResult maintainableLabelQueryResult = DdiManager.getInstance().getStudyLabel(id, version,
				parentId, parentVersion);
		
		StudyUnit studyUnit = createStudyUnit(id, version, parentId, parentVersion, maintainableLabelQueryResult);
		
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
//		try {
//			DdiManager.getInstance().createElement(studyUnit.getStudyUnitDocument(),
//					studyUnit.getParentId(), studyUnit.getParentVersion(), "DDIInstance");
//		} catch (DDIFtpException e) {
//			log.error("Create DBXML Study Unit error: " + e.getMessage());
//			
//			throw new DDIFtpException(e.getMessage());
//		}
//		
//		// TODO When is xml-file updated - when object saved?
//		if (xml_export_filename.length() > 0) {
//			File outFile = new File("resources" + File.separator + xml_export_filename);
//			PersistenceManager.getInstance().exportResoure(DbXml.FULLY_DECLARED_NS_DOC, outFile);
//		}
		
		List<MaintainableLabelUpdateElement> elements = new ArrayList<MaintainableLabelUpdateElement>();
		MaintainableLabelUpdateElement update = new MaintainableLabelUpdateElement();
		MaintainableLabelQueryResult result = new MaintainableLabelQueryResult();

		elements.clear();
		update.setCrudValue(0);
//		update.setValue();
		elements.add(update);
		DdiManager.getInstance().updateMaintainableLabel(result, elements);
		studyUnit.setStudyUnitQueryResult(result);

		// TODO When is xml-file updated - when object saved?
		if (xml_export_filename.length() > 0) {
			File outFile = new File("resources" + File.separator + xml_export_filename);
			PersistenceManager.getInstance().exportResoure(DbXml.FULLY_DECLARED_NS_DOC, outFile);
		}
		studyUnit.clearChanged();
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
		MaintainableLabelQueryResult studyUnitQueryResult = studyUnit.getStudyUnitQueryResult();
//		studyUnitQueryResult.cleanElements();
		DdiManager.getInstance().updateMaintainableLabel(studyUnitQueryResult, studyUnit.getUpdateElements());
		
		// TODO When is xml-file updated - when object saved?
		if (xml_export_filename.length() > 0) {
			File outFile = new File("resources" + File.separator + xml_export_filename);
			PersistenceManager.getInstance().exportResoure(DbXml.FULLY_DECLARED_NS_DOC, outFile);
		}
		studyUnit.clearChanged();
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
//		try {
//			DdiManager.getInstance().deleteElement(studyUnit.getStudyUnitDocument(), studyUnit.getParentId(),
//					studyUnit.getParentVersion(), "DDIInstance");
//		} catch (DDIFtpException e) {
//			log.error("Delete DBXML Study Unit error: " + e.getMessage());
//			
//			throw new DDIFtpException(e.getMessage());
//		}
		
		// TODO When is xml-file updated - when object saved?
		if (xml_export_filename.length() > 0) {
			File outFile = new File("resources" + File.separator + xml_export_filename);
			PersistenceManager.getInstance().exportResoure(DbXml.FULLY_DECLARED_NS_DOC, outFile);
		}
	}
}
