package org.ddialliance.ddieditor.ui.dbxml.universe;

import java.util.List;

import org.ddialliance.ddi3.xml.xmlbeans.conceptualcomponent.UniverseSchemeDocument;
import org.ddialliance.ddieditor.logic.identification.IdentificationManager;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.dbxml.IDao;
import org.ddialliance.ddieditor.ui.model.ElementType;
import org.ddialliance.ddieditor.ui.model.IModel;
import org.ddialliance.ddieditor.ui.model.universe.UniverseScheme;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;

public class UniverseSchemeDao implements IDao {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, UniverseSchemeDao.class);

	@Override
	public IModel create(String id, String version, String parentId, String parentVersion) throws Exception {

		log.debug("UniverseSchemeDao.create()");
		UniverseSchemeDocument doc = UniverseSchemeDocument.Factory.newInstance();
		IdentificationManager.getInstance().addIdentification(doc.addNewUniverseScheme(),
				ElementType.getElementType("UniverseScheme").getIdPrefix(), null);
		IdentificationManager.getInstance().addVersionInformation(doc.getUniverseScheme(), null, null);
		UniverseScheme universeScheme = new UniverseScheme(doc, parentId, parentVersion);
		return universeScheme;
	}

	@Override
	public void create(IModel model) throws DDIFtpException {

		log.debug("UniverseSchemeDao.create()");
		DdiManager.getInstance().createElement(model.getDocument(), model.getParentId(), model.getParentVersion(),
				"ConceptualComponent");
	}

	@Override
	public void delete(String id, String version, String parentId, String parentVersion) throws Exception {

		log.debug("Delete DBXML Universe Scheme");
		IModel model = getModel(id, version, parentId, parentVersion);
		DdiManager.getInstance().deleteElement(model.getDocument(), model.getParentId(), model.getParentVersion(),
				"ConceptualComponent");
	}

	@Override
	public List<LightXmlObjectType> getLightXmlObject(LightXmlObjectType parentUniverseScheme) throws Exception {

		log.debug("UniverseSchemeDao.getLightXmlObject()");
		return getLightXmlObject("", "", parentUniverseScheme.getId(), parentUniverseScheme.getVersion());
	}

	@Override
	public List<LightXmlObjectType> getLightXmlObject(String id, String version, String parentId, String parentVersion)
			throws Exception {

		log.debug("UniverseSchemeDao.getLightXmlObject()");
		List<LightXmlObjectType> lightXmlObjectTypeList = DdiManager.getInstance().getUniverseSchemesLight(id, version,
				parentId, parentVersion).getLightXmlObjectList().getLightXmlObjectList();

		return lightXmlObjectTypeList;
	}

	@Override
	public UniverseScheme getModel(String id, String version, String parentId, String parentVersion) throws Exception {

		log.debug("UniverseSchemeDao.getModel(" + id + ")");
		UniverseSchemeDocument doc = DdiManager.getInstance().getUniverseScheme(id, version, parentId, parentVersion);
		UniverseScheme model = new UniverseScheme(doc, parentId, parentVersion);

		return model;
	}

	@Override
	public void update(IModel model) throws DDIFtpException {
		DdiManager.getInstance().updateElement(model.getDocument(), model.getId(), model.getVersion());
	}
}