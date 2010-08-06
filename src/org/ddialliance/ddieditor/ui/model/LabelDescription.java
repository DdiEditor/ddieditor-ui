package org.ddialliance.ddieditor.ui.model;

/**
 * Label and description model 
 * - provides 'get' and 'set' methods for accessing Labels and Descriptions
 *  sub-elements.
 * 
 */
import java.util.List;

import org.apache.xmlbeans.XmlObject;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.DescriptionDocument;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.LabelDocument;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.LabelType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.StructuredStringType;
import org.ddialliance.ddieditor.ui.Activator;
import org.ddialliance.ddieditor.ui.preference.PreferenceConstants;
import org.ddialliance.ddieditor.ui.util.LanguageUtil;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.ddialliance.ddiftp.util.xml.XmlBeansUtil;
import org.eclipse.jface.preference.IPreferenceStore;

public abstract class LabelDescription extends Model implements IModel {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			LabelDescription.class);

	private List<LabelType> labels;
	private List<StructuredStringType> descrs;

	/**
	 * Constructor
	 * 
	 * @param id
	 *            Identification
	 * @param version
	 *            Version
	 * @param parentId
	 *            Parent identification
	 * @param parentVersion
	 *            Parent version
	 * @param labels
	 *            List of Label Types
	 * @param descrs
	 *            List of Structured String Types
	 */

	public LabelDescription(String id, String version, String parentId,
			String parentVersion, List<LabelType> labels,
			List<StructuredStringType> descrs) {
		super(id, version, parentId, parentVersion, "TODO");

		this.labels = labels;
		this.descrs = descrs;
	}

	/**
	 * Get Display Label of Simple Element.
	 * 
	 * @return String Label string
	 */
	public String getDisplayLabel() {

		if (labels.size() > 0) {
			String displayLang = LanguageUtil.getDisplayLanguage();
			try {
				return XmlBeansUtil.getTextOnMixedElement((XmlObject) XmlBeansUtil.getLangElement(displayLang, labels));
			} catch (DDIFtpException e) {
				e.printStackTrace();
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
	 */
	public LabelType setDisplayLabel(String string) {
		XmlObject label = null;
		
		if (labels.size() > 0) {
			try {
				label = (XmlObject) XmlBeansUtil.getLangElement(LanguageUtil.getDisplayLanguage(), labels);
			} catch (DDIFtpException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (string.length() > 0) {
				// update label:
				XmlBeansUtil.setTextOnMixedElement(label, string);
				return null;
			} else {
				// remove label:
				for (int i = 0; i < labels.size(); i++) {
					if (label == labels.get(i)) {
						labels.remove(i);
						return null;
					}
				}
			}
		}
		
		if (string.length() == 0) {
			// label does not exists but string is empty - ignore requestion
			return null;
		}

		// add new label:
		LabelType labelType = (LabelType )label;
		labelType = LabelDocument.Factory.newInstance().addNewLabel();
		labelType.setTranslated(false);
		labelType.setTranslatable(true);
		labelType.setLang(LanguageUtil.getDisplayLanguage());
		XmlBeansUtil.setTextOnMixedElement(labelType, string);
		return labelType;
	}

	/**
	 * Get Original Description of Simple Element. Original means not
	 * translated.
	 * 
	 * @return String
	 */
	public String getDescr() {
		StructuredStringType descriptionType;

		// Get Description corresponding to language
		int length = descrs.size();
		for (int i = 0; i < length; i++) {
			descriptionType = descrs.get(i);
			if (!descriptionType.getTranslated()) {
				return XmlBeansUtil.getTextOnMixedElement(descriptionType);
			}
		}
		// Not mandatory Description - not found
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
		StructuredStringType descriptionType;

		int length = descrs.size();
		for (int i = 0; i < length; i++) {
			descriptionType = descrs.get(i);
			if (descriptionType.getLang().equals(LanguageUtil.getDisplayLanguage())) {
				if (string.length() > 0) {
					XmlBeansUtil.setTextOnMixedElement(descriptionType, string);
				} else {
					descrs.remove(i);
				}
				return null;
			}
		}

		if (string.length() == 0) {
			return null;
		}
		descriptionType = DescriptionDocument.Factory.newInstance()
				.addNewDescription();
		try {
			XmlBeansUtil.addTranslationAttributes(descriptionType, LanguageUtil.getDisplayLanguage(), false, true);
		} catch (DDIFtpException e) {
			e.printStackTrace();
		}
		XmlBeansUtil.setTextOnMixedElement(descriptionType, string);
		return descriptionType;
	}

	public List<LabelType> getLabels() {
		return labels;
	}

	public List<StructuredStringType> getDescrs() {
		return descrs;
	}
	
	/**
	 * Validates the element before it is saved.
	 * 
	 * @throws Exception
	 */
	public void validate() throws Exception {
		log.debug("LabelDescription validation performed");
		boolean found;
		
		// TODO Remove check of attributes to Translation popup module.
//		// Check if more labels without lang attribute
//		found = false;
//		List<LabelType> labelList = getLabels();
//		for (LabelType labelType : labelList) {
//			if (labelType.getLang() == null && found) {
//				throw new Exception("More Labels do not have a 'lang' attribute");
//			} else {
//				found = true;
//			}
//		}
//		
//		// Check if more Descriptions without lang attribute
//		found = false;
//		List<StructuredStringType> descrs = getDescrs();
//		for (StructuredStringType descr : descrs) {
//			if (descr.getLang() == null && found) {
//				throw new Exception("More Descriptions do not have a 'lang' attribute");
//			} else {
//				found = true;
//			}
//		}
		
		// No error found:
		return;
	}


}
