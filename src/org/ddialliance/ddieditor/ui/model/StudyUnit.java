package org.ddialliance.ddieditor.ui.model;

/**
 * Study Unit model.
 * 
 */
/*
 * $Author$ 
 * $Date$ 
 * $Revision$
 */

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.ddialliance.ddi_3_0.xml.xmlbeans.conceptualcomponent.UniverseReferenceDocument;
import org.ddialliance.ddi_3_0.xml.xmlbeans.conceptualcomponent.impl.UniverseReferenceDocumentImpl;
import org.ddialliance.ddi_3_0.xml.xmlbeans.reusable.CitationType;
import org.ddialliance.ddi_3_0.xml.xmlbeans.reusable.IDType;
import org.ddialliance.ddi_3_0.xml.xmlbeans.reusable.IdentifiedStructuredStringType;
import org.ddialliance.ddi_3_0.xml.xmlbeans.reusable.InternationalStringType;
import org.ddialliance.ddi_3_0.xml.xmlbeans.reusable.ReferenceType;
import org.ddialliance.ddi_3_0.xml.xmlbeans.reusable.StructuredStringType;
import org.ddialliance.ddi_3_0.xml.xmlbeans.reusable.impl.AlternateTitleDocumentImpl;
import org.ddialliance.ddi_3_0.xml.xmlbeans.reusable.impl.CitationDocumentImpl;
import org.ddialliance.ddi_3_0.xml.xmlbeans.reusable.impl.FundingInformationDocumentImpl;
import org.ddialliance.ddi_3_0.xml.xmlbeans.reusable.impl.NameDocumentImpl;
import org.ddialliance.ddi_3_0.xml.xmlbeans.reusable.impl.ReferenceTypeImpl;
import org.ddialliance.ddi_3_0.xml.xmlbeans.studyunit.impl.AbstractDocumentImpl;
import org.ddialliance.ddi_3_0.xml.xmlbeans.studyunit.impl.PurposeDocumentImpl;
import org.ddialliance.ddi_3_0.xml.xmlbeans.reusable.DateType;
import org.ddialliance.ddieditor.persistenceaccess.maintainablelabel.MaintainableLabelQueryResult;
import org.ddialliance.ddieditor.persistenceaccess.maintainablelabel.MaintainableLabelUpdateElement;
import org.ddialliance.ddieditor.ui.dbxml.Util;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;

public class StudyUnit extends Model {
	/**
	 * Class used for saving information about Study Unit Sub-Elements e.g. 
	 * array of objects of a given type (e.g. Citations), change flag 
	 * and CRUD value. 
	 * 
	 * @author dak
	 *
	 */
	protected class SubElements {
		private XmlObject[] xmlObjects;
		// Note: Currently only single update is supported
		private boolean changed = false;
		private int crudValue;
		
		SubElements(XmlObject[] xmlObjects) {
			this.xmlObjects = xmlObjects;
		}
		
		protected void addXmlObject(XmlObject xmlObject) {
			// TODO Add XML object
		}
		
		protected XmlObject[] getXmlObjects() {
			return xmlObjects;
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
				return xmlObjects[crudValue-1]; // Updates start by '1' - index by '0'
			}
			return null;
		}
	}
	
	private static Log log = LogFactory.getLog(LogType.SYSTEM, StudyUnit.class);
	public static enum SUB_ELEMENT_TYPE {NAME, CITATION_TITLE, CITATION_SUBTITLE}; 
	private MaintainableLabelQueryResult studyUnitQueryResult = null;
	private SubElements xnames;
	private SubElements xcitations;
	private SubElements xabstracts;
	private UniverseReferenceDocumentImpl[] universerefs = null;
	private SubElements xfundings;
	private SubElements xpurposes;
//	private PurposeDocumentImpl[] purposes = null; zzz
	private boolean languageChanged = false;
	private boolean contentChanged = false;
	
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
	public StudyUnit(String id, String version, String parentId, String parentVersion,
			MaintainableLabelQueryResult studyUnitQueryResult) {
		super(id, version, parentId, parentVersion);
		log.debug("StudyUnit.StudyUnit()");
		this.studyUnitQueryResult = studyUnitQueryResult;
		try {
			xnames = new SubElements(studyUnitQueryResult.getSubElement("Name"));
			xcitations = new SubElements(studyUnitQueryResult.getSubElement("Citation"));
			xabstracts = new SubElements(studyUnitQueryResult.getSubElement("Abstract"));
//			universerefs = getUniverseReferences(studyUnitQueryResult.getSubElement("UniverseReference"));
			xfundings = new SubElements(studyUnitQueryResult.getSubElement("FundingInformation"));
			xpurposes = new SubElements(studyUnitQueryResult.getSubElement("Purpose"));
//			purposes = getPurposes(studyUnitQueryResult.getSubElement("Purpose"));
		} catch (DDIFtpException e) {
			// TODO Auto-gener1ated catch block
			System.out.println("GetSubElement DDIFtpException: "+e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Clear changed status of Study Unit subelements
	 */
	public void clearChanged() {
		xnames.changed(false);
		xcitations.changed(false);
	}
	
	private AbstractDocumentImpl[] getAbstracts(XmlObject[] xmlObjects) {
		AbstractDocumentImpl[] abstracts = new AbstractDocumentImpl[xmlObjects.length];
		
		for (int i = 0; i < xmlObjects.length; i++) {
			if (xmlObjects[i] instanceof AbstractDocumentImpl) {
				abstracts[i] = (AbstractDocumentImpl )xmlObjects[i];
			}
		}
		return abstracts;
	}
	
	private UniverseReferenceDocumentImpl[] getUniverseReferences(XmlObject[] xmlObjects) {
		UniverseReferenceDocumentImpl[] universeRerencences = new UniverseReferenceDocumentImpl[xmlObjects.length];
		
		for (int i = 0; i < xmlObjects.length; i++) {
			if (xmlObjects[i] instanceof UniverseReferenceDocumentImpl) {
				universeRerencences[i] = (UniverseReferenceDocumentImpl )xmlObjects[i];
			}
		}
		return universeRerencences;
	}
	
	private PurposeDocumentImpl[] getPurposes(XmlObject[] xmlObjects) {
		PurposeDocumentImpl[] purposes = new PurposeDocumentImpl[xmlObjects.length];
		
		for (int i = 0; i < xmlObjects.length; i++) {
			if (xmlObjects[i] instanceof PurposeDocumentImpl) {
				purposes[i] = (PurposeDocumentImpl )xmlObjects[i];
			}
		}
		return purposes;
	}
	
	
	private boolean setInternationalString(String string, InternationalStringType[] internationalStringTypes, String languageCode) {

		for (int i = 0; i < internationalStringTypes.length; i++) {
			InternationalStringType internationalStringType = internationalStringTypes[i];
			if (internationalStringType.getLang() == null /* No language attr. */
					|| (internationalStringType.getLang() != null && internationalStringType.getLang().equals(
							languageCode))) {
				internationalStringType.setStringValue(string);
				return true;
			}
		}
		log.debug("No matching International String found! International Strings: "
				+ internationalStringTypes.toString() + " Language Code: " + languageCode);
		return false;
	}
	
	private String getInternationalString(InternationalStringType[] internationalStringTypes, String languageCode) {

		for (int i = 0; i < internationalStringTypes.length; i++) {
			InternationalStringType internationalStringType = internationalStringTypes[i];
			if (internationalStringType.getLang() == null /* No language attr. */
					|| (internationalStringType.getLang() != null && internationalStringType.getLang().equals(
							languageCode))) {
				return internationalStringType.getStringValue();
			}
		}
		// TODO Error handling
		System.out.println("*** No matching International String ***");
		return "";
	}

	private boolean setStructuredString(String string, StructuredStringType structuredStringType, String languageCode) {

		if (structuredStringType.getLang() == null /* No language attr. */
				|| (structuredStringType.getLang() != null && structuredStringType.getLang().equals(languageCode))) {
			Util.setTextOnMixedElement(structuredStringType, string);
			return true;
		}
		// TODO Error handling
		System.out.println("*** No matching Structured String ***");
		return false;
	}

	private String getStructuredString(StructuredStringType structuredStringType, String languageCode) {

		if (structuredStringType.getLang() == null /* No language attr. */
				|| (structuredStringType.getLang() != null && structuredStringType.getLang().equals(languageCode))) {
			return Util.getTextOnMixedElement(structuredStringType);
		}
		// TODO Error handling
		System.out.println("*** No matching Structured String ***");
		return "";
	}

	/**
	 * Get element value of a given type corresponding to the given language.
	 * 
	 * @param subElements
	 * @param SUB_ELEMENT_TYPE
	 * @param languageCode
	 * @return String - value of element.
	 */
	public String xgetElement(SubElements subElements, SUB_ELEMENT_TYPE subElementType, String languageCode) {
		InternationalStringType internationalStringType;

		log.debug("StudyUnit.xgetElement()");
		XmlObject[] xmlObjects = subElements.getXmlObjects();
		for (int i = 0; i < xmlObjects.length; i++) {
			if (xmlObjects[i] instanceof NameDocumentImpl) {
				internationalStringType = ((NameDocumentImpl) xmlObjects[i]).getName();
			} else {
				internationalStringType = null;
			}
			if (internationalStringType.getLang() == null /* No language attr. */
					|| (internationalStringType.getLang() != null && internationalStringType.getLang().equals(
							languageCode))) {
				return internationalStringType.getStringValue();
			} else {
				// TODO Error handling
				System.out.println("*** No matching sub-element ***");
			}
		}
		return "";
	}
	
	/**
	 * Set value of the element corresponding to the given language.
	 * 
	 * @param string - new value
	 * @param subElements - SubElements to be updated.
	 * @param languageCode
	 * @throws DDIFtpException 
	 */
	public void xsetElement(String string, SubElements subElements, String languageCode) throws DDIFtpException {
		InternationalStringType internationalStringType;

		log.debug("StudyUnit.xsetElement()");
		XmlObject[] xmlObjects = subElements.getXmlObjects();
		for (int i = 0; i < xmlObjects.length; i++) {
			if (xmlObjects[i] instanceof NameDocumentImpl) {
				internationalStringType = ((NameDocumentImpl) xmlObjects[i]).getName();
			} else if (xmlObjects[i] instanceof CitationDocumentImpl) {
				internationalStringType = null; // tmp
			} else if (xmlObjects[i] instanceof AbstractDocumentImpl) {
				internationalStringType = null; // tmp
			} else {
				internationalStringType = null;
			}
			if (internationalStringType.getLang() == null /* No language attr. */
					|| (internationalStringType.getLang() != null && internationalStringType.getLang().equals(
							languageCode))) {
				internationalStringType.setStringValue(string);
				subElements.setCrudValue(i + 1); // Updates - starts by '1'
				subElements.changed(true);
				return;
			}
		}
		throw new DDIFtpException("No matching Study Unit subelement found! Sublements: " + subElements.toString()
				+ "Language Code: " + languageCode);
	}
	
	/**
	 * Set Study Unit Name corresponding to the given language (first one).
	 * 
	 * @param name
	 * @param languageCode
	 * @throws DDIFtpException 
	 */
	public void setName(String name, String languageCode) throws DDIFtpException {

		log.debug("StudyUnit.setName()");
		xsetElement(name, xnames, languageCode);
	}
	
	/**
	 * 
	 * Get Study Unit Name corresponding to the given
	 * language (first one returned).
	 * 
	 * @param languageCode
	 */
	public String getName(String languageCode) {
		
		log.debug("StudyUnit.getName()");
		return xgetElement(xnames, SUB_ELEMENT_TYPE.NAME, languageCode);
		
	}
	
	/**
	 * 
	 * Set Study Unit Citation Title corresponding to the given
	 * language
	 * 
	 * @param title
	 * @param languageCode
	 */
	@SuppressWarnings("deprecation")
	public void setCitationTitle(String title, String languageCode) {
		
		log.debug("StudyUnit.setCitationTitle()");
		XmlObject[] citations = xcitations.getXmlObjects();
		for (int i = 0; i < citations.length; i++) {
			CitationType citation = ((CitationDocumentImpl) citations[i]).getCitation();
			if (setInternationalString(title, citation.getTitleArray(), languageCode)) {
				xcitations.changed(true);
				xcitations.setCrudValue(i + 1); // update - starts by '1'
				return;
			}
			if (i == citations.length - 1) {
				// Existing title not found - at new to last Citation
				InternationalStringType internationalStringType = citation.addNewTitle();
				internationalStringType.setStringValue(title);
				internationalStringType.setLang(languageCode);
			}
		}
	}
	
	/**
	 * 
	 * Get Study Unit Citation Title corresponding to the given
	 * language
	 * 
	 * @param languageCode
	 */
	@SuppressWarnings("deprecation")
	public String getCitationTitle(String languageCode) {

		log.debug("StudyUnit.getCitationTitle()");
		XmlObject[] citations = xcitations.getXmlObjects();
		if (citations != null) {
			for (int i = 0; i < citations.length; i++) {
				CitationType citation = ((CitationDocumentImpl) citations[i]).getCitation();
				String title = getInternationalString(citation.getTitleArray(), languageCode);
				if (title.length() > 0) {
					return title;
				}
			}
		}
		return "";
	}
	
	/**
	 * 
	 * Set Study Unit Citation Sub-Title corresponding to the given
	 * language
	 * 
	 * @param subTitle
	 * @param languageCode
	 */
	@SuppressWarnings("deprecation")
	public void setCitationSubTitle(String subTitle, String languageCode) {
		
		log.debug("StudyUnit.setCitationSubTitle()");
		XmlObject[] citations = xcitations.getXmlObjects();
		for (int i = 0; i < citations.length; i++) {
			CitationType citation = ((CitationDocumentImpl) citations[i]).getCitation();
			if (setInternationalString(subTitle, citation.getSubTitleArray(), languageCode)) {
				xcitations.changed(true);
				xcitations.setCrudValue(i + 1); // update - starts by '1'
				return;
			}
			if (i == citations.length - 1) {
				// Existing Subtitle not found - at new to last Citation
				InternationalStringType internationalStringType = citation.addNewSubTitle();
				internationalStringType.setStringValue(subTitle);
				internationalStringType.setLang(languageCode);
			}
		}
	}
	
	/**
	 * 
	 * Get Study Unit Citation Sub-Title corresponding to the given
	 * language
	 * 
	 * @param languageCode
	 */
	@SuppressWarnings("deprecation")
	public String getCitationSubTitle(String languageCode) {

		log.debug("StudyUnit.getCitationSubTitle()");
		XmlObject[] citations = xcitations.getXmlObjects();
		for (int i = 0; i < citations.length; i++) {
			CitationType citation = ((CitationDocumentImpl) citations[i]).getCitation();
			String subTitle = getInternationalString(citation.getSubTitleArray(), languageCode);
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
		
		log.debug("StudyUnit.setCitationAltTitle()");
		XmlObject[] citations = xcitations.getXmlObjects();
		for (int i = 0; i < citations.length; i++) {
			CitationType citation = ((CitationDocumentImpl) citations[i]).getCitation();
			if (setInternationalString(altTitle, citation.getAlternateTitleArray(), languageCode)) {
				xcitations.changed(true);
				xcitations.setCrudValue(i + 1); // update - starts by '1'
				return;
			}
			if (i == citations.length - 1) {
				// Existing Alternate Title not found - at new to last Citation
				InternationalStringType internationalStringType = citation.addNewAlternateTitle();
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
		
		log.debug("StudyUnit.getCitationAltTitle()");
		XmlObject[] citations = xcitations.getXmlObjects();
		for (int i = 0; i < citations.length; i++) {
			CitationType citation = ((CitationDocumentImpl) citations[i]).getCitation();
			String altTitle = getInternationalString(citation.getAlternateTitleArray(), languageCode);
			if (altTitle.length() > 0) {
				return altTitle;
			}
		}
		return "";
	}
	
	/**
	 * 
	 * Set Study Unit Citation Creator corresponding to the given
	 * language
	 * 
	 * @param title
	 * @param languageCode
	 */
	@SuppressWarnings("deprecation")
	public void setCitationCreator(String creator, String languageCode) {
		
		XmlObject[] citations = xcitations.getXmlObjects();
		for (int i = 0; i < citations.length; i++) {
			CitationType citation = ((CitationDocumentImpl) citations[i]).getCitation();
			if (setInternationalString(creator, citation.getCreatorArray(), languageCode)) {
				xcitations.changed(true);
				xcitations.setCrudValue(i + 1); // update - starts by '1'
				return;
			}
			if (i == citations.length - 1) {
				// Existing Creator not found - at new to last Citation
				InternationalStringType internationalStringType = citation.addNewCreator();
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
	@SuppressWarnings("deprecation")
	public String getCitationCreator(String languageCode) {
		
		log.debug("StudyUnit.getCitationSubTitle()");
		XmlObject[] citations = xcitations.getXmlObjects();
		for (int i = 0; i < citations.length; i++) {
			CitationType citation = ((CitationDocumentImpl )citations[i]).getCitation();
			String creator = getInternationalString(citation.getCreatorArray(), languageCode);
			if (creator.length() > 0) {
				return creator;
			}
		}
		return "";
	}
	
	/**
	 * 
	 * Set Study Unit Citation Publisher corresponding to the given
	 * language
	 * 
	 * @param title
	 * @param languageCode
	 */
	@SuppressWarnings("deprecation")
	public void setCitationPublisher(String publisher, String languageCode) {
		
		log.debug("StudyUnit.setCitationPublisher()");
		XmlObject[] citations = xcitations.getXmlObjects();
		for (int i = 0; i < citations.length; i++) {
			CitationType citation = ((CitationDocumentImpl) citations[i]).getCitation();
			if (setInternationalString(publisher, citation.getPublisherArray(), languageCode)) {
				xcitations.changed(true);
				xcitations.setCrudValue(i + 1); // update - starts by '1'
				return;
			}
			if (i == citations.length - 1) {
				// Existing Publisher not found - at new to last Citation
				InternationalStringType internationalStringType = citation.addNewPublisher();
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
	@SuppressWarnings("deprecation")
	public String getCitationPublisher(String languageCode) {
		
		log.debug("StudyUnit.getCitationSubTitle()");
		XmlObject[] citations = xcitations.getXmlObjects();
		for (int i = 0; i < citations.length; i++) {
			CitationType citation = ((CitationDocumentImpl )citations[i]).getCitation();
			String publisher = getInternationalString(citation.getPublisherArray(), languageCode);
			if (publisher.length() > 0) {
				return publisher;
			}
		}
		return "";
	}
	
	/**
	 * 
	 * Set Study Unit Citation Contributor corresponding to the given
	 * language
	 * 
	 * @param title
	 * @param languageCode
	 */
	@SuppressWarnings("deprecation")
	public void setCitationContributor(String contributor, String languageCode) {
		
		log.debug("StudyUnit.setCitationContributor()");
		XmlObject[] citations = xcitations.getXmlObjects();
		for (int i = 0; i < citations.length; i++) {
			CitationType citation = ((CitationDocumentImpl) citations[i]).getCitation();
			if (setInternationalString(contributor, citation.getContributorArray(), languageCode)) {
				xcitations.changed(true);
				xcitations.setCrudValue(i + 1); // update - starts by '1'
				return;
			}
			if (i == citations.length - 1) {
				// Existing Contributor not found - at new to last Citation
				InternationalStringType internationalStringType = citation.addNewContributor();
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
	@SuppressWarnings("deprecation")
	public String getCitationContributor(String languageCode) {
		
		log.debug("StudyUnit.getCitationContributor()");
		XmlObject[] citations = xcitations.getXmlObjects();
		for (int i = 0; i < citations.length; i++) {
			CitationType citation = ((CitationDocumentImpl )citations[i]).getCitation();
			String contributer = getInternationalString(citation.getContributorArray(), languageCode);
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

		log.debug("StudyUnit.setCitationPublicationDate()");
		XmlObject[] citations = xcitations.getXmlObjects();
		if (citations.length > 1) {
			log.error("*** More Study Unit Citations - 'Publication Date' of first one set! ***");
		}
		CitationType citation = ((CitationDocumentImpl )citations[0]).getCitation();
		DateType dataType = DateType.Factory.newInstance();
		dataType.setSimpleDate(publicationDate);
		citation.setPublicationDate(dataType);
		xcitations.changed(true);
		xcitations.setCrudValue(0+1); // update - starts by '1'

	}
	
	/**
	 * 
	 * Get Study Unit Citation Publication Date
	 * @throws Exception 
	 * 
	 */
	public String getCitationPublicationDate() throws Exception {
		
		log.debug("StudyUnit.getCitationContributor()");
		XmlObject[] citations = xcitations.getXmlObjects();
		if (citations.length > 1) {
			throw new Exception("More Study Unit Citations!");
		}
		CitationType citation = ((CitationDocumentImpl) citations[0]).getCitation();
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

		log.debug("StudyUnit.setCitationLanguage()");
		XmlObject[] citations = xcitations.getXmlObjects();
		if (citations.length > 1) {
			log.error("*** More Study Unit Citations - 'Language' of first one used! ***");
		}
		CitationType citation = ((CitationDocumentImpl )citations[0]).getCitation();
		citation.setLanguage(language);
		xcitations.changed(true);
		xcitations.setCrudValue(0+1); // update - starts by '1'
	}
	
	/**
	 * 
	 * Get Study Unit Citation Language
	 * 
	 */
	public String getCitationLanguage() {

		log.debug("StudyUnit.getCitationLanguage()");
		XmlObject[] citations = xcitations.getXmlObjects();
		if (citations.length > 1) {
			log.error("*** More Study Unit Citations - 'Language' of first one retrieved! ***");
		}
		CitationType citation = ((CitationDocumentImpl )citations[0]).getCitation();
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
		XmlObject[] abstracts = xabstracts.getXmlObjects();
		for (int i = 0; i < abstracts.length; i++) {
			StructuredStringType content = ((AbstractDocumentImpl) abstracts[i]).getAbstract().getContent();
			if (setStructuredString(contentVal, content, languageCode)) {
				xabstracts.changed(true);
				xabstracts.setCrudValue(i + 1); // update - starts by '1'
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
		XmlObject[] abstracts = xabstracts.getXmlObjects();
		if (abstracts != null) {
			for (int i = 0; i < abstracts.length; i++) {
				StructuredStringType content = ((AbstractDocumentImpl) abstracts[i]).getAbstract().getContent();
				String contentString = getStructuredString(content, languageCode);
				if (contentString.length() > 0) {
					return contentString;
				}
			}
		}
		return "";
	}
	
	private ReferenceType getFundingAgencyOrganizationReference() {

		log.debug("StudyUnit.getFundingAgencyOrganizationReference()");
		XmlObject[] fundings = xfundings.getXmlObjects();
		if (fundings.length == 0) {
			log.error("No Funding element found");
			// TODO Create new FundingInformation object and return it
			return null;
		}
		if (fundings.length > 1) {
			log.error("Only one Funding element currently supported");
			return null;
		}
		List<ReferenceType> agencyRef = ((FundingInformationDocumentImpl) fundings[0]).getFundingInformation()
				.getAgencyOrganizationReferenceList();
		if (agencyRef.size() != 1) {
			log.error("Only one Agency Organization Reference currently supported");
			return null;
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
	 * @param ID - identifies the agency inside the organisation
	 */
	public void setFundingAgencyID(String agencyID) {

		log.debug("StudyUnit.setFundingAgencyID()");
		ReferenceType referenceType = getFundingAgencyOrganizationReference();
		if (referenceType == null) {
			referenceType = ReferenceType.Factory.newInstance();
		} else {
			referenceType.removeID(0);
		}
		referenceType.addNewID().setStringValue(agencyID);
		xfundings.changed(true);
		xfundings.setCrudValue(0 + 1); // update - starts by '1'
	}
	
	/**
	 * Set Funding Identifying Agency
	 * 
	 * Note: Only one-to-on relation between FundingInformation,
	 * AgencyOrganisationReference and ID/IdentifyingAgency is supported.
	 * 
	 * @param agencyOrganisation -defines the organisation
	 */
	public void setFundingIdentifyingAgency(String agencyOrganisation) {

		log.debug("StudyUnit.setFundingIdentifyingAgency()");
		ReferenceType referenceType = getFundingAgencyOrganizationReference();
		referenceType.removeIdentifyingAgency(0);
		referenceType.addIdentifyingAgency(agencyOrganisation);
		xfundings.changed(true);
		xfundings.setCrudValue(0 + 1); // update - starts by '1'
	}
	
	/**
	 * Get Funding Agency ID
	 * 
	 * Note: Only one-to-on relation between FundingInformation,
	 * AgencyOrganisationReference and ID/IdentifyingAgency is supported.
	 * 
	 */
	public String getFundingAgencyID() {

		log.debug("StudyUnit.getFundingAgencyID()");
		ReferenceType referenceType = getFundingAgencyOrganizationReference();
		return referenceType == null ? "" : referenceType.getIDArray(0).getStringValue();
	}

	/**
	 * Get Funding Identifying Agency
	 * 
	 * Note: Only one-to-on relation between FundingInformation,
	 * AgencyOrganisationReference and ID/IdentifyingAgency is supported.
	 * 
	 */
	public String getFundingIdentifyingAgency() {

		log.debug("StudyUnit.getFundingIdentifyingAgency()");
		ReferenceType referenceType = getFundingAgencyOrganizationReference();
		return referenceType == null ? "" : referenceType.getIdentifyingAgencyArray(0);
	}

	/**
	 * Set Study Unit Purpose Content for the given language
	 * 
	 * @param content
	 * @param LanguageCode
	 */
	public void setPurposeContent(String contentVal, String languageCode) {

		log.debug("StudyUnit.setPurposeContent()");
		XmlObject[] purposes = xpurposes.getXmlObjects();
		for (int i = 0; i < purposes.length; i++) {
			StructuredStringType content = ((PurposeDocumentImpl) purposes[i]).getPurpose().getContent();
			if (setStructuredString(contentVal, content, languageCode)) {
				xpurposes.changed(true);
				xpurposes.setCrudValue(i + 1); // update - starts by '1'
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
		XmlObject[] purposes = xpurposes.getXmlObjects();
		if (purposes != null) {
			for (int i = 0; i < purposes.length; i++) {
				StructuredStringType content = ((PurposeDocumentImpl) purposes[i]).getPurpose().getContent();
				String contentString = getStructuredString(content, languageCode);
				if (contentString.length() > 0) {
					return contentString;
				}
			}
		}
		return "";
	}
	
	private MaintainableLabelUpdateElement getNameUpdateElement() {

		log.debug("StudyUnit.getNameUpdateElement()");
		if (xnames.getChangedStatus()) {
			MaintainableLabelUpdateElement updName = new MaintainableLabelUpdateElement();

			updName.setLocalName("Name");
			// TODO Compute Crud value
			updName.setCrudValue(xnames.getCrudValue()); // Update
			updName.setValue(xnames.getModifiedXmlObject().xmlText());
			return updName;

		}
		return null;
	}
	
	private MaintainableLabelUpdateElement getCitationUpdateElement() {

		log.debug("StudyUnit.getCitationUpdateElement()");
		if (xcitations.getChangedStatus()) {
			MaintainableLabelUpdateElement updCitation = new MaintainableLabelUpdateElement();

			updCitation.setLocalName("Citation");
			// TODO Compute Crud value
			updCitation.setCrudValue(xcitations.getCrudValue());
			updCitation.setValue(xcitations.getModifiedXmlObject().xmlText());
			return updCitation;
		}
		return null;
	}
	
	private MaintainableLabelUpdateElement getAbstractUpdateElement() {
		
		log.debug("StudyUnit.getAbstractUpdateElement()");
		if (xabstracts.changed) {
			MaintainableLabelUpdateElement updAbstract = new MaintainableLabelUpdateElement();

			updAbstract.setLocalName("Abstract");
			updAbstract.setCrudValue(xabstracts.getCrudValue()); // Update
			updAbstract.setValue(xabstracts.getModifiedXmlObject().xmlText());
			return updAbstract;
		}
		return null;
	}
	
	private MaintainableLabelUpdateElement getUniverseReferenceUpdateElement() {
		// TODO UniverseReferenceUpdateElement
		return null;
	}
	
	private MaintainableLabelUpdateElement getFundingInformationUpdateElement() {
		
		log.debug("StudyUnit.getFundingInformationUpdateElement()");
		if (xfundings.changed) {
			MaintainableLabelUpdateElement updFunding = new MaintainableLabelUpdateElement();

			updFunding.setLocalName("FundingInformation");
			updFunding.setCrudValue(xfundings.getCrudValue()); // Update
			updFunding.setValue(xfundings.getModifiedXmlObject().xmlText());
			return updFunding;
		}
		return null;
	}
	
	private MaintainableLabelUpdateElement getPurposeUpdateElementUpdateElement() {

		log.debug("getPurposeUpdateElementUpdateElement()");
		if (xpurposes.changed) {
			MaintainableLabelUpdateElement updPurpose = new MaintainableLabelUpdateElement();

			updPurpose.setLocalName("Purpose");
			updPurpose.setCrudValue(xpurposes.getCrudValue()); // Update
			updPurpose.setValue(xpurposes.getModifiedXmlObject().xmlText());
			return updPurpose;
		}
		return null;
	}
	
	/**
	 * Get Study Unit Query Result.
	 * 
	 * @return MaintainableLabelQueryResult
	 */
	public MaintainableLabelQueryResult getStudyUnitQueryResult() {
		return studyUnitQueryResult;
	}
	
	/**
	 * Set Study Unit Query Result.
	 * 
	 *  @param studyUnitQueryResult
	 */
	public void setStudyUnitQueryResult(MaintainableLabelQueryResult studyUnitQueryResult) {
		this.studyUnitQueryResult = studyUnitQueryResult;
	}
	

	/**
	 * Return list of UpdateElements
	 * 
	 * @return List<MaintainableLabelUpdateElement>
	 */
	public List<MaintainableLabelUpdateElement> getUpdateElements() {
		List<MaintainableLabelUpdateElement> elements = new ArrayList<MaintainableLabelUpdateElement>();

		log.debug("StudyUnit.getUpdateElements()");
		MaintainableLabelUpdateElement element;
		element = getNameUpdateElement();
		if (element != null) {
			elements.add(element);
		}
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
		
	/**
	 * Validates the Study Unit before it is saved. It e.g. checks if all
	 * mandatory attributes has been given.
	 * 
	 * @throws Exception
	 */
	public void validate() throws Exception {
		log.debug("StudyUnit validation performed");

		return; // No error found
	}


}
