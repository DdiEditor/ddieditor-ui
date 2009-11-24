package org.ddialliance.ddieditor.ui.editor;

import java.util.List;

import org.ddialliance.ddi3.xml.xmlbeans.reusable.NameType;
import org.ddialliance.ddieditor.ui.editor.Editor.EditorStatus;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Text;


public class NameTypeModyfiListener implements ModifyListener {
	private NameType name;
	private List<NameType> list;
	private EditorStatus editorStatus;

	public NameTypeModyfiListener(NameType name, List<NameType> list, EditorStatus editorStatus) {
		this.name = name;
		this.list = list;
		this.editorStatus = editorStatus;
	}

	@Override
	public void modifyText(ModifyEvent e) {
		editorStatus.setChanged();
		Text text = (Text) e.getSource();
		name.setStringValue(text.getText());
		if ((Boolean) text.getData(Editor.NEW_ITEM)) {
			list.add(name);
			for (NameType test : list) {
				if (test.getStringValue().equals(text.getText())) {
					name = test;
				}
			}
			text.setData(Editor.NEW_ITEM, Boolean.FALSE);
		}
	}
}
