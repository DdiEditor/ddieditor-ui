package org.ddialliance.ddieditor.ui.model;

import java.util.Iterator;
import java.util.List;

import org.apache.xmlbeans.SchemaType;
import org.ddialliance.ddi_3_0.xml.xmlbeans.datacollection.DynamicTextType;
import org.ddialliance.ddi_3_0.xml.xmlbeans.datacollection.QuestionItemDocument;
import org.ddialliance.ddi_3_0.xml.xmlbeans.datacollection.QuestionSchemeDocument;
import org.ddialliance.ddi_3_0.xml.xmlbeans.datacollection.TextType;
import org.ddialliance.ddi_3_0.xml.xmlbeans.datacollection.impl.QuestionSchemeTypeImpl;
import org.ddialliance.ddi_3_0.xml.xmlbeans.reusable.LabelType;
import org.ddialliance.ddi_3_0.xml.xmlbeans.reusable.StructuredStringType;
import org.ddialliance.ddi_3_0.xml.xmlbeans.reusable.impl.StructuredStringTypeImpl;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.model.lightxmlobject.impl.LightXmlObjectTypeImpl;
import org.ddialliance.ddieditor.ui.dbxml.Util;
import org.ddialliance.ddieditor.ui.view.Messages;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.w3.x1999.xhtml.H1Document;
import org.w3.x1999.xhtml.H1Type;
import org.w3.x1999.xhtml.impl.H1TypeImpl;

public class QuestionScheme {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, QuestionItem.class);

	private QuestionSchemeDocument questionSchemeDocument;
	private QuestionSchemeTypeImpl questionSchemeTypeImpl;
	private String parentId;
	private String parentVersion;
	
	/**
	 * Constructor
	 * 
	 * @param questionSchemeDocument
	 * @param parentId
	 * @param parentVersion
	 * @throws Exception
	 */
	public QuestionScheme(QuestionSchemeDocument questionSchemeDocument, String parentId, String parentVersion)
			throws Exception {
		
		if (questionSchemeDocument == null) {
			// TODO Create new Question Scheme
			this.questionSchemeDocument = null;
		}
		this.questionSchemeDocument = questionSchemeDocument;
		this.questionSchemeTypeImpl = (QuestionSchemeTypeImpl) questionSchemeDocument.getQuestionScheme();
		this.parentId = parentId;
		this.parentVersion = parentVersion;
	}
	
	/**
	 * Get Question Scheme Document of Question Scheme.
	 * 
	 * @return QuestionSchemeDocument
	 */
	public QuestionSchemeDocument getQuestionSchemeDocument() {
		return questionSchemeDocument;
	}
	
	/**
	 * Get ID of Question Scheme.
	 * 
	 * @return ID string.
	 */
	public String getId() {
		return questionSchemeTypeImpl.getId();
	}

	/**
	 * Get version of Question Scheme.
	 * 
	 * @return version string
	 */
	public String getVersion() {
		return questionSchemeTypeImpl.getVersion();
	}

	
	/**
	 * Set Parent ID.
	 * 
	 * @param String
	 *            parentId
	 */
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	/**
	 * Get Parent ID.
	 * 
	 * @return String
	 */
	public String getParentId() {
		return this.parentId;
	}

	/**
	 * Set Parent version
	 * 
	 * @param String
	 *            parentVersion
	 */
	public void setParentVersion(String parentVersion) {
		this.parentVersion = parentVersion;
	}

	/**
	 * Get Parent version
	 * 
	 * @return String
	 */
	public String getParentVersion() {
		return this.parentVersion;
	}

	/**
	 * Get label of Question Scheme.
	 * 
	 * @return String
	 * @throws Exception
	 */
	public String getLabel(Language language) throws Exception {
		LabelType labelType;

		// Get Label corresponding to language
		int length = questionSchemeTypeImpl.getLabelList().size();
		for (int i = 0; i < length; i++) {
			labelType = questionSchemeTypeImpl.getLabelArray(i);
			if (labelType.getLang().equals(language)) {
				return labelType.getStringValue();
			}
		}
		log.error("*** Question Scheme Label of given Language <"+language+"> not found ***");
		return "";
	}

	/**
	 * Set label of Question Scheme.
	 * 
	 * @param string
	 * @return Void
	 */
	public void setLabel(String string, Language language) {
		LabelType labelType;

		// Remove label corresponding to language
		int length = questionSchemeTypeImpl.getLabelList().size();
		for (int i = 0; i < length; i++) {
			labelType = questionSchemeTypeImpl.getLabelArray(i);
			if (labelType.getLang().equals(language)) {
				labelType.setStringValue(string);
				return;
			}
		}

		labelType = questionSchemeTypeImpl.addNewLabel();
		labelType.setTranslated(false);
		// TODO Get default language
		labelType.setLang("da");
		labelType.setStringValue(string);
	}
	
	/**
	 * Get Original Label of Question Scheme.
	 * 'Original' means not translated.
	 * 
	 * @return String
	 * @throws Exception
	 */
	public String getLabel() {
		LabelType labelType;

		// Get Label corresponding to language
		int length = questionSchemeTypeImpl.getLabelList().size();
		for (int i = 0; i < length; i++) {
			labelType = questionSchemeTypeImpl.getLabelArray(i);
			if (!labelType.getTranslated()) {
				return labelType.getStringValue();
			}
		}
		log.error("*** Question Scheme Label of 'Original' Language not found ***");
		return "";
	}

	/**
	 * Set Original Label of Question Scheme.
	 * 'Original' means not translated.
	 * 
	 * @param string
	 * @return Void
	 */
	public void setLabel(String string) {
		LabelType labelType;

		// Remove label corresponding to language
		int length = questionSchemeTypeImpl.getLabelList().size();
		for (int i = 0; i < length; i++) {
			labelType = questionSchemeTypeImpl.getLabelArray(i);
			if (!labelType.getTranslated()) {
				labelType.setStringValue(string);
				return;
			}
		}
		labelType = questionSchemeTypeImpl.addNewLabel();
		labelType.setTranslated(false);
		// TODO Get default language
		labelType.setLang("da");
		labelType.setStringValue(string);
	}
	
	/**
	 * Get Description of Question Scheme.
	 * 
	 * @return String
	 */
	public String getDescr(Language language) {

		StructuredStringType descriptionType;

		// Get Description corresponding to language
		int length = questionSchemeTypeImpl.getDescriptionList().size();
		for (int i = 0; i < length; i++) {
			descriptionType = questionSchemeTypeImpl.getDescriptionArray(i);
			if (descriptionType.getLang().equals(language)) {
				return questionSchemeTypeImpl.getDescriptionArray(i).toString();
			}
		}
		log.error("*** Question Scheme Description of given Language <"+language+"> not found ***");
		return "";
	}

	/**
	 * Set Description of Question Scheme.
	 * 
	 * @param string
	 * @return Void
	 */
	public void setDescr(String string, Language language) {
		StructuredStringType descriptionType;
		
		// Remove description corresponding to language
		int length = questionSchemeTypeImpl.getDescriptionList().size();
		for (int i = 0; i < length; i++) {
			descriptionType = questionSchemeTypeImpl.getDescriptionArray(i);
			if (descriptionType.getLang().equals(language)) {
				// TODO save text.
//				descriptionType.setText(string);
				return;
			}
		}
		
		descriptionType = questionSchemeTypeImpl.addNewDescription();
		descriptionType.setTranslated(false);
		// TODO Get default language
		descriptionType.setLang("da");
		// TODO save text.
		// descriptionType.setStringValue(string);
	}
	
	/**
	 * Get Original Description of Question Scheme.
	 * Original means not translated.
	 * 
	 * @return String
	 */
	public String getDescr() {

		StructuredStringType descriptionType;

		// Get Description corresponding to language
		int length = questionSchemeTypeImpl.getDescriptionList().size();
		for (int i = 0; i < length; i++) {
			descriptionType = questionSchemeTypeImpl.getDescriptionArray(i);
			if (!descriptionType.getTranslated()) {
//				return descriptionType.getText();
				return "";
			}
		}
		log.error("*** Question Scheme Original Description not found ***");
		return "";
	}

	/**
	 * Set Original Description of Question Scheme.
	 * Original means not translated.
	 * 
	 * @param string
	 * @return Void
	 */
	public void setDescr(String string) {
		StructuredStringType descriptionType;
		
		// Remove description corresponding to language
		int length = questionSchemeTypeImpl.getDescriptionList().size();
		for (int i = 0; i < length; i++) {
			descriptionType = questionSchemeTypeImpl.getDescriptionArray(i);
			if (!descriptionType.getTranslated()) {
				// TODO Assign text
//				StructuredStringType stringType = new StructuredStringTypeImpl(null);
//				stringType.addNewH1().setTitle(string);
//				descriptionType.setH1Array(0, (H1Type) stringType);
				return;
			}
		}
		descriptionType = questionSchemeTypeImpl.addNewDescription();
		descriptionType.setTranslated(false);
		// TODO Get default language
		descriptionType.setLang("da");
		// TODO save text.
		// descriptionType.setStringValue(string);
	}
	
	/**
	 * Validates the Question Scheme before it is saved. It e.g. checks if all
	 * mandatory attributes has been given.
	 * 
	 * @return boolean - true if no error
	 * @throws Exception
	 */
	public void validate() throws Exception {
		log.debug("Question Scheme validation performed");

		// No error found:
		return;
	}

}
