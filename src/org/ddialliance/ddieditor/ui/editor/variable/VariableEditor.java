package org.ddialliance.ddieditor.ui.editor.variable;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.ddialliance.ddi3.xml.xmlbeans.logicalproduct.CodeRepresentationType;
import org.ddialliance.ddi3.xml.xmlbeans.logicalproduct.CodeSchemeDocument;
import org.ddialliance.ddi3.xml.xmlbeans.logicalproduct.CodeType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.DateTimeRepresentationType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.DateTypeCodeType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.ExternalCategoryRepresentationType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.NameType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.NumericRepresentationType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.NumericTypeCodeType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.ReferenceType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.TextRepresentationType;
import org.ddialliance.ddieditor.logic.urn.ddi.ReferenceResolution;
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
import org.ddialliance.ddiftp.util.Translator;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
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
		super(
				Messages.getString("VariableEditor.label"),
				Messages.getString("VariableEditor.label.useTheEditorLabel.Description"),
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
				modelImpl.getConceptReference(), conceptRefList, false);
		conRefSelectCombo.addSelectionListener(Messages
				.getString("VariableEditor.label.conceptref"),
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
				Messages.getString("VariableEditor.label.universeref"),
				Messages.getString("VariableEditor.label.universeref"),
				modelImpl.getUniverseReference(), uniRefList, false);
		uniRefSelectCombo.addSelectionListener(Messages
				.getString("VariableEditor.label.universeref"),
				new ReferenceSelectionAdapter(uniRefSelectCombo, model,
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
				new ReferenceSelectionAdapter(refSelecCombo, model,
						ModelIdentifingType.Type_A.class,
						getEditorIdentification()));

		// representation
		createLabel(group,
				Messages.getString("VariableEditor.label.representation"))
				.setLayoutData(
						new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
		Group representationGroup = createGroup(group,
				Messages.getString("VariableEditor.label.representation"));
		representationGroup.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING,
				true, true, 1, 1));

		Object repImpl = modelImpl.getValueRepresentation();
		// CodeRepresentation
		try {
			if (repImpl instanceof CodeRepresentationType) {
				representationGroup.setText(Messages
						.getString("VariableEditor.label.coderepresentation"));
				ReferenceType codeSchemeRef = ((CodeRepresentationType) repImpl)
						.getCodeSchemeReference();

				// code schemes light
				List<LightXmlObjectType> codeSchemeRefList = new ArrayList<LightXmlObjectType>();
				try {
					codeSchemeRefList = DdiManager.getInstance()
							.getCodeSchemesLight(null, null, null, null)
							.getLightXmlObjectList().getLightXmlObjectList();
				} catch (Exception e) {
					DialogUtil.errorDialog(getEditorSite(), ID, null,
							e.getMessage(), e);
				}

				// code scheme selection
				final ReferenceSelectionCombo refSelectCodeSchemeCombo = createRefSelection(
						representationGroup,
						Messages.getString("VariableEditor.label.codeschemereference"),
						Messages.getString("VariableEditor.label.codeschemereference"),
						codeSchemeRef, codeSchemeRefList, false);
				refSelectCodeSchemeCombo.addSelectionListener(Messages
						.getString("VariableEditor.label.codeschemereference"),
						new ReferenceSelectionAdapter(refSelectCodeSchemeCombo,
								model, ModelIdentifingType.Type_D.class,
								getEditorIdentification()));

				// codes values
				createLabel(
						representationGroup,
						Messages.getString("VariableEditor.label.coderepresentation.codevalues"));
				final Label codeValue = createLabel(representationGroup, "");
				codeValue.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER,
						false, false, 1, 1));
				changeCodeRepresentationCodeValues(codeSchemeRef, codeValue);
				refSelectCodeSchemeCombo.getCombo().addModifyListener(
						new ModifyListener() {
							@Override
							public void modifyText(ModifyEvent e) {
								try {
									changeCodeRepresentationCodeValues(
											new ReferenceResolution(
													refSelectCodeSchemeCombo
															.getResult())
													.getReference(), codeValue);
								} catch (Exception e1) {
									showError(e1);
								}
							}
						});
			}
			// NumericRepresentation
			if (repImpl instanceof NumericRepresentationType) {
				NumericRepresentationType numRep = (NumericRepresentationType) repImpl;
				representationGroup
						.setText(Messages
								.getString("VariableEditor.label.numericrepresentation"));
				// numeric type
				createLabel(
						representationGroup,
						Messages.getString("VariableEditor.label.numericrepresentation.types"));
				Combo numericCombo = createCombo(representationGroup,
						Variable.NUMERIC_TYPES);
				int search = numRep.getType().intValue();
				if (search != 0) {
					search--;
				}
				for (int i = 0; i < Variable.NUMERIC_TYPES.length; i++) {
					if (search == i) {
						numericCombo.select(i);
					}
				}
				numericCombo.addModifyListener(new ModifyListener() {
					@Override
					public void modifyText(ModifyEvent event) {
						editorStatus.setChanged();
						int index = ((Combo) event.getSource())
								.getSelectionIndex();
						index++;
						try {
							modelImpl.applyChange(
									NumericTypeCodeType.Enum.forInt(index),
									ModelIdentifingType.Type_E.class);
						} catch (Exception e) {
							showError(e);
						}
					}
				});
				if (!modelImpl.getNumericDecimalPosition().toString()
						.equals("0")) {
					Text numericDecimalPositionText = createTextInput(
							representationGroup,
							Messages.getString("VariableEditor.label.numericrepresentation.decimalpositions"),
							modelImpl.getNumericDecimalPosition().toString(),
							false);
					numericDecimalPositionText
							.addModifyListener(new ModifyListener() {
								@Override
								public void modifyText(ModifyEvent e) {
									editorStatus.setChanged();

									try {
										BigInteger bigint = null;
										bigint = new BigInteger(((Text) e
												.getSource()).getText());
										modelImpl
												.applyChange(
														bigint,
														ModelIdentifingType.Type_F.class);
									} catch (Exception e1) {
										showError(e1);
									}
								}
							});
				}
				StringBuffer missingValues = new StringBuffer();
				List list = modelImpl.getMissingValue();
				for (Iterator iterator = list.iterator(); iterator.hasNext();) {
					String missingValue = (String) iterator.next();
					if (iterator.hasNext()) {
						missingValues.append(missingValue + " ");
					} else {
						missingValues.append(missingValue);
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
							List<String> tokens = Arrays.asList(missing
									.split("\\s+"));
							List<String> list = tokens.subList(0, tokens.size());
							modelImpl.applyChange(list,
									ModelIdentifingType.Type_L.class);
						} catch (Exception e1) {
							showError(e1);
						}
					}
				});
			}
			// TextRepresentation
			if (repImpl instanceof TextRepresentationType) {
				TextRepresentationType rep = (TextRepresentationType) repImpl;
				representationGroup.setText(Messages
						.getString("VariableEditor.label.textrepresentation"));
				// min length
				Text minlengthText = createTextInput(
						representationGroup,
						Messages.getString("VariableEditor.label.textrepresentation.minlength"),
						modelImpl.getMinLength() == null ? "" : modelImpl
								.getMinLength().toString(), false);

				minlengthText.addVerifyListener(new VerifyListener() {
					public void verifyText(final VerifyEvent e) {
						Editor.verifyField(FIELD_TYPE.DIGIT, e, getSite());
					}
				});

				minlengthText.addModifyListener(new ModifyListener() {
					@Override
					public void modifyText(ModifyEvent e) {
						try {
							BigInteger bigint = Translator
									.stringToBigInt(((Text) e.getSource())
											.getText());
							modelImpl.applyChange(bigint,
									ModelIdentifingType.Type_G.class);
							editorStatus.setChanged();
						} catch (Exception e1) {
							showError(e1);
						}
					}
				});

				// max length
				Text maxlengthText = createTextInput(
						representationGroup,
						Messages.getString("VariableEditor.label.textrepresentation.maxlength"),
						modelImpl.getMaxLength() == null ? "" : modelImpl
								.getMaxLength().toString(), false);

				maxlengthText.addVerifyListener(new VerifyListener() {
					public void verifyText(final VerifyEvent e) {
						Editor.verifyField(FIELD_TYPE.DIGIT, e, getSite());
					}
				});

				maxlengthText.addModifyListener(new ModifyListener() {
					@Override
					public void modifyText(ModifyEvent e) {
						try {
							BigInteger bigint = Translator
									.stringToBigInt(((Text) e.getSource())
											.getText());
							modelImpl.applyChange(bigint,
									ModelIdentifingType.Type_I.class);
							editorStatus.setChanged();
						} catch (Exception e1) {
							showError(e1);
						}
					}
				});

				// regx
				Text regxText = createTextInput(
						representationGroup,
						Messages.getString("VariableEditor.label.textrepresentation.regx"),
						modelImpl.getRegx() == null ? "" : modelImpl.getRegx(),
						false);
				regxText.addModifyListener(new ModifyListener() {
					@Override
					public void modifyText(ModifyEvent e) {
						try {
							modelImpl.applyChange(
									((Text) e.getSource()).getText(),
									ModelIdentifingType.Type_H.class);
							editorStatus.setChanged();
						} catch (Exception e1) {
							showError(e1);
						}
					}
				});
			}
			// DateTimeRepresentation
			if (repImpl instanceof DateTimeRepresentationType) {
				representationGroup
						.setText(Messages
								.getString("VariableEditor.label.datetimerepresentation"));

				// format
				Text format = createTextInput(
						representationGroup,
						Messages.getString("VariableEditor.label.datetimerepresentation.format"),
						modelImpl.getFormat() == null ? "" : modelImpl
								.getFormat(), false);
				format.addModifyListener(new ModifyListener() {
					@Override
					public void modifyText(ModifyEvent e) {
						try {
							modelImpl.applyChange(
									((Text) e.getSource()).getText(),
									ModelIdentifingType.Type_J.class);
							editorStatus.setChanged();
						} catch (Exception e1) {
							showError(e1);
						}
					}
				});

				// date time type
				createLabel(
						representationGroup,
						Messages.getString("VariableEditor.label.datetimerepresentation.type"));
				Combo datetimeCombo = createCombo(representationGroup,
						Variable.DATE_TIME_TYPES);
				DateTypeCodeType.Enum dateTime = modelImpl.getDateTimeType();
				int value = dateTime.intValue();
				datetimeCombo.select(--value);
				datetimeCombo.addModifyListener(new ModifyListener() {
					@Override
					public void modifyText(ModifyEvent e) {
						try {
							int select = ((Combo) e.getSource())
									.getSelectionIndex();
							modelImpl.executeChange(++select,
									ModelIdentifingType.Type_K.class);
							editorStatus.setChanged();
						} catch (Exception e1) {
							showError(e1);
						}
					}
				});
			}

			// ExternalCategoryRepresentation
			if (repImpl instanceof ExternalCategoryRepresentationType) {
				// TODO external category representation
				representationGroup
						.setText(Messages
								.getString("VariableEditor.label.externalcategoryrepresentation"));
				createLabel(representationGroup, "Not implemented!");
			}
		} catch (Exception e) {
			if (!(e instanceof DDIFtpException)) {
				DDIFtpException ddiE = new DDIFtpException(e);
				ddiE.setRealThrowable(new Throwable());
			}
		}

		// description tab
		TabItem tabItem2 = createTabItem(Messages
				.getString("editor.label.description"));
		Group descriptionGroup = createGroup(tabItem2,
				Messages.getString("editor.label.description"));

		try {
			// name
			Text nameText = createTextInput(descriptionGroup,
					Messages.getString("VariableEditor.label.name"),
					modelImpl.getName() == null ? "" : modelImpl.getName()
							.getStringValue(), false);
			nameText.addModifyListener(new TextStyledTextModyfiListener(model,
					NameType.class, getEditorIdentification()));

			Text txt = createLabelInput(descriptionGroup,
					Messages.getString("editor.label.label"), modelImpl
							.getDocument().getVariable().getLabelList(),
					modelImpl.getDocument().getVariable().getId());

			createTranslation(descriptionGroup,
					Messages.getString("editor.button.translate"), modelImpl
							.getDocument().getVariable().getLabelList(),
					new LabelTdI(), "", txt);

			StyledText styledText = createStructuredStringInput(
					descriptionGroup,
					Messages.getString("editor.label.description"), modelImpl
							.getDocument().getVariable().getDescriptionList(),
					modelImpl.getDocument().getVariable().getId());
			createTranslation(descriptionGroup,
					Messages.getString("editor.button.translate"), modelImpl
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
		CodeSchemeDocument codeScheme = getCodeScheme(refRes);
		StringBuilder codeValues = new StringBuilder("");
		if (codeScheme == null) {
			throw new DDIFtpException("Code Scheme, with ID: " + refRes.getId()
					+ " is not found!", new Throwable());
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

	private static CodeSchemeDocument getCodeScheme(ReferenceResolution refRes)
			throws Exception {
		List<LightXmlObjectType> codeSchemeRefList = DdiManager.getInstance()
				.getCodeSchemesLight(null, null, null, null)
				.getLightXmlObjectList().getLightXmlObjectList();
		for (LightXmlObjectType lightXmlObject : codeSchemeRefList) {
			if (lightXmlObject.getId().equals(refRes.getId())) {
				return DdiManager.getInstance().getCodeScheme(
						lightXmlObject.getId(), lightXmlObject.getVersion(),
						lightXmlObject.getParentId(),
						lightXmlObject.getParentVersion());
			}
		}
		return null;
	}
}
