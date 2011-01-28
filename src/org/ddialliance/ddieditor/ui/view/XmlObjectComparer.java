package org.ddialliance.ddieditor.ui.view;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlCursor.TokenType;
import org.apache.xmlbeans.XmlObject;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.eclipse.jface.viewers.IElementComparer;

public class XmlObjectComparer implements IElementComparer {
	// static Log log = LogFactory.getLog(LogType.SYSTEM,
	// XmlObjectComparer.class);

	@Override
	public boolean equals(Object a, Object b) {
		if (a instanceof XmlObject && b instanceof XmlObject) {
			boolean result = ((XmlObject) a).valueEquals((XmlObject) b);
			// if (log.isDebugEnabled() && result) {
			// log.debug(result + "\n" + a + "\n" + b);
			// }
			if (a instanceof LightXmlObjectType
					&& b instanceof LightXmlObjectType) {
				return result
						&& ((LightXmlObjectType) a).getId().equals(
								((LightXmlObjectType) b).getId());
			}
			return result;
		}
		return a.equals(b);
	}

	private CharSequence id = "id";

	@Override
	public int hashCode(Object element) {
		if (element instanceof XmlObject) {
			int key = 0;
			XmlCursor xmlCursor = ((XmlObject) element).newCursor();
			while (key == 0 && !xmlCursor.toNextToken().isNone()) {
				// log.debug("next " +
				// xmlCursor.currentTokenType().toString());
				if (xmlCursor.currentTokenType().equals(TokenType.ATTR)
						&& xmlCursor.getName().getLocalPart().equals(id)) {
					// log.debug("attr "
					// + xmlCursor.getName().getLocalPart());

					key = xmlCursor.getTextValue().hashCode();
					// log.debug("Key: " + key);
				}
			}
			xmlCursor.dispose();
			return key;
		} else {
			return element.hashCode();
		}
	}
}
