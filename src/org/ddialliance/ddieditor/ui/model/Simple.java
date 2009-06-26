package org.ddialliance.ddieditor.ui.model;

import java.util.List;

import org.ddialliance.ddi_3_0.xml.xmlbeans.reusable.DescriptionDocument;
import org.ddialliance.ddi_3_0.xml.xmlbeans.reusable.LabelDocument;
import org.ddialliance.ddi_3_0.xml.xmlbeans.reusable.LabelType;
import org.ddialliance.ddi_3_0.xml.xmlbeans.reusable.StructuredStringType;
import org.ddialliance.ddieditor.ui.Activator;
import org.ddialliance.ddieditor.ui.dbxml.Util;
import org.ddialliance.ddieditor.ui.preference.PreferenceConstants;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.ddialliance.ddiftp.util.xml.XmlBeansUtil;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 *  Simple Element provides 'get' and 'set' methods for accessing Labels and Descriptions
 *  sub-elements.
 *  
 */

public class Simple extends Model {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, Simple.class);
	
	private List<LabelType> labels;
	private List<StructuredStringType> descrs;
	
	/**
	 *  Constructor for Simple Element.
	 *  
	 * @param id
	 * 			Identification
	 * @param version
	 * 			Version
	 * @param parentId
	 * 			Parent identification
	 * @param parentVersion
	 * 			Parent version
	 * @param labels
	 * 			List of Label Types
	 * @param descrs
	 * 			List of Structured String Types
	 */

	public Simple(String id, String version, String parentId, String parentVersion, List<LabelType> labels,
			List<StructuredStringType> descrs) {
		super(id, version, parentId, parentVersion);

		this.labels = labels;
		this.descrs = descrs;
	}

	/**
	 * Get label of Simple Element.
	 * 
	 * @return String
	 * 			Label corresponding to the given language.
	 * @throws Exception
	 */
	public String getLabel(Language language) throws Exception {
		LabelType labelType;

		// Get Label corresponding to language
		int length = labels.size();
		for (int i = 0; i < length; i++) {
			labelType = labels.get(i);
			if (labelType.getLang().equals(language)) {
				return labelType.getStringValue();
			}
		}
		log.error("*** Simple Element Label of given Language <"+language+"> not found ***");
		return "";
	}

	/**
	 * Set label of Simple Element.
	 * 
	 * @param string
	 * 			Label value to be assigned.
	 * @param language
	 * @return LabelType
	 * 			null - if label updated (no new label is added)
	 * 			LabelType - if new label added
	 */
	public LabelType setLabel(String string, Language language) {
		LabelType labelType;

		// Remove label corresponding to language
		int length = labels.size();
		for (int i = 0; i < length; i++) {
			labelType = labels.get(i);
			if (labelType.getLang().equals(language)) {
				labelType.setStringValue(string);
				return null;
			}
		}
		labelType = org.ddialliance.ddi_3_0.xml.xmlbeans.reusable.LabelDocument.Factory.newInstance().addNewLabel();
		labelType.setTranslated(false);
		labelType.setTranslatable(true);
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		labelType.setLang(store.getString(PreferenceConstants.DDI_LANGUAGE));
		labelType.setStringValue(string);
		return labelType;
	}
	
	/**
	 * Get Original Label of Simple Element.
	 * 'Original' means not translated.
	 * 
	 * @return String
	 * 			Label string
	 * @throws Exception
	 */
	public String getLabel() {
		LabelType labelType;

		// Get Label corresponding to language
		int length = labels.size();
		for (int i = 0; i < length; i++) {
			labelType = labels.get(i);
			if (!labelType.getTranslated()) {
				return labelType.getStringValue();
			}
		}
		log.error("*** Simple Element Label of 'Original' Language not found ***");
		return "";
	}

	/**
	 * Set Original Label of Simple Element.
	 * 'Original' means not translated.
	 * 
	 * @param string
	 * @return LabelType
	 * 			null - if label updated (no label is added)
	 * 			LabelType is new label added
	 */
	public LabelType setLabel(String string) {
		LabelType labelType;

		// Remove label corresponding to language
		int length = labels.size();
		for (int i = 0; i < length; i++) {
			labelType = labels.get(i);
			if (!labelType.getTranslated()) {
				labelType.setStringValue(string);
				return null;
			}
		}

		labelType = LabelDocument.Factory.newInstance().addNewLabel();
		labelType.setTranslated(false);
		labelType.setTranslatable(true);
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		labelType.setLang(store.getString(PreferenceConstants.DDI_LANGUAGE));
		labelType.setStringValue(string);
		return labelType;
	}
	
	/**
	 * Get Description of Simple Element.
	 * 
	 * @param language
	 * @return String
	 * 			Description string
	 */
	public String getDescr(Language language) {

		StructuredStringType descriptionType;

		// Get Description corresponding to language
		int length = descrs.size();
		for (int i = 0; i < length; i++) {
			descriptionType = descrs.get(i);
			if (descriptionType.getLang().equals(language)) {
				return descrs.get(i).toString();
			}
		}
		log.error("*** Simple Element Description of given Language <"+language+"> not found ***");
		return "";
	}

	/**
	 * Set Description of Simple Element.
	 * 
	 * @param string
	 * 			Description string.
	 * @param language
	 * @return StructuredStringType
	 * 			null - if description updated (no new description is added)
	 * 			StructuredStringType - if new description added
	 */
	public StructuredStringType setDescr(String string, Language language) {
		StructuredStringType descriptionType;
		
		// Remove description corresponding to language
		int length = descrs.size();
		for (int i = 0; i < length; i++) {
			descriptionType = descrs.get(i);
			if (descriptionType.getLang().equals(language)) {
				// TODO Does not handle namespace: XmlBeansUtil.setTextOnMixedElement(descriptionType, string);
				Util.setTextOnMixedElement(descriptionType, string);
				return null;
			}
		}


		descriptionType = DescriptionDocument.Factory.newInstance().addNewDescription();
		descriptionType.setTranslated(false);
		descriptionType.setTranslatable(true);
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		descriptionType.setLang(store.getString(PreferenceConstants.DDI_LANGUAGE));
		// TODO Does not handle namespace: XmlBeansUtil.setTextOnMixedElement(descriptionType, string);
		Util.setTextOnMixedElement(descriptionType, string);
		return descriptionType;
	}
	
	/**
	 * Get Original Description of Simple Element.
	 * Original means not translated.
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
				//	TODO Does not handle namespace: return XmlBeansUtil.getTextOnMixedElement(descriptionType);
				return Util.getTextOnMixedElement(descriptionType);
			}
		}
		log.error("*** Simple Element Description not found ***");
		return "";
	}

	/**
	 * Set Original Description of Simple Element.
	 * Original means not translated.
	 * 
	 * @param string
	 * @return StructuredStringType
	 * 			null - if description updated (no new description is added)
	 * 			StructuredStringType - if new description added
	 */
	public StructuredStringType setDescr(String string) {
		StructuredStringType descriptionType;
		
		int length = descrs.size();
		for (int i = 0; i < length; i++) {
			descriptionType = descrs.get(i);
			if (!descriptionType.getTranslated()) {
				// TODO Does not handle namespace: XmlBeansUtil.setTextOnMixedElement(descriptionType, string);
				Util.setTextOnMixedElement(descriptionType, string);
				return null;
			}
		}
		
		descriptionType = DescriptionDocument.Factory.newInstance().addNewDescription();
		// Original = translated is false 
		descriptionType.setTranslated(false);
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		descriptionType.setLang(store.getString(PreferenceConstants.DDI_LANGUAGE));
		// TODO Does not handle namespace: XmlBeansUtil.setTextOnMixedElement(descriptionType, string);
		Util.setTextOnMixedElement(descriptionType, string);
		return descriptionType;
	}
}
