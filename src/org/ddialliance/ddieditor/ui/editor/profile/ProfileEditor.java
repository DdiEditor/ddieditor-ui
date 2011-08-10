package org.ddialliance.ddieditor.ui.editor.profile;

import java.util.Iterator;

import org.ddialliance.ddieditor.ui.dbxml.profile.ProfileDao;
import org.ddialliance.ddieditor.ui.editor.BooleanCellEditor;
import org.ddialliance.ddieditor.ui.editor.CellEditorListener;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddieditor.ui.model.profile.Profile;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.Translator;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;

public class ProfileEditor extends Editor {
	public static final String ID = "org.ddialliance.ddieditor.ui.editor.profile.ProfileEditor";
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			ProfileEditor.class);

	private Profile model;
	private ProfileDao dao;

	private TableViewer tableViewer;
	private Table table;
	private Composite addRemoveComposite = null;

	private String enabled, disabled;

	public ProfileEditor(String headerEditorTitle, String headerEditorDescr) {
		super(headerEditorTitle, headerEditorDescr, ID);
		enabled = Translator.trans("enabled");
		disabled = Translator.trans("disabled");
	}

	private final void showError(DDIFtpException e) {
		new ErrorDialog(getSite().getShell(),
				ErrorDialog.DLG_IMG_MESSAGE_ERROR, e.getMessage(), new Status(
						Status.ERROR, "", e.getMessage()), 0).open();
	}

	@Override
	public void createPartControl(Composite parent) {
		// root components
		final Composite topComposite = new Composite(parent, SWT.NONE);
		topComposite.setLayout(new GridLayout());
		topComposite.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));

		final GridLayout topGridLayout = new GridLayout();
		topComposite.setLayout(topGridLayout);
		final Group topGroup = new Group(topComposite, SWT.NONE);
		topGroup.setLayoutData(new GridData(756, 647));
		topGroup.setLayout(new GridLayout());
		topGroup.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));
		topGroup.setText(Translator.trans("translationdialog.topgroup")); //$NON-NLS-1$

		// table viewer, table, table label provider, table content label
		// provider
		tableViewer = new TableViewer(topGroup, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		table = tableViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		tableViewer.setContentProvider(new TransTableContentProvider());
		TransLabelProvider tableLabelProvider = new TransLabelProvider();
		tableLabelProvider.createColumns(tableViewer);
		tableViewer.setLabelProvider(tableLabelProvider);
		try {
			tableViewer.setInput(model.getDocument().getDDIProfile()
					.getUsedList());
		} catch (DDIFtpException e1) {
			showError(e1);
		}

		// add remove composite
		addRemoveComposite = new Composite(topGroup, SWT.NONE);
		addRemoveComposite.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));
		final GridLayout gridLayout_2 = new GridLayout();
		gridLayout_2.numColumns = 2;
		addRemoveComposite.setLayout(gridLayout_2);
		addRemoveComposite.setEnabled(true);
		addRemoveComposite.setVisible(true);

		// add
		final Button addButton = new Button(addRemoveComposite, SWT.NONE);
		addButton.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));
		addButton.setText(Translator.trans("translationdialog.button.add")); //$NON-NLS-1$
		addButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				Object newItem = null;
				try {
					newItem = model.getDocument().getDDIProfile().addNewUsed();
				} catch (DDIFtpException e) {
					showError(e);
					return;
				}
				tableViewer.insert(newItem, -1);
				tableViewer.refresh(false);
				table.select(table.getItemCount() - 1);

				// to be improved, does not set the focus in gui!
				table.forceFocus();
				tableViewer.editElement(newItem, 0);
			}
		});

		// remove
		final Button removeButton = new Button(addRemoveComposite, SWT.NONE);
		removeButton.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));
		removeButton.setText(Translator
				.trans("translationdialog.button.remove")); //$NON-NLS-1$
		removeButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				ISelection selection = tableViewer.getSelection();
				if (selection instanceof IStructuredSelection) {
					Iterator iterator = ((IStructuredSelection) selection)
							.iterator();
					while (iterator.hasNext()) {
						Object obj = iterator.next();
						tableViewer.remove(obj);
						try {
							model.getDocument().getDDIProfile().getUsedList()
									.remove(obj);
						} catch (DDIFtpException e) {
							showError(e);
						}
						tableViewer.refresh();
					}
				}
			}
		});
	}

	/**
	 * Label provider
	 */
	public class TransLabelProvider extends LabelProvider implements
			ITableLabelProvider {
		/**
		 * Creates table columns
		 * 
		 * @param viewer
		 *            to be created on
		 */
		public void createColumns(final TableViewer viewer) {
			Table table = viewer.getTable();
			String[] titles = {
					Translator.trans("translationdialog.tablecolumn.translate"),
					Translator.trans("translationdialog.tablecolumn.language"),
					Translator
							.trans("translationdialog.tablecolumn.translated"),
					Translator
							.trans("translationdialog.tablecolumn.translatable") };
			// translationdialog.tablecolumn.preferred=Preferred
			int[] widths = { 350, 100, 120, 50 };
			for (int i = 0; i < titles.length; i++) {
				TableViewerColumn column = new TableViewerColumn(viewer, SWT.UP);
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
			switch (columnIndex) {
			case 0:
				// AlternateName" type="r:InternationalStringType" minOccurs="0" maxOccurs="unbounded">

				break;
			case 1:
				// Description" type="r:StructuredStringType" minOccurs="0" maxOccurs="unbounded">

				break;
			case 2:
				// Instructions" type="r:StructuredStringType" minOccurs="0" maxOccurs="unbounded">

				break;
			case 3:
				// fixedValue" type="xs:boolean" default="false">

				break;
			case 4:
				// required" type="xs:boolean" default="false">

				break;
			case 5:
				// path" type="xs:string" use="required">

				break;
			case 6:
				// defaultValue" type="xs:string" use="optional">

				break;
			default:
				DDIFtpException e = new DDIFtpException(
						Translator
								.trans("translationdialog.error.columnindexnotfound"), new Throwable()); //$NON-NLS-1$
				showError(e);
			}
			return "";
		}
	}

	/**
	 * Content provider
	 */
	public class TransTableContentProvider implements
			IStructuredContentProvider {
		@Override
		public Object[] getElements(Object parent) {
			try {
				return model.getDocument().getDDIProfile().getUsedList()
						.toArray();
			} catch (DDIFtpException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		public void dispose() {
			// TODO Auto-generated method stub
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// TODO Auto-generated method stub
		}
	}

	public enum OPEN_DIALOG {
		YES, NO;
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
				// AlternateName" type="r:InternationalStringType" minOccurs="0" maxOccurs="unbounded">
				editor = new TextCellEditor(((TableViewer) viewer).getTable(),
						SWT.MULTI | SWT.V_SCROLL);
				break;
			case 1:
				// Description" type="r:StructuredStringType" minOccurs="0" maxOccurs="unbounded">
				editor = new TextCellEditor(((TableViewer) viewer).getTable(),
						SWT.MULTI | SWT.V_SCROLL);
				break;
			case 2:
				// Instructions" type="r:StructuredStringType" minOccurs="0" maxOccurs="unbounded">
				editor = new TextCellEditor(((TableViewer) viewer).getTable(),
						SWT.MULTI | SWT.V_SCROLL);
				break;
			case 3:
				// fixedValue" type="xs:boolean" default="false">
				editor = new BooleanCellEditor(
						((TableViewer) viewer).getTable(), enabled, disabled);
				editor.setValue(Boolean.valueOf(true));
				break;
			case 4:
				// required" type="xs:boolean" default="false">
				editor = new BooleanCellEditor(
						((TableViewer) viewer).getTable(), enabled, disabled);
				break;
			case 5:
				// path" type="xs:string" use="required">
				editor = new TextCellEditor(((TableViewer) viewer).getTable(),
						SWT.MULTI | SWT.V_SCROLL);
				break;
			case 6:
				// defaultValue" type="xs:string" use="optional">
				editor = new TextCellEditor(((TableViewer) viewer).getTable(),
						SWT.MULTI | SWT.V_SCROLL);
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
			ICellEditorListener listener = new CellEditorListener(editor,
					editorStatus, column);
			editor.addListener(listener);
			return editor;
		}

		@Override
		protected Object getValue(Object element) {
			log.debug("Column: " + this.column);
			switch (this.column) {
			case 0:
				// AlternateName" type="r:InternationalStringType" minOccurs="0" maxOccurs="unbounded">

				break;
			case 1:
				// Description" type="r:StructuredStringType" minOccurs="0" maxOccurs="unbounded">

				break;
			case 2:
				// Instructions" type="r:StructuredStringType" minOccurs="0" maxOccurs="unbounded">

				break;
			case 3:
				// fixedValue" type="xs:boolean" default="false">

				break;
			case 4:
				// required" type="xs:boolean" default="false">

				break;
			case 5:
				// path" type="xs:string" use="required">

				break;
			case 6:
				// defaultValue" type="xs:string" use="optional">

				break;
			default:
				DDIFtpException e = new DDIFtpException(
						Translator
								.trans("translationdialog.error.columnindexnotfound"), new Throwable()); //$NON-NLS-1$
				showError(e);
				break;
			}
			return null;
		}

		@Override
		protected void setValue(Object element, Object value) {
			switch (this.column) {
			case 0:
				// AlternateName" type="r:InternationalStringType" minOccurs="0" maxOccurs="unbounded">

				break;
			case 1:
				// Description" type="r:StructuredStringType" minOccurs="0" maxOccurs="unbounded">

				break;
			case 2:
				// Instructions" type="r:StructuredStringType" minOccurs="0" maxOccurs="unbounded">

				break;
			case 3:
				// fixedValue" type="xs:boolean" default="false">

				break;
			case 4:
				// required" type="xs:boolean" default="false">

				break;
			case 5:
				// path" type="xs:string" use="required">

				break;
			case 6:
				// defaultValue" type="xs:string" use="optional">

				break;
			default:
				DDIFtpException e = new DDIFtpException(
						Translator
								.trans("translationdialog.error.columnindexnotfound"), new Throwable()); //$NON-NLS-1$
				showError(e);
				break;
			}
			getViewer().update(element, null);
		}
	}
}
