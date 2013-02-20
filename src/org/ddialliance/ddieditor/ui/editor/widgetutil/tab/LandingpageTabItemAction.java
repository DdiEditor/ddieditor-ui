package org.ddialliance.ddieditor.ui.editor.widgetutil.tab;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.ui.model.IModel;
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
		} catch (Exception e) {
			throw new DDIFtpException("XML to HTML Transformer error: "
					+ e.getMessage());
		}
	}

	@Override
	public Object action() throws DDIFtpException {
		// get xml
		String xml = DdiManager
				.getInstance()
				.getDdi3NamespaceHelper()
				.substitutePrefixesFromElements(
						model.getDocument().xmlText(
								DdiManager.getInstance().getXmlOptions()));

		// transform xml to html
		StreamResult ddaMetaDataStreamResult = null;
		StreamResult streamResult = null;
		try {
			InputStream ddaMetaDataInputStream = new ByteArrayInputStream(
					xml.getBytes("UTF-8"));
			ddaMetaDataStreamResult = new StreamResult(new StringWriter());
			ddaMetaDataTrasformer.transform(new StreamSource(
					ddaMetaDataInputStream), ddaMetaDataStreamResult);

			InputStream inputStream = new ByteArrayInputStream(
					ddaMetaDataStreamResult.getWriter().toString()
							.getBytes("UTF-8"));
			streamResult = new StreamResult(new StringWriter());
			lpTransformer.transform(new StreamSource(inputStream),
					streamResult);
		} catch (TransformerException e1) {
			throw new DDIFtpException("XML to HTML transformation error: ", e1);
		} catch (UnsupportedEncodingException e) {
			throw new DDIFtpException("XML to HTML Encoding error: ", e);
		}

		browser.setText(streamResult.getWriter().toString());
		return null;
	}
}
