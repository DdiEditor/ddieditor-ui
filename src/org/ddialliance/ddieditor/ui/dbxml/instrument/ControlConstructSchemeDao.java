package org.ddialliance.ddieditor.ui.dbxml.instrument;

import java.util.List;

import org.ddialliance.ddi3.xml.xmlbeans.datacollection.ControlConstructSchemeDocument;
import org.ddialliance.ddieditor.logic.identification.IdentificationManager;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.dbxml.IDao;
import org.ddialliance.ddieditor.ui.model.ElementType;
import org.ddialliance.ddieditor.ui.model.IModel;
import org.ddialliance.ddieditor.ui.model.instrument.ControlConstructScheme;
import org.ddialliance.ddiftp.util.DDIFtpException;

public class ControlConstructSchemeDao implements IDao {
	@Override
	public List<LightXmlObjectType> getLightXmlObject(
			LightXmlObjectType parent) throws Exception {
		return getLightXmlObject("", "", parent.getId(),
				parent.getVersion());
	}

	@Override
	public List<LightXmlObjectType> getLightXmlObject(String id,
			String version, String parentId, String parentVersion)
			throws Exception {
		return null;
	}

	@Override
	public ControlConstructScheme create(String id, String version, String parentId,
			String parentVersion) throws Exception {
		ControlConstructSchemeDocument doc = ControlConstructSchemeDocument.Factory.newInstance();
		IdentificationManager.getInstance().addIdentification(
				doc.addNewControlConstructScheme(),
				ElementType.getElementType("ControlConstructScheme").getIdPrefix(), null);
		IdentificationManager.getInstance().addVersionInformation(
				doc.getControlConstructScheme(), null, null);
		ControlConstructScheme model = new ControlConstructScheme(doc, parentId, parentVersion);
		return model;
	}

	@Override
	public ControlConstructScheme getModel(String id, String version, String parentId,
			String parentVersion) throws Exception {
		
		ControlConstructSchemeDocument doc = DdiManager.getInstance().getControlConstructScheme(id,
				version, parentId, parentVersion);
		ControlConstructScheme model = new ControlConstructScheme(doc, parentId, parentVersion);
		return model;
	}

	@Override
	public void create(IModel model) throws DDIFtpException {
		DdiManager.getInstance().createElement(model.getDocument(),
				model.getParentId(), model.getParentVersion(),
				"datacollection__DataCollection");
	}

	@Override
	public void update(IModel model) throws DDIFtpException {
		// TODO Version Control - not supported
		DdiManager.getInstance().updateElement(model.getDocument(),
				model.getId(), model.getVersion());
	}

	@Override
	public void delete(String id, String version, String parentId,
			String parentVersion) throws Exception {
		ControlConstructScheme model = getModel(id, version, parentId, parentVersion);
		DdiManager.getInstance().deleteElement(model.getDocument(),
				model.getParentId(), model.getParentVersion(),
				"datacollection__DataCollection");
	}

}
