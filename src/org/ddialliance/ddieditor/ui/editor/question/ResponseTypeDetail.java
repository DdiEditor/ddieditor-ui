package org.ddialliance.ddieditor.ui.editor.question;

/**
 * Response Type Details.
 * 
 */
/*
 * $Author$ 
 * $Date$ 
 * $Revision$
 */

import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.xalan.transformer.DecimalToRoman;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.CodeDomainType;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.NumericDomainType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.NumericTypeCodeType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.NumericTypeCodeType.Enum;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.RepresentationType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.TextDomainType;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.dbxml.code.CodeSchemeDao;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddieditor.ui.editor.Editor.EditorStatus;
import org.ddialliance.ddieditor.ui.editor.Editor.FIELD_TYPE;
import org.ddialliance.ddieditor.ui.editor.FilteredItemsSelection;
import org.ddialliance.ddieditor.ui.model.question.QuestionItem;
import org.ddialliance.ddieditor.ui.model.question.QuestionItem.RESPONSE_TYPES;
import org.ddialliance.ddieditor.ui.model.question.ResponseTypeReference;
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

	public class DecimalPositionModifyListener implements ModifyListener {

		@Override
		public void modifyText(ModifyEvent e) {
			log.debug(".modifyText(): " + decimalPosition.getText());
			BigInteger pos = null;
			if (decimalPosition.getText().length() > 0) {
				pos = new BigInteger(decimalPosition.getText());
			}
			Enum type = null;
			if (numTypeCombo.getText().equals(
					Messages.getString("ResponseTypeDetail.label.Double"))) {
				type = NumericTypeCodeType.DOUBLE;
			} else {
				type = NumericTypeCodeType.FLOAT;
			}
			questionItem.setNumericResponseDomain(type, pos);
			editorStatus.setChanged();
		}
	}

	public class DecimalPositionVerifyListener implements VerifyListener {

		@Override
		public void verifyText(VerifyEvent e) {
			Editor.verifyField(
					FIELD_TYPE.DIGIT, e, site);
		}
	}
	
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			ResponseTypeDetail.class);

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

	public static String[] RESPONSE_TYPE_LABELS = { "",
			Messages.getString("ResponseTypeDetail.label.Code"),
			Messages.getString("ResponseTypeDetail.label.Text"),
			Messages.getString("ResponseTypeDetail.label.Numeric"),
			Messages.getString("ResponseTypeDetail.label.Date"),
			Messages.getString("ResponseTypeDetail.label.Category"),
			Messages.getString("ResponseTypeDetail.label.Geographic") };

	static private final String[] NUMERIC_TYPES = {
			Messages.getString("ResponseTypeDetail.label.Integer"),
			Messages.getString("ResponseTypeDetail.label.Double"),
			Messages.getString("ResponseTypeDetail.label.Float") };

	static private enum NUMERIC_TYPE_INDEX {
		INTEGER, DOUBLE, FLOAT
	};

	public ResponseTypeDetail(QuestionItem questionItem,
			Composite parentLabelComposite, Composite parentCodeComposite,
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

		responseDomainReferenceList.add(new ResponseTypeReference(
				RESPONSE_TYPE_LABELS[RESPONSE_TYPES.UNDEFINED.ordinal()],
				RESPONSE_TYPES.UNDEFINED));
		responseDomainReferenceList.add(new ResponseTypeReference(
				RESPONSE_TYPE_LABELS[RESPONSE_TYPES.CODE.ordinal()],
				RESPONSE_TYPES.CODE));
		responseDomainReferenceList.add(new ResponseTypeReference(
				RESPONSE_TYPE_LABELS[RESPONSE_TYPES.TEXT.ordinal()],
				RESPONSE_TYPES.TEXT));
		responseDomainReferenceList.add(new ResponseTypeReference(
				RESPONSE_TYPE_LABELS[RESPONSE_TYPES.NUMERIC.ordinal()],
				RESPONSE_TYPES.NUMERIC));
		responseDomainReferenceList.add(new ResponseTypeReference(
				RESPONSE_TYPE_LABELS[RESPONSE_TYPES.DATE.ordinal()],
				RESPONSE_TYPES.DATE));
		responseDomainReferenceList.add(new ResponseTypeReference(
				RESPONSE_TYPE_LABELS[RESPONSE_TYPES.CATEGORY.ordinal()],
				RESPONSE_TYPES.CATEGORY));
		responseDomainReferenceList.add(new ResponseTypeReference(
				RESPONSE_TYPE_LABELS[RESPONSE_TYPES.GEOGRAPHIC.ordinal()],
				RESPONSE_TYPES.GEOGRAPHIC));

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
	 * Dispose Decimal position details
	 */
	private void disposeDecimalPosition() {
		if (decimalPositionLabel != null) {
			decimalPositionLabel.dispose();
			decimalPositionLabel = null;
		}
		if (decimalPosition != null) {
			decimalPosition.dispose();
			decimalPosition = null;
		}
	}
	
	/**
	 * Create Decimal Position details
	 */
	private void createDecimalPosition (String position) {
		if (decimalPositionLabel == null || decimalPositionLabel.isDisposed()) {
			decimalPositionLabel = new Label(
					parentLabelComposite, SWT.NONE);
			decimalPositionLabel.setLayoutData(new GridData(
					SWT.RIGHT, SWT.CENTER, true, false));
			decimalPositionLabel.setText(Messages
					.getString("ResponseTypeDetail.label.DecimalPosition") + ":"); //$NON-NLS-1$
			decimalPositionLabel.setBackground(Display
					.getCurrent().getSystemColor(
							SWT.COLOR_WHITE));

			decimalPosition = new Text(parentCodeComposite,
					SWT.BORDER);
			decimalPosition.setLayoutData(new GridData(
					SWT.FILL, SWT.CENTER, true, false));
			decimalPosition.setText(position);
			decimalPosition.addVerifyListener(new DecimalPositionVerifyListener());
			decimalPosition.addModifyListener(new DecimalPositionModifyListener());
		}
	}

	/**
	 * Set Question Response Type details e.g. detail of NUMERIC i.e. Integer,
	 * Double or Float
	 * 
	 * @param responseType
	 *            CODE, TEXT, etc.
	 * @param responseDomain
	 * 
	 */
	public void setDetails(RepresentationType representationType) {
		// public void setDetails(RESPONSE_TYPES responseType) {

		// m_representationType = representationType;

		RESPONSE_TYPES responseType = ResponseTypeDetail
				.getResponseType(representationType);

		this.dispose();

		if (responseType == RESPONSE_TYPES.UNDEFINED) {
			return;
		} else if (responseType == RESPONSE_TYPES.TEXT) {

			final Label textLabel = new Label(parentLabelComposite, SWT.NONE);
			textLabel.setLayoutData(new GridData());
			textLabel.setText(Messages
					.getString("ResponseTypeDetail.label.Text") + ":"); //$NON-NLS-1$
			textLabel.setBackground(Display.getCurrent().getSystemColor(
					SWT.COLOR_WHITE));

			final Label dummyLabel = new Label(parentCodeComposite, SWT.NONE);

			final Label LengthLabel = new Label(parentLabelComposite, SWT.NONE);
			LengthLabel.setText(Messages
					.getString("ResponseTypeDetail.label.MaxLength") + ":"); //$NON-NLS-1$
			LengthLabel.setBackground(Display.getCurrent().getSystemColor(
					SWT.COLOR_WHITE));

			lengthText = new Text(parentCodeComposite, SWT.BORDER);
			lengthText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
					false));
			if (representationType != null) {
				BigInteger maxLength = ((TextDomainType) representationType)
						.getMaxLength();
				if (maxLength != null) {
					lengthText.setText(maxLength.toString());
				}
			}
			lengthText.addVerifyListener(new VerifyListener() {
				public void verifyText(final VerifyEvent e) {
					Editor.verifyField(FIELD_TYPE.DIGIT, e, site);
					System.out.println("lengthText: " + lengthText.getText());
				}
			});
			lengthText.addModifyListener(new ModifyListener() {
				public void modifyText(final ModifyEvent e) {
					log.debug(".modifyText(): " + lengthText.getText());
					questionItem.setTextResponseDomain(new BigInteger(
							lengthText.getText()));
					editorStatus.setChanged();
				}
			});
			parentLabelComposite.getParent().layout();
			return;
		} else if (responseType == RESPONSE_TYPES.NUMERIC) {

			// ***** NUMERIC *****
			// --------------------
			String numericType = "";
			final Label numericLabel = new Label(parentLabelComposite, SWT.NONE);
			numericLabel.setLayoutData(new GridData());
			numericLabel.setText(Messages
					.getString("ResponseTypeDetail.label.Numeric") + ":"); //$NON-NLS-1$
			numericLabel.setBackground(Display.getCurrent().getSystemColor(
					SWT.COLOR_WHITE));

			final Label dummyLabel = new Label(parentCodeComposite, SWT.NONE);

			final Label integerLabel = new Label(parentLabelComposite, SWT.NONE);
			integerLabel.setText(Messages
					.getString("ResponseTypeDetail.label.Type") + ":"); //$NON-NLS-1$
			integerLabel.setBackground(Display.getCurrent().getSystemColor(
					SWT.COLOR_WHITE));

			numTypeCombo = new Combo(parentCodeComposite, SWT.READ_ONLY);
			final GridData gd_langCombo = new GridData(SWT.FILL, SWT.CENTER,
					true, false);
			numTypeCombo.setLayoutData(gd_langCombo);
			numTypeCombo.setItems(NUMERIC_TYPES);
			if (representationType != null) {
				NumericTypeCodeType.Enum codeType = ((NumericDomainType) representationType)
						.getType();
				if (codeType != null) {
					if (codeType.equals(NumericTypeCodeType.INTEGER)) {
						numTypeCombo
								.setText(NUMERIC_TYPES[NUMERIC_TYPE_INDEX.INTEGER
										.ordinal()]);
					} else if (codeType.equals(NumericTypeCodeType.DOUBLE)) {
						numTypeCombo
								.setText(NUMERIC_TYPES[NUMERIC_TYPE_INDEX.DOUBLE
										.ordinal()]);
					} else {
						numTypeCombo
								.setText(NUMERIC_TYPES[NUMERIC_TYPE_INDEX.FLOAT
										.ordinal()]);
					}
				} else {
					// Default:
					((NumericDomainType) representationType)
							.setType(NumericTypeCodeType.INTEGER);
					numTypeCombo
							.setText(NUMERIC_TYPES[NUMERIC_TYPE_INDEX.INTEGER
									.ordinal()]);
				}
			}
			numTypeCombo.addModifyListener(new ModifyListener() {
				public void modifyText(final ModifyEvent e) {
					log.debug(".modifyText()x: " + numTypeCombo.getText());
					log.debug("Layout Data: "
							+ parentLabelComposite.getLayoutData());
					String numericType = numTypeCombo.getText();
					if (numericType.equals(Messages
							.getString("ResponseTypeDetail.label.Double"))
							|| numericType.equals(Messages
									.getString("ResponseTypeDetail.label.Float"))) {
						// create decimal position detail - if not already created
						BigInteger pos = null;
						if (decimalPosition != null
								&& !decimalPosition.isDisposed()
								&& decimalPosition.getText().length() > 0) {
							pos = new BigInteger(decimalPosition.getText());
						}
						createDecimalPosition(pos == null ? "" : pos.toString());
						if (numericType.equals(Messages
								.getString("ResponseTypeDetail.label.Double"))) {
							questionItem.setNumericResponseDomain(
									NumericTypeCodeType.DOUBLE, pos);
						} else {
							questionItem.setNumericResponseDomain(
									NumericTypeCodeType.FLOAT, pos);
						}
					} else {
						// Integer - no decimal position
						disposeDecimalPosition();
						questionItem.setNumericResponseDomain(
								NumericTypeCodeType.INTEGER, null);
					}
					parentLabelComposite.getParent().layout();
					editorStatus.setChanged();
				}
			});
			if (numTypeCombo.getText().equals(
					Messages.getString("ResponseTypeDetail.label.Double"))
					|| numTypeCombo
							.getText()
							.equals(Messages
									.getString("ResponseTypeDetail.label.Float"))) {
				createDecimalPosition(representationType == null ? "" : ((NumericDomainType) representationType)
						.getDecimalPositions().toString());
				editorStatus.setChanged();
			}
			parentLabelComposite.getParent().layout();
			return;
		} else if (responseType == RESPONSE_TYPES.CODE) {

			// ***** CODE *****
			// -----------------
			final FilteredItemsSelection filteredItemsSelection = new FilteredItemsSelection();

			final Composite labelComposite = new Composite(
					parentLabelComposite, SWT.NONE);
			labelComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP,
					false, false));
			labelComposite.setBackground(Display.getCurrent().getSystemColor(
					SWT.COLOR_WHITE));
			final GridLayout gridLayout_3 = new GridLayout();
			gridLayout_3.marginWidth = 0;
			gridLayout_3.marginHeight = 0;
			labelComposite.setLayout(gridLayout_3);

			final Composite codeComposite = new Composite(parentCodeComposite,
					SWT.NONE);
			codeComposite.setBackground(Display.getCurrent().getSystemColor(
					SWT.COLOR_WHITE));
			codeComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
					false, false));
			codeComposite.setLayout(new GridLayout());

			// - Get available Code Schemes:
			List<LightXmlObjectType> codeSchemeReferenceList = new ArrayList();
			try {
				codeSchemeReferenceList = CodeSchemeDao.getCodeSchemesLight("",
						"");
			} catch (Exception e1) {
				String errMess = MessageFormat
						.format(Messages
								.getString("ResponseTypeDetail.mess.CodeSchemeRetrievalError"), e1.getMessage()); //$NON-NLS-1$
				MessageDialog.openError(parentCodeComposite.getShell(),
						Messages.getString("ErrorTitle"), errMess);
			}

			// - Create Code Scheme selection composite:
			filteredItemsSelection
					.createPartControl(
							labelComposite,
							codeComposite,
							Messages.getString("ResponseTypeDetail.label.Code"),
							Messages.getString("ResponseTypeDetail.label.CodeScheme"),
							codeSchemeReferenceList,
							representationType != null ? ((CodeDomainType) representationType)
									.getCodeSchemeReference().getIDList()
									.get(0).getStringValue()
									: "");
			filteredItemsSelection
					.addSelectionListener(
							Messages.getString("ResponseTypeDetail.label.SelectCodeShemeReference"),
							codeSchemeReferenceList, new SelectionAdapter() {
								public void widgetSelected(SelectionEvent e) {
									LightXmlObjectType result = (LightXmlObjectType) filteredItemsSelection
											.getResult();
									if (result != null) {
										questionItem
												.setCodeResponseDomain(result
														.getId());
										editorStatus.setChanged();
									}
								}
							});
			parentLabelComposite.layout();
			parentCodeComposite.layout();
			return;
		} else {
			// ***** OTHER *****
			// ------------------
			String errMess = MessageFormat
					.format(Messages
							.getString("ResponseTypeDetail.mess.QuestionItemResponseTypeNotSupported"), responseType); //$NON-NLS-1$
			MessageDialog.openError(parentLabelComposite.getShell(),
					Messages.getString("ErrorTitle"), errMess);
		}
	}

	/**
	 * Return Response Type of a given Response Domain
	 * 
	 * @param representationType
	 * @return
	 */
	static public RESPONSE_TYPES getResponseType(
			RepresentationType representationType) {

		if (representationType == null) {
			return RESPONSE_TYPES.UNDEFINED;
		}

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
