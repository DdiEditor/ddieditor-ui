package org.ddialliance.ddieditor.ui.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.ddialliance.ddi_3_0.xml.xmlbeans.reusable.InternationalStringType;
import org.ddialliance.ddi_3_0.xml.xmlbeans.reusable.StructuredStringType;
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
import org.eclipse.jface.dialogs.MessageDialog;
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

public class TranslationDialog extends Dialog {
	private static final Log log = LogFactory.getLog(LogType.SYSTEM,
			TranslationDialog.class);
	private EditorStatus editorStatus;
	private Composite translatedButtonComposite = null;
	private TableViewer tableViewer;
	private Table table;

	private List items;
	private Object base = null;
	XmlOptions xmlOptions = new XmlOptions();
	String[] queries = { "translated=\"", "translatable=\"", "lang=\"" };

	public TranslationDialog(Shell parentShell, EditorStatus editorStatus,
			List<?> items) {
		super(parentShell);
		this.items = items;
		this.editorStatus = editorStatus;
		xmlOptions.setSavePrettyPrint();

		try {
			setBase();
		} catch (DDIFtpException e) {
			showError(e);
		}
	}

	private void setBase() throws DDIFtpException {
		for (Object xmlObject : items) {
			if (!Boolean.parseBoolean(getAttribute(((XmlObject) xmlObject)
					.xmlText(xmlOptions), queries[0]))) {
				base = xmlObject;
			}
		}
		if (base == null) {
			throw new DDIFtpException("Original language is not set",
					new Throwable());
		}
	}

	public final String getXmlLang(Object obj) throws DDIFtpException {
		if (obj instanceof InternationalStringType) {
			return ((InternationalStringType) obj).getLang();
		} else if (obj instanceof StructuredStringType) {
			return ((StructuredStringType) obj).getLang();
		}
		throw createTypeException(obj, new Throwable());
	}

	public final String getXmlText(Object obj) throws DDIFtpException {
		if (obj instanceof InternationalStringType) {
			return ((InternationalStringType) obj).getStringValue();
		} else if (obj instanceof StructuredStringType) {
			return ((StructuredStringType) obj).getDomNode().getTextContent();
		}
		throw createTypeException(obj, new Throwable());
	}

	public final void setXmlLang(Object obj, String lang)
			throws DDIFtpException {
		if (obj instanceof InternationalStringType) {
			((InternationalStringType) obj).setLang(lang);
		} else if (obj instanceof StructuredStringType) {
			((StructuredStringType) obj).setLang(lang);
		} else {
			throw createTypeException(obj, new Throwable());
		}
	}

	public final void setXmlText(Object obj, String text)
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

	public final Object addItem() throws DDIFtpException {
		if (items.get(0) instanceof InternationalStringType) {
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

	public final List<Object> getTranslateables(String orgLang)
			throws Exception {
		List<Object> result = new ArrayList<Object>();
		boolean translated = false;
		boolean translatable = false;
		String lang = null;

		XmlOptions xmlOptions = new XmlOptions();
		xmlOptions.setSavePrettyPrint();
		for (Object xmlObject : items) {
			String xml = ((XmlObject) xmlObject).xmlText(xmlOptions);
			for (int i = 0; i < queries.length; i++) {
				switch (i) {
				case 0:
					translated = Boolean.parseBoolean(getAttribute(xml,
							queries[i]));
					break;
				case 1:
					translatable = Boolean.parseBoolean(getAttribute(xml,
							queries[i]));
					break;
				case 2:
					lang = getAttribute(xml, queries[i]);
					break;
				default:
					break;
				}
			}
			if (log.isDebugEnabled()) {
				log.debug("translatable: " + translatable + ", translated: "
						+ translated + ", lang: " + lang);
			}
			if (translatable && translated && !lang.equals(orgLang)) {
				result.add(xmlObject);
			}
		}
		return result;
	}

	private String getAttribute(String xml, String attrQuery) {
		// Hack, prop with xmlbeans to handle qNames on <xml-fragment ....>
		// xml objects. Fixed with simple string parsing.
		int index, start, end;
		index = xml.indexOf(attrQuery);
		// guard
		if (index == -1) {
			return "";
		}
		start = index + attrQuery.length();
		end = xml.indexOf("\"", start);
		return xml.substring(start, end);
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
		// - Root Composite:
		final Composite topComposite = new Composite(parent, SWT.NONE);
		topComposite.setLayout(new GridLayout());
		topComposite.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));

		final GridLayout translatedGridLayout = new GridLayout();
		topComposite.setLayout(translatedGridLayout);

		// - Translated Group
		final Group translatedGroup = new Group(topComposite, SWT.NONE);
		translatedGroup.setLayoutData(new GridData(756, 647));
		translatedGroup.setLayout(new GridLayout());
		translatedGroup.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));
		translatedGroup
				.setText(Messages
						.getString("QuestionItemEditor.label.translatedQuestionsGroup.TranslatedQuestions")); //$NON-NLS-1$

		final Button translatableButton = new Button(translatedGroup, SWT.CHECK);
		translatableButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				boolean translatable = translatableButton.getSelection();
				if (translatable) {
					translatedButtonComposite.setEnabled(true);
					editorStatus.setChanged();
				} else {
					if (tableViewer.getTable().getItemCount() > 0
							&& MessageDialog
									.openConfirm(
											getShell(),
											Messages.getString("ConfirmTitle"),
											Messages
													.getString("QuestionItemEditor.mess.ConfirmCleanTranslatedQuestionItemList"))) {
						tableViewer.getTable().removeAll();
						translatedButtonComposite.setEnabled(false);
						editorStatus.setChanged();
					} else {
						// Restore status
						translatableButton.setSelection(true);
					}
				}
			}
		});
		translatableButton.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));
		translatableButton
				.setText(Messages
						.getString("QuestionItemEditor.label.translatableButton.Translatable")); //$NON-NLS-1$

		// Define - Question Item Table Viewer:
		tableViewer = new TableViewer(translatedGroup, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		table = tableViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		tableViewer.setContentProvider(new TransTableContentProvider());
		TransLabelProvider tableLabelProvider = new TransLabelProvider();
		tableLabelProvider.createColumns(tableViewer);
		tableViewer.setLabelProvider(tableLabelProvider);
		tableViewer.setInput(items);

		translatedButtonComposite = new Composite(translatedGroup, SWT.NONE);
		translatedButtonComposite.setBackground(Display.getCurrent()
				.getSystemColor(SWT.COLOR_WHITE));
		final GridLayout gridLayout_2 = new GridLayout();
		gridLayout_2.numColumns = 2;
		translatedButtonComposite.setLayout(gridLayout_2);

		// - "Add" button:
		final Button addButton = new Button(translatedButtonComposite, SWT.NONE);
		addButton.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));
		addButton.setText(Messages
				.getString("QuestionItemEditor.label.addButton.Add")); //$NON-NLS-1$
		addButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Object newItem = null;
				try {
					newItem = addItem();
				} catch (DDIFtpException e1) {
					showError(e1);
					return;
				}

				tableViewer.add(newItem);
				table.setTopIndex(table.getItemCount());
				table.select(table.getItemCount() - 1);
				tableViewer.editElement(newItem, 0);
				items.add(newItem);
			}
		});

		// - "Remove" button:
		final Button removeButton = new Button(translatedButtonComposite,
				SWT.NONE);
		removeButton.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));
		removeButton.setText("Remove");
		removeButton.setText(Messages
				.getString("QuestionItemEditor.label.removeButton.Remove")); //$NON-NLS-1$
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
					}
				}
			}
		});

		// Check or un-check translatable check-box and enable/disable Add /
		// Remove buttons accordingly
		if (tableViewer.getTable().getItemCount() > 0) {
			translatableButton.setSelection(true);
		} else {
			translatedButtonComposite.setEnabled(false);
		}
		return topComposite;
	}

	// Question Item Text - table label provider
	public class TransLabelProvider extends LabelProvider implements
			ITableLabelProvider {

		public void createColumns(TableViewer viewer) {
			Table table = viewer.getTable();
			String[] titles = {
					Messages
							.getString("QITTableLabelProvider.label.TranslatedQuestionText"),
					Messages.getString("QITTableLabelProvider.label.Language") };
			int[] bounds = { 500, 60, };

			for (int i = 0; i < titles.length; i++) {
				TableViewerColumn column = new TableViewerColumn(viewer,
						SWT.NONE);
				column.getColumn().setText(titles[i]);
				column.getColumn().setWidth(bounds[i]);
				column.getColumn().setResizable(true);
				try {
					column.setEditingSupport(new TableEditingSupport(viewer, i,
							Language.getLanguageCode(getXmlLang(base))));
				} catch (DDIFtpException e) {
					showError(e);
				}
				table.setHeaderVisible(true);
				table.setLinesVisible(true);
			}
		}

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		/**
		 * Returns the label text of a given column for the element
		 */
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
			default:
				showError(new DDIFtpException("Error", new Throwable()));
				throw new RuntimeException(
						Messages
								.getString("QITTableLabelProvider.mess.UnexpectedColumnIndexFound")); //$NON-NLS-1$
			}
		}
	}

	public class TransTableContentProvider implements
			IStructuredContentProvider {

		/*
		 * Get Translated Question Item Texts
		 * 
		 * - i.e. Question Text with "true" translated attribute.
		 * 
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.IStructuredContentProvider#getElements(
		 * java.lang.Object)
		 */
		@Override
		public Object[] getElements(Object parent) {
			try {
				return getTranslateables(getXmlLang(base)).toArray();
			} catch (DDIFtpException e) {
				showError(e);
			} catch (Exception e) {
				showError(new DDIFtpException(e));
			}
			return new Object[] {};
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

	public class TableEditingSupport extends EditingSupport {
		private CellEditor editor;
		private int column;
		private String originalLanguageCode; // Code = da Label = Danish

		// Constructor:
		public TableEditingSupport(ColumnViewer viewer, int column,
				String originalLanguageCode) {
			super(viewer);
			this.originalLanguageCode = originalLanguageCode;

			// Create the correct editor based on the column index
			switch (column) {
			case 0:
				editor = new TextCellEditor(((TableViewer) viewer).getTable(),
						SWT.MULTI | SWT.V_SCROLL);
				break;
			case 1:
				editor = new ComboBoxCellEditor(
						((TableViewer) viewer).getTable(),
						Language
								.getLanguagesExcludingOrginalLanguage(originalLanguageCode));
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
			return true;
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			ICellEditorListener listener = new CellEditorListener(editor,
					editorStatus, column);
			editor.addListener(listener);
			return editor;
		}

		/**
		 * Get the value (text or language) from the questionItemLiteralText to
		 * the Editor
		 */
		@Override
		protected Object getValue(Object element) {
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
											.getLanguageCodesExcludingOrginalLanguage(originalLanguageCode))
							.indexOf(getXmlLang(element));
				} catch (DDIFtpException e) {
					showError(e);
				}
				return i == -1 ? 0 : new Integer(i);
			default:
				break;
			}
			return null;
		}

		/**
		 * Restore the value (text or language) from the editor in the
		 * QuestionItemLiteralText instance
		 */
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
									.getLanguageCodesExcludingOrginalLanguage(originalLanguageCode)[((Integer) value)]);
				} catch (DDIFtpException e) {
					showError(e);
				}
				break;
			default:
				break;
			}
			getViewer().refresh();
		}
	}
}
