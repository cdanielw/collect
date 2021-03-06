/**
 * This class is generated by jOOQ
 */
package org.openforis.collect.persistence.jooq.tables.records;

/**
 * This class is generated by jOOQ.
 */
@javax.annotation.Generated(value    = {"http://www.jooq.org", "2.5.0"},
                            comments = "This class is generated by jOOQ")
public class OfcTaxonVernacularNameRecord extends org.jooq.impl.UpdatableRecordImpl<org.openforis.collect.persistence.jooq.tables.records.OfcTaxonVernacularNameRecord> {

	private static final long serialVersionUID = -1311878767;

	/**
	 * The table column <code>collect.ofc_taxon_vernacular_name.id</code>
	 * <p>
	 * This column is part of the table's PRIMARY KEY
	 */
	public void setId(java.lang.Integer value) {
		setValue(org.openforis.collect.persistence.jooq.tables.OfcTaxonVernacularName.OFC_TAXON_VERNACULAR_NAME.ID, value);
	}

	/**
	 * The table column <code>collect.ofc_taxon_vernacular_name.id</code>
	 * <p>
	 * This column is part of the table's PRIMARY KEY
	 */
	public java.lang.Integer getId() {
		return getValue(org.openforis.collect.persistence.jooq.tables.OfcTaxonVernacularName.OFC_TAXON_VERNACULAR_NAME.ID);
	}

	/**
	 * The table column <code>collect.ofc_taxon_vernacular_name.vernacular_name</code>
	 */
	public void setVernacularName(java.lang.String value) {
		setValue(org.openforis.collect.persistence.jooq.tables.OfcTaxonVernacularName.OFC_TAXON_VERNACULAR_NAME.VERNACULAR_NAME, value);
	}

	/**
	 * The table column <code>collect.ofc_taxon_vernacular_name.vernacular_name</code>
	 */
	public java.lang.String getVernacularName() {
		return getValue(org.openforis.collect.persistence.jooq.tables.OfcTaxonVernacularName.OFC_TAXON_VERNACULAR_NAME.VERNACULAR_NAME);
	}

	/**
	 * The table column <code>collect.ofc_taxon_vernacular_name.language_code</code>
	 */
	public void setLanguageCode(java.lang.String value) {
		setValue(org.openforis.collect.persistence.jooq.tables.OfcTaxonVernacularName.OFC_TAXON_VERNACULAR_NAME.LANGUAGE_CODE, value);
	}

	/**
	 * The table column <code>collect.ofc_taxon_vernacular_name.language_code</code>
	 */
	public java.lang.String getLanguageCode() {
		return getValue(org.openforis.collect.persistence.jooq.tables.OfcTaxonVernacularName.OFC_TAXON_VERNACULAR_NAME.LANGUAGE_CODE);
	}

	/**
	 * The table column <code>collect.ofc_taxon_vernacular_name.language_variety</code>
	 */
	public void setLanguageVariety(java.lang.String value) {
		setValue(org.openforis.collect.persistence.jooq.tables.OfcTaxonVernacularName.OFC_TAXON_VERNACULAR_NAME.LANGUAGE_VARIETY, value);
	}

	/**
	 * The table column <code>collect.ofc_taxon_vernacular_name.language_variety</code>
	 */
	public java.lang.String getLanguageVariety() {
		return getValue(org.openforis.collect.persistence.jooq.tables.OfcTaxonVernacularName.OFC_TAXON_VERNACULAR_NAME.LANGUAGE_VARIETY);
	}

	/**
	 * The table column <code>collect.ofc_taxon_vernacular_name.taxon_id</code>
	 * <p>
	 * This column is part of a FOREIGN KEY: <code><pre>
	 * CONSTRAINT ofc_taxon_vernacular_name__ofc_taxon_vernacular_name_taxon_fkey
	 * FOREIGN KEY (taxon_id)
	 * REFERENCES collect.ofc_taxon (id)
	 * </pre></code>
	 */
	public void setTaxonId(java.lang.Integer value) {
		setValue(org.openforis.collect.persistence.jooq.tables.OfcTaxonVernacularName.OFC_TAXON_VERNACULAR_NAME.TAXON_ID, value);
	}

	/**
	 * The table column <code>collect.ofc_taxon_vernacular_name.taxon_id</code>
	 * <p>
	 * This column is part of a FOREIGN KEY: <code><pre>
	 * CONSTRAINT ofc_taxon_vernacular_name__ofc_taxon_vernacular_name_taxon_fkey
	 * FOREIGN KEY (taxon_id)
	 * REFERENCES collect.ofc_taxon (id)
	 * </pre></code>
	 */
	public java.lang.Integer getTaxonId() {
		return getValue(org.openforis.collect.persistence.jooq.tables.OfcTaxonVernacularName.OFC_TAXON_VERNACULAR_NAME.TAXON_ID);
	}

	/**
	 * Link this record to a given {@link org.openforis.collect.persistence.jooq.tables.records.OfcTaxonRecord 
	 * OfcTaxonRecord}
	 */
	public void setTaxonId(org.openforis.collect.persistence.jooq.tables.records.OfcTaxonRecord value) {
		if (value == null) {
			setValue(org.openforis.collect.persistence.jooq.tables.OfcTaxonVernacularName.OFC_TAXON_VERNACULAR_NAME.TAXON_ID, null);
		}
		else {
			setValue(org.openforis.collect.persistence.jooq.tables.OfcTaxonVernacularName.OFC_TAXON_VERNACULAR_NAME.TAXON_ID, value.getValue(org.openforis.collect.persistence.jooq.tables.OfcTaxon.OFC_TAXON.ID));
		}
	}

	/**
	 * The table column <code>collect.ofc_taxon_vernacular_name.taxon_id</code>
	 * <p>
	 * This column is part of a FOREIGN KEY: <code><pre>
	 * CONSTRAINT ofc_taxon_vernacular_name__ofc_taxon_vernacular_name_taxon_fkey
	 * FOREIGN KEY (taxon_id)
	 * REFERENCES collect.ofc_taxon (id)
	 * </pre></code>
	 */
	public org.openforis.collect.persistence.jooq.tables.records.OfcTaxonRecord fetchOfcTaxon() {
		return create()
			.selectFrom(org.openforis.collect.persistence.jooq.tables.OfcTaxon.OFC_TAXON)
			.where(org.openforis.collect.persistence.jooq.tables.OfcTaxon.OFC_TAXON.ID.equal(getValue(org.openforis.collect.persistence.jooq.tables.OfcTaxonVernacularName.OFC_TAXON_VERNACULAR_NAME.TAXON_ID)))
			.fetchOne();
	}

	/**
	 * The table column <code>collect.ofc_taxon_vernacular_name.step</code>
	 */
	public void setStep(java.lang.Integer value) {
		setValue(org.openforis.collect.persistence.jooq.tables.OfcTaxonVernacularName.OFC_TAXON_VERNACULAR_NAME.STEP, value);
	}

	/**
	 * The table column <code>collect.ofc_taxon_vernacular_name.step</code>
	 */
	public java.lang.Integer getStep() {
		return getValue(org.openforis.collect.persistence.jooq.tables.OfcTaxonVernacularName.OFC_TAXON_VERNACULAR_NAME.STEP);
	}

	/**
	 * The table column <code>collect.ofc_taxon_vernacular_name.qualifier1</code>
	 */
	public void setQualifier1(java.lang.String value) {
		setValue(org.openforis.collect.persistence.jooq.tables.OfcTaxonVernacularName.OFC_TAXON_VERNACULAR_NAME.QUALIFIER1, value);
	}

	/**
	 * The table column <code>collect.ofc_taxon_vernacular_name.qualifier1</code>
	 */
	public java.lang.String getQualifier1() {
		return getValue(org.openforis.collect.persistence.jooq.tables.OfcTaxonVernacularName.OFC_TAXON_VERNACULAR_NAME.QUALIFIER1);
	}

	/**
	 * The table column <code>collect.ofc_taxon_vernacular_name.qualifier2</code>
	 */
	public void setQualifier2(java.lang.String value) {
		setValue(org.openforis.collect.persistence.jooq.tables.OfcTaxonVernacularName.OFC_TAXON_VERNACULAR_NAME.QUALIFIER2, value);
	}

	/**
	 * The table column <code>collect.ofc_taxon_vernacular_name.qualifier2</code>
	 */
	public java.lang.String getQualifier2() {
		return getValue(org.openforis.collect.persistence.jooq.tables.OfcTaxonVernacularName.OFC_TAXON_VERNACULAR_NAME.QUALIFIER2);
	}

	/**
	 * The table column <code>collect.ofc_taxon_vernacular_name.qualifier3</code>
	 */
	public void setQualifier3(java.lang.String value) {
		setValue(org.openforis.collect.persistence.jooq.tables.OfcTaxonVernacularName.OFC_TAXON_VERNACULAR_NAME.QUALIFIER3, value);
	}

	/**
	 * The table column <code>collect.ofc_taxon_vernacular_name.qualifier3</code>
	 */
	public java.lang.String getQualifier3() {
		return getValue(org.openforis.collect.persistence.jooq.tables.OfcTaxonVernacularName.OFC_TAXON_VERNACULAR_NAME.QUALIFIER3);
	}

	/**
	 * Create a detached OfcTaxonVernacularNameRecord
	 */
	public OfcTaxonVernacularNameRecord() {
		super(org.openforis.collect.persistence.jooq.tables.OfcTaxonVernacularName.OFC_TAXON_VERNACULAR_NAME);
	}
}
