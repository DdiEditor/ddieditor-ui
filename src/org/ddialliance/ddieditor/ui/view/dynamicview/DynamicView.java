package org.ddialliance.ddieditor.ui.view.dynamicview;

import java.util.ArrayList;
import java.util.List;

import org.apache.xmlbeans.XmlObject;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.QuestionItemType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.ReferenceType;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectListDocument;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddieditor.ui.editor.widgetutil.table.TableColumnSort;
import org.ddialliance.ddieditor.ui.model.IModel;
import org.ddialliance.ddieditor.ui.model.Model;
import org.ddialliance.ddieditor.ui.model.ModelAccessor;
import org.ddialliance.ddieditor.ui.model.concept.Concept;
import org.ddialliance.ddieditor.ui.model.question.MultipleQuestionItem;
import org.ddialliance.ddieditor.ui.model.question.QuestionItem;
import org.ddialliance.ddieditor.ui.model.universe.Universe;
import org.ddialliance.ddieditor.ui.model.variable.Variable;
import org.ddialliance.ddieditor.ui.util.LanguageUtil;
import org.ddialliance.ddieditor.util.LightXmlObjectUtil;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.Translator;
import org.ddialliance.ddiftp.util.xml.XmlBeansUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

public class DynamicView extends ViewPart {
	public static final String ID = "org.ddialliance.ddieditor.ui.view.dynamicview";
	Editor editor = null;
	Group group;
	int tableProperties = SWT.VIRTUAL | SWT.FULL_SELECTION | SWT.MULTI
			| SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER;

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

	@Override
	public void init(IViewSite site) throws PartInitException {
		super.init(site);
		editor = new Editor();
	}

	@Override
	public void createPartControl(Composite parent) {
		group = editor.createGroup(parent, "Elements");

		// concept - table
		conceptLabel = editor.createLabel(group, Translator.trans("Concepts"));
		conceptTable = new Table(group, tableProperties);

		String[] conceptNames = { Translator.trans("Label") };
		initTable(conceptLabel, conceptTable, conceptNames);

		// universe - table
		universeLabel = editor
				.createLabel(group, Translator.trans("Universes"));
		universeTable = new Table(group, tableProperties);
		String[] uniNames = { Translator.trans("Label") };
		initTable(universeLabel, universeTable, uniNames);

		// question - table
		questionLabel = editor
				.createLabel(group, Translator.trans("Questions"));
		questionTable = new Table(group, tableProperties);
		String[] queiNames = { Translator.trans("Label") };
		initTable(questionLabel, questionTable, queiNames);

		// categories - table

		// variable - table
		variableLabel = editor
				.createLabel(group, Translator.trans("Variables"));
		variableTable = new Table(group, tableProperties);
		String[] variNames = { Translator.trans("Label") };
		initTable(variableLabel, variableTable, variNames);
	}

	@Override
	public void setFocus() {
		// do nothing
	}

	private void initTable(Label label, Table table, String[] columnNames) {
		// label.setVisible(false);
		// table.setVisible(false);

		// table properties
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.GRAB_HORIZONTAL);
		// | GridData.VERTICAL_ALIGN_FILL
		// | GridData.GRAB_VERTICAL);
		gd.horizontalSpan = 1;
		
		table.setRedraw(true);
		table.setLayoutData(gd);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.addSelectionListener(new TableListener());

		// create table columns
		TableColumnSort sort = new TableColumnSort(table);
		for (int i = 0; i < columnNames.length; i++) {
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(columnNames[i]);
			column.addListener(SWT.Selection, sort);
			column.setResizable(true);
			column.setWidth(130);
		}
	}

	public void refresh(IModel model) {
		// init
		((Model) model).setCreate(false);

		// concept
		if (model instanceof Concept) {
			// conceptLabel.setVisible(false);
			// conceptTable.setVisible(false);
			//
			// universeLabel.setVisible(true);
			// universeTable.setVisible(true);
			//
			// questionLabel.setVisible(true);
			// questionTable.setVisible(true);
			//
			// variableLabel.setVisible(true);
			// variableTable.setVisible(true);
		}

		// universe
		if (model instanceof Universe) {
			// universeLabel.setVisible(false);
			// universeTable.setVisible(false);
			//
			// conceptLabel.setVisible(true);
			// conceptTable.setVisible(true);
			//
			// questionLabel.setVisible(true);
			// questionTable.setVisible(true);
			//
			// variableLabel.setVisible(true);
			// variableTable.setVisible(true);
		}

		// question
		if (model instanceof QuestionItem) {
			// questionLabel.setVisible(false);
			// questionTable.setVisible(false);
			//
			// conceptLabel.setVisible(true);
			// conceptTable.setVisible(true);
			//
			// universeLabel.setVisible(true);
			// universeTable.setVisible(true);
			//
			// variableLabel.setVisible(true);
			// variableTable.setVisible(true);

			QuestionItem modelImpl = (QuestionItem) model;

			// concept
			updateTable(modelImpl.getDocument().getQuestionItem()
					.getConceptReferenceList(), "Concept", conceptTable);

			// universe
			updateTable(emptyList, "", universeTable);

			// question
			updateTable(emptyList, "", questionTable);

			// variable
			updateTable(emptyList, "", variableTable);
		}

		// multiple question
		if (model instanceof MultipleQuestionItem) {
			// questionLabel.setVisible(false);
			// questionTable.setVisible(false);
			//
			// conceptLabel.setVisible(true);
			// conceptTable.setVisible(true);
			//
			// universeLabel.setVisible(true);
			// universeTable.setVisible(true);
			//
			// variableLabel.setVisible(true);
			// variableTable.setVisible(true);

			MultipleQuestionItem modelImpl = (MultipleQuestionItem) model;

			// concept
			try {
				updateTable(modelImpl.getDocument().getMultipleQuestionItem()
						.getConceptReferenceList(), "Concept", conceptTable);
			} catch (DDIFtpException e) {
				Editor.showError(e, ID);
			}

			// universe
			updateTable(emptyList, "", universeTable);

			// question
			updateQuestionTable(modelImpl);

			// variable
			updateTable(emptyList, "", variableTable);
		}

		// variable
		if (model instanceof Variable) {
			// variableLabel.setVisible(false);
			// variableTable.setVisible(false);
			//
			// conceptLabel.setVisible(true);
			// conceptTable.setVisible(true);
			//
			// universeLabel.setVisible(true);
			// universeTable.setVisible(true);
			//
			// questionLabel.setVisible(true);
			// questionTable.setVisible(true);
		}

		// finalize
		((Model) model).setCreate(true);
		group.layout(true);
		group.redraw();
	}

	private void updateTable(List<ReferenceType> list, String elementType,
			Table table) {
		// clear table
		clearTable(table);

		// insert into table
		for (ReferenceType referenceType : list) {
			referenceType.getIDArray(0);
			LightXmlObjectListDocument lightXmlObjectDoc = null;
			try {
				lightXmlObjectDoc = ModelAccessor.resolveReference(
						referenceType, elementType);
			} catch (DDIFtpException e) {
				Editor.showError(e, ID);
			}

			// guard
			if (lightXmlObjectDoc.getLightXmlObjectList()
					.getLightXmlObjectList().isEmpty()) {
				return;
			}
			LightXmlObjectType lightXmlObject = lightXmlObjectDoc
					.getLightXmlObjectList().getLightXmlObjectList().get(0);

			TableItem item = new TableItem(table, SWT.NONE);
			try {
				item.setText(0, XmlBeansUtil
						.getTextOnMixedElement((XmlObject) XmlBeansUtil
								.getLangElement(
										LanguageUtil.getDisplayLanguage(),
										lightXmlObject.getLabelList())));
			} catch (DDIFtpException e) {
				Editor.showError(e, ID);
			}
			item.setData(lightXmlObject);
		}
	}

	private void clearTable(Table table) {
		try {
			TableItem[] items = table.getItems();
			for (int i = 0; i < items.length; i++) {
				items[i].dispose();
			}
		} catch (Exception e) {
			// swt null items in table exception
		}
	}

	private void updateQuestionTable(MultipleQuestionItem modelImpl) {
		// clear table
		clearTable(questionTable);

		// question item list
		List<QuestionItemType> questionItemList = null;
		try {
			if (modelImpl.getDocument().getMultipleQuestionItem()
					.getSubQuestions() != null) {

				questionItemList = modelImpl.getDocument()
						.getMultipleQuestionItem().getSubQuestions()
						.getQuestionItemList();
			}
		} catch (DDIFtpException e) {
			Editor.showError(e, ID);
		}
		if (questionItemList == null) { // guard
			return;
		}

		// insert into table
		for (QuestionItemType questionItemType : questionItemList) {
			LightXmlObjectType lightXmlObject = LightXmlObjectUtil
					.createLightXmlObject(modelImpl.getParentId(),
							modelImpl.getParentVersion(),
							questionItemType.getId(),
							questionItemType.getVersion(), "QuestionItem");

			TableItem item = new TableItem(questionTable, SWT.NONE);
			try {
				item.setText(0, XmlBeansUtil
						.getTextOnMixedElement((XmlObject) XmlBeansUtil
								.getLangElement(
										LanguageUtil.getDisplayLanguage(),
										lightXmlObject.getLabelList())));
			} catch (DDIFtpException e) {
				Editor.showError(e, ID);
			}
			item.setData(lightXmlObject);

			// set label
			try {
				item.setText(0, XmlBeansUtil
						.getTextOnMixedElement((XmlObject) XmlBeansUtil
								.getLangElement(
										LanguageUtil.getDisplayLanguage(),
										lightXmlObject.getLabelList())));
			} catch (DDIFtpException e) {
				Editor.showError(e, ID);
			}
			item.setData(lightXmlObject);
		}
	}

	// currently not used as edit is not supported, yet!
	class TableListener implements SelectionListener {
		public TableListener() {

		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			// do nothing
		}

		@Override
		public void widgetSelected(SelectionEvent event) {
			TableItem[] selections = ((Table) event.widget).getSelection();

			// guard
			if (selections.length == 0) {
				return;
			}
		}
	}
}