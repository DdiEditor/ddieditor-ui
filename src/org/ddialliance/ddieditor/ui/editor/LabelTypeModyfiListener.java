package org.ddialliance.ddieditor.ui.editor;

import java.util.Iterator;
import java.util.List;

import org.ddialliance.ddi3.xml.xmlbeans.reusable.LabelType;
import org.ddialliance.ddieditor.ui.view.XmlObjectComparer;
import org.ddialliance.ddiftp.util.xml.XmlBeansUtil;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Text;

public class LabelTypeModyfiListener implements ModifyListener {
	private LabelType label;
	private List<LabelType> list;
	private Editor editor;

	public LabelTypeModyfiListener(LabelType label, List<LabelType> list,
			Editor editor) {
		this.label = label;
		this.list = list;
		this.editor = editor;
	}

	@Override
	public void modifyText(ModifyEvent e) {
		editor.editorStatus.setChanged();
		Text text = (Text) e.getSource();
		boolean isNew = (Boolean) text.getData(Editor.NEW_ITEM);

		// remove from list
		if (text.getText().equals("") && !isNew) {
			XmlObjectComparer comparer = new XmlObjectComparer();
			for (Iterator<LabelType> iterator = list.iterator(); iterator
					.hasNext();) {
				LabelType listLabel = iterator.next();
				if (comparer.equals(listLabel, label)) {
					iterator.remove();
					break;
				}
			}
			return;
		}

		// set text
		XmlBeansUtil.setTextOnMixedElement(label, text.getText());
		editor.setEditorTabName(text.getText());

		// add to list
		if ((Boolean) text.getData(Editor.NEW_ITEM)) {
			list.add(label);
			for (LabelType test : list) {
				if (XmlBeansUtil.getTextOnMixedElement(test).equals(
						text.getText())) {
					label = test;
				}
			}
			text.setData(Editor.NEW_ITEM, Boolean.FALSE);
		}
	}
}
