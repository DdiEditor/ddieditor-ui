package org.ddialliance.ddieditor.ui.editor;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.ddialliance.ddi_3_0.xml.xmlbeans.datacollection.CodeDomainType;
import org.ddialliance.ddi_3_0.xml.xmlbeans.datacollection.NumericDomainType;
import org.ddialliance.ddi_3_0.xml.xmlbeans.reusable.NumericTypeCodeType;
import org.ddialliance.ddi_3_0.xml.xmlbeans.reusable.RepresentationType;
import org.ddialliance.ddieditor.ui.editor.Editor.EditorStatus;
import org.ddialliance.ddieditor.ui.model.QuestionItem;
import org.ddialliance.ddieditor.ui.reference.ResponseTypeReference;
import org.ddialliance.ddieditor.ui.view.Messages;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class ResponseTypeDetail {

	private static Log log = LogFactory.getLog(LogType.SYSTEM, ResponseTypeDetail.class);

	// Member variables:
	private QuestionItem questionItem;
	private EditorStatus editorStatus;
	private Composite parentComposite;
	private Label decimalPositionLabel = null;
	private Text decimalPosition = null;
	private Combo numTypeCombo = null;

	public static String[] RESPONSE_TYPE_LABELS = { "", Messages.getString("ResponseTypeDetail.label.Code"),
			Messages.getString("ResponseTypeDetail.label.Text"),
			Messages.getString("ResponseTypeDetail.label.Numeric"),
			Messages.getString("ResponseTypeDetail.label.Date"),
			Messages.getString("ResponseTypeDetail.label.Category"),
			Messages.getString("ResponseTypeDetail.label.Geographic") };

	public enum RESPONSE_TYPES {
		UNDEFINED, CODE, TEXT, NUMERIC, DATE, CATEGORY, GEOGRAPHIC
	};

	static private final String[] NUMERIC_TYPES = { Messages.getString("ResponseTypeDetail.label.Integer"),
			Messages.getString("ResponseTypeDetail.label.Float") };

	static private enum NUMERIC_TYPE_INDEX {
		INTEGER, FLOAT
	};

	public ResponseTypeDetail(QuestionItem questionItem, Composite parentComposite, EditorStatus editorStatus) {
		this.parentComposite = parentComposite;
		this.editorStatus = editorStatus;
		this.questionItem = questionItem;
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

		// responseDomainReferenceList.add(new ResponseTypeReference("", null));
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

		Control[] children = parentComposite.getChildren();
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

		RESPONSE_TYPES responseType = ResponseTypeDetail.getResponseType(representationType);

		this.dispose();

		if (responseType == RESPONSE_TYPES.UNDEFINED) {
			return;
		} else if (responseType == RESPONSE_TYPES.NUMERIC) {
			String numericType = "";
			final Label numericLabel = new Label(parentComposite, SWT.NONE);
			numericLabel.setLayoutData(new GridData());
			numericLabel.setText(Messages.getString("ResponseTypeDetail.label.Numeric") + ":"); //$NON-NLS-1$
			numericLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

			final Label dummyLabel = new Label(parentComposite, SWT.NONE);

			final Label integerLabel = new Label(parentComposite, SWT.NONE);
			integerLabel.setText(Messages.getString("ResponseTypeDetail.label.Type") + ":"); //$NON-NLS-1$
			integerLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

			numTypeCombo = new Combo(parentComposite, SWT.READ_ONLY);
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
					if (numTypeCombo.getText().equals(Messages.getString("ResponseTypeDetail.label.Float"))) {
						decimalPositionLabel = new Label(parentComposite, SWT.NONE);
						decimalPositionLabel
								.setText(Messages.getString("ResponseTypeDetail.label.DecimalPosition") + ":"); //$NON-NLS-1$
						decimalPositionLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

						decimalPosition = new Text(parentComposite, SWT.BORDER);
						decimalPosition.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
						decimalPosition.setText("");
					} else {
						if (decimalPositionLabel != null) {
							decimalPositionLabel.dispose();
						}
						if (decimalPosition != null) {
							decimalPosition.dispose();
						}
					}
					parentComposite.getParent().layout();
					editorStatus.setChanged();
				}
			});
			if (numericType.equals("Float")) {
				decimalPositionLabel = new Label(parentComposite, SWT.NONE);
				decimalPositionLabel.setText(Messages.getString("ResponseTypeDetail.label.DecimalPosition") + ":"); //$NON-NLS-1$
				decimalPositionLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

				decimalPosition = new Text(parentComposite, SWT.BORDER);
				decimalPosition.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
				if (representationType != null) {
					decimalPosition.setText(((NumericDomainType) representationType).getDecimalPositions().toString());
				}
			}
			parentComposite.getParent().layout();
			return;
		} else if (responseType == RESPONSE_TYPES.CODE) {
			final Label numericLabel = new Label(parentComposite, SWT.NONE);
			numericLabel.setLayoutData(new GridData());
			numericLabel.setText(Messages.getString("ResponseTypeDetail.label.Code") + ":"); //$NON-NLS-1$
			numericLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

			final Label dummyLabel = new Label(parentComposite, SWT.NONE);

			final Label integerLabel = new Label(parentComposite, SWT.NONE);
			integerLabel.setText(Messages.getString("ResponseTypeDetail.label.CodeScheme") + ":"); //$NON-NLS-1$
			integerLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

			final Text text = new Text(parentComposite, SWT.BORDER);
			text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			// TODO - get list of Code Schemes
			if (representationType != null) {
				text.setText(((CodeDomainType) representationType).getCodeSchemeReferenceArray(0).getIDArray(0)
						.getStringValue());
			} else {
				text.setText("");
			}
			text.addModifyListener(new ModifyListener() {
				public void modifyText(final ModifyEvent e) {
					log.debug(".modifyCode(): " + text.getText());
					questionItem.setResponseDomain(getResponseType(questionItem.getResponseDomain()), text.getText());
					parentComposite.getParent().layout();
					editorStatus.setChanged();
				}
			});

			parentComposite.getParent().layout();
			return;
		} else {
			String errMess = MessageFormat.format(Messages
					.getString("ResponseTypeDetail.mess.QuestionItemResponseTypeNotSupported"), responseType); //$NON-NLS-1$
			MessageDialog.openError(parentComposite.getShell(), Messages.getString("ErrorTitle"), errMess);
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
