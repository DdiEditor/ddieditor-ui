package org.ddialliance.ddieditor.ui.model;

import org.ddialliance.ddieditor.ui.editor.code.CodeSchemeEditor;
import org.ddialliance.ddieditor.ui.editor.concept.ConceptEditor;
import org.ddialliance.ddieditor.ui.editor.concept.ConceptSchemeEditor;
import org.ddialliance.ddieditor.ui.editor.file.FileEditor;
import org.ddialliance.ddieditor.ui.editor.instrument.ComputationItemEditor;
import org.ddialliance.ddieditor.ui.editor.instrument.ControlConstructSchemeEditor;
import org.ddialliance.ddieditor.ui.editor.instrument.IfThenElseEditor;
import org.ddialliance.ddieditor.ui.editor.instrument.InstrumentEditor;
import org.ddialliance.ddieditor.ui.editor.instrument.LoopEditor;
import org.ddialliance.ddieditor.ui.editor.instrument.QuestionConstructEditor;
import org.ddialliance.ddieditor.ui.editor.instrument.RepeatUntilEditor;
import org.ddialliance.ddieditor.ui.editor.instrument.RepeatWhileEditor;
import org.ddialliance.ddieditor.ui.editor.instrument.SequenceEditor;
import org.ddialliance.ddieditor.ui.editor.instrument.StatementItemEditor;
import org.ddialliance.ddieditor.ui.editor.question.QuestionItemEditor;
import org.ddialliance.ddieditor.ui.editor.question.QuestionSchemeEditor;
import org.ddialliance.ddieditor.ui.editor.study.StudyUnitEditor;
import org.ddialliance.ddieditor.ui.perspective.ConceptsPerspective;
import org.ddialliance.ddieditor.ui.perspective.InfoPerspective;
import org.ddialliance.ddieditor.ui.perspective.InstrumentPerspective;
import org.ddialliance.ddieditor.ui.perspective.QuestionsPerspective;
import org.ddialliance.ddieditor.ui.view.CodeView;
import org.ddialliance.ddieditor.ui.view.Messages;
import org.ddialliance.ddiftp.util.DDIFtpException;

/**
 * Type to bind DDI elements with Eclipse RCP via the following properties:
 * <ul>
 * <li>elementName DDI local name</li>
 * <li>perspectiveId preferred RCP prospective ID</li>
 * <li>editorId corresponding RCP editor ID</li>
 * <li>idPrefix prefix for DDI ID generation</li>
 * <li>displayMessageEntry message key for retrieving i18n label of elementName</li>
 * </ul>
 */
public enum ElementType {
	// application
	FILE("", InfoPerspective.ID, FileEditor.ID, "", "ddi3file.label", ""), MAINTAINABLE_LIGHTLABEL("", "", "", "", "",
			""),

	// studyunit
	CONCEPTUAL_STUDY_UNIT("studyunit__StudyUnit", "", StudyUnitEditor.ID, "",
			"InfoView.label.studyUnitLabel.StudyUnit", "OPEN"), STUDY_UNIT("studyunit__StudyUnit", "",
			StudyUnitEditor.ID, "stdu", "InfoView.label.studyUnitLabel.StudyUnit", "OPEN"),

	// universe
	UNIVERSE("Universe", null, null, "univ", "", ""), UNIVERSE_SCHEME("UniverseScheme", null, null, "unis", "", ""),

	// concept
	CONCEPT_SCHEME("ConceptScheme", ConceptsPerspective.ID, ConceptSchemeEditor.ID, "cons",
			"ConceptView.label.conceptSchemeLabel.ConceptScheme", ""), CONCEPT("Concept", ConceptsPerspective.ID,
			ConceptEditor.ID, "conc", "ConceptView.label.conceptLabel.Concept", ""),

	// question
	QUESTION_SCHEME("QuestionScheme", QuestionsPerspective.ID, QuestionSchemeEditor.ID, "ques",
			"QuestionItemView.label.questionSchemeLabel.QuesitionScheme", ""), QUESTION_ITEM("QuestionItem",
			QuestionsPerspective.ID, QuestionItemEditor.ID, "quei",
			"QuestionItemView.label.questionItemLabel.QuestionItem", ""),

	// category
	CATEGORY_SCHEME("CategoryScheme", null, null, "cats", "", ""), CATEGORY("Category", null, null, "cate", "", ""),

	// instrument
	INSTRUMENT("Instrument", InstrumentPerspective.ID, InstrumentEditor.ID, "inst",
			"InstrumentItemView.label.instrumentItemLabel.Instrument", ""), CONTROL_CONSTRUCT_SCHEME(
			"ControlConstructScheme", InstrumentPerspective.ID, ControlConstructSchemeEditor.ID, "cocs",
			"InstrumentView.ControlConstructScheme.label", ""), QUESTION_CONSTRUCT("QuestionConstruct",
			InstrumentPerspective.ID, QuestionConstructEditor.ID, "quec", "InstrumentView.QuestionConstruct.label", ""), STATEMENT_ITEM(
			"StatementItem", InstrumentPerspective.ID, StatementItemEditor.ID, "stai",
			"InstrumentView.StatementItem.label", ""), IF_THEN_ELSE("IfThenElse", InstrumentPerspective.ID,
			IfThenElseEditor.ID, "ifth", "InstrumentView.IfThenElse.label", ""), REPEAT_UNTIL("RepeatUntil",
			InstrumentPerspective.ID, RepeatUntilEditor.ID, "repu", "InstrumentView.RepeatUntil.label", ""), LOOP(
			"Loop", InstrumentPerspective.ID, LoopEditor.ID, "loop", "InstrumentView.Loop.label", ""), REPEAT_WHILE(
			"RepeatWhile", InstrumentPerspective.ID, RepeatWhileEditor.ID, "repw", "InstrumentView.RepeatWhile.label",
			""), SEQUENCE("Sequence", InstrumentPerspective.ID, SequenceEditor.ID, "seqc",
			"InstrumentView.Sequence.label", ""), COMPUTATION_ITEM("ComputationItem", InstrumentPerspective.ID,
			ComputationItemEditor.ID, "copi", "InstrumentView.ComputationItem.label", ""),

	// code
	CODE_SCHEME("CodeScheme", CodeView.ID, CodeSchemeEditor.ID, "cods", "codeView.label.codeSchemeLabel.CodeScheme", ""),

	// variable
	VARIABLE_SCHEME("Variable", null, null, "vars", "", ""), VARIABLE("Variable", null, null, "vari", "", "");

	private String elementName;
	private String perspectiveId;
	private String editorId;
	private String idPrefix;
	private String displayMessageEntry;
	private String withOpen;

	/**
	 * Constructor
	 * 
	 * @param elementName
	 *            DDI local name
	 * @param perspectiveId
	 *            RCP prospective ID
	 * @param editorId
	 *            editor ID
	 * @param idPrefix
	 *            prefix for DDI ID generation
	 * @param displayMessageEntry
	 *            message key for retrieving i18n label of elementName
	 */
	private ElementType(String elementName, String perspectiveId, String editorId, String idPrefix,
			String displayMessageEntry, String withOpen) {
		this.elementName = elementName;
		this.perspectiveId = perspectiveId;
		this.editorId = editorId;
		this.idPrefix = idPrefix;
		this.displayMessageEntry = displayMessageEntry;
		this.withOpen = withOpen;
	}

	public String getElementName() {
		return elementName;
	}

	public String getPerspectiveId() {
		return perspectiveId;
	}

	public String getEditorId() {
		return editorId;
	}

	public String getIdPrefix() {
		return idPrefix;
	}

	/**
	 * Get message key
	 * 
	 * @return message key
	 */
	public String getDisplayMessageEntry() {
		return displayMessageEntry;
	}

	/**
	 * Translate message key
	 * 
	 * @return translated message key
	 */
	public String getTranslatedDisplayMessageEntry() {
		return Messages.getString(displayMessageEntry);
	}

	public String getWithOpen() {
		return withOpen;
	}

	public static String getPerspectiveId(String elementName) throws DDIFtpException {
		for (int i = 0; i < ElementType.values().length; i++) {
			ElementType elementType = ElementType.values()[i];
			if (elementType.getElementName().equals(elementName)) {
				return elementType.getPerspectiveId();
			}
		}
		// not found
		DDIFtpException e = new DDIFtpException(Messages.getString("editor.editelement.notimplemented"),
				new Object[] { elementName }, new Throwable());
		throw e;
	}

	public static ElementType getElementType(String elementName) throws DDIFtpException {
		for (int i = 0; i < ElementType.values().length; i++) {
			ElementType elementType = ElementType.values()[i];
			if (elementType.getElementName().equals(elementName)) {
				return elementType;
			}
		}
		// not found
		DDIFtpException e = new DDIFtpException(Messages.getString("editor.editelement.notimplemented"),
				new Object[] { elementName }, new Throwable());
		throw e;
	}

	public static boolean withOpenMenuItem(String elementName) throws DDIFtpException {
		for (int i = 0; i < ElementType.values().length; i++) {
			ElementType elementType = ElementType.values()[i];
			if (elementType.getElementName().equals(elementName)) {
				return (elementType.getWithOpen().equals("") ? false : true);
			}
		}
		// not found
		DDIFtpException e = new DDIFtpException(Messages.getString("editor.editelement.notimplemented"),
				new Object[] { elementName }, new Throwable());
		throw e;
	}
}