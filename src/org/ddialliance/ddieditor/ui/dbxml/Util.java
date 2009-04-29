package org.ddialliance.ddieditor.ui.dbxml;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlCursor.TokenType;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;

public class Util {
	
	/**
	 * Get text on a mixed content element at first position after attributs. 
	 * 'xmlns' attributes (namespace declarations) are alse skipped.
	 * 
	 * @param xmlObject
	 *            to set text on
	 *            
	 * @return String value
	 */
	public static String getTextOnMixedElement(XmlObject xmlObject) {

		XmlCursor xmlCursor = xmlObject.newCursor();
		xmlCursor.toLastAttribute();
		System.out.println(xmlObject);
		// toLastAttribute does not skip namespaces - so continue
		// until none empty TEXT token
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

















