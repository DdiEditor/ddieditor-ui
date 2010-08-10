package org.ddialliance.ddieditor.ui.editor.widgetutil.table;

import java.text.Collator;
import java.util.Locale;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class TableColumnSort implements Listener {


	Table table;

	int sortCollater = 0;

	public TableColumnSort(Table table) {
		this.table = table;
	}

	public void handleEvent(Event e) {
		TableColumn defaultSortColumn = table.getSortColumn();
		TableColumn seletedSortColumn = (TableColumn) e.widget;

		// retrive index
		TableColumn[] columns = table.getColumns();
		int index = 2;
		for (int i = 0; i < columns.length; i++) {
			if (columns[i].getText().equals(seletedSortColumn.getText()))
				index = i;
		}

		// determin sort direction
		int dir = table.getSortDirection();
		if (defaultSortColumn == seletedSortColumn) {
			dir = dir == SWT.UP ? SWT.DOWN : SWT.UP;
		} else {
			table.setSortColumn(seletedSortColumn);
			dir = SWT.UP;
		}

		if (dir == SWT.UP) {
			sortCollater = 1;
		} else {
			sortCollater = -1;
		}

		// set sort column
		table.setSortColumn(seletedSortColumn);
		table.setSortDirection(dir);

		// sort
		TableItem[] items = table.getItems();
		String[] columnValues = new String[columns.length];
		Object columnObjectValue = null;
		Collator collator = Collator.getInstance(Locale.getDefault());
		
		// progressbar
		ProgressBar bar = null;
		if(items.length>100) {
			bar = new ProgressBar(table.getParent(), SWT.SMOOTH);
			bar.moveBelow(table);
			GridData barGD = new GridData(GridData.HORIZONTAL_ALIGN_FILL
					|GridData.END);
			barGD.horizontalSpan=2;
			bar.setLayoutData(barGD);
			bar.setMaximum(items.length);
			table.getParent().layout();
		}
		
		String value1 = null;
		String value2 = null;
		for (int i = 1; i < items.length; i++) {
			value1 = items[i].getText(index);
			if(bar!=null) {
				bar.setSelection(i);
			}
			for (int j = 0; j < i; j++) {
				value2 = items[j].getText(index);

				if ((sortCollater == 1 && collator.compare(value1, value2) > 0)
						|| (sortCollater == -1 && collator.compare(value1,
								value2) < 0)) {
					// retrive row values
					for (int k = 0; k < columns.length; k++) {
						columnValues[k] = items[i].getText(k);
						columnObjectValue = null;
						columnObjectValue = items[i].getData();
					}
					items[i].dispose();
					TableItem tableItem = new TableItem(table, SWT.NONE, j);
					tableItem.setText(columnValues);
					tableItem.setData(columnObjectValue);
					items = table.getItems();
					break;
				}
			}
		}
		
		if (bar!=null) {
			bar.dispose();
			bar=null;
			table.getParent().layout();
		}
	}
}
