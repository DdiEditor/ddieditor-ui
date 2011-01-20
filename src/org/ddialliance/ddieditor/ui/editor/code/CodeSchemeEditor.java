package org.ddialliance.ddieditor.ui.editor.code;

/**
 * Code Scheme Editor.
 * 
 */
/*
 * $Author$ 
 * $Date$ 
 * $Revision$
 */

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ddialliance.ddi3.xml.xmlbeans.logicalproduct.CodeType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.ReferenceType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.SchemeReferenceType;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.Activator;
import org.ddialliance.ddieditor.ui.dbxml.IDao;
import org.ddialliance.ddieditor.ui.dbxml.category.CategoryDao;
import org.ddialliance.ddieditor.ui.dbxml.category.CategorySchemeDao;
import org.ddialliance.ddieditor.ui.dbxml.code.CodeSchemeDao;
import org.ddialliance.ddieditor.ui.editor.CellEditorListener;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddieditor.ui.editor.widgetutil.lightxmlobjectdnd.LightXmlObjectDragListener;
import org.ddialliance.ddieditor.ui.editor.widgetutil.lightxmlobjectdnd.LightXmlObjectTransfer;
import org.ddialliance.ddieditor.ui.editor.widgetutil.referenceselection.ReferenceSelectionAdapter;
import org.ddialliance.ddieditor.ui.editor.widgetutil.referenceselection.ReferenceSelectionCombo;
import org.ddialliance.ddieditor.ui.model.IModel;
import org.ddialliance.ddieditor.ui.model.code.CodeScheme;
import org.ddialliance.ddieditor.ui.model.translationdialoginput.DescriptionTdI;
import org.ddialliance.ddieditor.ui.model.translationdialoginput.NameTdI;
import org.ddialliance.ddieditor.ui.perspective.CodesPerspective;
import org.ddialliance.ddieditor.ui.util.DialogUtil;
import org.ddialliance.ddieditor.ui.util.swtdesigner.ResourceManager;
import org.ddialliance.ddieditor.ui.view.Messages;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.ddialliance.ddiftp.util.xml.XmlBeansUtil;
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
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class CodeSchemeEditor extends Editor {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, CodeSchemeEditor.class);
	public static final String ID = "org.ddialliance.ddieditor.ui.editor.code.CodeSchemeEditor";
	CodeScheme modelImpl;
	private TableViewer tableViewer;
	CodeLabelProvider tableLabelProvider;
	private List items = new ArrayList(); // (Codes
	
	private enum PopupAction {
		ADD, REMOVE
	};

	public CodeSchemeEditor() {
		super(Messages
				.getString("CodeSchemeEditor.label.CodeSchemeEditorLabel.CodeSchemeEditor"), Messages
				.getString("CodeSchemeEditor.label.useTheEditorLabel.Description"));
		dao = (IDao) new CodeSchemeDao();
	}
	
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input);
		modelImpl = (CodeScheme) model;
		
		List<CodeType> codes = modelImpl.getCodes();
		
		// Code value in Light Xml Object:
		// LightXmlObjectType.label: CodeType.value
		// LightXmlObjectType.Id: CodeType.Id
		for (int i = 0; i < codes.size(); i++) {
			LightXmlObjectType lightXmlObject = LightXmlObjectType.Factory.newInstance();
			XmlBeansUtil.setTextOnMixedElement(lightXmlObject.addNewLabel(), codes.get(i).getValue());
			lightXmlObject.setId(codes.get(i).getCategoryReference().getIDList().get(0).getStringValue());
			items.add(lightXmlObject);
		}
	}
	
	private void popupMenuAction(PopupAction action) {
		TableItem[] tableItems = tableViewer.getTable().getSelection();
		// guard
		if (action.equals(PopupAction.REMOVE) && tableItems.length <= 0) {
			DialogUtil.errorDialog(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell(), ID, null, null,
					new DDIFtpException());
			return;
		}
		
		boolean update = false;
		for (int i = 0; i < tableItems.length; i++) {
			LightXmlObjectType selectedLightXmlObject = (LightXmlObjectType) tableItems[i].getData();

			if (action.equals(PopupAction.ADD)) {
				try {
					int insert = 0;
					for (Iterator<LightXmlObjectType> iterator = items.iterator(); iterator.hasNext();insert++) {
						LightXmlObjectType lightXmlObject = iterator.next();
						if (lightXmlObject.equals(selectedLightXmlObject)) {
							update = true;
							// add to table
							LightXmlObjectType newLightXmlObject = LightXmlObjectType.Factory.newInstance();
							newLightXmlObject.setId("");
							XmlBeansUtil.setTextOnMixedElement(newLightXmlObject.addNewLabel(), "");
							items.add(insert, newLightXmlObject);
							// add to document
							CodeType code = CodeType.Factory.newInstance();
							code.addNewCategoryReference().addNewID().setStringValue("");
							code.setValue("");
							modelImpl.getDocument().getCodeScheme().getCodeList().add(insert,code);
							break;
						}
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			} else
			// remove
			if (action.equals(PopupAction.REMOVE)) {
				try {
					for (Iterator<CodeType> iterator = modelImpl.getDocument().getCodeScheme().getCodeList().iterator(); iterator.hasNext();) {
						CodeType codeType = iterator.next();
						// remove from document
						if (selectedLightXmlObject.getId().equals(codeType.getCategoryReference().getIDList().get(0).getStringValue())) {
							iterator.remove();
							// remove from table
							update = true;
							tableViewer.remove(selectedLightXmlObject);
							items.remove(selectedLightXmlObject);
							break;
						}
					}
				} catch (DDIFtpException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				log.warn("Action not specified!");
			}
			
			// update
			if (update) {
				tableViewer.refresh(true);
				this.editorStatus.setChanged();
			}
		}
	}
	
	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		parent.setLayout(new GridLayout());
		createTabFolder(getComposite_1());

		// main tab
		TabItem tabItem = createTabItem("Code Scheme");
		Group group = createGroup(tabItem, Messages.getString("QuestionConstruct"));
		
		// Default Category Scheme reference
		
		// - Get available Categori Schemes:
		List<LightXmlObjectType> categorySchemeReferenceList = new ArrayList();

		try {
			categorySchemeReferenceList = new CategorySchemeDao().getLightXmlObject("", "",	"", "");
		} catch (Exception e1) {
			DialogUtil
			.errorDialog(getEditorSite(), ID, null, e1.getMessage(), e1);
		}
		
		ReferenceSelectionCombo refSelecCombo = createRefSelection(group,
				Messages.getString("CodeSchemeEditor.label.DefaultCategoryScheme"),
				Messages.getString("CodeSchemeEditor.label.CategoryScheme"), modelImpl.getCategorySchemeReference(),
				categorySchemeReferenceList, false);

		refSelecCombo.addSelectionListener(Messages.getString("CodeSchemeEditor.label.CategoryScheme"),
				new CategorySchemeSelectionAdapter(refSelecCombo, modelImpl, ReferenceType.class,
						getEditorIdentification()));

		// Categories
		// table viewer, table, table label provider, table content label
		// provider
		createLabel(group, Messages.getString("CodeSchemeEditor.label.CategoriesAndCodes"));

		tableViewer = new TableViewer(group, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.FULL_SELECTION | SWT.BORDER);
		tableViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		tableViewer.setContentProvider(new CodeTableContentProvider());
		tableLabelProvider = new CodeLabelProvider();
		tableLabelProvider.createColumns(tableViewer);
		tableViewer.setLabelProvider(tableLabelProvider);
		tableViewer.setInput(items);
		
		// dnd difs
		int operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT;
		Transfer[] transferTypes = new Transfer[] { LightXmlObjectTransfer
				.getInstance() };

		// drag
		tableViewer.addDragSupport(operations, transferTypes,
				new LightXmlObjectDragListener(tableViewer, ID));

		// drop
		tableViewer.addDropSupport(operations, transferTypes,
				new CodeDropListener(this));

		// popup menu
		Menu menu = new Menu(tableViewer.getControl());

		// menu add
		final MenuItem addMenuItem = new MenuItem(menu, SWT.CASCADE);
		addMenuItem.setSelection(true);
		addMenuItem.setText(Messages.getString("View.label.addMenuItem.Add"));
		addMenuItem.setImage(ResourceManager.getPluginImage(
				Activator.getDefault(), "icons/new_wiz.gif"));
		addMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				popupMenuAction(PopupAction.ADD);
			}
		});

		// menu remove
		final MenuItem removeMenuItem = new MenuItem(menu, SWT.NONE);
		removeMenuItem.setText(Messages
				.getString("View.label.removeMenuItem.Remove"));
		removeMenuItem.setImage(ResourceManager.getPluginImage(
				Activator.getDefault(), "icons/delete_obj.gif"));
		removeMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				popupMenuAction(PopupAction.REMOVE);
			}
		});
		
		// menu enumerate
//		final MenuItem enumerateMenuItem = new MenuItem(menu, SWT.NONE);
//		enumerateMenuItem.setText("Enumerate");
//		enumerateMenuItem.setImage(ResourceManager.getPluginImage(
//				Activator.getDefault(), "icons/new_wiz_obj.gif"));
//		enumerateMenuItem.addSelectionListener(new SelectionAdapter() {
//			public void widgetSelected(final SelectionEvent e) {
//				popupMenuAction(PopupAction.ENUMERATE);
//			}
//		});
		
		// support double clip to activate Category editor
//		tableViewer.addDoubleClickListener(new IDoubleClickListener() {
//			public void doubleClick(final DoubleClickEvent event) {
//				Object obj = ((StructuredSelection) event.getSelection()).getFirstElement();
//				System.out.println(XmlBeansUtil.getXmlAttributeValue(event.getSelection().toString(), "id=\""));
//				openEditor(tableViewer, currentView, EditorModeType.EDIT, null);
//			}
//		});
		
		tableViewer.getControl().setMenu(menu);
		
		// description tab
		// name
		TabItem tabItem2 = createTabItem(Messages.getString("editor.label.description"));
		Group group2 = createGroup(tabItem2, Messages.getString("editor.label.description"));

		try {
			Text txt = createLabelInput(group2, Messages.getString("editor.label.label"), modelImpl.getDocument()
					.getCodeScheme().getLabelList(), modelImpl.getDocument().getCodeScheme().getId());

			createTranslation(group2, Messages.getString("editor.button.translate"), modelImpl.getDocument()
					.getCodeScheme().getLabelList(), new NameTdI(), "", txt);

			StyledText styledText = createStructuredStringInput(group2, Messages.getString("editor.label.description"),
					modelImpl.getDocument().getCodeScheme().getDescriptionList(), modelImpl.getDocument()
							.getCodeScheme().getId());
			createTranslation(group2, Messages.getString("editor.button.translate"), modelImpl.getDocument()
					.getCodeScheme().getDescriptionList(), new DescriptionTdI(), "", styledText);
		} catch (DDIFtpException e) {
			DialogUtil.errorDialog(getEditorSite(), ID, null, e.getMessage(), e);
		}

		// id tab
		createPropertiesTab(getTabFolder());

		// xml tab
		createXmlTab(modelImpl);

		editorStatus.clearChanged();
	}
	
	/**
	 * Label provider
	 */
	public class CodeLabelProvider extends LabelProvider implements ITableLabelProvider {
		/**
		 * Creates table columns
		 * 
		 * @param viewer
		 *            to be created on
		 */
		public void createColumns(final TableViewer viewer) {
			Table table = viewer.getTable();
			String[] titles = {
					// 0=Value, 1=Category
					"Value" ,"Category" };
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
			// Code value in Light Xml Object:
			// LightXmlObjectType.label: CodeType.value
			// LightXmlObjectType.Id: CodeType.Id
			LightXmlObjectType code = (LightXmlObjectType) element;

			switch (columnIndex) {
			// 0=value, 1=Category label
			case 0:
				return XmlBeansUtil.getTextOnMixedElement(code.getLabelList().get(0));
			case 1:
				// convert category id to category label
				LightXmlObjectType xml = null;
				if (code.getId().equals("")) {
					return "";
				}
				try {
					xml = new CategoryDao().getLightXmlObject(
							code.getId(), "", "", "").get(0);
				} catch (Exception e1) {
					DDIFtpException e2 = new DDIFtpException(
							Messages.getString("CodeSchemeEditor.mess.GetCategoryError"), e1); //$NON-NLS-1$
					showError(e2);
					return "";
				}
				return XmlBeansUtil.getTextOnMixedElement(xml);
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
	public class CodeTableContentProvider implements IStructuredContentProvider {
		@Override
		public Object[] getElements(Object parent) {
			try {
				if (items.size() == 0) {
					// check if default category scheme is specified
					SchemeReferenceType categoryScheme = modelImpl.getCategorySchemeReference();
					if (categoryScheme == null) {
						return items.toArray();
					}

					// - use it
					String categorySchemeId = modelImpl.getCategorySchemeReference().getIDList().get(0)
							.getStringValue();

					List<LightXmlObjectType> cats = new CategoryDao().getLightXmlObject("", "", categorySchemeId, "");

					// Code value in Light Xml Object:
					// LightXmlObjectType.label: CodeType.value
					// LightXmlObjectType.Id: CodeType.Id
					for (int i = 0; i < cats.size(); i++) {
						modelImpl.addCode(XmlBeansUtil.getXmlAttributeValue(cats.get(i).xmlText(), "id=\""),
								Integer.toString(i));
						LightXmlObjectType lightXmlObject = LightXmlObjectType.Factory.newInstance();
						XmlBeansUtil.setTextOnMixedElement(lightXmlObject.addNewLabel(), Integer.toString(i));
						lightXmlObject.setId(cats.get(i).getId());
						items.add(lightXmlObject);
					}
				}
				return items.toArray();
			} catch (Exception e) {
				DDIFtpException e1 = new DDIFtpException(
						Messages.getString("CodeSchemeEditor.mess.TableConcentProviderError"), e); //$NON-NLS-1$
				showError(e1);
				return new Object[0];
			}
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
				editor = new TextCellEditor(((TableViewer) viewer).getTable(),
						SWT.SINGLE | SWT.V_SCROLL);
				break;
			case 1:
				// Do not edit Category - drag instead
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
			//log.debug("Column: " + this.column);
			switch (this.column) {
			// code value
			case 0:
				LightXmlObjectType lightXmlObject = (LightXmlObjectType) element;
				return XmlBeansUtil.getTextOnMixedElement(lightXmlObject.getLabelList().get(0));
			// Category reference
			case 1:
				// Not editable - use drag and drop instead
				return "";
			default:
				break;
			}
			return null;
		}

		@Override
		public void setValue(Object element, Object value) {
			
			switch (this.column) {
			// code value
			case 0:
				LightXmlObjectType lightXmlObject = (LightXmlObjectType) element;
				XmlBeansUtil.setTextOnMixedElement(lightXmlObject.getLabelList().get(0), (String) value);
				// update Code Scheme Document
				Table table = (Table) ((TableViewer) getViewer()).getControl();
				int j = -1;
				for (j = 0; j < table.getItems().length; j++) {
					if (table.getItems()[j].getData().equals(lightXmlObject)) {
						break;
					}
				}
				if (j != -1) {
					try {
						modelImpl.getDocument().getCodeScheme().getCodeList().get(j).setValue((String) value);
					} catch (DDIFtpException e) {
						showError(e);
					}
				} else {
					DDIFtpException e1 = new DDIFtpException(
							Messages.getString("CodeSchemeEditor.mass.CorrespondingCodeNotFound"), new Throwable());
					showError(e1);
				}
				break;
				// Category reference
			case 1:
				// Not editable - use drag and drop instead
				break;
			default:
				break;
			}
			getViewer().update(element, null);
		}
	}

	
	private class CategorySchemeSelectionAdapter extends ReferenceSelectionAdapter {

		public CategorySchemeSelectionAdapter(ReferenceSelectionCombo refSelecCombo, IModel model, Class type,
				EditorIdentification editorIdentification) {
			super(refSelecCombo, model, type, editorIdentification);
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			super.widgetSelected(e);
			tableViewer.refresh();
		}
	}


	public String getPreferredPerspectiveId() {
		return CodesPerspective.ID;
	}

	public String getPerspectiveSwitchDialogText() {
		return MessageFormat.format(Messages.getString("perspective.switch.dialogtext"), Messages
				.getString("perspective.codes"));
	}

	public Viewer getViewer() {
		// TODO Auto-generated method stub
		return tableViewer;
	}

}
