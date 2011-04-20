package org.ddialliance.ddieditor.ui.dbxml.category;

import java.util.List;

import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.persistenceaccess.maintainablelabel.MaintainableLabelQueryResult;
import org.ddialliance.ddieditor.ui.dbxml.DaoSchemeHelper;
import org.ddialliance.ddieditor.ui.dbxml.IDao;
import org.ddialliance.ddieditor.ui.model.IModel;
import org.ddialliance.ddieditor.ui.model.LabelDescriptionScheme;
import org.ddialliance.ddieditor.ui.model.category.CategoryScheme;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;

public class CategorySchemeDao implements IDao {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			CategorySchemeDao.class);

	@Override
	public IModel create(String id, String version, String parentId,
			String parentVersion) throws Exception {
		MaintainableLabelQueryResult maintainableLabelQueryResult = LabelDescriptionScheme
				.createLabelDescriptionScheme("CategoryScheme");

		return new CategoryScheme(maintainableLabelQueryResult.getId(),
				maintainableLabelQueryResult.getVersion(), parentId,
				parentVersion, maintainableLabelQueryResult.getAgency(),
				maintainableLabelQueryResult);
	}

	@Override
	public void create(IModel model) throws DDIFtpException {
		if (model.getParentId() == null) {
			List<LightXmlObjectType> logpList;
			try {
				logpList = DdiManager
				.getInstance()
				.getLogicalProductsLight(null, null, null, null)
				.getLightXmlObjectList()
				.getLightXmlObjectList();
			} catch (Exception e) {
				throw new DDIFtpException(e.getMessage());
			}
			model.setParentId(logpList.get(0).getId());
			model.setParentVersion(logpList.get(0).getParentVersion());
		}
		DdiManager.getInstance().createElement(model.getDocument(),
				model.getParentId(), model.getParentVersion(),
				"logicalproduct__LogicalProduct");
	}

	@Override
	public void delete(String id, String version, String parentId,
			String parentVersion) throws Exception {
		IModel model = getModel(id, version, parentId, parentVersion);
		DdiManager.getInstance().deleteElement(model.getDocument(),
				model.getParentId(), model.getParentVersion(),
				"logicalproduct__LogicalProduct");
	}

	@Override
	public List<LightXmlObjectType> getLightXmlObject(
			LightXmlObjectType parentCategoryScheme) throws Exception {
		return getLightXmlObject("", "", parentCategoryScheme.getId(),
				parentCategoryScheme.getVersion());
	}

	@Override
	public List<LightXmlObjectType> getLightXmlObject(String id,
			String version, String parentId, String parentVersion)
			throws Exception {
		List<LightXmlObjectType> lightXmlObjectTypeList = DdiManager
				.getInstance().getCategorySchemesLight(id, version, parentId,
						parentVersion).getLightXmlObjectList()
				.getLightXmlObjectList();

		return lightXmlObjectTypeList;
	}

	@Override
	public CategoryScheme getModel(String id, String version, String parentId,
			String parentVersion) throws Exception {
		MaintainableLabelQueryResult maintainableLabelQueryResult = DdiManager
				.getInstance().getCategorySchemeLabel(id, version, parentId,
						parentVersion);

		return new CategoryScheme(id, version, parentId, parentVersion,
				maintainableLabelQueryResult.getAgency(),
				maintainableLabelQueryResult);
	}

	@Override
	public void update(IModel model) throws DDIFtpException {
		DaoSchemeHelper.update(model);
	}
}
