package org.ddialliance.ddieditor.ui.editor.widgetutil.tab;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;

import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
import org.ddialliance.ddieditor.persistenceaccess.PersistenceManager;
import org.ddialliance.ddieditor.ui.model.IModel;
import org.ddialliance.ddieditor.ui.model.userid.UserIdType;
import org.ddialliance.ddieditor.ui.util.LanguageUtil;
import org.ddialliance.ddieditor.ui.util.PrintUtil;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.swt.browser.Browser;

public class LandingpageTabItemAction extends TabItemAction {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			LandingpageTabItemAction.class);
	Browser browser;
	IModel model;
	Transformer ddaMetaDataTrasformer = null;
	Transformer lpTransformer = null;
	public File htmlFile = null;
	File tmpXml = null;

	public LandingpageTabItemAction(String id, IModel model, Browser browser)
			throws DDIFtpException {
		super(id);
		this.model = model;
		this.browser = browser;

		// prepare transformer
		try {
			ddaMetaDataTrasformer = new PrintUtil().getDdaMetadataTransformer();

			lpTransformer = new PrintUtil().getLandingpageTransformer();
			lpTransformer.setParameter("lang",
					LanguageUtil.getDisplayLanguage());
			lpTransformer.setParameter("host", "http://localhost");
			lpTransformer.setParameter("cvFolder", "../cv");

			tmpXml = File.createTempFile("landingpage", ".xml");
			htmlFile = File.createTempFile("landingpage", ".html");

			transform();
		} catch (Exception e) {
			throw new DDIFtpException("XML to HTML Transformer error: "
					+ e.getMessage());
		}
	}

	@Override
	public Object action() throws DDIFtpException {
		transform();
		return null;
	}

	private void transform() throws DDIFtpException {
		PersistenceManager.getInstance().exportResoure(
				PersistenceManager.getInstance().getWorkingResource(),
				tmpXml,
				org.ddialliance.ddieditor.ui.Activator.getDefault()
						.getPreferenceStore()
						.getString(UserIdType.DDI_EDITOR_VERSION.getType()));

		// transform xml to html
		StreamResult ddaMetaDataStreamResult = null;
		StreamResult streamResult = null;
		try {

			InputStream ddaMetaDataInputStream = new FileInputStream(tmpXml);
			ddaMetaDataStreamResult = new StreamResult(new StringWriter());
			ddaMetaDataTrasformer.transform(new StreamSource(
					ddaMetaDataInputStream), ddaMetaDataStreamResult);

			// log.debug(ddaMetaDataStreamResult.getWriter().toString());

			InputStream inputStream = new ByteArrayInputStream(
					ddaMetaDataStreamResult.getWriter().toString()
							.getBytes("UTF-8"));
			streamResult = new StreamResult(new StringWriter());
			lpTransformer.transform(new StreamSource(inputStream),
					new StreamResult(htmlFile.toURI().toURL().toString()));
			// lpTransformer
			// .transform(new StreamSource(inputStream), streamResult);

			FileUtils.copyFile(new File("resources/landingpagexslt/style.css"),
					new File(htmlFile.getParent() + File.separator
							+ "style.css"));
			browser.setUrl(new URL("file://" + htmlFile.getAbsolutePath())
					.toURI().toString());
		} catch (Exception e) {
			throw new DDIFtpException("XML to HTML transformation error: ", e);
		}

		// log.debug(streamResult.getWriter().toString());
	}
}
