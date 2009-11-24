package org.ddialliance.ddieditor.ui.editor;

/**
 * Cell Editor Listener.
 * 
 */
/*
 * $Author$ 
 * $Date$ 
 * $Revision$
 */

import org.ddialliance.ddieditor.ui.editor.Editor.EditorStatus;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellEditorListener;

public class CellEditorListener implements ICellEditorListener {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, CellEditorListener.class);
	
	private CellEditor editor;
	private EditorStatus editorStatus;
	private int column;
	
	public CellEditorListener(CellEditor editor, EditorStatus editorStatus, int column) {
		this.editor = editor;
		this.editorStatus = editorStatus;
		this.column = column;
	}

	public void applyEditorValue() {
		if (log.isDebugEnabled()) {
			log.debug("Editing applied: "+editor.getValue()+" - "+column);
		}
		// Update Model
		// DONE in EditingSupport for given element
		
		// switch (column) {
		// case 0:
		// String text = (String) editor.getValue();
		// //TODO Update Question Item Literal Text - text part.
		// break;
		// case 1:
		// String language = Language.getLanguages()[((Integer)
		// editor.getValue())];
		// //TODO Update Question Item Literal Text - language part.
		// break;
		// default:
		// }

		editorStatus.setChanged();
	}

	public void cancelEditor() {
		if (log.isDebugEnabled()) {
			log.debug("Editing canceled");
		}
	}

	public void editorValueChanged(boolean oldValidState, boolean newValidState) {
		if (log.isDebugEnabled()) {
			log.debug("Editing value changed: Old Valid State: "+oldValidState+" New Valid State: "+newValidState);
		}
		editorStatus.setChanged();
	}
}
