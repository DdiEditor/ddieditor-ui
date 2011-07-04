package org.ddialliance.ddieditor.ui.model.variable;

import java.math.BigInteger;
import java.util.List;

import org.ddialliance.ddi3.xml.xmlbeans.logicalproduct.CodeRepresentationType;
import org.ddialliance.ddi3.xml.xmlbeans.logicalproduct.VariableDocument;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.DateTimeRepresentationType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.DateTypeCodeType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.NameType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.NumericRepresentationType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.NumericTypeCodeType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.ReferenceType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.RepresentationType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.TextRepresentationType;
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
	public static String[] DATE_TIME_TYPES = new String[] { "DateTime", "Date",
			"Time", "Year", "Month", "Day", "MonthDay", "YearMonth",
			"Duration", "Timespan" };

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
		org.ddialliance.ddi3.xml.xmlbeans.logicalproduct.RepresentationType representation = doc
				.getVariable().getRepresentation();
		if (representation==null) {
			return null;
		} else {
			return doc.getVariable().getRepresentation().getValueRepresentation();			
		}
	}

	// code rep
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

	// missing value
	public List getMissingValue() {
		if (!(getValueRepresentation() instanceof NumericRepresentationType)) {
			log.warn("Not setting NumericRepresentation.type as ValueRepresentation is of type: "
					+ getValueRepresentation().getClass().getName());
			return null;
		}
		return ((NumericRepresentationType) getValueRepresentation())
				.getMissingValue();
	}

	// numeric rep
	public BigInteger getNumericDecimalPosition() {
		if (!(getValueRepresentation() instanceof NumericRepresentationType)) {
			log.warn("Not setting NumericRepresentation.type as ValueRepresentation is of type: "
					+ getValueRepresentation().getClass().getName());
			return null;
		}
		return ((NumericRepresentationType) getValueRepresentation())
				.getDecimalPositions();
	}

	// text rep
	public BigInteger getMaxLength() {
		if (!(getValueRepresentation() instanceof TextRepresentationType)) {
			log.warn("Not setting TextRepresentation.maxlenght as ValueRepresentation is of type: "
					+ getValueRepresentation().getClass().getName());
			return null;
		}
		return ((TextRepresentationType) getValueRepresentation())
				.getMaxLength();
	}

	public BigInteger getMinLength() {
		if (!(getValueRepresentation() instanceof TextRepresentationType)) {
			log.warn("Not setting TextRepresentation.minlength as ValueRepresentation is of type: "
					+ getValueRepresentation().getClass().getName());
			return null;
		}
		return ((TextRepresentationType) getValueRepresentation())
				.getMinLength();
	}

	public String getRegx() {
		if (!(getValueRepresentation() instanceof TextRepresentationType)) {
			log.warn("Not setting TextRepresentation.regx as ValueRepresentation is of type: "
					+ getValueRepresentation().getClass().getName());
			return null;
		}
		return ((TextRepresentationType) getValueRepresentation()).getRegExp();
	}

	// date time rep
	public String getFormat() {
		if (!(getValueRepresentation() instanceof DateTimeRepresentationType)) {
			log.warn("Not setting DateTimeRepresentation.format as ValueRepresentation is of type: "
					+ getValueRepresentation().getClass().getName());
			return null;
		}
		return ((DateTimeRepresentationType) getValueRepresentation())
				.getFormat();
	}

	public DateTypeCodeType.Enum getDateTimeType() {
		if (!(getValueRepresentation() instanceof DateTimeRepresentationType)) {
			log.warn("Not setting DateTimeRepresentation.format as ValueRepresentation is of type: "
					+ getValueRepresentation().getClass().getName());
			return null;
		}
		return ((DateTimeRepresentationType) getValueRepresentation())
				.getType();
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
			ModelAccessor.setReference(doc.getVariable().getQuestionReferenceList(), ref, (LightXmlObjectType) value);
		}

		// concept ref
		if (type.equals(ModelIdentifingType.Type_B.class)) {
			ReferenceType ref = getConceptReference();
			if (((LightXmlObjectType) value).getId().equals("")) {
				doc.getVariable().unsetConceptReference();
			} else {
				ModelAccessor.setReference(ref, (LightXmlObjectType) value);
			}
		}

		// universe ref
		if (type.equals(ModelIdentifingType.Type_C.class)) {
			ReferenceType ref = getUniverseReference();
			if (((LightXmlObjectType) value).getId().equals("")) {
				doc.getVariable().getUniverseReferenceList().remove(0);
			} else {
				ModelAccessor.setReference(ref, (LightXmlObjectType) value);
			}
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

		// numeric rep 
		// type
		if (type.equals(ModelIdentifingType.Type_E.class)) {
			if (!(getValueRepresentation() instanceof NumericRepresentationType)) {
				log.warn("Not setting NumericRepresentation.type as ValueRepresentation is of type: "
						+ getValueRepresentation().getClass().getName());
				return;
			}
			((NumericRepresentationType) getValueRepresentation())
					.setType((NumericTypeCodeType.Enum) value);
		}

		// dec position
		if (type.equals(ModelIdentifingType.Type_F.class)) {
			if (!(getValueRepresentation() instanceof NumericRepresentationType)) {
				log.warn("Not setting NumericRepresentation.type as ValueRepresentation is of type: "
						+ getValueRepresentation().getClass().getName());
				return;
			}
			((NumericRepresentationType) getValueRepresentation())
					.setDecimalPositions((BigInteger) value);
		}

		// text rep
		// min lenght
		if (type.equals(ModelIdentifingType.Type_G.class)) {
			if (!(getValueRepresentation() instanceof TextRepresentationType)) {
				log.warn("Not setting TextRepresentation.type as ValueRepresentation is of type: "
						+ getValueRepresentation().getClass().getName());
				return;
			}
			if (bigIntIsZero((BigInteger) value)) {
				((TextRepresentationType) getValueRepresentation())
						.unsetMinLength();
				return;
			}
			((TextRepresentationType) getValueRepresentation())
					.setMinLength((BigInteger) value);
		}

		// regx
		if (type.equals(ModelIdentifingType.Type_H.class)) {
			if (!(getValueRepresentation() instanceof TextRepresentationType)) {
				log.warn("Not setting TextRepresentation.type as ValueRepresentation is of type: "
						+ getValueRepresentation().getClass().getName());
				return;
			}
			if ("".equals(((String) value))) {
				((TextRepresentationType) getValueRepresentation())
						.unsetRegExp();
				return;
			}
			((TextRepresentationType) getValueRepresentation())
					.setRegExp((String) value);
		}

		// max lenght
		if (type.equals(ModelIdentifingType.Type_I.class)) {
			if (!(getValueRepresentation() instanceof TextRepresentationType)) {
				log.warn("Not setting TextRepresentation.type as ValueRepresentation is of type: "
						+ getValueRepresentation().getClass().getName());
				return;
			}
			if (bigIntIsZero((BigInteger) value)) {
				((TextRepresentationType) getValueRepresentation())
						.unsetMaxLength();
				return;
			}
			((TextRepresentationType) getValueRepresentation())
					.setMaxLength((BigInteger) value);
		}

		// date time rep
		// format
		if (type.equals(ModelIdentifingType.Type_J.class)) {
			if (!(getValueRepresentation() instanceof DateTimeRepresentationType)) {
				log.warn("Not setting DateTimeRepresentation.format as ValueRepresentation is of type: "
						+ getValueRepresentation().getClass().getName());
				return;
			}
			if (value == null || "".equals((String) value)) {
				((DateTimeRepresentationType) getValueRepresentation())
						.unsetFormat();
				return;
			}
			((DateTimeRepresentationType) getValueRepresentation())
					.setFormat((String) value);
		}

		// type
		if (type.equals(ModelIdentifingType.Type_K.class)) {
			if (!(getValueRepresentation() instanceof DateTimeRepresentationType)) {
				log.warn("Not setting DateTimeRepresentation.type as ValueRepresentation is of type: "
						+ getValueRepresentation().getClass().getName());
				return;
			}
			DateTypeCodeType.Enum dateTime = DateTypeCodeType.Enum
					.forInt((Integer) value);
			((DateTimeRepresentationType) getValueRepresentation())
					.setType(dateTime);
		}
		
		// numeric rep
		// - missing values
		if (type.equals(ModelIdentifingType.Type_L.class)) {
			if (!(getValueRepresentation() instanceof NumericRepresentationType)) {
				log.warn("Not setting NumericRepresentation.type as ValueRepresentation is of type: "
						+ getValueRepresentation().getClass().getName());
				return;
			}
			((NumericRepresentationType) getValueRepresentation())
					.setMissingValue((List) value);
		}
	}

	private boolean bigIntIsZero(BigInteger bigInt) {
		return bigInt.intValue() == 0;
	}

	@Override
	public VariableDocument getDocument() throws DDIFtpException {
		return doc;
	}
}
