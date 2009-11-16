package org.ddialliance.ddieditor.ui.model;

import org.ddialliance.ddieditor.ui.perspective.ConceptsPerspective;
import org.ddialliance.ddieditor.ui.perspective.InfoPerspective;
import org.ddialliance.ddieditor.ui.perspective.InstrumentPerspective;
import org.ddialliance.ddieditor.ui.perspective.QuestionsPerspective;
import org.ddialliance.ddieditor.ui.view.Messages;
import org.ddialliance.ddiftp.util.DDIFtpException;

public enum ElementType {
	// application
	FILE("", "", "", ""), MAINTAINABLE_LIGHTLABEL("", "", "", ""),

	// studyunit
	STUDY_UNIT("studyunit__StudyUnit", InfoPerspective.ID, "stdu", ""),

	// concept
	CONCEPT_SCHEME("ConceptScheme", ConceptsPerspective.ID, "cons",
			"ConceptView.lable.conceptSchemeLabel.ConceptScheme"), CONCEPT(
			"Concept", ConceptsPerspective.ID, "conc",
			"ConceptView.lable.conceptLabel.Concept"),

	// question
	QUESTION_SCHEME("QuestionScheme", QuestionsPerspective.ID, "ques",
			"QuestionItemView.lable.questionSchemeLabel.QuesitionScheme"), QUESTION_ITEM(
			"QuestionItem", QuestionsPerspective.ID, "quei",
			"QuestionItemView.lable.questionItemLabel.Question"),

	// instrument
	INSTRUMENT("Instrument", InstrumentPerspective.ID, "inst",
			"InstrumentItemView.lable.instrumentItemLabel.Instrument"), ControlConstructScheme(
			"ControlConstructScheme", InstrumentPerspective.ID, "cocs", ""), QUESTION_CONSTRUCT(
			"QuestionConstruct", InstrumentPerspective.ID, "quec", ""), STATEMENT_ITEM(
			"StatementItem", InstrumentPerspective.ID, "stai", ""),

	// code
	CODE_SCHEME("CodeScheme", null, "cods",
			"codeView.lable.codeShemeLabel.CodeScheme"), CODE("Code", null,
			"code", "");

	private String perspectiveId;
	private String elementName;
	private String idPrefix;
	private String displayMessageEntry;

	private ElementType(String elementName, String perspectiveId,
			String idPrefix, String displayMessageEntry) {
		this.elementName = elementName;
		this.perspectiveId = perspectiveId;
		this.idPrefix = idPrefix;
		this.displayMessageEntry = displayMessageEntry;
	}

	public String getElementName() {
		return elementName;
	}

	public String getPerspectiveId() {
		return perspectiveId;
	}

	public String getIdPrefix() {
		return idPrefix;
	}

	public String getDisplayMessageEntry() {
		return displayMessageEntry;
	}

	public String getTranslatedDisplayMessageEntry() {
		return Messages.getString(displayMessageEntry);
	}

	public static String getPerspectiveId(String elementName)
			throws DDIFtpException {
		for (int i = 0; i < ElementType.values().length; i++) {
			ElementType elmentType = ElementType.values()[i];
			if (elmentType.getElementName().equals(elementName)) {
				return elmentType.getPerspectiveId();
			}
		}
		// not found
		DDIFtpException e = new DDIFtpException(Messages
				.getString("editor.editelement.notimplemented"),
				new Object[] { elementName }, new Throwable());
		throw e;
	}

	public static ElementType getElementType(String elementName)
			throws DDIFtpException {
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