package org.ddialliance.ddieditor.ui.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.InternationalStringType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.NameType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.StructuredStringType;
import org.ddialliance.ddieditor.ui.editor.Editor.EditorStatus;
import org.ddialliance.ddieditor.ui.model.Language;
import org.ddialliance.ddieditor.ui.view.Messages;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.ddialliance.ddiftp.util.xml.XmlBeansUtil;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

/**
 * Generic translation dialog
 * <p>
 * Supports: InternationalStringType, StructuredStringType <br>
 * For other support enhance methods: getXmlLang, setLang, getXmlText,
 * setXmlText, getTranslateable, setTranslateable, setTranslated, getTranslated
 * </p>
 */
public class TranslationDialog extends Dialog {
	private static final Log log = LogFactory.getLog(LogType.SYSTEM,
			TranslationDialog.class);

	private final EditorStatus editorStatus;
	private List items;
	private String parentLabel;
	private XmlOptions xmlOptions = new XmlOptions();

	private Composite addRemoveComposite = null;
	private TableViewer tableViewer;
	private Table table;
	private List<String> langUsed = new ArrayList<String>();
	private String[] queries = { "translated=\"", "translatable=\"", "lang=\"" };
	private String enabled, disabled;

	/**
	 * Constructor creating dialog and defining the original language as the
	 * item in the items list with the attribute translated set to false.
	 * 
	 * @param parentShell
	 *            parent SWT shell
	 * @param editorStatus
	 *            editor status to be updated upon change in items
	 * @param items
	 *            to edit
	 * @param parentLabel
	 *            label to identify parent, eg. type and id, used in title of
	 *            dialog box
	 */
	public TranslationDialog(Shell parentShell, EditorStatus editorStatus,
			List<?> items, String parentLabel) {
		super(parentShell);
		this.items = items;
		this.editorStatus = editorStatus;
		this.parentLabel = parentLabel;
		xmlOptions.setSavePrettyPrint();
		enabled = Messages.getString("enabled");
		disabled = Messages.getString("disabled");
		updateLangUsed();
	}

	private final String getXmlLang(Object obj) throws DDIFtpException {
		if (obj instanceof InternationalStringType) {
			return ((InternationalStringType) obj).getLang();
		} else if (obj instanceof StructuredStringType) {
			return ((StructuredStringType) obj).getLang();
		}
		throw createTypeException(obj, new Throwable());
	}

	private final String getXmlText(Object obj) throws DDIFtpException {
		if (obj instanceof InternationalStringType) {
			return ((InternationalStringType) obj).getStringValue();
		} else if (obj instanceof StructuredStringType) {
			return ((StructuredStringType) obj).getDomNode().getTextContent();
		}
		throw createTypeException(obj, new Throwable());
	}

	private final void setXmlLang(Object obj, String lang)
			throws DDIFtpException {
		if (obj instanceof InternationalStringType) {
			((InternationalStringType) obj).setLang(lang);
		} else if (obj instanceof StructuredStringType) {
			((StructuredStringType) obj).setLang(lang);
		} else {
			throw createTypeException(obj, new Throwable());
		}
	}

	private final void setXmlText(Object obj, String text)
			throws DDIFtpException {
		if (obj instanceof InternationalStringType) {
			((InternationalStringType) obj).setStringValue(text);
		} else if (obj instanceof StructuredStringType) {
			XmlBeansUtil.setTextOnMixedElement(((StructuredStringType) obj),
					text);
		} else {
			throw createTypeException(obj, new Throwable());
		}
	}

	public Boolean getTranslated(Object obj) throws DDIFtpException {
		if (obj instanceof InternationalStringType) {
			return ((InternationalStringType) obj).getTranslated();
		} else if (obj instanceof StructuredStringType) {
			return ((StructuredStringType) obj).getTranslated();
		} else {
			throw createTypeException(obj, new Throwable());
		}
	}

	public void setTranslated(Object obj, boolean translated)
			throws DDIFtpException {
		if (obj instanceof InternationalStringType) {
			((InternationalStringType) obj).setTranslated(translated);
		} else if (obj instanceof StructuredStringType) {
			((StructuredStringType) obj).setTranslated(translated);
		} else {
			throw createTypeException(obj, new Throwable());
		}
	}

	public Boolean getTranslateable(Object obj) throws DDIFtpException {
		if (obj instanceof InternationalStringType) {
			return ((InternationalStringType) obj).getTranslatable();
		} else if (obj instanceof StructuredStringType) {
			return ((StructuredStringType) obj).getTranslatable();
		} else {
			throw createTypeException(obj, new Throwable());
		}
	}

	public void setTranslateable(Object obj, boolean translateable)
			throws DDIFtpException {
		if (obj instanceof InternationalStringType) {
			((InternationalStringType) obj).setTranslatable(translateable);
		} else if (obj instanceof StructuredStringType) {
			((StructuredStringType) obj).setTranslatable(translateable);
		} else {
			throw createTypeException(obj, new Throwable());
		}
	}

	private final Object addItem() throws DDIFtpException {
		// 'instance of' in hierarchy of sub types type first
		if (items.get(0) instanceof NameType) {
			NameType newItem = NameType.Factory.newInstance();
			newItem.setTranslatable(true);
			newItem.setTranslated(true);
			newItem.setStringValue("");
			return newItem;
		} else if (items.get(0) instanceof InternationalStringType) {
			InternationalStringType newItem = InternationalStringType.Factory
					.newInstance();
			newItem.setTranslatable(true);
			newItem.setTranslated(true);
			newItem.setStringValue("");
			return newItem;
		} else if (items.get(0) instanceof StructuredStringType) {
			StructuredStringType newItem = StructuredStringType.Factory
					.newInstance();
			newItem.setTranslatable(true);
			newItem.setTranslated(true);
			XmlBeansUtil.setTextOnMixedElement(newItem, "");
			return newItem;
		} else {
			throw createTypeException(items.get(0), new Throwable());
		}
	}

	public List<Object> getItems() {
		return items;
	}

	public List<String> updateLangUsed() {
		langUsed.clear();
		String lang = null;
		for (Object xmlObject : items) {
			String xml = ((XmlObject) xmlObject).xmlText(xmlOptions);
			lang = XmlBeansUtil.getXmlAttributeValue(xml, queries[2]);
			langUsed.add(lang);
		}
		return langUsed;
	}

	private DDIFtpException createTypeException(Object obj, Throwable t) {
		return new DDIFtpException("Element of class: "
				+ obj.getClass().getName() + " is not supported!", t);
	}

	private final void showError(DDIFtpException e) {
		new ErrorDialog(getShell(), ErrorDialog.DLG_IMG_MESSAGE_ERROR, e
				.getMessage(), new Status(Status.ERROR, "", e.getMessage()), 0)
				.open();
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		this.getShell().setText(
				Messages.getString("translationdialog.tittle") + parentLabel);

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
		topGroup.setText(Messages.getString("translationdialog.topgroup")); //$NON-NLS-1$

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
		tableViewer.setInput(items);

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
		addButton.setText(Messages.getString("translationdialog.button.add")); //$NON-NLS-1$
		addButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				Object newItem = null;
				try {
					newItem = addItem();
				} catch (DDIFtpException e) {
					showError(e);
					return;
				}
				try {
					items.add(newItem);
				} catch (Exception e) {
					showError(new DDIFtpException(e));
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
		removeButton.setText(Messages
				.getString("translationdialog.button.remove")); //$NON-NLS-1$
		removeButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ISelection selection = tableViewer.getSelection();
				if (selection instanceof IStructuredSelection) {
					Iterator iterator = ((IStructuredSelection) selection)
							.iterator();
					while (iterator.hasNext()) {
						Object obj = iterator.next();
						tableViewer.remove(obj);
						items.remove(obj);
						tableViewer.refresh();
						updateLangUsed();
					}
				}
			}
		});
		return topComposite;
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
					Messages
							.getString("translationdialog.tablecolumn.translate"),
					Messages
							.getString("translationdialog.tablecolumn.language"),
					Messages
							.getString("translationdialog.tablecolumn.translated"),
					Messages
							.getString("translationdialog.tablecolumn.translatable") };
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
				try {
					return getXmlText(element);
				} catch (DDIFtpException e) {
					showError(e);
				}
			case 1:
				try {
					return Language.getLanguage(getXmlLang(element));
				} catch (DDIFtpException e) {
					showError(e);
				}
			case 2:
				// translated
				try {
					return getTranslated(element) ? enabled : disabled;
				} catch (DDIFtpException e) {
					showError(e);
				}
			case 3:
				// translatable
				try {
					return getTranslateable(element) ? enabled : disabled;
				} catch (DDIFtpException e) {
					showError(e);
				}
			default:
				DDIFtpException e = new DDIFtpException(
						Messages
								.getString("translationdialog.error.columnindexnotfound"), new Throwable()); //$NON-NLS-1$
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
			return items.toArray();
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
				editor = new TextCellEditor(((TableViewer) viewer).getTable(),
						SWT.MULTI | SWT.V_SCROLL);
				break;
			case 1:
				editor = new ComboBoxCellEditor(((TableViewer) viewer)
						.getTable(), Language
						.getLanguageCodesExcludingLanguagesUsed(langUsed));
				break;
			case 2:
				// translated
				editor = new BooleanCellEditor(((TableViewer) viewer)
						.getTable(), enabled, disabled);
				editor.setValue(new Boolean(true));
				break;
			case 3:
				// translatable
				editor = new BooleanCellEditor(((TableViewer) viewer)
						.getTable(), enabled, disabled);
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
				try {
					return getXmlText(element);
				} catch (DDIFtpException e) {
					showError(e);
				}
			case 1:
				// Convert Language Code to Combo index e.g. 'no' to '1'
				int i = -1;
				try {
					i = Arrays
							.asList(
									Language
											.getLanguageCodesExcludingOrginalLanguage("da"))
							.indexOf(getXmlLang(element));
				} catch (DDIFtpException e) {
					showError(e);
				}
				return i == -1 ? 0 : new Integer(i);
			case 2:
				// translated
				try {
					return getTranslated(element);
				} catch (DDIFtpException e) {
					showError(e);
				}
			case 3:
				// translatable
				try {
					return getTranslateable(element);
				} catch (DDIFtpException e) {
					showError(e);
				}
			default:
				break;
			}
			return null;
		}

		@Override
		protected void setValue(Object element, Object value) {
			switch (this.column) {
			case 0:
				try {
					setXmlText(element, value.toString());
				} catch (DDIFtpException e) {
					showError(e);
				}
				break;
			case 1:
				// Convert Combo index to Language Code e.g. '1' to 'no'
				try {
					setXmlLang(
							element,
							Language
									.getLanguageCodesExcludingLanguagesUsed(langUsed)[((Integer) value)]);
				} catch (DDIFtpException e) {
					showError(e);
				}
				updateLangUsed();
				break;
			case 2:
				// translated
				try {
					setTranslated(element, (Boolean) value);
				} catch (DDIFtpException e) {
					showError(e);
				}
				break;
			case 3:
				// translatable
				try {
					setTranslateable(element, (Boolean) value);
				} catch (DDIFtpException e) {
					showError(e);
				}
				break;
			default:
				break;
			}
			getViewer().update(element, null);
		}
	}
}
