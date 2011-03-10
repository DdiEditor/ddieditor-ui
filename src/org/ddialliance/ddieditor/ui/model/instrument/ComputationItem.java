package org.ddialliance.ddieditor.ui.model.instrument;

import org.ddialliance.ddi3.xml.xmlbeans.datacollection.ComputationItemDocument;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.CodeType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.ProgrammingLanguageCodeType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.ReferenceType;
import org.ddialliance.ddieditor.logic.identification.IdentificationManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.model.Model;
import org.ddialliance.ddieditor.ui.model.ModelIdentifingType;

public class ComputationItem extends Model {
	ComputationItemDocument doc;

	public ComputationItem(ComputationItemDocument doc, String parentId,
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

		// variable reference
		if (type.equals(ModelIdentifingType.Type_B.class)) {
			IdentificationManager.getInstance().addReferenceInformation(
					getAssignedVariableReference(), (LightXmlObjectType) value);
		}
	}

	@Override
	public ComputationItemDocument getDocument() {
		return doc;
	}

	public CodeType getCodeType(CodeType codeType) {
		if (codeType == null) {
			if (create) {
				codeType = doc.getComputationItem().addNewCode();
			} else {
				return null;
			}
		}
		return codeType;
	}

	public ProgrammingLanguageCodeType getUntilCondition() {
		CodeType codeType = getCodeType(doc.getComputationItem().getCode());
		if (codeType == null) {
			return null;
		}
		if (codeType.getCodeList().isEmpty()) {
			return create ? codeType.addNewCode() : null;
		} else {
			return codeType.getCodeList().get(0);
		}
	}

	public ReferenceType getAssignedVariableReference() {
		ReferenceType ref = doc.getComputationItem()
				.getAssignedVariableReference();
		if (ref == null) {
			return create ? doc.getComputationItem()
					.addNewAssignedVariableReference() : null;
		}
		return ref;
	}
}
