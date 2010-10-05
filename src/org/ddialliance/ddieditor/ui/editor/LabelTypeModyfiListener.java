package org.ddialliance.ddieditor.ui.editor;
import java.util.List;

import org.ddialliance.ddi3.xml.xmlbeans.reusable.LabelType;
import org.ddialliance.ddieditor.ui.editor.Editor.EditorStatus;
import org.ddialliance.ddiftp.util.xml.XmlBeansUtil;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Text;

public class LabelTypeModyfiListener  implements ModifyListener {
	private LabelType label;
	private List<LabelType> list;
	private EditorStatus editorStatus;

	public LabelTypeModyfiListener(LabelType label, List<LabelType> list, EditorStatus editorStatus) {
		this.label = label;
		this.list = list;
		this.editorStatus = editorStatus;
	}

	@Override
	public void modifyText(ModifyEvent e) {
		editorStatus.setChanged();
		Text text = (Text) e.getSource();
		XmlBeansUtil.setTextOnMixedElement(label, text.getText());
		if ((Boolean) text.getData(Editor.NEW_ITEM)) {
			list.add(label);
			for (LabelType test : list) {
				if (XmlBeansUtil.getTextOnMixedElement(test).equals(text.getText())) {
					label = test;
				}
			}
			text.setData(Editor.NEW_ITEM, Boolean.FALSE);
		}
	}
}
