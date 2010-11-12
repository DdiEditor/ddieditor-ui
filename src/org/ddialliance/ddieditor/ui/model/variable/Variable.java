package org.ddialliance.ddieditor.ui.model.variable;

import java.math.BigInteger;

import org.ddialliance.ddi3.xml.xmlbeans.logicalproduct.CodeRepresentationType;
import org.ddialliance.ddi3.xml.xmlbeans.logicalproduct.VariableDocument;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.NameType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.NumericRepresentationType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.NumericTypeCodeType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.ReferenceType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.RepresentationType;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.model.Model;
import org.ddialliance.ddieditor.ui.model.ModelAccessor;
import org.ddialliance.ddieditor.ui.model.ModelIdentifingType;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;

public class Variable extends Model {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, Variable.class);
	VariableDocument doc;
	public static String[] NUMERIC_TYPES = new String[] { "BigInteger",
			"Integer", "Long", "Short", "Decimal", "Float", "Double", "Count",
			"Incremental" };

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

	public org.ddialliance.ddi3.xml.xmlbeans.logicalproduct.RepresentationType getRepresentation() {
		return doc.getVariable().getRepresentation();
	}

	public RepresentationType getValueRepresentation() {
		// LiteralTextType lTextType = (LiteralTextType) textType.substitute(
		// LiteralTextDocument.type.getDocumentElementName(),
		// LiteralTextType.type);
		// lTextType.addNewText();
		return doc.getVariable().getRepresentation().getValueRepresentation();
	}

	public ReferenceType getCodeRepresentationCodeSchemeReference() {
		RepresentationType rep = getValueRepresentation();
		if (!(rep instanceof CodeRepresentationType)) {
			return null;
		}

		ReferenceType reference = ((CodeRepresentationType) rep)
				.getCodeSchemeReference();
		if (create && reference == null) {
			reference = ((CodeRepresentationType) rep)
					.addNewCodeSchemeReference();
		}
		return reference;
	}

	public BigInteger getNumericDecimalPosition() {
		if (!(getValueRepresentation() instanceof NumericRepresentationType)) {
			log.warn("Not setting NumericRepresentation.type as ValueRepresentation is of type: "
					+ getValueRepresentation().getClass().getName());
			return null;
		}
		return ((NumericRepresentationType) getValueRepresentation())
				.getDecimalPositions();
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

		// code representation
		// code scheme ref
		if (type.equals(ModelIdentifingType.Type_D.class)) {
			ReferenceType ref = getCodeRepresentationCodeSchemeReference();
			if (ref == null) {
				log.warn("Not setting CodeRepresentationCodeSchemeReference as ValueRepresentation is of type: "
						+ getValueRepresentation().getClass().getName());
				return;
			}
			ModelAccessor.setReference(ref, (LightXmlObjectType) value);
		}

		// numeric type
		if (type.equals(ModelIdentifingType.Type_E.class)) {
			if (!(getValueRepresentation() instanceof NumericRepresentationType)) {
				log.warn("Not setting NumericRepresentation.type as ValueRepresentation is of type: "
						+ getValueRepresentation().getClass().getName());
				return;
			}
			((NumericRepresentationType) getValueRepresentation())
					.setType((NumericTypeCodeType.Enum) value);
		}

		if (type.equals(ModelIdentifingType.Type_F.class)) {
			if (!(getValueRepresentation() instanceof NumericRepresentationType)) {
				log.warn("Not setting NumericRepresentation.type as ValueRepresentation is of type: "
						+ getValueRepresentation().getClass().getName());
				return;
			}
			((NumericRepresentationType) getValueRepresentation())
					.setDecimalPositions((BigInteger) value);
		}
	}

	@Override
	public VariableDocument getDocument() throws DDIFtpException {
		return doc;
	}
}
