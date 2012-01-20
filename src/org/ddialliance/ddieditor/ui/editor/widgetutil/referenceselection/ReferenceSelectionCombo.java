package org.ddialliance.ddieditor.ui.editor.widgetutil.referenceselection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ddialliance.ddieditor.model.lightxmlobject.LabelType;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddiftp.util.Translator;
import org.ddialliance.ddiftp.util.xml.XmlBeansUtil;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;

/**
 * Generic Filtered Items Section
 */
public class ReferenceSelectionCombo {
	Composite parentCodeComposite;
	ComboViewer comboViewer;
	Button browseButton;
	LightXmlObjectType result;
	protected List<LightXmlObjectType> referenceList;

	boolean isNew;
	private String preIdValue;

	public ReferenceSelectionCombo(boolean isNew) {
		this.isNew = isNew;
	}

	public Combo getCombo() {
		if (comboViewer != null) {
			return comboViewer.getCombo();
		}
		return null;
	}

	/**
	 * Create Part Control i.e. create labels, field and browse button
	 * 
	 * @param parentComposite
	 * @param parentCodeComposite
	 * @param subTitle
	 * @param itemLabel
	 * @param referenceList
	 * @param preIdValue
	 * @wbp.parser.entryPoint
	 */
	public void createPartControl(Composite parentLabelComposite,
			final Composite parentCodeComposite, String subTitleText,
			String itemLabelText, final List<LightXmlObjectType> referenceList,
			String preIdValue) {
		this.preIdValue = preIdValue;
		this.parentCodeComposite = parentCodeComposite;

		// prefix reference list with 'empty' LigthXmlObject
		if (referenceList == null) {
			this.referenceList = new ArrayList<LightXmlObjectType>();
		} else
			this.referenceList = referenceList;

		// Create Label Composite:
		final Composite labelComposite = new Composite(parentLabelComposite,
				SWT.NONE);
		final GridLayout gridLayout_1 = new GridLayout();
		gridLayout_1.marginHeight = 0;
		gridLayout_1.marginWidth = 0;
		labelComposite.setLayout(gridLayout_1);
		labelComposite.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));

		if (subTitleText.length() > 0) {
			final Label SubTitleLabel = new Label(labelComposite, SWT.NONE);
			final GridData gd_subTitleLabel = new GridData(SWT.RIGHT,
					SWT.CENTER, false, false);
			// gd_subTitleLabel.widthHint = 500;
			SubTitleLabel.setLayoutData(gd_subTitleLabel);
			SubTitleLabel.setText(subTitleText + ":");
			SubTitleLabel.setBackground(Display.getCurrent().getSystemColor(
					SWT.COLOR_WHITE));
		}

		final Label itemLabel = new Label(labelComposite, SWT.NONE);
		itemLabel
				.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, true));
		itemLabel.setText(itemLabelText);
		itemLabel.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));

		// Create Code Composite i.e. combobox and browse button:
		final Composite codeComposite = new Composite(parentCodeComposite,
				SWT.NONE);
		codeComposite.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));
		final GridLayout gridLayout = new GridLayout();
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.numColumns = 2;
		final GridData gd_composite = new GridData(SWT.FILL, SWT.CENTER, true,
				true, 2, 1);
		codeComposite.setLayoutData(gd_composite);
		codeComposite.setLayout(gridLayout);

		if (subTitleText.length() > 0) {
			new Label(codeComposite, SWT.NONE);
		}

		// combo
		comboViewer = new ComboViewer(codeComposite);
		comboViewer
				.setContentProvider(new org.eclipse.jface.viewers.ArrayContentProvider());
		comboViewer.setLabelProvider(new ReferenceSelectionLabelProvider());

		final GridData gd_combo_1 = new GridData(SWT.FILL, SWT.CENTER, true,
				false);
		gd_combo_1.widthHint = 254;
		comboViewer.getCombo().setLayoutData(gd_combo_1);
		comboViewer.getCombo().setData(Editor.NEW_ITEM, isNew);

		this.referenceList = addedWithEmpty(getReferenceList());
		setComboContent(getReferenceList());

		// - "Browse..." button:
		browseButton = new Button(codeComposite, SWT.NONE);
		browseButton.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));
		browseButton.setText(Translator
				.trans("ResponseTypeDetail.label.Browse")); //$NON-NLS-1$
	}

	boolean updateCombo = true;

	/**
	 * Add selection listener with call back to the added selection listener
	 * 
	 * @param Title
	 *            of FilteredItemsSelectionDialog
	 * @param selectionListener
	 */
	public void addSelectionListener(final String title,
			SelectionListener selectionListener) {
		comboViewer.getCombo().addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				result = referenceList.get(comboViewer.getCombo()
						.getSelectionIndex());
			}
		});

		comboViewer.getCombo().addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
			}

			@Override
			public void focusGained(FocusEvent e) {
				// the focus listener has two focuses
				if (updateCombo) {
					updateCombo = false;
					// no update
					// 1 on fucus on button
					return;
				}

				updateCombo = true;
				// do update
				// 2 on expanding the combo list
				// 2.1 update is done on expanding the list ;)

				// TODO removeAll insert on combo
				
				// getReferenceList();
				// setComboContent(referenceList);
				// System.out.println("DONE update combo");
			}
		});

		if (selectionListener != null) {
			comboViewer.getCombo().addSelectionListener(selectionListener);
		}

		browseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Shell shell = new Shell();
				List<LightXmlObjectType> referenceList = getReferenceList();

				FilteredItemsSelectionDialog dialog = new ReferenceSelectionPopUp(
						shell, title, referenceList, false, false);

				dialog.setInitialPattern(comboViewer.getCombo()
						.getSelectionIndex() == -1 ? "**" : comboViewer
						.getCombo().getText());
				dialog.open();

				// Sync. Combo Box:
				result = (LightXmlObjectType) dialog.getFirstResult();
				if (result != null) {
					int index = 0;
					for (Iterator iterator = referenceList.iterator(); iterator
							.hasNext();) {
						LightXmlObjectType lightXmlObjectType = (LightXmlObjectType) iterator
								.next();
						if (lightXmlObjectType.getId().equals(result.getId())) {
							break;
						}
						index++;
					}
					comboViewer.getCombo().select(index);
				}
			}
		});

		if (selectionListener != null) {
			browseButton.addSelectionListener(selectionListener);
		}
	}

	/**
	 * Get result
	 * 
	 * @return
	 */
	public LightXmlObjectType getResult() {
		return result;
	}

	public List<LightXmlObjectType> getReferenceList() {
		return referenceList;
	}

	protected List<LightXmlObjectType> addedWithEmpty(
			List<LightXmlObjectType> referenceList) {
		List<LightXmlObjectType> result = new ArrayList<LightXmlObjectType>();

		// add empty element
		LightXmlObjectType lightXmlObject = LightXmlObjectType.Factory
				.newInstance();
		lightXmlObject.setId("");
		LabelType labelType = lightXmlObject.addNewLabel();
		XmlBeansUtil.setTextOnMixedElement(labelType, "");
		result.add(lightXmlObject);

		// add org list
		result.addAll(referenceList);
		return result;
	}

	private void setComboContent(List<LightXmlObjectType> referenceList) {

		// set combo content
		comboViewer.getCombo().removeAll();
		comboViewer.getCombo().update();
		comboViewer.setInput(referenceList);
		comboViewer.refresh(true);

		// Select preIdValue:
		int index = 0;
		for (Iterator<LightXmlObjectType> iterator = referenceList.iterator(); iterator
				.hasNext();) {
			LightXmlObjectType lightXmlObjectType = (LightXmlObjectType) iterator
					.next();
			if (lightXmlObjectType.getId() != null
					&& lightXmlObjectType.getId().equals(preIdValue)) {
				break;
			}
			index++;
		}
		comboViewer.getCombo().select(index);
	}
}
