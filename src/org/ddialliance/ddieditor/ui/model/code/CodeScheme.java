package org.ddialliance.ddieditor.ui.model.code;

/**
 * Code Scheme model.
 * 
 */
/*
 * $Author$ 
 * $Date$ 
 * $Revision$
 */

import org.ddialliance.ddieditor.persistenceaccess.maintainablelabel.MaintainableLabelQueryResult;
import org.ddialliance.ddieditor.ui.model.LabelDescriptionScheme;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;

public class CodeScheme extends LabelDescriptionScheme {
	private static Log log = LogFactory
			.getLog(LogType.SYSTEM, CodeScheme.class);

	private MaintainableLabelQueryResult maintainableLabelQueryResult;

	/**
	 * Constructor
	 * 
	 * @param id
	 * @param version
	 * @param parentId
	 * @param parentVersion
	 * @param maintainableLabelQueryResult
	 * @throws DDIFtpException
	 */
	public CodeScheme(String id, String version, String parentId,
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
		// not impemented
	}
}
