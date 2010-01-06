package org.ddialliance.ddieditor.ui.editor.widgetutil.referenceselection;

import java.util.List;

import org.apache.xmlbeans.XmlObject;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddieditor.ui.editor.Editor.EditorStatus;
import org.ddialliance.ddieditor.ui.editor.widgetutil.GenericGetSet;
import org.ddialliance.ddieditor.ui.editor.widgetutil.GenericGetSetClosure;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.ui.IEditorSite;

public class ReferenceSelectionAdapter extends GenericGetSet implements
		SelectionListener {
	ReferenceSelectionCombo refSelecCombo;

	public ReferenceSelectionAdapter(ReferenceSelectionCombo refSelecCombo,
			XmlObject editItem, GenericGetSetClosure closure,
			String getMethodName, String setMethodName, List<?> list,
			EditorStatus editorStatus, IEditorSite site, String editorId) {
		super(editItem, closure, getMethodName, setMethodName, list,
				editorStatus, site, editorId);
		this.refSelecCombo = refSelecCombo;
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		Combo combo = (Combo) e.getSource();
		set(refSelecCombo.getResult().getId(), (Boolean) combo
				.getData(Editor.NEW_ITEM));
		if ((Boolean) combo.getData(Editor.NEW_ITEM)) {
			combo.setData(Editor.NEW_ITEM, Boolean.FALSE);
		}
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		// do noting
	}
}
