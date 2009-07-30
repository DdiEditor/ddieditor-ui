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
import org.ddialliance.ddieditor.ui.model.QuestionScheme;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;

public class QuestionSchemes extends XmlEntities {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, QuestionSchemes.class);

	/**
	 * Get Light Question Scheme List
	 * 
	 * @return List<LightXmlObjectType>
	 * @throws Exception
	 */
	public static List<LightXmlObjectType> getQuestionSchemesLight() throws Exception {
		return getQuestionSchemesLight("", "");
	}

	/**
	 * 
	 * Get Light Question Scheme List
	 * 
	 * @param id
	 * @param version
	 * @return List<LightXmlObjectType>
	 * @throws Exception
	 */
	public static List<LightXmlObjectType> getQuestionSchemesLight(String id, String version) throws Exception {
		log.debug("QuestionScheme.getQuestionSchemesLight(). Id: "+id+" Version: "+version);

		DdiManager ddiManager = DdiManager.getInstance();

		LightXmlObjectListDocument listDoc = ddiManager.getQuestionSchemesLight(id, version, null, null);

		LightXmlObjectListType lightXmlObjectListType = listDoc.getLightXmlObjectList();

		List<LightXmlObjectType> listLightXmlObjectListType = lightXmlObjectListType.getLightXmlObjectList();

		return listLightXmlObjectListType;
	}
	
	/**
	 * 
	 * Get Light Question Item List
	 * 
	 * - get children (Question Items) of given Question Scheme
	 * 
	 * @param id
	 * @param version
	 * @return List<LightXmlObjectType>
	 * @throws Exception
	 */
	public static List<LightXmlObjectType> getQuestionItemsLight(String id, String version) throws Exception {
		log.debug("ConceptScheme.getQuestionItemsLight(). Id: "+id+" Version: "+version);

		DdiManager ddiManager = DdiManager.getInstance();

		LightXmlObjectListDocument listDoc = ddiManager.getQuestionItemsLight("", "", id, version);

		LightXmlObjectListType lightXmlObjectListType = listDoc.getLightXmlObjectList();

		List<LightXmlObjectType> listLightXmlObjectListType = lightXmlObjectListType.getLightXmlObjectList();

		return listLightXmlObjectListType;
	}

	/**
	 * Get Question Scheme by Id
	 * 
	 * @param id
	 * @param parentId
	 * @return QuestionSchemeType
	 * @throws Exception
	 */
	public QuestionSchemeType getQuestionSchemeById(String id, String parentId) throws Exception {
		log.debug("QuestionScheme.getQuestionSchemeById()");
		return DdiManager.getInstance().getQuestionScheme(id, null, parentId, null).getQuestionScheme();
	}

	/**
	 * Create Question Scheme object
	 * 
	 * @param id
	 * @param version
	 * @param parentId
	 * @param parentVersion
	 * @return QuestionScheme
	 * @throws Exception
	 */
	static public QuestionScheme createQuestionScheme(String id, String version, String parentId, String parentVersion)
			throws Exception {
		log.debug("QuestionSchemes.createQuestionScheme()");

		QuestionSchemeDocument questionShemeDocument = QuestionSchemeDocument.Factory.newInstance();

		QuestionSchemeType questionSchemeType = questionShemeDocument.addNewQuestionScheme();
		questionSchemeType.setId(id);
		if (version != null) {
			questionSchemeType.setVersion(version);			
		}

		QuestionScheme questionScheme = new QuestionScheme(questionShemeDocument, parentId, parentVersion);

		return questionScheme;
	}

	/**
	 * 
	 * @param id
	 * @param version
	 * @param parentId
	 * @param parentVersion
	 * @return QuestionScheme
	 * @throws Exception
	 */
	static public QuestionScheme getQuestionScheme(String id, String version, String parentId, String parentVersion)
			throws Exception {
		log.debug("QuestionSchemes.getQuestionScheme()");

		QuestionSchemeDocument questionSchemeDocument = DdiManager.getInstance().getQuestionScheme(id, version,
				parentId, parentVersion);

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
		try {
			DdiManager.getInstance().createElement(questionScheme.getQuestionSchemeDocument(),
					questionScheme.getParentId(), questionScheme.getParentVersion(), "datacollection__DataCollection");
		} catch (DDIFtpException e) {
			log.error("Create DBXML Question Scheme error: " + e.getMessage());
			
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
	 * Update DBXML Question Scheme corresponding to the given QuestionScheme
	 * instance
	 * 
	 * @param questionScheme
	 *            question Scheme instance
	 * @throws DDIFtpException
	 */
	static public void update(QuestionScheme questionScheme) throws DDIFtpException {
		// TODO Version Control - not supported
		log.debug("Update DBXML Question Scheme:\n" + questionScheme.getQuestionSchemeDocument());
		try {
			DdiManager.getInstance().updateElement(questionScheme.getQuestionSchemeDocument(), questionScheme.getId(),
					questionScheme.getVersion());
		} catch (DDIFtpException e) {
			log.error("Update DBXML Question Scheme error: " + e.getMessage());
			
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
	 * Delete DBXML Question Scheme
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
	static public void delete(String id, String version, String parentId, String parentVersion) throws Exception {
		log.debug("Delete DBXML Question Scheme");
		QuestionScheme questionScheme = getQuestionScheme(id, version, parentId, parentVersion);
		try {
			DdiManager.getInstance().deleteElement(questionScheme.getQuestionSchemeDocument(), questionScheme.getParentId(),
					questionScheme.getParentVersion(), "datacollection__DataCollection");
		} catch (DDIFtpException e) {
			log.error("Delete DBXML Question Scheme error: " + e.getMessage());
			
			throw new DDIFtpException(e.getMessage());
		}
		
		// TODO When is xml-file updated - when object saved?
		if (xml_export_filename.length() > 0) {
			File outFile = new File("resources" + File.separator + xml_export_filename);
			PersistenceManager.getInstance().exportResoure(DbXml.FULLY_DECLARED_NS_DOC, outFile);
		}
	}
}
