package org.ddialliance.ddieditor.ui.dbxml.question;

/**
 * Question Item.
 * 
 */
/*
 * $Author$ 
 * $Date$ 
 * $Revision$
 */

import java.util.List;

import org.apache.xmlbeans.XmlCursor;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.QuestionItemDocument;
import org.ddialliance.ddieditor.logic.identification.IdentificationManager;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.persistenceaccess.PersistenceManager;
import org.ddialliance.ddieditor.ui.dbxml.IDao;
import org.ddialliance.ddieditor.ui.model.ElementType;
import org.ddialliance.ddieditor.ui.model.IModel;
import org.ddialliance.ddieditor.ui.model.question.QuestionItem;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;

public class QuestionItemDao implements IDao {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			QuestionItemDao.class);
	protected ElementType parentElementType = null;

	public QuestionItemDao() {

	}

	/**
	 * Get Light Question Items List of the given Question Scheme
	 * 
	 * @param parentQuestionScheme
	 * @return List<LightXmlObjectType>
	 * @throws Exception
	 */
	public List<LightXmlObjectType> getLightXmlObject(
			LightXmlObjectType parentXmlObject) throws Exception {

		return getLightXmlObject("", "", parentXmlObject.getId(),
				parentXmlObject.getParentVersion());
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
	public List<LightXmlObjectType> getLightXmlObject(String id,
			String version, String parentId, String parentVersion)
			throws Exception {

		log.debug("QuestionItems.getLightXmlObject() - parent: " + parentId
				+ "/" + parentVersion);

		List<LightXmlObjectType> lightXmlObjectTypeList = null;
		if (this.parentElementType == null) {
			log.error("getLightXmlObject: Missing parent element type");
			// TODO get error message
			throw new DDIFtpException(
					"getLightXmlObject: Missing parent element type");
		}
		if (this.parentElementType == ElementType.MULTIPLE_QUESTION_ITEM) {
			lightXmlObjectTypeList = DdiManager
					.getInstance()
					.getMultipleQuestionQuestionItemsLight(id, version,
							parentId, parentVersion).getLightXmlObjectList()
					.getLightXmlObjectList();
		} else {
			lightXmlObjectTypeList = DdiManager.getInstance()
					.getQuestionItemsLightPlus(null, null, null, null)
					.getLightXmlObjectList().getLightXmlObjectList();
		}

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
	 * Create QuestionItem object
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
	 *            Element Type of selected tree element
	 * @return QuestionItem
	 * @throws Exception
	 */
	public QuestionItem create(String id, String version, String parentId,
			String parentVersion) throws Exception {
		log.debug("QuestionItems.createQuestionItem()");

		QuestionItemDocument doc = QuestionItemDocument.Factory.newInstance();
		IdentificationManager.getInstance().addIdentification(
				doc.addNewQuestionItem(),
				ElementType.getElementType("QuestionItem").getIdPrefix(), null);
		IdentificationManager.getInstance().addVersionInformation(
				doc.getQuestionItem(), null, null);
		QuestionItem model = new QuestionItem(doc, parentId, parentVersion);
		return model;
	}

	/**
	 * Get QuestionItem
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
	public QuestionItem getModel(String id, String version, String parentId,
			String parentVersion) throws Exception {

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

		QuestionItemDocument doc = null;
		if (this.parentElementType == null) {
			log.error("Missing parent element type");
			// TODO get error message
			throw new DDIFtpException("getModel: Missing parent element type");
		}
		if (this.parentElementType.equals(ElementType.MULTIPLE_QUESTION_ITEM)) {
			System.out
					.println("*********** MULTIPLE_QUESTION_ITEM parent ********************");
			doc = DdiManager.getInstance().getMultipleQuestionQuestionItem(id,
					version, parentId, parentVersion);
		} else {
			doc = DdiManager.getInstance().getQuestionItem(id, version,
					parentId, parentVersion);
		}
		return doc == null ? null : new QuestionItem(doc, parentId,
				parentVersion);
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
	public void create(IModel questionItem) throws DDIFtpException {
		log.debug("Create DBXML Question Item:\n" + questionItem.getDocument()
				+ " Parent Id: " + questionItem.getParentId());
		try {
			if (this.parentElementType == null) {
				log.error("create: Missing parent element type");
				// TODO get error message
				throw new DDIFtpException("create: Missing parent element type");
			}
			if (this.parentElementType
					.equals(ElementType.MULTIPLE_QUESTION_ITEM)) {
				DdiManager.getInstance().createElement(
						questionItem.getDocument(), questionItem.getParentId(),
						questionItem.getParentVersion(),
						"MultipleQuestionItem", "SubQuestions",
						"getMultipleQuestionItemsLight");
			} else {
				DdiManager.getInstance().createElement(
						questionItem.getDocument(), questionItem.getParentId(),
						questionItem.getParentVersion(), "QuestionScheme");
			}
		} catch (DDIFtpException e) {
			log.error("Create DBXML Question Item error: " + e.getMessage());

			throw new DDIFtpException(e.getMessage());
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
	public void update(IModel questionItem) throws DDIFtpException {
		log.debug("Update DBXML Question Item:\n" + questionItem.getDocument());
		try {
			// TODO Version Control - not supported

			DdiManager.getInstance().updateElement(questionItem.getDocument(),
					questionItem.getId(), questionItem.getVersion());
		} catch (DDIFtpException e) {
			log.error("Update DBXML Question Item error: " + e.getMessage());

			throw new DDIFtpException(e.getMessage());
		}
	}

	/**
	 * 
	 * Delete DBXML Question Item
	 * 
	 * @param id
	 *            Identification
	 * @param version
	 *            Version
	 * @param elementType
	 *            Element Type
	 * @param parentId
	 *            Parent Identification
	 * @param parentVersion
	 *            Parent Version
	 * @throws Exception
	 */
	public void delete(String id, String version, String parentId,
			String parentVersion) throws Exception {
		log.debug("Delete DBXML Question Item");
		QuestionItem questionItem = getModel(id, version, parentId,
				parentVersion);
		try {
			if (this.parentElementType == null) {
				log.error("delete: Missing parent element type");
				// TODO get error message
				throw new DDIFtpException("delete: Missing parent element type");
			}
			if (this.parentElementType
					.equals(ElementType.MULTIPLE_QUESTION_ITEM)) {
				DdiManager.getInstance()
						.deleteElement(questionItem.getDocument(),
								questionItem.getParentId(),
								questionItem.getParentVersion(),
								"MultipleQuestionItem");
			} else {
				DdiManager.getInstance().deleteElement(
						questionItem.getDocument(), questionItem.getParentId(),
						questionItem.getParentVersion(), "QuestionScheme");
			}
		} catch (DDIFtpException e) {
			log.error("Delete DBXML Question Item error: " + e.getMessage());

			throw new DDIFtpException(e.getMessage());
		}

		if (log.isDebugEnabled()) {
			questionItem = null;
			questionItem = getModel(id, version, parentId, parentVersion);
			if (questionItem != null) {
				log.error("****************** Question Item not deleted *****************");
			}
		}
	}

	public void setParentElementType(ElementType parentElementTYpe) {
		this.parentElementType = parentElementTYpe;
	}
}
