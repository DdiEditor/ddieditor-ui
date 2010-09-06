package org.ddialliance.ddieditor.ui.model.universe;

import org.apache.xmlbeans.XmlException;
import org.ddialliance.ddieditor.persistenceaccess.maintainablelabel.MaintainableLabelQueryResult;
import org.ddialliance.ddieditor.ui.model.LabelDescriptionScheme;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;

/**
 * Universe scheme model
 */
public class UniverseScheme extends LabelDescriptionScheme {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			UniverseScheme.class);

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
		super(maintainableLabelQueryResult, parentId, parentVersion);
	}

	@Override
	public void executeChange(Object value, Class<?> type) throws Exception {
		// not implemented
	}
}
