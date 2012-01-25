package org.ddialliance.ddieditor.ui.perspective;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;

public class PerspectiveUtil {
	final static float leftRatio = 0.30f;
	final static float rightRatio = 0.65f;

	public static IFolderLayout createLeftFolder(IPageLayout layout) {
		return createLeftFolder(layout, leftRatio);
	}

	public static IFolderLayout createLeftFolder(IPageLayout layout, float ratio) {
		IFolderLayout leftFolder = layout.createFolder("topLeft",
				IPageLayout.LEFT, ratio, layout.getEditorArea());
		return leftFolder;
	}

	public static IFolderLayout createLeftFolder(IPageLayout layout,
			float ratio, String... viewIds) {
		IFolderLayout leftFolder = createLeftFolder(layout, ratio);
		for (String id : viewIds) {
			leftFolder.addView(id);
		}
		return leftFolder;
	}

	public static IFolderLayout createLeftFolder(IPageLayout layout,
			String... viewIds) {
		return createLeftFolder(layout, leftRatio, viewIds);
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
