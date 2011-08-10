package org.ddialliance.ddieditor.ui.util;

import org.ddialliance.ddiftp.util.Translator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

public class MessageUtil {

	public static void currentNotSupported(Shell shell) {
		MessageDialog.openInformation(shell, Translator.trans("InfoTitle"),
				Translator.trans("CurrentlyNotSupported"));
	}

}
