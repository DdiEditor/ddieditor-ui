package org.ddialliance.ddieditor.ui.command;

import org.ddialliance.ddi3.xml.xmlbeans.group.ResourcePackageType;
import org.ddialliance.ddi3.xml.xmlbeans.instance.DDIInstanceDocument;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.IdentifiedStructuredStringType;
import org.ddialliance.ddieditor.logic.identification.IdentificationManager;
import org.ddialliance.ddieditor.ui.model.ElementType;
import org.ddialliance.ddiftp.util.xml.XmlBeansUtil;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/**
 * Create minimal resource package
 */
public class NewResourcePackage extends AbstractHandler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.
	 * ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			// new instance
			DDIInstanceDocument ddiInstanceDoc = DDIInstanceDocument.Factory
					.newInstance();
			ddiInstanceDoc.addNewDDIInstance();
			IdentificationManager.getInstance().addIdentification(
					ddiInstanceDoc.getDDIInstance(), null, null);
			IdentificationManager.getInstance().addVersionInformation(
					ddiInstanceDoc.getDDIInstance(), null, null);
			XmlBeansUtil.addXsiAttributes(ddiInstanceDoc);

			// resource pack
			ResourcePackageType resourcePackType = ddiInstanceDoc
					.getDDIInstance().addNewResourcePackage();
			IdentificationManager.getInstance().addIdentification(
					resourcePackType,
					ElementType.RESOURCE_PACKAGE.getIdPrefix(), null);
			IdentificationManager.getInstance().addVersionInformation(
					resourcePackType, null, null);

			// purpose
			IdentifiedStructuredStringType purposeType = resourcePackType
					.addNewPurpose();
			IdentificationManager.getInstance().addIdentification(purposeType,
					ElementType.PURPOSE.getIdPrefix(), null);
			purposeType.addNewContent();

			CommandHelper.createResource(ddiInstanceDoc);
		} catch (Exception e) {
			throw new ExecutionException(e.getLocalizedMessage());
		}
		return null;
	}

}
