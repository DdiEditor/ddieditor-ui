package org.ddialliance.ddieditor.ui.command;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.ddialliance.ddieditor.ui.dialogs.DisplayNoteDialog;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PlatformUI;

public class DisplayNote extends org.eclipse.core.commands.AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(
					"resources/release-note.txt"));

			char[] buf = new char[1024];
			int numRead = 0;
			while ((numRead = reader.read(buf)) != -1) {
				String readData = String.valueOf(buf, 0, numRead);
				fileData.append(readData);
				buf = new char[1024];
			}
		} catch (Exception e) {
			throw new ExecutionException(e.getMessage());
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				// do nothing
			}
		}
		String txt = "Release Note";
		DisplayNoteDialog dialog = new DisplayNoteDialog(PlatformUI
				.getWorkbench().getDisplay().getActiveShell(), txt, txt, txt,
				fileData.toString());
		dialog.open();

		return null;
	}
}
