package org.ddialliance.ddieditor.ui.model;

import java.util.Arrays;
import java.util.List;

import org.ddialliance.ddieditor.model.conceptual.ConceptualType;
import org.ddialliance.ddieditor.ui.editor.category.CategoryEditor;
import org.ddialliance.ddieditor.ui.editor.category.CategorySchemeEditor;
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
import org.ddialliance.ddieditor.ui.editor.question.MultipleQuestionItemEditor;
import org.ddialliance.ddieditor.ui.editor.question.QuestionItemEditor;
import org.ddialliance.ddieditor.ui.editor.question.QuestionSchemeEditor;
import org.ddialliance.ddieditor.ui.editor.study.StudyUnitEditor;
import org.ddialliance.ddieditor.ui.editor.universe.UniverseEditor;
import org.ddialliance.ddieditor.ui.editor.universe.UniverseSchemeEditor;
import org.ddialliance.ddieditor.ui.editor.variable.VariableEditor;
import org.ddialliance.ddieditor.ui.editor.variable.VariableSchemeEditor;
import org.ddialliance.ddieditor.ui.perspective.CategoryPerspective;
import org.ddialliance.ddieditor.ui.perspective.CodesPerspective;
import org.ddialliance.ddieditor.ui.perspective.ConceptsPerspective;
import org.ddialliance.ddieditor.ui.perspective.InfoPerspective;
import org.ddialliance.ddieditor.ui.perspective.InstrumentPerspective;
import org.ddialliance.ddieditor.ui.perspective.QuestionsPerspective;
import org.ddialliance.ddieditor.ui.perspective.UniversePerspective;
import org.ddialliance.ddieditor.ui.perspective.VariablePerspective;
import org.ddialliance.ddieditor.ui.view.Messages;
import org.ddialliance.ddiftp.util.DDIFtpException;

/**
 * Type to bind DDI elements with Eclipse RCP via the following properties:
 * <ul>
 * <li>elementName - DDI local name</li>
 * <li>perspectiveId - preferred RCP perspective ID</li>
 * <li>editorId - corresponding RCP editor ID</li>
 * <li>idPrefix - prefix for DDI ID generation</li>
 * <li>displayMessageEntry - message key for retrieving i18n label of
 * elementName</li>
 * <li>if OPEN - support for open view given
 * <li>list of subelements (e.g. Multiple Question Item and Question Item are
 * subelements to Question Scheme)
 * </ul>
 */
public enum ElementType {
	// application
	FILE("", InfoPerspective.ID, FileEditor.ID, "", "ddi3file.label", "", null), MAINTAINABLE_LIGHTLABEL(
			"", "", "", "", "", "", null),

	// studyunit
	CONCEPTUAL_STUDY_UNIT("studyunit__StudyUnit", "", StudyUnitEditor.ID, "",
			"InfoView.label.studyUnitLabel.StudyUnit", "OPEN", null), STUDY_UNIT(
			"studyunit__StudyUnit", "", StudyUnitEditor.ID, "stdu",
			"InfoView.label.studyUnitLabel.StudyUnit", "OPEN", null),

	// universe
	UNIVERSE("Universe", UniversePerspective.ID, UniverseEditor.ID, "univ",
			"UniverseView.label.universeLabel.Universe", "", null), UNIVERSE_SCHEME(
			"UniverseScheme", UniversePerspective.ID, UniverseSchemeEditor.ID,
			"unis", "UniverseView.label.universeSchemeLabel.UniverseScheme",
			"", Arrays.asList(ElementType.UNIVERSE)),

	// concept
	CONCEPT("Concept", ConceptsPerspective.ID, ConceptEditor.ID, "conc",
			"ConceptView.label.conceptLabel.Concept", "", null), CONCEPT_SCHEME(
			"ConceptScheme", ConceptsPerspective.ID, ConceptSchemeEditor.ID,
			"cons", "ConceptView.label.conceptSchemeLabel.ConceptScheme", "",
			Arrays.asList(ElementType.CONCEPT)),
	// question
	QUESTION_ITEM("QuestionItem", QuestionsPerspective.ID,
			QuestionItemEditor.ID, "quei",
			"QuestionItemView.label.questionItemLabel.QuestionItem", "", null), MULTIPLE_QUESTION_ITEM(
			"MultipleQuestionItem",
			QuestionsPerspective.ID,
			MultipleQuestionItemEditor.ID,
			"mquei",
			"QuestionItemView.label.multipleQuestionItemLabel.MultipleQuestionItem",
			"", Arrays.asList(ElementType.QUESTION_ITEM)), QUESTION_SCHEME(
			"QuestionScheme", QuestionsPerspective.ID, QuestionSchemeEditor.ID,
			"ques",
			"QuestionItemView.label.questionSchemeLabel.QuesitionScheme", "",
			Arrays.asList(ElementType.QUESTION_ITEM,
					ElementType.MULTIPLE_QUESTION_ITEM)),

	// category
	CATEGORY("Category", CategoryPerspective.ID, CategoryEditor.ID, "cat",
			"CategoryView.label.categoryLabel.Category", "", null), CATEGORY_SCHEME(
			"CategoryScheme", CategoryPerspective.ID, CategorySchemeEditor.ID,
			"cats", "CategoryView.label.categorySchemeLabel.CategoryScheme",
			"", Arrays.asList(ElementType.CATEGORY)),

	// code
	CODE_SCHEME("CodeScheme", CodesPerspective.ID, CodeSchemeEditor.ID, "cods",
			"codeView.label.codeSchemeLabel.CodeScheme", "", null),

	// instrument
	INSTRUMENT("Instrument", InstrumentPerspective.ID, InstrumentEditor.ID,
			"inst", "InstrumentItemView.label.instrumentItemLabel.Instrument",
			"", null), QUESTION_CONSTRUCT("QuestionConstruct",
			InstrumentPerspective.ID, QuestionConstructEditor.ID, "quec",
			"InstrumentView.QuestionConstruct.label", "", null), STATEMENT_ITEM(
			"StatementItem", InstrumentPerspective.ID, StatementItemEditor.ID,
			"stai", "InstrumentView.StatementItem.label", "", null), IF_THEN_ELSE(
			"IfThenElse", InstrumentPerspective.ID, IfThenElseEditor.ID,
			"ifth", "InstrumentView.IfThenElse.label", "", null), REPEAT_UNTIL(
			"RepeatUntil", InstrumentPerspective.ID, RepeatUntilEditor.ID,
			"repu", "InstrumentView.RepeatUntil.label", "", null), LOOP("Loop",
			InstrumentPerspective.ID, LoopEditor.ID, "loop",
			"InstrumentView.Loop.label", "", null), REPEAT_WHILE("RepeatWhile",
			InstrumentPerspective.ID, RepeatWhileEditor.ID, "repw",
			"InstrumentView.RepeatWhile.label", "", null), SEQUENCE("Sequence",
			InstrumentPerspective.ID, SequenceEditor.ID, "seqc",
			"InstrumentView.Sequence.label", "", null), COMPUTATION_ITEM(
			"ComputationItem", InstrumentPerspective.ID,
			ComputationItemEditor.ID, "copi",
			"InstrumentView.ComputationItem.label", "", null), CONTROL_CONSTRUCT_SCHEME(
			"ControlConstructScheme", InstrumentPerspective.ID,
			ControlConstructSchemeEditor.ID, "cocs",
			"InstrumentView.ControlConstructScheme.label", "", Arrays.asList(
					ElementType.QUESTION_CONSTRUCT, ElementType.STATEMENT_ITEM,
					ElementType.IF_THEN_ELSE, ElementType.REPEAT_UNTIL,
					ElementType.LOOP, ElementType.REPEAT_WHILE,
					ElementType.SEQUENCE, ElementType.COMPUTATION_ITEM)),

	// variable
	VARIABLE("Variable", VariablePerspective.ID, VariableEditor.ID, "vari",
			"Variable", "", null), VARIABLE_SCHEME("VariableScheme",
			VariablePerspective.ID, VariableSchemeEditor.ID, "vars",
			"VariableScheme", "", Arrays.asList(ElementType.VARIABLE));

	private String elementName;
	private String perspectiveId;
	private String editorId;
	private String idPrefix;
	private String displayMessageEntry;
	private String withOpen;
	private List<ElementType> subElements;

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
	 * @param withOpen
	 *            view supported
	 * @param subElements
	 *            of additional subelements
	 */
	private ElementType(String elementName, String perspectiveId,
			String editorId, String idPrefix, String displayMessageEntry,
			String withOpen, List<ElementType> subElements) {
		this.elementName = elementName;
		this.perspectiveId = perspectiveId;
		this.editorId = editorId;
		this.idPrefix = idPrefix;
		this.displayMessageEntry = displayMessageEntry;
		this.withOpen = withOpen;
		this.subElements = subElements;
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

	public List<ElementType> getSubElements() {
		return subElements;
	}

	public static String getPerspectiveId(String elementName)
			throws DDIFtpException {
		for (int i = 0; i < ElementType.values().length; i++) {
			ElementType elementType = ElementType.values()[i];
			if (elementType.getElementName().equals(elementName)) {
				return elementType.getPerspectiveId();
			}
		}
		// not found
		DDIFtpException e = new DDIFtpException(
				Messages.getString("editor.editelement.notimplemented"),
				new Object[] { elementName }, new Throwable());
		throw e;
	}

	public static ElementType getElementType(String elementName)
			throws DDIFtpException {
		for (int i = 0; i < ElementType.values().length; i++) {
			ElementType elementType = ElementType.values()[i];
			if (elementType.getElementName().equals(elementName)) {
				return elementType;
			}
		}
		// not found
		DDIFtpException e = new DDIFtpException(
				Messages.getString("editor.editelement.notimplemented"),
				new Object[] { elementName }, new Throwable());
		throw e;
	}

	public static boolean withOpenMenuItem(String elementName)
			throws DDIFtpException {
		for (int i = 0; i < ElementType.values().length; i++) {
			ElementType elementType = ElementType.values()[i];
			if (elementType.getElementName().equals(elementName)) {
				return (elementType.getWithOpen().equals("") ? false : true);
			}
		}
		// not found
		DDIFtpException e = new DDIFtpException(
				Messages.getString("editor.editelement.notimplemented"),
				new Object[] { elementName }, new Throwable());
		throw e;
	}

	public static ElementType getElementTypeByConceptualType(
			ConceptualType conceptualType) throws DDIFtpException {
		if (conceptualType.equals(ConceptualType.STUDY)) {
			return ElementType.STUDY_UNIT;
		} else if (conceptualType.equals(ConceptualType.LOGIC_Universe)) {
			return ElementType.UNIVERSE_SCHEME;
		} else if (conceptualType.equals(ConceptualType.LOGIC_concepts)) {
			return ElementType.CONCEPT_SCHEME;
		} else if (conceptualType.equals(ConceptualType.LOGIC_category)) {
			return ElementType.CATEGORY_SCHEME;
		} else if (conceptualType.equals(ConceptualType.LOGIC_code)) {
			return ElementType.CODE_SCHEME;
		} else if (conceptualType.equals(ConceptualType.LOGIC_questions)) {
			return ElementType.QUESTION_SCHEME;
		} else if (conceptualType.equals(ConceptualType.LOGIC_instumentation)) {
			return ElementType.INSTRUMENT;
		} else if (conceptualType.equals(ConceptualType.LOGIC_variable)) {
			return ElementType.VARIABLE_SCHEME;
		}
		// not found
		throw new DDIFtpException(
				Messages.getString("editor.editelement.notimplemented"),
				new Object[] { conceptualType }, new Throwable());
	}
}