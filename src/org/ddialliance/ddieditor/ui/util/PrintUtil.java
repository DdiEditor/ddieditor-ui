package org.ddialliance.ddieditor.ui.util;

import java.io.File;
import java.io.InputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.ddialliance.ddieditor.util.DdiEditorConfig;

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

		transformer.setParameter("translations", "i18n/messages_"
				+ LanguageUtil.getDisplayLanguage() + ".properties.xml");

		// render all html-elements
		// or just the content of
		// body
		transformer.setParameter("render-as-document", "true");

		// include interactive js
		// and jquery for navigation
		// (external links to eXist)
		transformer.setParameter("include-js", "true");

		// print anchors for eg
		// QuestionItems
		transformer.setParameter("print-anchor", "true");

		// show the title (and
		// subtitle, creator, etc.)
		// of the study
		transformer.setParameter("show-study-title", "true");

		// show study-information
		transformer.setParameter("show-study-information", "true");

		// show guidance
		transformer.setParameter("show-guidance", "true");
		transformer.setParameter("guidancelink", DdiEditorConfig
				.get(DdiEditorConfig.DDI_STYLE_SHEET_CODEBOOK_GUIDE_LINK));
		transformer.setParameter("currationprocesslink", DdiEditorConfig
				.get(DdiEditorConfig.DDI_STYLE_SHEET_CURATION_PROCESS_LINK));

		// show study-information
		transformer.setParameter("show-kind-of-data", "false");

		// show citaion as part study-information
		transformer.setParameter("show-citation", "false");

		// show the abstract as part
		// of study-information
		transformer.setParameter("show-abstract", "false");

		// show the coverage as part
		// of study-information
		transformer.setParameter("show-coverage", "false");

		// show the questions as a
		// separate flow from the
		// variables
		transformer.setParameter("show-questionnaires", "false");

		// show variable
		// navigation-bar
		transformer.setParameter("show-navigration-bar",
				addNaviagtionBar ? "true" : "false");

		// show inline variable toc
		transformer.setParameter("show-variable-list",
				addNaviagtionBar ? "false" : "true");

		// show inline document toc
		transformer.setParameter("show-toc", addNaviagtionBar ? "false"
				: "true");

		// show category statistics
		transformer.setParameter("show-category-statistics", "true");

		// path prefix to the
		// css-files
		transformer.setParameter("theme-path", "theme/default");

		// path prefix (used for
		// css, js when rendered on
		// the web)
		transformer.setParameter("path-prefix", ".");

		// show numeric freq
		transformer.setParameter("show-numeric-var-frequence",
				showNumVarStatistic ? "true" : "false");

		// print universe on
		// variable
		transformer.setParameter("show-universe",
				universerefOnVariables ? "true" : "false");

		return transformer;
	}
}
