package org.ddialliance.ddieditor.ui.dbxml;

import java.io.File;
import java.util.List;

import org.apache.xmlbeans.XmlCursor;
import org.ddialliance.ddi_3_0.xml.xmlbeans.datacollection.QuestionSchemeDocument;
import org.ddialliance.ddi_3_0.xml.xmlbeans.datacollection.QuestionSchemeType;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectListDocument;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectListType;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.persistenceaccess.PersistenceManager;
import org.ddialliance.ddieditor.ui.model.QuestionItem;
import org.ddialliance.ddieditor.ui.model.QuestionScheme;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;


public class QuestionSchemes {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, QuestionSchemes.class);

	/**
	 * Get Light Question Scheme List
	 * 
	 * @return List<LightXmlObjectType>
	 * @throws Exception
	 */
	public static List<LightXmlObjectType> getQuestionSchemesLight() throws Exception {
		log.info("QuestionScheme.getQuestionSchemesLight()");

		DdiManager ddiManager = DdiManager.getInstance();
		
		LightXmlObjectListDocument listDoc = ddiManager.getQuestionSchemesLight(null, null, null, null);
		
		LightXmlObjectListType lightXmlObjectListType = listDoc.getLightXmlObjectList();
		
		List<LightXmlObjectType> listLightXmlObjectListType = lightXmlObjectListType.getLightXmlObjectList();
		
		for (LightXmlObjectType l : listLightXmlObjectListType) {
			log.info("Question Scheme Id: " + l.getId());
			log.info("Question Scheme ParentId: " + l.getParentId());
			log.info("Question Scheme Lagel list size: "+l.getLabelList().size());
			log.info("Question Scheme lang(0): "+l.getLabelList().get(0).getLang());
			log.info("Question Scheme LangXML(0): "+l.getLabelList().get(0).xmlText());
			
			XmlCursor xmlCursor = l.getLabelArray(0).newCursor();
			xmlCursor.toLastAttribute();
			xmlCursor.toNextToken();
			String result = xmlCursor.getChars();
			xmlCursor.dispose();
			
			log.info("Question Scheme Label text: "+result);
		}
		
		return listLightXmlObjectListType;
	}

	/**
	 * Get Light Question Scheme by Id.
	 * 
	 * @return List<LightXmlObjectType>
	 * @throws Exception
	 */
	public static List<LightXmlObjectType> getQuestionSchemesLight(String id, String version) throws Exception {
		log.info("QuestionScheme.getQuestionSchemesLight()");

		DdiManager ddiManager = DdiManager.getInstance();
		
		LightXmlObjectListDocument listDoc = ddiManager.getQuestionSchemesLight(id, version, null, null);
		
		LightXmlObjectListType lightXmlObjectListType = listDoc.getLightXmlObjectList();
		
		List<LightXmlObjectType> listLightXmlObjectListType = lightXmlObjectListType.getLightXmlObjectList();
		
		// Debug only:
		boolean numbers;
		if (numbers = listLightXmlObjectListType.size() != 1) {
			log.error("Unexpected numbers of Question Schemes:"+ numbers);
		}
		for (LightXmlObjectType l : listLightXmlObjectListType) {
			log.info("Question Scheme Id: " + l.getId());
			log.info("Question Scheme ParentId: " + l.getParentId());
			log.info("Question Scheme Lagel list size: "+l.getLabelList().size());
			log.info("Question Scheme lang(0): "+l.getLabelList().get(0).getLang());
			log.info("Question Scheme LangXML(0): "+l.getLabelList().get(0).xmlText());
			
			XmlCursor xmlCursor = l.getLabelArray(0).newCursor();
			xmlCursor.toLastAttribute();
			xmlCursor.toNextToken();
			String result = xmlCursor.getChars();
			xmlCursor.dispose();
			
			log.info("Question Scheme Label text: "+result);
		}
		
		return listLightXmlObjectListType;
	}

	public QuestionSchemeType getQuestionSchemeById(String id, String parentId) throws Exception {
		log.info("QuestionScheme.getQuestionSchemeById()");
		return DdiManager.getInstance().getQuestionScheme(id, null,
				parentId, null).getQuestionScheme();
	}

	static public QuestionScheme createQuestionScheme(String id, String version, String parentId, String parentVersion)
			throws Exception {
		log.info("QuestionSchemes.createQuestionScheme()");

		QuestionSchemeDocument questionShemeDocument = QuestionSchemeDocument.Factory.newInstance();

		QuestionSchemeType questionSchemeType = questionShemeDocument.addNewQuestionScheme();
		questionSchemeType.setId(id);
		questionSchemeType.setVersion(version);

		QuestionScheme questionScheme = new QuestionScheme(questionShemeDocument, parentId, parentVersion);

		return questionScheme;
	}

	static public QuestionScheme getQuestionScheme(String id, String version, String parentId, String parentVersion)
			throws Exception {
		log.info("QuestionSchemes.getQuestionScheme()");

		QuestionSchemeDocument questionSchemeDocument = DdiManager.getInstance().getQuestionScheme(id, version, parentId,
				parentVersion);

		QuestionScheme questionScheme = new QuestionScheme(questionSchemeDocument, parentId, parentVersion);

		return questionScheme;
	}

	/**
	 * Create new DBXML Question Scheme
	 * 
	 * @param questionScheme
	 *            question scheme instance
	 * @param parentId
	 *            Id. of Data Collection
	 * @param parentVersion
	 *            Version of Data Collection
	 * @throws DDIFtpException
	 */
	static public void create(QuestionScheme questionScheme) throws DDIFtpException {
		log.info("Create DBXML Question Scheme:\n" + questionScheme.getQuestionSchemeDocument());
		System.out.println("Parent Id: "+questionScheme.getParentId());
		System.out.println(questionScheme.getId());
		System.out.println(questionScheme.getLabel());
		System.out.println(questionScheme.getDescr());
		try {
			DdiManager.getInstance().createElement(questionScheme.getQuestionSchemeDocument(), questionScheme.getParentId(),
					questionScheme.getParentVersion(), "DataCollection");		
		} catch (DDIFtpException e) {
			log.error("Create DBXML Question Scheme error: "+e.getMessage());
			PersistenceManager.getInstance().rollbackWorkingResource();
			throw new DDIFtpException(e.getMessage());
		}
		PersistenceManager.getInstance().commitWorkingResource();
		// TODO When is xml-file updated - when object saved?
		File outFile = new File("resources" + File.separator + "large-doc-out.xml");
        PersistenceManager.getInstance().exportResoure(DbXml.FULLY_DECLARED_NS_DOC, outFile);
	}

	/**
	 * 
	 * Update DBXML Question Scheme corresponding to the given QuestionScheme
	 * instance
	 * 
	 * @param questionScheme
	 *            question Scheme instance
	 * @throws DDIFtpException
	 */
	static public void update(QuestionScheme questionScheme) throws DDIFtpException {
		// TODO How is version handled?????
		log.info("Update DBXML Question Scheme:\n" + questionScheme.getQuestionSchemeDocument());
		try {
			DdiManager.getInstance().updateElement(questionScheme.getQuestionSchemeDocument(), questionScheme.getId(),
					questionScheme.getVersion());
		} catch (DDIFtpException e) {
			log.error("Update DBXML Question Scheme error: "+e.getMessage());
			PersistenceManager.getInstance().rollbackWorkingResource();
			throw new DDIFtpException(e.getMessage());
		}
		PersistenceManager.getInstance().commitWorkingResource();
		// TODO When is xml-file updated - when object saved?
		File outFile = new File("resources" + File.separator + "large-doc-out.xml");
        PersistenceManager.getInstance().exportResoure(DbXml.FULLY_DECLARED_NS_DOC, outFile);
	}
	
	/**
	 * 
	 * Delete DBXML Question Scheme
	 * 
	 * @param id			Identification
	 * @param version		Version
	 * @param parentId		Parent Identification
	 * @param parentVersion	Parent Version
	 * @throws Exception
	 */
	static public void delete(String id, String version, String parentId, String parentVersion) throws Exception {
		log.info("Delete DBXML Question Scheme");
		QuestionScheme questionScheme = getQuestionScheme(id, version, parentId, parentVersion);
		try {
			DdiManager.getInstance().deleteElement(questionScheme.getQuestionSchemeDocument(), questionScheme.getId(),
					questionScheme.getVersion(), "QuestionScheme");
		} catch (DDIFtpException e) {
			log.error("Delete DBXML Question Scheme error: "+e.getMessage());
			PersistenceManager.getInstance().rollbackWorkingResource();
			throw new DDIFtpException(e.getMessage());
		}
		PersistenceManager.getInstance().commitWorkingResource();
		// TODO When is xml-file updated - when object saved?
		File outFile = new File("resources" + File.separator + "large-doc-out.xml");
        PersistenceManager.getInstance().exportResoure(DbXml.FULLY_DECLARED_NS_DOC, outFile);
	}
}
