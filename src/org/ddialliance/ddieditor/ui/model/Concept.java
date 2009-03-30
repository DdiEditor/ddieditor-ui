package org.ddialliance.ddieditor.ui.model;

import org.ddialliance.ddi_3_0.xml.xmlbeans.conceptualcomponent.impl.ConceptTypeImpl;
import org.ddialliance.ddieditor.ui.dbxml.Util;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;

/**
 * 
 * Concept is a subclass of ConseptTypeImpl (Interface) - but is only implementing used
 * methods.
 * 
 * @author dak
 *
 */

public class Concept {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, Concept.class);

	private ConceptTypeImpl conceptTypeImpl;

	public Concept(ConceptTypeImpl conceptTypeImpl) {
		this.conceptTypeImpl = conceptTypeImpl;
	}

	/**
	 * Return Concept Label
	 * 
	 * @return String 	Label
	 */
	public String getFirstLabel() {
		
		String result = Util.getTextOnMixedElement(conceptTypeImpl.getLabelList().get(0));
		log.info("conceptCombo: " + result);
		return result;
	}

}
