package org.ddialliance.ddieditor.ui.editor.category;

/**
 * Category Scheme Editor.
 * 
 */
/*
 * $Author: ddadak $ 
 * $Date: 2010-07-08 13:33:59 +0200 (Thu, 08 Jul 2010) $ 
 * $Revision: 1349 $
 */

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.ddialliance.ddieditor.ui.dbxml.IDao;
import org.ddialliance.ddieditor.ui.dbxml.category.CategorySchemeDao;
import org.ddialliance.ddieditor.ui.editor.CellEditorListener;
import org.ddialliance.ddieditor.ui.editor.LabelDescriptionEditor;
import org.ddialliance.ddieditor.ui.model.category.CategoryScheme;
import org.ddialliance.ddieditor.ui.perspective.CategoryPerspective;
import org.ddialliance.ddieditor.ui.view.Messages;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

public class CategorySchemeEditor extends LabelDescriptionEditor {

	private class CategoryCode {
		private String category;
		private String code;

		CategoryCode(String category, String code) {
			this.category = category;
			this.code = code;
		}

		public String getCategory() {
			return category;
		}
		
		public void setCategory(String category) {
			this.category = category;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getCode() {
			return code;
		}
	}

	private static Log log = LogFactory.getLog(LogType.SYSTEM, CategorySchemeEditor.class);
	public static final String ID = "org.ddialliance.ddieditor.ui.editor.category.CategorySchemeEditor";
	private CategoryScheme modelImpl;
	private TableViewer tableViewer;
	private CategoryLabelProvider tableLabelProvider;
	private List items = new ArrayList();

	public CategorySchemeEditor() {
		super(Messages.getString("CategorySchemeEditor.label.categorySchemeEditorLabel.CategorySchemeEditor"), Messages
				.getString("CategorySchemeEditor.label.useTheEditorLabel.Description"), Messages
				.getString("CategorySchemeEditor.label.CategorySchemeTabItem"), ID);
		dao = (IDao) new CategorySchemeDao();
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input);
		modelImpl = (CategoryScheme) model;
		items.add(new CategoryCode("CAT_1", "CODE_1"));
	}

	public String getPreferredPerspectiveId() {
		return CategoryPerspective.ID;
	}

	public String getPerspectiveSwitchDialogText() {
		return MessageFormat.format(Messages.getString("perspective.switch.dialogtext"),
				Messages.getString("perspective.category"));
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		// Categories
		// table viewer, table, table label provider, table content label
		// provider
//		createLabel(getLabelDescriptionTabGroup(), "Categories and codes");
//
//		tableViewer = new TableViewer(getLabelDescriptionTabGroup(), SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL
//				| SWT.FULL_SELECTION | SWT.BORDER);
//		tableViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
//		tableViewer.setContentProvider(new CategoryTableContentProvider());
//		tableLabelProvider = new CategoryLabelProvider();
//		tableLabelProvider.createColumns(tableViewer);
//		tableViewer.setLabelProvider(tableLabelProvider);
//		tableViewer.setInput(items);
	}

	/**
	 * Label provider
	 */
	public class CategoryLabelProvider extends LabelProvider implements ITableLabelProvider {
		/**
		 * Creates table columns
		 * 
		 * @param viewer
		 *            to be created on
		 */
		public void createColumns(final TableViewer viewer) {
			Table table = viewer.getTable();
			String[] titles = {
					// 0=Category label, 1=Code Label
					"Category", "Code" };
			int[] widths = { 200, 375, 225 };
			int[] style = { SWT.RIGHT, SWT.LEFT, SWT.LEFT };
			for (int i = 0; i < titles.length; i++) {
				TableViewerColumn column = new TableViewerColumn(viewer, style[i]);
				column.getColumn().setText(titles[i]);
				column.getColumn().setWidth(widths[i]);
				column.getColumn().setResizable(true);
				column.setEditingSupport(new TableEditingSupport(viewer, i));
			}
			table.setHeaderVisible(true);
			table.setLinesVisible(true);
			table.pack();
		}

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
//			LightXmlObjectType lightXmlObject = (LightXmlObjectType) element;
//			LabelType label = null;

			CategoryCode cc = (CategoryCode) items.get(0);
			switch (columnIndex) {
			// 0=Category label, 1=code label
			case 0:
				return cc.getCategory();
			case 1:
				return cc.getCode();
			default:
				DDIFtpException e = new DDIFtpException(
						Messages.getString("translationdialog.error.columnindexnotfound"), new Throwable()); //$NON-NLS-1$
				showError(e);
				return "";
			}
		}
	}

	/**
	 * Content provider
	 */
	public class CategoryTableContentProvider implements IStructuredContentProvider {
		@Override
		public Object[] getElements(Object parent) {
			return items.toArray();
		}

		public List getItems() {
			return items;
		}

		@Override
		public void dispose() {
			// noting to do
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// noting to do
		}
	}

	/**
	 * Column editing
	 */
	public class TableEditingSupport extends EditingSupport {
		private CellEditor editor;
		private int column;

		/**
		 * Constructor
		 * 
		 * @param viewer
		 *            of column
		 * @param column
		 *            no
		 */
		public TableEditingSupport(ColumnViewer viewer, int column) {
			super(viewer);

			// Create the correct editor based on the column index
			switch (column) {
			case 0:
				editor = new TextCellEditor(((TableViewer) viewer).getTable(), SWT.SINGLE | SWT.V_SCROLL);
				break;
			case 1:
				editor = new TextCellEditor(((TableViewer) viewer).getTable(), SWT.SINGLE | SWT.V_SCROLL);
				break;
			default:
				editor = new TextCellEditor(((TableViewer) viewer).getTable());
			}
			this.column = column;
		}

		@Override
		protected boolean canEdit(Object element) {
			return true;
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			ICellEditorListener listener = new CellEditorListener(editor, editorStatus, column);
			editor.addListener(listener);
			return editor;
		}

		@Override
		protected Object getValue(Object element) {
			log.debug("Column: " + this.column);
			CategoryCode cc = (CategoryCode) items.get(0);
			switch (this.column) {
			// xml text
			case 0:
				return cc.getCategory();
			case 1:
				return cc.getCode();
			default:
				break;
			}
			return null;
		}

		@Override
		public void setValue(Object element, Object value) {

			CategoryCode cc = (CategoryCode) items.get(0);
			switch (this.column) {
			case 0:
				cc.setCategory(value.toString());
				break;
			case 1:
				cc.setCode(value.toString());
				break;
			default:
				break;
			}
			getViewer().update(element, null);
		}
	}

}
