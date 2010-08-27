package org.ddialliance.ddieditor.ui.model;

/**
 * Label and description model 
 * - provides 'get' and 'set' methods for accessing Labels and Descriptions
 *  sub-elements.
 * 
 */
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlTokenSource;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.AbstractMaintainableType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.LabelDocument;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.LabelType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.StructuredStringType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.impl.DescriptionDocumentImpl;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.impl.LabelDocumentImpl;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.impl.LabelTypeImpl;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.impl.StructuredStringTypeImpl;
import org.ddialliance.ddieditor.persistenceaccess.maintainablelabel.MaintainableLabelQueryResult;
import org.ddialliance.ddieditor.persistenceaccess.maintainablelabel.MaintainableLabelUpdateElement;
import org.ddialliance.ddieditor.ui.util.LanguageUtil;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.ddialliance.ddiftp.util.xml.XmlBeansUtil;

public abstract class LabelDescription extends Model implements IModel {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			LabelDescription.class);

	private MaintainableLabelQueryResult maintainableLabelQueryResult;
	private List<MaintainableLabelUpdateElement> labelUpd = new ArrayList<MaintainableLabelUpdateElement>();
	private List<MaintainableLabelUpdateElement> descrUpd = new ArrayList<MaintainableLabelUpdateElement>();

	private List<LabelType> labelObj;
	private List<StructuredStringType> descrObj;

	/**
	 * Constructor
	 * 
	 * @param id
	 * @param version
	 * @param parentId
	 * @param parentVersion
	 * @param maintainableLabelQueryResult
	 * @throws XmlException
	 * @throws DDIFtpException
	 */
	public LabelDescription(String id, String version, String parentId,
			String parentVersion, String agency,
			MaintainableLabelQueryResult maintainableLabelQueryResult)
			throws DDIFtpException {
		
		super(id, version, parentId, parentVersion, "TODO");
		this.maintainableLabelQueryResult = maintainableLabelQueryResult;
		if (maintainableLabelQueryResult != null) {
			XmlObject[] xml = maintainableLabelQueryResult.getSubElement("Label");
			for (int i = 0; i < xml.length; i++) {
				labelUpd.add(new MaintainableLabelUpdateElement((LabelType) ((LabelDocumentImpl) xml[i]).getLabel(), null /* NOP */));
			}
			
			xml = maintainableLabelQueryResult.getSubElement("Description");
			for (int i = 0; i < xml.length; i++) {
				descrUpd.add(new MaintainableLabelUpdateElement((StructuredStringType) ((DescriptionDocumentImpl) xml[i]).getDescription(), null /* NOP */));
			}
		}
	}
	
	/**
	 * Get all not deleted XML objects from list of maintainable update elements
	 * 
	 * @param elements
	 * @return
	 */
	private List<XmlObject> getXmlObjects(List<MaintainableLabelUpdateElement> elements) {

		List<XmlObject> objects = new ArrayList<XmlObject>();
		for (Iterator<MaintainableLabelUpdateElement> iterator = elements.iterator(); iterator.hasNext();) {
			MaintainableLabelUpdateElement maintainableLabelUpdateElement = (MaintainableLabelUpdateElement) iterator
					.next();
			if (maintainableLabelUpdateElement.getCrudValue() == null || maintainableLabelUpdateElement.getCrudValue() > -1) {
				objects.add(maintainableLabelUpdateElement.getXmlObject());
			}
		}
		return objects;
	}
	
	private int findXmlObject(List<MaintainableLabelUpdateElement> elements, XmlObject xmlObject) {
		
		for (int i = 0; i < elements.size(); i++) {
			if (elements.get(i).getXmlObject() == xmlObject) {
				return i;
			}
		}
		return -1;
	}
		
	private List<MaintainableLabelUpdateElement> getDirtyUpdateElements(List<MaintainableLabelUpdateElement> elements) {
		List<MaintainableLabelUpdateElement> dirtyElements = new ArrayList<MaintainableLabelUpdateElement>();
		for (Iterator<MaintainableLabelUpdateElement> iterator = elements.iterator(); iterator.hasNext();) {
			MaintainableLabelUpdateElement maintainableLabelUpdateElement = (MaintainableLabelUpdateElement) iterator
					.next();
			if (maintainableLabelUpdateElement.getCrudValue() != null) {
				dirtyElements.add(maintainableLabelUpdateElement);
			}
		}
		return dirtyElements;
	}
	
	/**
	 * Sync. MaintainableLabelUpdateElement with item elements (update by Tranlation pop-up)
	 * 
	 * Note: Do not find new items
	 * @throws DDIFtpException 
	 */
	private void syncUpdWithObj(String string, List<MaintainableLabelUpdateElement> upd, List<?> obj) throws DDIFtpException {

		// loop over all MaintainableLabelUpdateElements
		for (int i = 0; i < upd.size(); i++) {
			boolean found = false;
			// find corresponding objects
			for (int j = 0; j < obj.size(); j++) {
				if (obj.get(j) == upd.get(i).getXmlObject()) {
					found = true;
					break;
				}
			}
			if (found && string.length() > 0) {
				// mark update element as updated
				upd.get(i).setCrudValue(i + 1);
				if (obj.get(i) instanceof LabelType) {
					upd.get(i).setValue(((LabelType) obj.get(i)).xmlText());					
				} else if (obj.get(i) instanceof StructuredStringType) {
					upd.get(i).setValue(((StructuredStringType) obj.get(i)).xmlText());					
				} else {
					throw new DDIFtpException("Unsupported object type.");
				}
			} else {
				// mark update element as deleted
				upd.get(i).setCrudValue((i+1)*-1);
			}
		}
		return;
	}
	
	private void syncObjWithUpd(List<?> obj, List<MaintainableLabelUpdateElement> upds) throws DDIFtpException {

		// loop over list of items
		for (int i = 0; i < obj.size(); i++) {
			// find corresponding update element
			boolean found = false;
			for (int j = 0; j < upds.size(); j++) {
				if (obj.get(i) == upds.get(j).getXmlObject()) {
					found = true;
					continue;
				}
			}
			if (!found) {
				if (obj.get(i) instanceof LabelType) {
				upds
				.add(new MaintainableLabelUpdateElement((LabelType) obj.get(i), MaintainableLabelUpdateElement.NEW));
				} else if (obj.get(i) instanceof StructuredStringType) {
					upds
					.add(new MaintainableLabelUpdateElement((StructuredStringType) obj.get(i), MaintainableLabelUpdateElement.NEW));

				} else {
					throw new DDIFtpException("Unsupported object type.");
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

		if (labelUpd.size() > 0) {
			try {
				return XmlBeansUtil.getTextOnMixedElement((XmlObject) XmlBeansUtil.getLangElement(LanguageUtil
						.getDisplayLanguage(), getXmlObjects(labelUpd)));
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
		syncUpdWithObj(string, labelUpd, labelObj);
		
		// Sync label type list with update elements
		syncObjWithUpd(labelObj, labelUpd);
		
		// Sync. label type list with update elements
		if (labelObj.size() > 0) {
			try {
				label = (XmlObject) XmlBeansUtil.getLangElement(LanguageUtil.getDisplayLanguage(), labelObj);
			} catch (DDIFtpException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
			if (label != null) {
				// Label in Display language - found
				if (string.length() > 0) {
					// replace Label:
					XmlBeansUtil.setTextOnMixedElement(((LabelTypeImpl) label), string);
					return null;
				}
			}
		}
		
		if (string.length() == 0) {
			// label does not exists and string is empty - ignore requestion
			return null;
		}

		// Create new label - in Original language:
		LabelType labelType = (LabelType )label;
		labelType = LabelDocument.Factory.newInstance().addNewLabel();
		labelType.setTranslated(false);
		labelType.setTranslatable(true);
		labelType.setLang(LanguageUtil.getOriginalLanguage());
		XmlBeansUtil.setTextOnMixedElement(labelType, string);
		labelObj.add(labelType);
		labelUpd.add(new MaintainableLabelUpdateElement(labelType, MaintainableLabelUpdateElement.NEW));

		return labelType;
	}

	/**
	 * Get Display Description of Simple Element.
	 * 
	 * @return String
	 */
	public String getDisplayDescr()  throws DDIFtpException {
		
		if (descrUpd.size() > 0) {
			try {
				return XmlBeansUtil.getTextOnMixedElement((XmlObject) XmlBeansUtil.getLangElement(LanguageUtil
						.getDisplayLanguage(), getXmlObjects(descrUpd)));
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
			syncUpdWithObj(string, descrUpd, descrObj);
		} catch (DDIFtpException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
		
		// Sync description object list with update elements
		try {
			syncObjWithUpd(descrObj, descrUpd);
		} catch (DDIFtpException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}

		XmlObject descr = null;

		if (descrObj.size() > 0) {
			try {
				descr = (XmlObject) XmlBeansUtil.getLangElement(LanguageUtil.getDisplayLanguage(), descrObj);
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
			// Description does not exists and string is empty - ignore requestion
			return null;
		}

		// Create new Description - in Original language:
		StructuredStringType descriptionType = (StructuredStringType )descr;
		descriptionType = LabelDocument.Factory.newInstance().addNewLabel();
		descriptionType.setTranslated(false);
		descriptionType.setTranslatable(true);
		descriptionType.setLang(LanguageUtil.getOriginalLanguage());
		XmlBeansUtil.setTextOnMixedElement(descriptionType, string);
		descrObj.add(descriptionType);
		descrUpd.add(new MaintainableLabelUpdateElement(descriptionType, MaintainableLabelUpdateElement.NEW));
		return descriptionType;
	}

	public List<LabelType> getLabelList() {
		
		List<XmlObject> xmls = getXmlObjects(labelUpd);
		List<LabelType> result = new ArrayList<LabelType>();
		for (Iterator<XmlObject> iterator = xmls.iterator(); iterator.hasNext();) {
			XmlObject xmlObject = (XmlObject) iterator.next();
			result.add((LabelType) xmlObject);
		}
		
		// store label list for later use
		this.labelObj = result;
		
		return result;
	}
	
	public LabelType[] getLabels() {
		
		List<XmlObject> xmls = getXmlObjects(labelUpd);
		LabelType[] result = new LabelType[xmls.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = (LabelType) xmls.get(i);
		}
		return result;
	}

	public List<StructuredStringType> getDescrList() {
		
		List<XmlObject> xmls = getXmlObjects(descrUpd);
		List<StructuredStringType> result = new ArrayList<StructuredStringType>();
		for (Iterator<XmlObject> iterator = xmls.iterator(); iterator.hasNext();) {
			XmlObject xmlObject = (XmlObject) iterator.next();
			result.add((StructuredStringType) xmlObject);
		}
		
		// save description list for later use:
		this.descrObj = result;
		
		return result;
	}

	public StructuredStringType[] getDescrs() {
		
		List<XmlObject> xmls = getXmlObjects(descrUpd);
		StructuredStringType[] result = new StructuredStringType[xmls.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = (StructuredStringType) xmls.get(i);
		}
		return result;
	}
	
	
	public void getDocument(MaintainableLabelQueryResult maintainableLabelQueryResult,
			AbstractMaintainableType abstractMaintainable) throws DDIFtpException {

		abstractMaintainable.setId(maintainableLabelQueryResult.getId());

		if (maintainableLabelQueryResult.getAgency() != null && (!maintainableLabelQueryResult.getAgency().equals(""))) {
			abstractMaintainable.setAgency(maintainableLabelQueryResult.getAgency());
		}

		if (maintainableLabelQueryResult.getVersion() != null
				&& (!maintainableLabelQueryResult.getVersion().equals(""))) {
			abstractMaintainable.setVersion(maintainableLabelQueryResult.getVersion());
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
		
		elements.addAll(getDirtyUpdateElements(labelUpd));
		elements.addAll(getDirtyUpdateElements(descrUpd));
		
		return elements;
	}

	/**
	 * Validates the element before it is saved.
	 * 
	 * @throws Exception
	 */
	public void validate() throws Exception {
		log.debug("LabelDescription validation performed");
				
		// No error found:
		return;
	}

}
