package org.ddialliance.ddieditor.ui.dbxml;

import java.util.Properties;

public class XmlEntities {

	protected static String xml_export_filename = "";
	
	public static void init (Properties properties) {
		
		xml_export_filename = properties.getProperty("xml_export_filename", "");
	}
	
}
