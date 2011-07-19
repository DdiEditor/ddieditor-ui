package org.ddialliance.ddieditor.ui.model.instrument;

import org.ddialliance.ddi3.xml.xmlbeans.datacollection.LoopDocument;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.CodeType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.ProgrammingLanguageCodeType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.ReferenceType;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.model.Model;
import org.ddialliance.ddieditor.ui.model.ModelAccessor;
import org.ddialliance.ddieditor.ui.model.ModelIdentifingType;
import org.ddialliance.ddieditor.util.DdiEditorConfig;

/**
 * Structures a control construct which loops until a limiting condition is met.
 * The ControlConstruct contained in the Loop operates on the LoopVariable until
 * the LoopWhile condition is met, and then control is handed back to the
 * containing control construct.
 */
public class Loop extends Model {
	LoopDocument doc;

	public Loop(LoopDocument doc, String parentId, String parentVersion) {
		super(doc, parentId, parentVersion);
		this.doc = doc;
		this.create = false;
	}

	@Override
	public void executeChange(Object value, Class<?> type) throws Exception {
		// init value
		if (type.equals(ModelIdentifingType.Type_A.class)) {
			getCode(getInitialValue()).setStringValue((String) value);
		}

		// init value program lang
		if (type.equals(ModelIdentifingType.Type_B.class)) {
			getCode(getInitialValue()).setProgrammingLanguage((String) value);
		}

		// init value source question ref
		if (type.equals(ModelIdentifingType.Type_C.class)) {
			ModelAccessor.setReference(getQuestionReference(getInitialValue()),
					(LightXmlObjectType) value);
		}

		// loop while
		if (type.equals(ModelIdentifingType.Type_D.class)) {
			getCode(getLoopWhile()).setStringValue((String) value);
		}

		// loop while program lang
		if (type.equals(ModelIdentifingType.Type_E.class)) {
			getCode(getLoopWhile()).setProgrammingLanguage((String) value);
		}

		// loop while source question ref
		if (type.equals(ModelIdentifingType.Type_F.class)) {
			ModelAccessor.setReference(getQuestionReference(getLoopWhile()),
					(LightXmlObjectType) value);
		}

		// step value
		if (type.equals(ModelIdentifingType.Type_G.class)) {
			getCode(getStepValue()).setStringValue((String) value);
		}

		// step value program lang
		if (type.equals(ModelIdentifingType.Type_H.class)) {
			getCode(getStepValue()).setProgrammingLanguage((String) value);
		}

		// step value source question ref
		if (type.equals(ModelIdentifingType.Type_I.class)) {
			ModelAccessor.setReference(getQuestionReference(getStepValue()),
					(LightXmlObjectType) value);
		}

		// control construct ref
		if (type.equals(ModelIdentifingType.Type_J.class)) {
			ModelAccessor.setReference(getControlConstructReference(),
					(LightXmlObjectType) value);
		}

		// loop variable reference
		if (type.equals(ModelIdentifingType.Type_K.class)) {
			// IdentificationManager.getInstance().addReferenceInformation(
			// getLoopVariableReference(), (LightXmlObjectType) value);
			ModelAccessor.setReference(getLoopVariableReference(),
					(LightXmlObjectType) value);
		}
	}

	@Override
	public LoopDocument getDocument() {
		return doc;
	}

	public CodeType getInitialValue() {
		CodeType codeType = doc.getLoop().getInitialValue();
		if (codeType == null) {
			if (create) {
				codeType = doc.getLoop().addNewInitialValue();
			} else {
				return null;
			}
		}
		return codeType;
	}

	public CodeType getLoopWhile() {
		CodeType codeType = doc.getLoop().getLoopWhile();
		if (codeType == null) {
			if (create) {
				codeType = doc.getLoop().addNewLoopWhile();
			} else {
				return null;
			}
		}
		return codeType;
	}

	public CodeType getStepValue() {
		CodeType codeType = doc.getLoop().getStepValue();
		if (codeType == null) {
			if (create) {
				codeType = doc.getLoop().addNewStepValue();
			} else {
				return null;
			}
		}
		return codeType;
	}

	public ProgrammingLanguageCodeType getCode(CodeType codeType) {
		if (codeType == null) {
			return null;
		}
		if (create && codeType.getCodeList().isEmpty()) {
			if (create) {
				codeType.addNewCode();
				codeType.getCodeList()
						.get(0)
						.setProgrammingLanguage(
								DdiEditorConfig
										.get(DdiEditorConfig.DDI_INSTRUMENT_PROGRAM_LANG));
			}
		} else {
			return codeType.getCodeList().get(0);
		}
		return null;
	}

	public ReferenceType getQuestionReference(CodeType codeType) {
		if (codeType == null && !create) {
			return null;
		}
		ReferenceType ref = null;
		if (codeType.getSourceQuestionReferenceList().isEmpty()) {
			ref = create ? codeType.addNewSourceQuestionReference() : null;
			return ref;
		} else {
			return codeType.getSourceQuestionReferenceList().get(0);
		}
	}

	public ReferenceType getLoopVariableReference() {
		ReferenceType ref = doc.getLoop().getLoopVariableReference();
		if (ref == null) {
			return create ? doc.getLoop().addNewLoopVariableReference() : null;
		}
		return ref;
	}

	public ReferenceType getControlConstructReference() {
		ReferenceType ref = null;
		if (doc.getLoop().getControlConstructReferenceList().isEmpty()) {
			ref = create ? doc.getLoop().addNewControlConstructReference()
					: null;
			return ref;
		} else {
			return doc.getLoop().getControlConstructReferenceList().get(0);
		}
	}
}
