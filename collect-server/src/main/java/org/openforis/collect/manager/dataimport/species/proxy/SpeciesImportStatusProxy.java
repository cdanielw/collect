/**
 * 
 */
package org.openforis.collect.manager.dataimport.species.proxy;

import org.granite.messaging.amf.io.util.externalizer.annotation.ExternalizedProperty;
import org.openforis.collect.manager.dataimport.proxy.DataImportStatusProxy;
import org.openforis.collect.manager.dataimport.species.SpeciesImportStatus;

/**
 * @author S. Ricci
 *
 */
public class SpeciesImportStatusProxy extends DataImportStatusProxy {
	
	private transient SpeciesImportStatus speciesImportStatus;
	
	public SpeciesImportStatusProxy(SpeciesImportStatus status) {
		super(status);
		this.speciesImportStatus = status;
	}
	
	@ExternalizedProperty
	public int getTaxonomyId() {
		return speciesImportStatus.getTaxonomyId();
	}
	
}
