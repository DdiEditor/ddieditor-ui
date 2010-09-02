package org.ddialliance.ddieditor.ui.dbxml.category;

import java.util.List;

import org.ddialliance.ddi3.xml.xmlbeans.logicalproduct.CategorySchemeDocument;
import org.ddialliance.ddieditor.logic.identification.IdentificationManager;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.persistenceaccess.maintainablelabel.MaintainableLabelQueryResult;
import org.ddialliance.ddieditor.persistenceaccess.maintainablelabel.MaintainableLightLabelQueryResult;
import org.ddialliance.ddieditor.ui.dbxml.IDao;
import org.ddialliance.ddieditor.ui.model.ElementType;
import org.ddialliance.ddieditor.ui.model.IModel;
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
		CategorySchemeDocument doc = CategorySchemeDocument.Factory
				.newInstance();
		IdentificationManager.getInstance().addIdentification(
				doc.addNewCategoryScheme(),
				ElementType.getElementType("CategoryScheme").getIdPrefix(),
				null);
		IdentificationManager.getInstance().addVersionInformation(
				doc.getCategoryScheme(), null, null);
		MaintainableLabelQueryResult maintainableLabelQueryResult = new MaintainableLabelQueryResult();
		maintainableLabelQueryResult.
		CategoryScheme categoryScheme = new CategoryScheme(maintainableLabelQueryResult, parentId,
				parentVersion);																			
		return categoryScheme;
	}

	@Override
	public void create(IModel model) throws DDIFtpException {
		DdiManager.getInstance().createElement(model.getDocument(),
				model.getParentId(), model.getParentVersion(),
				"CategoryComponent");
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
		CategorySchemeDocument doc = DdiManager.getInstance()
				.getCategoryScheme(id, version, parentId, parentVersion);
		CategoryScheme model = new CategoryScheme(doc, parentId, parentVersion);

		return model;
	}

	@Override
	public void update(IModel model) throws DDIFtpException {
		DdiManager.getInstance().updateElement(model.getDocument(),
				model.getId(), model.getVersion());
	}
}
