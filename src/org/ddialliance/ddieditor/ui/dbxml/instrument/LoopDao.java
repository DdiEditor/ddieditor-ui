package org.ddialliance.ddieditor.ui.dbxml.instrument;

import java.util.List;

import org.ddialliance.ddi3.xml.xmlbeans.datacollection.LoopDocument;
import org.ddialliance.ddieditor.logic.identification.IdentificationManager;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.dbxml.IDao;
import org.ddialliance.ddieditor.ui.model.ElementType;
import org.ddialliance.ddieditor.ui.model.IModel;
import org.ddialliance.ddieditor.ui.model.Model;
import org.ddialliance.ddieditor.ui.model.instrument.Loop;
import org.ddialliance.ddiftp.util.DDIFtpException;

public class LoopDao implements IDao {
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
		return DdiManager.getInstance().getLoopsLight(id, version,
				parentId, parentVersion).getLightXmlObjectList()
				.getLightXmlObjectList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ddialliance.ddieditor.ui.dbxml.Dao#create(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	public Loop create(String id, String version, String parentId,
			String parentVersion) throws Exception {
		LoopDocument doc = LoopDocument.Factory
				.newInstance();
		doc.addNewLoop();
		IdentificationManager.getInstance()
				.addIdentification(
						doc.getLoop(),
						ElementType.getElementType("Loop")
								.getIdPrefix(), "");
		IdentificationManager.getInstance().addVersionInformation(
				doc.getLoop(), null, null);
		return new Loop(doc, parentId, parentVersion);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ddialliance.ddieditor.ui.dbxml.Dao#getModel(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	public Loop getModel(String id, String version, String parentId,
			String parentVersion) throws Exception {
		LoopDocument doc = DdiManager.getInstance()
				.getLoop(id, version, parentId, parentVersion);
		return new Loop(doc, parentId, parentVersion);
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
				model.getParentId(), model.getParentVersion(),
				"ControlConstructScheme");
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
				((LoopDocument) model.getDocument())
						.getLoop(), null, null);
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
