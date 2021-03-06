package org.ddialliance.ddieditor.ui.editor;

/**
 * <copyright> 
 *
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *   IBM - Initial API and implementation
 *
 * </copyright>
 *
 * $Id$
 */
//package org.eclipse.emf.common.ui.celleditor;
//bug fix url: https://bugs.eclipse.org/bugs/show_bug.cgi?id=75624

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class BooleanCellEditor extends CellEditor {
	protected Button button;
	String strTrue = "true", strFalse = "false";

	public BooleanCellEditor(Composite parent) {
		super(parent);
	}

	public BooleanCellEditor(Composite parent, String strTrue, String strFalse) {
		super(parent);
		this.strTrue = strTrue;
		this.strFalse = strFalse;
	}

	public BooleanCellEditor(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	protected Control createControl(Composite parent) {
		Font font = parent.getFont();
		Color bg = parent.getBackground();

		button = new Button(parent, getStyle() | SWT.CHECK);
		button.setFont(font);
		button.setBackground(bg);

		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				button.setText(button.getSelection()?strTrue:strFalse);
			}
		});

		return button;
	}

	@Override
	protected Object doGetValue() {
		return button.getSelection();
	}

	@Override
	protected void doSetValue(Object value) {
		boolean selection = Boolean.TRUE.equals(value);
		button.setSelection(selection);
		button.setText(selection?strTrue:strFalse);
		this.fireEditorValueChanged(!selection, selection);
	}

	@Override
	protected void doSetFocus() {
		if (button != null) {
			button.setFocus();
		}
	}
}
