/**
 * 
 */
package org.openforis.collect.manager.dataimport.proxy;

import java.util.List;

import org.granite.messaging.amf.io.util.externalizer.annotation.ExternalizedProperty;
import org.openforis.collect.manager.dataimport.DataImportStatus;
import org.openforis.collect.manager.dataimport.ParsingError;
import org.openforis.collect.manager.process.proxy.ProcessStatusProxy;

/**
 * @author S. Ricci
 *
 */
public class DataImportStatusProxy extends ProcessStatusProxy {
	
	private transient DataImportStatus<ParsingError> status;

	public DataImportStatusProxy(DataImportStatus<ParsingError> status) {
		super(status);
		this.status = status;
	}
	
	@ExternalizedProperty
	public List<ParsingErrorProxy> getErrors() {
		List<ParsingError> errors = status.getErrors();
		return ParsingErrorProxy.fromList(errors);
	}

}
