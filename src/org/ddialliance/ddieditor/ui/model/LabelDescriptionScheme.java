package org.ddialliance.ddieditor.ui.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.xmlbeans.XmlObject;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.AbstractMaintainableType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.LabelDocument;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.LabelType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.StructuredStringType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.impl.DescriptionDocumentImpl;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.impl.LabelDocumentImpl;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.impl.LabelTypeImpl;
import org.ddialliance.ddieditor.persistenceaccess.maintainablelabel.MaintainableLabelQueryResult;
import org.ddialliance.ddieditor.persistenceaccess.maintainablelabel.MaintainableLabelUpdateElement;
import org.ddialliance.ddieditor.ui.util.LanguageUtil;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.ddialliance.ddiftp.util.xml.XmlBeansUtil;

/**
 * Label and description model - provides 'get' and 'set' methods for accessing
 * Labels and Descriptions sub-elements
 */
public abstract class LabelDescriptionScheme extends Model implements IModel {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			LabelDescriptionScheme.class);

	private MaintainableLabelQueryResult maintainableLabelQueryResult;
	private List<MaintainableLabelUpdateElement> maintainableUpdateLabels = new ArrayList<MaintainableLabelUpdateElement>();
	private List<MaintainableLabelUpdateElement> maintainableUpdateDescriptions = new ArrayList<MaintainableLabelUpdateElement>();

	private List<LabelType> labels;
	private List<StructuredStringType> descriptions;

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
		super(maintainableLabelQueryResult.getId(),
				maintainableLabelQueryResult.getVersion(), parentId,
				parentVersion, maintainableLabelQueryResult.getAgency());
		this.maintainableLabelQueryResult = maintainableLabelQueryResult;
		if (maintainableLabelQueryResult != null) {
			XmlObject[] xml = maintainableLabelQueryResult
					.getSubElement("Label");
			for (int i = 0; i < xml.length; i++) {
				maintainableUpdateLabels
						.add(new MaintainableLabelUpdateElement(
								(LabelType) ((LabelDocumentImpl) xml[i])
										.getLabel(), null /* NOP */));
			}

			xml = maintainableLabelQueryResult.getSubElement("Description");
			for (int i = 0; i < xml.length; i++) {
				maintainableUpdateDescriptions
						.add(new MaintainableLabelUpdateElement(
								(StructuredStringType) ((DescriptionDocumentImpl) xml[i])
										.getDescription(), null /* NOP */));
			}
		}
	}

	/**
	 * Get all not deleted XML objects from list of maintainable update elements
	 * 
	 * @param elements
	 * @return
	 */
	private List<XmlObject> getNonDeletedElements(
			List<MaintainableLabelUpdateElement> elements)
			throws DDIFtpException {
		List<XmlObject> objects = new ArrayList<XmlObject>();
		for (Iterator<MaintainableLabelUpdateElement> iterator = elements
				.iterator(); iterator.hasNext();) {
			MaintainableLabelUpdateElement maintainableLabelUpdateElement = (MaintainableLabelUpdateElement) iterator
					.next();
			if (maintainableLabelUpdateElement.getCrudValue() == null
					|| maintainableLabelUpdateElement.getCrudValue() > -1) {
				try {
					objects.add(maintainableLabelUpdateElement
							.getValueAsXmlObject());
				} catch (Exception e) {
					throw new DDIFtpException(e);
				}
			}
		}
		return objects;
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
			List<MaintainableLabelUpdateElement> elements) {
		List<MaintainableLabelUpdateElement> dirtyElements = new ArrayList<MaintainableLabelUpdateElement>();
		for (Iterator<MaintainableLabelUpdateElement> iterator = elements
				.iterator(); iterator.hasNext();) {
			MaintainableLabelUpdateElement maintainableLabelUpdateElement = (MaintainableLabelUpdateElement) iterator
					.next();
			if (maintainableLabelUpdateElement.getCrudValue() != null) {
				dirtyElements.add(maintainableLabelUpdateElement);
			}
		}
		return dirtyElements;
	}

	/**
	 * Sync. MaintainableLabelUpdateElement with item elements (update by
	 * translation pop-up)
	 * 
	 * Note: Do not find new items
	 * 
	 * @throws DDIFtpException
	 */
	private void syncUpdWithObj(
			String string,
			List<MaintainableLabelUpdateElement> maintainableLabelUpdateElements,
			List<?> obj) throws DDIFtpException {

		// loop over all MaintainableLabelUpdateElements
		for (MaintainableLabelUpdateElement maintainableLabelUpdateElement : maintainableLabelUpdateElements) {

		}
		for (int i = 0; i < maintainableLabelUpdateElements.size(); i++) {
			boolean found = false;
			// find corresponding objects
			for (int j = 0; j < obj.size(); j++) {
				try {
					if (obj.get(j) == maintainableLabelUpdateElements.get(i)
							.getValueAsXmlObject()) {
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
				if (obj.get(i) instanceof LabelType) {
					maintainableLabelUpdateElements.get(i).setValue(
							((LabelType) obj.get(i)).xmlText());
				} else if (obj.get(i) instanceof StructuredStringType) {
					maintainableLabelUpdateElements.get(i).setValue(
							((StructuredStringType) obj.get(i)).xmlText());
				} else {
					throw new DDIFtpException("Unsupported object type: "
							+ obj.getClass().getName());
				}
			} else {
				// mark update element as deleted
				maintainableLabelUpdateElements.get(i).setCrudValue(
						(i + 1) * -1);
			}
		}
		return;
	}

	private void syncObjWithUpd(List<?> obj,
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

	/**
	 * Get Display Label of Simple Element.
	 * 
	 * @return String Label string
	 * @throws DDIFtpException
	 */
	public String getDisplayLabel() throws DDIFtpException {
		if (maintainableUpdateLabels.size() > 0) {
			try {
				return XmlBeansUtil
						.getTextOnMixedElement((XmlObject) XmlBeansUtil
								.getLangElement(
										LanguageUtil.getDisplayLanguage(),
										getNonDeletedElements(maintainableUpdateLabels)));
			} catch (DDIFtpException e) {
				// TODO - error handling
				e.printStackTrace();
				throw e;
			}
		}

		// No label found - not an error
		return "";
	}

	/**
	 * Set Label of Display Language.
	 * 
	 * @param string
	 * @return LabelType null - if label updated (no label is added) LabelType
	 *         is new label added
	 * @throws DDIFtpException
	 */
	public LabelType setDisplayLabel(String string) throws DDIFtpException {
		XmlObject label = null;

		// Sync. update elements with label type list
		syncUpdWithObj(string, maintainableUpdateLabels, labels);

		// Sync label type list with update elements
		syncObjWithUpd(labels, maintainableUpdateLabels);

		// Sync. label type list with update elements
		if (labels.size() > 0) {
			try {
				label = (XmlObject) XmlBeansUtil.getLangElement(LanguageUtil
						.getDisplayLanguage(), labels);
			} catch (DDIFtpException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
			if (label != null) {
				// Label in Display language - found
				if (string.length() > 0) {
					// replace Label:
					XmlBeansUtil.setTextOnMixedElement(((LabelTypeImpl) label),
							string);
					return null;
				}
			}
		}

		if (string.length() == 0) {
			// label does not exists and string is empty - ignore requestion
			return null;
		}

		// Create new label - in Original language:
		LabelType labelType = (LabelType) label;
		labelType = LabelDocument.Factory.newInstance().addNewLabel();
		labelType.setTranslated(false);
		labelType.setTranslatable(true);
		labelType.setLang(LanguageUtil.getOriginalLanguage());
		XmlBeansUtil.setTextOnMixedElement(labelType, string);
		labels.add(labelType);
		maintainableUpdateLabels.add(new MaintainableLabelUpdateElement(
				labelType, MaintainableLabelUpdateElement.NEW));

		return labelType;
	}

	/**
	 * Get Display Description of Simple Element.
	 * 
	 * @return String
	 */
	public String getDisplayDescr() throws DDIFtpException {

		if (maintainableUpdateDescriptions.size() > 0) {
			try {
				return XmlBeansUtil
						.getTextOnMixedElement((XmlObject) XmlBeansUtil
								.getLangElement(
										LanguageUtil.getDisplayLanguage(),
										getNonDeletedElements(maintainableUpdateDescriptions)));
			} catch (DDIFtpException e) {
				// TODO - error handling
				e.printStackTrace();
				throw e;
			}
		}

		// No description found - not an error
		return "";
	}

	/**
	 * Set Display Description.
	 * 
	 * @param string
	 * @return StructuredStringType null - if description updated (no new
	 *         description is added) StructuredStringType - if new description
	 *         added
	 */
	public StructuredStringType setDisplayDescr(String string) {

		// Sync. update elements with description object list
		try {
			syncUpdWithObj(string, maintainableUpdateDescriptions, descriptions);
		} catch (DDIFtpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		// Sync description object list with update elements
		try {
			syncObjWithUpd(descriptions, maintainableUpdateDescriptions);
		} catch (DDIFtpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		XmlObject descr = null;
		if (descriptions.size() > 0) {
			try {
				descr = (XmlObject) XmlBeansUtil.getLangElement(LanguageUtil
						.getDisplayLanguage(), descriptions);
			} catch (DDIFtpException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
			if (descr != null) {
				// Description in Display language - found
				if (string.length() > 0) {
					// update Description:
					XmlBeansUtil.setTextOnMixedElement(descr, string);
					return null;
				}
			}
		}

		if (string.length() == 0) {
			// Description does not exists and string is empty - ignore
			// requestion
			return null;
		}

		// Create new Description - in Original language:
		StructuredStringType descriptionType = (StructuredStringType) descr;
		descriptionType = LabelDocument.Factory.newInstance().addNewLabel();
		descriptionType.setTranslated(false);
		descriptionType.setTranslatable(true);
		descriptionType.setLang(LanguageUtil.getOriginalLanguage());
		XmlBeansUtil.setTextOnMixedElement(descriptionType, string);
		descriptions.add(descriptionType);
		maintainableUpdateDescriptions.add(new MaintainableLabelUpdateElement(
				descriptionType, MaintainableLabelUpdateElement.NEW));
		return descriptionType;
	}

	public List<LabelType> getLabelList() throws DDIFtpException {
		List<XmlObject> xmls = getNonDeletedElements(maintainableUpdateLabels);
		List<LabelType> result = new ArrayList<LabelType>();
		for (Iterator<XmlObject> iterator = xmls.iterator(); iterator.hasNext();) {
			XmlObject xmlObject = (XmlObject) iterator.next();
			result.add((LabelType) xmlObject);
		}

		// store label list for later use
		this.labels = result;

		return result;
	}

	public LabelType[] getLabels() throws DDIFtpException {
		List<XmlObject> xmls = getNonDeletedElements(maintainableUpdateLabels);
		LabelType[] result = new LabelType[xmls.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = (LabelType) xmls.get(i);
		}
		return result;
	}

	public List<StructuredStringType> getDescrList() throws DDIFtpException {
		List<XmlObject> xmls = getNonDeletedElements(maintainableUpdateDescriptions);
		List<StructuredStringType> result = new ArrayList<StructuredStringType>();
		for (Iterator<XmlObject> iterator = xmls.iterator(); iterator.hasNext();) {
			XmlObject xmlObject = (XmlObject) iterator.next();
			result.add((StructuredStringType) xmlObject);
		}

		// save description list for later use:
		this.descriptions = result;

		return result;
	}

	public StructuredStringType[] getDescrs() throws DDIFtpException {
		List<XmlObject> xmls = getNonDeletedElements(maintainableUpdateDescriptions);
		StructuredStringType[] result = new StructuredStringType[xmls.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = (StructuredStringType) xmls.get(i);
		}
		return result;
	}

	public void getDocument(
			MaintainableLabelQueryResult maintainableLabelQueryResult,
			AbstractMaintainableType abstractMaintainable)
			throws DDIFtpException {

		abstractMaintainable.setId(maintainableLabelQueryResult.getId());

		if (maintainableLabelQueryResult.getAgency() != null
				&& (!maintainableLabelQueryResult.getAgency().equals(""))) {
			abstractMaintainable.setAgency(maintainableLabelQueryResult
					.getAgency());
		}

		if (maintainableLabelQueryResult.getVersion() != null
				&& (!maintainableLabelQueryResult.getVersion().equals(""))) {
			abstractMaintainable.setVersion(maintainableLabelQueryResult
					.getVersion());
		}

		return;
	}

	/**
	 * Return list of UpdateElements
	 * 
	 * @return List<MaintainableLabelUpdateElement>
	 */
	public List<MaintainableLabelUpdateElement> getUpdateElements() {
		List<MaintainableLabelUpdateElement> elements = new ArrayList<MaintainableLabelUpdateElement>();

		elements.addAll(getDirtyUpdateElements(maintainableUpdateLabels));
		elements.addAll(getDirtyUpdateElements(maintainableUpdateDescriptions));

		return elements;
	}

	@Override
	public void validate() throws Exception {
		return;
	}
}
