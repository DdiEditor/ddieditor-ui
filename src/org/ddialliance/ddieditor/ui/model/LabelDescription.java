package org.ddialliance.ddieditor.ui.model;

import java.util.List;

import org.apache.xmlbeans.XmlObject;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.LabelDocument;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.LabelType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.StructuredStringType;
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
	 * @throws DDIFtpException
	 */
	public String getDisplayLabel() throws DDIFtpException {

		if (labels.size() > 0) {
			String displayLang = LanguageUtil.getDisplayLanguage();
			try {
				return XmlBeansUtil
						.getTextOnMixedElement((XmlObject) XmlBeansUtil
								.getLangElement(displayLang, labels));
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
	public LabelType setDisplayLabel(String string) {
		XmlObject label = null;

		// Show label in Display language
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
				// Label in Display language found
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
		XmlObject descr = null;

		// Show Description in Display language
		if (descrs.size() > 0) {
			try {
				descr = (XmlObject) XmlBeansUtil.getLangElement(LanguageUtil
						.getDisplayLanguage(), descrs);
			} catch (DDIFtpException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
			if (descr != null) {
				// Description in Display language found
				if (string.length() > 0) {
					// update description:
					XmlBeansUtil.setTextOnMixedElement(descr, string);
					return null;
				} else {
					// remove Description:
					for (int i = 0; i < descrs.size(); i++) {
						if (descr == descrs.get(i)) {
							descrs.remove(i);
							return null;
						}
					}
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
		return descriptionType;
	}

	public List<LabelType> getLabels() {
		return labels;
	}

	public List<StructuredStringType> getDescrs() {
		return descrs;
	}

	@Override
	public void validate() throws Exception {
		return;
	}
}
