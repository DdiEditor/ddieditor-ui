package org.ddialliance.ddieditor.ui.editor;

import java.util.Arrays;

import org.ddialliance.ddieditor.ui.editor.Editor.EditorStatus;
import org.ddialliance.ddieditor.ui.model.Language;
import org.ddialliance.ddieditor.ui.model.QuestionItemLiteralText;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;

public class TableEditingSupport extends EditingSupport {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, TableEditingSupport.class);
	
	private CellEditor editor;
	private int column;
	private EditorStatus editorStatus;
	private String originalLanguageCode; // Code = da Label = Danish
	
	// Constructor:
	public TableEditingSupport(ColumnViewer viewer, int column, EditorStatus editorStatus, String originalLanguageCode) {
		super(viewer);
		
		log.debug("Column: "+column);
		log.debug("Viewer object: "+viewer);
		
		this.editorStatus = editorStatus;
		this.originalLanguageCode = originalLanguageCode;
		
		// Create the correct editor based on the column index
		switch (column) {
		case 0:
			editor = new TextCellEditor(((TableViewer) viewer).getTable(), SWT.MULTI|SWT.V_SCROLL);
			break;
		case 1:
			editor = new ComboBoxCellEditor(((TableViewer) viewer).getTable(),
					Language.getLanguagesExcludingOrginalLanguage(originalLanguageCode));
			break;
		default:
			editor = new TextCellEditor(((TableViewer) viewer).getTable());
		}
		this.column = column;
	}
	
	public void setOriginalLanguageCode(String originalLanguageCode) {
		this.originalLanguageCode = originalLanguageCode;
	}

	@Override
	protected boolean canEdit(Object element) {
		log.debug("Column: "+this.column);
		return true;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		if (log.isDebugEnabled()) {
			log.debug("Column: " + this.column);
			log.debug("Editor: " + editor);
			log.debug("Value: " + element.toString());
		}
		ICellEditorListener listener = new CellEditorListener(editor, editorStatus, column);
		editor.addListener(listener);
		return editor;
	}

	/**
	 *  Get the value (text or language) from the questionItemLiteralText to the Editor
	 */
	@Override
	protected Object getValue(Object element) {
		log.debug("Get Column: "+this.column);
		QuestionItemLiteralText questionItemLiteralText = (QuestionItemLiteralText) element;

		switch (this.column) {
		case 0:
			log.debug("Text: "+questionItemLiteralText.getText());
			return questionItemLiteralText.getText();
		case 1:
			// Convert Language Code to Combo index e.g. 'no' to '1'
			log.debug("Language Code: "+questionItemLiteralText.getLanguageCode());
			int i = Arrays.asList(Language.getLanguageCodesExcludingOrginalLanguage(originalLanguageCode)).indexOf(questionItemLiteralText.getLanguageCode());
			return i == -1 ? 0 : new Integer(i);
		default:
			break;
		}
		return null;
	}

	/** 
	 * Restore the value (text or language) from the editor in the QuestionItemLiteralText instance
	 */
	@Override
	protected void setValue(Object element, Object value) {
		log.debug("Set Column: "+this.column);
   		QuestionItemLiteralText questionItemLiteralText = (QuestionItemLiteralText) element;

		switch (this.column) {
		case 0:
	   		log.debug("Text:"+value);
			questionItemLiteralText.setText(String.valueOf(value));
			break;
		case 1:
			// Convert Combo index to Language Code e.g. '1' to 'no'
	   		log.debug("Language Combo index: "+value);
			questionItemLiteralText.setLanguageCode(Language.getLanguageCodesExcludingOrginalLanguage(originalLanguageCode)[((Integer) value)]);			
			break;
		default:
			break;
		}
		editorStatus.setChanged();
		getViewer().update(element, null);
	}

}

