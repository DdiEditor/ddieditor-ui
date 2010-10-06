package org.ddialliance.ddieditor.ui.editor.instrument;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;

public class SequenceDropListener extends ViewerDropAdapter {



	public SequenceDropListener(Viewer viewer) {
		super(viewer);
	}

	@Override
	public boolean performDrop(Object data) {
		// TODO Auto-generated method stub
		System.out.println("performDrop");
		return true;
	}

	@Override
	public boolean validateDrop(Object target, int operation,
			TransferData transferType) {
		System.out.println("validateDrop");
		// TODO Auto-generated method stub
		return true;
	}
	@Override
	public void dragEnter(DropTargetEvent event) {
		System.out.println("dragEnter");
		// TODO Auto-generated method stub
		super.dragEnter(event);
	}
	@Override
	public void dragOver(DropTargetEvent event) {
		System.out.println("dragOver");
		// TODO Auto-generated method stub
		super.dragOver(event);
	}
	@Override
	public void drop(DropTargetEvent event) {
		System.out.println("drop");
		// TODO Auto-generated method stub
		super.drop(event);
	}
	
}
/*
 

	@Override
	public void drop(DropTargetEvent event) {
		int location = this.determineLocation(event);
		String target = (String) determineTarget(event);			
		String translatedLocation = "";
		switch (location) {
		case 1:
			translatedLocation = "Dropped before the target ";
			break;
		case 2:
			translatedLocation = "Dropped after the target ";
			break;
		case 3:
			translatedLocation = "Dropped on the target ";
			break;
		case 4:
			translatedLocation = "Dropped into nothing ";
			break;
		}
		System.out.println(translatedLocation);
		System.out.println("The drop was done on the element: " + target);
		super.drop(event);
	}

	// This method performs the actual drop
	// We simply add the String we receive to the model and trigger a refresh of
	// the
	// viewer by calling its setInput method.
	@Override
	public boolean performDrop(Object data) {
//		ContentProviderTree.INSTANCE.getModel().add(data.toString());
//		viewer.setInput(ContentProviderTree.INSTANCE.getModel());
		return false;
	}

	@Override
	public boolean validateDrop(Object target, int operation,
			TransferData transferType) {
		return true;

	}
 */
