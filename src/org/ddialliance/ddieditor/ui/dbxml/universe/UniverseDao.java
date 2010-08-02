package org.ddialliance.ddieditor.ui.dbxml.universe;

import java.util.List;

import org.ddialliance.ddi3.xml.xmlbeans.conceptualcomponent.UniverseDocument;
import org.ddialliance.ddieditor.logic.identification.IdentificationManager;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.dbxml.IDao;
import org.ddialliance.ddieditor.ui.model.ElementType;
import org.ddialliance.ddieditor.ui.model.IModel;
import org.ddialliance.ddieditor.ui.model.universe.Universe;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;

public class UniverseDao implements IDao {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, UniverseDao.class);

	/**
	 * Get Light XML Object
	 * 
	 * @param parentUniverseScheme
	 * @return List<LightXmlObjectType>
	 * @throws Exception
	 */
	public List<LightXmlObjectType> getLightXmlObject(LightXmlObjectType parentUniverseScheme) throws Exception {

		log.debug("UniverseDao.getLightXmlObject()");

		return getLightXmlObject("", "", parentUniverseScheme.getId(), parentUniverseScheme.getVersion());
	}

	/**
	 * Get Light XML Object
	 * 
	 * @param id
	 * @param version
	 * @param parentId
	 * @param parentVersion
	 * @return List<LightXmlObjectType>
	 * @throws Exception
	 */
	public List<LightXmlObjectType> getLightXmlObject(String id, String version, String parentId, String parentVersion)
			throws Exception {

		log.debug("UniverseDao.getLightXmlObject()");

		List<LightXmlObjectType> lightXmlObjectTypeList = DdiManager.getInstance().getUniversesLight(id, version,
				parentId, parentVersion).getLightXmlObjectList().getLightXmlObjectList();

		return lightXmlObjectTypeList;
	}

	/**
	 * Create model
	 * 
	 * @param id
	 * @param version
	 * @param parentId
	 * @param parentVersion
	 * @return Universe
	 * @throws Exception
	 */
	public Universe create(String id, String version, String parentId, String parentVersion) throws Exception {

		UniverseDocument doc = UniverseDocument.Factory.newInstance();
		IdentificationManager.getInstance().addIdentification(
				doc.addNewUniverse(),
				ElementType.getElementType("Universe").getIdPrefix(), null);
		IdentificationManager.getInstance().addVersionInformation(
				doc.getUniverse(), null, null);
		Universe model = new Universe(doc, parentId, parentVersion);
		return model;
	}

	/**
	 * Get model
	 * 
	 * @param id
	 * @param version
	 * @param parentId
	 * @param parentVersion
	 * @return Universe
	 * @throws Exception
	 */
	public Universe getModel(String id, String version, String parentId, String parentVersion) throws Exception {
		log.debug("UniverseDao.getUniverse(" + id + ")");

		 UniverseDocument doc = DdiManager.getInstance().getUniverse(id,
		 version, parentId, parentVersion);
		 Universe model = new Universe(doc, parentId, parentVersion);
		 return model;
	}

	/**
	 * Create new DBXML
	 * 
	 * @param model
	 *            model
	 * @param parentId
	 *            parent id
	 * @param parentVersion
	 *            parent version
	 * @throws DDIFtpException
	 */
	public void create(IModel model) throws DDIFtpException {
		DdiManager.getInstance().createElement(model.getDocument(), model.getParentId(), model.getParentVersion(),
				"UniverseScheme");
	}

	/**
	 * 
	 * Update DBXML
	 * 
	 * @param model
	 *            model
	 * @throws DDIFtpException
	 */
	public void update(IModel model) throws DDIFtpException {
		DdiManager.getInstance().updateElement(model.getDocument(), model.getId(), model.getVersion());
	}

	/**
	 * 
	 * Delete DBXML
	 * 
	 * @param id
	 *            Identification
	 * @param version
	 *            Version
	 * @param parentId
	 *            Parent Identification
	 * @param parentVersion
	 *            Parent Version
	 * @throws Exception
	 */
	public void delete(String id, String version, String parentId, String parentVersion) throws Exception {
		log.debug("UniverseDao.delete: Delete DBXML");
		IModel model = getModel(id, version, parentId, parentVersion);
		DdiManager.getInstance().deleteElement(model.getDocument(), model.getParentId(), model.getParentVersion(),
				"UniverseScheme");
	}
}
