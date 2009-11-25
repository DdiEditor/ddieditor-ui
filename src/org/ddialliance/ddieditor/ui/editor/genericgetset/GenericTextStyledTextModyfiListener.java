package org.ddialliance.ddieditor.ui.editor.genericgetset;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.xmlbeans.XmlObject;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddieditor.ui.editor.Editor.EditorStatus;
import org.ddialliance.ddieditor.ui.util.DialogUtil;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.ReflectionUtil;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.ddialliance.ddiftp.util.xml.XmlBeansUtil;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorSite;

/**
 * Generically handle modifications of SWT controls mimicking ddi xmlbeans by
 * using declarations of get and set methods or complex get and methods.
 */
public class GenericTextStyledTextModyfiListener implements ModifyListener {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			GenericTextStyledTextModyfiListener.class);
	public List<DDIFtpException> exceptions = new ArrayList<DDIFtpException>();

	XmlObject editItem;
	GenericGetSetStringValue closure;
	String getMethodName;
	String setMethodName;
	List<Object> list;
	EditorStatus editorStatus;
	IEditorSite site;
	String editorId;

	public static final String SET_TEXT_ON_MIXED_ELEMENT = "stome";
	public static final String GET_TEXT_ON_MIXED_ELEMENT = "gtome";

	/**
	 * Constructor
	 * 
	 * @param editItem
	 *            element to edit
	 * @param closure
	 *            to perform complex get and set on edit item, the closure
	 *            parameter will take precedent over the parameters
	 *            'getMethodName' and 'setMethodName'
	 * @param getMethodName
	 *            on edit item for getting string value of element
	 * @param setMethodName
	 *            on edit item for setting string value of element
	 * @param list
	 *            to add edit item to
	 * @param editorStatus
	 *            editor status
	 * @param site
	 *            i editor site
	 * @param editorId
	 *            ID of editor
	 */
	public GenericTextStyledTextModyfiListener(XmlObject editItem,
			GenericGetSetStringValue closure, String getMethodName,
			String setMethodName, List<?> list, EditorStatus editorStatus,
			IEditorSite site, String editorId) {
		this.editItem = editItem;
		this.closure = closure;
		this.getMethodName = getMethodName;
		this.setMethodName = setMethodName;
		this.list = (List<Object>) list;
		this.editorStatus = editorStatus;
		this.site = site;
		this.editorId = editorId;
	}

	@Override
	public void modifyText(ModifyEvent event) {
		// get input
		String text = null;
		boolean isNew = false;
		if (event.getSource() instanceof Text) {
			Text textTxt = ((Text) event.getSource());
			text = textTxt.getText();
			isNew = (Boolean) textTxt.getData(Editor.NEW_ITEM);
			if (isNew) {
				textTxt.setData(Editor.NEW_ITEM, false);
			}
		} else if (event.getSource() instanceof StyledText) {
			StyledText textTxt = (StyledText) event.getSource();
			text = textTxt.getText();
			isNew = (Boolean) textTxt.getData(Editor.NEW_ITEM);
			if (isNew) {
				textTxt.setData(Editor.NEW_ITEM, false);
			}
		}

		// set input on edit item
		editorStatus.setChanged();
		if (setMethodName != null
				&& setMethodName.equals(SET_TEXT_ON_MIXED_ELEMENT)) {
			XmlBeansUtil.setTextOnMixedElement(editItem, text);
		} else {
			// generic
			try {
				if (closure != null) {
					invokeClosure(closure,
							GenericGetSetStringValue.SET_STRING_METHOD_NAME,
							editItem, text);
				} else {
					ReflectionUtil.invokeMethod(editItem, setMethodName, false,
							text);
				}
			} catch (Exception e) {
				DialogUtil.errorDialog(site, editorId, e.getMessage(),
						new DDIFtpException(e));
			}
		}

		// add edit item to model
		if (isNew) {
			// add item to list
			try {
				list.add(editItem);
			} catch (Exception ex) {
				this.exceptions.add(new DDIFtpException(ex));
			}

			// recap editItem to newly added item
			// xmlbeans hack
			String testTxt = null;
			for (Object test : list) {
				// test on input
				try {
					if (closure != null) {
						Object result = invokeClosure(
								closure,
								GenericGetSetStringValue.GET_STRING_METHOD_NAME,
								test);
						testTxt = (result != null ? result.toString() : null);
					} else if (getMethodName.equals(GET_TEXT_ON_MIXED_ELEMENT)) {
						testTxt = XmlBeansUtil
								.getTextOnMixedElement((XmlObject) test);
					} else {
						testTxt = (String) ReflectionUtil.invokeMethod(test,
								getMethodName, false, null);
					}
				} catch (Exception e) {
					DialogUtil.errorDialog(site, editorId, e.getMessage(),
							new DDIFtpException(e));
				}

				if (testTxt != null && testTxt.equals(text)) {
					if (log.isDebugEnabled()) {
						log.debug("Test: " + test + "\n ~ editItem: \n"
								+ editItem);
					}
					// recap editItem to newly added item
					editItem = (XmlObject) test;
					break;
				}
			}
		}
	}

	private Object invokeClosure(Object obj, String methodName, Object... args)
			throws Exception {
		Object returnObj = null;

		// sort out params
		Class[] params = new Class[args.length];
		for (int i = 0; i < params.length; i++) {
			params[i] = Object.class;
		}

		Method m = obj.getClass().getMethod(methodName, params);

		// execute method
		returnObj = m.invoke(obj, args);

		if (log.isDebugEnabled()) {
			log.debug("Invoke method: " + methodName + " on object type: "
					+ obj.getClass().getName() + " result: " + returnObj);
		}
		return returnObj;
	}
}
