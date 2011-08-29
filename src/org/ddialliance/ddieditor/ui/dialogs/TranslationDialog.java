package org.ddialliance.ddieditor.ui.dialogs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.InternationalStringType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.StructuredStringType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.impl.LabelDocumentImpl;
import org.ddialliance.ddieditor.ui.editor.BooleanCellEditor;
import org.ddialliance.ddieditor.ui.editor.CellEditorListener;
import org.ddialliance.ddieditor.ui.editor.Editor.EditorStatus;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.LanguageUtil;
import org.ddialliance.ddiftp.util.ReflectionUtil;
import org.ddialliance.ddiftp.util.Translator;
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
import org.eclipse.swt.widgets.Widget;

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
	private TranslationDialogInput translationDialogOption;
	private String parentLabel;
	private XmlOptions xmlOptions = new XmlOptions();

	private Composite addRemoveComposite = null;
	private TableViewer tableViewer;
	private Table table;
	private List<String> langUsed = new ArrayList<String>();
	private String[] queries = { "translated=\"", "translatable=\"", "lang=\"" };
	private String enabled, disabled;

	private String[][] availableLanguages = LanguageUtil
			.getAvailableLanguages();
	private Widget parentWidget;

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
	 * @param translationDialogOption
	 *            content description
	 * @param parentLabel
	 *            label to identify parent, eg. type and id, used in title of
	 *            dialog box
	 */
	public TranslationDialog(Shell parentShell, EditorStatus editorStatus,
			List<?> items, TranslationDialogInput translationDialogOption,
			String parentLabel, Widget parentWidget) {
		super(parentShell);
		this.items = items;
		this.translationDialogOption = translationDialogOption;
		this.editorStatus = editorStatus;
		this.parentLabel = parentLabel;
		this.parentWidget = parentWidget;
		xmlOptions.setSavePrettyPrint();
		enabled = Translator.trans("enabled");
		disabled = Translator.trans("disabled");
		updateLangUsed();
	}

	public List<String> updateLangUsed() {
		langUsed.clear();
		String lang = null;
		for (Object xmlObject : items) {
			lang = queryAttribute(xmlObject, queries[2]);
			if (lang != null && lang.equals("")) {
				langUsed.add(lang);
			}
		}
		return langUsed;
	}

	// ------------------------------------------------------------------------
	// xml beans
	// ------------------------------------------------------------------------
	private String queryAttribute(Object item, String query) {
		String result = null;
		String xml = ((XmlObject) item).xmlText(xmlOptions);
		result = XmlBeansUtil.getXmlAttributeValue(xml, query);
		return result;
	}

	private final String getXmlLang(Object obj) throws DDIFtpException {
		return queryAttribute(obj, queries[2]);
	}

	private final String getXmlText(Object obj) throws DDIFtpException {
		XmlObject xmlObject = XmlBeansUtil.getElementInElementStructure(
				translationDialogOption.getTextElement(), (XmlObject) obj);
		if (xmlObject != null) {
			if (xmlObject instanceof InternationalStringType) {
				return ((InternationalStringType) xmlObject).getStringValue();
			} else if (xmlObject instanceof StructuredStringType) {
				return XmlBeansUtil
						.getTextOnMixedElement((StructuredStringType) xmlObject);
			} else if (xmlObject instanceof LabelDocumentImpl) {
				return XmlBeansUtil
						.getTextOnMixedElement((LabelDocumentImpl) xmlObject);
			}
			throw createTypeException(xmlObject, new Throwable());
		}
		return "";
	}

	public final void setXmlText(Object obj, String text)
			throws DDIFtpException {
		XmlObject xmlObject = XmlBeansUtil.getElementInElementStructure(
				translationDialogOption.getTextElement(), (XmlObject) obj);
		if (xmlObject != null) {
			if (xmlObject instanceof InternationalStringType) {
				((InternationalStringType) xmlObject).setStringValue(text);
			} else if (xmlObject instanceof StructuredStringType) {
				XmlBeansUtil.setTextOnMixedElement(
						((StructuredStringType) xmlObject), text);
			} else {
				throw createTypeException(xmlObject, new Throwable());
			}
		}
	}

	public Boolean getTranslated(Object obj) throws DDIFtpException {
		return Boolean.parseBoolean(queryAttribute(obj, queries[0]));
	}

	public Boolean getTranslateable(Object obj) throws DDIFtpException {
		return Boolean.parseBoolean(queryAttribute(obj, queries[1]));
	}

	public final void setXmlLang(Object obj, String lang)
			throws DDIFtpException, Exception {
		if (lang != null) {
			ReflectionUtil.invokeMethod(obj, "setLang", false, lang);

		}
	}

	public void setTranslated(Object obj, boolean translated)
			throws DDIFtpException, Exception {
		ReflectionUtil.invokeMethod(obj, "setTranslated", false, translated);
	}

	public void setTranslateable(Object obj, boolean translateable)
			throws DDIFtpException, Exception {
		ReflectionUtil.invokeMethod(obj, "setTranslatable", false,
				translateable);
	}

	/**
	 * Add new element item to table
	 * 
	 * @param tableCount
	 *            - number of already existing table items.
	 * @return
	 * @throws DDIFtpException
	 */
	private final Object addItem(int tableItemCount) throws DDIFtpException {
		XmlObject xml = translationDialogOption.getTemplateElement();
		try {
			if (tableItemCount == 0) {
				setTranslated(xml, false);
				setTranslateable(xml, true);
				setXmlLang(xml,
						org.ddialliance.ddieditor.ui.util.LanguageUtil
								.getOriginalLanguage());
			} else {
				setTranslated(xml, true);
				setTranslateable(xml, false);
				setXmlLang(xml, "");
			}
		} catch (Exception e) {
			throw new DDIFtpException(e.getMessage());
		}
		return xml;
	}

	public List<Object> getItems() {
		return items;
	}

	public Widget getParentWidget() {
		return parentWidget;
	}

	// ------------------------------------------------------------------------
	// house keeping
	// ------------------------------------------------------------------------
	private DDIFtpException createTypeException(Object obj, Throwable t) {
		return new DDIFtpException("Element of class: "
				+ obj.getClass().getName() + " is not supported!", t);
	}

	private final void showError(Exception e) {
		new ErrorDialog(getShell(), ErrorDialog.DLG_IMG_MESSAGE_ERROR,
				e.getMessage(), new Status(Status.ERROR, "", e.getMessage()), 0)
				.open();
	}

	// ------------------------------------------------------------------------
	// swt
	// ------------------------------------------------------------------------
	@Override
	protected Control createDialogArea(Composite parent) {
		this.getShell().setText(
				Translator.trans("translationdialog.tittle") + parentLabel);

		// root components
		final Composite topComposite = new Composite(parent, SWT.NONE);
		topComposite.setLayout(new GridLayout());
		topComposite.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));

		final GridLayout topGridLayout = new GridLayout();
		topComposite.setLayout(topGridLayout);
		final Group topGroup = new Group(topComposite, SWT.NONE);
		topGroup.setLayoutData(new GridData(675, 250));
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
		addButton.setText(Translator.trans("translationdialog.button.add")); //$NON-NLS-1$
		addButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				Object newItem = null;
				try {
					newItem = addItem(table.getItemCount());
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
		removeButton.setText(Translator
				.trans("translationdialog.button.remove")); //$NON-NLS-1$
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

	// ------------------------------------------------------------------------
	// table support
	// ------------------------------------------------------------------------
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
				// text
				try {
					return getXmlText(element);
				} catch (DDIFtpException e) {
					showError(e);
				}
			case 1:
				// language name
				try {
					return LanguageUtil.getLanguage(getXmlLang(element),
							availableLanguages);
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
			return items.toArray();
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
				editor = new TextCellEditor(((TableViewer) viewer).getTable(),
						SWT.SINGLE | SWT.V_SCROLL);
				break;
			case 1:
				editor = new ComboBoxCellEditor(
						((TableViewer) viewer).getTable(),
						LanguageUtil.getLanguages(availableLanguages),
						SWT.READ_ONLY);
				break;
			case 2:
				// translated
				editor = new BooleanCellEditor(
						((TableViewer) viewer).getTable(), enabled, disabled);
				editor.setValue(new Boolean(true));
				break;
			case 3:
				// translatable
				editor = new BooleanCellEditor(
						((TableViewer) viewer).getTable(), enabled, disabled);
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
			// xml text
			case 0:
				try {
					return getXmlText(element);
				} catch (DDIFtpException e) {
					showError(e);
				}
			case 1:
				// language
				// Convert Language Code to Combo index e.g. 'no' to '1'
				int i = -1;
				try {
					i = LanguageUtil.getLanguageIndex(getXmlLang(element),
							availableLanguages);
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
		public void setValue(Object element, Object value) {

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
					setXmlLang(element, availableLanguages[(Integer) value][1]);
				} catch (Exception e) {
					showError(e);
				}
				updateLangUsed();
				break;
			case 2:
				// translated
				try {
					setTranslated(element, (Boolean) value);
				} catch (Exception e) {
					showError(e);
				}
				break;
			case 3:
				// translatable
				try {
					setTranslateable(element, (Boolean) value);
				} catch (Exception e) {
					showError(e);
				}
				break;
			default:
				break;
			}
			getViewer().update(element, null);
		}
	}

	@Override
	protected void okPressed() {
		// do validation
		// System.out.println("***** Items: "+items);
		// List<XmlObject> xList = items;
		// for (XmlObject xmlObject : xList) {
		// }
		// if validate
		super.okPressed();
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

}
