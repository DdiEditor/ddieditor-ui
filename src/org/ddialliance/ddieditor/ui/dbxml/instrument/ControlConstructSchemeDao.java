package org.ddialliance.ddieditor.ui.dbxml.instrument;

import java.util.List;

import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.persistenceaccess.maintainablelabel.MaintainableLabelQueryResult;
import org.ddialliance.ddieditor.ui.dbxml.DaoSchemeHelper;
import org.ddialliance.ddieditor.ui.dbxml.IDao;
import org.ddialliance.ddieditor.ui.model.IModel;
import org.ddialliance.ddieditor.ui.model.LabelDescriptionScheme;
import org.ddialliance.ddieditor.ui.model.instrument.ControlConstructScheme;
import org.ddialliance.ddiftp.util.DDIFtpException;

public class ControlConstructSchemeDao implements IDao {
	@Override
	public List<LightXmlObjectType> getLightXmlObject(LightXmlObjectType parent)
			throws Exception {
		return getLightXmlObject("", "", parent.getId(), parent.getVersion());
	}

	@Override
	public List<LightXmlObjectType> getLightXmlObject(String id,
			String version, String parentId, String parentVersion)
			throws Exception {
		return null;
	}

	@Override
	public ControlConstructScheme create(String id, String version,
			String parentId, String parentVersion) throws Exception {

		MaintainableLabelQueryResult maintainableLabelQueryResult = LabelDescriptionScheme
				.createLabelDescriptionScheme("ControlConstructScheme");

		return new ControlConstructScheme(maintainableLabelQueryResult.getId(),
				maintainableLabelQueryResult.getVersion(), parentId,
				parentVersion, maintainableLabelQueryResult.getAgency(),
				maintainableLabelQueryResult);
	}

	@Override
	public ControlConstructScheme getModel(String id, String version,
			String parentId, String parentVersion) throws Exception {
		MaintainableLabelQueryResult maintainableLabelQueryResult = DdiManager
				.getInstance().getControlConstructSchemeLabel(id, version, parentId,
						parentVersion);

		return new ControlConstructScheme(id, version, parentId, parentVersion,
				maintainableLabelQueryResult.getAgency(),
				maintainableLabelQueryResult);
	}

	@Override
	public void create(IModel model) throws DDIFtpException {
		DdiManager.getInstance().createElement(model.getDocument(),
				model.getParentId(), model.getParentVersion(),
				"ControlConstructScheme");
	}

	@Override
	public void update(IModel model) throws DDIFtpException {
		DaoSchemeHelper.update(model);
	}

	@Override
	public void delete(String id, String version, String parentId,
			String parentVersion) throws Exception {
		ControlConstructScheme model = getModel(id, version, parentId, parentVersion);
		DdiManager.getInstance().deleteElement(model.getDocument(),
				model.getParentId(), model.getParentVersion(),
				"ControlConstructScheme");
	}
}
