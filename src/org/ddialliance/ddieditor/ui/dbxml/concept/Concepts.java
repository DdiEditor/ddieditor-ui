package org.ddialliance.ddieditor.ui.dbxml.concept;

import java.io.File;
import java.util.List;

import org.ddialliance.ddi3.xml.xmlbeans.conceptualcomponent.ConceptDocument;
import org.ddialliance.ddi3.xml.xmlbeans.conceptualcomponent.ConceptType;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.persistenceaccess.PersistenceManager;
import org.ddialliance.ddieditor.ui.dbxml.DbXml;
import org.ddialliance.ddieditor.ui.dbxml.XmlEntities;
import org.ddialliance.ddieditor.ui.model.concept.Concept;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;

/**
 * Concepts (DBXML).
 * 
 */
/*
 * $Author$ 
 * $Date$ 
 * $Revision$
 */

public class Concepts extends XmlEntities {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, Concepts.class);

	/**
	 * Get Concepts - light version
	 * 
	 * @param parentConceptScheme
	 * @return List<LightXmlObjectType>
	 * @throws Exception
	 */
	static public List<LightXmlObjectType> getConceptsLight(LightXmlObjectType parentConceptScheme) throws Exception {

		log.debug("Concepts.getConceptsLight()");

		return Concepts.getConceptsLight("", "", parentConceptScheme.getId(), parentConceptScheme.getVersion());
	}

	/**
	 * 
	 * Get Light version of Concepts
	 * 
	 * @param id
	 * @param version
	 * @param parentId
	 * @param parentVersion
	 * @return List<LightXmlObjectType>
	 * @throws Exception
	 */
	static public List<LightXmlObjectType> getConceptsLight(String id, String version, String parentId,
			String parentVersion) throws Exception {

		log.debug("Concepts.getConceptsLight()");

		List<LightXmlObjectType> lightXmlObjectTypeList = DdiManager.getInstance().getConceptsLight(id, version,
				parentId, parentVersion).getLightXmlObjectList().getLightXmlObjectList();

		return lightXmlObjectTypeList;
	}
	
	/**
	 * Create Concept object
	 * 
	 * @param id
	 * @param version
	 * @param parentId
	 * @param parentVersion
	 * @return Concept
	 * @throws Exception
	 */
	static public Concept createConcept(String id, String version, String parentId, String parentVersion)
			throws Exception {
		log.debug("Concept.createConcept()");

		ConceptDocument conceptDocument = ConceptDocument.Factory.newInstance();

		ConceptType conceptType = conceptDocument.addNewConcept();
		conceptType.setId(id);
		if (version != null) {
			conceptType.setVersion(version);
		}
		
		Concept concept = new Concept(conceptDocument, parentId, parentVersion);

		return concept;
	}

	/**
	 * 
	 * Get Concept
	 * 
	 * @param id
	 * @param version
	 * @param parentId
	 * @param parentVersion
	 * @return Concept
	 * @throws Exception
	 */
	static public Concept getConcept(String id, String version, String parentId, String parentVersion) throws Exception {
		log.debug("Concepts.getConcept("+id+")");

		ConceptDocument conceptDocument = DdiManager.getInstance().getConcept(id, version, parentId, parentVersion);
		Concept concept = new Concept(conceptDocument, parentId, parentVersion);
		
		return concept;
	}

	/**
	 * Create new DBXML Concept
	 * 
	 * @param concept
	 *            concept instance
	 * @param parentId
	 *            Id. of Concept Scheme
	 * @param parentVersion
	 *            Version of Concept Scheme
	 * @throws DDIFtpException
	 */
	static public void create(Concept concept) throws DDIFtpException {
		try {
			DdiManager.getInstance().createElement(concept.getConceptDocument(),
					concept.getParentId(), concept.getParentVersion(), "ConceptScheme");
		} catch (DDIFtpException e) {
			log.error("Create DBXML Concept error: " + e.getMessage());
			throw e;
		}
		
		// TODO When is xml-file updated - when object saved?
		if (xml_export_filename.length() > 0) {
			File outFile = new File("resources" + File.separator + xml_export_filename);
			PersistenceManager.getInstance().exportResoure(DbXml.FULLY_DECLARED_NS_DOC, outFile);
		}
	}

	/**
	 * 
	 * Update DBXML Concept corresponding to the given Concept instance
	 * 
	 * @param concept
	 *            Concept instance
	 * @throws DDIFtpException
	 */
	static public void update(Concept concept) throws DDIFtpException {
		// TODO Version Control - not supported
		log.debug("Update DBXML Concept:\n" + concept.getConceptDocument());
		try {
			DdiManager.getInstance().updateElement(concept.getConceptDocument(), concept.getId(),
					concept.getVersion());
		} catch (DDIFtpException e) {
			log.error("Update DBXML Concept error: " + e.getMessage());
			throw e;
		}
		
		// TODO When is xml-file updated - when object saved?
		if (xml_export_filename.length() > 0) {
			File outFile = new File("resources" + File.separator + xml_export_filename);
			PersistenceManager.getInstance().exportResoure(DbXml.FULLY_DECLARED_NS_DOC, outFile);
		}
	}

	/**
	 * 
	 * Delete DBXML Concept
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
		log.debug("Delete DBXML Concept Scheme");
		Concept concept = getConcept(id, version, parentId, parentVersion);
		try {
			DdiManager.getInstance().deleteElement(concept.getConceptDocument(), concept.getParentId(),
					concept.getParentVersion(), "ConceptScheme");
		} catch (DDIFtpException e) {
			log.error("Delete DBXML Concept Scheme error: " + e.getMessage());
			throw e;
		}
		
		// TODO When is xml-file updated - when object saved?
		if (xml_export_filename.length() > 0) {
			File outFile = new File("resources" + File.separator + xml_export_filename);
			PersistenceManager.getInstance().exportResoure(DbXml.FULLY_DECLARED_NS_DOC, outFile);
		}
	}
}
