package org.ddialliance.ddieditor.ui.dbxml.concept;

import java.io.File;
import java.util.List;

import org.ddialliance.ddi3.xml.xmlbeans.conceptualcomponent.ConceptSchemeDocument;
import org.ddialliance.ddi3.xml.xmlbeans.conceptualcomponent.ConceptSchemeType;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectListDocument;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectListType;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.persistenceaccess.PersistenceManager;
import org.ddialliance.ddieditor.ui.dbxml.DbXml;
import org.ddialliance.ddieditor.ui.dbxml.XmlEntities;
import org.ddialliance.ddieditor.ui.model.concept.ConceptScheme;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;

public class ConceptSchemes extends XmlEntities {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, ConceptSchemes.class);

	/**
	 * Get Light Concept Scheme List
	 * 
	 * @return List<LightXmlObjectType>
	 * @throws Exception
	 */
	public static List<LightXmlObjectType> getConceptSchemesLight() throws Exception {
		return getConceptSchemesLight("", "");
	}

	/**
	 * 
	 * Get Light Concept Scheme List
	 * 
	 * @param id
	 * @param version
	 * @return List<LightXmlObjectType>
	 * @throws Exception
	 */
	public static List<LightXmlObjectType> getConceptSchemesLight(String id, String version) throws Exception {
		log.debug("ConceptScheme.getConceptSchemesLight(). Id: "+id+" Version: "+version);

		DdiManager ddiManager = DdiManager.getInstance();

		LightXmlObjectListDocument listDoc = ddiManager.getConceptSchemeLight(id, version, null, null);

		LightXmlObjectListType lightXmlObjectListType = listDoc.getLightXmlObjectList();

		List<LightXmlObjectType> listLightXmlObjectListType = lightXmlObjectListType.getLightXmlObjectList();

		return listLightXmlObjectListType;
	}

	/**
	 * 
	 * Get Light Concept List
	 * 
	 * - get children (Concepts) of given Concept Scheme
	 * 
	 * @param id
	 * @param version
	 * @return List<LightXmlObjectType>
	 * @throws Exception
	 */
	public static List<LightXmlObjectType> getConceptsLight(String id, String version) throws Exception {
		log.debug("ConceptScheme.getConceptsLight(). Id: "+id+" Version: "+version);

		DdiManager ddiManager = DdiManager.getInstance();

		LightXmlObjectListDocument listDoc = ddiManager.getConceptsLight("", "", id, version);

		LightXmlObjectListType lightXmlObjectListType = listDoc.getLightXmlObjectList();

		List<LightXmlObjectType> listLightXmlObjectListType = lightXmlObjectListType.getLightXmlObjectList();

		return listLightXmlObjectListType;
	}


	/**
	 * Get Concept Scheme by Id
	 * 
	 * @param id
	 * @param parentId
	 * @return ConceptSchemeType
	 * @throws Exception
	 */
	public ConceptSchemeType getConceptSchemeById(String id, String parentId) throws Exception {
		log.debug("ConceptScheme.getConceptSchemeById()");
		return DdiManager.getInstance().getConceptScheme(id, null, parentId, null).getConceptScheme();
	}

	/**
	 * Create Concept Scheme object
	 * 
	 * @param id
	 * @param version
	 * @param parentId
	 * @param parentVersion
	 * @return ConceptScheme
	 * @throws Exception
	 */
	static public ConceptScheme createConceptScheme(String id, String version, String parentId, String parentVersion)
			throws Exception {
		log.debug("ConceptSchemes.createConceptScheme()");

		ConceptSchemeDocument conceptShemeDocument = ConceptSchemeDocument.Factory.newInstance();

		ConceptSchemeType conceptSchemeType = conceptShemeDocument.addNewConceptScheme();
		conceptSchemeType.setId(id);
		if (version != null) {
			conceptSchemeType.setVersion(version);
		}

		ConceptScheme conceptScheme = new ConceptScheme(conceptShemeDocument, parentId, parentVersion);

		return conceptScheme;
	}

	/**
	 * 
	 * @param id
	 * @param version
	 * @param parentId
	 * @param parentVersion
	 * @return ConceptScheme
	 * @throws Exception
	 */
	static public ConceptScheme getConceptScheme(String id, String version, String parentId, String parentVersion)
			throws Exception {
		log.debug("ConceptSchemes.getConceptScheme()");

		ConceptSchemeDocument conceptSchemeDocument = DdiManager.getInstance().getConceptScheme(id, version,
				parentId, parentVersion);

		ConceptScheme conceptScheme = new ConceptScheme(conceptSchemeDocument, parentId, parentVersion);

		return conceptScheme;
	}

	/**
	 * Create new DBXML Concept Scheme
	 * 
	 * @param conceptScheme
	 *            concept scheme instance
	 * @param parentId
	 *            Id. of Conceptual Component
	 * @param parentVersion
	 *            Version of Conceptual Component
	 * @throws DDIFtpException
	 */
	static public void create(ConceptScheme conceptScheme) throws DDIFtpException {
		try {
			DdiManager.getInstance().createElement(conceptScheme.getConceptSchemeDocument(),
					conceptScheme.getParentId(), conceptScheme.getParentVersion(), "ConceptualComponent");
		} catch (DDIFtpException e) {
			log.error("Create DBXML Concept Scheme error: " + e.getMessage());
			
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
	 * Update DBXML Concept Scheme corresponding to the given ConceptScheme
	 * instance
	 * 
	 * @param conceptScheme
	 *            concept Scheme instance
	 * @throws DDIFtpException
	 */
	static public void update(ConceptScheme conceptScheme) throws DDIFtpException {
		// TODO Version Control - not supported
		log.debug("Update DBXML Concept Scheme:\n" + conceptScheme.getConceptSchemeDocument());
		try {
			DdiManager.getInstance().updateElement(conceptScheme.getConceptSchemeDocument(), conceptScheme.getId(),
					conceptScheme.getVersion());
		} catch (DDIFtpException e) {
			log.error("Update DBXML Concept Scheme error: " + e.getMessage());
			
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
	 * Delete DBXML Concept Scheme
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
		ConceptScheme conceptScheme = getConceptScheme(id, version, parentId, parentVersion);
		try {
			DdiManager.getInstance().deleteElement(conceptScheme.getConceptSchemeDocument(), conceptScheme.getParentId(),
					conceptScheme.getParentVersion(), "ConceptualComponent");
		} catch (DDIFtpException e) {
			log.error("Delete DBXML Concept Scheme error: " + e.getMessage());
			
			throw new DDIFtpException(e.getMessage());
		}
		
		// TODO When is xml-file updated - when object saved?
		if (xml_export_filename.length() > 0) {
			File outFile = new File("resources" + File.separator + xml_export_filename);
			PersistenceManager.getInstance().exportResoure(DbXml.FULLY_DECLARED_NS_DOC, outFile);
		}
	}
}
