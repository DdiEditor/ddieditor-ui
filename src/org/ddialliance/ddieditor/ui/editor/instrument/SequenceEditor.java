package org.ddialliance.ddieditor.ui.editor.instrument;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ddialliance.ddi3.xml.xmlbeans.reusable.ReferenceType;
import org.ddialliance.ddieditor.logic.urn.ddi.ReferenceResolution;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LabelType;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.persistenceaccess.PersistenceManager;
import org.ddialliance.ddieditor.ui.Activator;
import org.ddialliance.ddieditor.ui.dbxml.instrument.SequenceDao;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddieditor.ui.editor.EditorInput;
import org.ddialliance.ddieditor.ui.editor.EditorInput.EditorModeType;
import org.ddialliance.ddieditor.ui.editor.widgetutil.lightxmlobjectdnd.LightXmlObjectDragListener;
import org.ddialliance.ddieditor.ui.editor.widgetutil.lightxmlobjectdnd.LightXmlObjectTransfer;
import org.ddialliance.ddieditor.ui.model.ElementType;
import org.ddialliance.ddieditor.ui.model.instrument.Sequence;
import org.ddialliance.ddieditor.ui.perspective.IAutoChangePerspective;
import org.ddialliance.ddieditor.ui.util.DialogUtil;
import org.ddialliance.ddieditor.ui.util.LanguageUtil;
import org.ddialliance.ddieditor.ui.util.swtdesigner.ResourceManager;
import org.ddialliance.ddieditor.ui.view.Messages;
import org.ddialliance.ddieditor.ui.view.TreeMenu;
import org.ddialliance.ddieditor.ui.view.TreeMenuProvider;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.ddialliance.ddiftp.util.xml.XmlBeansUtil;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
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
	SequenceLabelProvider tableLabelProvider;
	private List items = new ArrayList();

	private enum PopupAction {
		ADD, EDIT, REMOVE
	};

	public SequenceEditor() {
		super(Messages.getString("SequenceEditor.label"), Messages
				.getString("SequenceEditor.description"));
		this.dao = new SequenceDao();
	}

	public Viewer getViewer() {
		return tableViewer;
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
		modelImpl = (Sequence) model;

		try {
			controlConstructMap = DdiManager.getInstance()
					.getControlConstructsLightasMap();
		} catch (Exception e) {
			showError(e);
		}

		// items to feed table
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
	public void createPartControl(final Composite parent) {
		super.createPartControl(parent);

		// table viewer, table, table label provider, table content label
		// provider
		tableViewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		tableViewer.getTable().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));
		tableViewer.setContentProvider(new SequenceTableContentProvider());
		tableLabelProvider = new SequenceLabelProvider();
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
				new SequenceDropListener(this));

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

		// menu edit
		MenuItem editMenuItem = new MenuItem(menu, SWT.NONE);
		editMenuItem
				.setText(Messages.getString("View.label.editMenuItem.Edit"));
		editMenuItem.setImage(ResourceManager.getPluginImage(
				Activator.getDefault(), "icons/editor_area.gif"));
		editMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				popupMenuAction(PopupAction.EDIT);
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
		tableViewer.getControl().setMenu(menu);

		// properties
		Group pGroup = createGroup(parent,
				Messages.getString("SequenceEditor.properties"));

		Label label = createLabel(pGroup,
				Messages.getString("SequenceEditor.properties"));
		label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1,
				1));
		Button button = new Button(pGroup, 0);
		button.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		button.setText(Messages.getString("SequenceEditor.properties"));
		button.addSelectionListener(new PropertiesEditorListener(this));

		// clear unwanted changes
		editorStatus.clearChanged();
	}

	class PropertiesEditorListener implements SelectionListener {
		Editor editor;

		PropertiesEditorListener(Editor editor) {
			this.editor = editor;
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			SequencePropertiesDialog spd = new SequencePropertiesDialog(editor
					.getSite().getShell(), Messages.getString("Sequence"),
					editor);
			spd.open();
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			// do nothing
		}
	}

	// ------------------------------------------------------------------------
	// table support
	// ------------------------------------------------------------------------
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

			// add
			if (action.equals(PopupAction.ADD)) {
				List<LightXmlObjectType> controlConstructRefList = new ArrayList<LightXmlObjectType>();
				try {
					controlConstructRefList = DdiManager.getInstance()
							.getControlConstructsLight();
				} catch (Exception e) {
					DialogUtil.errorDialog(getSite().getShell(), ID, null,
							e.getMessage(), e);
				}
				for (Iterator<LightXmlObjectType> iterator = controlConstructRefList
						.iterator(); iterator.hasNext();) {
					LightXmlObjectType lightXmlObjectType = iterator.next();
					for (LightXmlObjectType item : (Collection<LightXmlObjectType>) items) {
						if (lightXmlObjectType.getId().equals(item.getId())) {
							try {
								iterator.remove();
							} catch (IllegalStateException e) {
								continue; // hack to prevent exception on
											// duplicate control construct
											// in list
							}
							continue; // guard to not remove it twice in
							// case of duplicates
						}
					}
				}

				// labelQueryResult = null;
				SequenceMenuPopupAddDialog addDialog = new SequenceMenuPopupAddDialog(
						getSite().getShell(),
						Messages.getString("SequenceEditor.adddialog.title"),
						Messages.getString("SequenceEditor.adddialog.label"),
						controlConstructRefList, modelImpl);
				addDialog.open();

				if (addDialog.getResult() != null) {
					int count = 0; // count is before
					for (Iterator<LightXmlObjectType> iterator = items
							.iterator(); iterator.hasNext(); count++) {
						LightXmlObjectType lightXmlObject = iterator.next();
						if (lightXmlObject.equals(selectedLightXmlObject)) {
							update = true;
							int insert = addDialog.beforeAfter == 0 ? count + 1
									: count;

							if (insert < items.size()) {
								// add xml
								modelImpl
										.getDocument()
										.getSequence()
										.getControlConstructReferenceList()
										.add(insert,
												new ReferenceResolution(
														addDialog.getResult())
														.getReference());

								// add table
								items.add(insert, addDialog.getResult());
							} else {
								// add xml
								modelImpl
										.getDocument()
										.getSequence()
										.getControlConstructReferenceList()
										.add(new ReferenceResolution(addDialog
												.getResult()).getReference());

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
					TreeMenu.defineInputAndOpenEditor(null, null,
							selectedLightXmlObject, EditorModeType.EDIT,
							PersistenceManager.getInstance()
									.getWorkingResource(), null);
				} catch (DDIFtpException e) {
					showError(e);
				}
			}
			// remove
			else if (action.equals(PopupAction.REMOVE)) {
				int count = 0;
				for (Iterator<ReferenceType> iterator = modelImpl.getDocument()
						.getSequence().getControlConstructReferenceList()
						.iterator(); iterator.hasNext(); count++) {
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
					// 0=type, 1=label, 2=id
					Messages.getString("SequenceEditor.tabel.typelabel"),
					Messages.getString("SequenceEditor.tabel.labellabel"),
					Messages.getString("SequenceEditor.tabel.idlabel") };
			int[] widths = { 200, 375, 225 };
			int[] style = { SWT.RIGHT, SWT.LEFT, SWT.LEFT };
			for (int i = 0; i < titles.length; i++) {
				TableViewerColumn column = new TableViewerColumn(viewer,
						style[i]);
				column.getColumn().setText(titles[i]);
				column.getColumn().setWidth(widths[i]);
				column.getColumn().setResizable(true);
			}

			final String resourceid = ((EditorInput) getEditorInput())
					.getResourceId();
			table.addMouseListener(new MouseListener() {
				@Override
				public void mouseUp(MouseEvent e) {
					// do nothing
				}

				@Override
				public void mouseDown(MouseEvent e) {
					// do noting
				}

				@Override
				public void mouseDoubleClick(MouseEvent e) {
					TableItem[] items = ((Table) e.getSource()).getSelection();
					for (int i = 0; i < items.length; i++) {
						LightXmlObjectType lightXmlObject = (LightXmlObjectType) items[i]
								.getData();
						try {
							TreeMenuProvider.defineInputAndOpenEditor(
									ElementType.getElementType(lightXmlObject
											.getElement()),
									ElementType.CONCEPT_SCHEME, lightXmlObject,
									EditorModeType.EDIT, resourceid, null);
						} catch (DDIFtpException e1) {
							showError(e1);
						}
					}
				}
			});
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
			// 0=type, 1=label, 2=id
			case 2:
				return lightXmlObject.getId();
			case 0:
				return lightXmlObject.getElement();
			case 1:
				try {
					label = (LabelType) XmlBeansUtil.getLangElement(
							LanguageUtil.getDisplayLanguage(),
							lightXmlObject.getLabelList());
				} catch (DDIFtpException e) {
					showError(e);
				}
				return label == null ? "" : XmlBeansUtil
						.getTextOnMixedElement(label);
			default:
				DDIFtpException e = new DDIFtpException(
						Messages.getString("translationdialog.error.columnindexnotfound"), new Throwable()); //$NON-NLS-1$
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
}
