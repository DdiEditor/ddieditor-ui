package org.ddialliance.ddieditor.ui.util;

import org.eclipse.core.internal.runtime.InternalPlatform;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

public class OsgiServiceLocator {
	public static final String serviceName = "someserviceid";
	public void getService(String serviceId) throws InvalidSyntaxException {
		ServiceReference[] serviceReference = InternalPlatform.getDefault().getBundleContext().getAllServiceReferences("", "");
	}
}
