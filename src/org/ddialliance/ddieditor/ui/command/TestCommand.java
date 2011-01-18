package org.ddialliance.ddieditor.ui.command;

// import org.ddialliance.ddieditor.ui.XmlObjectTest;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class TestCommand extends org.eclipse.core.commands.AbstractHandler {
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// XmlObjectTest test =new XmlObjectTest();
		try {
			// test.run();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
