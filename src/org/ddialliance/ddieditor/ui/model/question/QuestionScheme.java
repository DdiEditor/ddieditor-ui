package org.ddialliance.ddieditor.ui.model.question;

import org.apache.xmlbeans.XmlException;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.QuestionSchemeDocument;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.QuestionSchemeType;
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
	public QuestionScheme(String id, String version, String parentId,
			String parentVersion, String agency,
			MaintainableLabelQueryResult maintainableLabelQueryResult)
			throws DDIFtpException {

		super(id, version, parentId, parentVersion, "TODO",
				maintainableLabelQueryResult);
		this.maintainableLabelQueryResult = maintainableLabelQueryResult;
	}

	/**
	 * Validates the Question Scheme before it is saved. It e.g. checks if all
	 * mandatory attributes has been given.
	 * 
	 * @return boolean - true if no error
	 * @throws Exception
	 */
	public void validate() throws Exception {
		log.debug("Question Scheme validation performed");

		// No error found:
		return;
	}

	/**
	 * Provides the Question Scheme Document.
	 */
	@Override
	public QuestionSchemeDocument getDocument() throws DDIFtpException {

		QuestionSchemeDocument doc = QuestionSchemeDocument.Factory
				.newInstance();
		QuestionSchemeType type = doc.addNewQuestionScheme();

		super.getDocument(maintainableLabelQueryResult, type);

		type.setLabelArray(super.getLabelsAsArray());
		type.setDescriptionArray(super.getDescrsAsArray());
		return doc;
	}

	@Override
	public void executeChange(Object value, Class<?> type) throws Exception {
		// TODO Auto-generated method stub

	}
}
