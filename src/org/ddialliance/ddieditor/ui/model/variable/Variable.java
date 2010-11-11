package org.ddialliance.ddieditor.ui.model.variable;

import org.ddialliance.ddi3.xml.xmlbeans.logicalproduct.RepresentationType;
import org.ddialliance.ddi3.xml.xmlbeans.logicalproduct.VariableDocument;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.NameType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.ReferenceType;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.model.Model;
import org.ddialliance.ddieditor.ui.model.ModelAccessor;
import org.ddialliance.ddieditor.ui.model.ModelIdentifingType;
import org.ddialliance.ddiftp.util.DDIFtpException;

public class Variable extends Model {
	VariableDocument doc;

	public Variable(VariableDocument doc, String parentId, String parentVersion) {
		super(doc, parentId, parentVersion);
		this.doc = doc;
	}

	public ReferenceType getQuestionReference() {
		if (doc.getVariable().getQuestionReferenceList().isEmpty()) {
			if (create) {
				ReferenceType ref = doc.getVariable().addNewQuestionReference();
				ref.addNewID();
				return ref;
			}
			return null;
		} else {
			return doc.getVariable().getQuestionReferenceList().get(0);
		}
	}

	public ReferenceType getConceptReference() {
		if (doc.getVariable().getConceptReference() == null) {
			if (create) {
				ReferenceType ref = doc.getVariable().addNewConceptReference();
				ref.addNewID();
				return ref;
			}
			return null;
		} else {
			return doc.getVariable().getConceptReference();
		}
	}

	public ReferenceType getUniverseReference() {
		if (doc.getVariable().getUniverseReferenceList().isEmpty()) {
			if (create) {
				ReferenceType ref = doc.getVariable().addNewUniverseReference();
				ref.addNewID();
				return ref;
			}
			return null;
		} else {
			return doc.getVariable().getUniverseReferenceList().get(0);
		}
	}

	public NameType getName() {
		if (doc.getVariable().getVariableNameList().isEmpty()) {
			if (create) {
				NameType ref = doc.getVariable().addNewVariableName();
				return ref;
			}
			return null;
		} else {
			return doc.getVariable().getVariableNameList().get(0);
		}
	}
	
	public RepresentationType getRepresentation() {
		return doc.getVariable().getRepresentation();
	}

	@Override
	public void executeChange(Object value, Class<?> type) throws Exception {
		// name
		if (type.equals(NameType.class)) {
			getName().setStringValue((String) value);
		}

		// question ref
		if (type.equals(ModelIdentifingType.Type_A.class)) {
			ReferenceType ref = getQuestionReference();
			ModelAccessor.setReference(ref, (LightXmlObjectType) value);
		}

		// concept ref
		if (type.equals(ModelIdentifingType.Type_B.class)) {
			ReferenceType ref = getConceptReference();
			ModelAccessor.setReference(ref, (LightXmlObjectType) value);
		}

		// universe ref
		if (type.equals(ModelIdentifingType.Type_C.class)) {
			ReferenceType ref = getUniverseReference();
			ModelAccessor.setReference(ref, (LightXmlObjectType) value);
		}
	}

	@Override
	public VariableDocument getDocument() throws DDIFtpException {
		return doc;
	}
}
