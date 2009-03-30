package org.ddialliance.ddieditor.ui.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class NewComposite extends Group {

	private Text text_1;
	private Text text;
	/**
	 * Create the composite
	 * @param parent
	 * @param style
	 */
	public NewComposite(Composite parent, int style) {
		super(parent, style);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		setLayout(gridLayout);
		setText("Name");

		final Label firstLabel = new Label(this, SWT.NONE);
		final GridData gd_firstLabel = new GridData(SWT.LEFT, SWT.FILL, false, false);
		firstLabel.setLayoutData(gd_firstLabel);
		firstLabel.setText("First:");

		text = new Text(this, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		final Label lastLabel = new Label(this, SWT.NONE);
		lastLabel.setText("Last:");

		text_1 = new Text(this, SWT.BORDER);
		text_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		//
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
