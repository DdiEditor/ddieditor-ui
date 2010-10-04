package org.ddialliance.ddieditor.ui.dbxml.instrument;

import java.util.List;

import org.ddialliance.ddi3.xml.xmlbeans.datacollection.SequenceDocument;
import org.ddialliance.ddieditor.logic.identification.IdentificationManager;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.dbxml.IDao;
import org.ddialliance.ddieditor.ui.model.ElementType;
import org.ddialliance.ddieditor.ui.model.IModel;
import org.ddialliance.ddieditor.ui.model.Model;
import org.ddialliance.ddieditor.ui.model.instrument.Sequence;
import org.ddialliance.ddiftp.util.DDIFtpException;

public class SequenceDao implements IDao {

	@Override
	public IModel create(String id, String version, String parentId,
			String parentVersion) throws Exception {
		SequenceDocument doc = SequenceDocument.Factory.newInstance();
		doc.addNewSequence();
		IdentificationManager.getInstance().addIdentification(
				doc.getSequence(),
				ElementType.getElementType("Sequence").getIdPrefix(), "");
		IdentificationManager.getInstance().addVersionInformation(
				doc.getSequence(), null, null);
		return new Sequence(doc, parentId, parentVersion);
	}

	@Override
	public void create(IModel model) throws DDIFtpException {
		DdiManager.getInstance().createElement(model.getDocument(),
				model.getParentId(), model.getParentVersion(),
				"ControlConstructScheme");
	}

	@Override
	public void delete(String id, String version, String parentId,
			String parentVersion) throws Exception {
		Model model = getModel(id, version, parentId, parentVersion);
		DdiManager.getInstance().deleteElement(model.getDocument(),
				model.getParentId(), model.getParentVersion(),
				"ControlConstructScheme");
	}

	@Override
	public List<LightXmlObjectType> getLightXmlObject(LightXmlObjectType parent)
			throws Exception {
		return getLightXmlObject("", "", parent.getId(), parent.getVersion());
	}

	@Override
	public List<LightXmlObjectType> getLightXmlObject(String id,
			String version, String parentId, String parentVersion)
			throws Exception {
		return DdiManager.getInstance().getSequencesLight(id, version,
				parentId, parentVersion).getLightXmlObjectList()
				.getLightXmlObjectList();
	}

	@Override
	public Model getModel(String id, String version, String parentId,
			String parentVersion) throws Exception {
		return new Sequence(DdiManager.getInstance().getSequence(id, version,
				parentId, parentVersion), parentId, parentVersion);
	}

	@Override
	public void update(IModel model) throws DDIFtpException {
		DdiManager.getInstance().updateElement(model.getDocument(),
				model.getId(), model.getVersion());
	}
}
