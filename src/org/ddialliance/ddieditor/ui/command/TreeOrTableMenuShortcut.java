package org.ddialliance.ddieditor.ui.command;

import org.ddialliance.ddieditor.ui.editor.Editor;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Tree;

public class TreeOrTableMenuShortcut extends AbstractHandler {
	public static final String ID = "org.ddialliance.ddieditor.ui.command.TreeOrTableMenuShortcut";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			if (event.getTrigger() instanceof Event) {
				Menu menu = null;
				if (((Event) event.getTrigger()).widget instanceof Table) {
					menu = ((Table) ((Event) event.getTrigger()).widget)
							.getMenu();
				} else if (((Event) event.getTrigger()).widget instanceof Tree) {
					menu = ((Tree) ((Event) event.getTrigger()).widget)
							.getMenu();
				}
				if (menu != null) {
					// TODO set location
					// menu.setLocation(location)
					menu.setVisible(true);
				}
			}
		} catch (Exception e) {
			Editor.showError(e, ID, null);
		}
		return null;
	}

}
