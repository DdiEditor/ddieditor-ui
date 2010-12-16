package org.ddialliance.ddieditor.ui.editor.widgetutil.referenceselection;

import org.ddialliance.ddieditor.ui.editor.Editor.EditorIdentification;
import org.ddialliance.ddieditor.ui.model.IModel;
import org.ddialliance.ddieditor.ui.util.DialogUtil;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

public class ReferenceSelectionAdapter implements SelectionListener {
	ReferenceSelectionCombo refSelecCombo;
	IModel model;
	Class type;
	EditorIdentification editorIdentification;

	public ReferenceSelectionAdapter(ReferenceSelectionCombo refSelecCombo,
			IModel model, Class type, EditorIdentification editorIdentification) {
		super();
		this.refSelecCombo = refSelecCombo;
		this.model = model;
		this.type = type;
		this.editorIdentification = editorIdentification;
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		// Combo combo = (Combo) e.getSource();
		try {
			System.out.println("********************\n"+refSelecCombo.getResult());
			if (refSelecCombo.getResult().getId().equals("")) {
				// TODO Remove sub-element of given type
//				model.applyChange(value, type, DELETE);
			} else {
				model.applyChange(refSelecCombo.getResult(), type /* , UPDATE */);
			}
		} catch (Exception ex) {
			DDIFtpException e1 = new DDIFtpException(ex);
			DialogUtil.errorDialog(editorIdentification.getSite(),
					editorIdentification.getID(), null, e1.getMessage(), e1);
		}
		editorIdentification.getEditorStatus().setChanged();
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		// do noting
	}
}
