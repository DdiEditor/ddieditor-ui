package org.ddialliance.ddieditor.ui.dbxml.instrument;

import java.util.List;

import org.ddialliance.ddi3.xml.xmlbeans.datacollection.ComputationItemDocument;
import org.ddialliance.ddieditor.logic.identification.IdentificationManager;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.dbxml.IDao;
import org.ddialliance.ddieditor.ui.model.ElementType;
import org.ddialliance.ddieditor.ui.model.IModel;
import org.ddialliance.ddieditor.ui.model.Model;
import org.ddialliance.ddieditor.ui.model.instrument.ComputationItem;
import org.ddialliance.ddiftp.util.DDIFtpException;

public class ComputationItemDao implements IDao {
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
		return DdiManager.getInstance().getComputationItemsLight(id, version,
				parentId, parentVersion).getLightXmlObjectList()
				.getLightXmlObjectList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ddialliance.ddieditor.ui.dbxml.Dao#create(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	public ComputationItem create(String id, String version, String parentId,
			String parentVersion) throws Exception {
		ComputationItemDocument doc = ComputationItemDocument.Factory.newInstance();
		doc.addNewComputationItem();
		IdentificationManager.getInstance()
				.addIdentification(
						doc.getComputationItem(),
						ElementType.getElementType("ComputationItem")
								.getIdPrefix(), "");
		IdentificationManager.getInstance().addVersionInformation(
				doc.getComputationItem(), null, null);
		return new ComputationItem(doc, parentId, parentVersion);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ddialliance.ddieditor.ui.dbxml.Dao#getModel(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	public ComputationItem getModel(String id, String version, String parentId,
			String parentVersion) throws Exception {
		ComputationItemDocument doc = DdiManager.getInstance().getComputationItem(id,
				version, parentId, parentVersion);
		return new ComputationItem(doc, parentId, parentVersion);
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
		// IdentificationManager.getInstance().addVersionInformation(
		// ((ComputationItemDocument) model.getDocument()).getComputationItem(),
		// null, null);
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
