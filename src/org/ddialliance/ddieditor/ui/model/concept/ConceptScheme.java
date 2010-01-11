package org.ddialliance.ddieditor.ui.model.concept;

/**
 * Concept Scheme model.
 * 
 */
/*
 * $Author$ 
 * $Date$ 
 * $Revision$
 */

import org.apache.xmlbeans.XmlObject;
import org.ddialliance.ddi3.xml.xmlbeans.conceptualcomponent.ConceptSchemeDocument;
import org.ddialliance.ddi3.xml.xmlbeans.conceptualcomponent.impl.ConceptSchemeTypeImpl;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.LabelType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.StructuredStringType;
import org.ddialliance.ddieditor.ui.model.LabelDescription;
import org.ddialliance.ddieditor.ui.model.Language;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;

public class ConceptScheme extends LabelDescription {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			ConceptScheme.class);

	private ConceptSchemeDocument conceptSchemeDocument;
	private ConceptSchemeTypeImpl conceptSchemeTypeImpl;

	/**
	 * Constructor of Concept Scheme
	 * 
	 * @param conceptSchemeDocument
	 * @param parentId
	 * @param parentVersion
	 * @throws Exception
	 */
	public ConceptScheme(ConceptSchemeDocument conceptSchemeDocument,
			String parentId, String parentVersion) throws Exception {

		super(conceptSchemeDocument.getConceptScheme().getId(),
				conceptSchemeDocument.getConceptScheme().getVersion(),
				parentId, parentVersion, conceptSchemeDocument
						.getConceptScheme().getLabelList(),
				conceptSchemeDocument.getConceptScheme().getDescriptionList());

		if (conceptSchemeDocument == null) {
			// TODO Create new Concept Scheme
			this.conceptSchemeDocument = null;
		}
		this.conceptSchemeDocument = conceptSchemeDocument;
		this.conceptSchemeTypeImpl = (ConceptSchemeTypeImpl) conceptSchemeDocument
				.getConceptScheme();
	}

	/**
	 * Get Concept Scheme Document of Concept Scheme.
	 * 
	 * @return ConceptSchemeDocument
	 */
	public ConceptSchemeDocument getConceptSchemeDocument() {
		return conceptSchemeDocument;
	}

	/**
	 * Set label of Concept Scheme.
	 * 
	 * @param string
	 * @return LabelType (always null)
	 */
	public LabelType setLabel(String string, Language language) {

		LabelType labelType = super.setLabel(string, language);
		if (labelType != null) {
			conceptSchemeTypeImpl.getLabelList().add(labelType);
		}
		return null;
	}

	/**
	 * Set Original Label of Concept Scheme. 'Original' means not translated.
	 * 
	 * @param string
	 * @return LabelType
	 */
	public LabelType setLabel(String string) {

		LabelType labelType = super.setLabel(string);
		if (labelType != null) {
			conceptSchemeTypeImpl.getLabelList().add(labelType);
		}
		return null;
	}

	/**
	 * Set Description of Concept Scheme.
	 * 
	 * @param string
	 * @return StructuredStringType
	 */
	public StructuredStringType setDescr(String string, Language language) {
		StructuredStringType descriptionType = super.setDescr(string, language);
		if (descriptionType != null) {
			conceptSchemeTypeImpl.getDescriptionList().add(descriptionType);
		}
		return null;
	}

	/**
	 * Set Original Description of Concept Scheme. Original means not
	 * translated.
	 * 
	 * @param string
	 * @return StructuredStringType
	 */
	public StructuredStringType setDescr(String string) {

		StructuredStringType descriptionType = super.setDescr(string);
		if (descriptionType != null) {
			conceptSchemeTypeImpl.getDescriptionList().add(descriptionType);
		}
		return null;
	}

	/**
	 * Validates the Concept Scheme before it is saved. It e.g. checks if all
	 * mandatory attributes has been given.
	 * 
	 * @throws Exception
	 */
	public void validate() throws Exception {
		log.debug("Concept Scheme validation performed");

		// No error found:
		return;
	}

	@Override
	public XmlObject getDocument() throws DDIFtpException {
		return conceptSchemeDocument;
	}

	@Override
	public void executeChange(Object value, Class<?> type) throws Exception {
		// TODO Auto-generated method stub

	}
}
