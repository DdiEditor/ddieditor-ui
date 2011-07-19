package org.ddialliance.ddieditor.ui.model.instrument;

import org.ddialliance.ddi3.xml.xmlbeans.datacollection.RepeatUntilDocument;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.CodeType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.ProgrammingLanguageCodeType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.ReferenceType;
import org.ddialliance.ddieditor.logic.identification.IdentificationManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.model.Model;
import org.ddialliance.ddieditor.ui.model.ModelIdentifingType;
import org.ddialliance.ddieditor.util.DdiEditorConfig;

public class RepeatUntil extends Model {
	RepeatUntilDocument doc;

	public RepeatUntil(RepeatUntilDocument doc, String parentId,
			String parentVersion) {
		super(doc, parentId, parentVersion);
		this.doc = doc;
	}

	@Override
	public void executeChange(Object value, Class<?> type) throws Exception {
		// ProgrammingLanguageCodeType
		if (type.equals(ProgrammingLanguageCodeType.class)) {
			getUntilCondition().setStringValue((String) value);
		}

		// ProgrammingLanguageCodeType/@programmingLanguage
		if (type.equals(ModelIdentifingType.Type_A.class)) {
			getUntilCondition().setProgrammingLanguage((String) value);
		}

		// question reference
		if (type.equals(ModelIdentifingType.Type_B.class)) {
			IdentificationManager.getInstance().addReferenceInformation(
					getUntilReference(), (LightXmlObjectType) value);
		}
	}

	@Override
	public RepeatUntilDocument getDocument() {
		return doc;
	}

	public CodeType getCodeType(CodeType codeType) {
		if (codeType == null) {
			if (create) {
				codeType = doc.getRepeatUntil().addNewUntilCondition();
			} else {
				return null;
			}
		}
		return codeType;
	}

	public ProgrammingLanguageCodeType getUntilCondition() {
		CodeType codeType = getCodeType(doc.getRepeatUntil()
				.getUntilCondition());
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

	public ReferenceType getUntilReference() {
		ReferenceType ref = doc.getRepeatUntil().getUntilConstructReference();
		if (ref == null) {
			return create ? doc.getRepeatUntil()
					.addNewUntilConstructReference() : null;
		}
		return ref;
	}
}
