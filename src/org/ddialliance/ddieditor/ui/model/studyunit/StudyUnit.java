package org.ddialliance.ddieditor.ui.model.studyunit;

import java.util.ArrayList;
import java.util.List;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.CitationType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.DateType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.FundingInformationDocument;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.IDType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.InternationalStringType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.ReferenceType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.StructuredStringType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.UniverseReferenceDocument;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.impl.CitationDocumentImpl;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.impl.FundingInformationDocumentImpl;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.impl.NameDocumentImpl;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.impl.UniverseReferenceDocumentImpl;
import org.ddialliance.ddi3.xml.xmlbeans.studyunit.StudyUnitDocument;
import org.ddialliance.ddi3.xml.xmlbeans.studyunit.StudyUnitType;
import org.ddialliance.ddi3.xml.xmlbeans.studyunit.impl.AbstractDocumentImpl;
import org.ddialliance.ddi3.xml.xmlbeans.studyunit.impl.PurposeDocumentImpl;
import org.ddialliance.ddieditor.persistenceaccess.maintainablelabel.MaintainableLabelQueryResult;
import org.ddialliance.ddieditor.persistenceaccess.maintainablelabel.MaintainableLabelUpdateElement;
import org.ddialliance.ddieditor.ui.model.Model;
import org.ddialliance.ddieditor.ui.view.Messages;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.ddialliance.ddiftp.util.xml.XmlBeansUtil;

/**
 * Study Unit Sub-Elements e.g. list of objects of a given type (e.g.
 * Citations), change flag and CRUD value.
 */
public class StudyUnit extends Model {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, StudyUnit.class);

	public static enum SUB_ELEMENT_TYPE {
		NAME, CITATION_TITLE, CITATION_SUBTITLE
	};

	private MaintainableLabelQueryResult maintainableLabelQueryResult = null;
	private SubElement citationSubElements;
	private SubElement abstractSubElements;
	private SubElement universeRefSubElements;
	private SubElement fundingSubElements;
	private SubElement purposeSubElements;

	/**
	 * Study Unit constructor
	 * 
	 * @param id
	 * @param version
	 * @param parentId
	 * @param parentVersion
	 * @param studyUnitQueryResult
	 * @throws XmlException
	 * @throws DDIFtpException
	 */
	public StudyUnit(String id, String version, String parentId,
			String parentVersion, String agency,
			MaintainableLabelQueryResult studyUnitQueryResult)
			throws DDIFtpException {
		super(id, version, parentId, parentVersion, agency);
		this.maintainableLabelQueryResult = studyUnitQueryResult;
		if (studyUnitQueryResult != null) {
			citationSubElements = new SubElement(studyUnitQueryResult
					.getSubElement("Citation"));
			abstractSubElements = new SubElement(studyUnitQueryResult
					.getSubElement("Abstract"));
			universeRefSubElements = new SubElement(studyUnitQueryResult
					.getSubElement("UniverseReference"));
			fundingSubElements = new SubElement(studyUnitQueryResult
					.getSubElement("FundingInformation"));
			purposeSubElements = new SubElement(studyUnitQueryResult
					.getSubElement("Purpose"));
		}
	}

	/**
	 * Clear changed status for sub elements
	 */
	public void clearChanged() {
		// TODO add is debug enabled
		log.debug("StudyUnit.clearChanged()");
		citationSubElements.changed(false);
		abstractSubElements.changed(false);
		universeRefSubElements.changed(false);
		fundingSubElements.changed(false);
		purposeSubElements.changed(false);
	}

	@Override
	public void validate() throws Exception {
		return; // No error found
	}

	@Override
	public XmlObject getDocument() throws DDIFtpException {
		StudyUnitDocument doc = StudyUnitDocument.Factory.newInstance();
		StudyUnitType type = doc.addNewStudyUnit();
		type.setId(maintainableLabelQueryResult.getId());
		if (maintainableLabelQueryResult.getAgency() != null
				&& (!maintainableLabelQueryResult.getAgency().equals(""))) {
			type.setAgency(maintainableLabelQueryResult.getAgency());
		}
		if (maintainableLabelQueryResult.getVersion() != null
				&& (!maintainableLabelQueryResult.getVersion().equals(""))) {
			type.setVersion(maintainableLabelQueryResult.getVersion());
		}
		return doc;
	}

	@Override
	public void executeChange(Object value, Class<?> type) throws Exception {
		// TODO model update
		log.debug("TODO model update");
	}

	/**
	 * Get Study Unit Query Result.
	 * 
	 * @return MaintainableLabelQueryResult
	 */
	public MaintainableLabelQueryResult getStudyUnitQueryResult() {
		return maintainableLabelQueryResult;
	}

	/**
	 * Set Study Unit Query Result.
	 * 
	 * @param studyUnitQueryResult
	 */
	public void setStudyUnitQueryResult(
			MaintainableLabelQueryResult studyUnitQueryResult) {
		this.maintainableLabelQueryResult = studyUnitQueryResult;
	}

	/**
	 * Return list of UpdateElements
	 * 
	 * @return List<MaintainableLabelUpdateElement>
	 */
	public List<MaintainableLabelUpdateElement> getUpdateElements() {
		List<MaintainableLabelUpdateElement> elements = new ArrayList<MaintainableLabelUpdateElement>();
	
		MaintainableLabelUpdateElement element;
		element = getCitationUpdateElement();
		if (element != null) {
			elements.add(element);
		}
		element = getAbstractUpdateElement();
		if (element != null) {
			elements.add(element);
		}
		element = getUniverseReferenceUpdateElement();
		if (element != null) {
			elements.add(element);
		}
		element = getFundingInformationUpdateElement();
		if (element != null) {
			elements.add(element);
		}
		element = getPurposeUpdateElementUpdateElement();
		if (element != null) {
			elements.add(element);
		}
		return elements;
	}

	private MaintainableLabelUpdateElement getCitationUpdateElement() {
		if (citationSubElements.getChangedStatus()) {
			MaintainableLabelUpdateElement updCitation = new MaintainableLabelUpdateElement();
	
			updCitation.setLocalName("Citation");
			// TODO Compute Crud value
			updCitation.setCrudValue(citationSubElements.getCrudValue());
			updCitation.setValue(citationSubElements.getModifiedXmlObject().xmlText());
			return updCitation;
		}
		return null;
	}

	private MaintainableLabelUpdateElement getAbstractUpdateElement() {
		if (abstractSubElements.changed) {
			MaintainableLabelUpdateElement updAbstract = new MaintainableLabelUpdateElement();
	
			updAbstract.setLocalName("Abstract");
			updAbstract.setCrudValue(abstractSubElements.getCrudValue()); // Update
			updAbstract.setValue(abstractSubElements.getModifiedXmlObject().xmlText());
			return updAbstract;
		}
		return null;
	}

	private MaintainableLabelUpdateElement getUniverseReferenceUpdateElement() {
		if (universeRefSubElements.changed) {
			MaintainableLabelUpdateElement updUniverseRef = new MaintainableLabelUpdateElement();
	
			updUniverseRef.setLocalName("UniverseReference");
			updUniverseRef.setCrudValue(universeRefSubElements.getCrudValue()); // Update
			updUniverseRef.setValue(universeRefSubElements.getModifiedXmlObject()
					.xmlText());
			return updUniverseRef;
		}
		return null;
	}

	private MaintainableLabelUpdateElement getFundingInformationUpdateElement() {
		if (fundingSubElements.changed) {
			MaintainableLabelUpdateElement updFunding = new MaintainableLabelUpdateElement();
	
			updFunding.setLocalName("FundingInformation");
			updFunding.setCrudValue(fundingSubElements.getCrudValue()); // Update
			updFunding.setValue(fundingSubElements.getModifiedXmlObject().xmlText());
			return updFunding;
		}
		return null;
	}

	private MaintainableLabelUpdateElement getPurposeUpdateElementUpdateElement() {
		if (purposeSubElements.changed) {
			MaintainableLabelUpdateElement updPurpose = new MaintainableLabelUpdateElement();
	
			updPurpose.setLocalName("Purpose");
			updPurpose.setCrudValue(purposeSubElements.getCrudValue()); // Update
			updPurpose.setValue(purposeSubElements.getModifiedXmlObject().xmlText());
			return updPurpose;
		}
		return null;
	}

	//
	// accessors
	//
	private AbstractDocumentImpl[] getAbstracts(XmlObject[] xmlObjects) {
		AbstractDocumentImpl[] abstracts = new AbstractDocumentImpl[xmlObjects.length];

		for (int i = 0; i < xmlObjects.length; i++) {
			if (xmlObjects[i] instanceof AbstractDocumentImpl) {
				abstracts[i] = (AbstractDocumentImpl) xmlObjects[i];
			}
		}
		return abstracts;
	}

	private UniverseReferenceDocumentImpl[] getUniverseReferences(
			XmlObject[] xmlObjects) {
		UniverseReferenceDocumentImpl[] universeRerencences = new UniverseReferenceDocumentImpl[xmlObjects.length];

		for (int i = 0; i < xmlObjects.length; i++) {
			if (xmlObjects[i] instanceof UniverseReferenceDocumentImpl) {
				universeRerencences[i] = (UniverseReferenceDocumentImpl) xmlObjects[i];
			}
		}
		return universeRerencences;
	}

	private PurposeDocumentImpl[] getPurposes(XmlObject[] xmlObjects) {
		PurposeDocumentImpl[] purposes = new PurposeDocumentImpl[xmlObjects.length];

		for (int i = 0; i < xmlObjects.length; i++) {
			if (xmlObjects[i] instanceof PurposeDocumentImpl) {
				purposes[i] = (PurposeDocumentImpl) xmlObjects[i];
			}
		}
		return purposes;
	}

	private boolean setInternationalString(String string,
			InternationalStringType[] internationalStringTypes,
			String languageCode) {

		for (int i = 0; i < internationalStringTypes.length; i++) {
			InternationalStringType internationalStringType = internationalStringTypes[i];
			if (internationalStringType.getLang() == null /* No language attr. */
					|| (internationalStringType.getLang() != null && internationalStringType
							.getLang().equals(languageCode))) {
				internationalStringType.setStringValue(string);
				return true;
			}
		}
		// TODO add is debug enabled
		log
				.debug("No matching International String found! International Strings: "
						+ internationalStringTypes.toString()
						+ " Language Code: " + languageCode);
		return false;
	}

	private String getInternationalString(
			InternationalStringType[] internationalStringTypes,
			String languageCode) {

		for (int i = 0; i < internationalStringTypes.length; i++) {
			InternationalStringType internationalStringType = internationalStringTypes[i];
			if (internationalStringType.getLang() == null /* No language attr. */
					|| (internationalStringType.getLang() != null && internationalStringType
							.getLang().equals(languageCode))) {
				return internationalStringType.getStringValue();
			}
		}
		// TODO add is debug enabled
		log.debug("*** No matching International String ***");
		return "";
	}

	private boolean setStructuredString(String string,
			StructuredStringType structuredStringType, String languageCode) {

		if (structuredStringType.getLang() == null /* No language attr. */
				|| (structuredStringType.getLang() != null && structuredStringType
						.getLang().equals(languageCode))) {
			XmlBeansUtil.setTextOnMixedElement(structuredStringType, string);
			return true;
		}
		// TODO add is debug enabled
		log.debug("*** No matching Structured String ***");
		return false;
	}

	private String getStructuredString(
			StructuredStringType structuredStringType, String languageCode) {

		if (structuredStringType.getLang() == null /* No language attr. */
				|| (structuredStringType.getLang() != null && structuredStringType
						.getLang().equals(languageCode))) {
			return XmlBeansUtil.getTextOnMixedElement(structuredStringType);
		}
		return "";
	}

	/**
	 * Get element value of a given type corresponding to the given language.
	 * 
	 * @param subElement
	 * @param SUB_ELEMENT_TYPE
	 * @param languageCode
	 * @return String - value of element.
	 */
	public String getTranslatedSubElementValue(SubElement subElement,
			SUB_ELEMENT_TYPE subElementType, String languageCode) {
		InternationalStringType result;
		
		XmlObject[] elements = subElement.getXmlObjects();
		for (int i = 0; i < elements.length; i++) {
			if (elements[i] instanceof NameDocumentImpl) {
				result = ((NameDocumentImpl) elements[i])
						.getName();
			} else {
				result = null;
			}
			if (result.getLang() == null /* No language attr. */
					|| (result.getLang() != null && result
							.getLang().equals(languageCode))) {
				return result.getStringValue();
			}
		}
		return "";
	}

	/**
	 * Set value of the element corresponding to the given language.
	 * 
	 * @param string
	 *            - new value
	 * @param subElement
	 *            - SubElements to be updated.
	 * @param languageCode
	 * @throws DDIFtpException
	 */
	public void setTranslatedSubElementValue(String string, SubElement subElement,
			String languageCode) throws DDIFtpException {
		InternationalStringType result;
		
		XmlObject[] elements = subElement.getXmlObjects();
		for (int i = 0; i < elements.length; i++) {
			if (elements[i] instanceof NameDocumentImpl) {
				result = ((NameDocumentImpl) elements[i])
						.getName();
			} else if (elements[i] instanceof CitationDocumentImpl) {
				result = null; // tmp
			} else if (elements[i] instanceof AbstractDocumentImpl) {
				result = null; // tmp
			} else {
				result = null;
			}
			if (result.getLang() == null /* No language attr. */
					|| (result.getLang() != null && result
							.getLang().equals(languageCode))) {
				result.setStringValue(string);
				subElement.setCrudValue(i + 1); // Updates - starts by '1'
				subElement.changed(true);
				return;
			}
		}
		throw new DDIFtpException(
				"No matching Study Unit subelement found! Sublements: "
						+ subElement.toString() + "Language Code: "
						+ languageCode);
	}

	/**
	 * 
	 * Set Study Unit Citation Title corresponding to the given language
	 * 
	 * @param title
	 * @param languageCode
	 */
	public void setCitationTitle(String title, String languageCode) {
		XmlObject[] citations = citationSubElements.getXmlObjects();
		for (int i = 0; i < citations.length; i++) {
			CitationType citation = ((CitationDocumentImpl) citations[i])
					.getCitation();
			if (setInternationalString(title, citation.getTitleArray(),
					languageCode)) {
				citationSubElements.changed(true);
				citationSubElements.setCrudValue(i + 1); // update - starts by '1'
				return;
			}
			if (i == citations.length - 1) {
				// Existing title not found - at new to last Citation
				InternationalStringType internationalStringType = citation
						.addNewTitle();
				internationalStringType.setStringValue(title);
				internationalStringType.setLang(languageCode);
			}
		}
	}

	/**
	 * 
	 * Get Study Unit Citation Title corresponding to the given language
	 * 
	 * @param languageCode
	 */
	public String getCitationTitle(String languageCode) {
		XmlObject[] citations = citationSubElements.getXmlObjects();
		if (citations != null) {
			for (int i = 0; i < citations.length; i++) {
				CitationType citation = ((CitationDocumentImpl) citations[i])
						.getCitation();
				String title = getInternationalString(citation.getTitleArray(),
						languageCode);
				if (title.length() > 0) {
					return title;
				}
			}
		}
		return "";
	}

	/**
	 * 
	 * Set Study Unit Citation Sub-Title corresponding to the given language
	 * 
	 * @param subTitle
	 * @param languageCode
	 */
	public void setCitationSubTitle(String subTitle, String languageCode) {
		log.debug("StudyUnit.setCitationSubTitle()");
		XmlObject[] citations = citationSubElements.getXmlObjects();
		for (int i = 0; i < citations.length; i++) {
			CitationType citation = ((CitationDocumentImpl) citations[i])
					.getCitation();
			if (setInternationalString(subTitle, citation.getSubTitleArray(),
					languageCode)) {
				citationSubElements.changed(true);
				citationSubElements.setCrudValue(i + 1); // update - starts by '1'
				return;
			}
			if (i == citations.length - 1) {
				// Existing Subtitle not found - at new to last Citation
				InternationalStringType internationalStringType = citation
						.addNewSubTitle();
				internationalStringType.setStringValue(subTitle);
				internationalStringType.setLang(languageCode);
			}
		}
	}

	/**
	 * 
	 * Get Study Unit Citation Sub-Title corresponding to the given language
	 * 
	 * @param languageCode
	 */
	public String getCitationSubTitle(String languageCode) {
		XmlObject[] citations = citationSubElements.getXmlObjects();
		for (int i = 0; i < citations.length; i++) {
			CitationType citation = ((CitationDocumentImpl) citations[i])
					.getCitation();
			String subTitle = getInternationalString(citation
					.getSubTitleArray(), languageCode);
			if (subTitle.length() > 0) {
				return subTitle;
			}
		}
		return "";
	}

	/**
	 * 
	 * Set Study Unit Citation Alternate Title string corresponding to the given
	 * language
	 * 
	 * @param SubTitle
	 * @param languageCode
	 */
	public void setCitationAltTitle(String altTitle, String languageCode) {
		XmlObject[] citations = citationSubElements.getXmlObjects();
		for (int i = 0; i < citations.length; i++) {
			CitationType citation = ((CitationDocumentImpl) citations[i])
					.getCitation();
			if (setInternationalString(altTitle, citation
					.getAlternateTitleArray(), languageCode)) {
				citationSubElements.changed(true);
				citationSubElements.setCrudValue(i + 1); // update - starts by '1'
				return;
			}
			if (i == citations.length - 1) {
				// Existing Alternate Title not found - at new to last Citation
				InternationalStringType internationalStringType = citation
						.addNewAlternateTitle();
				internationalStringType.setStringValue(altTitle);
				internationalStringType.setLang(languageCode);
			}
		}
	}

	/**
	 * 
	 * Get Study Unit Citation Alternate Title string corresponding to the given
	 * language
	 * 
	 * @param languageCode
	 */
	public String getCitationAltTitle(String languageCode) {
		XmlObject[] citations = citationSubElements.getXmlObjects();
		for (int i = 0; i < citations.length; i++) {
			CitationType citation = ((CitationDocumentImpl) citations[i])
					.getCitation();
			String altTitle = getInternationalString(citation
					.getAlternateTitleArray(), languageCode);
			if (altTitle.length() > 0) {
				return altTitle;
			}
		}
		return "";
	}

	/**
	 * 
	 * Set Study Unit Citation Creator corresponding to the given language
	 * 
	 * @param title
	 * @param languageCode
	 */
	public void setCitationCreator(String creator, String languageCode) {

		XmlObject[] citations = citationSubElements.getXmlObjects();
		for (int i = 0; i < citations.length; i++) {
			CitationType citation = ((CitationDocumentImpl) citations[i])
					.getCitation();
			if (setInternationalString(creator, citation.getCreatorArray(),
					languageCode)) {
				citationSubElements.changed(true);
				citationSubElements.setCrudValue(i + 1); // update - starts by '1'
				return;
			}
			if (i == citations.length - 1) {
				// Existing Creator not found - at new to last Citation
				InternationalStringType internationalStringType = citation
						.addNewCreator();
				internationalStringType.setStringValue(creator);
				internationalStringType.setLang(languageCode);
			}
		}
	}

	/**
	 * 
	 * Get Study Unit Citation Creator string corresponding to the given
	 * language
	 * 
	 * @param languageCode
	 */
	public String getCitationCreator(String languageCode) {
		// TODO add is debug enabled
		log.debug("StudyUnit.getCitationSubTitle()");
		XmlObject[] citations = citationSubElements.getXmlObjects();
		for (int i = 0; i < citations.length; i++) {
			CitationType citation = ((CitationDocumentImpl) citations[i])
					.getCitation();
			String creator = getInternationalString(citation.getCreatorArray(),
					languageCode);
			if (creator.length() > 0) {
				return creator;
			}
		}
		return "";
	}

	/**
	 * 
	 * Set Study Unit Citation Publisher corresponding to the given language
	 * 
	 * @param title
	 * @param languageCode
	 */

	public void setCitationPublisher(String publisher, String languageCode) {
		// TODO add is debug enabled
		log.debug("StudyUnit.setCitationPublisher()");
		XmlObject[] citations = citationSubElements.getXmlObjects();
		for (int i = 0; i < citations.length; i++) {
			CitationType citation = ((CitationDocumentImpl) citations[i])
					.getCitation();
			if (setInternationalString(publisher, citation.getPublisherArray(),
					languageCode)) {
				citationSubElements.changed(true);
				citationSubElements.setCrudValue(i + 1); // update - starts by '1'
				return;
			}
			if (i == citations.length - 1) {
				// Existing Publisher not found - at new to last Citation
				InternationalStringType internationalStringType = citation
						.addNewPublisher();
				internationalStringType.setStringValue(publisher);
				internationalStringType.setLang(languageCode);
			}
		}
	}

	/**
	 * 
	 * Get Study Unit Citation Publisher string corresponding to the given
	 * language
	 * 
	 * @param languageCode
	 */

	public String getCitationPublisher(String languageCode) {
		// TODO add is debug enabled
		log.debug("StudyUnit.getCitationSubTitle()");
		XmlObject[] citations = citationSubElements.getXmlObjects();
		for (int i = 0; i < citations.length; i++) {
			CitationType citation = ((CitationDocumentImpl) citations[i])
					.getCitation();
			String publisher = getInternationalString(citation
					.getPublisherArray(), languageCode);
			if (publisher.length() > 0) {
				return publisher;
			}
		}
		return "";
	}

	/**
	 * 
	 * Set Study Unit Citation Contributor corresponding to the given language
	 * 
	 * @param title
	 * @param languageCode
	 */

	public void setCitationContributor(String contributor, String languageCode) {
		// TODO add is debug enabled
		log.debug("StudyUnit.setCitationContributor()");
		XmlObject[] citations = citationSubElements.getXmlObjects();
		for (int i = 0; i < citations.length; i++) {
			CitationType citation = ((CitationDocumentImpl) citations[i])
					.getCitation();
			if (setInternationalString(contributor, citation
					.getContributorArray(), languageCode)) {
				citationSubElements.changed(true);
				citationSubElements.setCrudValue(i + 1); // update - starts by '1'
				return;
			}
			if (i == citations.length - 1) {
				// Existing Contributor not found - at new to last Citation
				InternationalStringType internationalStringType = citation
						.addNewContributor();
				internationalStringType.setStringValue(contributor);
				internationalStringType.setLang(languageCode);
			}
		}
	}

	/**
	 * 
	 * Get Study Unit Citation Contributor string corresponding to the given
	 * language
	 * 
	 * @param languageCode
	 */
	public String getCitationContributor(String languageCode) {
		// TODO add is debug enabled
		log.debug("StudyUnit.getCitationContributor()");
		XmlObject[] citations = citationSubElements.getXmlObjects();
		for (int i = 0; i < citations.length; i++) {
			CitationType citation = ((CitationDocumentImpl) citations[i])
					.getCitation();
			String contributer = getInternationalString(citation
					.getContributorArray(), languageCode);
			if (contributer.length() > 0) {
				return contributer;
			}
		}
		return "";
	}

	/**
	 * 
	 * Set Study Unit Citation Publication Date
	 * 
	 * @param publicationDate
	 */
	public void setCitationPublicationDate(String publicationDate) {
		// TODO add is debug enabled
		log.debug("StudyUnit.setCitationPublicationDate()");
		XmlObject[] citations = citationSubElements.getXmlObjects();
		// TODO add is debug enabled
		if (citations.length > 1) {
			log
					.error("*** More Study Unit Citations - 'Publication Date' of first one set! ***");
		}
		CitationType citation = ((CitationDocumentImpl) citations[0])
				.getCitation();
		DateType dataType = DateType.Factory.newInstance();
		dataType.setSimpleDate(publicationDate);
		citation.setPublicationDate(dataType);
		citationSubElements.changed(true);
		citationSubElements.setCrudValue(0 + 1); // update - starts by '1'

	}

	/**
	 * 
	 * Get Study Unit Citation Publication Date
	 * 
	 * @throws Exception
	 * 
	 */
	public String getCitationPublicationDate() throws Exception {
		// TODO add is debug enabled
		log.debug("StudyUnit.getCitationContributor()");
		XmlObject[] citations = citationSubElements.getXmlObjects();
		if (citations.length > 1) {
			throw new Exception("More Study Unit Citations!");
		}
		CitationType citation = ((CitationDocumentImpl) citations[0])
				.getCitation();
		if (citation != null) {
			DateType dateType = citation.getPublicationDate();
			if (dateType != null) {
				return dateType.getSimpleDate().toString();
			}
		}
		return "";
	}

	/**
	 * 
	 * Set Study Unit Citation Language
	 * 
	 * @param publicationDate
	 */
	public void setCitationLanguage(String language) {
		// TODO add is debug enabled
		log.debug("StudyUnit.setCitationLanguage()");
		XmlObject[] citations = citationSubElements.getXmlObjects();
		// TODO add is debug enabled
		if (citations.length > 1) {
			log
					.error("*** More Study Unit Citations - 'Language' of first one used! ***");
		}
		CitationType citation = ((CitationDocumentImpl) citations[0])
				.getCitation();
		citation.setLanguage(language);
		citationSubElements.changed(true);
		citationSubElements.setCrudValue(0 + 1); // update - starts by '1'
	}

	/**
	 * 
	 * Get Study Unit Citation Language
	 * 
	 */
	public String getCitationLanguage() {
		// TODO add is debug enabled
		log.debug("StudyUnit.getCitationLanguage()");
		XmlObject[] citations = citationSubElements.getXmlObjects();
		// TODO add is debug enabled
		if (citations.length > 1) {
			log
					.error("*** More Study Unit Citations - 'Language' of first one retrieved! ***");
		}
		CitationType citation = ((CitationDocumentImpl) citations[0])
				.getCitation();
		String language = citation.getLanguage();
		if (language != null) {
			return language;
		}
		return "";
	}

	/**
	 * Set Study Unit Abstract Content for the given language
	 * 
	 * @param content
	 * @param LanguageCode
	 */
	public void setAbstractContent(String contentVal, String languageCode) {

		log.debug("StudyUnit.setAbstractContent()");
		XmlObject[] abstracts = abstractSubElements.getXmlObjects();
		for (int i = 0; i < abstracts.length; i++) {
			StructuredStringType content = ((AbstractDocumentImpl) abstracts[i])
					.getAbstract().getContent();
			if (setStructuredString(contentVal, content, languageCode)) {
				abstractSubElements.changed(true);
				abstractSubElements.setCrudValue(i + 1); // update - starts by '1'
				return;
			}
		}
	}

	/**
	 * Get Study Unit Abstract Content for the given language
	 * 
	 * @param languageCode
	 * @return
	 */
	public String getAbstractContent(String languageCode) {

		log.debug("StudyUnit.getAbstractContent()");
		XmlObject[] abstracts = abstractSubElements.getXmlObjects();
		if (abstracts != null) {
			for (int i = 0; i < abstracts.length; i++) {
				StructuredStringType content = ((AbstractDocumentImpl) abstracts[i])
						.getAbstract().getContent();
				String contentString = getStructuredString(content,
						languageCode);
				if (contentString.length() > 0) {
					return contentString;
				}
			}
		}
		return "";
	}

	/**
	 * Set Study Unit Universe Reference ID
	 * 
	 * @param id
	 */
	public void setUniverseRefId(String contentVal) {

		log.debug("StudyUnit.setUniverseRefId()");
		XmlObject[] universeRefs = universeRefSubElements.getXmlObjects();
		IDType id = ((UniverseReferenceDocumentImpl) universeRefs[0])
				.getUniverseReference().getIDArray(0);
		id.setStringValue(contentVal);
		universeRefSubElements.changed(true);
		if (universeRefSubElements.getCrudValue() == 0) {
			return; // New
		}
		universeRefSubElements.setCrudValue(0 + 1); // update - starts by '1'
	}

	/**
	 * Get Study Unit Univerce Reference ID
	 * 
	 * @return
	 */
	public String getUniverseRefId() throws DDIFtpException {

		log.debug("StudyUnit.getUniverseRefId()");
		XmlObject[] universeRefs = universeRefSubElements.getXmlObjects();
		if (universeRefs.length == 0) {
			log
					.debug("No Universe Reference element found - create new element");
			UniverseReferenceDocument universeReferenceDocument = UniverseReferenceDocument.Factory
					.newInstance();
			universeRefSubElements.addXmlObject((XmlObject) universeReferenceDocument);
			universeRefSubElements.changed(true);
			universeRefSubElements.setCrudValue(0); // New
			ReferenceType referenceType = universeReferenceDocument
					.addNewUniverseReference();
			referenceType.addNewID().setStringValue("");
			referenceType.addIdentifyingAgency("");
			return "";
		}
		if (universeRefs.length > 1) {
			throw new DDIFtpException(
					"Only one Universe Reference element currently supported");
		}
		List<IDType> universeRef = ((UniverseReferenceDocumentImpl) universeRefs[0])
				.getUniverseReference().getIDList();
		if (universeRef.size() != 1) {
			throw new DDIFtpException(
					"Only one Universe Reference ID currently supported");
		}
		IDType universeRefType = universeRef.get(0);
		return universeRefType.getStringValue();
	}

	private ReferenceType getFundingAgencyOrganizationReference()
			throws DDIFtpException {

		log.debug("StudyUnit.getFundingAgencyOrganizationReference()");
		XmlObject[] fundings = fundingSubElements.getXmlObjects();
		if (fundings.length == 0) {
			log
					.debug("No Funding Information element found - create new element");
			FundingInformationDocument fundingInformationDocument = FundingInformationDocument.Factory
					.newInstance();
			fundingSubElements.addXmlObject((XmlObject) fundingInformationDocument);
			fundingSubElements.changed(true);
			fundingSubElements.setCrudValue(0); // New
			ReferenceType agencyReferenceType = fundingInformationDocument
					.addNewFundingInformation()
					.addNewAgencyOrganizationReference();
			agencyReferenceType.addNewID().setStringValue("");
			agencyReferenceType.addIdentifyingAgency("");
			return agencyReferenceType;
		}
		if (fundings.length > 1) {
			throw new DDIFtpException(
					Messages
							.getString("StudyUnitEditor.mess.OnlyOneFundingInformationEntityCurrentlySupported"));
		}
		List<ReferenceType> agencyRef = ((FundingInformationDocumentImpl) fundings[0])
				.getFundingInformation().getAgencyOrganizationReferenceList();
		if (agencyRef.size() != 1) {
			throw new DDIFtpException(
					Messages
							.getString("StudyUnitEditor.mess.OnlyOneFundingInformationAgencyOrganizationReferenceCurrentlySupported"));
		}
		ReferenceType agencyReferenceType = agencyRef.get(0);
		return agencyReferenceType;
	}

	/**
	 * Set Funding Agency ID
	 * 
	 * Note: Only one-to-on relation between FundingInformation,
	 * AgencyOrganisationReference and ID/IdentifyingAgency is supported.
	 * 
	 * @param ID
	 *            - identifies the agency inside the organisation
	 */
	public void setFundingAgencyID(String agencyID) throws DDIFtpException {

		log.debug("StudyUnit.setFundingAgencyID()");
		ReferenceType referenceType = getFundingAgencyOrganizationReference();
		if (referenceType == null) {
			return;
		} else {
			referenceType.removeID(0);
		}
		referenceType.addNewID().setStringValue(agencyID);
		fundingSubElements.changed(true);
		if (fundingSubElements.getCrudValue() == 0) {
			return; // New
		}
		fundingSubElements.setCrudValue(0 + 1); // update - starts by '1'
	}

	/**
	 * Set Funding Identifying Agency
	 * 
	 * Note: Only one-to-on relation between FundingInformation,
	 * AgencyOrganisationReference and ID/IdentifyingAgency is supported.
	 * 
	 * @param agencyOrganisation
	 *            -defines the organisation
	 */
	public void setFundingIdentifyingAgency(String agencyOrganisation)
			throws DDIFtpException {

		log.debug("StudyUnit.setFundingIdentifyingAgency()");
		ReferenceType referenceType = getFundingAgencyOrganizationReference();
		if (referenceType == null) {
			return;
		} else {
			referenceType.removeIdentifyingAgency(0);
		}
		referenceType.addIdentifyingAgency(agencyOrganisation);
		fundingSubElements.changed(true);
		if (fundingSubElements.getCrudValue() == 0) {
			return; // New
		}
		fundingSubElements.setCrudValue(0 + 1); // update - starts by '1'
	}

	/**
	 * Get Funding Agency ID
	 * 
	 * Note: Only one-to-on relation between FundingInformation,
	 * AgencyOrganisationReference and ID/IdentifyingAgency is supported.
	 * 
	 */
	public String getFundingAgencyID() throws DDIFtpException {

		log.debug("StudyUnit.getFundingAgencyID()");
		ReferenceType referenceType = getFundingAgencyOrganizationReference();
		return referenceType == null ? "" : referenceType.getIDArray(0)
				.getStringValue();
	}

	/**
	 * Get Funding Identifying Agency
	 * 
	 * Note: Only one-to-on relation between FundingInformation,
	 * AgencyOrganisationReference and ID/IdentifyingAgency is supported.
	 * 
	 */
	public String getFundingIdentifyingAgency() throws DDIFtpException {
		log.debug("StudyUnit.getFundingIdentifyingAgency()");
		ReferenceType referenceType = getFundingAgencyOrganizationReference();
		if (referenceType == null) {
			return "";
		}
		return referenceType.getIdentifyingAgencyList().size() == 0 ? ""
				: referenceType.getIdentifyingAgencyList().get(0);
	}

	/**
	 * Set Study Unit Purpose Content for the given language
	 * 
	 * @param content
	 * @param LanguageCode
	 */
	public void setPurposeContent(String contentVal, String languageCode) {

		log.debug("StudyUnit.setPurposeContent()");
		XmlObject[] purposes = purposeSubElements.getXmlObjects();
		for (int i = 0; i < purposes.length; i++) {
			StructuredStringType content = ((PurposeDocumentImpl) purposes[i])
					.getPurpose().getContent();
			if (setStructuredString(contentVal, content, languageCode)) {
				purposeSubElements.changed(true);
				purposeSubElements.setCrudValue(i + 1); // update - starts by '1'
				return;
			}
		}
	}

	/**
	 * Get Study Unit Purpose Content for the given language
	 * 
	 * @param languageCode
	 * @return
	 */
	public String getPurposeContent(String languageCode) {

		log.debug("StudyUnit.getPurposeContent()");
		XmlObject[] purposes = purposeSubElements.getXmlObjects();
		if (purposes != null) {
			for (int i = 0; i < purposes.length; i++) {
				StructuredStringType content = ((PurposeDocumentImpl) purposes[i])
						.getPurpose().getContent();
				String contentString = getStructuredString(content,
						languageCode);
				if (contentString.length() > 0) {
					return contentString;
				}
			}
		}
		return "";
	}

	protected class SubElement {
		private List<XmlObject> elements;
		// Note: Currently only single update is supported
		private boolean changed = false;
		private int crudValue = -1;
	
		SubElement(XmlObject[] elementArray) {
			elements = new ArrayList<XmlObject>();
			for (int i = 0; i < elementArray.length; i++) {
				addXmlObject(elementArray[i]);
			}
		}
	
		protected void addXmlObject(XmlObject xmlObject) {
			elements.add(xmlObject);
		}
	
		protected XmlObject[] getXmlObjects() {
			XmlObject[] xmlObject = new XmlObject[elements.size()];
			elements.toArray(xmlObject);
			return xmlObject;
		}
	
		protected void changed(boolean value) {
			this.changed = value;
		}
	
		protected boolean getChangedStatus() {
			return this.changed;
		}
	
		protected void setCrudValue(int crudValue) {
			this.crudValue = crudValue;
		}
	
		protected int getCrudValue() {
			return crudValue;
		}
	
		protected XmlObject getModifiedXmlObject() {
			if (changed) {
				if (crudValue == 0) {
					// New:
					return (XmlObject) elements.get(crudValue);
				}
				// Update - starts by '1' - index by '0'
				return (XmlObject) elements.get(crudValue - 1);
			}
			return null;
		}
	}
}
