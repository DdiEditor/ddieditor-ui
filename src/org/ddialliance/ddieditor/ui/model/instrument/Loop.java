package org.ddialliance.ddieditor.ui.model.instrument;

import org.ddialliance.ddi3.xml.xmlbeans.datacollection.InitialValueDocument;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.LoopDocument;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.CodeType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.ProgrammingLanguageCodeType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.ReferenceType;
import org.ddialliance.ddieditor.logic.identification.IdentificationManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.model.Model;
import org.ddialliance.ddieditor.ui.model.ModelIdentifingType;
import org.ddialliance.ddiftp.util.DDIFtpException;

public class Loop extends Model {
	LoopDocument doc;

	public Loop(LoopDocument doc, String parentId, String parentVersion) {
		super(doc, parentId, parentVersion);
		this.doc = doc;
		this.create = true;
	}

	@Override
	public void executeChange(Object value, Class<?> type) throws Exception {
		// variable reference
		if (type.equals(ModelIdentifingType.Type_B.class)) {
			IdentificationManager.getInstance().addReferenceInformation(
					getLoopVariableReference(), (LightXmlObjectType) value);
		}

		// init value

		// init value program lang

		// init value source question ref

		// loop while

		// loop while program lang

		// loop while source question ref

		// step value

		// step value program lang

		// step value source question ref

		// control construct ref
	}

	@Override
	public LoopDocument getDocument() {
		return doc;
	}

	public CodeType getInitialValue() {
		CodeType codeType = doc.getLoop().getInitialValue();
		if (codeType == null) {
			if (create) {
				codeType = doc.getLoop().addNewInitialValue();
			} else {
				return null;
			}
		}
		return codeType;
	}

	public CodeType getLoopWhile() {
		CodeType codeType = doc.getLoop().getLoopWhile();
		if (codeType == null) {
			if (create) {
				codeType = doc.getLoop().addNewLoopWhile();
			} else {
				return null;
			}
		}
		return codeType;
	}

	public CodeType getStepValue() {
		CodeType codeType = doc.getLoop().getStepValue();
		if (codeType == null) {
			if (create) {
				codeType = doc.getLoop().addNewStepValue();
			} else {
				return null;
			}
		}
		return codeType;
	}

	public ProgrammingLanguageCodeType getCode(CodeType codeType) {
		if (codeType == null) {
			return null;
		}
		if (codeType.getCodeList().isEmpty()) {
			return create ? codeType.addNewCode() : null;
		} else {
			return codeType.getCodeList().get(0);
		}
	}

	public ReferenceType getLoopVariableReference() {
		ReferenceType ref = doc.getLoop().getLoopVariableReference();
		if (ref == null) {
			return create ? doc.getLoop().addNewLoopVariableReference() : null;
		}
		return ref;
	}

	
	public ReferenceType getQuestionReference(CodeType codeType) {
		if (codeType == null && !create) {
			return null;
		}
		
		String type = null;
		try {
			type = codeType.getDomNode().getLocalName();
		} catch (Throwable t) {
			new DDIFtpException("Getting Dom Node exception", t);
		}
		if (type == null) {
			return null;
		}
		
		if (codeType instanceof InitialValueDocument) {
			System.out.println("break");

		}
		ReferenceType ref = null;
		if (doc.getLoop().getControlConstructReferenceList().isEmpty()) {
			ref = create ? doc.getLoop().addNewControlConstructReference()
					: null;
			return ref;
		} else {
			return doc.getLoop().getControlConstructReferenceList().get(0);
		}
	}
}
