package org.ddialliance.ddieditor.ui.view;

import java.util.Iterator;
import java.util.List;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlCursor.TokenType;
import org.apache.xmlbeans.XmlObject;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.persistenceaccess.maintainablelabel.MaintainableLightLabelQueryResult;
import org.ddialliance.ddieditor.ui.view.variable.questionrelation.VariableQuestionRelation;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.jface.viewers.IElementComparer;

public class XmlObjectComparer implements IElementComparer {
	static Log log = LogFactory.getLog(LogType.SYSTEM, XmlObjectComparer.class);

	@Override
	public boolean equals(Object a, Object b) {
		boolean result = false;
		// xmlobject
		if (a instanceof XmlObject && b instanceof XmlObject) {
			result = ((XmlObject) a).valueEquals((XmlObject) b);
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
		// maintainable light label query result
		else if (a instanceof MaintainableLightLabelQueryResult
				&& b instanceof MaintainableLightLabelQueryResult) {
			result = ((MaintainableLightLabelQueryResult) a).getId().equals(
					((MaintainableLightLabelQueryResult) b).getId());
			return result;
		}
		// list<?>
		else if (a instanceof List<?> && b instanceof List<?>) {
			List<?> aa = (List<?>) a;
			List<?> bb = (List<?>) b;
			if (aa.size() == bb.size()) {
				// empty list
				if (aa.isEmpty() && bb.isEmpty()) {
					return true;
				}
				// list<xmlobject>
				else if ((!(aa.isEmpty()) && (aa.get(0) instanceof XmlObject))
						&& (!(bb.isEmpty()) && (bb.get(0) instanceof XmlObject))) {
					for (Iterator aaIter = aa.iterator(), bbIter = bb
							.iterator(); aaIter.hasNext();) {
						result = equals(aaIter.next(), bbIter.next());
						if (!result) {
							return false;
						}
					}
				} // list<variable question relation>
				else if ((!(aa.isEmpty()) && (aa.get(0) instanceof VariableQuestionRelation))
						&& (!(bb.isEmpty()) && (bb.get(0) instanceof VariableQuestionRelation))) {
					// quick fix, size is equal :- )
					return true;
				}
			}
			return result;
		}

		// guard
		// if (log.isDebugEnabled()) {
		// log.debug("a: " + a.getClass().getName());
		// log.debug("b: " + b.getClass().getName());
		// log.debug("Result: " + a.equals(b));
		// }
		return a.equals(b);
	}

	private CharSequence id = "id";

	@Override
	public int hashCode(Object element) {
		// xmlobject
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
		}
		// maintainable light label query result
		// not to confuse, the hashcode method of mllqr resides in maintainable
		// light label query result

		// list<xmlobject>
		else if (element instanceof List<?>) {
			List<?> eList = (List<?>) element;
			if (!eList.isEmpty() && eList.get(0) instanceof XmlObject) {
				int hashCode = 1;
				for (Iterator iterator = eList.iterator(); iterator.hasNext();) {
					Object object = (Object) iterator.next();
					hashCode = 31 * hashCode + hashCode(object);
				}
				return hashCode;
			} else {
				return element.hashCode();
			}
		} 
		// guard
		else {
			return element.hashCode();
		}
	}
}
