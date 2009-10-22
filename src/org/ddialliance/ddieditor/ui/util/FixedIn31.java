package org.ddialliance.ddieditor.ui.util;

import java.util.ArrayList;
import java.util.List;

import org.ddialliance.ddi3.xml.xmlbeans.reusable.DescriptionDocument;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.LabelDocument;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.LabelType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.StructuredStringType;
import org.ddialliance.ddiftp.util.xml.XmlBeansUtil;

/**
 * TODO desolve references when moving to ddi-3.1
 */
public class FixedIn31 {
	static final String text = "Label will be fixed in ddi-3.1";
	public static List<LabelType> getLabelList() {
		LabelType label = LabelDocument.Factory.newInstance().addNewLabel();
		XmlBeansUtil.setTextOnMixedElement(label, text);

		List<LabelType> list = new ArrayList<LabelType>();
		list.add(label);
		return list;
	}
	
	public static List<StructuredStringType> getDesrcList() {
		StructuredStringType description = DescriptionDocument.Factory.newInstance().addNewDescription();
		XmlBeansUtil.setTextOnMixedElement(description, text);
		
		List<StructuredStringType> list = new ArrayList<StructuredStringType>();
		list.add(description);
		return list;
	}
}
