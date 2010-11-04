package org.ddialliance.ddieditor.ui.dbxml.question;

import java.util.ArrayList;
import java.util.List;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.DynamicTextType;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.MultipleQuestionItemDocument;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.QuestionGroupType;
import org.ddialliance.ddieditor.logic.identification.IdentificationManager;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.persistenceaccess.PersistenceManager;
import org.ddialliance.ddieditor.persistenceaccess.maintainablelabel.MaintainableLabelQueryResult;
import org.ddialliance.ddieditor.persistenceaccess.maintainablelabel.MaintainableLabelUpdateElement;
import org.ddialliance.ddieditor.ui.dbxml.DaoSchemeHelper;
import org.ddialliance.ddieditor.ui.dbxml.IDao;
import org.ddialliance.ddieditor.ui.model.ElementType;
import org.ddialliance.ddieditor.ui.model.IModel;
import org.ddialliance.ddieditor.ui.model.LabelDescriptionScheme;
import org.ddialliance.ddieditor.ui.model.question.MultipleQuestionItem;
import org.ddialliance.ddieditor.ui.model.question.QuestionScheme;
import org.ddialliance.ddieditor.ui.model.universe.UniverseScheme;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;

/**
 * Multi Question Item.
 * 
 */
/*
 * $Author: ddadak $ 
 * $Date: 2010-03-24 11:59:05 +0100 (Wed, 24 Mar 2010) $ 
 * $Revision: 1042 $
 */

public class MultipleQuestionItemDao implements IDao {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			MultipleQuestionItemDao.class);

	/**
	 * Get Light MultipleQuestionItems List of the given Question Scheme
	 * 
	 * @param parentQuestionScheme
	 * @return List<LightXmlObjectType>
	 * @throws Exception
	 */
	public List<LightXmlObjectType> getLightXmlObject(
			LightXmlObjectType parentQuestionScheme) throws Exception {

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
	public List<LightXmlObjectType> getLightXmlObject(String id,
			String version, String parentId, String parentVersion)
			throws Exception {

		log.debug("MultipleQuestionItems.getItemsLight() - parent: " + parentId
				+ "/" + parentVersion);

		List<LightXmlObjectType> lightXmlObjectTypeList = DdiManager
				.getInstance().getMultipleQuestionItemsLight(id, version, parentId,
						parentVersion).getLightXmlObjectList()
				.getLightXmlObjectList();

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
	public MultipleQuestionItem create(String id, String version, String parentId,
			String parentVersion) throws Exception {
		log.debug("MultipleQuestionItems.create()");

		MaintainableLabelQueryResult maintainableLabelQueryResult = LabelDescriptionScheme
		.createLabelDescriptionScheme("MultipleQuestionItem");

        return new MultipleQuestionItem(maintainableLabelQueryResult.getId(),
		maintainableLabelQueryResult.getVersion(), parentId,
		parentVersion, maintainableLabelQueryResult.getAgency(),
		maintainableLabelQueryResult);
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

		return new MultipleQuestionItem(id, version, parentId, parentVersion, 
				maintainableLabelQueryResult.getAgency(),
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
		log.debug("Create DBXML Multiple Question Item:\n" + multipleQuestionItem.getDocument()
				+ " Parent Id: " + multipleQuestionItem.getParentId());
		MultipleQuestionItemDocument doc = (MultipleQuestionItemDocument )multipleQuestionItem.getDocument();
		if (doc.getMultipleQuestionItem().getSubQuestions() == null) {
			doc.getMultipleQuestionItem().addNewSubQuestions();
		}
		try {
			DdiManager.getInstance().createElement(doc,
					multipleQuestionItem.getParentId(),
					multipleQuestionItem.getParentVersion(), "QuestionScheme");
		} catch (DDIFtpException e) {
			log.error("Create DBXML Multiple Question Item error: " + e.getMessage());

			throw new DDIFtpException(e.getMessage());
		}
	}

	/**
	 * 
	 * Update DBXML Multiple Question Item corresponding to the given MultipleQuestionItem
	 * instance
	 * 
	 * @param multipleQuestionItem
	 *            MultipleQuestionItem instance
	 * @throws DDIFtpException
	 */
	public void update(IModel multiplequestionItem) throws DDIFtpException {
		log.debug("Update DBXML Multiple Question Item:\n" + multiplequestionItem.getDocument());
		try {
			// TODO Version Control - not supported

			DdiManager.getInstance().updateElement(multiplequestionItem.getDocument(),
					multiplequestionItem.getId(), multiplequestionItem.getVersion());
		} catch (DDIFtpException e) {
			log.error("Update DBXML MultipleQuestion Item error: " + e.getMessage());

			throw new DDIFtpException(e.getMessage());
		}
		
//		// TODO Version Control - not supported
//		
//		MaintainableLabelQueryResult maintainableLabelQueryResult = DdiManager
//		.getInstance().getMultipleQuestionItemLabel(multiplequestionItem.getId(), multiplequestionItem.getVersion(), 
//				multiplequestionItem.getParentId(), multiplequestionItem.getParentVersion());
//		
//		System.out.println("MaintainableLabelQueryResult: "+maintainableLabelQueryResult.getResult());
//		System.out.println("MaintainableLabelQueryResult size:"+maintainableLabelQueryResult.getResult().size());
//
//		XmlObject[] questionTexts = maintainableLabelQueryResult.getSubElement("QuestionText");
//		XmlObject[] conceptReferences = maintainableLabelQueryResult.getSubElement("ConceptReference");
//		XmlObject[] subQuestionSequence = maintainableLabelQueryResult.getSubElement("SubQuestionSequence");
//		
//		List<MaintainableLabelUpdateElement> updQuestionTextsList = new ArrayList<MaintainableLabelUpdateElement>();
//		MaintainableLabelUpdateElement updQuestionTexts = new MaintainableLabelUpdateElement();
//
//		// update Question Text
//		updQuestionTexts.setLocalName("QuestionText");
//		updQuestionTexts.setCrudValue(1);
//		MultipleQuestionItemDocument doc = (MultipleQuestionItemDocument)multiplequestionItem.getDocument();
//		List<DynamicTextType> texts = doc.getMultipleQuestionItem().getQuestionTextList();
//		updQuestionTexts.setValue("<LiteralText xmlns='ddi:datacollection:3_1'><Text>Multple Question header text.</Text></LiteralText>");
//		updQuestionTextsList.add(updQuestionTexts);
//		
//		System.out.println("updQuestionTexts: "+updQuestionTexts);
//		System.out.println("updQuestionTextsList: "+updQuestionTextsList);
//
//		DdiManager.getInstance().updateMaintainableLabel(maintainableLabelQueryResult, updQuestionTextsList);
////
////		multiplequestionItem.clearChanged();
//		// updates target maintabable
//		// updates on childs
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
	public void delete(String id, String version, String parentId,
			String parentVersion) throws Exception {
		log.debug("Delete DBXML MultipleQuestion Item");
		MultipleQuestionItem multipleQuestionItem = getModel(id, version, parentId,
				parentVersion);
		try {
			DdiManager.getInstance().deleteElement(multipleQuestionItem.getDocument(),
					multipleQuestionItem.getParentId(),
					multipleQuestionItem.getParentVersion(), "QuestionScheme");
		} catch (DDIFtpException e) {
			log.error("Delete DBXML Question Item error: " + e.getMessage());

			throw new DDIFtpException(e.getMessage());
		}

		if (log.isDebugEnabled()) {
			multipleQuestionItem = null;
			multipleQuestionItem = getModel(id, version, parentId, parentVersion);
			if (multipleQuestionItem != null) {
				log
						.error("****************** Multiple Question Item not deleted *****************");
			}
		}
	}
}
