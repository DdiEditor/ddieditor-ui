package org.ddialliance.ddieditor.ui.editor.widgetutil.genericmodifylistener;

import org.ddialliance.ddieditor.ui.editor.Editor.EditorIdentification;
import org.ddialliance.ddieditor.ui.model.IModel;
import org.ddialliance.ddieditor.ui.util.DialogUtil;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.eclipse.swt.events.ModifyListener;

public abstract class GenericModifyListener implements ModifyListener {
	IModel model;
	Class<?> modifyClass;

	EditorIdentification editorIdentification;

	GenericModifyListener(IModel model, Class<?> modifyClass,
			EditorIdentification editorIdentification) {
		super();
		this.model = model;
		this.modifyClass = modifyClass;
		this.editorIdentification = editorIdentification;
	}

	void applyChange(Object value, Class<?> type) {
		try {
			model.applyChange(value, type);
		} catch (Exception e) {
			DDIFtpException e1 = new DDIFtpException(e);
			DialogUtil.errorDialog(editorIdentification.getSite(),
					editorIdentification.getID(), null, e1.getMessage(), e1);
		}
	}
}
