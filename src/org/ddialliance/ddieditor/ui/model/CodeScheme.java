package org.ddialliance.ddieditor.ui.model;

import org.ddialliance.ddi_3_0.xml.xmlbeans.logicalproduct.impl.CodeSchemeTypeImpl;
import org.ddialliance.ddieditor.ui.dbxml.Util;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;

public class CodeScheme {

	/**
	 * 
	 * CodeScheme is a subclass of ConseptTypeImpl (Interface) - but is only
	 * implementing used methods.
	 * 
	 * @author dak
	 * 
	 */

	private static Log log = LogFactory.getLog(LogType.SYSTEM, CodeScheme.class);

	private CodeSchemeTypeImpl CodeSchemeTypeImpl;

	public CodeScheme(CodeSchemeTypeImpl CodeSchemeTypeImpl) {
		this.CodeSchemeTypeImpl = CodeSchemeTypeImpl;
	}

	/**
	 * Return CodeScheme Label
	 * 
	 * @return String Label
	 */
	public String getFirstLabel() {

		if (CodeSchemeTypeImpl.getLabelList().size() > 0) {
			String result = Util.getTextOnMixedElement(CodeSchemeTypeImpl.getLabelList().get(0));
			log.debug("CodeScheme Label: " + result);
			return result;
		} else {
			log.debug("CodeScheme - Id. returned as label: " + CodeSchemeTypeImpl.getId());
			return CodeSchemeTypeImpl.getId();
		}
	}

}
