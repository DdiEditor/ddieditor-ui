package org.ddialliance.ddieditor.ui.editor.variable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.ddialliance.ddi3.xml.xmlbeans.logicalproduct.CodeSchemeDocument;
import org.ddialliance.ddi3.xml.xmlbeans.logicalproduct.CodeType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.NameType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.ReferenceType;
import org.ddialliance.ddieditor.logic.urn.ddi.ReferenceResolution;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.dbxml.code.CodeSchemeDao;
import org.ddialliance.ddieditor.ui.dbxml.variable.VariableDao;
import org.ddialliance.ddieditor.ui.dialogs.translationdialoginput.DescriptionTdI;
import org.ddialliance.ddieditor.ui.dialogs.translationdialoginput.LabelTdI;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddieditor.ui.editor.widgetutil.genericmodifylistener.TextStyledTextModyfiListener;
import org.ddialliance.ddieditor.ui.editor.widgetutil.referenceselection.ReferenceSelectionAdapter;
import org.ddialliance.ddieditor.ui.editor.widgetutil.referenceselection.ReferenceSelectionCombo;
import org.ddialliance.ddieditor.ui.model.ElementType;
import org.ddialliance.ddieditor.ui.model.ModelIdentifingType;
import org.ddialliance.ddieditor.ui.model.variable.Variable;
import org.ddialliance.ddieditor.ui.util.DialogUtil;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.Translator;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

public class VariableEditor extends Editor {
	public static final String ID = "org.ddialliance.ddieditor.ui.editor.variable.VariableEditor";
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			VariableEditor.class);
	Variable modelImpl;

	public VariableEditor() {
		super(Translator.trans("VariableEditor.label"), Translator
				.trans("VariableEditor.label.useTheEditorLabel.Description"),
				ID);
		this.dao = new VariableDao();
	}

	@Override
	public void setFocus() {
		super.setFocus();
		// DynamicQueryManager
		// List<String> q = new ArrayList<String>();
		// try {
		// q.add(DdiManager.getInstance().getQueryElementString(modelImpl.getId(),
		// modelImpl.getVersion(), "Variable", model.getParentId(),
		// modelImpl.getParentVersion(), "VariableScheme"));
		// q.add(DdiManager.getInstance().getQueryElementString(modelImpl.getId(),
		// modelImpl.getVersion(), "Variable", model.getParentId(),
		// modelImpl.getParentVersion(), "VariableScheme"));
		// } catch (DDIFtpException e) {
		// e.printStackTrace();
		// }
		// List result = DynamicQueryManager.getInstance().executeQueryList(q);
		//
		// for (Object object : result) {
		// try {
		// List result2 = ((Future<List<String>>)object).get();
		// for (Object object2 : result2) {
		// System.out.println(object2);
		// }
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (ExecutionException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
		modelImpl = (Variable) model;
	}

	private void createMissingValue(Group representationGroup) {

		StringBuffer missingValues = new StringBuffer();
		List list = modelImpl.getMissingValue();
		if (list != null) {
			for (Iterator iterator = list.iterator(); iterator.hasNext();) {
				String missingValue = (String) iterator.next();
				if (iterator.hasNext()) {
					missingValues.append(missingValue + " ");
				} else {
					missingValues.append(missingValue);
				}
			}
		}
		Text missingValueText = createTextInput(representationGroup,
				"Missing Values", missingValues.toString(), false);
		missingValueText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				editorStatus.setChanged();

				try {
					String missing = ((Text) e.getSource()).getText();
					// split string of space separated element into list
					List<String> tokens = Arrays.asList(missing.split("\\s+"));
					List<String> list = tokens.subList(0, tokens.size());
					modelImpl.applyChange(list,
							ModelIdentifingType.Type_L.class);
				} catch (Exception e1) {
					showError(e1);
				}
			}
		});
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		parent.setLayout(new GridLayout());
		createTabFolder(getComposite_1());

		// main tab
		TabItem tabItem = createTabItem(Translator.trans("Variable"));
		Group group = createGroup(tabItem, Translator.trans("Variable"));

		// concept ref
		List<LightXmlObjectType> conceptRefList = new ArrayList<LightXmlObjectType>();
		try {
			conceptRefList = DdiManager.getInstance()
					.getConceptsLight(null, null, null, null)
					.getLightXmlObjectList().getLightXmlObjectList();
		} catch (Exception e) {
			DialogUtil
					.errorDialog(getEditorSite(), ID, null, e.getMessage(), e);
		}
		ReferenceSelectionCombo conRefSelectCombo = null;
		conRefSelectCombo = createRefSelection(group,
				Translator.trans("VariableEditor.label.conceptref"),
				Translator.trans("VariableEditor.label.conceptref"),
				modelImpl.getConceptReference(), conceptRefList, false,
				ElementType.CONCEPT);
		conRefSelectCombo.addSelectionListener(Translator
				.trans("VariableEditor.label.conceptref"),
				new ReferenceSelectionAdapter(conRefSelectCombo, model,
						ModelIdentifingType.Type_B.class,
						getEditorIdentification()));

		// universe ref
		List<LightXmlObjectType> uniRefList = new ArrayList<LightXmlObjectType>();
		try {
			uniRefList = DdiManager.getInstance()
					.getUniversesLight(null, null, null, null)
					.getLightXmlObjectList().getLightXmlObjectList();
		} catch (Exception e) {
			DialogUtil
					.errorDialog(getEditorSite(), ID, null, e.getMessage(), e);
		}
		ReferenceSelectionCombo uniRefSelectCombo = null;
		uniRefSelectCombo = createRefSelection(group,
				Translator.trans("VariableEditor.label.universeref"),
				Translator.trans("VariableEditor.label.universeref"),
				modelImpl.getUniverseReference(), uniRefList, false,
				ElementType.UNIVERSE);
		uniRefSelectCombo.addSelectionListener(Translator
				.trans("VariableEditor.label.universeref"),
				new ReferenceSelectionAdapter(uniRefSelectCombo, model,
						ModelIdentifingType.Type_C.class,
						getEditorIdentification()));

		// question ref
		List<LightXmlObjectType> questionRefList = new ArrayList<LightXmlObjectType>();
		try {
			questionRefList = DdiManager.getInstance()
					.getQuestionItemsLight(null, null, null, null)
					.getLightXmlObjectList().getLightXmlObjectList();
			questionRefList.addAll(DdiManager
					.getInstance()
					.getMultipleQuestionQuestionItemsLight(null, null, null,
							null).getLightXmlObjectList()
					.getLightXmlObjectList());
		} catch (Exception e) {
			DialogUtil
					.errorDialog(getEditorSite(), ID, null, e.getMessage(), e);
		}
		ReferenceSelectionCombo refSelecCombo = null;
		refSelecCombo = createRefSelection(group,
				Translator.trans("VariableEditor.label.questionref"),
				Translator.trans("VariableEditor.label.questionref"),
				modelImpl.getQuestionReference(), questionRefList, false,
				ElementType.QUESTION_ITEM);
		refSelecCombo.addSelectionListener(Translator
				.trans("VariableEditor.label.questionref"),
				new ReferenceSelectionAdapter(refSelecCombo, model,
						ModelIdentifingType.Type_A.class,
						getEditorIdentification()));

		try {
			createResponseType(group);
		} catch (DDIFtpException e1) {
			DialogUtil.errorDialog(getEditorSite(), ID, null, e1.getMessage(),
					e1);
		}

		// description tab
		TabItem tabItem2 = createTabItem(Translator
				.trans("editor.label.description"));
		Group descriptionGroup = createGroup(tabItem2,
				Translator.trans("editor.label.description"));
		
		try {
			// name
			final Text nameText = createTextInput(descriptionGroup,
					Translator.trans("VariableEditor.label.name"),
					modelImpl.getName() == null ? "" : modelImpl.getName()
							.getStringValue(), false);
			
			// label
			final Text txt = createLabelInput(descriptionGroup,
					Translator.trans("editor.label.label"), modelImpl.getDocument()
							.getVariable().getLabelList(), modelImpl.getDocument()
							.getVariable().getId());
			
			nameText.addModifyListener(new TextStyledTextModyfiListener(model,
					NameType.class, getEditorIdentification()));

			nameText.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					Text text = (Text) e.getSource();
					setEditorTabName(text.getText() + " " + txt.getText());
				}
			});

			txt.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					Text text = (Text) e.getSource();
					setEditorTabName(nameText.getText() + " " + text.getText());
				}
			});

			// reset editor tab
			if (!nameText.getText().equals("")&&!txt.getText().equals("")) {
				setEditorTabName(nameText.getText() + " " + txt.getText());				
			}			

			createTranslation(descriptionGroup,
					Translator.trans("editor.button.translate"), modelImpl
							.getDocument().getVariable().getLabelList(),
					new LabelTdI(), "", txt);

			StyledText styledText = createStructuredStringInput(
					descriptionGroup,
					Translator.trans("editor.label.description"), modelImpl
							.getDocument().getVariable().getDescriptionList(),
					modelImpl.getDocument().getVariable().getId());
			createTranslation(descriptionGroup,
					Translator.trans("editor.button.translate"), modelImpl
							.getDocument().getVariable().getDescriptionList(),
					new DescriptionTdI(), "", styledText);
		} catch (DDIFtpException e) {
			showError(e);
		}

		// id tab
		createPropertiesTab(getTabFolder());

		// xml tab
		createXmlTab(modelImpl);

		// preview tab
		createPreviewTab(modelImpl);

		editorStatus.clearChanged();
	}

	public static void changeCodeRepresentationCodeValues(
			ReferenceType codeSchemeRef, Label codeValue) throws Exception {
		ReferenceResolution refRes = new ReferenceResolution(codeSchemeRef);
		CodeSchemeDocument codeScheme = CodeSchemeDao
				.getAllCodeSchemeByReference(refRes);
		StringBuilder codeValues = new StringBuilder("");
		if (codeScheme == null) {
			throw new DDIFtpException("Code Scheme, with ID: '"
					+ refRes.getId() + "' is not found!", new Throwable());
		}

		for (Iterator<CodeType> iterator = codeScheme.getCodeScheme()
				.getCodeList().iterator(); iterator.hasNext();) {
			codeValues.append(iterator.next().getValue());
			if (iterator.hasNext()) {
				codeValues.append(", ");
			}
		}
		codeValue.setText(codeValues.toString());
	}
}
