package org.ddialliance.ddieditor.ui.editor.question;

import java.util.ArrayList;
import java.util.List;

import org.ddialliance.ddi3.xml.xmlbeans.datacollection.DynamicTextType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.ReferenceType;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.dbxml.concept.ConceptDao;
import org.ddialliance.ddieditor.ui.dbxml.question.MultipleQuestionItemDao;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddieditor.ui.editor.widgetutil.genericmodifylistener.TextStyledTextModyfiListener;
import org.ddialliance.ddieditor.ui.editor.widgetutil.referenceselection.ReferenceSelectionAdapter;
import org.ddialliance.ddieditor.ui.editor.widgetutil.referenceselection.ReferenceSelectionCombo;
import org.ddialliance.ddieditor.ui.model.ModelIdentifingType;
import org.ddialliance.ddieditor.ui.model.question.MultipleQuestionItem;
import org.ddialliance.ddieditor.ui.model.translationdialoginput.DynamicTextTdI;
import org.ddialliance.ddieditor.ui.util.DialogUtil;
import org.ddialliance.ddieditor.ui.util.LanguageUtil;
import org.ddialliance.ddieditor.ui.view.Messages;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.ddialliance.ddiftp.util.xml.XmlBeansUtil;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.PartInitException;


/**
 * Multi Question Item Editor
 */

public class MultipleQuestionItemEditor extends Editor implements ISelectionListener {

	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			MultipleQuestionItemEditor.class);
	public static final String ID = "org.ddialliance.ddieditor.ui.editor.question.MultipleQuestionItemEditor";
	private MultipleQuestionItem modelImpl;
	private TableViewer tableViewer;

	public MultipleQuestionItemEditor() {
		super(
				Messages
						.getString("MultipleQuestionItemEditor.label.multipleQuestionItemEditorLabel.MultipleQuestionItemEditor"),
				Messages
						.getString("MultipleQuestionItemEditor.label.useTheEditorLabel.Description"));
		super.dao = new MultipleQuestionItemDao();
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout());
		super.createPartControl(parent);
		TabFolder multiQuestionTabFolder = createTabFolder(getComposite_1());

		// - Multiple Question Item Root Composite:
		final Composite multiQuestionComposite = new Composite(multiQuestionTabFolder,
				SWT.NONE);
		multiQuestionComposite.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));
		multiQuestionComposite.setLayout(new GridLayout());

		// - Multiple Question Item Tab Item:
		TabItem multiQuestionTabItem = new TabItem(multiQuestionTabFolder, SWT.NONE);
		multiQuestionTabItem.setControl(multiQuestionComposite);
		multiQuestionTabItem
				.setText(Messages
						.getString("MultipleQuestionItemEditor.label.multipleQuestionTabItem.MultipleQuestionItem")); //$NON-NLS-1$

		// - Multiple Question Item Group
		final Group multiQuestionGroup = new Group(multiQuestionComposite, SWT.NONE);
		final GridData gd_questionGroup = new GridData(SWT.FILL, SWT.CENTER,
				true, true);
		gd_questionGroup.heightHint = 646;
		gd_questionGroup.widthHint = 755;
		multiQuestionGroup.setLayoutData(gd_questionGroup);
		multiQuestionGroup.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));
		final GridLayout gridLayout_1 = new GridLayout();
		gridLayout_1.numColumns = 2;
		multiQuestionGroup.setLayout(gridLayout_1);
		multiQuestionGroup.setText("Multiple Question Item");
		
		// Concept Reference:

		// - Get available Concepts:
		List<LightXmlObjectType> conceptReferenceList = new ArrayList();
		try {
			conceptReferenceList = new ConceptDao().getLightXmlObject("", "",
					"", "");
		} catch (Exception e1) {
			String errMess = Messages
					.getString("MultipleQuestionItemEditor.mess.ConceptRetrievalError"); //$NON-NLS-1$
			ErrorDialog.openError(getSite().getShell(), Messages
					.getString("ErrorTitle"), null, new Status(IStatus.ERROR,
					ID, 0, errMess, e1));
		}

		// - Create Concept Reference selection combobox
		ReferenceSelectionCombo refSelecCombo = createRefSelection(
				multiQuestionGroup,
				Messages
						.getString("MultipleQuestionItemEditor.label.conceptLabel.Concept")
						+ ":",
				Messages
						.getString("MultipleQuestionItemEditor.label.conceptLabel.Concept"),
				modelImpl.getConceptReferenceType(), conceptReferenceList,
				false);
		refSelecCombo.addSelectionListener(Messages
				.getString("MultipleQuestionItemEditor.label.conceptLabel.Concept"),
				conceptReferenceList, new ReferenceSelectionAdapter(
						refSelecCombo, model, ReferenceType.class,
						getEditorIdentification()));

		// - Multiple Question Item Text
		DynamicTextType multiQuestionText = null;
		try {
			if (modelImpl.getQuestionText() != null) {
				multiQuestionText = (DynamicTextType) XmlBeansUtil.getLangElement(LanguageUtil.getDisplayLanguage(),
						modelImpl.getQuestionText());
			}
		} catch (DDIFtpException e) {
			DialogUtil.errorDialog(getEditorSite(), ID, null, e.getMessage(), e);
		}

		StyledText multiQuestionTxt = createTextAreaInput(multiQuestionGroup, Messages
				.getString("MultipleQuestionItemEditor.label.multipleQuestionItemTextLabel.MultipleQuestionItemText"), multiQuestionText == null ? ""
				: XmlBeansUtil.getTextOnMixedElement(multiQuestionText.getTextList().get(0)), false);

		multiQuestionTxt.addModifyListener(new TextStyledTextModyfiListener(modelImpl, ModelIdentifingType.Type_A.class,
				getEditorIdentification()));

		try {
			createTranslation(multiQuestionGroup, Messages.getString("editor.button.translate"),
					modelImpl.getQuestionText(), new DynamicTextTdI(), "", multiQuestionTxt);
		} catch (DDIFtpException e) {
			DialogUtil.errorDialog(getEditorSite(), ID, null, e.getMessage(), e);
		}
		

		// Create Property Tab Item:
		createPropertiesTab(multiQuestionTabFolder);

		// ddi xml tab
		createXmlTab(modelImpl);

		// Clean dirt from initialization
		editorStatus.clearChanged();
	}

	public Viewer getViewer() {
		log.debug("MultipleQuestionItemEditor.getViewer()");
		return this.tableViewer;
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		log.debug("MultipleQuestionItemEditor.init()");
		super.init(site, input);
		this.modelImpl = (MultipleQuestionItem) model;
	}


}