package org.ddialliance.ddieditor.ui.dbxml;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlCursor.TokenType;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;

public class Util {
	
	/**
	 * Get text on a mixed content element at first position after attributes. 
	 * 'xmlns' attributes (namespace declarations) are also skipped.
	 * 
	 * @param xmlObject
	 *            to set text on
	 *            
	 * @return String value
	 */
	public static String getTextOnMixedElement(XmlObject xmlObject) {

		XmlCursor xmlCursor = xmlObject.newCursor();
		// toLastAttribute does not skip namespaces - so continue
		// until none empty TEXT token
		xmlCursor.toLastAttribute();
		TokenType token = xmlCursor.toNextToken();
		String text = xmlCursor.getTextValue().trim();
		while (!token.equals(XmlCursor.TokenType.TEXT) || 
				(token.equals(XmlCursor.TokenType.TEXT) && text.length() == 0)) {
			token = xmlCursor.toNextToken();
			text = xmlCursor.getTextValue().trim();
		}
		xmlCursor.dispose();
		return text;
	}

	/**
	 * Set text on a mixed content element at first position after attributes. 
	 * 'xmlns' attributes (namespace declarations) are also skipped.
	 * 
	 * @param xmlObject
	 *            to set text on
	 * @param string
	 * 			  text to assign
	 */
	public static void setTextOnMixedElement(XmlObject xmlObject, String string) {

		XmlCursor xmlCursor = xmlObject.newCursor();
		while(!xmlCursor.isStart()) {
			xmlCursor.toNextToken();
		}
		xmlCursor.setTextValue(string);
		xmlCursor.dispose();
	}
	
	/**
	 * Get label of Light XML Object. If no label found the Id. is return as label.
	 * @param lightXmlObject
	 * @return 
	 */
	public static String getLabel(LightXmlObjectType lightXmlObject) {
		
		if (lightXmlObject == null) {
			return null;
		}
		
		if (lightXmlObject.getLabelList().size() != 0) {
			return Util.getTextOnMixedElement(lightXmlObject.getLabelList().get(0));
		} else {
			return lightXmlObject.getId();
		}
	}

}

















