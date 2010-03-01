package org.ddialliance.ddieditor.ui.model.instrument;

import org.ddialliance.ddi3.xml.xmlbeans.datacollection.RepeatWhileDocument;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.CodeType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.ProgrammingLanguageCodeType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.ReferenceType;
import org.ddialliance.ddieditor.logic.identification.IdentificationManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.model.Model;
import org.ddialliance.ddieditor.ui.model.ModelIdentifingType;

public class RepeatWhile extends Model {
	RepeatWhileDocument doc;

	public RepeatWhile(RepeatWhileDocument doc, String parentId,
			String parentVersion) {
		super(doc, parentId, parentVersion);
		this.doc = doc;
	}

	@Override
	public void executeChange(Object value, Class<?> type) throws Exception {
		// ProgrammingLanguageCodeType
		if (type.equals(ProgrammingLanguageCodeType.class)) {
			getWhileCondition().setStringValue((String) value);
		}

		// ProgrammingLanguageCodeType/@programmingLanguage
		if (type.equals(ModelIdentifingType.Type_A.class)) {
			getWhileCondition().setProgrammingLanguage((String) value);
		}

		// question reference
		if (type.equals(ModelIdentifingType.Type_B.class)) {
			IdentificationManager.getInstance().addReferenceInformation(
					getWhileReference(), (LightXmlObjectType) value);
		}
	}

	@Override
	public RepeatWhileDocument getDocument() {
		return doc;
	}

	public CodeType getCodeType(CodeType codeType) {
		if (codeType == null) {
			if (create) {
				codeType = doc.getRepeatWhile().addNewWhileCondition();
			} else {
				return null;
			}
		}
		return codeType;
	}

	public ProgrammingLanguageCodeType getWhileCondition() {
		CodeType codeType = getCodeType(doc.getRepeatWhile()
				.getWhileCondition());
		if (codeType == null) {
			return null;
		}
		if (codeType.getCodeList().isEmpty()) {
			return create ? codeType.addNewCode() : null;
		} else {
			return codeType.getCodeList().get(0);
		}
	}

	public ReferenceType getWhileReference() {
		ReferenceType ref = doc.getRepeatWhile().getWhileConstructReference();
		if (ref == null) {
			return create ? doc.getRepeatWhile()
					.addNewWhileConstructReference() : null;
		}
		return ref;
	}
}
