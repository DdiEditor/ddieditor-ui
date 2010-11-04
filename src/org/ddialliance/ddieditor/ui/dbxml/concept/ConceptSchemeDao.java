package org.ddialliance.ddieditor.ui.dbxml.concept;

import java.util.List;

import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.persistenceaccess.maintainablelabel.MaintainableLabelQueryResult;
import org.ddialliance.ddieditor.ui.dbxml.DaoSchemeHelper;
import org.ddialliance.ddieditor.ui.dbxml.IDao;
import org.ddialliance.ddieditor.ui.model.ElementType;
import org.ddialliance.ddieditor.ui.model.IModel;
import org.ddialliance.ddieditor.ui.model.LabelDescriptionScheme;
import org.ddialliance.ddieditor.ui.model.concept.ConceptScheme;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;

public class ConceptSchemeDao implements IDao {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			ConceptSchemeDao.class);

	@Override
	public List<LightXmlObjectType> getLightXmlObject(
			LightXmlObjectType parentConceptComponent) throws Exception {
		return getLightXmlObject("", "", parentConceptComponent.getId(),
				parentConceptComponent.getVersion());
	}

	@Override
	public List<LightXmlObjectType> getLightXmlObject(String id,
			String version, String parentId, String parentVersion)
			throws Exception {
		List<LightXmlObjectType> lightXmlObjectTypeList = DdiManager
				.getInstance().getConceptSchemesLight(id, version, parentId,
						parentVersion).getLightXmlObjectList()
				.getLightXmlObjectList();

		return lightXmlObjectTypeList;
	}

	@Override
	public ConceptScheme create(String id, String version, String parentId,
			String parentVersion) throws Exception {
		MaintainableLabelQueryResult maintainableLabelQueryResult = LabelDescriptionScheme
				.createLabelDescriptionScheme("ConceptScheme");

		return new ConceptScheme(maintainableLabelQueryResult.getId(),
				maintainableLabelQueryResult.getVersion(), parentId,
				parentVersion, maintainableLabelQueryResult.getAgency(),
				maintainableLabelQueryResult);
	}

	@Override
	public ConceptScheme getModel(String id, String version, String parentId,
			String parentVersion) throws Exception {
		MaintainableLabelQueryResult maintainableLabelQueryResult = DdiManager
				.getInstance().getConcetSchemeLabel(id, version, parentId,
						parentVersion);

		return new ConceptScheme(id, version, parentId, parentVersion,
				maintainableLabelQueryResult.getAgency(),
				maintainableLabelQueryResult);
	}

	@Override
	public void delete(String id, String version, String parentId,
			String parentVersion) throws Exception {
		ConceptScheme conceptScheme = getModel(id, version, parentId,
				parentVersion);
		DdiManager.getInstance().deleteElement(conceptScheme.getDocument(),
				conceptScheme.getParentId(), conceptScheme.getParentVersion(),
				"ConceptualComponent");
	}

	@Override
	public void create(IModel model) throws DDIFtpException {
		log.debug("ConceptSchemeDao.create()");
		DdiManager.getInstance().createElement(model.getDocument(),
				model.getParentId(), model.getParentVersion(),
				"ConceptualComponent");
	}

	@Override
	public void update(IModel model) throws DDIFtpException {
		DaoSchemeHelper.update(model);
	}
}
