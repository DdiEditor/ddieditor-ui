package org.ddialliance.ddieditor.ui.model.universe;

import org.apache.xmlbeans.XmlException;
import org.ddialliance.ddi3.xml.xmlbeans.conceptualcomponent.UniverseSchemeDocument;
import org.ddialliance.ddi3.xml.xmlbeans.conceptualcomponent.UniverseSchemeType;
import org.ddialliance.ddieditor.persistenceaccess.maintainablelabel.MaintainableLabelQueryResult;
import org.ddialliance.ddieditor.ui.model.LabelDescription;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;

public class UniverseScheme extends LabelDescription {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, UniverseScheme.class);

	private MaintainableLabelQueryResult maintainableLabelQueryResult;

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
	public UniverseScheme(String id, String version, String parentId,
			String parentVersion, String agency,
			MaintainableLabelQueryResult maintainableLabelQueryResult)
			throws DDIFtpException {
		
		super(id, version, parentId, parentVersion, "TODO", maintainableLabelQueryResult);
		this.maintainableLabelQueryResult = maintainableLabelQueryResult;
	}
	
	public MaintainableLabelQueryResult getMaintainableLabelQueryResult() {
		return maintainableLabelQueryResult;
	}
	
	/**
	 * Provides the Universe Scheme Document.
	 */
	@Override
	public UniverseSchemeDocument getDocument() throws DDIFtpException {

		UniverseSchemeDocument doc = UniverseSchemeDocument.Factory.newInstance();
		UniverseSchemeType type = doc.addNewUniverseScheme();

		super.getDocument(maintainableLabelQueryResult, type);
		
		type.setLabelArray(super.getLabels());
		type.setDescriptionArray(super.getDescrs());
		return doc;
	}
	
	@Override
	public void executeChange(Object value, Class<?> type) throws Exception {
		// TODO Auto-generated method stub

	}
}
