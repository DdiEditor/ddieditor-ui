package org.ddialliance.ddieditor.ui.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class ParentComposite extends Composite {

	private Button browseButton;
	private Composite composite;
	private Text secondField;
	private Text firstField;

	public ParentComposite(Composite parent, int style) {

		super(parent, style);
		createContents();

	}

	public void createContents() {

		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		setLayout(gridLayout);

		final Label firstFieldLabel = new Label(this, SWT.NONE);
		firstFieldLabel.setText("First Field");

		firstField = new Text(this, SWT.BORDER);
		final GridData gd_firstField = new GridData(SWT.FILL, SWT.CENTER, true,
				false);
		firstField.setLayoutData(gd_firstField);

		browseButton = new Button(this, SWT.NONE);
		browseButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false));
		browseButton.setText("Browse...");

		final Label secondFieldLabel = new Label(this, SWT.NONE);
		secondFieldLabel.setText("Second Field");

		secondField = new Text(this, SWT.BORDER);
		final GridData gd_secondField = new GridData(SWT.FILL, SWT.CENTER,
				true, false, 2, 1);
		secondField.setLayoutData(gd_secondField);

		composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true,
				3, 1));

	}

	public Composite getComposite() {

		return composite;

	}

	public String getBrowseButtonText() {

		return browseButton.getText();

	}

	public void setBrowseButtonText(String text) {

		browseButton.setText(text);

	}

	public Color getFirstFieldBackground() {

		return firstField.getBackground();

	}

	public void setFirstFieldBackground(Color background) {

		firstField.setBackground(background);

	}

}
