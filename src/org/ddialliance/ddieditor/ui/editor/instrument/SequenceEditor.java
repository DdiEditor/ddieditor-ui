package org.ddialliance.ddieditor.ui.editor.instrument;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.xmlbeans.XmlObject;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.ReferenceType;
import org.ddialliance.ddieditor.logic.urn.ddi.ReferenceResolution;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LabelType;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.persistenceaccess.PersistenceManager;
import org.ddialliance.ddieditor.persistenceaccess.maintainablelabel.MaintainableLightLabelQueryResult;
import org.ddialliance.ddieditor.ui.Activator;
import org.ddialliance.ddieditor.ui.dbxml.instrument.SequenceDao;
import org.ddialliance.ddieditor.ui.editor.CellEditorListener;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddieditor.ui.editor.EditorInput.EditorModeType;
import org.ddialliance.ddieditor.ui.model.instrument.Sequence;
import org.ddialliance.ddieditor.ui.model.translationdialoginput.DescriptionTdI;
import org.ddialliance.ddieditor.ui.model.translationdialoginput.LabelTdI;
import org.ddialliance.ddieditor.ui.perspective.IAutoChangePerspective;
import org.ddialliance.ddieditor.ui.util.DialogUtil;
import org.ddialliance.ddieditor.ui.util.LanguageUtil;
import org.ddialliance.ddieditor.ui.util.swtdesigner.ResourceManager;
import org.ddialliance.ddieditor.ui.view.Messages;
import org.ddialliance.ddieditor.ui.view.TreeMenu;
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

public class SequenceEditor extends Editor implements IAutoChangePerspective {

	public static final String ID = "org.ddialliance.ddieditor.ui.editor.instrument.SequenceEditor";
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			SequenceEditor.class);
	Sequence modelImpl;
	Map<String, LightXmlObjectType> controlConstructMap = new HashMap<String, LightXmlObjectType>();

	private TableViewer tableViewer;
	private Table table;
	private List items = new ArrayList();

	private enum PopupAction {
		ADD, EDIT, REMOVE
	};

	public SequenceEditor() {
		super(Messages.getString("SequenceEditor.label"), Messages
				.getString("SequenceEditor.description"));
		this.dao = new SequenceDao();
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
		modelImpl = (Sequence) model;

		// control constructs lights
		MaintainableLightLabelQueryResult controlConstructsTemp = null;
		try {
			controlConstructsTemp = DdiManager.getInstance()
					.getInstrumentLabel(null, null, null, null);
		} catch (DDIFtpException e) {
			DialogUtil.errorDialog(getSite().getShell(), ID, null, e
					.getMessage(), e);
		}

		for (LinkedList<LightXmlObjectType> lightXmlObjectList : controlConstructsTemp
				.getResult().values()) {
			for (LightXmlObjectType lightXmlObject : lightXmlObjectList) {
				controlConstructMap.put(lightXmlObject.getId(), lightXmlObject);
			}
		}
		controlConstructsTemp = null;

		LightXmlObjectType lightXmlObject = null;
		for (ReferenceType ref : modelImpl.getDocument().getSequence()
				.getControlConstructReferenceList()) {
			lightXmlObject = controlConstructMap.get(new ReferenceResolution(
					ref).getId());
			if (lightXmlObject != null) {
				items.add(lightXmlObject);
			}
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		parent.setLayout(new GridLayout());
		createTabFolder(getComposite_1());

		// main tab
		TabItem tabItem = createTabItem(Messages.getString("Sequence"));
		Group group = createGroup(tabItem, Messages.getString("Sequence"));

		// table viewer, table, table label provider, table content label
		// provider
		tableViewer = new TableViewer(group, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		table = tableViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		tableViewer.setContentProvider(new SequenceTableContentProvider());
		SequenceLabelProvider tableLabelProvider = new SequenceLabelProvider();
		tableLabelProvider.createColumns(tableViewer);
		tableViewer.setLabelProvider(tableLabelProvider);
		tableViewer.setInput(items);

		// popup menu
		Menu menu = new Menu(tableViewer.getTable());

		// menu add
		final MenuItem addMenuItem = new MenuItem(menu, SWT.CASCADE);
		addMenuItem.setSelection(true);
		addMenuItem.setText(Messages.getString("View.label.addMenuItem.Add"));
		addMenuItem.setImage(ResourceManager.getPluginImage(Activator
				.getDefault(), "icons/new_wiz.gif"));
		addMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				popupMenuAction(PopupAction.ADD);
			}
		});

		// menu edit
		MenuItem editMenuItem = new MenuItem(menu, SWT.NONE);
		editMenuItem
				.setText(Messages.getString("View.label.editMenuItem.Edit"));
		editMenuItem.setImage(ResourceManager.getPluginImage(Activator
				.getDefault(), "icons/editor_area.gif"));
		editMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				popupMenuAction(PopupAction.EDIT);
			}
		});

		// menu remove
		final MenuItem removeMenuItem = new MenuItem(menu, SWT.NONE);
		removeMenuItem.setText(Messages
				.getString("View.label.removeMenuItem.Remove"));
		removeMenuItem.setImage(ResourceManager.getPluginImage(Activator
				.getDefault(), "icons/delete_obj.gif"));
		removeMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				popupMenuAction(PopupAction.REMOVE);
			}
		});
		tableViewer.getControl().setMenu(menu);

		// specific sequence

		// description tab
		TabItem tabItem2 = createTabItem(Messages
				.getString("editor.label.description"));
		Group group2 = createGroup(tabItem2, Messages
				.getString("editor.label.description"));

		try {
			Text txt = createLabelInput(group2, Messages
					.getString("editor.label.label"), modelImpl.getDocument()
					.getSequence().getLabelList(), modelImpl.getDocument()
					.getSequence().getId());

			createTranslation(group2, Messages
					.getString("editor.button.translate"), modelImpl
					.getDocument().getSequence().getLabelList(),
					new LabelTdI(), "", txt);

			StyledText styledText = createStructuredStringInput(group2,
					Messages.getString("editor.label.description"), modelImpl
							.getDocument().getSequence().getDescriptionList(),
					modelImpl.getDocument().getSequence().getId());
			createTranslation(group2, Messages
					.getString("editor.button.translate"), modelImpl
					.getDocument().getSequence().getDescriptionList(),
					new DescriptionTdI(), "", styledText);
		} catch (DDIFtpException e) {
			DialogUtil
					.errorDialog(getEditorSite(), ID, null, e.getMessage(), e);
		}

		// id tab
		createPropertiesTab(getTabFolder());

		// xml tab
		createXmlTab(modelImpl);

		editorStatus.clearChanged();
	}

	// ------------------------------------------------------------------------
	// table support
	// ------------------------------------------------------------------------
	/**
	 * Add new element item to table
	 * 
	 * @param tableCount
	 *            - number of already existing table items.
	 * @return
	 * @throws DDIFtpException
	 */
	private final Object addItem(int tableItemCount) throws DDIFtpException {
		XmlObject xml = null;
		// do some logic here on xml
		return xml;
	}

	public List<Object> getItems() {
		return items;
	}

	private void popupMenuAction(PopupAction action) {
		TableItem[] tableItems = tableViewer.getTable().getSelection();
		// guard
		if (tableItems.length <= 0) {
			DialogUtil.errorDialog(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell(), ID, null, null,
					new DDIFtpException());
			return;
		}

		boolean update = false;
		for (int i = 0; i < tableItems.length; i++) {
			LightXmlObjectType selectedLightXmlObject = (LightXmlObjectType) tableItems[i]
					.getData();
			if (log.isDebugEnabled()) {
				log.debug(selectedLightXmlObject);
			}

			// add
			MaintainableLightLabelQueryResult controlConstructRefListTemp = null;
			try {
				controlConstructRefListTemp = DdiManager.getInstance()
						.getInstrumentLabel(null, null, null, null);
			} catch (DDIFtpException e) {
				DialogUtil.errorDialog(getSite().getShell(), ID, null, e
						.getMessage(), e);
			}
			List<LightXmlObjectType> controlConstructRefList = new ArrayList<LightXmlObjectType>();
			for (LinkedList<LightXmlObjectType> lightXmlObjectList : controlConstructRefListTemp
					.getResult().values()) {
				controlConstructRefList.addAll(lightXmlObjectList);
			}
			controlConstructRefListTemp = null;
			controlConstructRefList.removeAll(items);

			if (action.equals(PopupAction.ADD)) {
				SequenceMenuPopupAddDialog addDialog = new SequenceMenuPopupAddDialog(
						getSite().getShell(), Messages
								.getString("SequenceEditor.adddialog.title"),
						Messages.getString("SequenceEditor.adddialog.label"),
						controlConstructRefList, modelImpl);
				addDialog.open();

				// log.debug();
				if (addDialog.getResult() != null) {
					int count = 0; // count is before
					for (Iterator<LightXmlObjectType> iterator = items
							.iterator(); iterator.hasNext(); count++) {
						LightXmlObjectType lightXmlObject = iterator.next();
						if (lightXmlObject.equals(selectedLightXmlObject)) {
							update = true;
							int insert = addDialog.combo.getSelectionIndex() == 1 ? count + 1
									: count;

							if (insert < items.size()) {
								// add xml
								modelImpl.getDocument().getSequence()
										.getControlConstructReferenceList()
										.add(
												insert,
												new ReferenceResolution(
														addDialog.getResult())
														.getReference());

								// add table
								items.add(insert, addDialog.getResult());
							} else {
								// add xml
								modelImpl.getDocument().getSequence()
										.getControlConstructReferenceList()
										.add(
												new ReferenceResolution(
														addDialog.getResult())
														.getReference());

								// add table
								items.add(addDialog.getResult());
							}
							break;
						}
					}
				}
			}
			// edit
			else if (action.equals(PopupAction.EDIT)) {
				try {
					TreeMenu.defineInputAndOpenEditor(selectedLightXmlObject,
							EditorModeType.EDIT, PersistenceManager
									.getInstance().getWorkingResource(), null);
				} catch (DDIFtpException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// remove
			else if (action.equals(PopupAction.REMOVE)) {
				for (Iterator<ReferenceType> iterator = modelImpl.getDocument()
						.getSequence().getControlConstructReferenceList()
						.iterator(); iterator.hasNext();) {
					ReferenceType type = iterator.next();

					// remove from xml
					if (selectedLightXmlObject.getId().equals(
							new ReferenceResolution(type).getId())) {
						iterator.remove();

						// remove from table
						update = true;
						tableViewer.remove(selectedLightXmlObject);
						items.remove(selectedLightXmlObject);
						break;
					}
				}
			} else {
				log.warn("Action not specified!");
			}
		}

		// update
		if (update) {
			tableViewer.refresh(true);
			this.editorStatus.setChanged();
		}
	}

	/**
	 * Label provider
	 */
	public class SequenceLabelProvider extends LabelProvider implements
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
					// 0=id, 1=type, 2=label, 3=description
					Messages.getString("SequenceEditor.tabel.idlabel"),
					Messages.getString("SequenceEditor.tabel.typelabel"),
					Messages.getString("SequenceEditor.tabel.labellabel") };
			int[] widths = { 250, 200, 300 };
			for (int i = 0; i < titles.length; i++) {
				TableViewerColumn column = new TableViewerColumn(viewer,
						SWT.CENTER);
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
			LightXmlObjectType lightXmlObject = (LightXmlObjectType) element;
			LabelType label = null;

			switch (columnIndex) {
			// 0=id, 1=type, 2=label, 3=description
			case 0:
				return lightXmlObject.getId();
			case 1:
				return lightXmlObject.getElement();
			case 2:
				try {
					label = (LabelType) XmlBeansUtil.getLangElement(
							LanguageUtil.getDisplayLanguage(), lightXmlObject
									.getLabelList());
				} catch (DDIFtpException e) {
					showError(e);
				}
				return label == null ? "" : XmlBeansUtil
						.getTextOnMixedElement(label);
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
	public class SequenceTableContentProvider implements
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
			switch (column) {
			// 0=id, 1=type, 2=label, 3=description
			case 0:

				break;
			case 1:

				break;
			case 2:

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
			// 0=id, 1=type, 2=label, 3=description
			case 0:

				break;
			case 1:

				break;
			case 2:

				break;
			case 3:
				break;
			default:
				break;
			}
			return null;
		}

		@Override
		public void setValue(Object element, Object value) {
			switch (this.column) {
			case 0:

				break;
			case 1:

				break;
			case 2:

				break;
			case 3:
				break;
			default:
				break;
			}
			getViewer().update(element, null);
		}
	}

	// ------------------------------------------------------------------------
	// house keeping
	// ------------------------------------------------------------------------
	private final void showError(Exception e) {
		// TODO
		e.printStackTrace();
	}
}
