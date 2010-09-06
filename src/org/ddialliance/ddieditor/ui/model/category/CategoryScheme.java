package org.ddialliance.ddieditor.ui.model.category;

import org.ddialliance.ddieditor.persistenceaccess.maintainablelabel.MaintainableLabelQueryResult;
import org.ddialliance.ddieditor.ui.model.LabelDescriptionScheme;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;

/**
 * Category scheme model
 */
public class CategoryScheme extends LabelDescriptionScheme {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			CategoryScheme.class);

	/**
	 * Constructor
	 * 
	 * @param maintainableLabelQueryResult
	 * @param parentId
	 * @param parentVersion
	 * @throws DDIFtpException
	 */
	public CategoryScheme(String id, String version, String parentId,
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
