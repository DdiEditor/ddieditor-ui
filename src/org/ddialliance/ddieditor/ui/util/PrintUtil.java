package org.ddialliance.ddieditor.ui.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

public class PrintUtil {
	boolean showNumVarStatistic = false;
	boolean universerefOnVariables = false;
	boolean addNaviagtionBar = false;

	public Transformer getTransformer(boolean showNumVarStatistic,
			boolean universerefOnVariables, boolean addNaviagtionBar)
			throws Exception {
		this.showNumVarStatistic = showNumVarStatistic;
		this.universerefOnVariables = universerefOnVariables;
		this.addNaviagtionBar = addNaviagtionBar;
		return getTransformer();
	}

	public Transformer getTransformer() throws Exception {
		Transformer transformer = null;
		// protocol errors see:
		// https://forums.oracle.com/forums/thread.jspa?messageID=9456878
		System.setProperty("javax.xml.transform.TransformerFactory",
				"net.sf.saxon.TransformerFactoryImpl");

		InputStream xslInput = PrintUtil.this.getClass().getClassLoader()
				.getResourceAsStream("resources/ddixslt/ddaddi3_1.xsl");
		StreamSource source = new StreamSource(xslInput);
		// "file://resources/ddixslt/ddaddi3_1.xsl");
		
		source.setSystemId(new File("resources/ddixslt/ddaddi3_1.xsl").toURI()
				.toURL().toString());

		TransformerFactory tFactory = TransformerFactory.newInstance();
		transformer = tFactory.newTransformer(source);

		// svn revision
		// Date date = new Date(System.currentTimeMillis());
		// transformer.setParameter("svn-revision",
		// "Date: " + Translator.format(date));
		// + ", DdiEditor-UI Version: "
		// + Platform.getBundle("ddieditor-ui").getHeaders()
		// .get("Bundle-Version")

		// render text-elements of
		// this language
		transformer.setParameter("lang", LanguageUtil.getDisplayLanguage());

		// if the requested language
		// is not found for e.g.
		// questionText, use
		// fallback language
		transformer.setParameter("fallback-lang",
				LanguageUtil.getOriginalLanguage());

		// <!--xsl:param
		// name="translations">i18n/messages_da.properties.xml</xsl:param-->
		transformer.setParameter("translations", "i18n/messages_"
				+ LanguageUtil.getDisplayLanguage() + ".properties.xml");

		// render all html-elements
		// or just the content of
		// body
		transformer.setParameter("render-as-document", "true");

		// include interactive js
		// and jquery for navigation
		// (external links to eXist)
		transformer.setParameter("include-js", 1);

		// print anchors for eg
		// QuestionItems
		transformer.setParameter("print-anchor", 1);

		// show the title (and
		// subtitle, creator, etc.)
		// of the study
		transformer.setParameter("show-study-title", 1);

		// show study-information
		transformer.setParameter("show-study-information", 1);

		// show study-information
		transformer.setParameter("show-kind-of-data", 0);

		// show citaion as part study-information
		transformer.setParameter("show-citation", 0);

		// show the abstract as part
		// of study-information
		transformer.setParameter("show-abstract", 0);

		// show the coverage as part
		// of study-information
		transformer.setParameter("show-coverage", 0);

		// show the questions as a
		// separate flow from the
		// variables
		transformer.setParameter("show-questionnaires", 0);

		// show variable
		// navigation-bar
		transformer.setParameter("show-navigration-bar", addNaviagtionBar ? 1
				: 0);

		// show inline variable toc
		transformer.setParameter("show-inline-toc", addNaviagtionBar ? 0 : 1);

		// path prefix to the
		// css-files
		transformer.setParameter("theme-path", "theme/default");

		// path prefix (used for
		// css, js when rendered on
		// the web)
		transformer.setParameter("path-prefix", ".");

		// show numeric freq
		transformer.setParameter("show-numeric-var-frequence",
				showNumVarStatistic ? 1 : 0);

		// print universe on
		// variable
		transformer.setParameter("show-universe", universerefOnVariables ? 1
				: 0);

		return transformer;
	}
}
