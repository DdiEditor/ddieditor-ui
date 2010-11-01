package org.ddialliance.ddieditor.ui.editor.variable;

import java.util.ArrayList;
import java.util.List;

import org.ddialliance.ddi3.xml.xmlbeans.reusable.NameType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.ReferenceType;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.dbxml.variable.VariableDao;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddieditor.ui.editor.widgetutil.genericmodifylistener.TextStyledTextModyfiListener;
import org.ddialliance.ddieditor.ui.editor.widgetutil.referenceselection.ReferenceSelectionAdapter;
import org.ddialliance.ddieditor.ui.editor.widgetutil.referenceselection.ReferenceSelectionCombo;
import org.ddialliance.ddieditor.ui.model.ModelIdentifingType;
import org.ddialliance.ddieditor.ui.model.translationdialoginput.DescriptionTdI;
import org.ddialliance.ddieditor.ui.model.translationdialoginput.LabelTdI;
import org.ddialliance.ddieditor.ui.model.variable.Variable;
import org.ddialliance.ddieditor.ui.util.DialogUtil;
import org.ddialliance.ddieditor.ui.view.Messages;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

public class VariableEditor extends Editor {
	public static final String ID = "org.ddialliance.ddieditor.ui.editor.variable.VariableEditor";
	Variable modelImpl;

	public VariableEditor() {
		super(
				Messages.getString("VariableEditor.label"),
				Messages.getString("VariableEditor.label.useTheEditorLabel.Description"));
		this.dao = new VariableDao();
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
		modelImpl = (Variable) model;
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		parent.setLayout(new GridLayout());
		createTabFolder(getComposite_1());

		// main tab
		TabItem tabItem = createTabItem(Messages.getString("Variable"));
		Group group = createGroup(tabItem, Messages.getString("Variable"));

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
				Messages.getString("VariableEditor.label.conceptref"),
				Messages.getString("VariableEditor.label.conceptref"),
				modelImpl.getQuestionReference(), conceptRefList, false);
		conRefSelectCombo.addSelectionListener(Messages
				.getString("VariableEditor.label.conceptref"), conceptRefList,
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
				Messages.getString("VariableEditor.label.conceptref"),
				Messages.getString("VariableEditor.label.conceptref"),
				modelImpl.getQuestionReference(), conceptRefList, false);
		uniRefSelectCombo.addSelectionListener(Messages
				.getString("VariableEditor.label.conceptref"), uniRefList,
				new ReferenceSelectionAdapter(conRefSelectCombo, model,
						ModelIdentifingType.Type_C.class,
						getEditorIdentification()));

		// question ref
		List<LightXmlObjectType> questionRefList = new ArrayList<LightXmlObjectType>();
		try {
			questionRefList = DdiManager.getInstance()
					.getQuestionItemsLight(null, null, null, null)
					.getLightXmlObjectList().getLightXmlObjectList();
		} catch (Exception e) {
			DialogUtil
					.errorDialog(getEditorSite(), ID, null, e.getMessage(), e);
		}
		ReferenceSelectionCombo refSelecCombo = null;
		refSelecCombo = createRefSelection(group,
				Messages.getString("VariableEditor.label.questionref"),
				Messages.getString("VariableEditor.label.questionref"),
				modelImpl.getQuestionReference(), questionRefList, false);
		refSelecCombo.addSelectionListener(Messages
				.getString("VariableEditor.label.questionref"),
				questionRefList, new ReferenceSelectionAdapter(refSelecCombo,
						model, ModelIdentifingType.Type_A.class,
						getEditorIdentification()));

		// description tab
		TabItem tabItem2 = createTabItem(Messages
				.getString("editor.label.description"));
		Group group2 = createGroup(tabItem2,
				Messages.getString("editor.label.description"));

		try {
			Text txt = createLabelInput(group2,
					Messages.getString("editor.label.label"), modelImpl
							.getDocument().getVariable().getLabelList(),
					modelImpl.getDocument().getVariable().getId());

			createTranslation(group2,
					Messages.getString("editor.button.translate"), modelImpl
							.getDocument().getVariable().getLabelList(),
					new LabelTdI(), "", txt);

			StyledText styledText = createStructuredStringInput(group2,
					Messages.getString("editor.label.description"), modelImpl
							.getDocument().getVariable().getDescriptionList(),
					modelImpl.getDocument().getVariable().getId());
			createTranslation(group2,
					Messages.getString("editor.button.translate"), modelImpl
							.getDocument().getVariable().getDescriptionList(),
					new DescriptionTdI(), "", styledText);

			// name
			Text nameText = createTextInput(group2,
					Messages.getString("VariableEditor.label.name"),
					modelImpl.getName() == null ? "" : modelImpl.getName()
							.getStringValue(), false);
			nameText.addModifyListener(new TextStyledTextModyfiListener(model,
					NameType.class, getEditorIdentification()));
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
}
