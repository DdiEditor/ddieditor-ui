package org.ddialliance.ddieditor.ui.wizard.export;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;

public class ExportDdiXmlWizard extends Wizard implements IExportWizard {
	ExportDdiXmlWizardPage mainPage;

	public ExportDdiXmlWizard() {
		super();
	}

	@Override
	public boolean performFinish() {
//		IFile file = mainPage.createNewFile();
//        if (file == null)
//            return false;
        return true;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setWindowTitle("File Import Wizard"); //NON-NLS-1
		setNeedsProgressMonitor(true);
		
		mainPage = new ExportDdiXmlWizardPage("Import File",selection); //NON-NLS-1
	}
	
	@Override
	public void addPages() {
		super.addPages();
        addPage(mainPage);
	}
}
