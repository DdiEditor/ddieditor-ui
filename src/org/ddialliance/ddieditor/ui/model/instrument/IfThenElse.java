package org.ddialliance.ddieditor.ui.model.instrument;

import org.ddialliance.ddi3.xml.xmlbeans.datacollection.IfThenElseDocument;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.CodeType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.ProgrammingLanguageCodeType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.ReferenceType;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.model.Model;
import org.ddialliance.ddieditor.ui.model.ModelAccessor;
import org.ddialliance.ddieditor.ui.model.ModelIdentifingType;

public class IfThenElse extends Model {
	IfThenElseDocument doc;

	public IfThenElse(IfThenElseDocument doc, String parentId,
			String parentVersion) {
		super(doc, parentId, parentVersion);
		if (doc == null) {
			this.doc = IfThenElseDocument.Factory.newInstance();
			// add id and version
			setId("");
			setVersion("");
		} else {
			this.doc = doc;
		}
	}

	@Override
	public void executeChange(Object value, Class<?> type) throws Exception {
		// ProgrammingLanguageCodeType
		if (type.equals(ProgrammingLanguageCodeType.class)) {
			getProgrammingLanguageCode(
					getCodeType(doc.getIfThenElse().getIfCondition()))
					.setStringValue((String) value);
		}

		// ProgrammingLanguageCodeType/@programmingLanguage
		if (type.equals(ModelIdentifingType.Type_A.class)) {
			getProgrammingLanguageCode(
					getCodeType(doc.getIfThenElse().getIfCondition()))
					.setProgrammingLanguage((String) value);
		}

		// question reference
		if (type.equals(ModelIdentifingType.Type_B.class)) {
			ModelAccessor.setReference(getIfQuestionReference(),
					(LightXmlObjectType) value);
		}

		// then reference
		if (type.equals(ModelIdentifingType.Type_C.class)) {
			ModelAccessor.setReference(getThenReference(),
					(LightXmlObjectType) value);
		}

		// else reference
		if (type.equals(ModelIdentifingType.Type_D.class)) {
			ModelAccessor.setReference(getElseReference(),
					(LightXmlObjectType) value);
		}
	}
	
	@Override
	public void validate() throws Exception {
		// TODO Auto-generated method stub
		super.validate();
	}

	public ProgrammingLanguageCodeType getIfCondition() {
		CodeType codeType = getCodeType(doc.getIfThenElse().getIfCondition());
		return getProgrammingLanguageCode(codeType);
	}

	public ReferenceType getIfQuestionReference() {
		CodeType codeType = getCodeType(doc.getIfThenElse().getIfCondition());
		if (codeType==null) {
			return null;
		}
		ReferenceType ref = null;
		if (codeType.getSourceQuestionReferenceList().isEmpty()) {
			ref =  create ? codeType.addNewSourceQuestionReference() : null;
			if (ref!=null) {
				ref.addNewID();
			}
			return ref;
		} else {
			return codeType.getSourceQuestionReferenceList().get(0);
		}
	}

	public CodeType getCodeType(CodeType codeType) {
		if (codeType == null) {
			if (create) {
				codeType = doc.getIfThenElse().addNewIfCondition();
			} else {
				return null;
			}
		}
		return codeType;
	}

	public ProgrammingLanguageCodeType getProgrammingLanguageCode(
			CodeType codeType) {
		if (codeType == null) {
			return null;
		}
		if (codeType.getCodeList().isEmpty()) {
			return create ? codeType.addNewCode() : null;
		} else {
			return codeType.getCodeList().get(0);
		}
	}

	public ReferenceType getThenReference() {
		ReferenceType ref = doc.getIfThenElse().getThenConstructReference();
		if (ref == null) {
			ref = doc.getIfThenElse().addNewThenConstructReference();
			ref.addNewID();
		}
		return ref;
	}

	public ReferenceType getElseReference() {
		ReferenceType ref = doc.getIfThenElse().getElseConstructReference();
		if (create && ref == null) {
			ref = doc.getIfThenElse().addNewElseConstructReference();
			ref.addNewID();
		}
		return ref;
	}

	@Override
	public IfThenElseDocument getDocument() {
		return doc;
	}
}
