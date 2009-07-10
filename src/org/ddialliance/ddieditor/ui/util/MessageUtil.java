package org.ddialliance.ddieditor.ui.util;

import org.ddialliance.ddieditor.ui.view.Messages;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

public class MessageUtil {
	
	public static void currentNotSupported(Shell shell) {
		MessageDialog.openInformation(shell, Messages.getString("InfoTitle"), Messages
				.getString("CurrentlyNotSupported"));
	}

}
