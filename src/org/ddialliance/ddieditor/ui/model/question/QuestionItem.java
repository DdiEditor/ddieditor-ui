package org.ddialliance.ddieditor.ui.model.question;

/**
 * Question Item model.
 * 
 */
/*
 * $Author$ 
 * $Date$ 
 * $Revision$
 */

import java.math.BigInteger;
import java.util.List;

import org.ddialliance.ddi3.xml.xmlbeans.datacollection.CodeDomainDocument;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.CodeDomainType;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.DynamicTextType;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.LiteralTextDocument;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.LiteralTextType;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.NumericDomainDocument;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.NumericDomainType;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.QuestionItemDocument;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.TextDomainDocument;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.TextType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.NumericTypeCodeType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.ReferenceType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.RepresentationType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.TextDomainType;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.editor.question.ResponseTypeDetail.RESPONSE_TYPES;
import org.ddialliance.ddieditor.ui.model.Model;
import org.ddialliance.ddieditor.ui.model.ModelAccessor;
import org.ddialliance.ddieditor.ui.model.ModelIdentifingType;
import org.ddialliance.ddieditor.ui.util.LanguageUtil;
import org.ddialliance.ddieditor.ui.view.Messages;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.Translator;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.ddialliance.ddiftp.util.xml.XmlBeansUtil;

public class QuestionItem extends Model {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, QuestionItem.class);
	private QuestionItemDocument doc;
	private String originalLanguageCode = null;

	/**
	 * Constructor
	 * 
	 * @param questionItemDocument
	 * @param parentId
	 * @param parentVersion
	 * @throws Exception
	 */
	public QuestionItem(QuestionItemDocument doc, String parentId, String parentVersion) throws Exception {

		super(doc.getQuestionItem(), parentId, parentVersion);

		if (doc == null) {
			this.doc = QuestionItemDocument.Factory.newInstance();
			// add id and version
			setId("");
			setVersion("");
		} else {
			this.doc = doc;
		}
	}

	/**
	 * 
	 * Get Concept reference
	 * 
	 * @return String
	 * @throws Exception
	 */
	public String getConceptRef() throws Exception {
		String conceptRef = "";
		List<ReferenceType> conceptReferenceList = doc.getQuestionItem().getConceptReferenceList();
		if (conceptReferenceList.size() == 1) {
			conceptRef = XmlBeansUtil.getTextOnMixedElement(conceptReferenceList.get(0));
			log.debug("ConceptRef: " + conceptRef);
		} else if (conceptReferenceList.size() > 1) {
			throw new Exception(Messages.getString("QuestionItem.mess.MultipleConceptReferencesNotSupported")); //$NON-NLS-1$
		}
		return conceptRef;
	}

	public ReferenceType getConceptReferenceType() {
		List<ReferenceType> conceptReferenceList = doc.getQuestionItem().getConceptReferenceList();
		if (conceptReferenceList.isEmpty()) {
			ReferenceType ref = doc.getQuestionItem().addNewConceptReference();
			ref.addNewID();
			return ref;
		}
		return conceptReferenceList.get(0);
	}

	/**
	 * Set Concept reference
	 * 
	 * @param conceptRef
	 * @throws Exception
	 */
	public void setConceptRef(String conceptRef) throws Exception {

		if (doc.getQuestionItem().getConceptReferenceList().size() > 1) {
			throw new Exception(Messages.getString("QuestionItem.mess.MultipleConceptReferencesNotSupported")); //$NON-NLS-1$
		}

		if (doc.getQuestionItem().getConceptReferenceList().size() == 1) {
			doc.getQuestionItem().removeConceptReference(0);
		}
		doc.getQuestionItem().addNewConceptReference().addNewID().setStringValue(conceptRef);
	}

	/**
	 * Get Question Items
	 * 
	 * @return list of dynamic texts
	 * @throws DDIFtpException
	 */
	public List<DynamicTextType> getQuestionText() throws DDIFtpException {
		if (doc.getQuestionItem().getQuestionTextList().isEmpty() && create) {
			
			DynamicTextType dynamicText = doc.getQuestionItem().addNewQuestionText();
			TextType textType = dynamicText.addNewText();
			XmlBeansUtil.addTranslationAttributes(dynamicText, Translator
			.getLocaleLanguage(), false, true);
			LiteralTextType lTextType = (LiteralTextType) textType.substitute(LiteralTextDocument.type
					.getDocumentElementName(), LiteralTextType.type);
			lTextType.addNewText();
		}
		return(doc.getQuestionItem().getQuestionTextList());
	}


	/**
	 * Get Response Domain of Question Item.
	 * 
	 * @return RepresentationType
	 */
	public RepresentationType getResponseDomain() {
		return doc.getQuestionItem().getResponseDomain();
	}

	/**
	 * Set Question Item Code Response Domain
	 * 
	 * @param codeSchemeReference
	 * @return RepresentationType
	 */
	public RepresentationType setCodeResponseDomain(String codeSchemeReference) {

		if (doc.getQuestionItem().getResponseDomain() != null) {
			doc.getQuestionItem().unsetResponseDomain();
		}
		RepresentationType rt = doc.getQuestionItem().addNewResponseDomain();
		CodeDomainType cdt = (CodeDomainType) rt.substitute(CodeDomainDocument.type.getDocumentElementName(),
				CodeDomainType.type);
		cdt.addNewCodeSchemeReference().addNewID().setStringValue(codeSchemeReference);
		return cdt;
	}

	/**
	 * Set Question Item Text Response Domain
	 * 
	 * @param maxLength
	 * @return RepresentationType
	 */
	public RepresentationType setTextResponseDomain(BigInteger maxLength) {

		if (doc.getQuestionItem().getResponseDomain() != null) {
			doc.getQuestionItem().unsetResponseDomain();
		}
		RepresentationType rt = doc.getQuestionItem().addNewResponseDomain();
		TextDomainType tdt = (TextDomainType) rt.substitute(TextDomainDocument.type.getDocumentElementName(),
				TextDomainType.type);
		tdt.setMaxLength(maxLength);
		return tdt;
	}

	/**
	 * Set Question Item Numeric Response Domain
	 * 
	 * @param type
	 *            (integer or float)
	 * @param decimalPosition
	 *            (not used if integer)
	 * @return RepresentationType
	 */
	public RepresentationType setNumericResponseDomain(NumericTypeCodeType.Enum type, BigInteger decimalPosition) {

		if (doc.getQuestionItem().getResponseDomain() != null) {
			doc.getQuestionItem().unsetResponseDomain();
		}
		RepresentationType rt = doc.getQuestionItem().addNewResponseDomain();
		NumericDomainType ndt = (NumericDomainType) rt.substitute(NumericDomainDocument.type.getDocumentElementName(),
				NumericDomainType.type);
		ndt.setType(type);
		if (type == NumericTypeCodeType.FLOAT) {
			if (decimalPosition != null) {
				ndt.setDecimalPositions(decimalPosition);
			}
		} else {
			if (ndt.getDecimalPositions() != null) {
				ndt.unsetDecimalPositions();
			}
		}
		return ndt;
	}

	/**
	 * Set new Question Item Response Domain
	 * 
	 * @param responseType
	 * @param value
	 * @return RepresentationType or null - if unsupported response type
	 */
	public RepresentationType setResponseDomain(RESPONSE_TYPES responseType, String value) {

		if (doc.getQuestionItem().getResponseDomain() != null) {
			doc.getQuestionItem().unsetResponseDomain();
		}
		RepresentationType rt = doc.getQuestionItem().addNewResponseDomain();
		if (responseType == RESPONSE_TYPES.UNDEFINED) {
			return rt;
		} else if (responseType == RESPONSE_TYPES.CODE) {
			CodeDomainType cdt = (CodeDomainType) rt.substitute(CodeDomainDocument.type.getDocumentElementName(),
					CodeDomainType.type);
			cdt.addNewCodeSchemeReference().addNewID().setStringValue(value);
			return cdt;
		} else if (responseType == RESPONSE_TYPES.TEXT) {
			TextDomainType tdt = (TextDomainType) rt.substitute(TextDomainDocument.type.getDocumentElementName(),
					TextDomainType.type);
			// TODO Support MaxLength:
			// tdt.setMaxLength(arg0);
			return tdt;
		} else if (responseType == RESPONSE_TYPES.NUMERIC) {
			NumericDomainType ndt = (NumericDomainType) rt.substitute(NumericDomainDocument.type
					.getDocumentElementName(), NumericDomainType.type);
			ndt.setType(null);
			return ndt;
		} else if (responseType == RESPONSE_TYPES.DATE) {
			// TODO Support Date
			return null;
		} else if (responseType == RESPONSE_TYPES.CATEGORY) {
			// TODO Support Category
			return null;
		} else if (responseType == RESPONSE_TYPES.GEOGRAPHIC) {
			// TODO Support Geographic
			return null;
		} else {
			// TODO Error handling
			return null;
		}
	}

	/**
	 * Get Question Item Document of Question Item.
	 * 
	 * @return QuestionItemDocument
	 */
	@Override
	public QuestionItemDocument getDocument() {
		return doc;
	}

	/**
	 * Validates the Question Item before it is saved. It e.g. checks if all
	 * mandatory attributes has been given.
	 * 
	 * @throws Exception
	 */
	public void validate() throws Exception {
		// Check if a Response Domain has been given
		RepresentationType rt = getResponseDomain();
		if (rt == null) {
			throw new Exception(Messages.getString("QuestionItem.mess.QuestionResponseTypeHasNotBeenSpecified")); //$NON-NLS-1$
		}
		String sn = rt.getClass().getSimpleName();
		if (sn.equals("CodeDomainTypeImpl")) {
			try {
				XmlBeansUtil.getTextOnMixedElement(rt);
			} catch (Exception e) {
				throw new Exception(Messages.getString("QuestionItem.mess.QuestionResponseCodeReferenceNotBeenSpecified")); //$NON-NLS-1$
			}
		} else if (sn.equals("TextDomainTypeImpl")) {
			// Nothing to check
		} else if (sn.equals("NumericDomainTypeImpl")) {
			// Nothing to check
		}
		
		// Check if Language has been given for all Question Texts
		DynamicTextType dynamicText = null;
		List<DynamicTextType> dynamicTextList = doc.getQuestionItem().getQuestionTextList();
		for (int i = 0; i < dynamicTextList.size(); i++) {
			dynamicText = dynamicTextList.get(i);
			if (dynamicText == null) {
				continue;
			}
			if (XmlBeansUtil.getXmlAttributeValue(dynamicText.xmlText(), "lang=\"") == null) {
				throw new Exception(Messages.getString("QuestionItem.mess.QuestionTextLanguageTypeHasNotBeenSpecified")); //$NON-NLS-1$
			}
		}
		return; // No error found:
	}

	@Override
	public void executeChange(Object value, Class<?> type) throws Exception {
		// Set Concept reference
		if (type.equals(ReferenceType.class)) {
			ReferenceType ref = getConceptReferenceType();
			ModelAccessor.setReference(ref, ((LightXmlObjectType) value));
		} else if (type.equals(ModelIdentifingType.Type_A.class)) {
			if (((String )value).length() == 0) {
				if (doc.getQuestionItem().getQuestionTextList().size() > 0) {
				doc.getQuestionItem().removeQuestionText(0);
				}
			} else {
				DynamicTextType questionText = (DynamicTextType) XmlBeansUtil.getLangElement(
						LanguageUtil.getDisplayLanguage(), getQuestionText());
				LiteralTextType lTextType = (LiteralTextType) questionText.getTextList().get(0)
						.substitute(LiteralTextDocument.type.getDocumentElementName(), LiteralTextType.type);
				XmlBeansUtil.setTextOnMixedElement(lTextType.getText(), (String) value);
			}
		} else {
			log.debug("******** Class type not supported: " + type + " ********");
		}
	}
}