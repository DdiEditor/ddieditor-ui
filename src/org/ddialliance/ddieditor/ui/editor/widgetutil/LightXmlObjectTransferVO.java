package org.ddialliance.ddieditor.ui.editor.widgetutil;

import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;

/**
 * Light XMl object transfer value object
 */
public class LightXmlObjectTransferVO {
	public String rcpPartId;
	public int dragedFromPosition;
	public LightXmlObjectType lightXmlObject;

	public LightXmlObjectTransferVO() {
	}

	public LightXmlObjectTransferVO(String rcpPartId, int dragedFromPosition,
			LightXmlObjectType lightXmlObject) {
		this.rcpPartId = rcpPartId;
		this.dragedFromPosition = dragedFromPosition;
		this.lightXmlObject = lightXmlObject;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("DragedFromPosition: ");
		result.append(dragedFromPosition);
		result.append(", lightXmlObject: ");
		result.append(lightXmlObject);
		result.append(", rcpPartId: ");
		result.append(rcpPartId);
		return result.toString();
	}
}
