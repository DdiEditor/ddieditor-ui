package org.ddialliance.ddieditor.ui.editor;

import java.util.Iterator;
import java.util.List;

import org.ddialliance.ddi3.xml.xmlbeans.reusable.StructuredStringType;
import org.ddialliance.ddieditor.ui.editor.Editor.EditorStatus;
import org.ddialliance.ddieditor.ui.view.XmlObjectComparer;
import org.ddialliance.ddiftp.util.xml.XmlBeansUtil;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;

public class StructuredStringTypeModyfiListener implements ModifyListener {
	private StructuredStringType strType;
	private List<StructuredStringType> list;
	private EditorStatus editorStatus;

	public StructuredStringTypeModyfiListener(StructuredStringType strType,
			List<StructuredStringType> list, EditorStatus editorStatus) {
		this.strType = strType;
		this.list = list;
		this.editorStatus = editorStatus;
	}

	@Override
	public void modifyText(ModifyEvent e) {
		editorStatus.setChanged();
		StyledText text = (StyledText) e.getSource();
		boolean isNew = (Boolean) text.getData(Editor.NEW_ITEM);

		// remove from list
		if (text.getText().equals("") && !isNew) {			
			XmlObjectComparer comparer = new XmlObjectComparer();
			for (Iterator<StructuredStringType> iterator = list.iterator(); iterator.hasNext();) {
				StructuredStringType listStr = iterator.next();			
				if (comparer.equals(listStr, strType)) {
					iterator.remove();
					break;
				}
			}
			return;
		}

		// set text
		XmlBeansUtil.setTextOnMixedElement(strType, text.getText());

		// add to list
		if (isNew) {
			list.add(strType);
			for (StructuredStringType test : list) {
				if (XmlBeansUtil.getTextOnMixedElement(strType).equals(
						text.getText())) {
					strType = test;
				}
			}
			text.setData(Editor.NEW_ITEM, Boolean.FALSE);
		}
	}
}
