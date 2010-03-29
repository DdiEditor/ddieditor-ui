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
import org.ddialliance.ddieditor.ui.dbxml.IDao;
import org.ddialliance.ddieditor.ui.dbxml.XmlEntities;
import org.ddialliance.ddieditor.ui.model.IModel;
import org.ddialliance.ddieditor.ui.model.concept.ConceptScheme;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;

public class ConceptSchemes implements IDao {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, ConceptSchemes.class);

	/**
	 * Get Concept Schemes - light version
	 * 
	 * @param parentConceptComponent
	 * @return List<LightXmlObjectType>
	 * @throws Exception
	 */
	@Override
	public List<LightXmlObjectType> getLightXmlObject(LightXmlObjectType parentConceptComponent) throws Exception {

		log.debug("ConceptSchemes.getLightXmlObject()");

		return getLightXmlObject("", "", parentConceptComponent.getId(), parentConceptComponent.getVersion());
	}


	/**
	 * 
	 * Get Light version of Concept Schemes
	 * 
	 * @param id
	 * @param version
	 * @param parentId
	 * @param parentVersion
	 * @return List<LightXmlObjectType>
	 * @throws Exception
	 */
	@Override
	public List<LightXmlObjectType> getLightXmlObject(String id, String version, String parentId,
			String parentVersion) throws Exception {

		log.debug("ConceptSchemes.getLightXmlObject()");

		List<LightXmlObjectType> lightXmlObjectTypeList = DdiManager.getInstance().getConceptSchemeLight(id, version,
				parentId, parentVersion).getLightXmlObjectList().getLightXmlObjectList();

		return lightXmlObjectTypeList;
	}
	
//	/**
//	 * 
//	 * Get Light Concept List
//	 * 
//	 * - get children (Concepts) of given Concept Scheme
//	 * 
//	 * @param id
//	 * @param version
//	 * @return List<LightXmlObjectType>
//	 * @throws Exception
//	 */
//	public static List<LightXmlObjectType> getModel(String id, String version) throws Exception {
//		log.debug("ConceptScheme.getModel(). Id: "+id+" Version: "+version);
//
//		DdiManager ddiManager = DdiManager.getInstance();
//
//		LightXmlObjectListDocument listDoc = ddiManager.getConceptsLight("", "", id, version);
//
//		LightXmlObjectListType lightXmlObjectListType = listDoc.getLightXmlObjectList();
//
//		List<LightXmlObjectType> listLightXmlObjectListType = lightXmlObjectListType.getLightXmlObjectList();
//
//		return listLightXmlObjectListType;
//	}


//	/**
//	 * Get Concept Scheme by Id
//	 * 
//	 * @param id
//	 * @param parentId
//	 * @return ConceptSchemeType
//	 * @throws Exception
//	 */
//	public ConceptSchemeType getConceptSchemeById(String id, String parentId) throws Exception {
//		log.debug("ConceptScheme.getConceptSchemeById()");
//		return DdiManager.getInstance().getConceptScheme(id, null, parentId, null).getConceptScheme();
//	}
	
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
	@Override
	public ConceptScheme create(String id, String version, String parentId, String parentVersion)
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
	@Override
	public ConceptScheme getModel(String id, String version, String parentId, String parentVersion)
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
	public void create(ConceptScheme model) throws DDIFtpException {
		try {
			DdiManager.getInstance().createElement(model.getConceptSchemeDocument(),
					model.getParentId(), model.getParentVersion(), "ConceptualComponent");
		} catch (DDIFtpException e) {
			log.error("Create DBXML Concept Scheme error: " + e.getMessage());
			
			throw new DDIFtpException(e.getMessage());
		}
		
//		// TODO When is xml-file updated - when object saved?
//		if (xml_export_filename.length() > 0) {
//			File outFile = new File("resources" + File.separator + xml_export_filename);
//			PersistenceManager.getInstance().exportResoure(DbXml.FULLY_DECLARED_NS_DOC, outFile);
//		}
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
	static public void update(ConceptScheme model) throws DDIFtpException {
		// TODO Version Control - not supported
		log.debug("Update DBXML Concept Scheme:\n" + model.getConceptSchemeDocument());
		try {
			DdiManager.getInstance().updateElement(model.getConceptSchemeDocument(), model.getId(),
					model.getVersion());
		} catch (DDIFtpException e) {
			log.error("Update DBXML Concept Scheme error: " + e.getMessage());
			
			throw new DDIFtpException(e.getMessage());
		}
		
//		// TODO When is xml-file updated - when object saved?
//		if (xml_export_filename.length() > 0) {
//			File outFile = new File("resources" + File.separator + xml_export_filename);
//			PersistenceManager.getInstance().exportResoure(DbXml.FULLY_DECLARED_NS_DOC, outFile);
//		}
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
	@Override
	public void delete(String id, String version, String parentId, String parentVersion) throws Exception {
		log.debug("Delete DBXML Concept Scheme");
		ConceptScheme conceptScheme = getModel(id, version, parentId, parentVersion);
		try {
			DdiManager.getInstance().deleteElement(conceptScheme.getConceptSchemeDocument(), conceptScheme.getParentId(),
					conceptScheme.getParentVersion(), "ConceptualComponent");
		} catch (DDIFtpException e) {
			log.error("Delete DBXML Concept Scheme error: " + e.getMessage());
			
			throw new DDIFtpException(e.getMessage());
		}
		
		// TODO When is xml-file updated - when object saved?
//		if (xml_export_filename.length() > 0) {
//			File outFile = new File("resources" + File.separator + xml_export_filename);
//			PersistenceManager.getInstance().exportResoure(DbXml.FULLY_DECLARED_NS_DOC, outFile);
//		}
	}

	@Override
	public void create(IModel model) throws DDIFtpException {
		DdiManager.getInstance().createElement(model.getDocument(), model.getParentId(), model.getParentVersion(),
				"ConceptualComponent");
	}

	@Override
	public void update(IModel model) throws DDIFtpException {
		DdiManager.getInstance().updateElement(model.getDocument(),
				model.getId(), model.getVersion());
	}
}
