<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:r="ddi:reusable:3_1"
	xmlns:xhtml="http://www.w3.org/1999/xhtml"
	xmlns:dce="ddi:dcelements:3_1"
	xmlns:dc="http://purl.org/dc/elements/1.1/"
	xmlns:a="ddi:archive:3_1"
	xmlns:o="ddi:organizations:3_1"
	xmlns:g="ddi:group:3_1"
	xmlns:cm="ddi:comparative:3_1"
	xmlns:c="ddi:conceptualcomponent:3_1"
	xmlns:d="ddi:datacollection:3_1"
	xmlns:l="ddi:logicalproduct:3_1"
	xmlns:pd="ddi:physicaldataproduct:3_1"
	xmlns:ds="ddi:dataset:3_1"
	xmlns:pi="ddi:physicalinstance:3_1"
	xmlns:m1="ddi:physicaldataproduct/ncube/normal:3_1"
	xmlns:m2="ddi:physicaldataproduct/ncube/tabular:3_1"
	xmlns:m3="ddi:physicaldataproduct/ncube/inline:3_1"
	xmlns:s="ddi:studyunit:3_1"
	xmlns:pr="ddi:profile:3_1"
	xmlns:ddi="ddi:instance:3_1"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="ddi:instance:3_1 http://www.ddialliance.org/sites/default/files/schema/ddi3.1/instance.xsd">

	<xsl:param name="lang">da</xsl:param>
		
	<xsl:template match="/ddi:DDIInstance">
		<html>
			<head>
				<title><xsl:value-of select="s:StudyUnit/r:Citation/r:Title[@xml:lang=$lang]"/></title>
				<meta charset="utf-8" />
				<link type="text/css" rel="stylesheet" media="all" href="ddi.css" />
			</head>
			<body>
				<h1><xsl:value-of select="s:StudyUnit/r:Citation/r:Title[@xml:lang=$lang]"/></h1>
				<strong><xsl:value-of select="s:StudyUnit/r:Citation/r:AlternateTitle[@xml:lang=$lang]"/></strong>
				
				<h2><xsl:value-of select="s:StudyUnit/@id"/></h2>
				
				<h3>Abstract</h3>
				<p><xsl:value-of select="s:StudyUnit/s:Abstract/r:Content[@xml:lang=$lang]"/></p>

				<xsl:apply-templates select="s:StudyUnit/r:Citation" />

				<h3>Coverage</h3>
				<xsl:for-each select="s:StudyUnit/r:Coverage/r:TemporalCoverage">
					<p><xsl:value-of select="r:ReferenceDate/r:StartDate"/> - <xsl:value-of select="r:ReferenceDate/r:EndDate"/></p>
			    </xsl:for-each>
				
				<h3>Universe</h3>
				<xsl:for-each select="s:StudyUnit/c:ConceptualComponent/c:UniverseScheme/c:Universe">
					<p><xsl:value-of select="c:HumanReadable[@xml:lang=$lang]"/></p>
			    </xsl:for-each>
			    
			    <xsl:apply-templates select="s:StudyUnit/r:SeriesStatement" />
			    
			    <hr />
			   
			    <h3>Questions</h3>
			    <xsl:apply-templates select="s:StudyUnit/d:DataCollection/d:QuestionScheme" />

			</body>
		</html>	
	</xsl:template>
	
	<xsl:template match="r:Citation">
		<h3>Creator</h3>
		<xsl:for-each select="r:Creator[@xml:lang=$lang]">
			<p><xsl:value-of select="."/>, <em><xsl:value-of select="@affiliation"/></em></p>
	    </xsl:for-each>
	</xsl:template>
	
	<xsl:template match="r:SeriesStatement">
		<h3>Series</h3>
		<strong>Name: </strong><xsl:value-of select="r:SeriesName[@xml:lang=$lang]"/><br />
		<xsl:value-of select="r:SeriesDescription[@xml:lang=$lang]"/>
	</xsl:template>
	
	<xsl:template match="c:UniverseScheme">
		<ul class="questions">
			<strong>Label: </strong><xsl:value-of select="r:Label[@xml:lang=$lang]"/><br />
		</ul>
	</xsl:template>
	
	<xsl:template match="d:QuestionScheme">
		<xsl:variable name="x" select="3"/>
		<strong>Display language: <xsl:value-of select="$lang" /></strong>
    	<div class="question-scheme">
    		<xsl:value-of select="r:Label[@xml:lang=$lang]"/><br />
    		<xsl:value-of select="r:Description[@xml:lang=$lang]"/><br /> 		
	    	<ul class="questions">
	    		<xsl:apply-templates select="d:SubQuestions" />
	    	</ul>
    	</div>
	</xsl:template>

	<xsl:template match="d:QuestionItem">
    	<li class="question">
	    	<strong class="questionName" style="width:50px"><xsl:value-of select="d:QuestionItemName[@xml:lang=$lang]"/></strong>
	    	<xsl:value-of select="d:QuestionText[@xml:lang=$lang]/d:LiteralText/d:Text"/>
    	</li>
	</xsl:template>

	<xsl:template match="d:MultipleQuestionItem">
    	<li class="question">
	    	<strong class="questionName" style="width:50px"><xsl:value-of select="d:MultipleQuestionItemName[@xml:lang=$lang]"/></strong>
	    	<xsl:value-of select="d:QuestionText[@xml:lang=$lang]/d:LiteralText/d:Text"/>
	    	<ul class="questions">
			    	<xsl:apply-templates select="d:SubQuestions" />
	    	</ul>
    	</li>
	</xsl:template>	

	<xsl:template match="d:SubQuestions">
    	<ul class="questions">
	    	<xsl:for-each select="child::*">
		    	<li><xsl:apply-templates select="." /></li>
	    	</xsl:for-each>
    	</ul>
	</xsl:template>	
	
</xsl:stylesheet>