package org.ddialliance.ddieditor.ui.model.concept;

/**
 * Concept model.
 * 
 */
/*
 * $Author$ 
 * $Date$ 
 * $Revision$
 */

import org.ddialliance.ddi_3_0.xml.xmlbeans.conceptualcomponent.ConceptDocument;
import org.ddialliance.ddi_3_0.xml.xmlbeans.conceptualcomponent.impl.ConceptTypeImpl;
import org.ddialliance.ddi_3_0.xml.xmlbeans.reusable.LabelType;
import org.ddialliance.ddi_3_0.xml.xmlbeans.reusable.StructuredStringType;
import org.ddialliance.ddieditor.ui.model.LabelDescription;
import org.ddialliance.ddieditor.ui.model.Language;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;

public class Concept extends LabelDescription {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, Concept.class);

	private ConceptDocument conceptDocument;
	private ConceptTypeImpl conceptTypeImpl;
	
	/**
	 * Constructor
	 * 
	 * @param conceptDocument
	 * @param parentId
	 * @param parentVersion
	 * @throws Exception
	 */
	public Concept(ConceptDocument conceptDocument, String parentId, String parentVersion)
			throws Exception {
		
		super(conceptDocument.getConcept().getId(), conceptDocument.getConcept().getVersion(),
				parentId, parentVersion, conceptDocument.getConcept().getLabelList(),
				conceptDocument.getConcept().getDescriptionList());
		
		if (conceptDocument == null) {
			// TODO Create new Concept
			this.conceptDocument = null;
		}
		this.conceptDocument = conceptDocument;
		this.conceptTypeImpl = (ConceptTypeImpl) conceptDocument.getConcept();
	}
	
	/**
	 * Get Concept Document of Concept.
	 * 
	 * @return ConceptDocument
	 */
	public ConceptDocument getConceptDocument() {
		return conceptDocument;
	}
		
	/**
	 * Set label of Concept.
	 * 
	 * @param string
	 * @return LabelType (always null)
	 */
	public LabelType setLabel(String string, Language language) {
		
		LabelType labelType = super.setLabel(string, language);
		if (labelType != null) {
			conceptTypeImpl.getLabelList().add(labelType);
		}
		return null;
	}
	
	/**
	 * Set Original Label of Concept.
	 * 'Original' means not translated.
	 * 
	 * @param string
	 * @return LabelType (always null)
	 */
	public LabelType setLabel(String string) {

		LabelType labelType = super.setLabel(string);
		if (labelType != null) {
			conceptTypeImpl.getLabelList().add(labelType);
		}
		return null;
	}
	
	/**
	 * Set Description of Concept.
	 * 
	 * @param string
	 * @return StructuredStringType (always null)
	 */
	public StructuredStringType setDescr(String string, Language language) {
		StructuredStringType descriptionType = super.setDescr(string, language);
		if (descriptionType != null) {
			conceptTypeImpl.getDescriptionList().add(descriptionType);
		}
		return null;
	}
	
	/**
	 * Set Original Description of Concept.
	 * Original means not translated.
	 * 
	 * @param string
	 * @return StructuredStringType (always null)
	 */
	public StructuredStringType setDescr(String string) {

		StructuredStringType descriptionType = super.setDescr(string);
		if (descriptionType != null) {
			conceptTypeImpl.getDescriptionList().add(descriptionType);
		}
		return null;
	}
	
	/**
	 * Validates the Concept before it is saved. It e.g. checks if all
	 * mandatory attributes has been given.
	 * 
	 * @throws Exception
	 */
	public void validate() throws Exception {
		log.debug("Concept validation performed");

		// No error found:
		return;
	}
}