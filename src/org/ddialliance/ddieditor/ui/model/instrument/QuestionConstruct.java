package org.ddialliance.ddieditor.ui.model.instrument;

import org.ddialliance.ddi3.xml.xmlbeans.datacollection.QuestionConstructDocument;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.SpecificSequenceType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.InternationalStringType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.ReferenceType;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.model.Model;
import org.ddialliance.ddieditor.ui.model.ModelAccessor;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;

public class QuestionConstruct extends Model {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			QuestionConstruct.class);
	QuestionConstructDocument doc;

	/**
	 * Constructor
	 * @param doc
	 * @param parentId
	 * @param parentVersion
	 */
	public QuestionConstruct(QuestionConstructDocument doc, String parentId,
			String parentVersion) {
		super(doc, parentId, parentVersion);
		this.doc = doc;
	}

	/**
	 * QuestionReference - id
	 * 
	 * @return question reference
	 */
	public ReferenceType getQuestionReference() {
		ReferenceType result = null;
		if (doc.getQuestionConstruct().getQuestionReference() == null) {
			result = create ? doc.getQuestionConstruct()
					.addNewQuestionReference() : null;
			if (result != null) {
				result.addNewID();
			}
			return result;
		} else {
			return doc.getQuestionConstruct().getQuestionReference();
		}
	}

	/**
	 * ResponseUnit e.g. interviewee
	 * 
	 * @return responseUnit
	 */
	public InternationalStringType getResponseUnit() {
		InternationalStringType result = null;
		if (doc.getQuestionConstruct().getResponseUnitList().isEmpty()) {
			result = create ? doc.getQuestionConstruct().addNewResponseUnit()
					: null;
			return result;
		} else {
			return doc.getQuestionConstruct().getResponseUnitList().get(0);
		}
	}

	/**
	 * ResponseSequence - Describes the sequencing of the response categories or
	 * response options to a question
	 * 
	 * @return response sequence
	 */
	public SpecificSequenceType getResponseSequence() {
		SpecificSequenceType result = null;
		if (doc.getQuestionConstruct().getResponseSequence() == null) {
			result = create ? doc.getQuestionConstruct()
					.addNewResponseSequence() : null;
			return result;
		} else {
			return doc.getQuestionConstruct().getResponseSequence();
		}
	}

	@Override
	public void executeChange(Object value, Class<?> type) throws Exception {
		// QuestionReference
		if (type.equals(ReferenceType.class)) {
			ModelAccessor.setReference(getQuestionReference(),
					(LightXmlObjectType) value);
		}

		// InternationalStringType
		if (type.equals(InternationalStringType.class)) {
			getResponseUnit().setStringValue((String)value);
		}

		// SpecificSequenceType
		if (type.equals(SpecificSequenceType.class)) {
			//getResponseSequence().addNewAlternateSequenceType()
			log.debug("Not implemented");
		}
	}

	@Override
	public QuestionConstructDocument getDocument() throws DDIFtpException {
		return doc;
	}
}
