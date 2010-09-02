package org.ddialliance.ddieditor.ui.model;

import java.util.List;

import org.ddialliance.ddi3.xml.xmlbeans.reusable.LabelType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.StructuredStringType;
import org.ddialliance.ddiftp.util.DDIFtpException;

public interface ILabelDescription extends IModel {

	/**
	 * Get Display Label of Simple Element.
	 * 
	 * @return String Label string
	 * @throws DDIFtpException
	 */
	public abstract String getDisplayLabel() throws DDIFtpException;

	/**
	 * Set Label of Display Language.
	 * 
	 * @param string
	 * @return LabelType null - if label updated (no label is added) LabelType
	 *         is new label added
	 * @throws DDIFtpException
	 */
	public abstract LabelType setDisplayLabel(String string)
			throws DDIFtpException;

	/**
	 * Get Original Description of Simple Element. Original means not
	 * translated.
	 * 
	 * @return String
	 * @throws DDIFtpException
	 */
	public abstract String getDisplayDescr() throws DDIFtpException;

	/**
	 * Set Display Description.
	 * 
	 * @param string
	 * @return StructuredStringType null - if description updated (no new
	 *         description is added) StructuredStringType - if new description
	 *         added
	 * @throws DDIFtpException 
	 */
	public abstract StructuredStringType setDisplayDescr(String string) throws DDIFtpException;

	public abstract List<LabelType> getLabels() throws DDIFtpException;

	public abstract List<StructuredStringType> getDescrs()
			throws DDIFtpException;

}