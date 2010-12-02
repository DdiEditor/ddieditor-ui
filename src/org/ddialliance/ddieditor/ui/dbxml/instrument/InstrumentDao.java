package org.ddialliance.ddieditor.ui.dbxml.instrument;

import java.util.List;

import org.ddialliance.ddi3.xml.xmlbeans.datacollection.DataCollectionDocument;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.InstrumentDocument;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.InstrumentType;
import org.ddialliance.ddieditor.logic.identification.IdentificationManager;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.dbxml.IDao;
import org.ddialliance.ddieditor.ui.model.ElementType;
import org.ddialliance.ddieditor.ui.model.IModel;
import org.ddialliance.ddieditor.ui.model.instrument.Instrument;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;

public class InstrumentDao implements IDao {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			InstrumentDao.class);

	@Override
	public List<LightXmlObjectType> getLightXmlObject(
			LightXmlObjectType parentInstrumentScheme) throws Exception {

		log.debug("Instruments.getInstrumentsLight()");

		return getLightXmlObject("", "", parentInstrumentScheme.getId(),
				parentInstrumentScheme.getVersion());
	}

	@Override
	public List<LightXmlObjectType> getLightXmlObject(String id,
			String version, String parentId, String parentVersion)
			throws Exception {

		log.debug("Instruments.getInstrumentsLight()");

		List<LightXmlObjectType> lightXmlObjectTypeList = DdiManager
				.getInstance()
				.getInstrumentsLight(id, version, parentId, parentVersion)
				.getLightXmlObjectList().getLightXmlObjectList();

		return lightXmlObjectTypeList;
	}

	@Override
	public Instrument create(String id, String version, String parentId,
			String parentVersion) throws Exception {
		InstrumentDocument doc = InstrumentDocument.Factory.newInstance();
		InstrumentType type = doc.addNewInstrument();

		IdentificationManager.getInstance().addIdentification(
				doc.getInstrument(),
				ElementType.getElementType(type.getDomNode().getLocalName())
						.getIdPrefix(), "");
		IdentificationManager.getInstance().addVersionInformation(
				doc.getInstrument(), null, null);

		Instrument model = new Instrument(doc, parentId, parentVersion);
		return model;
	}

	@Override
	public Instrument getModel(String id, String version, String parentId,
			String parentVersion) throws Exception {
		log.debug("Instruments.getInstrument(" + id + ")");

		InstrumentDocument doc = DdiManager.getInstance().getInstrument(id,
				version, parentId, parentVersion);
		Instrument model = new Instrument(doc, parentId, parentVersion);

		return model;
	}

	@Override
	public void create(IModel model) throws DDIFtpException {
		LightXmlObjectType lightXmlObjectType = null;
		if (model.getParentId() == null) {
			try {
				lightXmlObjectType = createDataCollection();
			} catch (Exception e) {
				throw new DDIFtpException(e);
			}
		}
		model.setParentId(lightXmlObjectType.getId());
		model.setParentVersion(lightXmlObjectType.getVersion());
		DdiManager.getInstance().createElement(model.getDocument(),
				model.getParentId(), model.getParentVersion(),
				"datacollection__DataCollection");
	}

	private LightXmlObjectType createDataCollection() throws Exception {
		// study unit
		LightXmlObjectType studyUnitLight = null;
		List<LightXmlObjectType> studyUnits = DdiManager.getInstance()
				.getStudyUnitsLight(null, null, null, null)
				.getLightXmlObjectList().getLightXmlObjectList();

		if (studyUnits.isEmpty()) {
			throw new DDIFtpException("No study unit");
		} else {
			studyUnitLight = DdiManager.createLightXmlObject(null, null,
					studyUnits.get(0).getId(), studyUnits.get(0).getVersion());
		}

		// data collection
		LightXmlObjectType dataColLight = DdiManager.createLightXmlObject(null,
				null, null, null);

		List<LightXmlObjectType> datacollectionList = DdiManager.getInstance()
				.getDataCollectionsLight(null, null, null, null)
				.getLightXmlObjectList().getLightXmlObjectList();
		if (datacollectionList.isEmpty()) {
			// new data collection
			DataCollectionDocument dataColDoc = DataCollectionDocument.Factory
					.newInstance();
			dataColDoc.addNewDataCollection();
			// to element type datacollection
			IdentificationManager.getInstance().addIdentification(
					dataColDoc.getDataCollection(),
					ElementType.DATA_COLLECTION.getIdPrefix(), null);
			IdentificationManager.getInstance().addVersionInformation(
					dataColDoc.getDataCollection(), null, null);
			dataColDoc.getDataCollection().setAgency(ElementType.getAgency());

			dataColLight.setId(dataColDoc.getDataCollection().getId());
			dataColLight
					.setVersion(dataColDoc.getDataCollection().getVersion());

			DdiManager.getInstance().createElement(dataColDoc,
					studyUnitLight.getId(), studyUnitLight.getVersion(),
					"studyunit__StudyUnit");
		} else {
			dataColLight.setId(datacollectionList.get(0).getId());
			dataColLight.setVersion(datacollectionList.get(0).getVersion());
		}
		return dataColLight;
	}

	@Override
	public void update(IModel model) throws DDIFtpException {
		DdiManager.getInstance().updateElement(model.getDocument(),
				model.getId(), model.getVersion());
	}

	@Override
	public void delete(String id, String version, String parentId,
			String parentVersion) throws Exception {
		IModel model = getModel(id, version, parentId, parentVersion);
		DdiManager.getInstance().deleteElement(model.getDocument(),
				model.getParentId(), model.getParentVersion(),
				"datacollection__DataCollection");
	}
}
