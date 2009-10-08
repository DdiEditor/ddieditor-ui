package org.ddialliance.ddieditor.ui.editor;

import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

public class DateTimeWidget extends CDateTime {

	public DateTimeWidget(Composite parent, int style) {
		super(parent, style);
	}

	public DateTimeWidget(Composite parent) {
		super(parent, CDT.BORDER | CDT.COMPACT | CDT.DROP_DOWN | CDT.DATE_LONG
				| CDT.TIME_MEDIUM | CDT.CLOCK_24_HOUR);
		this.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		this.setPattern("d-M-yyyy 'T' HH:mm:ss");
	}
}
