package org.ddialliance.ddieditor.ui.view.variable.questionrelation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.xmlbeans.XmlObject;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.CodeDomainType;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.DateTimeDomainType;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.NumericDomainType;
import org.ddialliance.ddi3.xml.xmlbeans.logicalproduct.CodeRepresentationType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.DateTimeRepresentationType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.ExternalCategoryRepresentationType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.NumericRepresentationType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.ReferenceType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.TextDomainType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.TextRepresentationType;
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
import org.ddialliance.ddieditor.ui.editor.EditorInput.EditorModeType;
import org.ddialliance.ddieditor.ui.editor.widgetutil.lightxmlobjectdnd.LightXmlObjectDragListener;
import org.ddialliance.ddieditor.ui.editor.widgetutil.lightxmlobjectdnd.LightXmlObjectTransfer;
import org.ddialliance.ddieditor.ui.model.ElementType;
import org.ddialliance.ddieditor.ui.model.ModelIdentifingType;
import org.ddialliance.ddieditor.ui.model.question.QuestionItem;
import org.ddialliance.ddieditor.ui.model.question.ResponseType;
import org.ddialliance.ddieditor.ui.model.variable.Variable;
import org.ddialliance.ddieditor.ui.util.DialogUtil;
import org.ddialliance.ddieditor.ui.util.swtdesigner.ResourceManager;
import org.ddialliance.ddieditor.ui.view.TreeMenu;
import org.ddialliance.ddieditor.ui.view.TreeMenuProvider;
import org.ddialliance.ddieditor.ui.view.XmlObjectComparer;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.Translator;
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
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

public class VariableQuestionView extends ViewPart implements IPropertyListener {
	// TODO separate logic code into model layer !!!
	public static final String ID = "org.ddialliance.ddieditor.ui.view.variable.questionrelation.VariableQuestionView";
	public static final String TABLE_VIEWER_FREE_ID = "free-question";
	public static final String TABLE_VIEWER_QUEI_VARI_REL_ID = "question-variable-relation";

	final String LABEL = "label";

	final String VARI_NAME = "Name";

	final String VARI_VAL_REP = "ValueRepresentation";

	final String VARI_QUEI_REF = "QuestionReference";

	final String QUEI_TEXT = "Text";

	final String QUEI_RESP_DOMA = "ResponseDomain";

	public String selectedResource;

	private Composite parent;

	private Combo combo;

	private List<VariableQuestionRelation> relItems = new ArrayList<VariableQuestionRelation>();

	private List<LightXmlObjectType> freeItems = new ArrayList<LightXmlObjectType>();

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
				b.setText(Translator
						.trans("variablequestionview.button.applychange"));
				b.addSelectionListener(new SelectionListener() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						Thread longRunningJob = new Thread(new ApplyChangeJob());
						BusyIndicator.showWhile(PlatformUI.getWorkbench()
								.getDisplay(), longRunningJob);
						// TODO vari.cods to quei.cats create relation

						// refresh
						loadInItems();
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
						// 2 compare with local lists, implement changes [add,
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
				Translator.trans("sequence.diagram.zoomreset")) { //$NON-NLS-1$)
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

	QuestionItem getQuestionItem(LightXmlObjectType quei) throws Exception {
		QuestionItemDao dao = new QuestionItemDao();
		dao.setParentElementType(ElementType.QUESTION_SCHEME);
		QuestionItem model = null;
		model = dao.getModel(quei.getId(), quei.getVersion(),
				quei.getParentId(), quei.getParentVersion());
		if (model == null) {
			dao.setParentElementType(ElementType.MULTIPLE_QUESTION_ITEM);
			model = dao.getModel(quei.getId(), quei.getVersion(),
					quei.getParentId(), quei.getParentVersion());
		}
		return model;
	}

	/**
	 * Relates a question item to a variable via custom user id on the question
	 * item set as the variable name
	 * 
	 * @param quei
	 *            question item
	 * @param variName
	 *            variable name
	 * @return the changed question item
	 */
	QuestionItem setPseudoVariIdOnQuei(LightXmlObjectType quei, String variName) {
		QuestionItem model = null;
		try {
			model = getQuestionItem(quei);

			UserIDType userId = null;
			for (UserIDType userIdTmp : model.getDocument().getQuestionItem()
					.getUserIDList()) {
				if (userIdTmp.getType().equals(
						Ddi3NamespaceHelper.QUEI_VAR_USER_ID_TYPE)) {
					userId = userIdTmp;
					break;
				}
			}
			if (userId == null) {
				userId = model.getDocument().getQuestionItem().addNewUserID();

				userId.setType(Ddi3NamespaceHelper.QUEI_VAR_USER_ID_TYPE);
			}
			userId.setStringValue(variName);
			new QuestionItemDao().update(model);
			// TODO refresh open editor

			return model;
		} catch (Exception e) {
			Editor.showError(e, variName, getSite());
		}
		return model;
	}

	private void setConcepts(Variable vari, QuestionItem quei)
			throws DDIFtpException {
		if (quei.getConceptReferenceType() != null) {
			vari.getDocument().getVariable()
					.setConceptReference(quei.getConceptReferenceType());
			new VariableDao().update(vari);
		}
	}

	Variable getVariable(VariableQuestionRelation variQueiRel) throws Exception {

		VariableDao dao = new VariableDao();
		Variable model = null;
		model = dao.getModel(variQueiRel.vari.getId(),
				variQueiRel.vari.getVersion(), variQueiRel.vari.getParentId(),
				variQueiRel.vari.getParentVersion());
		return model;
	}

	/**
	 * Relates a variable to a question item via the variable question reference
	 * tag
	 * 
	 * @param variQueiRel
	 *            containing the variable and question item
	 * @return variable
	 */
	Variable setQueiRefOnVari(VariableQuestionRelation variQueiRel) {
		Variable model = null;
		try {
			model = getVariable(variQueiRel);
			model.setCreate(true);
			model.executeChange(variQueiRel.quei,
					ModelIdentifingType.Type_A.class);
			new VariableDao().update(model);
			// TODO refresh open editor
		} catch (Exception e) {
			Editor.showError(e, ID, getSite());
		}
		return model;
	}

	/**
	 * Removes the question reference on a variable (the first question
	 * reference in the list)
	 * 
	 * @param variQueiRel
	 *            containing the variable and question item
	 * @return variable
	 */
	Variable deleteQueiRefOnVari(VariableQuestionRelation variQueiRel) {
		Variable model = null;
		try {
			model = getVariable(variQueiRel);
			if (!model.getDocument().getVariable().getQuestionReferenceList()
					.isEmpty()) {
				model.getDocument().getVariable().getQuestionReferenceList()
						.remove(0);
				new VariableDao().update(model);
				// TODO refresh open editor
			}
		} catch (Exception e) {
			Editor.showError(e, ID, getSite());
		}
		return model;
	}

	void changeRespDomain(Variable vari, QuestionItem quei) {
		// clean quei domain
		if (quei.getResponseDomain() != null) {
			quei.setResponseDomain(ResponseType.UNDEFINED, "");
		}

		Object repImpl = vari.getRepresentation().getValueRepresentation();
		// CodeRepresentation
		if (repImpl instanceof CodeRepresentationType) {
			ReferenceType codeSchemeRef = ((CodeRepresentationType) repImpl)
					.getCodeSchemeReference();
			CodeDomainType rep = (CodeDomainType) quei.setResponseDomain(
					ResponseType.CODE, "");
			rep.setCodeSchemeReference(codeSchemeRef);
		}
		// NumericRepresentation
		else if (repImpl instanceof NumericRepresentationType) {
			NumericDomainType queiRep = (NumericDomainType) quei
					.setResponseDomain(ResponseType.NUMERIC, "");
			queiRep.setType(((NumericRepresentationType) vari
					.getRepresentation().getValueRepresentation()).getType());
			queiRep.setDecimalPositions(vari.getNumericDecimalPosition());
		}
		// TextRepresentation
		else if (repImpl instanceof TextRepresentationType) {
			// quei rep domain
			TextDomainType queiRep = (TextDomainType) quei.setResponseDomain(
					ResponseType.TEXT, "");

			// min length
			if (vari.getMinLength() != null) {
				queiRep.setMinLength(vari.getMinLength());
			}

			// max length
			if (vari.getMaxLength() != null) {
				queiRep.setMaxLength(vari.getMaxLength());
			}

			// regx
			if (vari.getRegx() != null) {
				queiRep.setRegExp(vari.getRegx());
			}
		}
		// DateTimeRepresentation
		else if (repImpl instanceof DateTimeRepresentationType) {
			// quei rep domain
			DateTimeDomainType queiRep = (DateTimeDomainType) quei
					.setResponseDomain(ResponseType.DATE, "");

			// format
			if (vari.getFormat() != null) {
				queiRep.setFormat(vari.getFormat());
			}

			// date time type
			if (vari.getDateTimeType() != null) {
				queiRep.setType(vari.getDateTimeType());
			}
		}

		// ExternalCategoryRepresentation
		else if (repImpl instanceof ExternalCategoryRepresentationType) {
			// TODO external category representation
		}

		// update quei
		try {
			new QuestionItemDao().update(quei);
		} catch (Exception e) {
			Editor.showError(e, ID, getSite());
		}

	}

	public void applyChange() {
		new ApplyChangeJob().run();
	}

	class ApplyChangeJob implements Runnable {
		@Override
		public void run() {
			ReferenceResolution queiRef = null;
			String variValRep = null;
			String queiRespDoma = null;
			QuestionItem quei = null;

			for (VariableQuestionRelation variQueiRel : relItems) {
				queiRef = null;
				variValRep = null;
				queiRespDoma = null;
				quei = null;

				// check the values
				if (variQueiRel.quei != null) {
					queiRef = new ReferenceResolution(variQueiRel.quei);

					// vari name
					String variName = null;
					for (CustomType cus : getCustomListbyType(variQueiRel.vari,
							VARI_NAME)) {
						variName = XmlBeansUtil.getTextOnMixedElement(cus);
					}

					// vari rep value
					for (CustomType cus : getCustomListbyType(variQueiRel.vari,
							VARI_VAL_REP)) {
						variValRep = XmlBeansUtil.getTextOnMixedElement(cus);
					}

					// quei rep domain
					for (CustomType cus : getCustomListbyType(variQueiRel.quei,
							QUEI_RESP_DOMA)) {
						queiRespDoma = XmlBeansUtil.getTextOnMixedElement(cus);
					}

					// vari id ~ quei userid pseudo vari id
					String variPseudoId = null;
					for (CustomType cus : getCustomListbyType(variQueiRel.quei,
							"UserID")) {
						if (cus.getOption().equals(
								Ddi3NamespaceHelper.QUEI_VAR_USER_ID_TYPE)) {
							variPseudoId = XmlBeansUtil
									.getTextOnMixedElement(cus);
							if (variName != null
									&& !variName.equals(variPseudoId)) {
								quei = setPseudoVariIdOnQuei(variQueiRel.quei,
										variName);
							} else {
								continue;
							}
						}
					}

					if (variPseudoId == null) {
						quei = setPseudoVariIdOnQuei(variQueiRel.quei, variName);
					}
				}

				// set vari quei ref
				Variable vari = null;
				String queiId = null;
				for (CustomType cus : getCustomListbyType(variQueiRel.vari,
						VARI_QUEI_REF)) {
					queiId = XmlBeansUtil.getTextOnMixedElement(cus);
					break;
				}
				if (queiId == null && queiRef == null) {
					continue;
				} else if (queiId != null && queiRef == null) {
					vari = deleteQueiRefOnVari(variQueiRel);
				} else if (queiId == null && queiRef != null) {
					vari = setQueiRefOnVari(variQueiRel);
				} else if (!queiRef.getId().equals(queiId)) {
					vari = setQueiRefOnVari(variQueiRel);
				}

				// change quei resp domain
				// set concept on vari
				if (variValRep != null && queiRef != null) {
					if (vari == null) {
						try {
							vari = getVariable(variQueiRel);
						} catch (Exception e1) {
							Editor.showError(e1, ID, getSite());
						}
					}

					if (quei == null) {
						try {
							quei = getQuestionItem(variQueiRel.quei);
						} catch (Exception e1) {
							Editor.showError(e1, ID, getSite());
						}
					}

					// resp domain
					changeRespDomain(vari, quei);

					try {
						// vari concept
						setConcepts(vari, quei);
					} catch (DDIFtpException e1) {
						Editor.showError(e1, ID, getSite());
					}
				}
			}
		}
	}

	public void refreshView() {
		if (combo.getSelectionIndex() < 0) {
			// nothing to do
			return;
		}
		loadInItems();
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

	public void loadInItems() {
		if (combo != null) {
			selectedResource = combo.getItem(combo.getSelectionIndex());
		}
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

			// question item plus
			List<LightXmlObjectType> lightQuei = DdiManager.getInstance()
					.getQuestionItemsLightPlus(true, null, null, null, null)
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
			boolean assigned = false;
			String variPseudoId = null;
			for (Iterator<LightXmlObjectType> iterator = lightQuei.iterator(); iterator
					.hasNext();) {
				LightXmlObjectType quei = iterator.next();
				assigned = false;

				// user id
				for (CustomType cus : getCustomListbyType(quei, "UserID")) {
					if (cus.getOption().equals(
							Ddi3NamespaceHelper.QUEI_VAR_USER_ID_TYPE)) {
						variPseudoId = XmlBeansUtil.getTextOnMixedElement(cus);
						if (variPseudoId != null) {
							for (VariableQuestionRelation variQueiRel : relItems) {
								if (variQueiRel.quei == null) {
									String variName = null;
									for (CustomType cusName : getCustomListbyType(
											variQueiRel.vari, "Name")) {
										variName = XmlBeansUtil
												.getTextOnMixedElement(cusName);
										if (variName != null
												&& variName
														.equals(variPseudoId)) {
											variQueiRel.quei = (LightXmlObjectType) quei
													.copy();
											assigned = true;
											break;
										}
									}
								}
								if (assigned) {
									break;
								}
							}
						}
					}
				}
				if (assigned) {
					iterator.remove();
				}
			}
			// refresh
			freeItems = lightQuei;
			if (relTableViewer != null && freeTableViewer != null) {
				relTableViewer.refresh();
				freeTableViewer.refresh();
			}
		} catch (Exception e2) {
			Editor.showError(e2, ID, getSite());
		}
	}

	@Override
	public void setFocus() {
		parent.setFocus();
	}

	private enum PopupAction {
		REMOVE, EDIT_QUESTION, EDIT_VARIABLE
	};

	@Override
	public void createPartControl(Composite parent) {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		parent.setLayout(gridLayout);
		this.parent = parent;

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

		// popup menu
		final Menu menu = new Menu(relTableViewer.getControl());

		// menu edit
		MenuItem editMenuItem = new MenuItem(menu, SWT.CASCADE);
		editMenuItem.setText(Translator.trans("View.label.editMenuItem.Edit"));
		editMenuItem.setImage(ResourceManager.getPluginImage(
				Activator.getDefault(), "icons/editor_area.gif"));

		Menu editsubmenu = new Menu(editMenuItem);
		MenuItem subeditquei = new MenuItem(editsubmenu, SWT.NONE);
		subeditquei.setText(Translator
				.trans("variablequestionview.popupmenu.editquestion"));
		subeditquei.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				popupMenuAction(PopupAction.EDIT_QUESTION);
			}
		});

		MenuItem subeditvari = new MenuItem(editsubmenu, SWT.NONE);
		subeditvari.setText(Translator
				.trans("variablequestionview.popupmenu.editvariable"));
		subeditvari.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				popupMenuAction(PopupAction.EDIT_VARIABLE);
			}
		});

		editMenuItem.setMenu(editsubmenu);

		// menu remove
		MenuItem removeMenuItem = new MenuItem(menu, SWT.NONE);
		removeMenuItem.setText(Translator
				.trans("variablequestionview.popupmenu.removequestion"));
		removeMenuItem.setImage(ResourceManager.getPluginImage(
				Activator.getDefault(), "icons/delete_obj.gif"));
		removeMenuItem.setSelection(true);

		removeMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				popupMenuAction(PopupAction.REMOVE);
			}
		});

		relTableViewer.getControl().setMenu(menu);

		// free questions
		freeTableViewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		freeTableViewer.getTable().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2));
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

	private void popupMenuAction(PopupAction action) {
		TableItem[] tableItems = relTableViewer.getTable().getSelection();
		// guard
		if (tableItems.length <= 0) {
			DialogUtil.errorDialog(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell(), ID, null, null,
					new DDIFtpException());
			return;
		}

		boolean update = false;
		for (int i = 0; i < tableItems.length; i++) {
			VariableQuestionRelation selected = (VariableQuestionRelation) tableItems[i]
					.getData();

			// edit
			if (action.equals(PopupAction.EDIT_QUESTION)
					&& selected.quei != null) {
				try {
					TreeMenu.defineInputAndOpenEditor(null,
							ElementType.QUESTION_SCHEME, selected.quei,
							EditorModeType.EDIT, PersistenceManager
									.getInstance().getWorkingResource(), null);
				} catch (DDIFtpException e) {
					Editor.showError(e, ID, getSite());
				}
			} else if (action.equals(PopupAction.EDIT_VARIABLE)) {
				try {
					TreeMenu.defineInputAndOpenEditor(null,
							ElementType.VARIABLE_SCHEME, selected.vari,
							EditorModeType.EDIT, PersistenceManager
									.getInstance().getWorkingResource(), null);
				} catch (DDIFtpException e) {
					Editor.showError(e, ID, getSite());
				}
			}
			// remove
			else if (action.equals(PopupAction.REMOVE)) {
				if (selected.quei != null) {
					XmlObjectComparer comparer = new XmlObjectComparer();
					for (VariableQuestionRelation variQueiRel : relItems) {

						if (comparer.equals(variQueiRel.vari, selected.vari)) {
							freeItems.add(variQueiRel.quei);
							variQueiRel.quei = null;

							break;
						}
					}
				}
				relTableViewer.refresh();
				freeTableViewer.refresh();
			} else {
				Editor.showError(new DDIFtpException(
						action + " not supported.", new Throwable()), ID,
						getSite());
			}
		}

		// update
		if (update) {
			relTableViewer.refresh(true);
		}
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
					Translator.trans("variablequestionview.column.vari"),
					Translator.trans("variablequestionview.column.quei") };
			int[] widths = { 200, 375 };
			int[] style = { SWT.LEFT, SWT.LEFT };
			for (int i = 0; i < titles.length; i++) {
				TableViewerColumn column = new TableViewerColumn(viewer,
						style[i]);
				column.getColumn().setText(titles[i]);
				column.getColumn().setWidth(widths[i]);
				column.getColumn().setResizable(true);
			}
			table.setHeaderVisible(true);
			table.setLinesVisible(true);
			table.pack();
			Editor.resizeTableFont(table);

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
						VariableQuestionRelation variQueirel = (VariableQuestionRelation) items[i]
								.getData();

						if (variQueirel.quei != null) {
							// hack on //question item
							if (variQueirel.quei.getElement().indexOf("//") > -1) {
								variQueirel.quei.setElement(variQueirel.quei
										.getElement().substring(2));
							}
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
						Translator
								.trans("translationdialog.error.columnindexnotfound"), new Throwable()); //$NON-NLS-1$
				Editor.showError(e, ID, getSite());
				break;
			}
			StringBuilder str = new StringBuilder();
			String empty = "";
			for (int i = 0; i < result.length; i++) {
				if (result[i] == null || result[i].equals(empty)) {
					str.append("");
				} else {
					str.append(result[i]);
					// str.append(System.getProperty("line.separator"));
					str.append(" - ");
				}
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
			Translator.trans("variablequestionview.column.quei") };
			int[] widths = { 375 };
			int[] style = { SWT.LEFT };
			for (int i = 0; i < titles.length; i++) {
				TableViewerColumn column = new TableViewerColumn(viewer,
						style[i]);
				column.getColumn().setText(titles[i]);
				column.getColumn().setWidth(widths[i]);
				column.getColumn().setResizable(true);
			}
			table.setHeaderVisible(true);
			table.setLinesVisible(true);
			table.pack();
			Editor.resizeTableFont(table);

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
						Translator
								.trans("translationdialog.error.columnindexnotfound"), new Throwable()); //$NON-NLS-1$
				Editor.showError(e, ID, getSite());
				break;
			}
			StringBuilder str = new StringBuilder();
			String empty = "";
			for (int i = 0; i < result.length; i++) {
				if (result[i] == null || result[i].equals(empty)) {
					str.append("");
				} else {
					str.append(result[i]);
					// str.append("\n");
					str.append(" - ");
				}
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

	@Override
	public void propertyChanged(Object source, int propId) {
		// TODO Auto-generated method stub

	}
}
