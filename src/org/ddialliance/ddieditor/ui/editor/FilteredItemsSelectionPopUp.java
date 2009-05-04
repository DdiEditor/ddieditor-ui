package org.ddialliance.ddieditor.ui.editor;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.Activator;
import org.ddialliance.ddieditor.ui.dbxml.Util;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;

public class FilteredItemsSelectionPopUp extends FilteredItemsSelectionDialog {
	
	class FilteredItemsSelectionLabelProvider extends LabelProvider {

		public String getText(Object element) {
			
			return Util.getLabel((LightXmlObjectType) element);
		}

		public Image getImage(Object element) {
			return null;
		}
	}
	
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
	public FilteredItemsSelectionPopUp(Shell shell, String title, List<LightXmlObjectType> lightXmlObjectTypeList,
			boolean multi, boolean undefined) {
		super(shell, multi);
		this.lightXmlObjectTypeList = lightXmlObjectTypeList;
		setTitle(title);
		this.undefined = undefined;
		// setSelectionHistory(new ResourceSelectionHistory()); - not supported
		setListLabelProvider(new FilteredItemsSelectionLabelProvider());
		setDetailsLabelProvider(new FilteredItemsSelectionLabelProvider());
		setStatusLineAboveButtons(false);
	}
	
	protected String xgetItemText(LightXmlObjectType item) {
		if (item == null) {
			return null;
		}
//		return item.getLabelList().size() > 0 ? item.getLabelArray(0).toString() : item.getId();
		return Util.getLabel((LightXmlObjectType) item);
	}

	@Override
	/*
	 * Create Extended Content Area - area below item list
	 */
	protected Control createExtendedContentArea(Composite parent) {
		// No extension area
		return null;
	}

	@Override
	/*
	 * Create resource filter
	 * 
	 * Extend ItemsFilter and implement matchItem and isConsistentItem.
	 */
	protected ItemsFilter createFilter() {
		return new ItemsFilter() {
			public boolean matchItem(Object item) {
				return matches(Util.getLabel((LightXmlObjectType) item));
			}

			public boolean isConsistentItem(Object item) {
				return true;
			}
		};

	}
	
	@Override
	/*
	 * Fills the content provider with matching items.
	 */
	protected void fillContentProvider(AbstractContentProvider contentProvider, ItemsFilter itemsFilter,
			IProgressMonitor progressMonitor) throws CoreException {
		System.out.println("FilteredItemsSelectionPopUp.fillContentProvider(1)");
		progressMonitor.beginTask("Searching", lightXmlObjectTypeList.size()); //$NON-NLS-1$
		if (undefined) {
			contentProvider.add("", itemsFilter);
		}
		for (Iterator iter = lightXmlObjectTypeList.iterator(); iter.hasNext();) {
			System.out.println("FilteredItemsSelectionPopUp.fillContentProvider(2)");
			LightXmlObjectType lightXmlObjectType = (LightXmlObjectType) iter.next();
			contentProvider.add(lightXmlObjectType, itemsFilter);
			progressMonitor.worked(1);
		}
		System.out.println("FilteredItemsSelectionPopUp.fillContentProvider(3)");
		progressMonitor.done();
	}
	
	@Override
	/*
	 * Returns the settings object that stores information about how the dialog
	 * information is persisted.
	 */
	protected IDialogSettings getDialogSettings() {
		System.out.println("FilteredItemsSelectionPopUp.getDialogSettings(1)");
		IDialogSettings settings = Activator.getDefault().getDialogSettings().getSection(DIALOG_SETTINGS);
		if (settings == null) {
			settings = Activator.getDefault().getDialogSettings().addNewSection(DIALOG_SETTINGS);
		}
		System.out.println("FilteredItemsSelectionPopUp.getDialogSettings(2): " + settings);
		return settings;
	}

	@Override
	/*
	 * Returns a name for the given object. This is used to check duplicates
	 */
	public String getElementName(Object item) {
		return Util.getLabel((LightXmlObjectType) item);
	}

	@Override
	/*
	 * Returns a comparator used to sort items.
	 */
	protected Comparator getItemsComparator() {
		System.out.println("FilteredItemsSelectionPopUp.getItemsComparator()");
		return new Comparator() {
			public int compare(Object arg0, Object arg1) {
				return arg0.toString().compareTo(arg1.toString());
			}
		};
	}

	@Override
	/*
	 * Validates that the item is a valid selection.
	 */
	protected IStatus validateItem(Object item) {
		System.out.println("FilteredItemsSelectionPopUp.validateItem()");
		return Status.OK_STATUS;
	}
	
	/**
	 * Get result as string
	 * 
	 * @return
	 */
	public String getFirstResultString() {
		return Util.getLabel((LightXmlObjectType) super.getFirstResult());
	}
	
}
