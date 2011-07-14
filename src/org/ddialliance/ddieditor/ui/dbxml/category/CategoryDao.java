package org.ddialliance.ddieditor.ui.dbxml.category;

import java.util.List;

import org.ddialliance.ddi3.xml.xmlbeans.logicalproduct.CategoryDocument;
import org.ddialliance.ddieditor.logic.identification.IdentificationManager;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.dbxml.IDao;
import org.ddialliance.ddieditor.ui.model.ElementType;
import org.ddialliance.ddieditor.ui.model.IModel;
import org.ddialliance.ddieditor.ui.model.category.Category;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;

public class CategoryDao implements IDao {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			CategoryDao.class);

	@Override
	public IModel create(String id, String version, String parentId,
			String parentVersion) throws Exception {
		CategoryDocument doc = CategoryDocument.Factory.newInstance();
		IdentificationManager.getInstance().addIdentification(doc.addNewCategory(),
				ElementType.getElementType("Category").getIdPrefix(), null);
		IdentificationManager.getInstance().addVersionInformation(doc.getCategory(), null, null);
		Category category = new Category(doc, parentId, parentVersion);
		return category;
	}

	@Override
	public void create(IModel model) throws DDIFtpException {
		DdiManager.getInstance().createElement(model.getDocument(),
				model.getParentId(),
				model.getParentVersion(),
				"CategoryScheme",
				// parentSubElements - elements of parent
				new String[] { "VersionRationale", "VersionResponsibility",
						"Label", "Description",
						"CategorySchemeReference", "CategoryGroup" },
				// stopElements - do not search below ...
				new String[] { },
				// jumpElements - jump over elements
				new String[] { "Category", });
	}

	@Override
	public void delete(String id, String version, String parentId,
			String parentVersion) throws Exception {
		IModel model = getModel(id, version, parentId, parentVersion);
		DdiManager.getInstance().deleteElement(model.getDocument(),
				model.getParentId(), model.getParentVersion(),
				"CategoryScheme");
	}

	@Override
	public List<LightXmlObjectType> getLightXmlObject(
			LightXmlObjectType parentCategory) throws Exception {
		return getLightXmlObject("", "", parentCategory.getId(), parentCategory.getVersion());
	}

	@Override
	public List<LightXmlObjectType> getLightXmlObject(String id,
			String version, String parentId, String parentVersion)
			throws Exception {
		List<LightXmlObjectType> lightXmlObjectTypeList = DdiManager.getInstance().getCategorysLight(id, version,
				parentId, parentVersion).getLightXmlObjectList().getLightXmlObjectList();
		return lightXmlObjectTypeList;
	}

	@Override
	public Category getModel(String id, String version, String parentId,
			String parentVersion) throws Exception {
		CategoryDocument doc = DdiManager.getInstance().getCategory(id,
				version, parentId, parentVersion);
		Category model = new Category(doc, parentId, parentVersion);

		return model;
	}

	@Override
	public void update(IModel model) throws DDIFtpException {
		DdiManager.getInstance().updateElement(model.getDocument(),
				model.getId(), model.getVersion());
	}
}
