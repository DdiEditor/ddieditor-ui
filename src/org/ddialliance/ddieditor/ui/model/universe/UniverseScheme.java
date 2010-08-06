package org.ddialliance.ddieditor.ui.model.universe;

import java.util.List;

import org.apache.xmlbeans.XmlObject;
import org.ddialliance.ddi3.xml.xmlbeans.conceptualcomponent.UniverseSchemeDocument;
import org.ddialliance.ddi3.xml.xmlbeans.conceptualcomponent.impl.UniverseSchemeTypeImpl;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.LabelType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.StructuredStringType;
import org.ddialliance.ddieditor.ui.model.LabelDescription;
import org.ddialliance.ddieditor.ui.model.Language;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;

public class UniverseScheme extends LabelDescription {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, UniverseScheme.class);

	private UniverseSchemeDocument universeSchemeDocument;
	private UniverseSchemeTypeImpl universeSchemeTypeImpl;

	/**
	 * Constructor of Universe Scheme
	 * 
	 * @param universeSchemeDocument
	 * @param parentId
	 * @param parentVersion
	 * @throws Exception
	 */
	public UniverseScheme(UniverseSchemeDocument universeSchemeDocument, String parentId, String parentVersion)
			throws Exception {

		super(universeSchemeDocument.getUniverseScheme().getId(), universeSchemeDocument.getUniverseScheme()
				.getVersion(), parentId, parentVersion, universeSchemeDocument.getUniverseScheme().getLabelList(),
				universeSchemeDocument.getUniverseScheme().getDescriptionList());

		if (universeSchemeDocument == null) {
			// TODO Create new UniverseScheme
			this.universeSchemeDocument = null;
		}
		this.universeSchemeDocument = universeSchemeDocument;
		this.universeSchemeTypeImpl = (UniverseSchemeTypeImpl) universeSchemeDocument.getUniverseScheme();
	}

	/**
	 * Get Universe Scheme Document of Universe Scheme.
	 * 
	 * @return UniverseSchemeDocument
	 */
	public UniverseSchemeDocument getUniverseSchemeDocument() {
		return universeSchemeDocument;
	}

	/**
	 * Set Display Label of Universe Scheme.
	 * 
	 * @param string
	 * @return LabelType (always null)
	 */
	@Override
	public LabelType setDisplayLabel(String string) {

		LabelType labelType = super.setDisplayLabel(string);
		if (labelType != null) {
			universeSchemeTypeImpl.getLabelList().add(labelType);
		}
		return null;
	}

	/**
	 * Set Display Description of Universe Scheme.
	 * 
	 * @param string
	 * @return StructuredStringType (always null)
	 */
	public StructuredStringType setDisplayDescr(String string) {
		StructuredStringType descriptionType = super.setDisplayDescr(string);
		if (descriptionType != null) {
			universeSchemeTypeImpl.getDescriptionList().add(descriptionType);
		}
		return null;
	}

	@Override
	public XmlObject getDocument() throws DDIFtpException {
		return universeSchemeDocument;
	}

	@Override
	public void executeChange(Object value, Class<?> type) throws Exception {
		// TODO Auto-generated method stub

	}
}
