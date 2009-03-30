package org.ddialliance.ddieditor.ui.editor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import com.swtdesigner.SWTResourceManager;

public class Tab {
	private StyledText styletextStyledText;
	
	/**
	 * Open the window
	 */
	public void open(Composite comp1) {


		final Composite composite = new Composite(comp1, SWT.NONE);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		composite.setLayout(gridLayout);

		final Label osirisFilnavnLabel = new Label(composite, SWT.NONE);
		osirisFilnavnLabel.setBackground(SWTResourceManager.getColor(255, 165, 0));
		osirisFilnavnLabel.setText("Osiris filnavn:");
		new Label(composite, SWT.NONE);

		styletextStyledText = new StyledText(composite, SWT.BORDER);
		styletextStyledText.setBackground(SWTResourceManager.getColor(255, 255, 255));
		styletextStyledText.setText("Dette er en text med en 'style'");

		final Button okButton = new Button(composite, SWT.NONE);
		okButton.setLayoutData(new GridData());
		okButton.addMouseListener(new MouseAdapter() {
			public void mouseDown(final MouseEvent e) {
				System.out.println("Mouse Down detected.");
			}
		});
		okButton.setBackground(SWTResourceManager.getColor(255, 165, 0));
		okButton.setText("OK");

		final Group group = new Group(composite, SWT.NONE);
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		group.setLayout(new GridLayout());

		final Text text = new Text(group, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
	}
}
