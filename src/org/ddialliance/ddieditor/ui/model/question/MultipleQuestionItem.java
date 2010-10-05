package org.ddialliance.ddieditor.ui.model.question;

import java.util.ArrayList;
import java.util.List;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.aspectj.weaver.ReferenceTypeDelegate;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.ConceptReferenceDocument;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.DynamicTextType;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.LiteralTextDocument;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.LiteralTextType;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.MultipleQuestionItemDocument;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.QuestionTextDocument;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.TextType;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.impl.ConceptReferenceDocumentImpl;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.impl.DynamicTextTypeImpl;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.LabelDocument;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.ReferenceType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.StructuredStringType;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.persistenceaccess.maintainablelabel.MaintainableLabelQueryResult;
import org.ddialliance.ddieditor.persistenceaccess.maintainablelabel.MaintainableLabelUpdateElement;
import org.ddialliance.ddieditor.ui.model.Model;
import org.ddialliance.ddieditor.ui.model.ModelAccessor;
import org.ddialliance.ddieditor.ui.model.ModelIdentifingType;
import org.ddialliance.ddieditor.ui.util.LanguageUtil;
import org.ddialliance.ddieditor.util.XmlObjectUtil;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.ReflectionUtil;
import org.ddialliance.ddiftp.util.Translator;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.ddialliance.ddiftp.util.xml.XmlBeansUtil;

public class MultipleQuestionItem extends Model {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, MultipleQuestionItem.class);
//	private MultipleQuestionItemDocument doc;
	
	private MaintainableLabelQueryResult maintainableLabelQueryResult;
	private List<MaintainableLabelUpdateElement> maintainableUpdateTexts = new ArrayList<MaintainableLabelUpdateElement>();
	private List<MaintainableLabelUpdateElement> maintainableUpdateConceptRefs = new ArrayList<MaintainableLabelUpdateElement>();
	private List<DynamicTextType> questionTexts = new ArrayList<DynamicTextType>(); // aka updates
	private List<ReferenceType> conceptReferences = new ArrayList<ReferenceType>();

	private XmlOptions xmlOptions = new XmlOptions();

	/**
	 * Constructor
	 * 
	 * @param id
	 * @param version
	 * @param parentId
	 * @param parentVersion
	 * @param maintainableLabelQueryResult
	 * @throws XmlException
	 * @throws DDIFtpException
	 */
	public MultipleQuestionItem(String id, String version, String parentId,
			String parentVersion, String agency,
			MaintainableLabelQueryResult maintainableLabelQueryResult)
			throws DDIFtpException {
		
		// init super
		super(maintainableLabelQueryResult.getId(), maintainableLabelQueryResult.getVersion(), parentId, parentVersion,
				agency);

		// init maintainable labels
		this.maintainableLabelQueryResult = maintainableLabelQueryResult;
		if (maintainableLabelQueryResult != null) {
			// get QuestionTextDocument(s) from Query Result
			XmlObject[] xmlObjects = maintainableLabelQueryResult.getSubElement("QuestionText");
			for (int i = 0; i < xmlObjects.length; i++) {
				maintainableUpdateTexts.add(new MaintainableLabelUpdateElement(((QuestionTextDocument)xmlObjects[i]).getQuestionText(), null /* NOP */));
				questionTexts.add((DynamicTextType) ((QuestionTextDocument)xmlObjects[i]).getQuestionText());
			}

			// get ConceptReference(s) from Query Result
			xmlObjects = maintainableLabelQueryResult.getSubElement("ConceptReference");
			for (int i = 0; i < xmlObjects.length; i++) {
				// - store as StructuredString
				maintainableUpdateConceptRefs.add(new MaintainableLabelUpdateElement(((ConceptReferenceDocumentImpl)xmlObjects[i]).getConceptReference(), null /* NOP */));				
				conceptReferences.add(((ConceptReferenceDocumentImpl)xmlObjects[i]).getConceptReference());
			}
		}

		xmlOptions.setSaveOuter();
		xmlOptions.setSaveAggressiveNamespaces();
	}

	/**
	 * 
	 * Get Concept reference
	 * 
	 * @return ReferenceType
	 */
	public ReferenceType getConceptReferenceType() {
		if (conceptReferences.size() > 0) {
			return conceptReferences.get(0);
		}
		return null;
	}

	/**
	 * 
	 * Get Concept reference
	 * 
	 * @return ReferenceType[]
	 */
	public ReferenceType[] getConceptREeferenceTypeAsArray() {
		if (conceptReferences.size() > 0) {
		return conceptReferences.toArray(new ReferenceType[] {});
		}
		return new ReferenceType[0];
	}
	
	/**
	 * Get Question Items
	 * 
	 * @return list of dynamic texts
	 */
	public List<DynamicTextType> getQuestionText() throws DDIFtpException {
		return questionTexts;
	}

	/**
	 * Get Question Items as array
	 * 
	 * @return DynamicTexts[]
	 */
	public DynamicTextType[] getQuestionTextAsArray() throws DDIFtpException {
		return questionTexts.toArray(new DynamicTextType[] {});
	}

	/**
	 * Get Question Item Document of Question Item.
	 * 
	 * @return MultipleQuestionItemDocument
	 * @throws DDIFtpException 
	 */
	@Override
	public MultipleQuestionItemDocument getDocument() throws DDIFtpException {
		XmlObject result = XmlObjectUtil.createXmlObjectDocument(maintainableLabelQueryResult.getLocalName());
		XmlObject type = XmlObjectUtil.addXmlObjectType(result);

		// identification
		try {
			// id
			ReflectionUtil.invokeMethod(type, "setId", false, maintainableLabelQueryResult.getId());
			// version
			if (maintainableLabelQueryResult.getVersion() != null
					&& (!maintainableLabelQueryResult.getVersion().equals(""))) {
				ReflectionUtil.invokeMethod(type, "setVersion", false, maintainableLabelQueryResult.getVersion());
			}
			// agency
			if (maintainableLabelQueryResult.getAgency() != null
					&& (!maintainableLabelQueryResult.getAgency().equals(""))) {
				ReflectionUtil.invokeMethod(type, "setAgency", false, maintainableLabelQueryResult.getAgency());
			}

			// concept reference
			ReflectionUtil.invokeMethod(type, "setConceptReferenceArray", false, new Object[] { getConceptREeferenceTypeAsArray() });
		} catch (Exception e) {
			throw new DDIFtpException(e);
		}

		// question text
		try {
			ReflectionUtil.invokeMethod(type, "setQuestionTextArray", false, new Object[] { getQuestionTextAsArray() });
		} catch (Exception e) {
			throw new DDIFtpException(e);
		}
		return (MultipleQuestionItemDocument) result;
	}

	/**
	 * Validates the Question Item before it is saved. It e.g. checks if all
	 * mandatory attributes has been given.
	 * 
	 * @throws Exception
	 */
	public void validate() throws Exception {
		
		return; // No error found:
	}

	@Override
	public void executeChange(Object value, Class<?> type) throws Exception {
		if (type.equals(ReferenceType.class)) {
			// Set Concept Reference
			ReferenceType ref = getConceptReferenceType();
			if (ref == null) {
				ref = ConceptReferenceDocument.Factory.newInstance().addNewConceptReference();
				ref.addNewID().setStringValue(XmlBeansUtil.getXmlAttributeValue(((XmlObject)value).xmlText(), "id=\""));
				conceptReferences.add(ref);
				maintainableUpdateConceptRefs.add(new MaintainableLabelUpdateElement(ref,
						MaintainableLabelUpdateElement.NEW));
			} else {
				ModelAccessor.setReference(ref, ((LightXmlObjectType) value));
			}
		} else if (type.equals(ModelIdentifingType.Type_A.class)) {
			// Set Question Text
			DynamicTextType questionText = (DynamicTextType) XmlBeansUtil.getLangElement(LanguageUtil
					.getDisplayLanguage(), getQuestionText());
			if (questionText == null) {
				if (value != null && !value.equals("")) {
					questionText = QuestionTextDocument.Factory.newInstance().addNewQuestionText();
					questionText.setTranslated(false);
					questionText.setTranslatable(true);
					questionText.setLang(LanguageUtil.getOriginalLanguage());
					TextType textType = questionText.addNewText();
					LiteralTextType lTextType = (LiteralTextType) textType.substitute(LiteralTextDocument.type
							.getDocumentElementName(), LiteralTextType.type);
					XmlBeansUtil.setTextOnMixedElement(lTextType.addNewText(), (String) value);
					questionTexts.add((DynamicTextType) questionText);
					maintainableUpdateTexts.add(new MaintainableLabelUpdateElement(questionText,
							MaintainableLabelUpdateElement.NEW));
				}
			} else {
				LiteralTextType lTextType = (LiteralTextType) questionText.getTextList().get(0).substitute(
						LiteralTextDocument.type.getDocumentElementName(), LiteralTextType.type);
				XmlBeansUtil.setTextOnMixedElement(lTextType.getText(), (String) value);
			}
		} else {
			log.debug("******** Class type not supported: " + type + " ********");
		}
	}

}
