package org.ddialliance.ddieditor.ui.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.AbstractIdentifiableType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.AbstractVersionableType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.DescriptionDocument;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.LabelDocument;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.LabelType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.StructuredStringType;
import org.ddialliance.ddieditor.logic.identification.IdentificationManager;
import org.ddialliance.ddieditor.persistenceaccess.maintainablelabel.MaintainableLabelQueryResult;
import org.ddialliance.ddieditor.persistenceaccess.maintainablelabel.MaintainableLabelUpdateElement;
import org.ddialliance.ddieditor.ui.util.LanguageUtil;
import org.ddialliance.ddieditor.util.XmlObjectUtil;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.ReflectionUtil;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.ddialliance.ddiftp.util.xml.XmlBeansUtil;

/**
 * Label and description model - provides 'get' and 'set' methods for accessing
 * Labels and Descriptions sub-elements
 */
public abstract class LabelDescriptionScheme extends Model implements IModel,
		ILabelDescription {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			LabelDescriptionScheme.class);

	private MaintainableLabelQueryResult maintainableLabelQueryResult;
	private List<MaintainableLabelUpdateElement> maintainableUpdateLabels = new ArrayList<MaintainableLabelUpdateElement>();
	private List<MaintainableLabelUpdateElement> maintainableUpdateDescriptions = new ArrayList<MaintainableLabelUpdateElement>();

	private List<LabelType> labels = new ArrayList<LabelType>(); // aka updates
	private List<StructuredStringType> descrs = new ArrayList<StructuredStringType>();

	private XmlOptions xmlOptions = new XmlOptions();

	/**
	 * Constructor
	 * 
	 * @param parentId
	 * @param parentVersion
	 * @param maintainableLabelQueryResult
	 * @throws DDIFtpException
	 */
	public LabelDescriptionScheme(
			MaintainableLabelQueryResult maintainableLabelQueryResult,
			String parentId, String parentVersion) throws DDIFtpException {
		// init super
		super(maintainableLabelQueryResult.getId(),
				maintainableLabelQueryResult.getVersion(), parentId,
				parentVersion, maintainableLabelQueryResult.getAgency());

		// init maintainable labels
		this.maintainableLabelQueryResult = maintainableLabelQueryResult;
		if (maintainableLabelQueryResult != null) {
			// labels
			XmlObject[] xmlObjects = maintainableLabelQueryResult
					.getSubElement("Label");
			for (int i = 0; i < xmlObjects.length; i++) {
				maintainableUpdateLabels
						.add(new MaintainableLabelUpdateElement(
								(LabelType) ((LabelDocument) xmlObjects[i])
										.getLabel(), null /* NOP */));
			}

			// description
			xmlObjects = maintainableLabelQueryResult
					.getSubElement("Description");
			for (int i = 0; i < xmlObjects.length; i++) {
				maintainableUpdateDescriptions
						.add(new MaintainableLabelUpdateElement(
								(StructuredStringType) ((DescriptionDocument) xmlObjects[i])
										.getDescription(), null /* NOP */));
			}
		}

		// init updates
		syncUpdates(labels, maintainableUpdateLabels);
		syncUpdates(descrs, maintainableUpdateDescriptions);

		xmlOptions.setSaveOuter();
		xmlOptions.setSaveAggressiveNamespaces();
	}

	public static MaintainableLabelQueryResult createLabelDescriptionScheme(
			String localName) throws Exception {
		// create doc and add new instance of type
		XmlObject xmlObject = XmlObjectUtil.createXmlObjectDocument(localName);
		xmlObject = XmlObjectUtil.addXmlObjectType(xmlObject);

		// add version and identification
		IdentificationManager.getInstance().addIdentification(
				(AbstractIdentifiableType) xmlObject,
				ElementType.getElementType(localName).getIdPrefix(), null);
		IdentificationManager.getInstance().addVersionInformation(
				(AbstractVersionableType) xmlObject, null, null);

		// maintainableLabel - identification
		MaintainableLabelQueryResult maintainableLabelQueryResult = new MaintainableLabelQueryResult();
		maintainableLabelQueryResult.setId(XmlBeansUtil
				.getXmlAttributeValue(xmlObject.xmlText(), "id=\""));
		maintainableLabelQueryResult.setVersion(XmlBeansUtil
				.getXmlAttributeValue(xmlObject.xmlText(), "version=\""));
		maintainableLabelQueryResult.setAgency(XmlBeansUtil
				.getXmlAttributeValue(xmlObject.xmlText(), "agency=\""));
		maintainableLabelQueryResult.setLocalName(localName);

		return maintainableLabelQueryResult;
	}

	public MaintainableLabelQueryResult getMaintainableLabelQueryResult() {
		return maintainableLabelQueryResult;
	}

	/**
	 * Get all not deleted XML objects from list of maintainable update elements
	 * 
	 * @param maintainableLabelUpdateElements
	 *            list of maintainable update elements
	 * @return list of simple type xmlobjects, not set for deletion
	 */
	private List<XmlObject> getNonDeletedElements(
			List<MaintainableLabelUpdateElement> maintainableLabelUpdateElements)
			throws DDIFtpException {
		List<XmlObject> result = new ArrayList<XmlObject>();
		for (MaintainableLabelUpdateElement maintainableLabelUpdateElement : maintainableLabelUpdateElements) {
			if (maintainableLabelUpdateElement.getCrudValue() == null
					|| maintainableLabelUpdateElement.getCrudValue() > -1) {
				try {
					result.add(maintainableLabelUpdateElement
							.getValueAsXmlObject());
				} catch (Exception e) {
					throw new DDIFtpException(e);
				}
			}
		}
		return result;
	}

	// never used ...
	// private int findXmlObject(List<MaintainableLabelUpdateElement> elements,
	// XmlObject xmlObject) throws DDIFtpException {
	//
	// for (int i = 0; i < elements.size(); i++) {
	// try {
	// if (elements.get(i).getValueAsXmlObject() == xmlObject) {
	// return i;
	// }
	// } catch (Exception e) {
	// throw new DDIFtpException(e);
	// }
	// }
	// return -1;
	// }

	private List<MaintainableLabelUpdateElement> getDirtyUpdateElements(
			List<MaintainableLabelUpdateElement> maintainableLabelUpdateElements) {
		List<MaintainableLabelUpdateElement> result = new ArrayList<MaintainableLabelUpdateElement>();
		for (MaintainableLabelUpdateElement maintainableLabelUpdateElement : maintainableLabelUpdateElements) {
			if (maintainableLabelUpdateElement.getCrudValue() != null) {
				result.add(maintainableLabelUpdateElement);
			}
		}
		return result;
	}

	/**
	 * Sync. MaintainableLabelUpdateElement with item elements (update by
	 * translation pop-up)
	 * 
	 * Note: Do not find new items
	 * 
	 * @throws DDIFtpException
	 */
	private void syncMaintainableLabelUpdateElements(
			String string,
			List<MaintainableLabelUpdateElement> maintainableLabelUpdateElements,
			List<?> updates) throws DDIFtpException {

		// loop over all MaintainableLabelUpdateElements
		for (int i = 0; i < maintainableLabelUpdateElements.size(); i++) {
			boolean found = false;
			// find corresponding objects
			for (int j = 0; j < updates.size(); j++) {
				try {
					if (log.isDebugEnabled()) {
						log.debug("Update: " + updates.get(j) + " ~ MLUP: "
								+ maintainableLabelUpdateElements.get(i));
					}
					if (((XmlObject) updates.get(j)).xmlText().equals(
							maintainableLabelUpdateElements.get(i)
									.getValueAsXmlObject().xmlText())) {
						found = true;
						break;
					}
				} catch (Exception e) {
					throw new DDIFtpException(e);
				}
			}
			if (found && string.length() > 0) {
				// mark update element as updated
				maintainableLabelUpdateElements.get(i).setCrudValue(i + 1);
				if (updates.get(i) instanceof LabelType) {
					maintainableLabelUpdateElements.get(i).setValue(
							((LabelType) updates.get(i)).xmlText(xmlOptions));
				} else if (updates.get(i) instanceof StructuredStringType) {
					maintainableLabelUpdateElements.get(i).setValue(
							((StructuredStringType) updates.get(i)).xmlText());
				} else {
					throw new DDIFtpException("Unsupported object type: "
							+ updates.getClass().getName());
				}
			} else {
				// mark update element as deleted
				maintainableLabelUpdateElements.get(i).setCrudValue(
						(i + 1) * -1);
			}
		}
		return;
	}

	private void syncUpdates(List<?> obj,
			List<MaintainableLabelUpdateElement> upds) throws DDIFtpException {
		// loop over list of items
		for (int i = 0; i < obj.size(); i++) {
			// find corresponding update element
			boolean found = false;
			for (int j = 0; j < upds.size(); j++) {
				try {
					if (obj.get(i) == upds.get(j).getValueAsXmlObject()) {
						found = true;
						continue;
					}
				} catch (Exception e) {
					throw new DDIFtpException(e);
				}
			}
			if (!found) {
				if (obj.get(i) instanceof LabelType) {
					upds.add(new MaintainableLabelUpdateElement((LabelType) obj
							.get(i), MaintainableLabelUpdateElement.NEW));
				} else if (obj.get(i) instanceof StructuredStringType) {
					upds.add(new MaintainableLabelUpdateElement(
							(StructuredStringType) obj.get(i),
							MaintainableLabelUpdateElement.NEW));

				} else {
					throw new DDIFtpException("Unsupported object type: "
							+ obj.getClass().getName());
				}
			}
		}
		return;
	}

	//
	// label
	//
	@Override
	public LabelType setDisplayLabel(String text) throws DDIFtpException {

		// sync
		syncMaintainableLabelUpdateElements(text, maintainableUpdateLabels,
				labels);
		syncUpdates(labels, maintainableUpdateLabels);

		// update existing label with language as key
		LabelType label = null;
		if (labels.size() > 0) {
			label = (LabelType) XmlBeansUtil.getLangElement(LanguageUtil
					.getDisplayLanguage(), labels);
			if (label != null) {
				// Label in Display language - found
				if (text.length() > 0) {
					// replace Label:
					XmlBeansUtil.setTextOnMixedElement(label, text);
					syncMaintainableLabelUpdateElements(text,
							maintainableUpdateLabels, labels);
					syncUpdates(labels, maintainableUpdateLabels);
				}
			}
		}

		// new label
		if (label == null && text != null && !text.equals("")) {
			label = LabelDocument.Factory.newInstance().addNewLabel();
			label.setTranslated(false);
			label.setTranslatable(true);
			label.setLang(LanguageUtil.getOriginalLanguage());
			XmlBeansUtil.setTextOnMixedElement(label, text);
			labels.add(label);
			MaintainableLabelUpdateElement newMaintainableLabelUpdateElement = new MaintainableLabelUpdateElement(
					null, MaintainableLabelUpdateElement.NEW);
			newMaintainableLabelUpdateElement.setValue(label
					.xmlText(xmlOptions));
			maintainableUpdateLabels.add(newMaintainableLabelUpdateElement);
		}

		return label;
	}

	@Override
	public String getDisplayLabel() throws DDIFtpException {
		if (maintainableUpdateLabels.size() > 0) {
			return XmlBeansUtil.getTextOnMixedElement((XmlObject) XmlBeansUtil
					.getLangElement(LanguageUtil.getDisplayLanguage(),
							getNonDeletedElements(maintainableUpdateLabels)));
		}

		// No label found - not an error
		return "";
	}

	@Override
	public List<LabelType> getLabels() throws DDIFtpException {
		return labels;
	}

	public LabelType[] getLabelsAsArray() throws DDIFtpException {
		return labels.toArray(new LabelType[] {});
	}

	//
	// description
	//
	@Override
	public StructuredStringType setDisplayDescr(String text)
			throws DDIFtpException {
		// sync
		syncMaintainableLabelUpdateElements(text,
				maintainableUpdateDescriptions, descrs);
		syncUpdates(descrs, maintainableUpdateDescriptions);

		// update
		StructuredStringType descr = null;
		if (descrs.size() > 0) {
			descr = (StructuredStringType) XmlBeansUtil.getLangElement(
					LanguageUtil.getDisplayLanguage(), descrs);
			if (descr != null) {
				if (text.length() > 0) {
					XmlBeansUtil.setTextOnMixedElement(descr, text);
				}
			}
		}

		// new
		if (descr == null && text != null && !text.equals("")) {
			StructuredStringType descriptionType = (StructuredStringType) descr;
			descriptionType = LabelDocument.Factory.newInstance().addNewLabel();
			descriptionType.setTranslated(false);
			descriptionType.setTranslatable(true);
			descriptionType.setLang(LanguageUtil.getOriginalLanguage());
			XmlBeansUtil.setTextOnMixedElement(descriptionType, text);
			descrs.add(descriptionType);
			maintainableUpdateDescriptions
					.add(new MaintainableLabelUpdateElement(descriptionType,
							MaintainableLabelUpdateElement.NEW));
		}
		return descr;
	}

	@Override
	public String getDisplayDescr() throws DDIFtpException {
		if (maintainableUpdateDescriptions.size() > 0) {
			return XmlBeansUtil
					.getTextOnMixedElement((XmlObject) XmlBeansUtil
							.getLangElement(
									LanguageUtil.getDisplayLanguage(),
									getNonDeletedElements(maintainableUpdateDescriptions)));
		}

		// No description found - not an error
		return "";
	}

	@Override
	public List<StructuredStringType> getDescrs() throws DDIFtpException {
		return descrs;
	}

	public StructuredStringType[] getDescrsAsArray() throws DDIFtpException {
		return descrs.toArray(new StructuredStringType[] {});
	}

	@Override
	public XmlObject getDocument() throws DDIFtpException {
		XmlObject result = XmlObjectUtil
				.createXmlObjectDocument(maintainableLabelQueryResult
						.getLocalName());
		XmlObject type = XmlObjectUtil.addXmlObjectType(result);

		// identification
		try {
			// id
			ReflectionUtil.invokeMethod(type, "setId", false,
					maintainableLabelQueryResult.getId());
			// version
			if (maintainableLabelQueryResult.getVersion() != null
					&& (!maintainableLabelQueryResult.getVersion().equals(""))) {
				ReflectionUtil.invokeMethod(type, "setVersion", false,
						maintainableLabelQueryResult.getVersion());
			}
			// agency
			if (maintainableLabelQueryResult.getAgency() != null
					&& (!maintainableLabelQueryResult.getAgency().equals(""))) {
				ReflectionUtil.invokeMethod(type, "setAgency", false,
						maintainableLabelQueryResult.getAgency());
			}

			// description
			ReflectionUtil.invokeMethod(type, "setDescriptionArray", false,
					new Object[] { getDescrsAsArray() });
		} catch (Exception e) {
			throw new DDIFtpException(e);
		}

		// names/ label
		try {
			// label default
			ReflectionUtil.invokeMethod(type, "setLabelArray", false,
					new Object[] { getLabelsAsArray() });
		} catch (Exception e) {
			// name
			try {
				ReflectionUtil.invokeMethod(type, "set"
						+ maintainableLabelQueryResult.getLocalName()
						+ "NameArray", false,
						new Object[] { getLabelsAsArray() });
			} catch (Exception e1) {
				throw new DDIFtpException(e1);
			}
		}
		return result;
	}

	/**
	 * Return list of UpdateElements
	 * 
	 * @return List<MaintainableLabelUpdateElement>
	 */
	public List<MaintainableLabelUpdateElement> getMaintainableLabelUpdateElements() {
		List<MaintainableLabelUpdateElement> result = new ArrayList<MaintainableLabelUpdateElement>();
		// dirty
		result.addAll(getDirtyUpdateElements(maintainableUpdateLabels));
		result.addAll(getDirtyUpdateElements(maintainableUpdateDescriptions));

		return result;
	}

	@Override
	public void validate() throws Exception {
		// not implemented
	}
}
