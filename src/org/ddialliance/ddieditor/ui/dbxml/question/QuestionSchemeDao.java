package org.ddialliance.ddieditor.ui.dbxml.question;

/**
 * Question Schemes (DBXML).
 * 
 */
/*
 * $Author$ 
 * $Date$ 
 * $Revision$
 */

import java.util.List;

import org.ddialliance.ddi3.xml.xmlbeans.datacollection.QuestionSchemeType;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.persistenceaccess.maintainablelabel.MaintainableLabelQueryResult;
import org.ddialliance.ddieditor.ui.dbxml.DaoSchemeHelper;
import org.ddialliance.ddieditor.ui.dbxml.IDao;
import org.ddialliance.ddieditor.ui.model.IModel;
import org.ddialliance.ddieditor.ui.model.LabelDescriptionScheme;
import org.ddialliance.ddieditor.ui.model.question.QuestionScheme;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;

public class QuestionSchemeDao implements IDao {
	private Log log = LogFactory
			.getLog(LogType.SYSTEM, QuestionSchemeDao.class);

	@Override
	public List<LightXmlObjectType> getLightXmlObject(
			LightXmlObjectType parentDataCollection) throws Exception {

		log.debug("QuestionSchemes.getLightXmlObject()");

		return getLightXmlObject("", "", parentDataCollection.getId(),
				parentDataCollection.getVersion());
	}

	@Override
	public List<LightXmlObjectType> getLightXmlObject(String id,
			String version, String parentId, String parentVersion)
			throws Exception {
		log.debug("QuestionSchemes.getLightXmlObject()");

		List<LightXmlObjectType> lightXmlObjectTypeList = DdiManager
				.getInstance().getQuestionSchemesLight(id, version, parentId,
						parentVersion).getLightXmlObjectList()
				.getLightXmlObjectList();

		return lightXmlObjectTypeList;
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
	public List<LightXmlObjectType> getLightXmlObject(String id, String version)
			throws Exception {
		log.debug("QuestionScheme.getQuestionSchemesLight(). Id: " + id
				+ " Version: " + version);

		List<LightXmlObjectType> lightXmlObjectTypeList = DdiManager
				.getInstance().getQuestionSchemesLight(id, version, null, null)
				.getLightXmlObjectList().getLightXmlObjectList();
		return lightXmlObjectTypeList;
	}

	/**
	 * Get Question Scheme by Id
	 * 
	 * @param id
	 * @param parentId
	 * @return QuestionSchemeType
	 * @throws Exception
	 */
	public QuestionSchemeType getQuestionSchemeById(String id, String parentId)
			throws Exception {
		log.debug("QuestionScheme.getQuestionSchemeById()");
		return DdiManager.getInstance().getQuestionScheme(id, null, parentId,
				null).getQuestionScheme();
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
	public QuestionScheme create(String id, String version, String parentId,
			String parentVersion) throws Exception {
		MaintainableLabelQueryResult maintainableLabelQueryResult = LabelDescriptionScheme
				.createLabelDescriptionScheme("QuestionScheme");

		return new QuestionScheme(maintainableLabelQueryResult.getId(),
				maintainableLabelQueryResult.getVersion(), parentId,
				parentVersion, maintainableLabelQueryResult.getAgency(),
				maintainableLabelQueryResult);
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
	public QuestionScheme getModel(String id, String version, String parentId,
			String parentVersion) throws Exception {
		MaintainableLabelQueryResult maintainableLabelQueryResult = DdiManager
				.getInstance().getQuestionSchemeLabel(id, version, parentId,
						parentVersion);

		return new QuestionScheme(id, version, parentId, parentVersion,
				maintainableLabelQueryResult.getAgency(),
				maintainableLabelQueryResult);
	}

	@Override
	public void create(IModel questionScheme) throws DDIFtpException {
		DdiManager.getInstance().createElement(questionScheme.getDocument(),
				questionScheme.getParentId(),
				questionScheme.getParentVersion(),
				"datacollection__DataCollection");
	}

	@Override
	public void update(IModel model) throws DDIFtpException {
		DaoSchemeHelper.update(model);
	}

	public void delete(String id, String version, String parentId,
			String parentVersion) throws Exception {
		QuestionScheme model = getModel(id, version, parentId, parentVersion);
		DdiManager.getInstance().deleteElement(model.getDocument(),
				model.getParentId(), model.getParentVersion(),
				"datacollection__DataCollection");
	}
}
