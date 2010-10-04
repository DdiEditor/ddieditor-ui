package org.ddialliance.ddieditor.ui.dbxml.instrument;

import java.util.List;

import org.ddialliance.ddi3.xml.xmlbeans.datacollection.QuestionConstructDocument;
import org.ddialliance.ddieditor.logic.identification.IdentificationManager;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.dbxml.IDao;
import org.ddialliance.ddieditor.ui.model.ElementType;
import org.ddialliance.ddieditor.ui.model.IModel;
import org.ddialliance.ddieditor.ui.model.instrument.QuestionConstruct;
import org.ddialliance.ddiftp.util.DDIFtpException;

public class QuestionConstructDao implements IDao {

	@Override
	public IModel create(String id, String version, String parentId,
			String parentVersion) throws Exception {
		QuestionConstructDocument doc = QuestionConstructDocument.Factory
				.newInstance();
		IdentificationManager.getInstance().addIdentification(
				doc.addNewQuestionConstruct(),
				ElementType.getElementType("QuestionConstruct").getIdPrefix(),
				null);
		IdentificationManager.getInstance().addVersionInformation(
				doc.getQuestionConstruct(), null, null);
		QuestionConstruct model = new QuestionConstruct(doc, parentId,
				parentVersion);
		return model;
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
		QuestionConstruct model = getModel(id, version, parentId, parentVersion);
		DdiManager.getInstance().deleteElement(model.getDocument(),
				model.getParentId(), model.getParentVersion(),
				"ControlConstructScheme");
	}

	@Override
	public List<LightXmlObjectType> getLightXmlObject(
			LightXmlObjectType lightXmlObject) throws Exception {
		return getLightXmlObject("", "", lightXmlObject.getId(), lightXmlObject
				.getVersion());
	}

	@Override
	public List<LightXmlObjectType> getLightXmlObject(String id,
			String version, String parentId, String parentVersion)
			throws Exception {
		return DdiManager.getInstance().getQuestionConstructsLight(id, version,
				parentId, parentVersion).getLightXmlObjectList()
				.getLightXmlObjectList();
	}

	@Override
	public QuestionConstruct getModel(String id, String version,
			String parentId, String parentVersion) throws Exception {
		QuestionConstructDocument doc = DdiManager.getInstance()
				.getQuestionConstruct(id, version, parentId, parentVersion);
		return new QuestionConstruct(doc, parentId, parentVersion);
	}

	@Override
	public void update(IModel model) throws DDIFtpException {
		DdiManager.getInstance().updateElement(model.getDocument(),
				model.getId(), model.getVersion());
	}
}
