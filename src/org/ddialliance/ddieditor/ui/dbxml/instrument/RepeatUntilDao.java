package org.ddialliance.ddieditor.ui.dbxml.instrument;

import java.util.List;

import org.ddialliance.ddi3.xml.xmlbeans.datacollection.RepeatUntilDocument;
import org.ddialliance.ddieditor.logic.identification.IdentificationManager;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.dbxml.IDao;
import org.ddialliance.ddieditor.ui.model.ElementType;
import org.ddialliance.ddieditor.ui.model.IModel;
import org.ddialliance.ddieditor.ui.model.Model;
import org.ddialliance.ddieditor.ui.model.instrument.RepeatUntil;
import org.ddialliance.ddiftp.util.DDIFtpException;

public class RepeatUntilDao implements IDao {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.ddialliance.ddieditor.ui.dbxml.Dao#getLightXmlObject(org.ddialliance
	 * .ddieditor.model.lightxmlobject.LightXmlObjectType)
	 */
	public List<LightXmlObjectType> getLightXmlObject(LightXmlObjectType parent)
			throws Exception {
		return getLightXmlObject("", "", parent.getId(), parent.getVersion());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.ddialliance.ddieditor.ui.dbxml.Dao#getLightXmlObject(java.lang.String
	 * , java.lang.String, java.lang.String, java.lang.String)
	 */
	public List<LightXmlObjectType> getLightXmlObject(String id,
			String version, String parentId, String parentVersion)
			throws Exception {
		return DdiManager.getInstance().getRepeatUntilsLight(id, version,
				parentId, parentVersion).getLightXmlObjectList()
				.getLightXmlObjectList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ddialliance.ddieditor.ui.dbxml.Dao#create(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	public RepeatUntil create(String id, String version, String parentId,
			String parentVersion) throws Exception {
		RepeatUntilDocument doc = RepeatUntilDocument.Factory.newInstance();
		doc.addNewRepeatUntil();
		IdentificationManager.getInstance().addIdentification(
				doc.getRepeatUntil(),
				ElementType.getElementType("RepeatUntil").getIdPrefix(), "");
		IdentificationManager.getInstance().addVersionInformation(
				doc.getRepeatUntil(), null, null);
		return new RepeatUntil(doc, parentId, parentVersion);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ddialliance.ddieditor.ui.dbxml.Dao#getModel(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	public RepeatUntil getModel(String id, String version, String parentId,
			String parentVersion) throws Exception {
		RepeatUntilDocument doc = DdiManager.getInstance().getRepeatUntil(id,
				version, parentId, parentVersion);
		return new RepeatUntil(doc, parentId, parentVersion);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.ddialliance.ddieditor.ui.dbxml.Dao#create(org.ddialliance.ddieditor
	 * .ui.model.IModel)
	 */
	public void create(IModel model) throws DDIFtpException {
		DdiManager.getInstance().createElement(model.getDocument(),
				model.getParentId(), model.getParentVersion(), "ControlConstructScheme");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.ddialliance.ddieditor.ui.dbxml.Dao#update(org.ddialliance.ddieditor
	 * .ui.model.IModel)
	 */
	public void update(IModel model) throws DDIFtpException {
		IdentificationManager.getInstance().addVersionInformation(
				((RepeatUntilDocument) model.getDocument()).getRepeatUntil(),
				null, null);
		DdiManager.getInstance().updateElement(model.getDocument(),
				model.getId(), model.getVersion());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ddialliance.ddieditor.ui.dbxml.Dao#delete(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	public void delete(String id, String version, String parentId,
			String parentVersion) throws Exception {
		Model model = getModel(id, version, parentId, parentVersion);
		DdiManager.getInstance().deleteElement(model.getDocument(),
				model.getParentId(), model.getParentVersion(),
				"ControlConstructScheme");
	}
}
