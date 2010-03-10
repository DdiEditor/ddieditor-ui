package org.ddialliance.ddieditor.ui.editor.widgetutil.genericmodifylistener;

import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddieditor.ui.editor.Editor.EditorIdentification;
import org.ddialliance.ddieditor.ui.model.IModel;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.widgets.Text;

/**
 * Generically handle modifications of SWT Widgets: Text, Styled Text 
 */
public class TextStyledTextModyfiListener extends GenericModifyListener {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			TextStyledTextModyfiListener.class);

	public TextStyledTextModyfiListener(IModel model,
			Class<?> modifyClass,
			EditorIdentification editorIdentification) {
		super(model, modifyClass, editorIdentification);
	}

	@Override
	public void modifyText(ModifyEvent event) {
		// get input
		String text = null;
		boolean isNew = false;
		if (event.getSource() instanceof Text) {
			Text textTxt = ((Text) event.getSource());
			text = textTxt.getText();
			if (textTxt.getData(Editor.NEW_ITEM)!=null) {
				isNew = (Boolean) textTxt.getData(Editor.NEW_ITEM);
				if (isNew) {
					textTxt.setData(Editor.NEW_ITEM, false);
				}
			}
		} else if (event.getSource() instanceof StyledText) {
			StyledText textTxt = (StyledText) event.getSource();
			text = textTxt.getText();
			if (textTxt.getData(Editor.NEW_ITEM)!=null) {
				isNew = (Boolean) textTxt.getData(Editor.NEW_ITEM);
				if (isNew) {
					textTxt.setData(Editor.NEW_ITEM, false);
				}
			}
		}

		// set input on edit item
		editorIdentification.getEditorStatus().setChanged();
		applyChange(text, modifyClass);
	}
}
