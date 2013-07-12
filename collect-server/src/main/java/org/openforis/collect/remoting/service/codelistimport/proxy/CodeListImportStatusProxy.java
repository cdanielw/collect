/**
 * 
 */
package org.openforis.collect.remoting.service.codelistimport.proxy;

import org.openforis.collect.manager.codelistimport.CodeListImportStatus;
import org.openforis.collect.manager.dataimport.proxy.DataImportStatusProxy;


/**
 * @author S. Ricci
 *
 */
public class CodeListImportStatusProxy extends DataImportStatusProxy {
	
	public CodeListImportStatusProxy(CodeListImportStatus status) {
		super(status);
	}
	
}
