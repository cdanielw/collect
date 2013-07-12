package org.openforis.collect.manager.dataimport.species;

import org.openforis.collect.manager.dataimport.DataImportStatus;
import org.openforis.collect.manager.dataimport.ParsingError;

/**
 * 
 * @author S. Ricci
 *
 */
public class SpeciesImportStatus extends DataImportStatus<ParsingError> {
	
	private int taxonomyId;
	
	public SpeciesImportStatus(int taxonomyName) {
		super();
		this.taxonomyId = taxonomyName;
	}
	
	public int getTaxonomyId() {
		return taxonomyId;
	}
	
}