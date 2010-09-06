package org.ddialliance.ddieditor.ui.dbxml;

import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.persistenceaccess.maintainablelabel.MaintainableLabelQueryResult;
import org.ddialliance.ddieditor.ui.model.IModel;
import org.ddialliance.ddieditor.ui.model.LabelDescriptionScheme;
import org.ddialliance.ddiftp.util.DDIFtpException;

public class DaoSchemeHelper {
	public static void update(IModel model) throws DDIFtpException {
		if (!(model instanceof LabelDescriptionScheme)) {
			throw new DDIFtpException("Model: "
					+ model.getClass().getSimpleName()
					+ " is not accepted for update. Only type: "
					+ LabelDescriptionScheme.class.getSimpleName()
					+ " is accepted.", new Throwable());
		}
		LabelDescriptionScheme ldScheme = (LabelDescriptionScheme) model;

		MaintainableLabelQueryResult result = ldScheme
				.getMaintainableLabelQueryResult();

		DdiManager.getInstance().updateMaintainableLabel(result,
				ldScheme.getMaintainableLabelUpdateElements());
	}
}
