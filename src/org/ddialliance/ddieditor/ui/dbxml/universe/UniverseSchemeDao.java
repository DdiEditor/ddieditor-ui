package org.ddialliance.ddieditor.ui.dbxml.universe;

import java.util.List;

import org.ddialliance.ddi3.xml.xmlbeans.conceptualcomponent.UniverseSchemeDocument;
import org.ddialliance.ddieditor.logic.identification.IdentificationManager;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.persistenceaccess.maintainablelabel.MaintainableLabelQueryResult;
import org.ddialliance.ddieditor.ui.dbxml.IDao;
import org.ddialliance.ddieditor.ui.model.ElementType;
import org.ddialliance.ddieditor.ui.model.IModel;
import org.ddialliance.ddieditor.ui.model.LabelDescriptionScheme;
import org.ddialliance.ddieditor.ui.model.universe.UniverseScheme;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;

public class UniverseSchemeDao implements IDao {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			UniverseSchemeDao.class);

	/**
	 * Create Universe Scheme object
	 * 
	 * @param id
	 * @param version
	 * @param parentId
	 * @param parentVersion
	 * @param schemeQueryResult
	 *            (or null)
	 * @return StudyUnit
	 * @throws Exception
	 */
	public UniverseScheme create(String id, String version,
			String parentId, String parentVersion,
			MaintainableLabelQueryResult maintainableLabelQueryResult)
			throws Exception {
		UniverseSchemeDocument doc = UniverseSchemeDocument.Factory
				.newInstance();
		IdentificationManager.getInstance().addIdentification(
				doc.addNewUniverseScheme(),
				ElementType.getElementType("UniverseScheme").getIdPrefix(),
				null);
		IdentificationManager.getInstance().addVersionInformation(
				doc.getUniverseScheme(), null, null);

		UniverseScheme universeScheme = new UniverseScheme(doc
				.getUniverseScheme().getId(), doc.getUniverseScheme()
				.getVersion(), parentId, parentVersion, "TODO",
				maintainableLabelQueryResult);

		return universeScheme;
	}

	@Override
	public IModel create(String id, String version, String parentId,
			String parentVersion) throws Exception {

		// log.debug("UniverseSchemeDao.create()");
		// UniverseSchemeDocument doc =
		// UniverseSchemeDocument.Factory.newInstance();
		// IdentificationManager.getInstance().addIdentification(doc.addNewUniverseScheme(),
		// ElementType.getElementType("UniverseScheme").getIdPrefix(), null);
		// IdentificationManager.getInstance().addVersionInformation(doc.getUniverseScheme(),
		// null, null);
		// UniverseScheme universeScheme = new UniverseScheme(doc, parentId,
		// parentVersion);
		return create(id, version, parentId, parentVersion,
				new MaintainableLabelQueryResult());
	}

	@Override
	public void create(IModel model) throws DDIFtpException {
		DdiManager.getInstance().createElement(model.getDocument(),
				model.getParentId(), model.getParentVersion(),
				"ConceptualComponent");
	}

	@Override
	public void delete(String id, String version, String parentId,
			String parentVersion) throws Exception {
		IModel model = getModel(id, version, parentId, parentVersion);
		DdiManager.getInstance().deleteElement(model.getDocument(),
				model.getParentId(), model.getParentVersion(),
				"ConceptualComponent");
	}

	@Override
	public List<LightXmlObjectType> getLightXmlObject(
			LightXmlObjectType parentUniverseScheme) throws Exception {
		return getLightXmlObject("", "", parentUniverseScheme.getId(),
				parentUniverseScheme.getVersion());
	}

	@Override
	public List<LightXmlObjectType> getLightXmlObject(String id,
			String version, String parentId, String parentVersion)
			throws Exception {
		List<LightXmlObjectType> lightXmlObjectTypeList = DdiManager
				.getInstance().getUniverseSchemesLight(id, version, parentId,
						parentVersion).getLightXmlObjectList()
				.getLightXmlObjectList();

		return lightXmlObjectTypeList;
	}

	@Override
	public UniverseScheme getModel(String id, String version, String parentId,
			String parentVersion) throws Exception {
		MaintainableLabelQueryResult maintainableLabelQueryResult = DdiManager
				.getInstance().getUniverseSchemeLabel(id, version, parentId,
						parentVersion);

		UniverseScheme universeScheme = create(id, version, parentId,
				parentVersion, maintainableLabelQueryResult);

		return universeScheme;
	}

	@Override
	public void update(IModel model) throws DDIFtpException {
		LabelDescriptionScheme ldScheme = (LabelDescriptionScheme)model;
		
		MaintainableLabelQueryResult universeSchemeQueryResult = ldScheme
				.getMaintainableLabelQueryResult();

		DdiManager.getInstance().updateMaintainableLabel(
				universeSchemeQueryResult, ldScheme.getMaintainableLabelUpdateElements());
	}
}