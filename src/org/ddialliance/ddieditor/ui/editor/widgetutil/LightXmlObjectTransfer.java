package org.ddialliance.ddieditor.ui.editor.widgetutil;

import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;

public class LightXmlObjectTransfer extends Transfer {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			LightXmlObjectTransfer.class);
	private static final String LIGHTXMLOBJECT = "LIGHTXMLOBJECT"; //$NON-NLS-1$
	private static final int LIGHTXMLOBJECT_ID = registerType(LIGHTXMLOBJECT);
	private static LightXmlObjectTransfer _instance = new LightXmlObjectTransfer();

	private LightXmlObjectTransfer() {
	}

	/**
	 * Returns the singleton instance of the TextTransfer class.
	 * 
	 * @return the singleton instance of the TextTransfer class
	 */
	public static LightXmlObjectTransfer getInstance() {
		return _instance;
	}

	@Override
	protected int[] getTypeIds() {
		log.debug("");
		return new int[] { LIGHTXMLOBJECT_ID };
	}

	@Override
	protected String[] getTypeNames() {
		log.debug("");
		return new String[] { LIGHTXMLOBJECT };
	}

	@Override
	public TransferData[] getSupportedTypes() {
		log.debug("");
		int[] types = getTypeIds();
		TransferData[] data = new TransferData[types.length];
		for (int i = 0; i < types.length; i++) {
			data[i] = new TransferData();
			data[i].type = types[i];
		}
		return data;
	}

	@Override
	public boolean isSupportedType(TransferData transferData) {
		log.debug("");
		if (transferData == null)
			return false;
		int[] types = getTypeIds();
		for (int i = 0; i < types.length; i++) {
			if (transferData.type == types[i])
				return true;
		}
		return false;
	}

	@Override
	protected void javaToNative(Object object, TransferData transferData) {
		log.debug("");
		System.out.println(object);
	}

	@Override
	protected Object nativeToJava(TransferData transferData) {
		log.debug("");
		System.out.println(transferData);
		return null;
	}
}
