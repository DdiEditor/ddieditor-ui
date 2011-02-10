package org.ddialliance.ddieditor.ui.editor.widgetutil.tab;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.ui.model.IModel;
import org.ddialliance.ddieditor.ui.util.LanguageUtil;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.swt.browser.Browser;

public class PreviewTabItemAction extends TabItemAction {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			PreviewTabItemAction.class);
	Browser browser;
	IModel model;
	Transformer transformer = null;

	public PreviewTabItemAction(String id, IModel model, Browser browser)
			throws DDIFtpException {
		super(id);
		this.model = model;
		this.browser = browser;

		// prepare transformer:
		TransformerFactory tFactory = TransformerFactory.newInstance();
		try {
			this.transformer = tFactory.newTransformer(new StreamSource(
					"resources" + File.separator + "ddi3_1.xsl"));
		} catch (TransformerConfigurationException e1) {
			throw new DDIFtpException("XML to HTML Transformer error: "+ e1.getMessage());
		}
	}

	@Override
	public Object action() throws DDIFtpException {
		// get xml
		String xml = DdiManager.getInstance().getDdi3NamespaceHelper()
				.substitutePrefixesFromElements(model.getDocument().xmlText());

		// set display language:
		transformer.setParameter("lang", LanguageUtil.getDisplayLanguage());

		// transform xml to html:
		StreamResult streamResult = null;
		try {
			InputStream inputStream = new ByteArrayInputStream(
					xml.getBytes("UTF-8"));
			streamResult = new StreamResult(new StringWriter());
			transformer.transform(new StreamSource(inputStream), streamResult);
		} catch (TransformerException e1) {
			throw new DDIFtpException("XML to HTML transformation error: ", e1);
		} catch (UnsupportedEncodingException e) {
			throw new DDIFtpException("XML to HTML Encoding error: ", e);
		}

		browser.setText(streamResult.getWriter().toString());
		return null;
	}
}
