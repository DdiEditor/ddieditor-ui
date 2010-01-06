package org.ddialliance.ddieditor.ui.editor.widgetutil;

/**
 * Definition of complex get and setStringValue on any element
 */
public interface GenericGetSetClosure {
	public static String GET_STRING_METHOD_NAME = "getStringValue";
	public static String SET_STRING_METHOD_NAME = "setStringValue";
	public static String GET_OBJECT_METHOD_NAME = "getObject";
	
	/**
	 * Get the editorial value on any element
	 * @param obj to perform complex get value on 
	 * @return value
	 */
	public String getStringValue(Object obj);

	/**
	 * Set the editorial value on any element
	 * @param obj to perform complex set value on
	 * @param text value to set
	 */
	public void setStringValue(Object obj, Object text);

	/**
	 * Get editorial element  
	 * @param obj to get element from
	 * @return editorial element
	 */
	public Object getObject(Object obj);
}