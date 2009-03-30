package org.ddialliance.ddieditor.ui.dbxml;

import java.util.List;

import org.apache.xmlbeans.XmlCursor;
import org.ddialliance.ddi_3_0.xml.xmlbeans.conceptualcomponent.impl.ConceptTypeImpl;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.model.Concept;
import org.ddialliance.ddieditor.ui.reference.ConceptReference;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;

public class Concepts {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, Concepts.class);

	/**
	 * Get Concepts - light version
	 * 
	 * @param parentConceptScheme
	 * @return List<LightXmlObjectType>
	 * @throws Exception
	 */
	static public List<LightXmlObjectType> getConceptsLight(LightXmlObjectType parentConceptScheme) throws Exception {

		log.info("Concepts.getConceptsLight()");

		return Concepts.getConceptsLight("", "", parentConceptScheme.getId(), parentConceptScheme.getVersion());
	}

	static public List<LightXmlObjectType> getConceptsLight(String id, String version, String parentId,
			String parentVersion) throws Exception {

		log.info("Concepts.getConceptsLight()");

		List<LightXmlObjectType> lightXmlObjectTypeList = DdiManager.getInstance().getConceptsLight(id, version,
				parentId, parentVersion).getLightXmlObjectList().getLightXmlObjectList();

		// Debug only:
		for (LightXmlObjectType l : lightXmlObjectTypeList) {
			log.info("Concept Id: " + l.getId());
			XmlCursor xmlCursor = l.getLabelArray(0).newCursor();
			xmlCursor.toLastAttribute();
			xmlCursor.toNextToken();
			String result = xmlCursor.getChars();
			xmlCursor.dispose();
			log.info("Concept Label: " + result);
		}

		return lightXmlObjectTypeList;
	}
	
	static public Concept getConcept(String id, String version, String parentId, String parentVesion) throws Exception {
		log.info("Concepts.getConcept("+id+")");

		ConceptTypeImpl conceptTypeImpl = (ConceptTypeImpl) DdiManager.getInstance().getConcept(id, version, parentId,
				parentVesion).getConcept();

		Concept concept = new Concept(conceptTypeImpl);

		return concept;
	}

}
