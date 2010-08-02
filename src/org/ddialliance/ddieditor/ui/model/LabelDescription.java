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
	 * Get label of Simple Element.
	 * 
	 * @return String Label corresponding to the given language.
	 * @throws Exception
	 */
	public String getLabel(Language language) throws Exception {
		LabelType labelType;

		// Get Label corresponding to language
		int length = labels.size();
		for (int i = 0; i < length; i++) {
			labelType = labels.get(i);
			if (labelType.getLang().equals(language)) {
				return XmlBeansUtil.getTextOnMixedElement(labelType);
			}
		}
		log.error("*** Simple Element Label of given Language <" + language
				+ "> not found ***");
		return "";
	}

	/**
	 * Set label of Simple Element.
	 * 
	 * @param string
	 *            Label value to be assigned.
	 * @param language
	 * @return LabelType null - if label updated (no new label is added)
	 *         LabelType - if new label added
	 */
	public LabelType setLabel(String string, Language language) {
		LabelType labelType;

		int length = labels.size();
		for (int i = 0; i < length; i++) {
			labelType = labels.get(i);
			if (labelType.getLang().equals(language)) {
				if (string.length() > 0) {
					XmlBeansUtil.setTextOnMixedElement(labelType, string);
				} else {
					labels.remove(i);
				}
				return null;
			}
		}
		
		if (string.length() == 0) {
			return null;
		}
		labelType = LabelDocument.Factory.newInstance().addNewLabel();
		labelType.setTranslated(false);
		labelType.setTranslatable(true);
		labelType.setLang(LanguageUtil.getOriginalLanguage());
		XmlBeansUtil.setTextOnMixedElement(labelType, string);
		return labelType;
	}

	/**
	 * Get Display Label of Simple Element.
	 * 
	 * @return String Label string
	 * @throws Exception
	 */
	public String getDisplayLabel() {
//		LabelType labelType;

		if (labels.size() > 0) {
			String displayLang = LanguageUtil.getDisplayLanguage();
			try {
				return XmlBeansUtil.getTextOnMixedElement((XmlObject) XmlBeansUtil.getLangElement(displayLang, labels));
			} catch (DDIFtpException e) {
				// TODO Auto-generated catch block
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
	public LabelType setLabel(String string) {
		LabelType labelType;

		int length = labels.size();
		for (int i = 0; i < length; i++) {
			labelType = labels.get(i);
			if (labelType.getLang().equals(LanguageUtil.getDisplayLanguage())) {
				if (string.length() > 0) {
					XmlBeansUtil.setTextOnMixedElement(labelType, string);
				} else {
					labels.remove(i);
				}
				return null;
			}
		}
		
		if (string.length() == 0) {
			return null;
		}
		labelType = LabelDocument.Factory.newInstance().addNewLabel();
		labelType.setTranslated(false);
		labelType.setTranslatable(true);
		labelType.setLang(LanguageUtil.getDisplayLanguage());
		XmlBeansUtil.setTextOnMixedElement(labelType, string);
		return labelType;
	}

	/**
	 * Get Description of Simple Element.
	 * 
	 * @param language
	 * @return String Description string
	 */
	// public String getDescr(Language language) {
	//
	// StructuredStringType descriptionType;
	//
	// // Get Description corresponding to language
	// int length = descrs.size();
	// for (int i = 0; i < length; i++) {
	// descriptionType = descrs.get(i);
	// if (descriptionType.getLang().equals(language)) {
	// return descrs.get(i).toString();
	// }
	// }
	// log.error("*** Simple Element Description of given Language <"
	// + language + "> not found ***");
	// return "";
	// }

	/**
	 * Set Description of Simple Element.
	 * 
	 * @param string
	 *            Description string.
	 * @param language
	 * @return StructuredStringType null - if description updated (no new
	 *         description is added) StructuredStringType - if new description
	 *         added
	 */
	public StructuredStringType setDescr(String string, Language language) {
		StructuredStringType descriptionType;

		int length = descrs.size();
		for (int i = 0; i < length; i++) {
			descriptionType = descrs.get(i);
			if (descriptionType.getLang().equals(language)) {
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
			XmlBeansUtil.addTranslationAttributes(descriptionType, LanguageUtil.getOriginalLanguage(), false, true);
		} catch (DDIFtpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		XmlBeansUtil.setTextOnMixedElement(descriptionType, string);
		return descriptionType;
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
	 * Set Description of Simple Element for Display Language.
	 * 
	 * @param string
	 * @return StructuredStringType null - if description updated (no new
	 *         description is added) StructuredStringType - if new description
	 *         added
	 */
	public StructuredStringType setDescr(String string) {
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
			XmlBeansUtil.addTranslationAttributes(descriptionType, LanguageUtil.getOriginalLanguage(), false, true);
		} catch (DDIFtpException e) {
			// TODO Auto-generated catch block
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
		
		// Check if more labels without lang attribute
		found = false;
		List<LabelType> labelList = getLabels();
		for (LabelType labelType : labelList) {
			if (labelType.getLang() == null && found) {
				throw new Exception("More Labels do not have a 'lang' attribute");
			} else {
				found = true;
			}
		}
		
		// Check if more Descriptions without lang attribute
		found = false;
		List<StructuredStringType> descrs = getDescrs();
		for (StructuredStringType descr : descrs) {
			if (descr.getLang() == null && found) {
				throw new Exception("More Descriptions do not have a 'lang' attribute");
			} else {
				found = true;
			}
		}
		
		// No error found:
		return;
	}


}
