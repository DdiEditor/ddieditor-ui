package org.ddialliance.ddieditor.ui.dbxml.instrument;

import java.util.List;

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
	
	/**
	 * Get Light XML Object
	 * 
	 * @param parentInstrumentScheme
	 * @return List<LightXmlObjectType>
	 * @throws Exception
	 */
	public List<LightXmlObjectType> getLightXmlObject(
			LightXmlObjectType parentInstrumentScheme) throws Exception {

		log.debug("Instruments.getInstrumentsLight()");

		return getLightXmlObject("", "", parentInstrumentScheme.getId(),
				parentInstrumentScheme.getVersion());
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
	public List<LightXmlObjectType> getLightXmlObject(String id,
			String version, String parentId, String parentVersion)
			throws Exception {

		log.debug("Instruments.getInstrumentsLight()");

		List<LightXmlObjectType> lightXmlObjectTypeList = DdiManager
				.getInstance().getInstrumentsLight(id, version, parentId,
						parentVersion).getLightXmlObjectList()
				.getLightXmlObjectList();

		return lightXmlObjectTypeList;
	}

	/**
	 * Create model
	 * 
	 * @param id
	 * @param version
	 * @param parentId
	 * @param parentVersion
	 * @return Instrument
	 * @throws Exception
	 */
	public Instrument create(String id, String version, String parentId,
			String parentVersion) throws Exception {
		InstrumentDocument doc = InstrumentDocument.Factory.newInstance();
		InstrumentType type = doc.addNewInstrument();
		
		IdentificationManager.getInstance()
		.addIdentification(
				doc.getInstrument(),
				ElementType.getElementType(type.getDomNode().getLocalName())
				.getIdPrefix(), "");
		IdentificationManager.getInstance().addVersionInformation(
				doc.getInstrument(), null, null);

		Instrument model = new Instrument(doc, parentId, parentVersion);
		return model;
	}

	/**
	 * Get model
	 * 
	 * @param id
	 * @param version
	 * @param parentId
	 * @param parentVersion
	 * @return Instrument
	 * @throws Exception
	 */
	public Instrument getModel(String id, String version, String parentId,
			String parentVersion) throws Exception {
		log.debug("Instruments.getInstrument(" + id + ")");

		InstrumentDocument doc = DdiManager.getInstance().getInstrument(id,
				version, parentId, parentVersion);
		Instrument model = new Instrument(doc, parentId, parentVersion);

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
		DdiManager.getInstance().createElement(model.getDocument(),
				model.getParentId(), model.getParentVersion(), "datacollection__DataCollection");
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
		DdiManager.getInstance().updateElement(model.getDocument(),
				model.getId(), model.getVersion());
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
	public void delete(String id, String version, String parentId,
			String parentVersion) throws Exception {
		log.debug("Delete DBXML");
		IModel  model = getModel(id, version, parentId, parentVersion);
		DdiManager.getInstance()
				.deleteElement(model.getDocument(), model.getParentId(),
						model.getParentVersion(), "datacollection__DataCollection");
	}
}
