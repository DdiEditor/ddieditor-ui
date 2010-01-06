package org.ddialliance.ddieditor.ui.editor.widgetutil.referenceselection;

/**
 * Generic Filtered Items Selection Pop-up
 */

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.Activator;
import org.ddialliance.ddieditor.util.LightXmlObjectUtil;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;

public class ReferenceSelectionPopUp extends FilteredItemsSelectionDialog {
	private static final String DIALOG_SETTINGS = "NA";
	private static List<LightXmlObjectType> lightXmlObjectTypeList;
	private boolean undefined;

	/**
	 * Construct Filtered Items Selection pop-up.
	 * 
	 * @param shell
	 * @param title
	 * @param lightXmlObjectTypeList
	 * @param multi
	 *            if true - multiple selection enabled.
	 * @param undefined
	 *            if true - 'undefined' - (empty row) - is generated.
	 */
	public ReferenceSelectionPopUp(Shell shell, String title,
			List<LightXmlObjectType> lightXmlObjectTypeList, boolean multi,
			boolean undefined) {
		super(shell, multi);
		this.lightXmlObjectTypeList = lightXmlObjectTypeList;
		setTitle(title);
		this.undefined = undefined;
		// setSelectionHistory(new ResourceSelectionHistory()); - not supported
		setListLabelProvider(new ReferenceSelectionLabelProvider());
		setDetailsLabelProvider(new ReferenceSelectionLabelProvider());
		setStatusLineAboveButtons(false);
	}

	@Override
	protected Control createExtendedContentArea(Composite parent) {
		// No extension area
		return null;
	}

	@Override
	protected ItemsFilter createFilter() {
		return new ItemsFilter() {
			public boolean matchItem(Object item) {
				return matches(LightXmlObjectUtil
						.getLabel((LightXmlObjectType) item));
			}

			public boolean isConsistentItem(Object item) {
				return true;
			}
		};

	}

	@Override
	protected void fillContentProvider(AbstractContentProvider contentProvider,
			ItemsFilter itemsFilter, IProgressMonitor progressMonitor)
			throws CoreException {
		progressMonitor.beginTask("Searching", lightXmlObjectTypeList.size()); //$NON-NLS-1$
		if (undefined) {
			contentProvider.add("", itemsFilter);
		}
		for (Iterator iter = lightXmlObjectTypeList.iterator(); iter.hasNext();) {
			LightXmlObjectType lightXmlObjectType = (LightXmlObjectType) iter
					.next();
			contentProvider.add(lightXmlObjectType, itemsFilter);
			progressMonitor.worked(1);
		}
		progressMonitor.done();
	}

	@Override
	protected IDialogSettings getDialogSettings() {
		IDialogSettings settings = Activator.getDefault().getDialogSettings()
				.getSection(DIALOG_SETTINGS);
		if (settings == null) {
			settings = Activator.getDefault().getDialogSettings()
					.addNewSection(DIALOG_SETTINGS);
		}
		return settings;
	}

	@Override
	public String getElementName(Object item) {
		return LightXmlObjectUtil.getLabel((LightXmlObjectType) item);
	}

	@Override
	protected Comparator getItemsComparator() {
		return new Comparator() {
			public int compare(Object arg0, Object arg1) {
				return arg0.toString().compareTo(arg1.toString());
			}
		};
	}

	@Override
	protected IStatus validateItem(Object item) {
		return Status.OK_STATUS;
	}

	/**
	 * Get result as string
	 * 
	 * @return
	 */
	public String getFirstResultString() {
		return LightXmlObjectUtil.getLabel((LightXmlObjectType) super
				.getFirstResult());
	}
}
