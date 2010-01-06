package org.ddialliance.ddieditor.ui.editor.widgetutil.tab;

import java.util.HashMap;

import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

/**
 * Custom listener to take action on registered tab items
 */
public class TabFolderListener implements SelectionListener {
	/**
	 * Map to hold registered tab items<br>
	 * To register a tab item:<br>
	 * actionMap.put([id of tabItem aka dataObject with key Editor.Tab_ID], tab item action)  
	 */
	public HashMap<String, TabItemAction> actionMap = new HashMap<String, TabItemAction>();
		
	@Override
	public void widgetDefaultSelected(SelectionEvent event) {			
			//
	}

	@Override
	public void widgetSelected(SelectionEvent event) {
		TabFolder tabFolder = (TabFolder)event.getSource();
		TabItem tabItem = tabFolder.getItem(tabFolder.getSelectionIndex());
		String id = (String)tabItem.getData(Editor.TAB_ID);
		if (id == null) {
			return;
		}
		TabItemAction action = actionMap.get(id);
		if (action!=null) {			
			try {
				action.action();
			} catch (DDIFtpException e) {
				//
			}
		}
	}
}
