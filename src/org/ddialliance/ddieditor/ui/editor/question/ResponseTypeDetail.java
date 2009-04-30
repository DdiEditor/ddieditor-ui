package org.ddialliance.ddieditor.ui.editor.question;

import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.ddialliance.ddi_3_0.xml.xmlbeans.datacollection.CodeDomainType;
import org.ddialliance.ddi_3_0.xml.xmlbeans.datacollection.NumericDomainType;
import org.ddialliance.ddi_3_0.xml.xmlbeans.reusable.NumericTypeCodeType;
import org.ddialliance.ddi_3_0.xml.xmlbeans.reusable.RepresentationType;
import org.ddialliance.ddi_3_0.xml.xmlbeans.reusable.TextDomainType;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.dbxml.CodeSchemes;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddieditor.ui.editor.FilteredItemsSelection;
import org.ddialliance.ddieditor.ui.editor.Editor.EditorStatus;
import org.ddialliance.ddieditor.ui.editor.Editor.FIELD_TYPE;
import org.ddialliance.ddieditor.ui.model.QuestionItem;
import org.ddialliance.ddieditor.ui.reference.ResponseTypeReference;
import org.ddialliance.ddieditor.ui.view.Messages;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorSite;

public class ResponseTypeDetail {

	private static Log log = LogFactory.getLog(LogType.SYSTEM, ResponseTypeDetail.class);

	// Member variables:
	private QuestionItem questionItem;
	private EditorStatus editorStatus;
	private Composite parentLabelComposite;
	private Composite parentCodeComposite;
	private IEditorSite site;
	private Text lengthText;
	private Label decimalPositionLabel = null;
	private Text decimalPosition = null;
	private Combo numTypeCombo = null;
	private ComboViewer codeSchemeComboViewer;
	// private RepresentationType m_representationType;

	public static String[] RESPONSE_TYPE_LABELS = { "", Messages.getString("ResponseTypeDetail.label.Code"),
			Messages.getString("ResponseTypeDetail.label.Text"),
			Messages.getString("ResponseTypeDetail.label.Numeric"),
			Messages.getString("ResponseTypeDetail.label.Date"),
			Messages.getString("ResponseTypeDetail.label.Category"),
			Messages.getString("ResponseTypeDetail.label.Geographic") };

	// Note: RESPONSE_TYPES is used as index in RESPONSE_TYPE_LABELS
	public enum RESPONSE_TYPES {
		UNDEFINED, CODE, TEXT, NUMERIC, DATE, CATEGORY, GEOGRAPHIC
	};

	static private final String[] NUMERIC_TYPES = { Messages.getString("ResponseTypeDetail.label.Integer"),
			Messages.getString("ResponseTypeDetail.label.Float") };

	static private enum NUMERIC_TYPE_INDEX {
		INTEGER, FLOAT
	};

	public ResponseTypeDetail(QuestionItem questionItem, Composite parentLabelComposite, Composite parentCodeComposite,
			EditorStatus editorStatus, IEditorSite site) {
		this.parentLabelComposite = parentLabelComposite;
		this.parentCodeComposite = parentCodeComposite;
		this.editorStatus = editorStatus;
		this.questionItem = questionItem;
		this.site = site;
	}

	static public String[] getResponseTypeLabels() {
		return RESPONSE_TYPE_LABELS;
	}

	static public String getResponseTypeLabel(RESPONSE_TYPES rt) {
		return RESPONSE_TYPE_LABELS[rt.ordinal()];
	}

	/**
	 * Return list of Response Domain references
	 * 
	 * @return List<ResponseDomainReference>
	 */
	static public List<ResponseTypeReference> getResponseDomainReferenceList() {
		List<ResponseTypeReference> responseDomainReferenceList = new ArrayList<ResponseTypeReference>();

		responseDomainReferenceList.add(new ResponseTypeReference(RESPONSE_TYPE_LABELS[RESPONSE_TYPES.UNDEFINED
				.ordinal()], RESPONSE_TYPES.UNDEFINED));
		responseDomainReferenceList.add(new ResponseTypeReference(RESPONSE_TYPE_LABELS[RESPONSE_TYPES.CODE.ordinal()],
				RESPONSE_TYPES.CODE));
		responseDomainReferenceList.add(new ResponseTypeReference(RESPONSE_TYPE_LABELS[RESPONSE_TYPES.TEXT.ordinal()],
				RESPONSE_TYPES.TEXT));
		responseDomainReferenceList.add(new ResponseTypeReference(
				RESPONSE_TYPE_LABELS[RESPONSE_TYPES.NUMERIC.ordinal()], RESPONSE_TYPES.NUMERIC));
		responseDomainReferenceList.add(new ResponseTypeReference(RESPONSE_TYPE_LABELS[RESPONSE_TYPES.DATE.ordinal()],
				RESPONSE_TYPES.DATE));
		responseDomainReferenceList.add(new ResponseTypeReference(RESPONSE_TYPE_LABELS[RESPONSE_TYPES.CATEGORY
				.ordinal()], RESPONSE_TYPES.CATEGORY));
		responseDomainReferenceList.add(new ResponseTypeReference(RESPONSE_TYPE_LABELS[RESPONSE_TYPES.GEOGRAPHIC
				.ordinal()], RESPONSE_TYPES.GEOGRAPHIC));

		return responseDomainReferenceList;
	}

	/**
	 * Dispose Response Type Details
	 */
	public void dispose() {
		log.debug("Dispose Response Type Details");

		Control[] children = parentLabelComposite.getChildren();
		for (int i = 0; i < children.length; i++) {
			children[i].dispose();
		}
		children = parentCodeComposite.getChildren();
		for (int i = 0; i < children.length; i++) {
			children[i].dispose();
		}
	}

	/**
	 * Set Question Response Type details e.g. detail of NUMERIC i.e. Integer or
	 * Float
	 * 
	 * @param responseType
	 *            CODE, TEXT, etc.
	 * @param responseDomain
	 * 
	 */
	public void setDetails(RepresentationType representationType) {
//	public void setDetails(RESPONSE_TYPES responseType) {

		// m_representationType = representationType;

		RESPONSE_TYPES responseType = ResponseTypeDetail.getResponseType(representationType);

		this.dispose();

		if (responseType == RESPONSE_TYPES.UNDEFINED) {
			return;
		} else if (responseType == RESPONSE_TYPES.TEXT) {
			
			final Label textLabel = new Label(parentLabelComposite, SWT.NONE);
			textLabel.setLayoutData(new GridData());
			textLabel.setText(Messages.getString("ResponseTypeDetail.label.Text") + ":"); //$NON-NLS-1$
			textLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

			final Label dummyLabel = new Label(parentCodeComposite, SWT.NONE);

			final Label LengthLabel = new Label(parentLabelComposite, SWT.NONE);
			LengthLabel.setText(Messages.getString("ResponseTypeDetail.label.MaxLength") + ":"); //$NON-NLS-1$
			LengthLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

			lengthText = new Text(parentCodeComposite, SWT.BORDER);
			lengthText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			if (representationType != null) {
				BigInteger maxLength = ((TextDomainType) representationType).getMaxLength();
				if (maxLength != null) {
					lengthText.setText(maxLength.toString());
				}
			}
			lengthText.addVerifyListener(new VerifyListener() {
				public void verifyText(final VerifyEvent e) {
					Editor.verifyField(FIELD_TYPE.DIGIT, e, site);
					System.out.println("lengthText: "+lengthText.getText());
				}
			});
			lengthText.addModifyListener(new ModifyListener() {
				public void modifyText(final ModifyEvent e) {
					log.debug(".modifyText(): " + lengthText.getText());
					questionItem.setTextResponseDomain(new BigInteger(lengthText.getText()));
					editorStatus.setChanged();
				}
			});
			parentLabelComposite.getParent().layout();
			return;
		} else if (responseType == RESPONSE_TYPES.NUMERIC) {
			
			// ***** NUMERIC *****
			//--------------------
			String numericType = "";
			final Label numericLabel = new Label(parentLabelComposite, SWT.NONE);
			numericLabel.setLayoutData(new GridData());
			numericLabel.setText(Messages.getString("ResponseTypeDetail.label.Numeric") + ":"); //$NON-NLS-1$
			numericLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

			final Label dummyLabel = new Label(parentCodeComposite, SWT.NONE);

			final Label integerLabel = new Label(parentLabelComposite, SWT.NONE);
			integerLabel.setText(Messages.getString("ResponseTypeDetail.label.Type") + ":"); //$NON-NLS-1$
			integerLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

			numTypeCombo = new Combo(parentCodeComposite, SWT.READ_ONLY);
			final GridData gd_langCombo = new GridData(SWT.FILL, SWT.CENTER, true, false);
			numTypeCombo.setLayoutData(gd_langCombo);
			numTypeCombo.setItems(NUMERIC_TYPES);
			if (representationType != null) {
				NumericTypeCodeType.Enum codeType = ((NumericDomainType) representationType).getType();
				if (codeType != null) {
					if (codeType.equals(NumericTypeCodeType.INTEGER)) {
						numTypeCombo.setText(NUMERIC_TYPES[NUMERIC_TYPE_INDEX.INTEGER.ordinal()]);
					} else {
						numTypeCombo.setText(NUMERIC_TYPES[NUMERIC_TYPE_INDEX.FLOAT.ordinal()]);
					}
				} else {
					// Default:
					((NumericDomainType) representationType).setType(NumericTypeCodeType.INTEGER);
					numTypeCombo.setText(NUMERIC_TYPES[NUMERIC_TYPE_INDEX.INTEGER.ordinal()]);
				}
			}
			numTypeCombo.addModifyListener(new ModifyListener() {
				public void modifyText(final ModifyEvent e) {
					log.debug(".modifyText(): " + numTypeCombo.getText());
					log.debug("Layout Data: "+parentLabelComposite.getLayoutData());
					if (numTypeCombo.getText().equals(Messages.getString("ResponseTypeDetail.label.Float"))) {
						decimalPositionLabel = new Label(parentLabelComposite, SWT.NONE);
						decimalPositionLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
						decimalPositionLabel
								.setText(Messages.getString("ResponseTypeDetail.label.DecimalPosition") + ":"); //$NON-NLS-1$
						decimalPositionLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

						decimalPosition = new Text(parentCodeComposite, SWT.BORDER);
						decimalPosition.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
						decimalPosition.setText("");
						decimalPosition.addVerifyListener(new VerifyListener() {
							public void verifyText(final VerifyEvent e) {
								Editor.verifyField(FIELD_TYPE.DIGIT, e, site);
								System.out.println("decimalPosition: "+decimalPosition.getText());
							}
						});
						decimalPosition.addModifyListener(new ModifyListener() {
							public void modifyText(final ModifyEvent e) {
								log.debug(".modifyText(): " + decimalPosition.getText());
								questionItem.setNumericResponseDomain(NumericTypeCodeType.FLOAT, new BigInteger(
										decimalPosition.getText()));
								editorStatus.setChanged();
							}
						});
					} else {
						if (decimalPositionLabel != null) {
							decimalPositionLabel.dispose();
						}
						if (decimalPosition != null) {
							decimalPosition.dispose();
						}
						questionItem.setNumericResponseDomain(NumericTypeCodeType.INTEGER, null);
						editorStatus.setChanged();
					}
					parentLabelComposite.getParent().layout();
				}
			});
			if (numericType.equals("Float")) {
				decimalPositionLabel = new Label(parentLabelComposite, SWT.NONE);
				decimalPositionLabel.setText(Messages.getString("ResponseTypeDetail.label.DecimalPosition") + ":"); //$NON-NLS-1$
				decimalPositionLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

				decimalPosition = new Text(parentCodeComposite, SWT.BORDER);
				decimalPosition.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
				if (representationType != null) {
					decimalPosition.setText(((NumericDomainType) representationType).getDecimalPositions().toString());
				}
			}
			parentLabelComposite.getParent().layout();
			return;
		} else if (responseType == RESPONSE_TYPES.CODE) {
			
			// ***** CODE *****
			//-----------------
			final FilteredItemsSelection filteredItemsSelection = new FilteredItemsSelection(); 

			final Composite labelComposite = new Composite(parentLabelComposite, SWT.NONE);
			labelComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
			labelComposite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
			final GridLayout gridLayout_3 = new GridLayout();
			gridLayout_3.marginWidth = 0;
			gridLayout_3.marginHeight = 0;
			labelComposite.setLayout(gridLayout_3);

			final Composite codeComposite = new Composite(parentCodeComposite, SWT.NONE);
			codeComposite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
			codeComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
			codeComposite.setLayout(new GridLayout());

			// - Get available Code Schemes:
			List<LightXmlObjectType> codeSchemeReferenceList = new ArrayList();
			try {
				codeSchemeReferenceList = CodeSchemes.getCodeSchemesLight("", "", "", "");
			} catch (Exception e1) {
				String errMess = MessageFormat.format(Messages
						.getString("ResponseTypeDetail.mess.CodeSchemeRetrievalError"), e1.getMessage()); //$NON-NLS-1$
				MessageDialog.openError(parentCodeComposite.getShell(), Messages.getString("ErrorTitle"), errMess);
			}

			// - Create Code Scheme selection composite: 
			filteredItemsSelection.createPartControl(labelComposite, codeComposite, 
					Messages.getString("ResponseTypeDetail.label.Code"), 
					Messages.getString("ResponseTypeDetail.label.CodeScheme"),
					codeSchemeReferenceList,
					representationType != null ? ((CodeDomainType) representationType).getCodeSchemeReferenceArray(0)
							.getIDArray(0).getStringValue() : "");
			filteredItemsSelection.addSelectionListener(Messages.getString("ResponseTypeDetail.label.SelectCodeShemeReference"), codeSchemeReferenceList,
					new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					LightXmlObjectType result = (LightXmlObjectType) filteredItemsSelection.getResult();
					if (result != null) {
						questionItem.setCodeResponseDomain(result.getId());
						editorStatus.setChanged();
					}
				}
			});

			parentLabelComposite.getParent().layout();
			return;
		} else {
			// ***** OTHER *****
			//------------------
			String errMess = MessageFormat.format(Messages
					.getString("ResponseTypeDetail.mess.QuestionItemResponseTypeNotSupported"), responseType); //$NON-NLS-1$
			MessageDialog.openError(parentLabelComposite.getShell(), Messages.getString("ErrorTitle"), errMess);
		}
	}

	/**
	 * Return Response Type of a given Response Domain
	 * 
	 * @param representationType
	 * @return
	 */
	static public RESPONSE_TYPES getResponseType(RepresentationType representationType) {

		String responseType = representationType.getClass().getSimpleName();
		RESPONSE_TYPES responseTypeEnum = null;
		if (responseType.equals("RepresentationTypeImpl")) {
			responseTypeEnum = RESPONSE_TYPES.UNDEFINED;
		} else if (responseType.equals("CodeDomainTypeImpl")) {
			responseTypeEnum = RESPONSE_TYPES.CODE;
		} else if (responseType.equals("TextDomainTypeImpl")) {
			responseTypeEnum = RESPONSE_TYPES.TEXT;
		} else if (responseType.equals("NumericDomainTypeImpl")) {
			responseTypeEnum = RESPONSE_TYPES.NUMERIC;
		} else if (responseType.equals("DateDomainTypeImpl")) {
			responseTypeEnum = RESPONSE_TYPES.DATE;
		} else if (responseType.equals("CategoryDomainTypeImpl")) {
			responseTypeEnum = RESPONSE_TYPES.CATEGORY;
		} else if (responseType.equals("GeographicDomainTypeImpl")) {
			responseTypeEnum = RESPONSE_TYPES.GEOGRAPHIC;
		}
		return responseTypeEnum;
	}

}
