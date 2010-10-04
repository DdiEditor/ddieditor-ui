package org.ddialliance.ddieditor.ui.model.instrument;

import org.ddialliance.ddi3.xml.xmlbeans.datacollection.SequenceDocument;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.SpecificSequenceType;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.ItemSequenceTypeType.Enum;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.CommandType;
import org.ddialliance.ddieditor.ui.model.Model;

public class Sequence extends Model {
	SequenceDocument doc;

	public Sequence(SequenceDocument doc, String parentId, String parentVersion) {
		super(doc, parentId, parentVersion);
		this.doc = doc;
		this.create = false;
	}

	@Override
	public void executeChange(Object value, Class<?> type) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public SequenceDocument getDocument() {
		return doc;
	}
	
	//
	// SpecificSequence
	//
	public SpecificSequenceType getSpecificSequence() {
		SpecificSequenceType result =  doc.getSequence().getConstructSequence();
		if (result == null) {
			if (create) {
				result = doc.getSequence().addNewConstructSequence();
			} else {
				return null;
			}
		}
		return result;
	}
	
	public Enum getItemSequenceType() {
		SpecificSequenceType specificSequence = getSpecificSequence();
		Enum result = specificSequence.getItemSequenceType();
		if (result == null) {
			if (create) {
				//ItemSequenceTypeType.INT_OTHER;
				//result = ItemSequenceTypeType.OTHER;
				// TODO can not implement standard way with ENUM create workaround				
			} else {
				return null;
			}
		}	
		return result;
	}
	
	public CommandType getAlternateSequence( ) {
		CommandType result = getSpecificSequence().getAlternateSequenceType();
		if (result == null) {
			if (create) {
				result = getSpecificSequence().addNewAlternateSequenceType();
			} else {
				return null;
			}
		}	
		return result;
	}
}

