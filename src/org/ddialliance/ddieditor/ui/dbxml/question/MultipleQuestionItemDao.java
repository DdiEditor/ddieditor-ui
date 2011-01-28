package org.ddialliance.ddieditor.ui.dbxml.question;

import java.util.ArrayList;
import java.util.List;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlOptions;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.MultipleQuestionItemDocument;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.persistenceaccess.maintainablelabel.MaintainableLabelQueryResult;
import org.ddialliance.ddieditor.persistenceaccess.maintainablelabel.MaintainableLabelUpdateElement;
import org.ddialliance.ddieditor.ui.dbxml.IDao;
import org.ddialliance.ddieditor.ui.model.IModel;
import org.ddialliance.ddieditor.ui.model.LabelDescriptionScheme;
import org.ddialliance.ddieditor.ui.model.question.MultipleQuestionItem;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.ddialliance.ddiftp.util.xml.XmlBeansUtil;

/**
 * Multi Question Item.
 * 
 */
/*
 * $Author: ddadak $ $Date: 2010-03-24 11:59:05 +0100 (Wed, 24 Mar 2010) $
 * $Revision: 1042 $
 */

public class MultipleQuestionItemDao implements IDao {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, MultipleQuestionItemDao.class);

	/**
	 * Get Light MultipleQuestionItems List of the given Question Scheme
	 * 
	 * @param parentQuestionScheme
	 * @return List<LightXmlObjectType>
	 * @throws Exception
	 */
	public List<LightXmlObjectType> getLightXmlObject(LightXmlObjectType parentQuestionScheme) throws Exception {

		return getLightXmlObject("", "", parentQuestionScheme.getId(), parentQuestionScheme.getParentVersion());
	}

	/**
	 * Get Light MultipleQuestionItems of the given Question Scheme by Id.
	 * 
	 * @param parentQuestionScheme
	 * @param id
	 * @param version
	 * @return
	 * @throws Exception
	 */
	public List<LightXmlObjectType> getLightXmlObject(String id, String version, String parentId, String parentVersion)
			throws Exception {

		log.debug("MultipleQuestionItems.getItemsLight() - parent: " + parentId + "/" + parentVersion);

		List<LightXmlObjectType> lightXmlObjectTypeList = DdiManager.getInstance().getMultipleQuestionItemsLight(id,
				version, parentId, parentVersion).getLightXmlObjectList().getLightXmlObjectList();

		if (log.isDebugEnabled()) {
			boolean numbers;
			if (numbers = lightXmlObjectTypeList.size() != 1) {
				log.error("Unexpected numbers of Multiple Question Items:" + numbers);
			}
			for (LightXmlObjectType l : lightXmlObjectTypeList) {
				log.debug("Multiple Question Item Id: " + l.getId());
				if (l.getLabelList().size() > 0) {
					XmlCursor xmlCursor = l.getLabelArray(0).newCursor();
					xmlCursor.toLastAttribute();
					xmlCursor.toNextToken();
					String result = xmlCursor.getChars();
					xmlCursor.dispose();
					log.debug("Multiple Question Item Label: " + result);
				}
			}
		}

		return lightXmlObjectTypeList;
	}

	/**
	 * Create MultipleQuestionItem object
	 * 
	 * @param id
	 *            Identification
	 * @param version
	 *            Version
	 * @param parentId
	 *            Parent Identification
	 * @param parentVersion
	 *            Parent version
	 * @param selectElementType
	 * @return QuestionItem
	 * @throws Exception
	 */
	public MultipleQuestionItem create(String id, String version, String parentId, String parentVersion)
			throws Exception {
		log.debug("MultipleQuestionItems.create()");

		MaintainableLabelQueryResult maintainableLabelQueryResult = LabelDescriptionScheme
				.createLabelDescriptionScheme("MultipleQuestionItem");

		return new MultipleQuestionItem(maintainableLabelQueryResult.getId(),
				maintainableLabelQueryResult.getVersion(), parentId, parentVersion, maintainableLabelQueryResult
						.getAgency(), maintainableLabelQueryResult);
	}

	/**
	 * Get MultipleQuestionItem
	 * 
	 * @param id
	 *            Identification
	 * @param version
	 *            Version
	 * @param parentId
	 *            Parent Identification
	 * @param parentVersion
	 *            Parent version
	 * @return QuestionItem
	 * @throws Exception
	 */
	public MultipleQuestionItem getModel(String id, String version, String parentId, String parentVersion)
			throws Exception {

		MaintainableLabelQueryResult maintainableLabelQueryResult = DdiManager.getInstance()
				.getMultipleQuestionItemLabel(id, version, parentId, parentVersion);

		return new MultipleQuestionItem(id, version, parentId, parentVersion, maintainableLabelQueryResult.getAgency(),
				maintainableLabelQueryResult);

		// MultipleQuestionItemDocument doc =
		// DdiManager.getInstance().getMultipleQuestionItem(id,
		// version, parentId, parentVersion);
		//
		// return doc == null ? null : new MultipleQuestionItem(doc, parentId,
		// parentVersion);
	}

	/**
	 * Create new DBXML Multiple Question Item
	 * 
	 * @param multipleQuestionItem
	 *            multiple question item instance
	 * @param parentId
	 *            Id. of Question Scheme
	 * @param parentVersion
	 *            Version of Question Scheme
	 * @throws DDIFtpException
	 */
	public void create(IModel multipleQuestionItem) throws DDIFtpException {
		log.debug("Create DBXML Multiple Question Item:\n" + multipleQuestionItem.getDocument() + " Parent Id: "
				+ multipleQuestionItem.getParentId());
		MultipleQuestionItemDocument doc = (MultipleQuestionItemDocument) multipleQuestionItem.getDocument();
		if (doc.getMultipleQuestionItem().getSubQuestions() == null) {
			doc.getMultipleQuestionItem().addNewSubQuestions();
		}
		try {
			DdiManager.getInstance().createElement(doc, multipleQuestionItem.getParentId(),
					multipleQuestionItem.getParentVersion(), "QuestionScheme");
		} catch (DDIFtpException e) {
			log.error("Create DBXML Multiple Question Item error: " + e.getMessage());

			throw new DDIFtpException(e.getMessage());
		}
	}
	
	private void cleanQuestionTexts(String id, String version, String parentId, String parentVersion) throws DDIFtpException {
		
		List<MaintainableLabelUpdateElement> maintainableLabelUpdateElementList = new ArrayList<MaintainableLabelUpdateElement>();
		MaintainableLabelQueryResult maintainableLabelQueryResult = DdiManager
		.getInstance().getMultipleQuestionItemLabel(id, version, parentId, parentVersion);

		MaintainableLabelUpdateElement questionTextUpdateElement = null;
		int nbrQuestionTextList = maintainableLabelQueryResult.getSubElement("QuestionText").length;
		// loop over all QuestionTextList elements:
		for (int i = 0; i < nbrQuestionTextList; i++) {
			questionTextUpdateElement = new MaintainableLabelUpdateElement();
			questionTextUpdateElement.setLocalName("QuestionText");
			questionTextUpdateElement.setCrudValue((i+1) * -1);
			questionTextUpdateElement.setValue("");
			maintainableLabelUpdateElementList.add(questionTextUpdateElement);
		}
//		System.out.println("Query Result length: "+maintainableLabelQueryResult.getSubElement("QuestionText").length);
//		System.out.println("Label Update Element list size: "+maintainableLabelUpdateElementList.size());
//		System.out.println("Query Result(0): "+maintainableLabelQueryResult.getSubElement("QuestionText")[0]);
//		System.out.println("Query Result(1): "+maintainableLabelQueryResult.getSubElement("QuestionText")[1]);
//		System.out.println("Label Update Element list: "+maintainableLabelUpdateElementList);
		if (maintainableLabelQueryResult.getSubElement("QuestionText").length != maintainableLabelUpdateElementList.size()) {
			throw new DDIFtpException("Internal error:\n"+maintainableLabelQueryResult.getSubElement("QuestionText").length+"\n"+
					maintainableLabelUpdateElementList.size());
		}
		if (maintainableLabelUpdateElementList.size() > 0) {
			DdiManager.getInstance().updateMaintainableLabel(maintainableLabelQueryResult, maintainableLabelUpdateElementList);
		}
	}
	
	private Object genCrudValue(int oldLength, int NewLength, int position) {
		if (oldLength < 0 || NewLength < 0) {
			log.error("Internal genCrudValue error: Negative length");
			return null;
		}
		if (oldLength == 0) {
			if (NewLength > 0) {
				return (MaintainableLabelUpdateElement.NEW);
			}
			return null; // NOP
		}
		if (NewLength == 0) {
			return (position * -1); // Delete
		}
		return (position);
	}

	/**
	 * 
	 * Update DBXML Multiple Question Item corresponding to the given
	 * MultipleQuestionItem instance
	 * 
	 * @param multipleQuestionItem
	 *            MultipleQuestionItem instance
	 * @throws DDIFtpException
	 */
	public void update(IModel multiplequestionItem) throws DDIFtpException {
		log.debug("Update DBXML Multiple Question Item:\n" + multiplequestionItem.getDocument());
		
		// Remove all Multiple QuestionTexts
		cleanQuestionTexts(multiplequestionItem.getId(), multiplequestionItem.getVersion(), 
				multiplequestionItem.getParentId(), multiplequestionItem.getParentVersion());
		
		MaintainableLabelQueryResult maintainableLabelQueryResult = DdiManager
		.getInstance().getMultipleQuestionItemLabel(multiplequestionItem.getId(), multiplequestionItem.getVersion(), 
				multiplequestionItem.getParentId(), multiplequestionItem.getParentVersion());
		
		List<MaintainableLabelUpdateElement> maintainableLabelUpdateElementList = new ArrayList<MaintainableLabelUpdateElement>();

		XmlOptions xmlOptions = new XmlOptions();
        xmlOptions.setSaveOuter();
        xmlOptions.setSaveAggressiveNamespaces();

		MultipleQuestionItemDocument doc = (MultipleQuestionItemDocument)multiplequestionItem.getDocument();
		
		// update ConceptReference to current value - only one is expected
		MaintainableLabelUpdateElement conceptReferenceUpdateElement = new MaintainableLabelUpdateElement();
		conceptReferenceUpdateElement.setLocalName("ConceptReference");
		int lengthOld = maintainableLabelQueryResult.getSubElement("ConceptReference").length == 0 ? 0 : 
			XmlBeansUtil.getTextOnMixedElement(maintainableLabelQueryResult.getSubElement("ConceptReference")[0]).length();
		int lengthNew = doc.getMultipleQuestionItem().getConceptReferenceList().size() == 0 ? 0 :
			doc.getMultipleQuestionItem().getConceptReferenceList().get(0).getIDList().get(0).getStringValue().length();
		Object CrudValue = genCrudValue(lengthOld, lengthNew, 1);
		if (CrudValue != null) {
			conceptReferenceUpdateElement.setCrudValue((Integer) CrudValue);
			conceptReferenceUpdateElement.setValue(doc.getMultipleQuestionItem().getConceptReferenceList().get(0)
					.xmlText(xmlOptions));
			maintainableLabelUpdateElementList.add(conceptReferenceUpdateElement);
		}

		// update SubQuestion Sequence to current value
		MaintainableLabelUpdateElement SubQuestionSequenceUpdateElement = new MaintainableLabelUpdateElement();
		SubQuestionSequenceUpdateElement.setLocalName("SubQuestionSequence");
		lengthOld = maintainableLabelQueryResult.getSubElement("SubQuestionSequence").length == 0 ? 0 :
			XmlBeansUtil.getTextOnMixedElement(maintainableLabelQueryResult.getSubElement("SubQuestionSequence")[0]).length();
		lengthNew = doc.getMultipleQuestionItem().getSubQuestionSequence() == null ? 0 :
			doc.getMultipleQuestionItem().getSubQuestionSequence().getItemSequenceType().toString().length();
		CrudValue = genCrudValue(lengthOld, lengthNew, 1);
		if (CrudValue != null) {
			SubQuestionSequenceUpdateElement.setCrudValue((Integer) CrudValue);
			SubQuestionSequenceUpdateElement.setValue(doc.getMultipleQuestionItem().getSubQuestionSequence().xmlText(
					xmlOptions));
			maintainableLabelUpdateElementList.add(SubQuestionSequenceUpdateElement);
		}

		// add Question Text
		MaintainableLabelUpdateElement questionTextUpdateElement = null;
		int nbrNewQuestionTextList = doc.getMultipleQuestionItem().getQuestionTextList().size();
		// loop over all QuestionTextList elements:
		for (int i = 0; i < nbrNewQuestionTextList; i++) {
			questionTextUpdateElement = new MaintainableLabelUpdateElement();
			questionTextUpdateElement.setLocalName("QuestionText");
			questionTextUpdateElement.setCrudValue(MaintainableLabelUpdateElement.NEW);
			questionTextUpdateElement.setValue(doc.getMultipleQuestionItem().getQuestionTextList().get(i).xmlText(
					xmlOptions));
			maintainableLabelUpdateElementList.add(questionTextUpdateElement);
		}
		DdiManager.getInstance().updateMaintainableLabel(maintainableLabelQueryResult, maintainableLabelUpdateElementList);

//		multiplequestionItem.clearChanged();
	}

	/**
	 * 
	 * Delete DBXML Multiple Question Item
	 * 
	 * @param id
	 *            Identification
	 * @param version
	 *            Version
	 * @param parentId
	 *            Parent Identification
	 * @param parentVersion
	 *            Parent Version
	 * @throws Exception
	 */
	public void delete(String id, String version, String parentId, String parentVersion) throws Exception {
		log.debug("Delete DBXML MultipleQuestion Item");
		MultipleQuestionItem multipleQuestionItem = getModel(id, version, parentId, parentVersion);
		try {
			DdiManager.getInstance().deleteElement(multipleQuestionItem.getDocument(),
					multipleQuestionItem.getParentId(), multipleQuestionItem.getParentVersion(), "QuestionScheme");
		} catch (DDIFtpException e) {
			log.error("Delete DBXML Question Item error: " + e.getMessage());

			throw new DDIFtpException(e.getMessage());
		}

		if (log.isDebugEnabled()) {
			multipleQuestionItem = null;
			multipleQuestionItem = getModel(id, version, parentId, parentVersion);
			if (multipleQuestionItem != null) {
				log.error("****************** Multiple Question Item not deleted *****************");
			}
		}
	}
}
