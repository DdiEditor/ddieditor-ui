package org.ddialliance.ddieditor.ui.dbxml.concept;

import java.util.List;

import org.ddialliance.ddi3.xml.xmlbeans.conceptualcomponent.ConceptSchemeDocument;
import org.ddialliance.ddieditor.logic.identification.IdentificationManager;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.dbxml.IDao;
import org.ddialliance.ddieditor.ui.model.ElementType;
import org.ddialliance.ddieditor.ui.model.IModel;
import org.ddialliance.ddieditor.ui.model.concept.ConceptScheme;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;

public class ConceptSchemeDao implements IDao {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			ConceptSchemeDao.class);

	/**
	 * Get Concept Schemes - light version
	 * 
	 * @param parentConceptComponent
	 * @return List<LightXmlObjectType>
	 * @throws Exception
	 */
	@Override
	public List<LightXmlObjectType> getLightXmlObject(
			LightXmlObjectType parentConceptComponent) throws Exception {

		log.debug("ConceptSchemes.getLightXmlObject()");

		return getLightXmlObject("", "", parentConceptComponent.getId(),
				parentConceptComponent.getVersion());
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
	public List<LightXmlObjectType> getLightXmlObject(String id,
			String version, String parentId, String parentVersion)
			throws Exception {

		log.debug("ConceptSchemes.getLightXmlObject()");

		List<LightXmlObjectType> lightXmlObjectTypeList = DdiManager
				.getInstance().getConceptSchemesLight(id, version, parentId,
						parentVersion).getLightXmlObjectList()
				.getLightXmlObjectList();

		return lightXmlObjectTypeList;
	}

	// /**
	// *
	// * Get Light Concept List
	// *
	// * - get children (Concepts) of given Concept Scheme
	// *
	// * @param id
	// * @param version
	// * @return List<LightXmlObjectType>
	// * @throws Exception
	// */
	// public static List<LightXmlObjectType> getModel(String id, String
	// version) throws Exception {
	// log.debug("ConceptScheme.getModel(). Id: "+id+" Version: "+version);
	//
	// DdiManager ddiManager = DdiManager.getInstance();
	//
	// LightXmlObjectListDocument listDoc = ddiManager.getConceptsLight("", "",
	// id, version);
	//
	// LightXmlObjectListType lightXmlObjectListType =
	// listDoc.getLightXmlObjectList();
	//
	// List<LightXmlObjectType> listLightXmlObjectListType =
	// lightXmlObjectListType.getLightXmlObjectList();
	//
	// return listLightXmlObjectListType;
	// }

	// /**
	// * Get Concept Scheme by Id
	// *
	// * @param id
	// * @param parentId
	// * @return ConceptSchemeType
	// * @throws Exception
	// */
	// public ConceptSchemeType getConceptSchemeById(String id, String parentId)
	// throws Exception {
	// log.debug("ConceptScheme.getConceptSchemeById()");
	// return DdiManager.getInstance().getConceptScheme(id, null, parentId,
	// null).getConceptScheme();
	// }

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
	public ConceptScheme create(String id, String version, String parentId,
			String parentVersion) throws Exception {
		log.debug("ConceptSchemes.create()");

		ConceptSchemeDocument doc = ConceptSchemeDocument.Factory.newInstance();
		IdentificationManager.getInstance()
				.addIdentification(
						doc.addNewConceptScheme(),
						ElementType.getElementType("ConceptScheme")
								.getIdPrefix(), null);
		IdentificationManager.getInstance().addVersionInformation(
				doc.getConceptScheme(), null, null);
		ConceptScheme conceptScheme = new ConceptScheme(doc, parentId,
				parentVersion);
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
	public ConceptScheme getModel(String id, String version, String parentId,
			String parentVersion) throws Exception {
		log.debug("ConceptSchemes.getConceptScheme()");

		ConceptSchemeDocument conceptSchemeDocument = DdiManager.getInstance()
				.getConceptScheme(id, version, parentId, parentVersion);

		ConceptScheme conceptScheme = new ConceptScheme(conceptSchemeDocument,
				parentId, parentVersion);

		return conceptScheme;
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
		try {
			DdiManager.getInstance().updateElement(
					model.getConceptSchemeDocument(), model.getId(),
					model.getVersion());
		} catch (DDIFtpException e) {
			log.error("Update DBXML Concept Scheme error: " + e.getMessage());

			throw new DDIFtpException(e.getMessage());
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
	@Override
	public void delete(String id, String version, String parentId,
			String parentVersion) throws Exception {
		log.debug("Delete DBXML Concept Scheme");
		ConceptScheme conceptScheme = getModel(id, version, parentId,
				parentVersion);
		try {
			DdiManager.getInstance().deleteElement(
					conceptScheme.getConceptSchemeDocument(),
					conceptScheme.getParentId(),
					conceptScheme.getParentVersion(), "ConceptualComponent");
		} catch (DDIFtpException e) {
			log.error("Delete DBXML Concept Scheme error: " + e.getMessage());

			throw new DDIFtpException(e.getMessage());
		}
	}

	@Override
	public void create(IModel model) throws DDIFtpException {
		log.debug("ConceptSchemeDao.create()");
		DdiManager.getInstance().createElement(model.getDocument(),
				model.getParentId(), model.getParentVersion(),
				"ConceptualComponent");
	}

	@Override
	public void update(IModel model) throws DDIFtpException {
		DdiManager.getInstance().updateElement(model.getDocument(),
				model.getId(), model.getVersion());
	}
}
