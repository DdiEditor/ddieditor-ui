package org.ddialliance.ddieditor.ui.editor.widgetutil.genericselectionlistener;

import org.ddialliance.ddieditor.ui.editor.Editor.EditorIdentification;
import org.ddialliance.ddieditor.ui.model.IModel;
import org.ddialliance.ddieditor.ui.util.DialogUtil;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

public abstract class GenericComboSelectionListener implements
		SelectionListener {
	IModel model;
	protected Class modifyClass;
	protected EditorIdentification editorIdentification;

	public GenericComboSelectionListener(IModel model, Class modifyClass,
			EditorIdentification editorIdentification) {
		super();
		this.model = model;
		this.modifyClass = modifyClass;
		this.editorIdentification = editorIdentification;
	}

	@Override
	public abstract void widgetSelected(SelectionEvent e);

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		// do nothing, pirat :o )
	}

	protected void applyChange(Object value, Class<?> type) {
		try {
			model.applyChange(value, type);
		} catch (Exception e) {
			DDIFtpException e1 = new DDIFtpException(e);
			DialogUtil.errorDialog(editorIdentification.getSite(),
					editorIdentification.getID(), null, e1.getMessage(), e1);
		}
	}
}
