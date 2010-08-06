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

import org.apache.xmlbeans.XmlObject;
import org.ddialliance.ddi3.xml.xmlbeans.conceptualcomponent.ConceptDocument;
import org.ddialliance.ddi3.xml.xmlbeans.conceptualcomponent.ConceptType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.LabelType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.StructuredStringType;
import org.ddialliance.ddieditor.ui.model.LabelDescription;
import org.ddialliance.ddieditor.ui.model.Language;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;

public class Concept extends LabelDescription {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, Concept.class);

	private ConceptDocument conceptDocument;
	private ConceptType conceptTypeImpl;

	/**
	 * Constructor
	 * 
	 * @param conceptDocument
	 * @param parentId
	 * @param parentVersion
	 * @throws Exception
	 */
	public Concept(ConceptDocument conceptDocument, String parentId,
			String parentVersion) throws Exception {

		super(conceptDocument.getConcept().getId(), conceptDocument
				.getConcept().getVersion(), parentId, parentVersion,
				conceptDocument.getConcept().getLabelList(), conceptDocument
						.getConcept().getDescriptionList());

		if (conceptDocument == null) {
			// TODO Create new Concept
			this.conceptDocument = null;
		}
		this.conceptDocument = conceptDocument;
		this.conceptTypeImpl = (ConceptType) conceptDocument.getConcept();
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
	 * Set Display Label of Concept.
	 * 
	 * @param string
	 * @return LabelType (always null)
	 */
	public LabelType setDisplayLabel(String string) {

		LabelType labelType = super.setDisplayLabel(string);
		if (labelType != null) {
			conceptTypeImpl.getLabelList().add(labelType);
		}
		return null;
	}

	/**
	 * Set Display Description of Concept.
	 * 
	 * @param string
	 * @return StructuredStringType (always null)
	 */
	public StructuredStringType setDisplayDescr(String string) {

		StructuredStringType descriptionType = super.setDisplayDescr(string);
		if (descriptionType != null) {
			conceptTypeImpl.getDescriptionList().add(descriptionType);
		}
		return null;
	}

	/**
	 * Validates the Concept before it is saved. It e.g. checks if all mandatory
	 * attributes has been given.
	 * 
	 * @throws Exception
	 */
	public void validate() throws Exception {
		log.debug("Concept validation performed");

		// No error found:
		return;
	}

	@Override
	public XmlObject getDocument() throws DDIFtpException {
		return this.conceptDocument;
	}

	@Override
	public void executeChange(Object value, Class<?> type) throws Exception {
		// TODO Auto-generated method stub

	}
}
