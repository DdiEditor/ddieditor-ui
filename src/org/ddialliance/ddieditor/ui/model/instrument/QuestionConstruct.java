package org.ddialliance.ddieditor.ui.model.instrument;

import org.ddialliance.ddi3.xml.xmlbeans.datacollection.ItemSequenceTypeType;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.QuestionConstructDocument;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.SpecificSequenceType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.InternationalStringType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.ReferenceType;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.editor.Editor;
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

	public enum ResponceUnit {
		INTERVIEWEE("Interviewee"), INTERVIEWER("Interviewer"), OTHER("Other");
		String label;

		private ResponceUnit(String label) {
			this.label = label;
		}

		public String getLabel() {
			return label;
		}
	};

	/**
	 * Constructor
	 * 
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

	public Integer getResponseUnitConverted() {
		InternationalStringType result = getResponseUnit();
		if (result == null) {
			return null;
		}
		for (int i = 0; i < ResponceUnit.values().length; i++) {
			if (ResponceUnit.values()[i].label.equals(result.getStringValue())) {
				return i+1;
			}
		}
		return 0;
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
			if (((LightXmlObjectType) value).getId().equals("")) {
				doc.getQuestionConstruct().getQuestionReference().removeID(0);
			} else {
				ModelAccessor.setReference(getQuestionReference(), (LightXmlObjectType) value);
			}
		}

		// InternationalStringType
		if (type.equals(InternationalStringType.class)) {
			int count = (Integer) value-1;
			if (count<1) {
				if (getResponseUnit()!=null) {
					doc.getQuestionConstruct().removeResponseUnit(0);
				}
				return;
			}
			String result = ResponceUnit.values()[(Integer) value-1].label;
			getResponseUnit().setStringValue(result);
		}

		// SpecificSequenceType
		if (type.equals(SpecificSequenceType.class)) {
			log.warn("AlternateSequenceType, not implemented");

			int defined = (Integer) value;
			if (defined == -1 || defined == 0) {
				// remove ResponseSequence
				doc.getQuestionConstruct().getDomNode().removeChild(
						doc.getQuestionConstruct().getResponseSequence()
								.getDomNode());
			} else {
				ItemSequenceTypeType.Enum itemSequenceType = ItemSequenceTypeType.Enum
						.forInt(defined);
				getResponseSequence().setItemSequenceType(itemSequenceType);
			}
		}
	}

	@Override
	public QuestionConstructDocument getDocument() throws DDIFtpException {
		return doc;
	}
}
