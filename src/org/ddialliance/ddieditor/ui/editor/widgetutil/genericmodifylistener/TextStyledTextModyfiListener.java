package org.ddialliance.ddieditor.ui.editor.widgetutil.genericmodifylistener;

import java.util.List;

import org.apache.xmlbeans.XmlObject;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddieditor.ui.editor.Editor.EditorStatus;
import org.ddialliance.ddieditor.ui.editor.widgetutil.GenericGetSet;
import org.ddialliance.ddieditor.ui.editor.widgetutil.GenericGetSetClosure;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorSite;

/**
 * Generically handle modifications of SWT controls mimicking ddi xmlbeans by
 * using declarations of get and set methods or complex get and methods.
 */
public class TextStyledTextModyfiListener extends GenericGetSet
		implements ModifyListener {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			TextStyledTextModyfiListener.class);

	/**
	 * Constructor
	 * 
	 * @see GenericGetSet#GenericGetSet(XmlObject, GenericGetSetClosure, String, String, List, EditorStatus, IEditorSite, String)
	 */
	public TextStyledTextModyfiListener(XmlObject editItem,
			GenericGetSetClosure closure, String getMethodName,
			String setMethodName, List<?> list, EditorStatus editorStatus,
			IEditorSite site, String editorId) {
		super(editItem, closure, getMethodName, setMethodName, list,
				editorStatus, site, editorId);
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
		this.set(text, isNew);
	}
}
