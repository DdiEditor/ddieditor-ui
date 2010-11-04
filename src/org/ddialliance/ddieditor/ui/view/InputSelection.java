package org.ddialliance.ddieditor.ui.view;

import org.ddialliance.ddieditor.ui.model.ElementType;

/**
 * Wrappes the selected item and the resource the selected item relates to
 */
public class InputSelection {
	String resourceId;
	Object selection;
	ElementType parentElementType;

	public InputSelection() {
	}

	public InputSelection(String resourceId, Object selection) {
		super();
		System.out.println("resourceId: "+resourceId+" selection: "+selection);
		this.resourceId = resourceId;
		this.selection = selection;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public Object getSelection() {
		return selection;
	}

	public void setSelection(Object selection) {
		this.selection = selection;
	}
	
	public ElementType getParentElementType() {
		return parentElementType;
	}

	public void setParentElementType(ElementType parentElementType) {
		this.parentElementType = parentElementType;
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("Resource id: ");
		result.append(resourceId);
		result.append(" , selection: [class: ");
		result.append(selection.getClass().getName());
		result.append(" , value: ");
		result.append(selection);
		result.append("]");
		return result.toString();
	}
}
