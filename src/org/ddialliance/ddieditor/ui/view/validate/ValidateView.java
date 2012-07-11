package org.ddialliance.ddieditor.ui.view.validate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.xmlbeans.XmlObject;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectListDocument;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.model.marker.MarkerDocument.Marker;
import org.ddialliance.ddieditor.model.marker.MarkerListDocument;
import org.ddialliance.ddieditor.model.marker.ResourceDocument.Resource;
import org.ddialliance.ddieditor.model.resource.DDIResourceType;
import org.ddialliance.ddieditor.persistenceaccess.PersistenceManager;
import org.ddialliance.ddieditor.ui.Activator;
import org.ddialliance.ddieditor.ui.command.ValidateDDI3;
import org.ddialliance.ddieditor.ui.dialogs.DisplayNoteDialog;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddieditor.ui.editor.EditorInput.EditorModeType;
import org.ddialliance.ddieditor.ui.editor.widgetutil.table.TableColumnSort;
import org.ddialliance.ddieditor.ui.model.ElementType;
import org.ddialliance.ddieditor.ui.util.DialogUtil;
import org.ddialliance.ddieditor.ui.util.swtdesigner.ResourceManager;
import org.ddialliance.ddieditor.ui.view.TreeMenu;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.Translator;
import org.ddialliance.ddiftp.util.xml.XmlBeansUtil;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

public class ValidateView extends ViewPart {
	public static final String ID = "org.ddialliance.ddieditor.ui.view.validate.ValidateView";
	private MarkerListDocument markerListDoc;
	private TableViewer markersTableViewer;
	private MarkersTableContentProvider markersTableContentProvider;

	public Combo combo;

	@Override
	public void init(IViewSite site) throws PartInitException {
		super.init(site);

		// validate
		IContributionItem validate = new ControlContribution("validate") {
			@Override
			protected Control createControl(Composite parent) {
				Button b = new Button(parent, 0);
				b.setText(Translator.trans("validate"));
				b.addSelectionListener(new SelectionListener() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						// validate
						String selectedResource = null;
						if (combo != null) {
							selectedResource = combo.getItem(combo
									.getSelectionIndex());
						}
						if (selectedResource.equals("")) { // guard
							return;
						}

						ValidateDDI3 validateDDI3 = new ValidateDDI3();
						ValidateDDI3.ValidateJob validateJob = validateDDI3.new ValidateJob(
								selectedResource);
						validateJob.run();
					}

					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
						// do nothing
					}
				});
				return b;
			}
		};

		// combo
		IContributionItem resourceChooser = new ControlContribution("id") {
			@Override
			protected Control createControl(Composite parent) {
				combo = new Combo(parent, SWT.READ_ONLY);
				combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
						false, 1, 1));
				try {
					intCombo();
				} catch (Exception e) {
					Editor.showError(e, ID, getSite());
				}

				// listeners
				combo.addFocusListener(new FocusListener() {
					@Override
					public void focusLost(FocusEvent e) {
						// do nothing
					}

					@Override
					public void focusGained(FocusEvent e) {
						try {
							intCombo();
						} catch (Exception e1) {
							Editor.showError(e1, ID, getSite());
						}
					}
				});

				combo.addSelectionListener(new SelectionListener() {
					@Override
					public void widgetSelected(SelectionEvent e) {

					}

					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
						// do nothing
					}
				});
				return combo;
			}
		};

		// toolbar
		IToolBarManager toolbarManager = getViewSite().getActionBars()
				.getToolBarManager();
		toolbarManager.add(validate);
		toolbarManager.add(resourceChooser);
	}

	private final void intCombo() throws Exception {
		// delete old value
		combo.setItems(new String[] {});
		combo.setData(null);

		// read
		List<DDIResourceType> resources = PersistenceManager.getInstance()
				.getResources();
		if (!resources.isEmpty()) {
			// define combo options
			String[] options = new String[resources.size() + 1];
			options[0] = "";
			int count = 1;
			for (DDIResourceType ddiResource : resources) {
				options[count] = ddiResource.getOrgName();
				count++;
			}
			combo.setItems(options);
		} else {
			combo.setItems(new String[] { "" });
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		// table viewer
		markersTableViewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		markersTableViewer.getTable().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2));

		// content provider
		markersTableContentProvider = new MarkersTableContentProvider();
		markersTableViewer.setContentProvider(markersTableContentProvider);

		// label provider
		MarkesLabelProvider markersLabelProvider = new MarkesLabelProvider();
		markersLabelProvider.createColumns(markersTableViewer);
		markersTableViewer.setLabelProvider(markersLabelProvider);
	}

	@Override
	public void setFocus() {
		// do nothing
	}

	public void refresh(MarkerListDocument markerListDoc) {
		this.markerListDoc = markerListDoc;
		markersTableViewer.setInput(markerListDoc);
		markersTableViewer.refresh();
	}

	public class MarkesLabelProvider extends LabelProvider implements
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
					// 0
					Translator.trans("validate.table.element"),
					// 1
					Translator.trans("validate.table.type"),
					// 2
					Translator.trans("validate.table.state"),
					// 3
					Translator.trans("validate.table.description"),
					// 4
					Translator.trans("validate.table.resource"),
					// 5
					Translator.trans("validate.table.position"),
					// 6
					Translator.trans("validate.table.note"), };

			int[] widths = { 125, 125, 75, 550, 150, 250, 50 };
			int[] style = { SWT.CENTER, SWT.CENTER, SWT.CENTER, SWT.LEFT,
					SWT.CENTER, SWT.LEFT, SWT.CENTER, };

			TableColumnSort sort = new TableColumnSort(table);
			for (int i = 0; i < titles.length; i++) {
				TableViewerColumn column = new TableViewerColumn(viewer,
						style[i]);
				column.getColumn().setText(titles[i]);
				column.getColumn().setWidth(widths[i]);
				column.getColumn().setResizable(true);
				column.getColumn().addListener(SWT.Selection, sort);
			}

			// popup menu
			final Menu menu = new Menu(markersTableViewer.getControl());

			// menu edit element
			MenuItem removeMenuItem = new MenuItem(menu, SWT.NONE);
			removeMenuItem.setText(Translator
					.trans("validate.table.popupmenueditelement"));
			removeMenuItem.setImage(ResourceManager.getPluginImage(
					Activator.getDefault(), "icons/edit_obj.gif"));
			removeMenuItem.setSelection(true);

			removeMenuItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(final SelectionEvent event) {
					try {
						popupMenuAction(PopupAction.EDIT_ELEMENT);
					} catch (DDIFtpException e) {
						Editor.showError(e, ID);
					}
				}
			});

			// mouse listener
			table.addMouseListener(new MouseListener() {
				@Override
				public void mouseUp(MouseEvent e) {
					// do nothing
				}

				@Override
				public void mouseDown(MouseEvent e) {
					// do nothing
				}

				@Override
				public void mouseDoubleClick(MouseEvent event) {
					TableItem[] tableItems = ((Table) event.getSource())
							.getSelection();
					for (int i = 0; i < tableItems.length; i++) {
						MarkerItem selected = (MarkerItem) tableItems[i]
								.getData();
						try {
							openEditor(selected);
						} catch (DDIFtpException e) {
							Editor.showError(e, ID);
						}
					}
				}
			});

			// finalize
			markersTableViewer.getControl().setMenu(menu);
			table.setHeaderVisible(true);
			table.setLinesVisible(true);
			Editor.resizeTableFont(table);
		}

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			MarkerItem marker = (MarkerItem) element;

			try {
				switch (columnIndex) {
				// element
				case 0:
					return XmlBeansUtil
							.getTextOnMixedElement((XmlObject) XmlBeansUtil
									.getDefaultLangElement(marker.marker
											.getLabelList()));

					// type
				case 1:
					return marker.marker.getType().toString();

					// state
				case 2:
					return marker.marker.getState().toString();

					// description
				case 3:
					String description = XmlBeansUtil
							.getTextOnMixedElement((XmlObject) XmlBeansUtil
									.getDefaultLangElement(marker.marker
											.getDescriptionList()));
					description = description.replace("cvc-complex-", "");
					return description;

					// resource
				case 4:
					return marker.resource;

					// position
				case 5:
					if (marker.marker.getPosition() == null) { // guard
						break;
					}

					// xpath
					if (marker.marker.getPosition().getXPath() != null) {
						return XmlBeansUtil.getTextOnMixedElement(marker.marker
								.getPosition().getXPath());
					}

					// light xml object
					if (marker.marker.getPosition().getLightXmlObject() != null) {
						LightXmlObjectType lightXmlObject = marker.marker
								.getPosition().getLightXmlObject();
						return XmlBeansUtil
								.getTextOnMixedElement((XmlObject) XmlBeansUtil
										.getDefaultLangElement(lightXmlObject
												.getLabelList()));
					}

					break;

				// 6 note
				case 6:
					return "0";

				default:
					DDIFtpException e = new DDIFtpException(
							Translator
									.trans("translationdialog.error.columnindexnotfound"), new Throwable()); //$NON-NLS-1$
					Editor.showError(e, ID, getSite());
					break;
				}
			} catch (DDIFtpException e) {
				Editor.showError(e, ID, getSite());
			}

			return "";
		}
	}

	public class MarkersTableContentProvider implements
			IStructuredContentProvider {
		@Override
		public Object[] getElements(Object parent) {
			List<MarkerItem> markerItems = new ArrayList<ValidateView.MarkerItem>();
			for (Resource resource : markerListDoc.getMarkerList()
					.getResourceList()) {
				for (Marker marker : resource.getMarkerList()) {
					markerItems.add(new MarkerItem(resource.getId(), marker));
				}
			}
			return markerItems.toArray(new MarkerItem[] {});
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

	class MarkerItem {
		String resource;
		Marker marker;

		public MarkerItem(String resource, Marker marker) {
			super();
			this.resource = resource;
			this.marker = marker;
		}
	}

	private enum PopupAction {
		EDIT_ELEMENT
	};

	private void popupMenuAction(PopupAction action) throws DDIFtpException {
		TableItem[] tableItems = markersTableViewer.getTable().getSelection();
		// guard
		if (tableItems.length <= 0) {
			DialogUtil.errorDialog(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell(), ID, null, null,
					new DDIFtpException());
			return;
		}

		for (int i = 0; i < tableItems.length; i++) {
			MarkerItem selected = (MarkerItem) tableItems[i].getData();

			// edit element
			if (action.equals(PopupAction.EDIT_ELEMENT)) {
				openEditor(selected);
			}
		}
	}

	private void openEditor(MarkerItem marker) throws DDIFtpException {
		if (marker.marker.getPosition() == null
				&& !marker.marker.getLabelList().isEmpty()) { // guard
			return;
		}

		// xpath
		if (marker.marker.getPosition().getXPath() != null) {
			String markerPath = XmlBeansUtil
					.getTextOnMixedElement(marker.marker.getPosition()
							.getXPath());
			// code
			if (XmlBeansUtil.getTextOnMixedElement(
					marker.marker.getLabelList().get(0)).equals(
					ElementType.CODE.getElementName())) {

				// define xpath
				int index = markerPath.indexOf("CodeScheme'][");

				StringBuilder xpath = new StringBuilder();
				xpath.append(markerPath.substring(0, index + 13));
				xpath.append(markerPath.substring(index + 13,
						markerPath.indexOf("]", index + 13)));
				xpath.append("]");

				// query light xml object
				LightXmlObjectListDocument lightXmlObjectList = DdiManager
						.getInstance().queryLightXmlByXpath("LogicalProduct",
								"CodeScheme", null, "reusable__Label", null,
								null, xpath.toString());

				// open editor
				for (LightXmlObjectType lightXmlObject : lightXmlObjectList
						.getLightXmlObjectList().getLightXmlObjectList()) {
					// TODO remove hard code element type
					TreeMenu.defineInputAndOpenEditor(null,
							ElementType.CODE_SCHEME, lightXmlObject,
							EditorModeType.EDIT, marker.resource, null);
				}
				return;
			} else {
				// not supported xpath alert
				Map<String, String> items = new HashMap<String, String>();
				items.put(Translator.trans("validate.marker.description"),
						XmlBeansUtil.getTextOnMixedElement(marker.marker
								.getDescriptionArray(0)));
				items.put(Translator.trans("validate.xpathvoid.label"),
						markerPath);

				DisplayNoteDialog dialog = new DisplayNoteDialog(PlatformUI
						.getWorkbench().getDisplay().getActiveShell(),
						Translator.trans("validate.xpathvoid.title"),
						Translator.trans("validate.autoopen.notsupported"),
						items);
				dialog.open();

				return;
			}
		}

		// light xml object
		if (marker.marker.getPosition().getLightXmlObject() != null) {
			LightXmlObjectType lightXmlObject = marker.marker.getPosition()
					.getLightXmlObject();

			// open editor
			// TODO remove hard code element type
			TreeMenu.defineInputAndOpenEditor(null,
					ElementType.QUESTION_SCHEME, lightXmlObject,
					EditorModeType.EDIT, marker.resource, null);
		}
	}
}
