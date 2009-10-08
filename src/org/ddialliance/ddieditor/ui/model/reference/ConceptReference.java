package org.ddialliance.ddieditor.ui.model.reference;

public class ConceptReference {
	private String label;
	private String id;

	public ConceptReference(String label, String id) {
		this.label = label;
		this.id = id;
	}

	public String toString() {
		return label;
	}

	public String getId() {
		return id;
	}
}
