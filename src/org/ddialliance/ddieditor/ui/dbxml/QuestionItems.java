package org.ddialliance.ddieditor.ui.dbxml;

import java.io.File;
import java.util.List;

import org.apache.xmlbeans.XmlCursor;
import org.ddialliance.ddi_3_0.xml.xmlbeans.datacollection.QuestionItemDocument;
import org.ddialliance.ddi_3_0.xml.xmlbeans.datacollection.QuestionItemType;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.persistenceaccess.PersistenceManager;
import org.ddialliance.ddieditor.ui.model.QuestionItem;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;

public class QuestionItems extends XmlEntities {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, QuestionItems.class);

	/**
	 * Get Light Question Items List of the given Question Scheme
	 * 
	 * @param 	parentQuestionScheme
	 * @return	List<LightXmlObjectType>
	 * @throws 	Exception
	 */
	static public List<LightXmlObjectType> getQuestionItemsLight(LightXmlObjectType parentQuestionScheme)
			throws Exception {

		log.debug("QuestionItems.getQuestionItemsLight() - parent: " + parentQuestionScheme.getId() + "/"
				+ parentQuestionScheme.getVersion());

		List<LightXmlObjectType> lightXmlObjectTypeList = DdiManager.getInstance().getQuestionItemsLight("", "",
				parentQuestionScheme.getId(), parentQuestionScheme.getVersion()).getLightXmlObjectList()
				.getLightXmlObjectList();

		if (log.isDebugEnabled()) {
			for (LightXmlObjectType l : lightXmlObjectTypeList) {
				log.debug("Question Item Id: " + l.getId());
				if (l.getLabelList().size() > 0) {
					XmlCursor xmlCursor = l.getLabelArray(0).newCursor();
					xmlCursor.toLastAttribute();
					xmlCursor.toNextToken();
					String result = xmlCursor.getChars();
					xmlCursor.dispose();
					log.debug("Question Item Label: " + result);
				}
			}
		}
		
		return lightXmlObjectTypeList;
	}

	/**
	 * Get Light Question Item of the given Question Scheme by Id.
	 * 
	 * @param parentQuestionScheme
	 * @param id
	 * @param version
	 * @return
	 * @throws Exception
	 */
	static public LightXmlObjectType getQuestionItemsLight(LightXmlObjectType parentQuestionScheme, String id,
			String version) throws Exception {

		log.debug("QuestionItems.getQuestionItemsLight() - parent: " + parentQuestionScheme.getId() + "/"
				+ parentQuestionScheme.getVersion());

		List<LightXmlObjectType> lightXmlObjectTypeList = DdiManager.getInstance().getQuestionItemsLight(id, version,
				parentQuestionScheme.getId(), parentQuestionScheme.getVersion()).getLightXmlObjectList()
				.getLightXmlObjectList();

		if (log.isDebugEnabled()) {
			boolean numbers;
			if (numbers = lightXmlObjectTypeList.size() != 1) {
				log.error("Unexpected numbers of Question Items:" + numbers);
			}
			for (LightXmlObjectType l : lightXmlObjectTypeList) {
				log.debug("Question Item Id: " + l.getId());
				if (l.getLabelList().size() > 0) {
					XmlCursor xmlCursor = l.getLabelArray(0).newCursor();
					xmlCursor.toLastAttribute();
					xmlCursor.toNextToken();
					String result = xmlCursor.getChars();
					xmlCursor.dispose();
					log.debug("Question Item Label: " + result);
				}
			}
		}
		
		return lightXmlObjectTypeList.get(0);
	}

	/**
	 * Create QuestionItem
	 * 
	 * @param id			Identification
	 * @param version		Version
	 * @param parentId		Parent Identification
	 * @param parentVersion	Parent version
	 * @return				QuestionItem
	 * @throws Exception
	 */
	static public QuestionItem createQuestionItem(String id, String version, String parentId, String parentVersion)
			throws Exception {
		log.debug("QuestionItems.createQuestionItem()");

		QuestionItemDocument questionItemDocument = QuestionItemDocument.Factory.newInstance();

		QuestionItemType questionItemType = questionItemDocument.addNewQuestionItem();
		questionItemType.setId(id);
		if (version != null) {
			questionItemType.setVersion(version);
		}
		
		QuestionItem questionItem = new QuestionItem(questionItemDocument, parentId, parentVersion);

		return questionItem;
	}

	/**
	 * Get QuestionItem
	 * 
	 * @param id			Identification
	 * @param version		Version
	 * @param parentId		Parent Identification
	 * @param parentVersion	Parent version
	 * @return				QuestionItem
	 * @throws Exception
	 */
	static public QuestionItem getQuestionItem(String id, String version, String parentId, String parentVersion)
			throws Exception {
		
		if (log.isDebugEnabled()) {
			log.debug("QuestionItems.getQuestionItem()");

			log.debug("WorkingResource: "
					+ PersistenceManager.getInstance().getWorkingResource());
			log.debug("DefaultResourceNs: "
					+ PersistenceManager.getInstance().getDefaultResourceNs());
			log.debug("GlobalResourcePath: "
					+ PersistenceManager.getInstance().getGlobalResourcePath());
			log.debug("ResourcePath: "
					+ PersistenceManager.getInstance().getResourcePath());
		}

		QuestionItemDocument questionItemDocument = DdiManager.getInstance().getQuestionItem(id, version, parentId,
				parentVersion);

		QuestionItem questionItem = new QuestionItem(questionItemDocument, parentId, parentVersion);

		return questionItem;
	}

	/**
	 * Create new DBXML Question Item
	 * 
	 * @param questionItem
	 *            question item instance
	 * @param parentId
	 *            Id. of Question Scheme
	 * @param parentVersion
	 *            Version of Question Scheme
	 * @throws DDIFtpException
	 */
	static public void create(QuestionItem questionItem) throws DDIFtpException {
		log.debug("Create DBXML Question Item:\n" + questionItem.getQuestionItemDocument()+" Parent Id: "+questionItem.getParentId());
		try {
			DdiManager.getInstance().createElement(questionItem.getQuestionItemDocument(), questionItem.getParentId(),
					questionItem.getParentVersion(), "QuestionScheme");		
		} catch (DDIFtpException e) {
			log.error("Create DBXML Question Item error: "+e.getMessage());
			
			throw new DDIFtpException(e.getMessage());
		}
		
		// TODO When is xml-file updated - when object saved?
		if (xml_export_filename.length() > 0) {
			File outFile = new File("resources" + File.separator + xml_export_filename);
			PersistenceManager.getInstance().exportResoure(DbXml.FULLY_DECLARED_NS_DOC, outFile);
		}
	}

	/**
	 * 
	 * Update DBXML Question Item corresponding to the given QuestionItem
	 * instance
	 * 
	 * @param questionItem
	 *            question Item instance
	 * @throws DDIFtpException
	 */
	static public void update(QuestionItem questionItem) throws DDIFtpException {
		log.debug("Update DBXML Question Item:\n" + questionItem.getQuestionItemDocument());
		try {
			// TODO Version Control - not supported

			DdiManager.getInstance().updateElement(questionItem.getQuestionItemDocument(), questionItem.getId(),
					questionItem.getVersion());
		} catch (DDIFtpException e) {
			log.error("Update DBXML Question Item error: "+e.getMessage());
			
			throw new DDIFtpException(e.getMessage());
		}
		
		// TODO When is xml-file updated - when object saved?
		if (xml_export_filename.length() > 0) {
			File outFile = new File("resources" + File.separator + xml_export_filename);
			PersistenceManager.getInstance().exportResoure(DbXml.FULLY_DECLARED_NS_DOC, outFile);
		}
	}
	
	/**
	 * 
	 * Delete DBXML Question Item
	 * 
	 * @param id			Identification
	 * @param version		Version
	 * @param parentId		Parent Identification
	 * @param parentVersion	Parent Version
	 * @throws Exception
	 */
	static public void delete(String id, String version, String parentId, String parentVersion) throws Exception {
		log.debug("Delete DBXML Question Item");
		QuestionItem questionItem = getQuestionItem(id, version, parentId, parentVersion);
		try {
			DdiManager.getInstance().deleteElement(questionItem.getQuestionItemDocument(), questionItem.getParentId(),
					questionItem.getParentVersion(), "QuestionScheme");
		} catch (DDIFtpException e) {
			log.error("Delete DBXML Question Item error: "+e.getMessage());
		
			throw new DDIFtpException(e.getMessage());
		}
		
		if (log.isDebugEnabled()) {
			questionItem = null;
			questionItem = getQuestionItem(id, version, parentId, parentVersion);
			if (questionItem != null) {
				log.error("****************** Question Item not deleted *****************");
			}
		}
		// TODO When is xml-file updated - when object saved?
		if (xml_export_filename.length() > 0) {
			File outFile = new File("resources" + File.separator + xml_export_filename);
			PersistenceManager.getInstance().exportResoure(DbXml.FULLY_DECLARED_NS_DOC, outFile);
		}
	}
}
