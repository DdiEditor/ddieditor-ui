package org.ddialliance.ddieditor.ui.view.dynamicview;

import java.util.ArrayList;
import java.util.List;

import org.apache.xmlbeans.XmlObject;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.CodeDomainDocument;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.CodeDomainType;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.MultipleQuestionItemDocument;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.QuestionItemType;
import org.ddialliance.ddi3.xml.xmlbeans.logicalproduct.CategorySchemeDocument;
import org.ddialliance.ddi3.xml.xmlbeans.logicalproduct.CategoryType;
import org.ddialliance.ddi3.xml.xmlbeans.logicalproduct.CodeSchemeDocument;
import org.ddialliance.ddi3.xml.xmlbeans.logicalproduct.CodeType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.NameType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.ReferenceType;
import org.ddialliance.ddieditor.logic.urn.ddi.ReferenceResolution;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LabelType;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectListDocument;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectListType;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.persistenceaccess.PersistenceManager;
import org.ddialliance.ddieditor.ui.dbxml.category.CategorySchemeDao;
import org.ddialliance.ddieditor.ui.dbxml.code.CodeSchemeDao;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddieditor.ui.editor.EditorInput.EditorModeType;
import org.ddialliance.ddieditor.ui.editor.widgetutil.table.TableColumnSort;
import org.ddialliance.ddieditor.ui.model.ElementType;
import org.ddialliance.ddieditor.ui.model.IModel;
import org.ddialliance.ddieditor.ui.model.Model;
import org.ddialliance.ddieditor.ui.model.ModelAccessor;
import org.ddialliance.ddieditor.ui.model.code.CodeScheme;
import org.ddialliance.ddieditor.ui.model.concept.Concept;
import org.ddialliance.ddieditor.ui.model.question.MultipleQuestionItem;
import org.ddialliance.ddieditor.ui.model.question.QuestionItem;
import org.ddialliance.ddieditor.ui.model.question.Response;
import org.ddialliance.ddieditor.ui.model.question.ResponseType;
import org.ddialliance.ddieditor.ui.model.universe.Universe;
import org.ddialliance.ddieditor.ui.model.variable.Variable;
import org.ddialliance.ddieditor.ui.view.TreeMenu;
import org.ddialliance.ddieditor.util.LightXmlObjectUtil;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.Translator;
import org.ddialliance.ddiftp.util.xml.XmlBeansUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

public class DynamicView extends ViewPart {
	public static final String ID = "org.ddialliance.ddieditor.ui.view.dynamicview.DynamicView";
	Editor editor = null;
	Composite parent;
	Group group;
	int tableProperties = SWT.VIRTUAL | SWT.FULL_SELECTION | SWT.MULTI
			| SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER;
	
	GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL
			| GridData.GRAB_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL
			| GridData.GRAB_VERTICAL);
	
	int[] catColumnWidth = new int[] {45, 200};
	
	private Table conceptTable;
	private Label conceptLabel;

	private Table universeTable;
	private Label universeLabel;

	private Table questionTable;
	private Label questionLabel;

	private Table categoryTable;
	private Label categoryLabel;

	private Table variableTable;
	private Label variableLabel;

	List<ReferenceType> emptyList = new ArrayList<ReferenceType>();
	String[] labelNames = new String[1];

	@Override
	public void init(IViewSite site) throws PartInitException {
		super.init(site);
		editor = new Editor();
		labelNames[0] = Translator.trans("Label");
	}

	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
		group = editor.createGroup(parent, "Elements");
	}

	@Override
	public void setFocus() {
		// do nothing
	}

	private void createConcepts() {
		conceptLabel = editor.createLabel(group, Translator.trans("Concepts"));
		conceptTable = new Table(group, tableProperties);
		initTable(conceptTable, labelNames);
	}

	private void createUniverses() {
		universeLabel = editor
				.createLabel(group, Translator.trans("Universes"));
		universeTable = new Table(group, tableProperties);
		initTable(universeTable, labelNames);
	}

	private void createQuestions() {
		questionLabel = editor
				.createLabel(group, Translator.trans("Questions"));
		questionTable = new Table(group, tableProperties);
		initTable(questionTable, labelNames);
	}

	private void createVariables() {
		variableLabel = editor
				.createLabel(group, Translator.trans("Variables"));
		variableTable = new Table(group, tableProperties);
		initTable(variableTable, labelNames);
	}

	private void createCategories(IModel model) {
		categoryLabel = editor.createLabel(group,
				Translator.trans("Representation"));

		ResponseType responseType = null;
		if (model instanceof QuestionItem) {
			QuestionItem modelImpl = ((QuestionItem) model);
			responseType = Response.getResponseType(modelImpl
					.getResponseDomain());
		} else if (model instanceof Variable) {
			Variable modelImpl = ((Variable) model);
			responseType = Response.getResponseType(modelImpl
					.getValueRepresentation());
		}

		if (responseType == null) {
			return;
		} else if (responseType.equals(ResponseType.CODE)) {
			categoryTable = new Table(group, tableProperties);
			initTable(categoryTable, new String[] { Translator.trans("Code"),
					Translator.trans("Category") }, catColumnWidth);
		} else {
			Label label = editor.createLabel(group,
					Response.RESPONSE_TYPE_LABELS[responseType.ordinal()]);
			FontData[] fontData = label.getFont().getFontData();
			for (int i = 0; i < fontData.length; i++) {
				fontData[i].setStyle(SWT.BOLD);
			}
			label.setFont(new Font(PlatformUI.getWorkbench().getDisplay(),
					fontData));
			label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
					false, 1, 1));
		}
	}
	
	private void initTable(Table table, String[] columnNames) {
		initTable(table, columnNames, new int[] {200});
	}

	private void initTable(Table table, String[] columnNames, int[] width) {
		// table properties
		gd.horizontalSpan = 1;
		table.setLayoutData(gd);		
		table.setRedraw(true);		
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.addMouseListener(new TableListener());

		// create table columns
		TableColumnSort sort = new TableColumnSort(table);
		for (int i = 0; i < columnNames.length; i++) {
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(columnNames[i]);
			column.addListener(SWT.Selection, sort);
			column.setResizable(true);
			column.setWidth(width[i]);
			// column.setAlignment(SWT.RIGHT);
		}
	}

	public void cleanUp() {
		group.dispose();
		parent.layout(true);
	}

	public void refresh(IModel model) throws Exception {
		// group
		cleanUp();
		group = editor.createGroup(parent, "Elements");

		// init model
		boolean isSetCreate = true;
		if (model != null) {
			isSetCreate = ((Model) model).isCreate();
			((Model) model).setCreate(false);
		}
		LightXmlObjectListDocument lightXmlObjectListDoc = null;

		// concept
		if (model instanceof Concept) {
			// init tables
			createUniverses();
			createQuestions();
			createVariables();

			// init model
			Concept modelImpl = (Concept) model;
			ReferenceResolution refRes = new ReferenceResolution(
					LightXmlObjectUtil.createLightXmlObject(
							modelImpl.getParentId(),
							modelImpl.getParentVersion(), modelImpl.getId(),
							modelImpl.getVersion(),
							"logicalproduct__ConceptReference"));

			// update questions
			lightXmlObjectListDoc = DdiManager.getInstance()
					.getQuestionItemsLightByConcept(refRes);
			updateTable(lightXmlObjectListDoc, questionTable);

			// update variables
			lightXmlObjectListDoc = DdiManager.getInstance()
					.getVariablesLightByConcept(refRes);
			updateTable(lightXmlObjectListDoc, variableTable);

			// update universe
			LightXmlObjectListDocument universeDoc = LightXmlObjectListDocument.Factory
					.newInstance();
			LightXmlObjectListType universeList = universeDoc
					.addNewLightXmlObjectList();
			universeList.setLightXmlObjectArray(LightXmlObjectUtil
					.getXmlObjectsByCustomListType(lightXmlObjectListDoc,
							"Universe").toArray(new LightXmlObjectType[] {}));

			updateTable(universeDoc, universeTable);
		}

		// universe
		if (model instanceof Universe) {
			createConcepts();
			createQuestions();
			createVariables();

			Universe modelImpl = (Universe) model;
			ReferenceResolution refRes = new ReferenceResolution(
					LightXmlObjectUtil.createLightXmlObject(
							modelImpl.getParentId(),
							modelImpl.getParentVersion(), modelImpl.getId(),
							modelImpl.getVersion(), "UniverseReference"));

			// update variables
			lightXmlObjectListDoc = DdiManager.getInstance()
					.getVariablesLightByUniverse(refRes);
			updateTable(lightXmlObjectListDoc, variableTable);

			// update concept
			updateTableWithCustom(lightXmlObjectListDoc, "Concept",
					conceptTable);

			// update question
			updateTableWithCustom(lightXmlObjectListDoc, "Question",
					questionTable);
		}

		// question
		if (model instanceof QuestionItem) {
			createConcepts();
			createUniverses();
			createVariables();
			createCategories(model);

			QuestionItem modelImpl = (QuestionItem) model;

			// concept
			updateTable(modelImpl.getDocument().getQuestionItem()
					.getConceptReferenceList(), "Concept", conceptTable);

			ReferenceResolution refRes = new ReferenceResolution(
					LightXmlObjectUtil.createLightXmlObject(
							modelImpl.getParentId(),
							modelImpl.getParentVersion(), modelImpl.getId(),
							modelImpl.getVersion(),
							"logicalproduct__QuestionReference"));

			// update variables
			lightXmlObjectListDoc = DdiManager.getInstance()
					.getVariablesLightByQuestionItem(refRes);
			updateTable(lightXmlObjectListDoc, variableTable);

			// update universe
			updateTableWithCustom(lightXmlObjectListDoc, "Universe",
					universeTable);

			// categories
			ResponseType rep = Response.getResponseType(modelImpl
					.getResponseDomain());
			if (rep != null && rep.equals(ResponseType.CODE)) {
				CodeDomainType codeDomain = (CodeDomainType) modelImpl
						.getResponseDomain()
						.substitute(
								CodeDomainDocument.type
										.getDocumentElementName(),
								CodeDomainType.type);
				updateCategoryTable(codeDomain.getCodeSchemeReference());
			}
		}

		// multiple question
		if (model instanceof MultipleQuestionItem) {
			createConcepts();
			createQuestions();

			MultipleQuestionItem modelImpl = (MultipleQuestionItem) model;

			// concept
			updateTable(modelImpl.getDocument().getMultipleQuestionItem()
					.getConceptReferenceList(), "Concept", conceptTable);

			// question
			updateQuestionTable(modelImpl);
		}

		// variable
		if (model instanceof Variable) {
			createConcepts();
			createUniverses();
			createQuestions();
			createCategories(model);

			Variable modelImpl = (Variable) model;

			// concept
			List<ReferenceType> concs = new ArrayList<ReferenceType>();
			concs.add(modelImpl.getDocument().getVariable()
					.getConceptReference());
			updateTable(concs, "Concept", conceptTable);

			// universes
			updateTable(modelImpl.getDocument().getVariable()
					.getUniverseReferenceList(), "Universe", universeTable);

			// question
			updateTable(modelImpl.getDocument().getVariable()
					.getQuestionReferenceList(), "QuestionItem", questionTable);

			// categories
			ReferenceType ref = modelImpl
					.getCodeRepresentationCodeSchemeReference();
			if (ref != null && !ref.getIDList().isEmpty()) {
				updateCategoryTable(ref);
			}
		}

		// categories
		if (model instanceof CodeScheme) {
			CodeScheme modelImpl = (CodeScheme) model;

			// init table
			createQuestions();
			createVariables();

			ReferenceResolution refRes = new ReferenceResolution(
					LightXmlObjectUtil.createLightXmlObject(
							modelImpl.getParentId(),
							modelImpl.getParentVersion(), modelImpl.getId(),
							modelImpl.getVersion(), "reusable__CodeSchemeReference"));

			// question
			lightXmlObjectListDoc = DdiManager.getInstance()
					.getQuestionItemsLightByCodeScheme(refRes);
			updateTable(lightXmlObjectListDoc, questionTable);

			// variable
			lightXmlObjectListDoc = DdiManager.getInstance().getVariablesLightByCodeScheme(refRes);
			updateTable(lightXmlObjectListDoc, variableTable);
		}

		// finalize
		if (model != null) {
			((Model) model).setCreate(isSetCreate);
		}
		parent.layout(true);
	}

	private void updateTable(List<ReferenceType> list, String elementType,
			Table table) throws DDIFtpException {
		// insert into table
		for (ReferenceType referenceType : list) {
			if (referenceType == null) {
				break;
			}
			referenceType.getIDArray(0);
			LightXmlObjectListDocument lightXmlObjectDoc = null;

			lightXmlObjectDoc = ModelAccessor.resolveReference(referenceType,
					elementType);

			// guard
			if (lightXmlObjectDoc.getLightXmlObjectList()
					.getLightXmlObjectList().isEmpty()) {
				return;
			}
			LightXmlObjectType lightXmlObject = lightXmlObjectDoc
					.getLightXmlObjectList().getLightXmlObjectList().get(0);
			setItem(table, 0, lightXmlObject);
		}
	}

	private void updateTable(LightXmlObjectListDocument list, Table table)
			throws DDIFtpException {
		// insert into table
		for (LightXmlObjectType lightXmlObject : list.getLightXmlObjectList()
				.getLightXmlObjectList()) {
			setItem(table, 0, lightXmlObject);
		}
	}

	private void updateTableWithCustom(
			LightXmlObjectListDocument customLightXmlObjectDoc,
			String elementName, Table table) throws DDIFtpException {
		LightXmlObjectListDocument lightXmlObjectDoc = LightXmlObjectListDocument.Factory
				.newInstance();
		LightXmlObjectListType lightXmlObjectList = lightXmlObjectDoc
				.addNewLightXmlObjectList();
		lightXmlObjectList.setLightXmlObjectArray(LightXmlObjectUtil
				.getXmlObjectsByCustomListType(customLightXmlObjectDoc,
						elementName).toArray(new LightXmlObjectType[] {}));
		if (elementName.equals("Question")) {
			for (LightXmlObjectType lightXmlObject : lightXmlObjectList
					.getLightXmlObjectList()) {
				lightXmlObject.setElement("QuestionItem");
			}
		}
		updateTable(lightXmlObjectDoc, table);
	}

	private void updateQuestionTable(MultipleQuestionItem modelImpl)
			throws DDIFtpException, Exception {
		MultipleQuestionItemDocument doc = null;
		doc = DdiManager.getInstance().getMultipleQuestionItem(
				modelImpl.getId(), modelImpl.getVersion(),
				modelImpl.getParentId(), modelImpl.getParentVersion());

		// question item list
		if (doc.getMultipleQuestionItem().getSubQuestions()
				.getQuestionItemList().isEmpty()) { // guard
			return;
		}

		// insert into table
		for (QuestionItemType questionItemType : doc.getMultipleQuestionItem()
				.getSubQuestions().getQuestionItemList()) {
			// light xml object
			LightXmlObjectType lightXmlObject = LightXmlObjectUtil
					.createLightXmlObject(modelImpl.getParentId(),
							modelImpl.getParentVersion(),
							questionItemType.getId(),
							questionItemType.getVersion(), "QuestionItem");

			// light xml object label list
			for (NameType name : questionItemType.getQuestionItemNameList()) {
				LabelType label = lightXmlObject.addNewLabel();
				label.setLang(name.getLang());
				XmlBeansUtil.setTextOnMixedElement(label,
						XmlBeansUtil.getTextOnMixedElement(name));
			}

			// table item
			setItem(questionTable, 0, lightXmlObject);
		}
	}

	private void updateCategoryTable(ReferenceType codeSchemeReference)
			throws Exception {
		// resolve references
		CodeSchemeDocument codeScheme = CodeSchemeDao
				.getCodeSchemeByReference(new ReferenceResolution(
						codeSchemeReference));

		ReferenceType catSchemeRef = codeScheme.getCodeScheme()
				.getCategorySchemeReference();
		CategorySchemeDocument catScheme = CategorySchemeDao
				.getCodeSchemeByReference(new ReferenceResolution(catSchemeRef));

		LightXmlObjectType lightXmlObject = LightXmlObjectUtil
				.createLightXmlObject(null, null, codeScheme.getCodeScheme()
						.getId(), codeScheme.getCodeScheme().getVersion(),
						"CodeScheme");

		for (CodeType code : codeScheme.getCodeScheme().getCodeList()) {
			// code
			XmlBeansUtil.setTextOnMixedElement(lightXmlObject.addNewLabel(),
					code.getValue());
			TableItem item = setItem(categoryTable, 0, lightXmlObject);

			// category
			for (CategoryType cat : catScheme.getCategoryScheme()
					.getCategoryList()) {
				if (cat.getId().equals(
						XmlBeansUtil.getTextOnMixedElement(code
								.getCategoryReference().getIDList().get(0)))) {

					String text = XmlBeansUtil
							.getTextOnMixedElement(((org.ddialliance.ddi3.xml.xmlbeans.reusable.LabelType) XmlBeansUtil
									.getDefaultLangElement(cat.getLabelList())));
					XmlBeansUtil.setTextOnMixedElement(
							lightXmlObject.addNewLabel(), text);
					addToTableItem(item, 1, lightXmlObject);
					break;
				}
			}
		}
	}

	private TableItem setItem(Table table, int index,
			LightXmlObjectType lightXmlObject) throws DDIFtpException {
		TableItem item = new TableItem(table, SWT.NONE);
		item.setText(
				index,
				lightXmlObject == null ? "" : XmlBeansUtil
						.getTextOnMixedElement((XmlObject) XmlBeansUtil
								.getDefaultLangElement(lightXmlObject
										.getLabelList())));
		item.setData(lightXmlObject);
		return item;
	}

	private void addToTableItem(TableItem item, int index,
			LightXmlObjectType lightXmlObject) throws DDIFtpException {
		item.setText(
				index,
				lightXmlObject == null ? "" : XmlBeansUtil
						.getTextOnMixedElement((XmlObject) XmlBeansUtil
								.getDefaultLangElement(lightXmlObject
										.getLabelList())));
		item.setData(lightXmlObject);
	}

	public static void closeUpdateDynamicView() {
		DynamicViewShutdownJob job = new DynamicViewShutdownJob();
		Display.getDefault().asyncExec(job);
	}

	class TableListener implements MouseListener {
		public TableListener() {
			super();
		}

		@Override
		public void mouseDoubleClick(MouseEvent event) {
			TableItem[] selections = ((Table) event.widget).getSelection();

			// guard
			if (selections.length == 0) {
				return;
			}

			// type
			LightXmlObjectType lightXmlObject = (LightXmlObjectType) selections[0]
					.getData();

			try {
				ElementType type = ElementType.getElementType(lightXmlObject
						.getElement());
				ElementType parentType = null;

				if (type.equals(ElementType.CONCEPT)) {
					parentType = ElementType.CONCEPT_SCHEME;
				}
				if (type.equals(ElementType.UNIVERSE)) {
					parentType = ElementType.UNIVERSE_SCHEME;
				}
				if (type.equals(ElementType.QUESTION_ITEM)) {
					parentType = ElementType.QUESTION_SCHEME;
				}
				if (type.equals(ElementType.VARIABLE)) {
					parentType = ElementType.VARIABLE_SCHEME;
				}
				if (type.equals(ElementType.CODE_SCHEME)) {
					parentType = ElementType.LOGICAL_PRODUCT;
				}

				TreeMenu.defineInputAndOpenEditor(type, parentType,
						lightXmlObject, EditorModeType.EDIT, PersistenceManager
								.getInstance().getWorkingResource(), null);
			} catch (DDIFtpException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void mouseDown(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseUp(MouseEvent e) {
			// TODO Auto-generated method stub

		}
	}
}