package org.ddialliance.ddieditor.ui.editor;

import java.util.List;

import org.ddialliance.ddi3.xml.xmlbeans.reusable.StructuredStringType;
import org.ddialliance.ddieditor.ui.editor.Editor.EditorStatus;
import org.ddialliance.ddiftp.util.xml.XmlBeansUtil;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;

public class StructuredStringTypeModyfiListener implements ModifyListener {
	private StructuredStringType name;
	private List<StructuredStringType> list;
	private EditorStatus editorStatus;

	public StructuredStringTypeModyfiListener(StructuredStringType name,
			List<StructuredStringType> list, EditorStatus editorStatus) {
		this.name = name;
		this.list = list;
		this.editorStatus = editorStatus;
	}

	@Override
	public void modifyText(ModifyEvent e) {
		editorStatus.setChanged();
		StyledText text = (StyledText) e.getSource();
		XmlBeansUtil.setTextOnMixedElement(name, text.getText());
		if ((Boolean) text.getData(Editor.NEW_ITEM)) {
			list.add(name);
			for (StructuredStringType test : list) {
				if (XmlBeansUtil.getTextOnMixedElement(name).equals(text.getText())) {
					name = test;
				}
			}
			text.setData(Editor.NEW_ITEM, Boolean.FALSE);
		}
	}
}
