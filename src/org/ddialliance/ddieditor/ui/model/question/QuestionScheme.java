package org.ddialliance.ddieditor.ui.model.question;

import org.apache.xmlbeans.XmlException;
import org.ddialliance.ddieditor.persistenceaccess.maintainablelabel.MaintainableLabelQueryResult;
import org.ddialliance.ddieditor.ui.model.LabelDescriptionScheme;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;

/**
 * Question scheme model
 */
public class QuestionScheme extends LabelDescriptionScheme {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			QuestionScheme.class);

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
	public QuestionScheme(String id, String version, String parentId,
			String parentVersion, String agency,
			MaintainableLabelQueryResult maintainableLabelQueryResult)
			throws DDIFtpException {
		super(maintainableLabelQueryResult, parentId, parentVersion);
	}

	@Override
	public void validate() throws Exception {
		// not implemented
	}

	@Override
	public void executeChange(Object value, Class<?> type) throws Exception {
		// not implemented
	}
}
