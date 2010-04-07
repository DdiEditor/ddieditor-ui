package org.ddialliance.ddieditor.ui.dbxml.concept;

import java.util.List;

import org.ddialliance.ddi3.xml.xmlbeans.conceptualcomponent.ConceptDocument;
import org.ddialliance.ddi3.xml.xmlbeans.conceptualcomponent.ConceptType;
import org.ddialliance.ddieditor.logic.identification.IdentificationManager;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.dbxml.IDao;
import org.ddialliance.ddieditor.ui.model.ElementType;
import org.ddialliance.ddieditor.ui.model.IModel;
import org.ddialliance.ddieditor.ui.model.concept.Concept;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.ddialliance.ddiftp.util.xml.XmlBeansUtil;

/**
 * Concepts (DBXML).
 * 
 */
/*
 * $Author$ 
 * $Date$ 
 * $Revision$
 */

public class ConceptDao implements IDao {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, ConceptDao.class);

	/**
	 * Get Concepts - light version
	 * 
	 * @param parentConceptScheme
	 * @return List<LightXmlObjectType>
	 * @throws Exception
	 */
	@Override
	public List<LightXmlObjectType> getLightXmlObject(LightXmlObjectType parentConceptScheme) throws Exception {

		log.debug("Concepts.getLightXmlObject()");

		return getLightXmlObject("", "", parentConceptScheme.getId(), parentConceptScheme.getVersion());
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
	@Override
	public List<LightXmlObjectType> getLightXmlObject(String id, String version, String parentId,
			String parentVersion) throws Exception {

		log.debug("Concepts.getLightXmlObject()");

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
	@Override
	public Concept create(String id, String version, String parentId, String parentVersion)
			throws Exception {
		log.debug("Concepts.create()");

		ConceptDocument doc = ConceptDocument.Factory.newInstance();
		IdentificationManager.getInstance().addIdentification(
				doc.addNewConcept(),
				ElementType.getElementType("Concept").getIdPrefix(), null);
		IdentificationManager.getInstance().addVersionInformation(
				doc.getConcept(), null, null);
		Concept concept = new Concept(doc, parentId, parentVersion);
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
	@Override
	public Concept getModel(String id, String version, String parentId, String parentVersion) throws Exception {
		log.debug("Concepts.getModel("+id+")");

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
	static public void create(Concept model) throws DDIFtpException {
		try {
			DdiManager.getInstance().createElement(model.getConceptDocument(),
					model.getParentId(), model.getParentVersion(), "ConceptScheme");
		} catch (DDIFtpException e) {
			log.error("Create DBXML Concept error: " + e.getMessage());
			throw e;
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
	static public void update(Concept model) throws DDIFtpException {
		// TODO Version Control - not supported
		log.debug("Update DBXML Concept:\n" + model.getConceptDocument());
		try {
			DdiManager.getInstance().updateElement(model.getConceptDocument(), model.getId(),
					model.getVersion());
		} catch (DDIFtpException e) {
			log.error("Update DBXML Concept error: " + e.getMessage());
			throw e;
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
	@Override
	public void delete(String id, String version, String parentId, String parentVersion) throws Exception {
		log.debug("Delete DBXML Concept Scheme");
		Concept concept = getModel(id, version, parentId, parentVersion);
		try {
			DdiManager.getInstance().deleteElement(concept.getConceptDocument(), concept.getParentId(),
					concept.getParentVersion(), "ConceptScheme");
		} catch (DDIFtpException e) {
			log.error("Delete DBXML Concept Scheme error: " + e.getMessage());
			throw e;
		}
	}

	@Override
	public void create(IModel model) throws DDIFtpException {
		DdiManager.getInstance().createElement(model.getDocument(),
				model.getParentId(),
				model.getParentVersion(),
				"ConceptScheme");
	}

	@Override
	public void update(IModel model) throws DDIFtpException {
		DdiManager.getInstance().updateElement(model.getDocument(),
				model.getId(), model.getVersion());
	}
}
