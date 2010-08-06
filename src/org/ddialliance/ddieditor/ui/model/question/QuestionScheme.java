package org.ddialliance.ddieditor.ui.model.question;

/**
 * Question Scheme model.
 * 
 */
import org.apache.xmlbeans.XmlObject;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.QuestionSchemeDocument;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.impl.QuestionSchemeTypeImpl;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.LabelType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.StructuredStringType;
import org.ddialliance.ddieditor.ui.model.LabelDescription;
import org.ddialliance.ddieditor.ui.model.Language;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;

public class QuestionScheme extends LabelDescription {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			QuestionScheme.class);

	private QuestionSchemeDocument questionSchemeDocument;
	private QuestionSchemeTypeImpl questionSchemeTypeImpl;

	/**
	 * Constructor
	 * 
	 * @param questionSchemeDocument
	 * @param parentId
	 * @param parentVersion
	 * @throws Exception
	 */
	public QuestionScheme(QuestionSchemeDocument questionSchemeDocument,
			String parentId, String parentVersion) throws Exception {
		super(questionSchemeDocument.getQuestionScheme().getId(),
				questionSchemeDocument.getQuestionScheme().getVersion(),
				parentId, parentVersion, questionSchemeDocument
						.getQuestionScheme().getLabelList(),
				questionSchemeDocument.getQuestionScheme().getDescriptionList());

		if (questionSchemeDocument == null) {
			// TODO Create new Question Scheme
			this.questionSchemeDocument = null;
		}
		this.questionSchemeDocument = questionSchemeDocument;
		this.questionSchemeTypeImpl = (QuestionSchemeTypeImpl) questionSchemeDocument
				.getQuestionScheme();
	}

	/**
	 * Set Display Label of Question Scheme.
	 * 
	 * @param string
	 * @return LabelType
	 */
	public LabelType setDisplayLabel(String string) {

		LabelType labelType = super.setDisplayLabel(string);
		if (labelType != null) {
			questionSchemeTypeImpl.getLabelList().add(labelType);
		}
		return null;
	}

	/**
	 * Set Display Description of Question Scheme.
	 * 
	 * @param string
	 * @return StructuredStringType
	 */
	public StructuredStringType setDisplayDescr(String string) {
		StructuredStringType descriptionType = super.setDisplayDescr(string);
		if (descriptionType != null) {
			questionSchemeTypeImpl.getDescriptionList().add(descriptionType);
		}
		return null;
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

	@Override
	public XmlObject getDocument() throws DDIFtpException {
		return questionSchemeDocument;
	}

	@Override
	public void executeChange(Object value, Class<?> type) throws Exception {
		// TODO Auto-generated method stub

	}
}
