package org.ddialliance.ddieditor.ui.util;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.ddialliance.ddieditor.util.DdiEditorConfig;

public class PrintUtil {
	final String codebookXsltLocation = "resources/ddixslt/ddaddi3_1.xsl";
	final String ddiLToDdiDLocation = "resources/ddixslt-lc/ddi3_1_to_ddi1_2_2.xsl";
	final String ddiCToFoLocation = "resources/ddixslt-cpdf/dditofo_complete.xsl";
	boolean showNumVarStatistic = false;
	boolean universerefOnVariables = false;
	boolean addNaviagtionBar = false;
	boolean suppressGraphics = false;

	final String ddaMetaDataXsltLocation = "resources/landingpagexslt/DdiStudyUnit_To_DdaMetadata.xsl";
	final String landingPageXsltLocation = "resources/landingpagexslt/lp-min.xsl";
	final String ddilToMarcLocation = "resources/ddimarcxml/ddi_3_1-marcxml.xsl";
	final String marcToEnglishHtmlLocation = "resources/ddimarcxml/MARC21slim2English.xsl";
	final String marcToSpecHtmlLocation = "resources/ddimarcxml/MARC21slim2HTML.xsl";
	final String marcToValidationXmlLocation = "resources/ddimarcxml/validate.xsl";
	final String lineXsltLocation = "resources/linexslt/nbr-var-codes.xsl";

	protected Transformer getTransformer(Source source) throws Exception {
		Transformer transformer = null;
		// protocol errors see:
		// https://forums.oracle.com/forums/thread.jspa?messageID=9456878
		System.setProperty("javax.xml.transform.TransformerFactory",
				"net.sf.saxon.TransformerFactoryImpl");

		TransformerFactory tFactory = TransformerFactory.newInstance();
		transformer = tFactory.newTransformer(source);

		return transformer;
	}	

	protected StreamSource getXsltSource(String pathLocation) throws Exception {
		InputStream xslInput = PrintUtil.this.getClass().getClassLoader()
				.getResourceAsStream(pathLocation);

		StreamSource source = new StreamSource(xslInput);
		source.setSystemId(new File(pathLocation).toURI().toURL().toString());

		return source;
	}

	public static void doTranformation(Transformer transformer, File sourceFile, File outFile)
			throws MalformedURLException, TransformerException {
		transformer.transform(new StreamSource(sourceFile.toURI().toURL()
				.toString()), new StreamResult(outFile.toURI().toURL()
				.toString()));
	}

	public Transformer getCodebookTransformer(boolean showNumVarStatistic,
			boolean universerefOnVariables, boolean addNaviagtionBar, boolean suppressGraphics)
			throws Exception {
		this.showNumVarStatistic = showNumVarStatistic;
		this.universerefOnVariables = universerefOnVariables;
		this.addNaviagtionBar = addNaviagtionBar;
		this.suppressGraphics = suppressGraphics;
		return getCodebookTransformer();
	}

	public Transformer getCodebookTransformer() throws Exception {
		Source codeBookXslt = getXsltSource(codebookXsltLocation);
		Transformer transformer = getTransformer(codeBookXslt);

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

		// show other material on study unit
		transformer.setParameter("show-otherdocuments", "false");

		// show variable
		// navigation-bar
		transformer.setParameter("show-navigration-bar",
				addNaviagtionBar ? "true" : "false");

		// suppress graphics for statistics
		transformer.setParameter("include-js",
				suppressGraphics ? "false" : "true");

		// show inline variable toc
		transformer.setParameter("show-variable-list",
				addNaviagtionBar ? "false" : "true");

		// show inline document toc
		transformer.setParameter("show-toc", addNaviagtionBar ? "false"
				: "true");

		// show category statistics
		transformer.setParameter("show-category-statistics", "true");

		// show instrumentation
		transformer.setParameter("show-instrumentation", "false");

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

	public Transformer getDdaMetadataTransformer() throws Exception {
		Source xslt = getXsltSource(ddaMetaDataXsltLocation);
		Transformer transformer = getTransformer(xslt);
		return transformer;
	}

	public Transformer getLandingpageTransformer() throws Exception {
		Source xslt = getXsltSource(landingPageXsltLocation);
		Transformer transformer = getTransformer(xslt);
		return transformer;
	}

	public Transformer getDdilToMarcTransformer() throws Exception {
		Source xslt = getXsltSource(ddilToMarcLocation);
		Transformer transformer = getTransformer(xslt);
		return transformer;
	}

	public Transformer getMarcToEnglishHtmlTransformer() throws Exception {
		Source xslt = getXsltSource(marcToEnglishHtmlLocation);
		Transformer transformer = getTransformer(xslt);
		return transformer;
	}

	public Transformer getMarcToSpecTransformer() throws Exception {
		Source xslt = getXsltSource(marcToSpecHtmlLocation);
		Transformer transformer = getTransformer(xslt);
		return transformer;
	}

	public Transformer getMarcToValidationXmlTransformer() throws Exception {
		Source xslt = getXsltSource(marcToValidationXmlLocation);
		Transformer transformer = getTransformer(xslt);
		return transformer;
	}

	public Transformer getLineNbrVarCodesXmlTransformer() throws Exception {
		Source xslt = getXsltSource(lineXsltLocation);
		Transformer transformer = getTransformer(xslt);
		return transformer;
	}
}
