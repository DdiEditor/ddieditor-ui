package org.ddialliance.ddieditor.ui.dbxml.instrument;

import java.util.List;

import org.ddialliance.ddi3.xml.xmlbeans.datacollection.StatementItemDocument;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.StatementItemType;
import org.ddialliance.ddieditor.logic.identification.IdentificationManager;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.dbxml.IDao;
import org.ddialliance.ddieditor.ui.dbxml.XmlEntities;
import org.ddialliance.ddieditor.ui.model.ElementType;
import org.ddialliance.ddieditor.ui.model.IModel;
import org.ddialliance.ddieditor.ui.model.instrument.StatementItem;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;

public class StatementItemDao extends XmlEntities implements IDao {
	private Log log = LogFactory.getLog(LogType.SYSTEM, StatementItemDao.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.ddialliance.ddieditor.ui.dbxml.Dao#getLightXmlObject(org.ddialliance
	 * .ddieditor.model.lightxmlobject.LightXmlObjectType)
	 */
	public List<LightXmlObjectType> getLightXmlObject(
			LightXmlObjectType parentStatementItemScheme) throws Exception {
		return getLightXmlObject("", "", parentStatementItemScheme.getId(),
				parentStatementItemScheme.getVersion());
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
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ddialliance.ddieditor.ui.dbxml.Dao#create(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	public StatementItem create(String id, String version, String parentId,
			String parentVersion) throws Exception {
		StatementItemDocument doc = StatementItemDocument.Factory.newInstance();
		StatementItemType type = doc.addNewStatementItem();
		IdentificationManager.getInstance()
				.addIdentification(
						type,
						ElementType.getElementType("StatementItem")
								.getIdPrefix(), null);
		StatementItem model = new StatementItem(doc, parentId, parentVersion);
		return model;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ddialliance.ddieditor.ui.dbxml.Dao#getModel(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	public StatementItem getModel(String id, String version, String parentId,
			String parentVersion) throws Exception {
		StatementItemDocument doc = DdiManager.getInstance().getStatementItem(
				id, version, parentId, parentVersion);
		StatementItem model = new StatementItem(doc, parentId, parentVersion);
		return model;
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
				model.getParentId(), model.getParentVersion(), "StatementItem");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.ddialliance.ddieditor.ui.dbxml.Dao#update(org.ddialliance.ddieditor
	 * .ui.model.IModel)
	 */
	public void update(IModel model) throws DDIFtpException {
		// TODO Version Control - not supported
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
		StatementItem model = getModel(id, version, parentId, parentVersion);
		DdiManager.getInstance().deleteElement(model.getDocument(),
				model.getParentId(), model.getParentVersion(),
				"ControlConstructScheme");
	}
}
