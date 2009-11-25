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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

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
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.impl.QuestionItemTypeImpl;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.NumericTypeCodeType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.ReferenceType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.RepresentationType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.TextDomainType;
import org.ddialliance.ddieditor.ui.editor.question.ResponseTypeDetail.RESPONSE_TYPES;
import org.ddialliance.ddieditor.ui.model.Language;
import org.ddialliance.ddieditor.ui.model.Model;
import org.ddialliance.ddieditor.ui.view.Messages;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.ddialliance.ddiftp.util.xml.XmlBeansUtil;

public class QuestionItem extends Model {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, QuestionItem.class);
	private QuestionItemDocument questionItemDocument;
	private QuestionItemTypeImpl questionItemTypeImpl;
	private List<QuestionItemLiteralText> questionItemLiteralTextList = new ArrayList<QuestionItemLiteralText>();
	private String originalLanguageCode = null;

	/**
	 * Constructor
	 * 
	 * @param questionItemDocument
	 * @param parentId
	 * @param parentVersion
	 * @throws Exception
	 */
	public QuestionItem(QuestionItemDocument questionItemDocument, String parentId, String parentVersion)
			throws Exception {
		
		super(questionItemDocument.getQuestionItem().getId(), questionItemDocument.getQuestionItem().getVersion(),
				parentId, parentVersion);
		
		if (questionItemDocument == null) {
			// TODO Create new Question Item
			this.questionItemDocument = null;
		}
		this.questionItemDocument = questionItemDocument;
		this.questionItemTypeImpl = (QuestionItemTypeImpl) questionItemDocument.getQuestionItem();

		// Generate list of Question Item Literal Texts:
		// - all QuestionText and LiteralText elements with same language is
		// gathered in same QuestionItemLiteralText object.
		String[] languageCodes = Language.getLanguageCodes();
		for (int i = 0; i < languageCodes.length; i++) {
			String languageCode = languageCodes[i];
			StringBuilder str = new StringBuilder(256);
			boolean firstElement = true;

			List<DynamicTextType> dynamicTextTypeList = questionItemTypeImpl.getQuestionTextList();
			// Loop over Question Item Text list:
			for (Iterator iterator = dynamicTextTypeList.iterator(); iterator.hasNext();) {
				DynamicTextType dynamicTextType = (DynamicTextType) iterator.next();
				if (originalLanguageCode == null && !dynamicTextType.getTranslated()) {
					originalLanguageCode = dynamicTextType.getLang();
				}
				if (dynamicTextType.getLang().equals(languageCode)) {
					List<TextType> textTypeList = dynamicTextType.getTextList();
					// Loop over Literal Text list
					for (Iterator iterator2 = textTypeList.iterator(); iterator2.hasNext();) {
						TextType textType = (TextType) iterator2.next();
						if (!firstElement) {
							str.append("\n");
						} else {
							firstElement = false;
						}
						str.append(XmlBeansUtil.getTextOnMixedElement(textType));
					}
					questionItemLiteralTextList.add(new QuestionItemLiteralText(str.toString(), languageCode,
							dynamicTextType.getTranslated(), dynamicTextType.getTranslatable()));
				}
			}
		}

	}

	/**
	 * Get Original Language Code
	 * 
	 * @return String
	 */
	public String getOriginalLanguageCode() {
		if (originalLanguageCode == null) {
			return Language.getDefaultLanguageCode();
		}
		return originalLanguageCode;
	}

	/**
	 * Set Original Language Code
	 * 
	 * @param languageCode
	 */
	public void setOriginalLanguageCode(String languageCode) {
		originalLanguageCode = languageCode;
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
		List<ReferenceType> conceptReferenceList = questionItemTypeImpl.getConceptReferenceList();
		if (conceptReferenceList.size() == 1) {
			conceptRef = XmlBeansUtil.getTextOnMixedElement(conceptReferenceList.get(0));
			log.debug("ConceptRef: " + conceptRef);
		} else if (conceptReferenceList.size() > 1) {
			throw new Exception(Messages.getString("QuestionItem.mess.MultipleConceptReferencesNotSupported")); //$NON-NLS-1$
		}
		return conceptRef;
	}

	/**
	 * Set Concept reference
	 * 
	 * @param conceptRef
	 * @throws Exception
	 */
	public void setConceptRef(String conceptRef) throws Exception {

		if (questionItemTypeImpl.getConceptReferenceArray().length > 1) {
			throw new Exception(Messages.getString("QuestionItem.mess.MultipleConceptReferencesNotSupported")); //$NON-NLS-1$
		}
		if (questionItemTypeImpl.getConceptReferenceArray().length == 1) {
			questionItemTypeImpl.removeConceptReference(0);
		}
		questionItemTypeImpl.addNewConceptReference().addNewID().setStringValue(conceptRef);
	}

	/**
	 * Get text of Question Item corresponding to the given language code.
	 * 
	 * @param translated
	 * @param languageCode
	 *            - only used if translated = true
	 * @return String
	 * @throws Exception
	 */
	public String getText(boolean translated, String languageCode) throws Exception {

		for (Iterator iterator = questionItemLiteralTextList.iterator(); iterator.hasNext();) {
			QuestionItemLiteralText questionItemLiteralText = (QuestionItemLiteralText) iterator.next();

			if (translated == false && questionItemLiteralText.getTranslated() == translated) {
				return questionItemLiteralText.getText();
			}
			if (translated == true && questionItemLiteralText.getTranslated() == translated
					&& questionItemLiteralText.getLanguageCode().equals(languageCode)) {
				return questionItemLiteralText.getText();
			}
		}
		return "";
	}

	/**
	 * Get QuestionItemLiteralText of Question Item corresponding to the given language code.
	 * 
	 * @param translated
	 * @param languageCode
	 *            - only used if translated = true
	 * @return QuestionItemLiteralText
	 * @throws Exception
	 */
	public QuestionItemLiteralText getQuestionItemLiteralText(boolean translated, String languageCode) throws Exception {

		for (Iterator iterator = questionItemLiteralTextList.iterator(); iterator.hasNext();) {
			QuestionItemLiteralText questionItemLiteralText = (QuestionItemLiteralText) iterator.next();

			if (translated == false && questionItemLiteralText.getTranslated() == translated) {
				return questionItemLiteralText;
			}
			if (translated == true && questionItemLiteralText.getTranslated() == translated
					&& questionItemLiteralText.getLanguageCode().equals(languageCode)) {
				return questionItemLiteralText;
			}
		}
		return null;
	}

	/**
	 * 
	 * Create/Update Question Item text string corresponding to the given
	 * language
	 * 
	 * Note: All GUI text lines are inserted into same Question Text element
	 * with one Literal Text sub-element per text line.
	 * 
	 * @param newText
	 * @param languageCode
	 */
	public void setText(String newText, String languageCode) {
		boolean found = false;

		for (Iterator iterator = questionItemLiteralTextList.iterator(); iterator.hasNext();) {
			QuestionItemLiteralText questionItemLiteralText = (QuestionItemLiteralText) iterator.next();

			if (questionItemLiteralText.getLanguageCode().equals(languageCode)) {
				questionItemLiteralText.setText(newText);
				found = true;
			}
		}

		if (!found) {
			// QuestionItemLiteralText not found - create it:
			// TODO Translatable control by check-flag on Translated Question
			// Tab
			this.addText(newText, languageCode, false, false);
		}
	}

	/**
	 * 
	 * Update QuestionItemDocument with Question Item Text elements. This method
	 * has to be called before the Question Item is saved to the DBXML.
	 * 
	 */
	public void update() {

		// Remove old Question Text elements:
		int length = questionItemTypeImpl.getQuestionTextList().size();
		for (int i = 0; i < length; i++) {
			questionItemTypeImpl.removeQuestionText(0);
		}

		// Loop over Question Text list:
		for (Iterator iterator = this.questionItemLiteralTextList.iterator(); iterator.hasNext();) {
			QuestionItemLiteralText questionItemLiteralText = (QuestionItemLiteralText) iterator.next();

			// Create a Question Text element:
			DynamicTextType questionText = questionItemTypeImpl.addNewQuestionText();
			questionText.setLang(questionItemLiteralText.getLanguageCode());
			questionText.setTranslatable(questionItemLiteralText.getTranslatable());
			questionText.setTranslated(questionItemLiteralText.getTranslated());

			// Add one Literal Text element per GUI text line
			String newText = questionItemLiteralText.getText();
			StringTokenizer st = new StringTokenizer(newText, "\n");
			while (st.hasMoreTokens()) {
				String substr = (String) st.nextElement();
				TextType textType = questionText.addNewText();
				LiteralTextType literalText = (LiteralTextType) textType.substitute(LiteralTextDocument.type
						.getDocumentElementName(), LiteralTextType.type);
				// TODO 3.1 upgrade
				//literalText.setText(substr);
			}
		}
		return;
	}

	/**
	 * Add QuestionItemLiteralText element to questionItemLiteralTextList.
	 * 
	 * @param questionItemLiteralText
	 */
	public QuestionItemLiteralText addText(String newText, String languageCode, boolean translated, boolean translatable) {
		QuestionItemLiteralText questionItemLiteralText = new QuestionItemLiteralText(newText, languageCode,
				translated, translatable); 
		questionItemLiteralTextList.add(questionItemLiteralText);
		return questionItemLiteralText;

	}

	/**
	 * Remove QuestionItemLiteralText list element from
	 * questionItemLiteralTextList;
	 * 
	 * @param questionItemLiteralText
	 */
	public void removeText(QuestionItemLiteralText questionItemLiteralText) {
		int i = 0;
		for (Iterator iterator = questionItemLiteralTextList.iterator(); iterator.hasNext();) {
			QuestionItemLiteralText questionItemLiteralText1 = (QuestionItemLiteralText) iterator.next();

			if (questionItemLiteralText.getLanguageCode().equals(questionItemLiteralText1.getLanguageCode())) {
				questionItemLiteralTextList.remove(i);
				return;
			}
			i++;
		}
	}

	/**
	 * Get Response Domain of Question Item.
	 * 
	 * @return RepresentationType
	 */
	public RepresentationType getResponseDomain() {
		return questionItemTypeImpl.getResponseDomain();
	}
	
	/**
	 * Set Question Item Code Response Domain
	 * 
	 * @param codeSchemeReference
	 * @return RepresentationType
	 */
	public RepresentationType setCodeResponseDomain(String codeSchemeReference) {

		if (questionItemTypeImpl.getResponseDomain() != null) {
			questionItemTypeImpl.unsetResponseDomain();
		}
		RepresentationType rt = questionItemTypeImpl.addNewResponseDomain();
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

		if (questionItemTypeImpl.getResponseDomain() != null) {
			questionItemTypeImpl.unsetResponseDomain();
		}
		RepresentationType rt = questionItemTypeImpl.addNewResponseDomain();
		TextDomainType tdt = (TextDomainType) rt.substitute(TextDomainDocument.type.getDocumentElementName(),
				TextDomainType.type);
		tdt.setMaxLength(maxLength);
		return tdt;
	}

	/**
	 * Set Question Item Numeric Response Domain
	 * 
	 * @param type (integer or float)
	 * @param decimalPosition (not used if integer)
	 * @return RepresentationType
	 */
	public RepresentationType setNumericResponseDomain(NumericTypeCodeType.Enum type, BigInteger decimalPosition) {

		if (questionItemTypeImpl.getResponseDomain() != null) {
			questionItemTypeImpl.unsetResponseDomain();
		}
		RepresentationType rt = questionItemTypeImpl.addNewResponseDomain();
		NumericDomainType ndt = (NumericDomainType) rt.substitute(NumericDomainDocument.type
				.getDocumentElementName(), NumericDomainType.type);
		ndt.setType(type);
		if (type == NumericTypeCodeType.FLOAT) {
			ndt.setDecimalPositions(decimalPosition);
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

		if (questionItemTypeImpl.getResponseDomain() != null) {
			questionItemTypeImpl.unsetResponseDomain();
		}
		RepresentationType rt = questionItemTypeImpl.addNewResponseDomain();
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
			ndt.setDecimalPositions(null);
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

//	/**
//	 * Get ID of Question Item.
//	 * 
//	 * @return ID string.
//	 */
//	public String getId() {
//		return questionItemTypeImpl.getId();
//	}
//
//	public boolean getIsVersionable() {
//		return questionItemTypeImpl.getIsVersionable();
//	}
//
//	/**
//	 * Get version of Question Item.
//	 * 
//	 * @return version string
//	 */
//	public String getVersion() {
//		return questionItemTypeImpl.getVersion();
//	}

	/**
	 * Get Question Item Document of Question Item.
	 * 
	 * @return QuestionItemDocument
	 */
	@Override
	public QuestionItemDocument getDocument() {
		return questionItemDocument;
	}

	/**
	 * Validates the Question Item before it is saved. It e.g. checks if all
	 * mandatory attributes has been given.
	 * 
	 * @throws Exception
	 */
	public void validate() throws Exception {
		log.debug("Question Item validation performed");

		// Check if a Response Domain has been given
		if (questionItemTypeImpl.getResponseDomain() == null
				|| questionItemTypeImpl.getResponseDomain().getClass().getSimpleName().equals("RepresentationTypeImpl")) {
			throw new Exception(Messages.getString("QuestionItem.mess.QuestionResponseTypeHasNotBeenSpecified")); //$NON-NLS-1$
		}
		// Check if Language has been given for all Question Texts
		for (Iterator iterator = questionItemLiteralTextList.iterator(); iterator.hasNext();) {
			QuestionItemLiteralText questionItemLiteralText = (QuestionItemLiteralText) iterator.next();
			if (questionItemLiteralText.getText().length() > 0 && questionItemLiteralText.getLanguageCode().length() == 0) {
				throw new Exception(Messages.getString("QuestionItem.mess.QuestionTextLanguageTypeHasNotBeenSpecified")); //$NON-NLS-1$
			}
		}

		return; // No error found:
	}

//	@Override
//	public XmlObject getDocument() throws DDIFtpException {
//		return questionItemDocument;
//	}

}