package org.ddialliance.ddieditor.ui.editor;

import org.ddialliance.ddieditor.ui.view.Messages;
import org.ddialliance.ddiftp.util.DDIFtpException;

public enum ElementType {
	// application
	FILE("", ""), MAINTAINABLE_LIGHTLABEL("", ""),
	// studyunit
	STUDY_UNIT("studyunit__StudyUnit", "stdu"),
	// concept
	CONCEPT_SCHEME("ConceptScheme", "cons"), CONCEPT("Concept", "conc"),
	// code
	CODE_SCHEME("CodeScheme", "cods"), CODE("Code", "code"),
	// question
	QUESTION_SCHEME("QuestionScheme", "ques"), QUESTION_ITEM("QuestionItem",
			"quei"),
	// instrument
	INSTRUMENT("Instrument", "inst"), QUESTION_CONSTRUCT("QuestionConstruct",
			"quec"), STATEMENT_ITEM("StatementItem", "stai");

	private String elementName;
	private String idPrefix;

	private ElementType(String elementName, String idPrefix) {
		this.elementName = elementName;
		this.idPrefix = idPrefix;
	}

	public String getElementName() {
		return elementName;
	}

	public String getIdPrefix() {
		return idPrefix;
	}

	public static ElementType getElementType(String elementName) throws DDIFtpException {
		for (int i = 0; i < ElementType.values().length; i++) {
			ElementType elmentType = ElementType.values()[i];
			if (elmentType.getElementName().equals(elementName)) {
				return elmentType;
			}
		}
		// not found
		DDIFtpException e = new DDIFtpException(Messages
				.getString("editor.editelement.notimplemented"),
				new Object[] { elementName }, new Throwable());
		throw e;
	}
}