package org.ddialliance.ddieditor.ui.view.variable.questionrelation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.xmlbeans.XmlObject;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.UserIDType;
import org.ddialliance.ddieditor.logic.urn.ddi.ReferenceResolution;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.CustomListType;
import org.ddialliance.ddieditor.model.lightxmlobject.CustomType;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.model.namespace.ddi3.Ddi3NamespaceHelper;
import org.ddialliance.ddieditor.model.resource.DDIResourceType;
import org.ddialliance.ddieditor.persistenceaccess.PersistenceManager;
import org.ddialliance.ddieditor.ui.Activator;
import org.ddialliance.ddieditor.ui.dbxml.question.QuestionItemDao;
import org.ddialliance.ddieditor.ui.dbxml.variable.VariableDao;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddieditor.ui.editor.EditorInput;
import org.ddialliance.ddieditor.ui.editor.EditorInput.EditorModeType;
import org.ddialliance.ddieditor.ui.editor.widgetutil.lightxmlobjectdnd.LightXmlObjectDragListener;
import org.ddialliance.ddieditor.ui.editor.widgetutil.lightxmlobjectdnd.LightXmlObjectTransfer;
import org.ddialliance.ddieditor.ui.model.ElementType;
import org.ddialliance.ddieditor.ui.model.ModelIdentifingType;
import org.ddialliance.ddieditor.ui.model.question.QuestionItem;
import org.ddialliance.ddieditor.ui.model.variable.Variable;
import org.ddialliance.ddieditor.ui.util.swtdesigner.ResourceManager;
import org.ddialliance.ddieditor.ui.view.Messages;
import org.ddialliance.ddieditor.ui.view.TreeMenuProvider;
import org.ddialliance.ddieditor.ui.view.View;
import org.ddialliance.ddieditor.ui.view.XmlObjectComparer;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.xml.Urn;
import org.ddialliance.ddiftp.util.xml.XmlBeansUtil;
import org.eclipse.jface.action.Action;
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
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

public class VariableQuestionView extends ViewPart {
	public static final String ID = "org.ddialliance.ddieditor.ui.view.variable.questionrelation.VariableQuestionView";
	public static final String TABLE_VIEWER_FREE_ID = "free-question";
	public static final String TABLE_VIEWER_QUEI_VARI_REL_ID = "question-variable-relation";

	final String LABEL = "label";

	final String VARI_NAME = "Name";

	final String VARI_VAL_REP = "ValueRepresentation";

	final String VARI_QUEI_REF = "QuestionReference";

	final String QUEI_TEXT = "Text";

	final String QUEI_RESP_DOMA = "ResponseDomain";

	String selectedResource;

	private Combo combo;

	private List<VariableQuestionRelation> relItems = new ArrayList<VariableQuestionRelation>();

	List<LightXmlObjectType> freeItems = new ArrayList<LightXmlObjectType>();

	private TableViewer relTableViewer;

	private TableViewer freeTableViewer;

	@Override
	public void init(IViewSite site) throws PartInitException {
		super.init(site);
		// apply change
		IContributionItem applyChange = new ControlContribution("applyChange") {
			@Override
			protected Control createControl(Composite parent) {
				Button b = new Button(parent, 0);
				b.setText(Messages
						.getString("variablequestionview.button.applychange"));
				b.addSelectionListener(new SelectionListener() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						ReferenceResolution queiRef = null;
						for (VariableQuestionRelation variQueiRel : relItems) {
							queiRef = null;

							// check the values
							if (variQueiRel.quei != null) {
								queiRef = new ReferenceResolution(
										variQueiRel.quei);

								// vari name
								String variName = null;
								for (CustomType cus : getCustomListbyType(
										variQueiRel.vari, "Name")) {
									variName = XmlBeansUtil
											.getTextOnMixedElement(cus);
								}

								// vari id ~ quei userid pseudo vari id
								String variPseudoId = null;
								for (CustomType cus : getCustomListbyType(
										variQueiRel.quei, "UserID")) {
									if (cus.getOption()
											.equals(Ddi3NamespaceHelper.QUEI_VAR_USER_ID_TYPE)) {
										variPseudoId = XmlBeansUtil
												.getTextOnMixedElement(cus);
										if (!variName.equals(variPseudoId)) {
											setPseudoVariIdOnQuei(
													variQueiRel.quei, variName);
										} else {
											continue;
										}
									}
								}
								if (variPseudoId == null) {
									setPseudoVariIdOnQuei(variQueiRel.quei,
											variName);
								}
							}

							// set vari quei ref
							String queiId = null;
							for (CustomType cus : getCustomListbyType(
									variQueiRel.vari, "QuestionReference")) {
								queiId = XmlBeansUtil
										.getTextOnMixedElement(cus);
								break;
							}
							if (queiId == null && queiRef == null) {
								continue;
							} else if (queiId != null && queiRef == null) {
								deleteQueiRefOnVari(variQueiRel);
							} else if (queiId == null && queiRef != null) {
								setQueiRefOnVari(variQueiRel);
							} else if (!queiRef.getId().equals(queiId)) {
								setQueiRefOnVari(variQueiRel);
							}
						}
					}

					void setPseudoVariIdOnQuei(LightXmlObjectType quei,
							String variName) {
						QuestionItemDao dao = new QuestionItemDao();
						dao.setParentElementType(ElementType.QUESTION_SCHEME);
						QuestionItem model;
						try {
							model = dao.getModel(quei.getId(),
									quei.getVersion(), quei.getParentId(),
									quei.getParentVersion());
							if (model == null) {
								dao.setParentElementType(ElementType.MULTIPLE_QUESTION_ITEM);
								model = dao.getModel(quei.getId(),
										quei.getVersion(), quei.getParentId(),
										quei.getParentVersion());
							}

							UserIDType userId = null;
							for (UserIDType userIdTmp : model.getDocument()
									.getQuestionItem().getUserIDList()) {
								if (userIdTmp
										.getType()
										.equals(Ddi3NamespaceHelper.QUEI_VAR_USER_ID_TYPE)) {
									userId = userIdTmp;
									break;
								}
							}
							if (userId == null) {
								userId = model.getDocument().getQuestionItem()
										.addNewUserID();

								userId.setType(Ddi3NamespaceHelper.QUEI_VAR_USER_ID_TYPE);
							}
							userId.setStringValue(variName);
							dao.update(model);
							// TODO refresh open editor
						} catch (Exception e) {
							Editor.showError(e, variName, getSite());
						}
					}

					void setQueiRefOnVari(VariableQuestionRelation variQueiRel) {
						VariableDao dao = new VariableDao();
						try {
							Variable model = dao.getModel(
									variQueiRel.vari.getId(),
									variQueiRel.vari.getVersion(),
									variQueiRel.vari.getParentId(),
									variQueiRel.vari.getParentVersion());
							model.setCreate(true);
							model.executeChange(variQueiRel.quei,
									ModelIdentifingType.Type_A.class);
							dao.update(model);
							// TODO refresh open editor
						} catch (Exception e) {
							Editor.showError(e, ID, getSite());
						}
					}

					void deleteQueiRefOnVari(
							VariableQuestionRelation variQueiRel) {
						VariableDao dao = new VariableDao();
						try {
							Variable model = dao.getModel(
									variQueiRel.vari.getId(),
									variQueiRel.vari.getVersion(),
									variQueiRel.vari.getParentId(),
									variQueiRel.vari.getParentVersion());
							if (!model.getDocument().getVariable()
									.getQuestionReferenceList().isEmpty()) {
								model.getDocument().getVariable()
										.getQuestionReferenceList().remove(0);
								dao.update(model);
								// TODO refresh open editor
							}
						} catch (Exception e) {
							Editor.showError(e, ID, getSite());
						}
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
						loadInItems();
						// TODO load in items and reflect changes keeping done
						// edit!
						// 1 load items
						// 2 compare with local lists, implement changs [add,
						// remove]
					}

					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
						// do nothing
					}
				});
				return combo;
			}
		};

		// refresh
		Action zoomReset = new Action(
				Messages.getString("sequence.diagram.zoomreset")) { //$NON-NLS-1$)
			public void run() {
				loadInItems();
			}
		};
		zoomReset.setImageDescriptor(ResourceManager.getPluginImageDescriptor(
				Activator.getDefault(), "icons/refresh.gif"));

		// toolbar
		IToolBarManager toolbarManager = getViewSite().getActionBars()
				.getToolBarManager();
		toolbarManager.add(applyChange);
		toolbarManager.add(resourceChooser);
		toolbarManager.add(zoomReset);
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
			String[] options = new String[resources.size()];
			int count = 0;
			for (DDIResourceType ddiResource : resources) {
				options[count] = ddiResource.getOrgName();
				count++;
			}
			combo.setItems(options);
		} else {
			combo.setItems(new String[] { "" });
		}
	}

	private void loadInItems() {
		selectedResource = combo.getItem(combo.getSelectionIndex());
		if (selectedResource.equals("")) { // guard
			return;
		}

		// clean up the tables
		relItems.clear();

		try {
			// set working doc
			DdiManager.getInstance().setWorkingDocument(selectedResource);
		} catch (Exception e) {
			Editor.showError(e, ID, getSite());
		}

		// set items to load table
		try {
			// variable plus
			List<LightXmlObjectType> lightVari = DdiManager.getInstance()
					.getVariablesLightPlus(null, null, null, null)
					.getLightXmlObjectList().getLightXmlObjectList();

			// question plus
			List<LightXmlObjectType> lightQuei = DdiManager.getInstance()
					.getQuestionItemsLightPlus(null, null, null, null)
					.getLightXmlObjectList().getLightXmlObjectList();

			// variable -> question relation
			Urn queiRef = new Urn();
			for (Iterator<LightXmlObjectType> variIterator = lightVari
					.iterator(); variIterator.hasNext();) {
				LightXmlObjectType vari = (LightXmlObjectType) variIterator
						.next().copy();
				VariableQuestionRelation variQueiRel = new VariableQuestionRelation(
						vari, null);

				for (CustomListType cusList : vari.getCustomListList()) {
					if (cusList.getType().equals(VARI_QUEI_REF)) {
						// question ref
						for (CustomType cusQueiRef : cusList.getCustomList()) {
							if (cusQueiRef.getOption() == null
									&& cusQueiRef.getValue().equals("ID")) {
								queiRef.setContainedElementId(XmlBeansUtil
										.getTextOnMixedElement(cusQueiRef));
								break;
							}
						}
						break;
					}
				}

				// associate with question
				if (queiRef.getContainedElementId() != null) {
					for (Iterator<LightXmlObjectType> queiIterator = lightQuei
							.iterator(); queiIterator.hasNext();) {
						LightXmlObjectType quei = queiIterator.next();
						if (quei.getId()
								.equals(queiRef.getContainedElementId())) {
							variQueiRel.quei = (LightXmlObjectType) quei.copy();
							queiIterator.remove();
							break;
						}
					}
				}

				// finalize
				relItems.add(variQueiRel);
				variIterator.remove();
			}

			// questions user id -> variable id
			// note 'variable -> question relation' has precedence over
			// 'questions user id -> variable id'
			String variPseudoId = null;
			for (Iterator iterator = lightQuei.iterator(); iterator.hasNext();) {
				LightXmlObjectType quei = (LightXmlObjectType) iterator.next();
				// user id
				for (CustomType cus : getCustomListbyType(quei, "UserID")) {
					if (cus.getOption().equals(
							Ddi3NamespaceHelper.QUEI_VAR_USER_ID_TYPE)) {
						variPseudoId = XmlBeansUtil.getTextOnMixedElement(cus);
						if (variPseudoId != null) {
							for (VariableQuestionRelation variQueiRel : relItems) {
								if (variQueiRel.quei == null
										&& variQueiRel.vari.getId().equals(
												variPseudoId)) {
									variQueiRel.quei = quei;
									iterator.remove();
									break;
								}
							}
						}
					}
				}
			}

			// refresh
			freeItems = lightQuei;
			relTableViewer.refresh();
			freeTableViewer.refresh();
		} catch (Exception e2) {
			Editor.showError(e2, ID, getSite());
		}
	}

	@Override
	public void setFocus() {
		// do nothing
	}

	@Override
	public void createPartControl(Composite parent) {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		parent.setLayout(gridLayout);

		XmlObjectComparer xmlObjectComparer = new XmlObjectComparer();

		// question variable relation
		relTableViewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		relTableViewer.getTable().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
		relTableViewer
				.setContentProvider(new QuestionVariableContentProvider());
		QuestionVariableLabelProvider tableLabelProvider = new QuestionVariableLabelProvider();
		tableLabelProvider.createColumns(relTableViewer);
		relTableViewer.setLabelProvider(tableLabelProvider);
		relTableViewer.setInput(relItems);
		relTableViewer.setComparer(xmlObjectComparer);

		// free questions
		freeTableViewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		freeTableViewer.getTable().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
		freeTableViewer
				.setContentProvider(new FreeQuestionTableContentProvider());

		FreeQuestionLabelProvider freeLabelProvider = new FreeQuestionLabelProvider();
		freeLabelProvider.createColumns(freeTableViewer);
		freeTableViewer.setLabelProvider(freeLabelProvider);
		freeTableViewer.setInput(freeItems);
		freeTableViewer.setComparer(xmlObjectComparer);

		// dnd
		int operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT;
		Transfer[] freeTransferTypes = new Transfer[] { LightXmlObjectTransfer
				.getInstance() };
		Transfer[] variQueiRelTransferTypes = new Transfer[] { VariableQuestionRelationTransfer
				.getInstance() };

		// drag question variable relation
		relTableViewer.addDragSupport(operations, variQueiRelTransferTypes,
				new VariableQuestionDragListener(relTableViewer,
						TABLE_VIEWER_QUEI_VARI_REL_ID));

		// drop question variable relation
		relTableViewer.addDropSupport(operations, freeTransferTypes,
				new VariableQuestionRelationDropListener(relTableViewer,
						freeTableViewer));

		// drag
		freeTableViewer.addDragSupport(operations, freeTransferTypes,
				new LightXmlObjectDragListener(freeTableViewer,
						TABLE_VIEWER_FREE_ID));

		freeTableViewer.addDropSupport(operations, variQueiRelTransferTypes,
				new VariableQuestionFreeDropListener(freeTableViewer,
						relTableViewer));
	}

	/**
	 * Label provider
	 */
	public class QuestionVariableLabelProvider extends LabelProvider implements
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
					// 0=vari, 1=quei
					Messages.getString("variablequestionview.column.vari"),
					Messages.getString("variablequestionview.column.quei") };
			int[] widths = { 200, 375 };
			int[] style = { SWT.LEFT, SWT.LEFT };
			for (int i = 0; i < titles.length; i++) {
				TableViewerColumn column = new TableViewerColumn(viewer,
						style[i]);
				column.getColumn().setText(titles[i]);
				column.getColumn().setWidth(widths[i]);
				column.getColumn().setResizable(true);
				// column.setEditingSupport(new TableEditingSupport(viewer, i));
			}
			table.setHeaderVisible(true);
			table.setLinesVisible(true);
			table.pack();

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
				public void mouseDoubleClick(MouseEvent e) {
					TableItem[] items = ((Table) e.getSource()).getSelection();
					for (int i = 0; i < items.length; i++) {
						System.out.println(items[i]);
						VariableQuestionRelation variQueirel = (VariableQuestionRelation) items[i]
								.getData();

						if (variQueirel.quei!=null) {
							TreeMenuProvider.defineInputAndOpenEditor(
									ElementType.QUESTION_ITEM,
									ElementType.QUESTION_SCHEME,
									variQueirel.quei, EditorModeType.EDIT,
									selectedResource, null);
						}
					}
				}
			});
		}

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			VariableQuestionRelation variQueiRel = (VariableQuestionRelation) element;
			String[] result = new String[3];

			switch (columnIndex) {
			// 0=vari, 1=quei
			case 0:
				// name
				for (CustomType cus : getCustomListbyType(variQueiRel.vari,
						VARI_NAME)) {
					result[0] = XmlBeansUtil.getTextOnMixedElement(cus);
				}

				// label
				try {
					result[1] = XmlBeansUtil
							.getTextOnMixedElement((XmlObject) XmlBeansUtil
									.getDefaultLangElement(variQueiRel.vari
											.getLabelList()));

				} catch (Exception e) {
					Editor.showError(e, ID, getSite());
				}

				// value rep
				for (CustomType cus : getCustomListbyType(variQueiRel.vari,
						VARI_VAL_REP)) {
					result[2] = XmlBeansUtil.getTextOnMixedElement(cus);
				}
				break;

			case 1:
				if (variQueiRel.quei != null) {
					// name
					try {
						result[0] = XmlBeansUtil
								.getTextOnMixedElement((XmlObject) XmlBeansUtil
										.getDefaultLangElement(variQueiRel.quei
												.getLabelList()));

					} catch (Exception e) {
						Editor.showError(e, ID, getSite());
					}

					// text
					for (CustomType cus : getCustomListbyType(variQueiRel.quei,
							QUEI_TEXT)) {
						result[1] = XmlBeansUtil.getTextOnMixedElement(cus);
					}

					// response domain
					for (CustomType cus : getCustomListbyType(variQueiRel.quei,
							QUEI_RESP_DOMA)) {
						result[2] = XmlBeansUtil.getTextOnMixedElement(cus);
					}
				}
				break;
			default:
				DDIFtpException e = new DDIFtpException(
						Messages.getString("translationdialog.error.columnindexnotfound"), new Throwable()); //$NON-NLS-1$
				Editor.showError(e, ID, getSite());
				break;
			}
			StringBuilder str = new StringBuilder();
			for (int i = 0; i < result.length; i++) {
				if (result[i] == null) {
					str.append("");
				} else
					str.append(result[i]);
				str.append("\n");
			}
			return str.toString();
		}
	}

	private List<CustomType> getCustomListbyType(
			LightXmlObjectType lightXmlObject, String type) {
		for (CustomListType cuslist : lightXmlObject.getCustomListList()) {
			if (cuslist.getType().equals(type)) {
				return cuslist.getCustomList();
			}
		}
		return Arrays.asList(CustomType.Factory.newInstance());
	}

	/**
	 * Content provider
	 */
	public class QuestionVariableContentProvider implements
			IStructuredContentProvider {
		@Override
		public Object[] getElements(Object parent) {
			return relItems.toArray();
		}

		public List getItems() {
			return relItems;
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

	public class FreeQuestionLabelProvider extends LabelProvider implements
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
			// 0=quei
			Messages.getString("variablequestionview.column.quei") };
			int[] widths = { 375 };
			int[] style = { SWT.LEFT };
			for (int i = 0; i < titles.length; i++) {
				TableViewerColumn column = new TableViewerColumn(viewer,
						style[i]);
				column.getColumn().setText(titles[i]);
				column.getColumn().setWidth(widths[i]);
				column.getColumn().setResizable(true);
				// column.setEditingSupport(new TableEditingSupport(viewer, i));
			}
			table.setHeaderVisible(true);
			table.setLinesVisible(true);
			table.pack();

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
				public void mouseDoubleClick(MouseEvent e) {
					TableItem[] items = ((Table) e.getSource()).getSelection();
					for (int i = 0; i < items.length; i++) {
						LightXmlObjectType lightXmlObject = (LightXmlObjectType) items[i]
								.getData();

						TreeMenuProvider.defineInputAndOpenEditor(
								ElementType.QUESTION_ITEM,
								ElementType.QUESTION_SCHEME, lightXmlObject,
								EditorModeType.EDIT, selectedResource, null);
					}
				}
			});
		}

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			LightXmlObjectType variQueiRel = (LightXmlObjectType) element;
			String[] result = new String[3];

			switch (columnIndex) {
			// 0=quei
			case 0:
				if (variQueiRel != null) {
					// name
					try {
						result[0] = XmlBeansUtil
								.getTextOnMixedElement((XmlObject) XmlBeansUtil
										.getDefaultLangElement(variQueiRel
												.getLabelList()));

					} catch (Exception e) {
						Editor.showError(e, ID, getSite());
					}

					// text
					for (CustomType cus : getCustomListbyType(variQueiRel,
							QUEI_TEXT)) {
						result[1] = XmlBeansUtil.getTextOnMixedElement(cus);
					}

					// response domain
					for (CustomType cus : getCustomListbyType(variQueiRel,
							QUEI_RESP_DOMA)) {
						result[2] = XmlBeansUtil.getTextOnMixedElement(cus);
					}
				}
				break;
			default:
				DDIFtpException e = new DDIFtpException(
						Messages.getString("translationdialog.error.columnindexnotfound"), new Throwable()); //$NON-NLS-1$
				Editor.showError(e, ID, getSite());
				break;
			}
			StringBuilder str = new StringBuilder();
			for (int i = 0; i < result.length; i++) {
				if (result[i] == null) {
					str.append("");
				} else
					str.append(result[i]);
				str.append("\n");
			}
			return str.toString();
		}
	}

	public class FreeQuestionTableContentProvider implements
			IStructuredContentProvider {
		@Override
		public Object[] getElements(Object parent) {
			return freeItems.toArray();
		}

		public List<LightXmlObjectType> getItems() {
			return freeItems;
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
