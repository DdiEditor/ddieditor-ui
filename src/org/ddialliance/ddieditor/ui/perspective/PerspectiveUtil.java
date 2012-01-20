package org.ddialliance.ddieditor.ui.perspective;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;

public class PerspectiveUtil {
	final static float leftRatio = 0.30f;
	final static float rightRatio = 0.65f;

	public static IFolderLayout createLeftFolder(IPageLayout layout) {
		IFolderLayout leftFolder = layout.createFolder("topLeft",
				IPageLayout.LEFT, leftRatio, layout.getEditorArea());
		return leftFolder;
	}

	public static IFolderLayout createLeftFolder(IPageLayout layout,
			String... viewIds) {
		IFolderLayout leftFolder = createLeftFolder(layout);
		for (String id : viewIds) {
			leftFolder.addView(id);
		}
		return leftFolder;
	}

	public static IFolderLayout createRightFolder(IPageLayout layout) {
		IFolderLayout rightFolder = layout.createFolder("topRight",
				IPageLayout.RIGHT, rightRatio, layout.getEditorArea());
		return rightFolder;
	}

	public static IFolderLayout createRightFolder(IPageLayout layout,
			String... viewIds) {
		IFolderLayout rightFolder = createRightFolder(layout);
		for (String id : viewIds) {
			rightFolder.addView(id);
		}
		return rightFolder;
	}
}
